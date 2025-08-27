<template>
  <!-- 루트: 프레임 제거 + 가운데 정렬 -->
  <div class="calendar-root mobile-screen flex flex-col mx-auto min-h-screen">
    <!-- ▣ 상단 헤더: 로고 + 알림(데코) -->
    <header class="flex items-center justify-between p-4 bg-gray-100 border-b">
      <a :href="homeHref" class="flex items-center space-x-2 hover:opacity-80 transition">
        <img :src="logoSrc" alt="하루담" class="w-8 h-8 rounded-lg object-contain" />
        <span class="text-lg">하루담</span>
      </a>
      <button class="relative" aria-label="알림">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"
             fill="currentColor" class="w-7 h-7 text-yellow-400 bell-svg">
          <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6z"/>
          <path d="M14 15a4 4 0 01-8 0h8z"/>
        </svg>
      </button>
    </header>

    <!-- 메인 -->
    <main class="flex-grow p-4 bg-white rounded-t-3xl -mt-4 overflow-auto relative">
      <!-- ▣ 달 이동 헤더(월 보기에서만 활성화) -->
      <div class="flex items-center justify-between mb-2">
        <button class="text-gray-600 hover:text-gray-900 w-8 h-8 disabled:opacity-40 disabled:hover:text-gray-600"
                title="이전 달" @click="goPrevMonth" :disabled="viewMode!=='month'">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-6 h-6">
            <path fill-rule="evenodd" d="M12.707 15.707a1 1 0 01-1.414 0l-5-5a1 1 0 010-1.414l5-5a1 1 0 111.414 1.414L8.414 10l4.293 4.293a1 1 0 010 1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
        <h2 class="text-xl">{{ monthLabel }}</h2>
        <button class="text-gray-600 hover:text-gray-900 w-8 h-8 disabled:opacity-40 disabled:hover:text-gray-600"
                title="다음 달" @click="goNextMonth" :disabled="viewMode!=='month'">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-6 h-6">
            <path fill-rule="evenodd" d="M7.293 4.293a1 1 0 011.414 0l5 5a1 1 0 010 1.414l-5 5a1 1 0 01-1.414-1.414L11.586 10 7.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>
      </main>

      <!-- ▣ 월/주 달력 카드 -->
      <section id="calendar-body" class="mb-4">
        <!-- 요일 헤더 -->
        <div id="dow-head" class="grid grid-cols-7 text-center text-sm text-gray-700 px-2">
          <div class="text-red-500">S</div><div>M</div><div>T</div><div>W</div>
          <div>T</div><div>F</div><div class="text-blue-500">S</div>
        </div>

        <!-- 날짜 그리드 -->
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
                <span class="date-chip"
                      :class="{ 'selected-badge': isSelected(cell), 'today-ring': isToday(cell) && !isSelected(cell) }">
                  {{ cell.day }}
                </span>
              </template>
            </div>
          </div>
        </div>

        <!-- ✅ 카드 우하단 모서리 FAB (월 보기에서만) -->
        <button id="card-add-event-fab" v-show="viewMode==='month'" aria-label="일정 등록" title="일정 등록" @click="openEventModal">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-6 h-6">
            <path d="M10 4a1 1 0 011 1v4h4a1 1 0 110 2h-4v4a1 1 0 11-2 0v-4H5a1 1 0 110-2h4V5a1 1 0 011-1z"/>
          </svg>
        </button>
      </section>

      <!-- ▣ 상세 시트(주간 보기) -->
      <section id="details-view" :class="viewMode==='week' ? 'sheet-show' : 'sheet-hidden'" :style="{ marginTop: detailsMarginTop }">
        <!-- 월 보기로 -->
        <button id="btn-back-fab" :class="{ hidden: viewMode!=='week' }" aria-label="월 보기로" title="월 보기로" @click="backToMonth">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5">
            <path fill-rule="evenodd" d="M7.707 3.293a1 1 0 010 1.414L5.414 7H11a5 5 0 015 5v4a1 1 0 11-2 0v-4a3 3 0 00-3-3H5.414l2.293 2.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z"/>
          </svg>
        </button>

        <h3 class="text-lg text-gray-800 mb-4 pr-12">{{ selectedDateText }}</h3>

        <div class="space-y-4 pb-20">
          <!-- (1) 다이어리 -->
          <div>
            <template v-if="currentPayload.diaries.length === 0">
              <div class="text-sm text-gray-500">등록된 기록이 없어요.</div>
            </template>
            <template v-else>
              <div v-for="d in currentPayload.diaries" :key="d.id" class="rounded-xl border bg-gray-50 px-4 py-3 flex gap-3 items-center">
                <div v-if="d.image" class="w-20 h-16 rounded-lg overflow-hidden">
                  <img :src="d.image" alt="" class="w-full h-full object-cover" />
                </div>
                <div v-else class="w-20 h-16 rounded-lg bg-[var(--lav-100)] text-[var(--lav-500)] flex items-center justify-center text-xs">이미지</div>
                <div class="flex-1">
                  <div>{{ d.title }}</div>
                  <div class="text-sm text-gray-600 line-clamp-2">{{ d.content || '' }}</div>
                </div>
                <button class="text-xs px-2 py-1 border rounded-lg hover:bg-white">보기</button>
              </div>
            </template>
          </div>

          <!-- (2) 일정 -->
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

      <!-- ▣ 일정 등록 모달(바텀시트): 항상 최상단 -->
      <div id="event-modal" :class="{ show: showModal }" aria-hidden="true" role="dialog">
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
/** ✅ ESLint ‘multi-word’ 회피용(선택) */
defineOptions({ name: 'CalendarChildView' })

import type { ComponentPublicInstance } from 'vue'
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import Logo from '@/assets/Harudam_logo.png'

/* ──────────────────────────────────────────────────────────────
   ※ 지금은 백엔드 연동 OFF 상태입니다.
   나중에 서버 붙일 때는 “🔌 백엔드 연동 지점” 주석을 검색해서
   해당 부분만 해제하면 바로 동작합니다.
──────────────────────────────────────────────────────────────*/

/* ===== 타입 ===== */
type ViewMode = 'month' | 'week'
type Diary = { id: string; title: string; content?: string; image?: string }
type EventItem = { id?: string; time?: string; title: string; type?: string; note?: string }
type DayPayload = { diaries: Diary[]; events: EventItem[] }
type DataByDate = Record<string, DayPayload>
type Cell = { dateObj: Date; key: string; day: number }

/* ===== 상단 로고/링크 ===== */
const logoSrc = Logo
const homeHref = '/main'

/* ===== 상태 ===== */
const viewMode = ref<ViewMode>('month')
const currentDate = ref<Date>(new Date()); currentDate.value.setDate(1)
const selectedDate = ref<Date | null>(null)
const selectedWeekIndex = ref<number>(-1)

/* ===== 데이터: 로컬 저장(프론트만) ===== */
const dataByDate = reactive<DataByDate>({})
const LS_KEY = 'harudam.calendar.local.v1'

/* ===== 유틸 ===== */
const pad2 = (n: number) => String(n).padStart(2, '0')
const toKey = (d: Date): string => `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`
const toInputDate = (d: Date): string => `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`

/* ===== 달력 계산 ===== */
const year = computed(() => currentDate.value.getFullYear())
const month = computed(() => currentDate.value.getMonth())
const monthLabel = computed(() => `${month.value + 1}월`)

const weeks = computed<(Cell | null)[][]>(() => {
  const firstDow = new Date(year.value, month.value, 1).getDay()
  const lastDate = new Date(year.value, month.value + 1, 0).getDate()
  const rows: (Cell | null)[][] = []
  let d = 1
  for (let r = 0; r < 6; r++) {
    const row: (Cell | null)[] = []
    for (let c = 0; c < 7; c++) {
      if (r === 0 && c < firstDow) row.push(null)
      else if (d <= lastDate) { const dateObj = new Date(year.value, month.value, d); row.push({ dateObj, key: toKey(dateObj), day: d }); d++ }
      else row.push(null)
    }
    rows.push(row)
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

/* ===== 레이아웃 참조/보정 ===== */
const gridRef = ref<HTMLElement | null>(null)
const weekRefs = ref<Array<HTMLElement | null>>([])
const gridStyle = ref<Record<string, string>>({})
const detailsMarginTop = ref('0px')

/* HTMLElement 안전 추출 (any 없이) */
type MaybeWithEl = { $el: unknown }
function isHTMLElement(v: unknown): v is HTMLElement { return v instanceof HTMLElement }
function has$el(v: unknown): v is MaybeWithEl {
  return typeof v === 'object' && v !== null && '$el' in (v as Record<string, unknown>)
}
function setWeekRef(index: number, el: Element | ComponentPublicInstance | null) {
  let dom: HTMLElement | null = null
  if (isHTMLElement(el)) {
    dom = el
  } else if (has$el(el) && isHTMLElement((el as MaybeWithEl).$el)) {
    dom = (el as MaybeWithEl).$el as HTMLElement
  }
  weekRefs.value[index] = dom
}

/* 헬퍼 */
function isSelected(cell: Cell) { return !!selectedDate.value && toKey(selectedDate.value) === cell.key }
function isToday(cell?: Cell | null) {
  if (!cell) return false
  const t = new Date()
  return cell.dateObj.getFullYear() === t.getFullYear()
      && cell.dateObj.getMonth() === t.getMonth()
      && cell.dateObj.getDate() === t.getDate()
}

/* 주간 시트 위치 보정 */
function adjustWeekPosition() {
  const grid = gridRef.value, row = weekRefs.value[selectedWeekIndex.value]
  if (!grid || !row) return
  const rowTop = row.offsetTop, rowH = row.offsetHeight, gridH = grid.scrollHeight, overlap = 12
  gridStyle.value = { '--week-shift': `${-rowTop}px` }
  const coverFrom = rowTop + rowH - overlap, pullUp = gridH - coverFrom, marginUp = pullUp + rowTop
  detailsMarginTop.value = `-${marginUp}px`
}
function handleResize() { if (viewMode.value === 'week') adjustWeekPosition() }

/* 화면 전환 */
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

/* 스크롤 잠금/해제 */
function lockBodyScroll() {
  if (typeof document !== 'undefined' && document.body) document.body.style.overflow = 'hidden'
}
function unlockBodyScroll() {
  if (typeof document !== 'undefined' && document.body) document.body.style.overflow = ''
}

/* ===== 모달(FAB) ===== */
const showModal = ref(false)
const form = reactive<{ date: string; time: string; type: string; title: string; note: string }>({
  date: toInputDate(new Date()), time: '', type: '일정', title: '', note: ''
})
function openEventModal() {
  const base = selectedDate.value ?? new Date()
  form.date = toInputDate(base); form.time = ''; form.type = '일정'; form.title = ''; form.note = ''
  showModal.value = true
  lockBodyScroll()
}
function closeEventModal() { showModal.value = false; unlockBodyScroll() }

/* ===== 저장/로드: 로컬스토리지 버전 ===== */
function persist() { try { localStorage.setItem(LS_KEY, JSON.stringify(dataByDate)) } catch {} }
function restore() {
  try {
    const raw = localStorage.getItem(LS_KEY)
    if (raw) Object.assign(dataByDate, JSON.parse(raw) as DataByDate)
  } catch {}
}

/* 저장 (로컬 전용) */
function saveEvent() {
  if (!form.title.trim()) return
  const [yy, mm, dd] = form.date.split('-').map(Number)
  const key = `${yy}-${mm}-${dd}`
  if (!dataByDate[key]) dataByDate[key] = { diaries: [], events: [] }
  dataByDate[key].events.push({
    id: `local-${Date.now()}`,
    time: form.time || '',
    title: form.title.trim(),
    type: form.type || '일정',
    note: form.note || ''
  })
  persist()
  closeEventModal()

  /* 🔌 백엔드 연동 지점: insert 후 실시간 반영
     await calendarService.create({...})
  */
}

/* 라이프사이클 */
onMounted(() => {
  window.addEventListener('resize', handleResize)
  restore()
  /* 🔌 백엔드 연동 지점: load + subscribe */
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  /* 🔌 백엔드 연동 지점: unsubscribe?.() */
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');

.calendar-root{
  font-family:'Jua','Noto Sans KR',-apple-system,BlinkMacSystemFont,'Apple SD Gothic Neo','Segoe UI',Roboto,'Helvetica Neue',Arial,'Malgun Gothic',sans-serif;
  font-variant-numeric:tabular-nums;
  --lav-50:#F5F3FF; --lav-100:#EDE9FE; --lav-200:#DDD6FE; --lav-400:#A78BFA; --lav-500:#8B5CF6;
}

/* 프레임 제거 + 가운데 정렬 */
.mobile-screen{
  width:100%; max-width:480px; min-height:100dvh; margin:0 auto;
  border:none; border-radius:0; background:transparent; box-shadow:none;
  overflow:visible; position:relative;
}

/* 종 아이콘 효과 */
.bell-svg{ filter:drop-shadow(0 2px 6px rgba(250,204,21,.35)); transition:transform .15s ease, filter .2s ease, opacity .2s ease; }
.bell-svg:hover{ transform:translateY(-1px); filter:drop-shadow(0 6px 14px rgba(250,204,21,.45)); opacity:.95; }

/* 달력 카드 */
#calendar-body{
  background:linear-gradient(180deg, var(--lav-50) 0%, var(--lav-100) 100%);
  border-radius:1.25rem; border:1px solid var(--lav-200);
  box-shadow:0 12px 24px rgba(0,0,0,.10);
  position:relative; /* ↞ FAB 앵커링용 */
  z-index:10; padding-bottom:.25rem;
}
#dow-head{
  position:relative; z-index:30; background:rgba(255,255,255,.55);
  -webkit-backdrop-filter:blur(2px); backdrop-filter:blur(2px);
  margin-bottom:.5rem !important; border-top-left-radius:1.25rem; border-top-right-radius:1.25rem;
  padding-top:.5rem; box-shadow:inset 0 -1px 0 rgba(0,0,0,.04);
}
.week-row{ display:grid; grid-template-columns:repeat(7,1fr); gap:.5rem 0; transition:opacity .25s ease; }
.week-row.above{ position:relative; z-index:30; }
.week-row.hidden-row{ visibility:hidden; opacity:0; pointer-events:none; }
#calendar-grid{ overflow:hidden; transition:transform .35s ease; will-change:transform; }
#calendar-grid.shifted{ transform:translateY(var(--week-shift,0px)); }

/* 날짜 칩 */
.date-chip{ display:flex; align-items:center; justify-content:center; width:2rem; height:2rem; border-radius:9999px; color:#1f2937; }

/* 상세 시트 */
#details-view{
  background:#fff; border-radius:1.25rem; border:1px solid #eee;
  box-shadow:0 12px 24px rgba(0,0,0,.12); position:relative; z-index:20;
  transition:opacity .25s ease, transform .25s ease, margin .25s ease; padding:16px 18px 22px;
}
.sheet-hidden{ opacity:0; visibility:hidden; transform:translateY(6px); }
.sheet-show{ opacity:1; visibility:visible; transform:translateY(0); }
#details-view::before{ content:""; position:absolute; top:-6px; left:16px; right:16px; height:12px; background:rgba(0,0,0,.06); filter:blur(10px); border-radius:9999px; z-index:-1; }
#btn-back-fab{ position:absolute; top:10px; right:10px; width:40px; height:40px; display:flex; align-items:center; justify-content:center; border-radius:9999px; background:#fff; border:1px solid #e5e7eb; box-shadow:0 8px 18px rgba(0,0,0,.12); }
#btn-back-fab.hidden{ display:none; }

/* ✅ 카드 모서리 FAB (카드 밖으로 살짝 튀어나오게) */
#card-add-event-fab{
  position:absolute;
  right:-12px;              /* 안쪽으로 붙이려면 12px */
  bottom:-12px;             /* 안쪽으로 붙이려면 12px */
  width:52px; height:52px;
  display:flex; align-items:center; justify-content:center;
  border-radius:9999px; background:var(--lav-500); color:#fff;
  box-shadow:0 12px 24px rgba(139,92,246,.35);
  cursor:pointer; z-index:40;           /* 카드 내용 위 클릭 보장 */
  transition: transform .1s ease, filter .2s ease;
}
#card-add-event-fab:active{ transform: scale(.98); }

/* 모달(바텀시트): 항상 맨 앞 */
#event-modal{ position:fixed; inset:0; display:none; z-index:5000; }
#event-modal.show{ display:block; }
#event-modal .backdrop{ position:absolute; inset:0; background:rgba(0,0,0,.45); }
#event-modal .sheet{
  position:absolute; left:0; right:0; bottom:0; background:#fff;
  border-top-left-radius:20px; border-top-right-radius:20px;
  box-shadow:0 -10px 30px rgba(0,0,0,.2); transform:translateY(100%);
  transition:transform .28s ease;
}
#event-modal.show .sheet{ transform:translateY(0); }

/* 인풋 */
.input{ width:100%; border:1px solid #e5e7eb; border-radius:12px; padding:10px 12px; }
.label{ font-size:.9rem; color:#374151; margin-bottom:.35rem; display:block; }

/* 날짜 숫자/선택/오늘/점 */
#calendar-grid .date-chip{ color:#111827 !important; background:transparent !important; position:relative; z-index:5 !important; font-weight:500; }
#calendar-grid .selected-badge{ background-color: var(--lav-500) !important; color:#fff !important; box-shadow:0 2px 6px rgba(139,92,246,.35); }
.today-ring{ outline: 2px solid rgba(16,185,129,.55); outline-offset: 2px; border-radius: 9999px; }
#calendar-grid .event-dot{ position:absolute; bottom:3px; width:6px; height:6px; border-radius:9999px; background:linear-gradient(180deg, var(--lav-400), var(--lav-500)); box-shadow:0 0 0 2px rgba(255,255,255,.85); z-index:0 !important; }
#calendar-grid .week-row > div{ position:relative; z-index:1; }
</style>
