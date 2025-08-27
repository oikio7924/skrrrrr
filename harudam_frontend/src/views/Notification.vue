<!-- src/views/Notifications.vue -->
<template>
  <!-- 화면 전체 컨테이너: 흰 배경 + 최소 화면 높이 -->
  <div class="min-h-screen bg-white">
    <!-- ▣ 상단 헤더: 스크롤해도 상단에 고정(sticky) -->
    <header class="sticky top-0 z-10 bg-white/95 backdrop-blur border-b">
      <div class="max-w-md mx-auto px-4 py-3 flex items-center justify-between">
        <!-- ◀ 뒤로가기 버튼: 라우터 히스토리 뒤로 -->
        <button
          class="p-2 -ml-2 rounded-lg hover:bg-gray-100 active:scale-95 transition"
          aria-label="뒤로가기"
          @click="goBack"
        >
          <!-- 아이콘(SVG) -->
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6"
               fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/>
          </svg>
        </button>

        <!-- 페이지 타이틀 -->
        <h1 class="text-base font-semibold">알림</h1>

        <!-- 우측 공간 맞춤(균형 맞추기용 더미 엘리먼트) -->
        <div class="w-10"></div>
      </div>
    </header>

    <!-- ▣ 본문: 섹션별 목록 -->
    <main class="max-w-md mx-auto px-4 py-6 space-y-10">
      <!-- [섹션] 오늘 알림: groups.today 배열에 값이 있을 때만 렌더 -->
      <section v-if="groups.today.length">
        <h2 class="text-gray-500 text-sm font-semibold mb-3">오늘</h2>

        <!-- 알림 카드 리스트 -->
        <ul class="space-y-3">
          <!-- 각 알림 아이템: key는 고유 id -->
          <li v-for="n in groups.today" :key="n.id" class="noti-card">
            <div class="flex items-start gap-3">
              <!-- 좌측: 알림 타입 아이콘 -->
              <div class="mt-0.5">
                <div class="w-9 h-9 rounded-xl bg-gray-50 border border-gray-200 flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-700" fill="none"
                       viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <!-- meta(n.type).icon으로 아이콘 종류 결정, path는 iconPath()가 반환 -->
                    <path stroke-linecap="round" stroke-linejoin="round" :d="iconPath(meta(n.type).icon)" />
                  </svg>
                </div>
              </div>

              <!-- 우측: 텍스트 콘텐츠 -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <!-- 타입 뱃지(일정/대화/자서전/시스템) -->
                  <span :class="['px-2.5 py-1 rounded-full text-[11px] font-semibold ring-1', meta(n.type).cls]">
                    {{ meta(n.type).text }}
                  </span>
                  <!-- 시간(로컬 시각) -->
                  <span class="text-xs text-gray-400">{{ fmtTime(n.at) }}</span>
                </div>

                <!-- 제목(굵게 한 줄) -->
                <p class="mt-1.5 text-sm font-medium text-gray-900 truncate">{{ n.title }}</p>

                <!-- 본문(옵션): body가 있을 때만 표시 -->
                <p v-if="n.body" class="mt-1 text-sm text-gray-600 whitespace-pre-line">{{ n.body }}</p>
              </div>
            </div>
          </li>
        </ul>
      </section>

      <!-- [섹션] 어제 -->
      <section v-if="groups.yesterday.length">
        <!-- sticky로 스크롤 중에도 섹션 제목이 상단에 붙음 -->
        <h2 class="text-gray-500 text-sm font-semibold mb-3 sticky top-[52px] bg-white/90 backdrop-blur">
          어제
        </h2>
        <ul class="space-y-3 mt-3">
          <li v-for="n in groups.yesterday" :key="n.id" class="noti-card">
            <div class="flex items-start gap-3">
              <div class="mt-0.5">
                <div class="w-9 h-9 rounded-xl bg-gray-50 border border-gray-200 flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-700" fill="none"
                       viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" :d="iconPath(meta(n.type).icon)" />
                  </svg>
                </div>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span :class="['px-2.5 py-1 rounded-full text-[11px] font-semibold ring-1', meta(n.type).cls]">
                    {{ meta(n.type).text }}
                  </span>
                  <span class="text-xs text-gray-400">{{ fmtTime(n.at) }}</span>
                </div>
                <p class="mt-1.5 text-sm font-medium text-gray-900 truncate">{{ n.title }}</p>
                <p v-if="n.body" class="mt-1 text-sm text-gray-600 whitespace-pre-line">{{ n.body }}</p>
              </div>
            </div>
          </li>
        </ul>
      </section>

      <!-- [섹션] 최근 7일 -->
      <section v-if="groups.last7.length">
        <h2 class="text-gray-500 text-sm font-semibold mb-3 sticky top-[52px] bg-white/90 backdrop-blur">
          이전 알림 (7일)
        </h2>
        <ul class="space-y-3 mt-3">
          <li v-for="n in groups.last7" :key="n.id" class="noti-card">
            <div class="flex items-start gap-3">
              <div class="mt-0.5">
                <div class="w-9 h-9 rounded-xl bg-gray-50 border border-gray-200 flex items-center justify-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-700" fill="none"
                       viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" :d="iconPath(meta(n.type).icon)" />
                  </svg>
                </div>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span :class="['px-2.5 py-1 rounded-full text-[11px] font-semibold ring-1', meta(n.type).cls]">
                    {{ meta(n.type).text }}
                  </span>
                  <span class="text-xs text-gray-400">{{ fmtTime(n.at) }}</span>
                </div>
                <p class="mt-1.5 text-sm font-medium text-gray-900 truncate">{{ n.title }}</p>
                <p v-if="n.body" class="mt-1 text-sm text-gray-600 whitespace-pre-line">{{ n.body }}</p>
              </div>
            </div>
          </li>
        </ul>
      </section>

      <!-- [빈 상태] 최근 7일 알림이 하나도 없을 때 -->
      <section v-if="totalCount === 0" class="pt-10">
        <div class="rounded-2xl border border-dashed border-gray-300 p-8 text-center">
          <div class="mx-auto mb-3 w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none"
                 viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round"
                    d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5"/>
            </svg>
          </div>
          <p class="text-sm text-gray-500">최근 7일간 알림이 없어요.</p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
/**
 * ▣ 로직 영역
 * - Vue 반응성/계산 속성, 라우터를 사용
 */
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'

/**
 * ▣ 알림 데이터 타입 정의
 * - type: 화면에서 칩과 아이콘을 결정 (event/chat/diary/system)
 * - body: 옵션(없을 수도 있음)
 * - at: ISO 문자열(시간 비교/표시에 사용)
 */
type Noti = {
  id: string
  type: 'event' | 'system' | 'chat' | 'diary'
  title: string
  body?: string
  at: string   // ISO 날짜문자열
}

/**
 * ▣ 임시 목업 데이터 (백엔드 붙기 전)
 * - 화면 형태와 동작을 테스트하기 위한 예시 데이터
 */
const state = reactive<{ list: Noti[] }>({
  list: [
    // 오늘 지금 시각
    { id: 'n1', type: 'event',  title: '오전 10:00 팀 미팅', body: '미팅 10분 전 리마인드', at: new Date().toISOString() },
    // 오늘 1시간 전
    { id: 'n2', type: 'chat',   title: 'AI 대화 요약이 도착했어요', body: '오늘 기분: 평온 😊  •  산책 일정이 캘린더에 추가됨', at: new Date(Date.now()-1000*60*60).toISOString() },
    // 어제 15:00
    { id: 'n3', type: 'diary',  title: '그림일기 자동 저장 완료', body: '8월 6일 수요일 • 2개 항목', at: isoDaysAgo(1, 15) },
    // 3일 전 09:00
    { id: 'n4', type: 'system', title: '앱 업데이트 안내', body: '접근성 개선 및 안정화', at: isoDaysAgo(3, 9) },
    // 5일 전 11:00
    { id: 'n5', type: 'event',  title: '병원 정기 검진 일정', body: '8월 9일(금) 오전 9:30', at: isoDaysAgo(5, 11) },
  ],
})

/**
 * ▣ 유틸: n일 전 특정 시각(시:00)을 ISO 문자열로 반환
 * - days: 오늘에서 며칠 전 (0=오늘, 1=어제)
 * - hour: 0~23, 기본 9시
 */
function isoDaysAgo(days: number, hour = 9) {
  const d = new Date()
  d.setDate(d.getDate() - days) // n일 전으로 이동
  d.setHours(hour, 0, 0, 0)     // 시:분:초:ms 고정
  return d.toISOString()        // ISO(UTC) 문자열로 변환
}

/**
 * ▣ 날짜 경계값(로컬 기준)
 * - todayStart: 오늘 00:00
 * - yesterdayStart: 어제 00:00
 * - last7Start: 7일 전 00:00
 *   → 이 경계들을 기준으로 three-group(오늘/어제/최근7일) 분류
 */
const todayStart = new Date(); todayStart.setHours(0,0,0,0)
const yesterdayStart = new Date(todayStart); yesterdayStart.setDate(todayStart.getDate()-1)
const last7Start = new Date(todayStart); last7Start.setDate(todayStart.getDate()-7)

/**
 * ▣ 그룹핑 계산 속성
 * - 최신순으로 정렬한 뒤 경계값에 따라 분류
 * - 반환: { today, yesterday, last7 }
 */
const groups = computed(() => {
  const t: Noti[] = []
  const y: Noti[] = []
  const l7: Noti[] = []

  // 스프레드로 사본을 만든 뒤, at 기준 내림차순(최신→오래됨) 정렬
  for (const n of [...state.list].sort((a,b) => +new Date(b.at) - +new Date(a.at))) {
    const at = new Date(n.at)
    if (at >= todayStart) t.push(n)
    else if (at >= yesterdayStart && at < todayStart) y.push(n)
    else if (at >= last7Start) l7.push(n)
    // 7일 이전 항목은 현재 화면에선 숨김
  }
  return { today: t, yesterday: y, last7: l7 }
})

/** 전체 알림 개수(빈 상태 UI 표시용) */
const totalCount = computed(() => state.list.length)

/**
 * ▣ 타입 메타 정보
 * - 각 type에 맞는 칩 텍스트/스타일(cls)과 아이콘 이름(icon) 매핑
 * - 템플릿에선 meta(n.type)으로 접근
 */
function meta(type: Noti['type']) {
  const map: Record<Noti['type'], { text: string; cls: string; icon: 'calendar'|'chat'|'book'|'bell' }> = {
    event:  { text: '일정',   cls: 'bg-violet-50 text-violet-700 ring-violet-200', icon: 'calendar' },
    chat:   { text: '대화',   cls: 'bg-sky-50 text-sky-700 ring-sky-200',         icon: 'chat' },
    diary:  { text: '자서전', cls: 'bg-rose-50 text-rose-700 ring-rose-200',      icon: 'book' },
    system: { text: '시스템', cls: 'bg-gray-100 text-gray-700 ring-gray-200',     icon: 'bell' },
  }
  return map[type]
}

/**
 * ▣ 시간 표시 포맷터
 * - ISO → 한국 로컬 시각(HH:MM, 오전/오후 포함)으로 변환
 */
function fmtTime(iso: string) {
  const d = new Date(iso)
  return d.toLocaleString('ko-KR', { hour: 'numeric', minute: '2-digit', hour12: true })
}

/**
 * ▣ 아이콘 path 생성기
 * - 간단한 outline SVG path를 반환
 */
function iconPath(name: 'calendar'|'chat'|'book'|'bell') {
  switch (name) {
    case 'calendar': return 'M8 7V5m8 2V5m-9 6h10M6 21h12a2 2 0 002-2V8a2 2 0 00-2-2H6a2 2 0 00-2 2v11a2 2 0 002 2z'
    case 'chat':     return 'M8 10h8m-8 4h5M5 20l3-3h11a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a1 1 0 001 1h1z'
    case 'book':     return 'M4 6a2 2 0 012-2h10a2 2 0 012 2v12a1 1 0 01-1 1H6a2 2 0 01-2-2V6z'
    default:         return 'M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5'
  }
}

/**
 * ▣ 네비게이션: 뒤로가기
 * - 히스토리가 있으면 back, 없으면 홈('/')로
 */
const router = useRouter()
function goBack() {
  if (window.history.length > 1) router.back()
  else router.push('/main_child')
}
</script>

<style scoped>
/* 카드 공통 스타일(둥근 모서리 + 테두리 + 그림자) */
.noti-card { @apply rounded-2xl border border-gray-200 p-4 bg-white shadow-sm; }
/* 버튼 살짝 눌림 애니메이션 */
button { transition: transform .05s ease; }
</style>
