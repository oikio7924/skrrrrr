# AIChat.py
# Senior Voice Chat Backend (FastAPI + OpenAI)
# - /api/health
# - /api/topics     : GPT-5-mini로 하루 30+ 화두 생성/캐시(미개통 시 4o-mini로 폴백)
# - /api/session    : Realtime 세션 발급 + 안전 지침 주입 + today_topics 동봉
# - /api/audio/*    : 자정 업로드된 녹음 저장/조회

import os, json, random, traceback
from pathlib import Path
from datetime import datetime, timezone, timedelta
from typing import List, Optional

from fastapi import FastAPI, APIRouter, HTTPException, Query, UploadFile, File, Form
from fastapi.responses import JSONResponse, FileResponse
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from openai import OpenAI

# =========================
# Env & Client
# =========================
load_dotenv(dotenv_path="./.env")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
if not OPENAI_API_KEY:
    raise RuntimeError("OPENAI_API_KEY 누락: .env에 설정하세요.")

client = OpenAI(api_key=OPENAI_API_KEY)

# =========================
# App & CORS
# =========================
app = FastAPI(title="Realtime Voice Chat Backend", version="1.0.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://127.0.0.1:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
api = APIRouter(prefix="/api")

# =========================
# Storage & Time
# =========================
RECORD_BASE = Path("./recordings"); RECORD_BASE.mkdir(parents=True, exist_ok=True)
TOPIC_BASE  = Path("./topics");      TOPIC_BASE.mkdir(parents=True, exist_ok=True)

def ensure_user_dir(user_id: str) -> Path:
    d = RECORD_BASE / user_id
    d.mkdir(parents=True, exist_ok=True)
    return d

KST = timezone(timedelta(hours=9))
def kst_today() -> str:
    return datetime.now(KST).strftime("%Y-%m-%d")

# =========================
# Safety & Topic Prompts
# =========================
SENIOR_SAFETY_INSTR = """
[역할] 당신은 65세 이상 시니어를 위한 한국어 음성 도우미입니다.
[언어] 항상 한국어(존댓말)로만 말합니다. 다른 언어 입력이어도 한국어로 정중히 안내하고 한국어로 응답합니다.
[말투] 따뜻하고 천천히, 쉬운 단어, 한 번에 1~2문장. 요약·반복으로 잘 확인합니다.
[금지] 욕설/비속어/모욕/혐오/차별/폭력/선정/음주·도박 권장/불법/과도한 의학·법률·재정 조언.
[정확성] 모르면 모른다고 말하고, 추측하지 않습니다. 필요한 경우 전문가 상담을 권유합니다.
[운영 규칙]
- 한 번에 화두 1개만 제시하세요. 목록을 나열하지 마세요.
- 제시한 화두로 자연스럽게 질문→경청→짧은 피드백을 반복합니다.
- 대화가 끊기거나(한참 반응이 없거나 “다음/그만/다른 이야기”라고 말하면) 아직 사용하지 않은 새 화두 1개를 조용히 제안하며 전환합니다.
- 사용자 선호를 기억해 너무 비슷한 화두가 연속되지 않도록 합니다.
"""

TOPIC_PROMPT_TEMPLATE = """
당신은 시니어(65+) 대상 한국어 대화 주제를 고안합니다.
조건:
- 반드시 한국어로만 작성
- 최소 30개
- 간단·안전·부담 없는 화두, 1줄 요지로 짧게
- 민감/금지(정치공방, 욕설, 선정적 내용, 편향/혐오, 위험행위, 심층의학/법률/재정 자문)는 제외
- 카테고리 다양화: 추억/가족/건강한 생활습관(가벼운 운동·스트레칭)/취미(원예·손뜨개·그림)/음식/여행(국내)/전통문화/계절/동네 이야기/음악·드라마/뉴스 ‘가벼운 현상’ 등
- 형식: JSON 배열(string 리스트)만 출력. 예: ["주제1","주제2",...]

추가 맥락(선택): {extra_context}
"""

# =========================
# Topic Cache Helpers
# =========================
def topics_cache_path(user_id: str, day: str) -> Path:
    d = TOPIC_BASE / user_id
    d.mkdir(parents=True, exist_ok=True)
    return d / f"{day}.json"

def load_cached_topics(user_id: str, day: str) -> Optional[List[str]]:
    p = topics_cache_path(user_id, day)
    if p.exists():
        try:
            return json.loads(p.read_text(encoding="utf-8"))
        except Exception:
            return None
    return None

def save_topics(user_id: str, day: str, topics: List[str]) -> None:
    p = topics_cache_path(user_id, day)
    p.write_text(json.dumps(topics, ensure_ascii=False, indent=2), encoding="utf-8")

# =========================
# Responses API Text Extract (SDK 호환)
# =========================
def _extract_output_text(resp) -> str:
    try:
        txt = resp.output_text
        if isinstance(txt, str) and txt.strip():
            return txt
    except Exception:
        pass
    try:
        d = resp.to_dict() if hasattr(resp, "to_dict") else resp
        for item in d.get("output", []):
            for c in item.get("content", []):
                if c.get("type") in ("output_text", "text"):
                    return c.get("text") or c.get("value") or ""
    except Exception:
        pass
    raise RuntimeError("Responses API 출력 텍스트 추출 실패")

# =========================
# Topic Generation (5-mini → 4o-mini 폴백)
# =========================
def generate_topics_gpt(user_id: str, day: str, extra_context: str = "") -> List[str]:
    prompt = TOPIC_PROMPT_TEMPLATE.format(extra_context=extra_context or "없음")
    models = ["gpt-5-mini", "gpt-4o-mini"]  # 5-mini 미개통/오류시 폴백
    last_err: Optional[Exception] = None
    text = ""

    for m in models:
        try:
            resp = client.responses.create(
                model=m,
                input=[{"role": "user", "content": prompt}],
                max_output_tokens=1200,
            )
            text = _extract_output_text(resp)
            if text and text.strip():
                break
        except Exception as e:
            last_err = e
            continue

    if not text.strip():
        raise RuntimeError(f"화두 생성 실패: {last_err}")

    # JSON 파싱 우선
    topics: List[str] = []
    try:
        topics = json.loads(text)
        if not isinstance(topics, list):
            raise ValueError("JSON 배열이 아님")
    except Exception:
        topics = [line.strip("-• ").strip() for line in text.splitlines() if line.strip()]

    # 한글 포함/중복 제거/길이 제한
    norm, seen = [], set()
    for t in topics:
        if not isinstance(t, str): continue
        s = t.strip()
        if not s: continue
        if len(s) > 80: s = s[:80] + "…"
        if any("가" <= ch <= "힣" for ch in s):
            if s not in seen:
                seen.add(s); norm.append(s)

    # 최소 30개 보장
    fallback = [
        "어릴 적 놀던 장소 이야기","좋아하는 계절 산책 코스","요즘 즐겨 보는 TV 프로그램",
        "자주 만들어 드시는 집밥 메뉴","가볍게 하는 스트레칭 루틴","가족과 함께한 기억에 남는 사진",
        "동네 시장에서 좋아하는 가게","키워보고 싶은 식물 이야기","추억의 노래 한 곡",
        "옛 직장에서 기억에 남는 순간","친구와의 따뜻한 에피소드","여행 가고 싶은 국내 도시",
        "비 오는 날의 소소한 즐거움","최근 배운 작은 기술","한 해 목표와 작은 실천",
        "잠들기 전 하는 루틴","마음이 편안해지는 장소","좋아하는 차/커피 습관",
        "손글씨/일기의 추억","집안 정리·수납 팁","스스로 칭찬해줄 일",
        "조용한 취미(색칠, 퍼즐 등)","좋아하는 과일·간식","요즘 관심 있는 동네 소식",
        "정리하고 싶은 추억 상자","명절의 기억","계절 음식 이야기",
        "가까운 공원에서의 산책","오늘 감사한 일 한 가지","학창시절 친구 이야기",
        "편지를 써본 적 있는지","창밖 풍경 묘사해 보기","좋아하는 전통문화 한 가지","수면 습관과 꿀팁"
    ]
    while len(norm) < 30 and fallback:
        pick = fallback.pop(0)
        if pick not in norm:
            norm.append(pick)

    random.shuffle(norm)
    return norm[:max(30, len(norm))]

def ensure_topics_today(user_id: str, extra_context: str = "") -> List[str]:
    day = kst_today()
    cached = load_cached_topics(user_id, day)
    if cached and len(cached) >= 30:
        return cached
    topics = generate_topics_gpt(user_id, day, extra_context=extra_context)
    save_topics(user_id, day, topics)
    return topics

# =========================
# Realtime Client (SDK 호환 탐색)
# =========================
def _get_realtime_client(oai_client):
    # 1) 최신 SDK: client.realtime
    rt = getattr(oai_client, "realtime", None)
    if rt is not None:
        return rt
    # 2) 구버전 베타: client.beta.realtime
    beta = getattr(oai_client, "beta", None)
    if beta is not None:
        rt = getattr(beta, "realtime", None)
        if rt is not None:
            return rt
    # 3) 둘 다 없으면 미지원
    raise RuntimeError("This openai-python SDK does not expose Realtime. Please upgrade the 'openai' package.")

# =========================
# Endpoints
# =========================
@api.get("/health")
def health():
    return {"ok": True}

@api.get("/topics")
def get_topics(
    user_id: str = Query(...),
    context: Optional[str] = Query(None, description="선택: 사용자 관심/지역 등 힌트"),
):
    try:
        topics = ensure_topics_today(user_id, extra_context=context or "")
        return {"ok": True, "day": kst_today(), "count": len(topics), "topics": topics}
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"토픽 생성 실패: {e}")

@api.get("/session")
def create_realtime_session(
    user_id: str = Query(...),
    context: Optional[str] = Query(None),
    user_name: Optional[str] = Query(None),
):
    try:
        topics = ensure_topics_today(user_id, extra_context=context or "")
        topics_lines = "\n".join([f"- {t}" for t in topics])
        name_line = f"\n[이름] 사용자를 '{user_name}'로 호칭합니다." if user_name else ""
        instructions = (
            SENIOR_SAFETY_INSTR
            + name_line
            + "\n[오늘의 화두 후보(내부용)]\n" + topics_lines +
            "\n[시작 방법] 오늘의 후보 중에서 적절한 1개만 먼저 제시하고, 바로 가볍게 질문 1개로 시작하세요. 목록을 나열하지 마세요."
        )

        rt = _get_realtime_client(client)  # SDK 버전에 맞춰 realtime 핸들 얻기
        session = rt.sessions.create(
            model="gpt-4o-mini-realtime-preview",
            voice="verse",
            instructions=instructions,
            # 자동응답(서버 VAD) 둔감화 — 소음 반응 줄이고 말 끊김 판정 여유
            turn_detection={
                "type": "server_vad",
                "threshold": 0.90,
                "silence_duration_ms": 1200,
                # "create_responses": False,  # 완전 수동 모드 원하면 주석 해제
            },
        )
        payload = session.to_dict() if hasattr(session, "to_dict") else session
        payload["today_topics"] = topics
        return JSONResponse(content=payload)
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=502, detail=str(e))

# --------- Audio Upload/List/File (자정 업로드용) ---------
def day_string_is_valid(day: str) -> bool:
    if len(day) != 10:
        return False
    y, m, d = day.split("-")
    return (len(y) == 4 and len(m) == 2 and len(d) == 2 and y.isdigit() and m.isdigit() and d.isdigit())

@api.post("/audio/upload")
async def upload_audio(
    user_id: str = Form(..., description="사용자 식별자 (예: uuid)"),
    day: str = Form(..., description="KST 기준 날짜 'YYYY-MM-DD'"),
    file: UploadFile = File(..., description="WebM/Opus 녹음 파일"),
):
    if not day_string_is_valid(day):
        raise HTTPException(status_code=400, detail="day 형식은 'YYYY-MM-DD' 이어야 합니다.")
    user_dir = ensure_user_dir(user_id)
    dst = user_dir / f"{day}.webm"
    data = await file.read()
    if not data:
        raise HTTPException(status_code=400, detail="빈 파일입니다.")
    with open(dst, "wb") as f:
        f.write(data)
    return {"ok": True, "path": str(dst)}

@api.get("/audio/list")
def list_audio(user_id: str = Query(...)):
    user_dir = ensure_user_dir(user_id)
    items = []
    for p in sorted(user_dir.glob("*.webm")):
        items.append({
            "day": p.stem,
            "filename": p.name,
            "path": f"/api/audio/file/{user_id}/{p.name}",
        })
    return {"ok": True, "items": items}

@api.get("/audio/file/{user_id}/{filename}")
def get_audio_file(user_id: str, filename: str):
    file_path = ensure_user_dir(user_id) / filename
    if not file_path.exists():
        raise HTTPException(status_code=404, detail="파일이 없습니다.")
    return FileResponse(file_path, media_type="audio/webm", filename=filename)

# Mount router
app.include_router(api)
