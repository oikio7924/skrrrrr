import os, json, traceback, random
instructions = (
SENIOR_SAFETY_INSTR
+ name_line
+ "\n[오늘의 화두 후보(내부용)]\n"
+ topics_lines
+ "\n[시작 방법] 오늘의 후보 중에서 적절한 1개만 먼저 제시하고, 바로 가볍게 질문 1개로 시작하세요. 목록을 나열하지 마세요."
)


session = client.beta.realtime.sessions.create(
model="gpt-4o-mini-realtime-preview",
voice="verse",
instructions=instructions,
# 자동응답(서버 VAD) 민감도 낮추기 — 필요시 create_responses=False로 완전 OFF
turn_detection={
"type": "server_vad",
"threshold": 0.90,
"silence_duration_ms": 1200,
# "create_responses": True,
},
)
payload = session.to_dict()
payload["today_topics"] = topics
return JSONResponse(content=payload)
except Exception as e:
print("=== /api/session ERROR ==="); traceback.print_exc()
raise HTTPException(status_code=502, detail=str(e))


# --------- Audio Upload/List/File ---------


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
user_dir = ensure_user_dir(user_id)
file_path = user_dir / filename
if not file_path.exists():
raise HTTPException(status_code=404, detail="파일이 없습니다.")
return FileResponse(file_path, media_type="audio/webm", filename=filename)


# Mount router
app.include_router(api)