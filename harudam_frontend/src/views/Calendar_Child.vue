<template>
  <!-- 페이지 전용 폰트 적용 -->
  <div class="calendar-root mobile-screen flex flex-col">
    <!-- Header -->
    <header class="flex items-center justify-between p-4 bg-gray-100 border-b">
      <a :href="homeHref" class="flex items-center space-x-2 hover:opacity-80 transition">
        <img :src="logoSrc" alt="하루담" class="w-8 h-8 rounded-lg object-contain" />
        <span class="text-lg">하루담</span>
      </a>

      <!-- 종 아이콘: 인라인 SVG(노란색) -->
      <button class="relative" aria-label="알림">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"
             fill="currentColor" class="w-7 h-7 text-yellow-400 bell-svg">
          <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6z"/>
          <path d="M14 15a4 4 0 01-8 0h8z"/>
        </svg>
      </button>
    </header>

    <!-- Main -->
    <main class="flex-grow p-4 bg-white rounded-t-3xl -mt-4 overflow-auto relative">
      <div class="flex items-center justify-between mb-2">
        <button class="text-gray-600 hover:text-gray-900 w-8 h-8" title="이전 달" @click="goPrevMonth" :disabled="viewMode!=='month'">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"
               fill="currentColor" class="w-6 h-6">
            <path fill-rule="evenodd"
                  d="M12.707 15.707a1 1 0 01-1.414 0l-5-5a1 1 0 010-1.414l5-5a1 1 0 111.414 1.414L8.414 10l4.293 4.293a1 1 0 010 1.414z"
                  clip-rule="evenodd"/>
          </svg>
        </button>
        <h2 class="text-xl">{{ monthLabel }}</h2>
        <button class="text-gray-600 hover:text-gray-900 w-8 h-8" title="다음 달" @click="goNextMonth" :disabled="viewMode!=='month'">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"
               fill="currentColor" class="w-6 h-6">
            <path fill-rule="evenodd"
                  d="M7.293 4.293a1 1 0 011.414 0l5 5a1 1 0 010 1.414l-5 5a1 1 0 01-1.414-1.414L11.586 10 7.293 5.707a1 1 0 010-1.414z"
                  clip-rule="evenodd"/>
          </svg>
        </button>
      </div>

      <!-- Calendar -->
      <section id="calendar-body" class="mb-4">
        <div id="dow-head" class="grid grid-cols-7 text-center text-sm text-gray-700 px-2">
          <div class="text-red-500">S</div><div>M</div><div>T</div><div>W</div>
          <div>T</div><div>F</div><div class="text-blue-500">S</div>
        </div>

        <div id="calendar-grid" class="text-center" :class="{ shifted: viewMode==='week' }" :style="gridStyle" ref="gridRef">
          <div
            v-for="(week, wIdx) in weeks"
            :key="wIdx"
            class="week-row"
            :class="{ 'hidden-row': viewMode==='week' && wIdx!==selectedWeekIndex, 'above': viewMode==='week' && wIdx===selectedWeekIndex }"
            :ref="el => setWeekRef(wIdx, el)"
          >
            <div
              v-for="(cell, cIdx) in week"
              :key="cIdx"
              class="flex justify-center items-center h-10 cursor-pointer relative"
              @click="cell && onDateClick(cell)"
            >
              <template v-if="cell">
                <div v-if="eventDaysSet.has(cell.key)" class="event-dot"></div>
                <span class="date-chip" :class="{ 'selected-badge': isSelected(cell) }">
                  {{ cell.day }}
                </span>
              </template>
            </div>
          </div>
        </div>
      </section>

      <!-- Details -->
      <section id="details-view" :class="viewMode==='week' ? 'sheet-show' : 'sheet-hidden'" :style="{ marginTop: detailsMarginTop }">
        <!-- 되돌아가기: 인라인 SVG -->
        <button id="btn-back-fab" :class="{ hidden: viewMode!=='week' }" aria-label="월 보기로" title="월 보기로" @click="backToMonth">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"
               fill="currentColor" class="w-5 h-5">
            <path fill-rule="evenodd"
                  d="M7.707 3.293a1 1 0 010 1.414L5.414 7H11a5 5 0 015 5v4a1 1 0 11-2 0v-4a3 3 0 00-3-3H5.414l2.293 2.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z"
                  clip-rule="evenodd" />
          </svg>
        </button>

        <h3 class="text-lg text-gray-800 mb-4 pr-12">{{ selectedDateText }}</h3>

        <div class="space-y-4 pb-20">
          <!-- Diaries -->
          <div>
            <template v-if="currentPayload.diaries.length === 0">
              <div class="text-sm text-gray-500">등록된 기록이 없어요.</div>
            </template>
            <template v-else>
              <div v-for="d in currentPayload.diaries" :key="d.id" class="rounded-xl border bg-gray-50 px-4 py-3 flex gap-3 items-center">
                <div v-if="d.image" class="w-20 h-16 rounded-lg overflow-hidden">
                  <img :src="d.image" alt="" class="w-full h-full object-cover" />
                </div>
                <div v-else class="w-20 h-16 rounded-lg bg-[var(--lav-100)] text-[var(--lav-500)] flex items-center justify-center text-xs">
                  이미지
                </div>
                <div class="flex-1">
                  <div>{{ d.title }}</div>
                  <div class="text-sm text-gray-600 line-clamp-2">{{ d.content || '' }}</div>
                </div>
                <button class="text-xs px-2 py-1 border rounded-lg hover:bg-white">보기</button>
              </div>
            </template>
          </div>

          <!-- Events -->
          <div>
            <h4 class="mb-2">일정</h4>
            <template v-if="currentPayload.events.length === 0">
              <div class="text-sm text-gray-500">등록된 일정이 없어요.</div>
            </template>
            <template v-else>
              <ul class="divide-y rounded-xl border overflow-hidden">
                <li v-for="(ev, i) in currentPayload.events" :key="i" class="px-4 py-3 bg-white flex items-center gap-3">
                  <span class="text-xs px-2 py-1 rounded-full border text-[var(--lav-500)] border-[var(--lav-200)] bg-[var(--lav-50)]">{{ ev.type || '일정' }}</span>
                  <span class="w-16 font-mono text-sm">{{ ev.time || '' }}</span>
                  <span class="flex-1">{{ ev.title }}</span>
                </li>
              </ul>
            </template>
          </div>
        </div>
      </section>

      <!-- 일정 추가 FAB: 인라인 SVG -->
      <button id="add-event-fab" class="hover:opacity-95 active:opacity-90" aria-label="일정 등록" title="일정 등록" @click="openEventModal">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"
             fill="currentColor" class="w-6 h-6">
          <path d="M10 4a1 1 0 011 1v4h4a1 1 0 110 2h-4v4a1 1 0 11-2 0v-4H5a1 1 0 110-2h4V5a1 1 0 011-1z"/>
        </svg>
      </button>
    </main>

    <!-- Modal -->
    <div id="event-modal" :class="{ show: showModal }" aria-hidden="true">
      <div class="backdrop" @click="closeEventModal"></div>
      <div class="sheet p-4">
        <div class="h-1 w-10 bg-gray-300 rounded-full mx-auto mb-3"></div>
        <div class="flex items-center justify-between mb-3">
          <h3 class="text-lg">일정 등록</h3>
          <button class="w-9 h-9 rounded-full border flex items-center justify-center hover:bg-gray-50" aria-label="닫기" @click="closeEventModal">
            <span class="sr-only">닫기</span>✕
          </button>
        </div>

        <form class="space-y-3" @submit.prevent="saveEvent">
          <div>
            <label class="label">날짜</label>
            <input v-model="form.date" type="date" class="input" required />
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="label">시간</label>
              <input v-model="form.time" type="time" class="input" />
            </div>
            <div>
              <label class="label">종류</label>
              <select v-model="form.type" class="input">
                <option value="일정">일정</option>
                <option value="hospital">병원</option>
                <option value="community">복지관</option>
                <option value="family">가족</option>
                <option value="etc">기타</option>
              </select>
            </div>
          </div>
          <div>
            <label class="label">제목</label>
            <input v-model.trim="form.title" type="text" class="input" placeholder="예: 예방접종, 내과 외래" required />
          </div>
          <div>
            <label class="label">메모</label>
            <textarea v-model="form.note" class="input" rows="3" placeholder="메모를 입력하세요(선택)"></textarea>
          </div>

          <div class="pt-2 flex gap-2">
            <button type="button" class="flex-1 h-10 rounded-xl border hover:bg-gray-50" @click="closeEventModal">취소</button>
            <button type="submit" class="flex-1 h-10 rounded-xl bg-[var(--lav-500)] text-white hover:brightness-110">저장</button>
          </div>
        </form>
      </div>
    </div>
    </div>
</template>

<script setup lang="ts">
import type { ComponentPublicInstance } from 'vue'
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import Logo from '@/assets/Harudam_logo.png'

/* ===== 타입 ===== */
type ViewMode = 'month' | 'week'
type Diary = { id: string; title: string; content?: string; image?: string }
type EventItem = { time?: string; title: string; type?: string; note?: string }
type DayPayload = { diaries: Diary[]; events: EventItem[] }
type DataByDate = Record<string, DayPayload>
type Cell = { dateObj: Date; key: string; day: number }

/* ===== 로고/링크 ===== */
const logoSrc = Logo
const homeHref = '/main'

/* ===== 상태 ===== */
const viewMode = ref<ViewMode>('month')
const currentDate = ref<Date>(new Date()); currentDate.value.setDate(1)
const selectedDate = ref<Date | null>(null)
const selectedWeekIndex = ref<number>(-1)

/* ===== 데이터 (예시) ===== */
const dataByDate = reactive<DataByDate>({
  '2024-8-6': {
    diaries: [{ id: 'd1', title: '유치원 소풍', content: '비가 와서 실내놀이로 대체했어요.' }],
    events: [
      { time: '10:30', title: '예방접종(2차)', type: 'hospital' },
      { time: '18:00', title: '영상통화(할머니)', type: 'family' }
    ]
  },
  '2024-8-10': {
    diaries: [{ id: 'd2', title: '수영장', content: '처음으로 자유형 성공!', image: '/assets/diary-pool.jpg' }],
    events: [{ time: '09:00', title: '놀이치료', type: 'community' }]
  }
})

/* ===== 유틸 ===== */
const toKey = (d: Date): string => `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`
const toInputDate = (d: Date): string => {
  const y = d.getFullYear(), m = String(d.getMonth() + 1).padStart(2, '0'), day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/* ===== 달력 계산 ===== */
const year = computed(() => currentDate.value.getFullYear())
const month = computed(() => currentDate.value.getMonth())
const monthLabel = computed(() => `${month.value + 1}월`)

const weeks = computed<Cell[][]>(() => {
  const firstDow = new Date(year.value, month.value, 1).getDay()
  const lastDate = new Date(year.value, month.value + 1, 0).getDate()
  const rows: Cell[][] = []
  let d = 1
  for (let r = 0; r < 6; r++) {
    const row: (Cell | null)[] = []
    for (let c = 0; c < 7; c++) {
      if (r === 0 && c < firstDow) row.push(null)
      else if (d <= lastDate) { const dateObj = new Date(year.value, month.value, d); row.push({ dateObj, key: toKey(dateObj), day: d }); d++ }
      else row.push(null)
    }
    rows.push(row as Cell[])
  }
  return rows
})

/* 기록 있는 날짜 → 점 표시 */
const eventDaysSet = computed<Set<string>>(() => {
  const set = new Set<string>()
  for (const [k, v] of Object.entries(dataByDate) as [string, DayPayload][]) {
    if ((v.diaries?.length ?? 0) > 0 || (v.events?.length ?? 0) > 0) set.add(k)
  }
  return set
})

/* 선택 텍스트/페이로드 */
const selectedDateText = computed(() => {
  if (!selectedDate.value) return ''
  const d = selectedDate.value, dow = d.toLocaleDateString('ko-KR', { weekday: 'long' })
  return `${d.getMonth() + 1}월 ${d.getDate()}일 ${dow}`
})
const currentPayload = computed<DayPayload>(() => {
  if (!selectedDate.value) return { diaries: [], events: [] }
  return dataByDate[toKey(selectedDate.value)] ?? { diaries: [], events: [] }
})

/* ===== 레이아웃/참조 ===== */
const gridRef = ref<HTMLElement | null>(null)
const weekRefs = ref<Array<HTMLElement | null>>([])
const gridStyle = ref<Record<string, string>>({})
const detailsMarginTop = ref('0px')

/* 컴포넌트처럼 $el을 가진 값 타입가드( any 금지 ) */
type WithEl = { $el: HTMLElement }
function isWithEl(val: unknown): val is WithEl {
  return typeof val === 'object' && val !== null && '$el' in val
}

/** 함수 ref로 넘어온 값에서 실제 DOM(HTMLElement)만 뽑아 저장 */
function setWeekRef(index: number, el: Element | ComponentPublicInstance | null) {
  let dom: HTMLElement | null = null
  if (el) {
    if (isWithEl(el)) dom = el.$el              // 컴포넌트 인스턴스
    else if (el instanceof HTMLElement) dom = el // 일반 DOM Element
  }
  weekRefs.value[index] = dom
}

function isSelected(cell: Cell): boolean {
  return !!selectedDate.value && toKey(selectedDate.value) === cell.key
}

async function onDateClick(cell: Cell) {
  selectedDate.value = cell.dateObj
  selectedWeekIndex.value = weeks.value.findIndex(row => row.some(c => c && c.key === cell.key))
  viewMode.value = 'week'
  await nextTick(); adjustWeekPosition()
}

function goPrevMonth() { if (viewMode.value !== 'month') return; currentDate.value = new Date(year.value, month.value - 1, 1) }
function goNextMonth() { if (viewMode.value !== 'month') return; currentDate.value = new Date(year.value, month.value + 1, 1) }
function backToMonth() {
  viewMode.value = 'month'
  gridStyle.value = { '--week-shift': '0px' }
  detailsMarginTop.value = '0px'
  selectedWeekIndex.value = -1
  selectedDate.value = null
}

/* 선택 주를 위로 당기고 상세 시트 위치 보정 */
function adjustWeekPosition() {
  const grid = gridRef.value, row = weekRefs.value[selectedWeekIndex.value]
  if (!grid || !row) return
  const rowTop = row.offsetTop, rowH = row.offsetHeight, gridH = grid.scrollHeight, overlap = 12
  gridStyle.value = { '--week-shift': `${-rowTop}px` }
  const coverFrom = rowTop + rowH - overlap, pullUp = gridH - coverFrom, marginUp = pullUp + rowTop
  detailsMarginTop.value = `-${marginUp}px`
}

/* 리사이즈 대응 */
function handleResize() { if (viewMode.value === 'week') adjustWeekPosition() }
onMounted(() => window.addEventListener('resize', handleResize))
onBeforeUnmount(() => window.removeEventListener('resize', handleResize))

/* ===== 모달(FAB) ===== */
const showModal = ref(false)
const form = reactive<{ date: string; time: string; type: string; title: string; note: string }>({
  date: toInputDate(new Date()), time: '', type: '일정', title: '', note: ''
})
function openEventModal() {
  const base = selectedDate.value ?? new Date()
  form.date = toInputDate(base); form.time = ''; form.type = '일정'; form.title = ''; form.note = ''
  showModal.value = true
}
function closeEventModal() { showModal.value = false }
function saveEvent() {
  if (!form.title.trim()) return
  const [yy, mm, dd] = form.date.split('-').map(Number)
  const eventDate = new Date(yy, mm - 1, dd)
  const key = toKey(eventDate)
  if (!dataByDate[key]) dataByDate[key] = { diaries: [], events: [] }
  dataByDate[key].events.push({ time: form.time || '', title: form.title.trim(), type: form.type || '일정', note: form.note || '' })
  showModal.value = false
}

/* 위치 보정 워치 */
watch([weeks, selectedWeekIndex, viewMode], async () => {
  if (viewMode.value === 'week') { await nextTick(); adjustWeekPosition() }
})
</script>

<style scoped>
/* 이 컴포넌트에서만 적용할 폰트 */
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');
.calendar-root{
  font-family:'Jua','Noto Sans KR',-apple-system,BlinkMacSystemFont,'Apple SD Gothic Neo','Segoe UI',Roboto,'Helvetica Neue',Arial,'Malgun Gothic',sans-serif;
  font-variant-numeric:tabular-nums;
}

/* 라벤더 팔레트 */
:root{ --lav-50:#F5F3FF; --lav-100:#EDE9FE; --lav-200:#DDD6FE; --lav-400:#A78BFA; --lav-500:#8B5CF6; }

/* 디바이스 프레임 느낌 */
.mobile-screen{ width:100%; max-width:430px; height:932px; border-radius:36px; border:10px solid #1a1a1a; overflow:hidden; position:relative; background:#f3f4f6; box-shadow:0 4px 12px rgba(0,0,0,.15); }

/* 종 아이콘 효과 */
.bell-svg{ filter:drop-shadow(0 2px 6px rgba(250,204,21,.35)); transition:transform .15s ease, filter .2s ease, opacity .2s ease; }
.bell-svg:hover{ transform:translateY(-1px); filter:drop-shadow(0 6px 14px rgba(250,204,21,.45)); opacity:.95; }

/* 달력 카드 */
#calendar-body{ background:linear-gradient(180deg, var(--lav-50) 0%, var(--lav-100) 100%); border-radius:1.25rem; border:1px solid var(--lav-200); box-shadow:0 12px 24px rgba(0,0,0,.10); position:relative; z-index:10; padding-bottom:.25rem; }
#dow-head{ position:relative; z-index:30; background:rgba(255,255,255,.55); -webkit-backdrop-filter:blur(2px); backdrop-filter:blur(2px); margin-bottom:.5rem !important; border-top-left-radius:1.25rem; border-top-right-radius:1.25rem; padding-top:.5rem; box-shadow:inset 0 -1px 0 rgba(0,0,0,.04); }

.week-row{ display:grid; grid-template-columns:repeat(7,1fr); gap:.5rem 0; transition:opacity .25s ease; }
.week-row.above{ position:relative; z-index:30; }
.week-row.hidden-row{ visibility:hidden; opacity:0; pointer-events:none; }

#calendar-grid{ overflow:hidden; transition:transform .35s ease; will-change:transform; }
#calendar-grid.shifted{ transform:translateY(var(--week-shift,0px)); }

/* 날짜 칩: 기본 글자색 지정(사라짐 방지) */
.date-chip{ display:flex; align-items:center; justify-content:center; width:2rem; height:2rem; border-radius:9999px; color:#1f2937; } /* text-gray-800 */

/* 선택된 날짜 (배경 확실히 채우기) */
#calendar-grid .selected-badge{
  background-color: var(--lav-500) !important;
  color:#fff !important;
}

.event-dot{ position:absolute; bottom:3px; width:6px; height:6px; border-radius:9999px; background:linear-gradient(180deg, var(--lav-400), var(--lav-500)); box-shadow:0 0 0 2px rgba(255,255,255,.85); }

/* 상세 시트 */
#details-view{ background:#fff; border-radius:1.25rem; border:1px solid #eee; box-shadow:0 12px 24px rgba(0,0,0,.12); position:relative; z-index:20; transition:opacity .25s ease, transform .25s ease, margin .25s ease; padding:16px 18px 22px; }
.sheet-hidden{ opacity:0; visibility:hidden; transform:translateY(6px); }
.sheet-show{ opacity:1; visibility:visible; transform:translateY(0); }
#details-view::before{ content:""; position:absolute; top:-6px; left:16px; right:16px; height:12px; background:rgba(0,0,0,.06); filter:blur(10px); border-radius:9999px; z-index:-1; }
#btn-back-fab{ position:absolute; top:10px; right:10px; width:40px; height:40px; display:flex; align-items:center; justify-content:center; border-radius:9999px; background:#fff; border:1px solid #e5e7eb; box-shadow:0 8px 18px rgba(0,0,0,.12); }
#btn-back-fab.hidden{ display:none; }
#add-event-fab{ position:absolute; right:16px; bottom:16px; width:56px; height:56px; display:flex; align-items:center; justify-content:center; border-radius:9999px; background:var(--lav-500); color:#fff; box-shadow:0 10px 20px rgba(139,92,246,.35); cursor:pointer; }

/* 모달(바텀시트) */
#event-modal{ position:fixed; inset:0; display:none; }
#event-modal.show{ display:block; }
#event-modal .backdrop{ position:absolute; inset:0; background:rgba(0,0,0,.35); }
#event-modal .sheet{ position:absolute; left:0; right:0; bottom:0; background:#fff; border-top-left-radius:20px; border-top-right-radius:20px; box-shadow:0 -10px 30px rgba(0,0,0,.2); transform:translateY(100%); transition:transform .28s ease; }
#event-modal.show .sheet{ transform:translateY(0); }
.input{ width:100%; border:1px solid #e5e7eb; border-radius:12px; padding:10px 12px; }
.label{ font-size:.9rem; color:#374151; margin-bottom:.35rem; display:block; }
header{ position:relative; z-index:40; }
</style>
