<template>
  <div class="calendar-root">
    <!-- 상단 헤더 -->
    <header class="timeline-header">
      <button class="back-btn" @click="goBack" aria-label="뒤로가기">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
          class="icon">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <h1 class="title">캘린더</h1>
    </header>

    <!-- 메인 -->
    <main class="content">
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
        <div id="dow-head">
          <div class="text-red-500">S</div>
          <div>M</div>
          <div>T</div>
          <div>W</div>
          <div>T</div>
          <div>F</div>
          <div class="text-blue-500">S</div>
        </div>
        <!-- 날짜 그리드 -->
        <div id="calendar-grid">
          <div v-for="(week, wIdx) in weeks" :key="wIdx" class="week-row"
            v-show="viewMode !== 'week' || wIdx === selectedWeekIndex">
            <div v-for="(cell, cIdx) in week" :key="cIdx" class="date-cell"
              @click="cell && onDateClick(cell)">
              <template v-if="cell">
                <span class="date-chip"
                  :class="{ 'selected-badge': isSelected(cell), 'today-ring': isToday(cell) && !isSelected(cell) }">
                  {{ cell.day }}
                </span>
                <div class="emotion-sticker">
                  <img v-if="getStickerForCell(cell)" :src="getStickerForCell(cell) || ''" alt="감정스티커"
                    class="emotion-sticker-img" />
                </div>
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
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2"
              stroke="currentColor" class="w-6 h-6">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
        </div>
        <!-- 기록 -->
        <div class="detail-section diary">
          <h4>기록</h4>
          <template v-if="currentPayload.diaries.length === 0">
            <div class="flex flex-col items-center text-center">
              <div
                class="w-full h-64 bg-gray-100 rounded-lg flex items-center justify-center mb-3 text-sm text-gray-400">
                이미지 없음
              </div>
              <div class="font-semibold text-gray-500">등록된 그림일기 기록이 없어요.</div>
            </div>
          </template>
          <template v-else>
            <div v-for="d in currentPayload.diaries" :key="d.id" class="flex flex-col items-center text-center">
              <div class="w-full h-64 bg-gray-100 rounded-lg overflow-hidden mb-3">
                <img v-if="d.image" :src="d.image" class="w-full h-full object-cover" />
                <span v-else class="text-sm text-gray-400 flex items-center justify-center h-full">이미지 없음</span>
              </div>
              <div class="font-semibold">{{ d.title || '등록된 그림일기 기록이 없어요.' }}</div>
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
              <span class="w-16 h-16 flex items-center justify-center rounded-full bg-gray-100">
                <img :src="getEmotionStickerSrc(currentPayload.emotion)" alt="감정 이모티콘" class="emotion-sticker-img" />
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
// 스크립트 부분은 수정할 필요가 없으므로 그대로 유지합니다.
defineOptions({ name: 'CalendarChildView' })

import type { ComponentPublicInstance } from 'vue'
import {
  ref,
  reactive,
  computed,
  onMounted,
  onBeforeUnmount,
  nextTick,
  onBeforeUpdate,
  watch,
} from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

type ViewMode = 'month' | 'week'
type Diary = { id: string; title: string; content?: string; image?: string }
type EventItem = { id?: string; time?: string; title: string; type?: string; note?: string }
type Emotion = 'happy' | 'neutral' | 'sad' | 'angry' | 'anxious' | 'calm' | 'surprised';

type DayPayload = {
  diaries: Diary[]
  events: EventItem[]
  emotion?: Emotion
}

type DataByDate = Record<string, DayPayload>
type Cell = { dateObj: Date; key: string; day: number }

const viewMode = ref<ViewMode>('month')
const currentDate = ref<Date>(new Date())
currentDate.value.setDate(1)
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
      if ((r === 0 && c < firstDow) || d > lastDate) {
        row.push(null)
      } else {
        const dateObj = new Date(year.value, month.value, d)
        row.push({ dateObj, key: toKey(dateObj), day: d })
        d++
      }
    }
    rows.push(row)
  }
  return rows
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

function isSelected(cell: Cell) {
  return !!selectedDate.value && toKey(selectedDate.value) === cell.key
}
function isToday(cell?: Cell | null) {
  if (!cell) return false
  const t = new Date()
  return (
    cell.dateObj.getFullYear() === t.getFullYear() &&
    cell.dateObj.getMonth() === t.getMonth() &&
    cell.dateObj.getDate() === t.getDate()
  )
}

function goToEventsPage() {
  router.push('/events')
}

function goBack() {
  router.push('/main_child')
}

function adjustWeekPosition() {
  if (selectedWeekIndex.value === -1 || viewMode.value !== 'week') return
  const grid = gridRef.value
  const row = weekRefs.value[selectedWeekIndex.value]
  if (!grid || !row) return
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

watch(
  [selectedDate, currentDate],
  ([newSelectedDate]) => {
    if (viewMode.value !== 'week' || !newSelectedDate) return
    nextTick(() => {
      const newKey = toKey(newSelectedDate)
      const newWeekIndex = weeks.value.findIndex((row) => row.some((c) => c && c.key === newKey))
      if (newWeekIndex !== -1) {
        selectedWeekIndex.value = newWeekIndex
        adjustWeekPosition()
      }
    })
  },
  { flush: 'post' },
)

function goPrev() {
  if (isNavigating.value) return
  isNavigating.value = true
  if (viewMode.value === 'month') {
    currentDate.value = new Date(year.value, month.value - 1, 1)
  } else if (viewMode.value === 'week' && selectedDate.value) {
    const newDate = new Date(selectedDate.value)
    newDate.setDate(newDate.getDate() - 7)
    selectedDate.value = newDate
    if (newDate.getMonth() !== month.value) {
      currentDate.value = new Date(newDate.getFullYear(), newDate.getMonth(), 1)
    }
  }
  setTimeout(() => { isNavigating.value = false }, 100)
}

function goNext() {
  if (isNavigating.value) return
  isNavigating.value = true
  if (viewMode.value === 'month') {
    currentDate.value = new Date(year.value, month.value + 1, 1)
  } else if (viewMode.value === 'week' && selectedDate.value) {
    const newDate = new Date(selectedDate.value)
    newDate.setDate(newDate.getDate() + 7)
    selectedDate.value = newDate
    if (newDate.getMonth() !== month.value) {
      currentDate.value = new Date(newDate.getFullYear(), newDate.getMonth(), 1)
    }
  }
  setTimeout(() => { isNavigating.value = false }, 100)
}

function onDateClick(cell: Cell) {
  viewMode.value = 'week'
  selectedDate.value = cell.dateObj
}

function handleResize() {
  if (viewMode.value === 'week') adjustWeekPosition()
}

function backToMonth() {
  viewMode.value = 'month'
  gridStyle.value = { '--week-shift': '0px' }
  detailsMarginTop.value = '0px'
  selectedWeekIndex.value = -1
  selectedDate.value = null
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  const sampleData: DataByDate = {
    '2025-8-2': { diaries: [], events: [], emotion: 'happy' },
    '2025-8-5': { diaries: [], events: [], emotion: 'anxious' },
    '2025-8-13': { diaries: [], events: [], emotion: 'neutral' },
    // ✅ [수정] 16일에 더미 일정 데이터 추가
    '2025-8-16': { diaries: [], events: [{ id: 'evt1', title: '오후 2시 병원 방문' }], emotion: 'sad' },
    '2025-8-19': { diaries: [], events: [], emotion: 'surprised' },
    '2025-8-22': { diaries: [], events: [], emotion: 'angry' },
    '2025-8-29': { diaries: [], events: [], emotion: 'calm' }
  };
  Object.assign(dataByDate, sampleData);
})

function getEmotionStickerSrc(emotion: Emotion): string {
  const map: Record<Emotion, string> = {
    happy: 'Happy_emotion.png',
    sad: 'Sad_emotion.png',
    angry: 'Angry_emotion.png',
    neutral: 'Neurtal_emotion.png',
    anxious: 'Anxious_emotion.png',
    calm: 'Calm_emotion.png',
    surprised: 'Surprized_emotion.png'
  }
  return new URL(`../assets/emotions/${map[emotion]}`, import.meta.url).href
}

function getStickerForCell(cell: Cell): string | null {
  const d = dataByDate[cell.key]
  return d && d.emotion ? getEmotionStickerSrc(d.emotion) : null
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');

/* 최상위 컨테이너 (모바일 화면 틀 + 중앙 정렬) */
.calendar-root {
  width: 100%;
  max-width: 720px; /* 모바일 화면 너비 */
  margin: 0 auto; /* 화면 중앙 배치 */
  height: 100vh; /* 화면 전체 높이 */
  display: flex;
  flex-direction: column;
  background-color: #f8fafc; /* 배경색 통일 */
  position: relative;
  font-family: 'Noto Sans KR', 'Jua', sans-serif;
  font-weight: 400;
  --lav-50: #f5f3ff;
  --lav-100: #ede9fe;
  --lav-200: #ddd6fe;
  --lav-400: #a78bfa;
  --lav-500: #8b5cf6;
}

/* 상단 헤더 */
.timeline-header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  z-index: 10;
  position: relative;
  border-bottom: 1px solid #e5e7eb;
}

.timeline-header .title {
  font-size: 1.125rem;
  font-weight: 700;
}

.back-btn {
  position: absolute;
  left: 1rem;
  background: none;
  border: none;
  cursor: pointer;
}

.back-btn .icon {
  width: 24px;
  height: 24px;
  stroke: #4c1d95;
}

/* 메인 콘텐츠 (스크롤 담당) */
.content {
  flex: 1;
  overflow-y: hidden;
  padding: 1rem;
  background-color: #f8fafc;
}

/* 월/주 이동 헤더의 h2 태그 */
h2.text-xl {
  font-weight: 700;
  font-family: 'Jua', 'Noto Sans KR', sans-serif;
}

/* 달력 카드 */
#calendar-body {
  background: linear-gradient(180deg, var(--lav-50) 0%, var(--lav-100) 100%);
  border-radius: 1.25rem;
  border: 1px solid var(--lav-200);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  padding-bottom: 0.5rem;
  display: flex;
  flex-direction: column;
  /* ✅ [수정] 달력의 높이를 고정하여 월별 크기 변화 방지 */
  height: 34rem;
}

/* 요일 헤더 */
#dow-head {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-size: 0.9rem;
  font-weight: 500;
  color: #374151;
  padding: 0.5rem 0;
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
  box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.04);
  border-top-left-radius: 1.25rem;
  border-top-right-radius: 1.25rem;
}

#calendar-grid {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  flex-grow: 1;
}

.week-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 0.25rem 0;
  flex-grow: 1;
  /* ✅ [수정] 주차별 높이가 동일하게 유지되도록 최소 높이 설정 */
  min-height: 4.5rem;
}

.date-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 0.25rem 0;
}

.date-chip {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  font-size: 1rem;
  border-radius: 9999px;
  color: #1f2937;
  cursor: pointer;
  transition: all 0.2s ease;
}

#calendar-grid .selected-badge {
  background-color: #6d28d9;
  color: #fff;
  font-weight: 700;
}

.today-ring {
  box-shadow: 0 0 0 2px var(--lav-400);
}

.emotion-sticker {
  margin-top: 4px;
  height: 30px;
  width: 30px;
}

.emotion-sticker-img {
  width: 100%;
  height: 100%;
}


/* 상세 시트 (하단에서 올라오는 UI) */
#details-view {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  max-height: 70%;
  background: #fff;
  border-radius: 1.25rem 1.25rem 0 0;
  box-shadow: 0 -6px 18px rgba(0, 0, 0, 0.1);
  padding: 1.5rem 1rem;
  transition: transform 0.35s ease-in-out, visibility 0.35s;
  z-index: 20;
  overflow-y: auto;
}

.sheet-hidden {
  visibility: hidden;
  transform: translateY(100%);
}

.sheet-show {
  visibility: visible;
  transform: translateY(0);
}

.detail-section {
  border-radius: 0.75rem;
  border: 1px solid #e5e7eb;
  padding: 1rem;
  background: #f9fafb;
  margin-bottom: 1rem;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  font-weight: 700;
  margin-bottom: 0.75rem;
  font-size: 1rem;
  color: #374151;
}

</style>

