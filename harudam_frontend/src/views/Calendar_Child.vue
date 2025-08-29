<template>
  <div class="calendar-root mobile-screen flex flex-col mx-auto min-h-screen">
    <!-- 상단 헤더 -->
    <header class="timeline-header">
      <button class="back-btn" @click="goBack" aria-label="뒤로가기">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
          class="icon">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <h1 class="title">타임라인</h1>
    </header>


    <!-- 메인 -->
    <main class="flex-grow p-4 bg-white rounded-t-3xl -mt-4 overflow-auto relative"
      :class="viewMode === 'week' ? 'overflow-auto' : 'overflow-hidden'">
      <!-- 월/주 이동 헤더 -->
      <div class="flex items-center justify-between mb-2">
        <button class="text-gray-600 hover:text-gray-900 w-8 h-8" title="이전" @click="goPrev">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-6 h-6">
            <path fill-rule="evenodd"
              d="M12.707 15.707a1 1 0 01-1.414 0l-5-5a1 1 0 010-1.414l5-5a1 1 0 111.414 1.414L8.414 10l4.293 4.293a1 1 0 010 1.414z"
              clip-rule="evenodd" />
          </svg>
        </button>

        <h2 class="text-xl">{{ monthLabel }}</h2>

        <button class="text-gray-600 hover:text-gray-900 w-8 h-8" title="다음" @click="goNext">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-6 h-6">
            <path fill-rule="evenodd"
              d="M7.293 4.293a1 1 0 011.414 0l5 5a1 1 0 010 1.414l-5 5a1 1 0 01-1.414-1.414L11.586 10 7.293 5.707a1 1 0 010-1.414z"
              clip-rule="evenodd" />
          </svg>
        </button>
      </div>

      <!-- 달력 -->
      <section id="calendar-body" class="mb-4">
        <!-- 요일 헤더 -->
        <div id="dow-head" class="grid grid-cols-7 text-center text-sm text-gray-700 px-2">
          <div class="text-red-500">S</div>
          <div>M</div>
          <div>T</div>
          <div>W</div>
          <div>T</div>
          <div>F</div>
          <div class="text-blue-500">S</div>
        </div>

        <!-- 날짜 그리드 -->
        <div id="calendar-grid" :key="`${year}-${month}`" class="text-center" :class="{ shifted: viewMode === 'week' }"
          :style="gridStyle" ref="gridRef">
          <div v-for="(week, wIdx) in weeks" :key="wIdx" class="week-row" :class="{
            'hidden-row': viewMode === 'week' && wIdx !== selectedWeekIndex,
            'above': viewMode === 'week' && wIdx === selectedWeekIndex
          }" :ref="el => setWeekRef(wIdx, el)">
            <div v-for="(cell, cIdx) in week" :key="cIdx"
              class="flex justify-center items-center h-12 cursor-pointer relative" @click="cell && onDateClick(cell)">
              <template v-if="cell">
                <div v-if="eventDaysSet.has(cell.key)" class="event-dot"></div>
                <span class="date-chip"
                  :class="{ 'selected-badge': isSelected(cell), 'today-ring': isToday(cell) && !isSelected(cell) }">
                  {{ cell.day }}
                </span>
              </template>
            </div>
          </div>
        </div>
      </section>

      <!-- 상세 시트 (주간 보기) -->
      <section id="details-view" :class="viewMode === 'week' ? 'sheet-show' : 'sheet-hidden'"
        :style="{ marginTop: detailsMarginTop }">

        <!-- 헤더 (날짜 + 되돌아가기 버튼) -->
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold text-gray-800">{{ selectedDateText }}</h3>
          <button @click="backToMonth"
            class="w-9 h-9 flex items-center justify-center rounded-full border border-gray-300 bg-white shadow">
            ←
          </button>
        </div>

        <!-- 기록 -->
        <div class="detail-section diary">
          <h4>기록</h4>

          <template v-if="currentPayload.diaries.length === 0">
            <div class="flex flex-col items-center text-center">
              <!-- 이미지 -->
              <div
                class="w-full max-w-xs h-40 bg-gray-100 rounded-lg flex items-center justify-center mb-3 text-sm text-gray-400">
                이미지 없음
              </div>
              <!-- 문구 -->
              <div class="font-semibold text-gray-500">등록된 그림일기 기록이 없어요.</div>
            </div>
          </template>

          <template v-else>
            <div v-for="d in currentPayload.diaries" :key="d.id" class="flex flex-col items-center text-center">

              <!-- 이미지 -->
              <div class="w-full max-w-xs h-40 bg-gray-100 rounded-lg overflow-hidden mb-3">
                <img v-if="d.image" :src="d.image" class="w-full h-full object-cover" />
                <span v-else class="text-sm text-gray-400 flex items-center justify-center h-full">이미지 없음</span>
              </div>

              <!-- 제목 -->
              <div class="font-semibold">{{ d.title || '등록된 그림일기 기록이 없어요.' }}</div>
              <!-- 내용 -->
              <div class="text-sm text-gray-600">{{ d.content || '내용이 없습니다.' }}</div>
            </div>
          </template>
        </div>



        <!-- 감정 -->
        <div class="detail-section">
          <h4>해당 날짜의 감정</h4>
          <template v-if="!currentPayload.emotion">
            <div class="flex items-center gap-3">
              <span class="w-12 h-12 flex items-center justify-center rounded-full bg-gray-100"></span>
              <span class="text-sm text-gray-500">감정 정보 없음</span>
            </div>
          </template>
          <template v-else>
            <div class="flex items-center gap-3">
              <span class="w-12 h-12 flex items-center justify-center rounded-full bg-gray-100">
                😊
              </span>
              <span class="font-medium">{{ currentPayload.emotion }}</span>
            </div>
          </template>
        </div>


        <!-- 일정 -->
        <div class="detail-section">
          <h4>일정</h4>
          <template v-if="currentPayload.events.length === 0">
            <div class="text-sm text-gray-500">등록된 일정이 없어요.</div>
          </template>
          <template v-else>
            <button class="text-sm text-indigo-600 font-medium underline" @click="goToEventsPage">
              오늘 일정 {{ currentPayload.events.length }}건 보기 →
            </button>
          </template>
        </div>

      </section>

    </main>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: 'CalendarChildView' })

import type { ComponentPublicInstance } from 'vue'
// ✅ [수정] watch 훅을 import 합니다.
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, onBeforeUpdate, watch } from 'vue'
import { useRouter } from "vue-router"
const Logo = 'https://placehold.co/32x32/A78BFA/FFFFFF?text=H'

const router = useRouter()

type ViewMode = 'month' | 'week'
type Diary = { id: string; title: string; content?: string; image?: string }
type EventItem = { id?: string; time?: string; title: string; type?: string; note?: string }
type Emotion = 'happy' | 'neutral' | 'sad' | 'angry' | 'anxious' | 'calm' | 'surprised'

type DayPayload = {
  diaries: Diary[];
  events: EventItem[];
  emotion?: Emotion
}

type DataByDate = Record<string, DayPayload>
type Cell = { dateObj: Date; key: string; day: number }

const logoSrc = Logo
const homeHref = '/main'

const viewMode = ref<ViewMode>('month')
const currentDate = ref<Date>(new Date()); currentDate.value.setDate(1)
const selectedDate = ref<Date | null>(null)
const selectedWeekIndex = ref<number>(-1)
const isNavigating = ref(false)

const dataByDate = reactive<DataByDate>({})
const LS_KEY = 'harudam.calendar.local.v1'

const toKey = (d: Date): string => `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`

const year = computed(() => currentDate.value.getFullYear())
const month = computed(() => currentDate.value.getMonth())
const monthLabel = computed(() => `${year.value}년 ${month.value + 1}월`)

const weeks = computed<(Cell | null)[][]>(() => {
  const firstDow = new Date(year.value, month.value, 1).getDay()
  const lastDate = new Date(year.value, month.value + 1, 0).getDate()
  const rows: (Cell | null)[][] = []
  let d = 1
  for (let r = 0; r < 6; r++) {
    const row: (Cell | null)[] = []
    for (let c = 0; c < 7; c++) {
      if (r === 0 && c < firstDow) row.push(null)
      else if (d <= lastDate) {
        const dateObj = new Date(year.value, month.value, d)
        row.push({ dateObj, key: toKey(dateObj), day: d })
        d++
      } else row.push(null)
    }
    if (d > lastDate && row.every(cell => cell === null)) continue;
    rows.push(row)
  }
  return rows
})

const eventDaysSet = computed<Set<string>>(() => {
  const set = new Set<string>()
  for (const [k, v] of Object.entries(dataByDate) as [string, DayPayload][]) {
    if ((v.diaries?.length ?? 0) > 0 || (v.events?.length ?? 0) > 0) set.add(k)
  }
  return set
})

const selectedDateText = computed(() => {
  if (!selectedDate.value) return ''
  const d = selectedDate.value
  const dow = d.toLocaleDateString('ko-KR', { weekday: 'long' })
  return `${d.getMonth() + 1}월 ${d.getDate()}일 ${dow}`
})
const currentPayload = computed<DayPayload>(() => {
  if (!selectedDate.value) return { diaries: [], events: [] }
  return dataByDate[toKey(selectedDate.value)] ?? { diaries: [], events: [] }
})

const gridRef = ref<HTMLElement | null>(null)
const weekRefs = ref<Array<HTMLElement | null>>([])
const gridStyle = ref<Record<string, string>>({})
const detailsMarginTop = ref('0px')

onBeforeUpdate(() => {
  weekRefs.value = []
})

type MaybeWithEl = { $el: unknown }
function isHTMLElement(v: unknown): v is HTMLElement { return v instanceof HTMLElement }
function has$el(v: unknown): v is MaybeWithEl {
  return typeof v === 'object' && v !== null && '$el' in (v as Record<string, unknown>)
}
function setWeekRef(index: number, el: Element | ComponentPublicInstance | null) {
  let dom: HTMLElement | null = null
  if (isHTMLElement(el)) dom = el
  else if (has$el(el) && isHTMLElement((el as MaybeWithEl).$el)) dom = (el as MaybeWithEl).$el as HTMLElement
  weekRefs.value[index] = dom
}

function isSelected(cell: Cell) { return !!selectedDate.value && toKey(selectedDate.value) === cell.key }
function isToday(cell?: Cell | null) {
  if (!cell) return false
  const t = new Date()
  return cell.dateObj.getFullYear() === t.getFullYear()
    && cell.dateObj.getMonth() === t.getMonth()
    && cell.dateObj.getDate() === t.getDate()
}

function goToEventsPage() {
  // 라우터 이동 예시
  router.push('/events')   // 일정 상세 페이지 경로
}

function goBack() {
  router.push('main_child')
}

// =================================================================
// ✅ [수정된 핵심 로직]
// =================================================================

/**
 * 화면 위치를 조정하는 함수
 */
function adjustWeekPosition() {
  if (selectedWeekIndex.value === -1 || viewMode.value !== 'week') return;

  const grid = gridRef.value
  const row = weekRefs.value[selectedWeekIndex.value]

  if (!grid || !row) return;

  const rowTop = row.offsetTop
  const rowH = row.offsetHeight
  const gridH = grid.scrollHeight
  const overlap = 12
  gridStyle.value = { '--week-shift': `${-rowTop}px` }
  const coverFrom = rowTop + rowH - overlap
  const pullUp = gridH - coverFrom
  const marginUp = pullUp + rowTop
  detailsMarginTop.value = `-${marginUp}px`
}

/**
 * selectedDate와 currentDate의 변경을 감지하여 주간 보기 UI를 업데이트하는 Watcher
 */
watch([selectedDate, currentDate], ([newSelectedDate], [oldSelectedDate]) => {
  if (viewMode.value !== 'week' || !newSelectedDate) {
    return;
  }

  // DOM 업데이트가 완료된 후 실행되도록 보장
  nextTick(() => {
    const newKey = toKey(newSelectedDate);
    const newWeekIndex = weeks.value.findIndex(row => row.some(c => c && c.key === newKey));

    if (newWeekIndex !== -1) {
      selectedWeekIndex.value = newWeekIndex;
      adjustWeekPosition();
    }
  });
}, { flush: 'post' }); // flush: 'post'는 컴포넌트 업데이트 후에 watcher가 실행되도록 보장


/** 이전 버튼 클릭 핸들러 */
function goPrev() {
  if (isNavigating.value) return;
  isNavigating.value = true;

  if (viewMode.value === 'month') {
    currentDate.value = new Date(year.value, month.value - 1, 1);
  } else if (viewMode.value === 'week' && selectedDate.value) {
    const newDate = new Date(selectedDate.value);
    newDate.setDate(newDate.getDate() - 7);

    // selectedDate만 변경하면 watch가 나머지를 처리
    selectedDate.value = newDate;
    if (newDate.getMonth() !== month.value) {
      currentDate.value = new Date(newDate.getFullYear(), newDate.getMonth(), 1);
    }
  }

  // 애니메이션 시간 등을 고려하여 짧은 딜레이 후 플래그 해제
  setTimeout(() => { isNavigating.value = false; }, 100);
}


/** 다음 버튼 클릭 핸들러 */
function goNext() {
  if (isNavigating.value) return;
  isNavigating.value = true;

  if (viewMode.value === 'month') {
    currentDate.value = new Date(year.value, month.value + 1, 1);
  } else if (viewMode.value === 'week' && selectedDate.value) {
    const newDate = new Date(selectedDate.value);
    newDate.setDate(newDate.getDate() + 7);

    // selectedDate만 변경하면 watch가 나머지를 처리
    selectedDate.value = newDate;
    if (newDate.getMonth() !== month.value) {
      currentDate.value = new Date(newDate.getFullYear(), newDate.getMonth(), 1);
    }
  }
  setTimeout(() => { isNavigating.value = false; }, 100);
}

/** 날짜 클릭 핸들러 */
function onDateClick(cell: Cell) {
  viewMode.value = 'week'
  // selectedDate만 변경하면 watch가 나머지를 처리
  selectedDate.value = cell.dateObj
}

// =================================================================

function handleResize() { if (viewMode.value === 'week') adjustWeekPosition() }

function backToMonth() {
  viewMode.value = 'month'
  gridStyle.value = { '--week-shift': '0px' }
  detailsMarginTop.value = '0px'
  selectedWeekIndex.value = -1
  selectedDate.value = null
}

function persist() { try { localStorage.setItem(LS_KEY, JSON.stringify(dataByDate)) } catch { } }
function restore() {
  try {
    const raw = localStorage.getItem(LS_KEY)
    if (raw) Object.assign(dataByDate, JSON.parse(raw) as DataByDate)
  } catch { }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  restore()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');

html,
body {
  height: 100%;
  overflow: hidden;
  /* ✅ 배경 스크롤 막기 */
}

main {
  overflow: hidden;
  /* ✅ 모달 외부는 스크롤 안되게 */
}

/* ✅ 타임라인 헤더 */
.timeline-header {
  position: fixed;   /* sticky → fixed */
  top: 0;
  z-index: 200;      /* 다른 요소 위에 오도록 */
  display: flex;
  align-items: center;
  justify-content: center;  /* 중앙 정렬 */
  width: 100%;
  padding: 0.8rem 1rem;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

.back-btn {
  position: absolute;
  /* 중앙 타이틀 영향 X */
  left: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.back-btn .icon {
  width: 28px;
  height: 28px;
  stroke: #6d28d9;
  /* 보라색 아이콘 */
}

.calendar-root {
  font-family: 'Jua', 'Noto Sans KR', sans-serif;
  font-variant-numeric: tabular-nums;
  --lav-50: #F5F3FF;
  --lav-100: #EDE9FE;
  --lav-200: #DDD6FE;
  --lav-400: #A78BFA;
  --lav-500: #8B5CF6;
}

.mobile-screen {
  width: 100%;
  max-width: 480px;
  margin: 0 auto;
  background: transparent;
  position: relative;
}

/* 월간 보기에서는 강제로 높이 채우지 않음 */
.view-month .mobile-screen {
  min-height: auto;
}

/* 주간 보기일 때는 화면을 채움 */
.view-week .mobile-screen {
  min-height: 100dvh;
}

/* 알림 아이콘 */
.bell-svg {
  filter: drop-shadow(0 2px 6px rgba(250, 204, 21, .35));
  transition: transform .15s ease, filter .2s ease, opacity .2s ease;
}

.bell-svg:hover {
  transform: translateY(-1px);
  filter: drop-shadow(0 6px 14px rgba(250, 204, 21, .45));
  opacity: .95;
}

/* 달력 카드 */
#calendar-body {
  background: linear-gradient(180deg, var(--lav-50) 0%, var(--lav-100) 100%);
  border-radius: 1.25rem;
  border: 1px solid var(--lav-200);
  box-shadow: 0 12px 24px rgba(0, 0, 0, .10);
  position: relative;
  padding-bottom: .5rem;
}

/* 요일 헤더 */
#dow-head {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-size: .9rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: .5rem !important;
  border-top-left-radius: 1.25rem;
  border-top-right-radius: 1.25rem;
  padding: .5rem 0;
  background: rgba(255, 255, 255, .55);
  -webkit-backdrop-filter: blur(2px);
  backdrop-filter: blur(2px);
  box-shadow: inset 0 -1px 0 rgba(0, 0, 0, .04);
}

/* 주 단위 줄 */
.week-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.25rem 0;
  height: 4.2rem;
}

.week-row.above {
  position: relative;
  z-index: 50;
}

.week-row.hidden-row {
  visibility: hidden;
  opacity: 0;
  pointer-events: none;
}

#calendar-grid {
  display: flex;
  flex-direction: column;
  gap: .4rem;
  position: relative;
  transition: transform .35s ease;
  will-change: transform;
}

#calendar-grid.shifted {
  transform: translateY(var(--week-shift, 0px));
}

.date-chip {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.4rem;
  height: 2.4rem;
  margin: 0 auto 0.3rem;
  font-size: 1rem;
  border-radius: 9999px;
  font-weight: 600;
  color: #111827;
}

#calendar-grid .selected-badge {
  background-color: #2563eb;
  color: #fff;
  box-shadow: 0 2px 6px rgba(37, 99, 235, .35);
}

.today-ring {
  outline: 2px solid rgba(16, 185, 129, .55);
  outline-offset: 2px;
  border-radius: 9999px;
}

#calendar-grid .event-dot {
  position: absolute;
  bottom: 3px;
  left: 50%;
  transform: translateX(-50%);
  width: 6px;
  height: 6px;
  border-radius: 9999px;
  background: linear-gradient(180deg, var(--lav-400), var(--lav-500));
  box-shadow: 0 0 0 2px rgba(255, 255, 255, .85);
  z-index: 0;
}

/* 상세 시트 */

#details-view {
  flex: 1 1 auto;
  /* ✅ flexbox 내부에서 영역 확보 */
  min-height: 65vh;
  max-height: 80vh;
  overflow-y: auto;
  /* ✅ 모달 내부만 스크롤 */
  background: #fff;
  border-radius: 1.25rem 1.25rem 0 0;
  border-top: 2px solid #e5e7eb;
  box-shadow: 0 -6px 18px rgba(0, 0, 0, .1);
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.sheet-hidden {
  opacity: 0;
  visibility: hidden;
  transform: translateY(6px);
}

.sheet-show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

/* 상세 시트 내 박스 */
.detail-section {
  border-radius: 0.75rem;
  border: 1px solid #e5e7eb;
  padding: 1rem;
  background: #fafafa;
  min-height: 80px;
}

.detail-section h4 {
  font-weight: 600;
  margin-bottom: .5rem;
}

.detail-section.diary {
  min-height: 140px;
  display: flex;
  flex-direction: column;
  gap: .5rem;
}
</style>
