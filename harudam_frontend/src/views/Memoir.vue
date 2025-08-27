<!-- src/views/MemoirMain.vue -->
<template>
  <div class="memoir-page">
    <!-- 메인 -->
    <main class="content">
      <h1 class="title">아버지의 일생</h1>
      <p class="subtitle">대화를 바탕으로 자서전을 만들어 드려요.</p>

      <!-- 중앙 스테이지: 버튼과 겹치지 않게 ‘아래’로 내림 -->
      <section class="stage">
        <!-- 생성 완료 시 작은 책 카드 + 캡션 -->
        <figure v-if="book" class="card book-card" @click="openViewer">
          <div class="cover small">
            <img class="cover-img" :src="book.coverSrc" alt="자서전 표지" />
            <!-- 표지 하단 얇은 스트립(책 안에 제목 표시) -->
            <div class="cover-strip">
              <span class="strip-title">{{ ownerName }}의 자서전</span>
            </div>
          </div>
          <!-- 책 아래 캡션(가독성 보강) -->
          <figcaption class="book-caption">{{ ownerName }}의 자서전</figcaption>
        </figure>
      </section>

      <div class="bottom-spacer" aria-hidden="true"></div>
    </main>

    <!-- 하단 고정: 생성 버튼 + 진행 토스트 -->
    <div class="fab-wrap">
      <div v-if="isGenerating" class="toast" role="status" aria-live="polite">
        <div class="toast-title">자서전 생성 중입니다</div>
        <div class="progress"><div class="bar" :style="{ width: progress + '%' }"></div></div>
        <div class="pct">{{ progress.toFixed(0) }}%</div>
      </div>

      <button class="btn" @click="onCreateClicked">
        <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 19.5V6.5A2.5 2.5 0 016.5 4H20v15H6.5A2.5 2.5 0 014 19.5z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M20 4v15a2 2 0 01-2 2H6.5a2.5 2.5 0 01-2.5-2.5" />
        </svg>
        자서전 생성
      </button>
    </div>

    <!-- ✅ 전자책 뷰어(풀스크린) -->
    <div v-if="viewerOpen" class="viewer" role="dialog" aria-modal="true">
      <!-- 상단바 -->
      <div class="viewer-topbar">
        <button class="close" @click="closeViewer" aria-label="닫기">닫기</button>
        <div class="viewer-title">{{ ownerName }}의 자서전</div>
        <div class="spacer"></div>
      </div>

      <!-- 가로 스와이프 -->
      <div class="pages-wrap" ref="pagesWrap" @scroll.passive="onScroll">
        <!-- 0: 표지(80/20) -->
        <section class="vpage">
          <div class="page-inner cover-first">
            <div class="cover-first__img-wrap">
              <img :src="book?.coverSrc" alt="표지" class="cover-first__img" />
            </div>
            <div class="cover-first__title">
              <h2 class="p-title big">{{ ownerName }}의 자서전</h2>
            </div>
          </div>
        </section>

        <!-- 본문 -->
        <section v-for="(page, i) in pages" :key="i" class="vpage">
          <div class="page-inner">
            <h3 class="p-title">{{ page.title }}</h3>
            <p class="p-text">{{ page.text }}</p>
          </div>
        </section>

        <!-- 좌/우 터치존 -->
        <button class="zone left" @click="prevPage" aria-label="이전 페이지"></button>
        <button class="zone right" @click="nextPage" aria-label="다음 페이지"></button>
      </div>

      <!-- 하단 컨트롤 -->
      <div class="viewer-bottom">
        <div class="indicator">{{ currentPage + 1 }} / {{ totalPages }}</div>
        <div class="controls">
          <button class="ctrl" @click="toggleSpeak" :aria-pressed="isSpeaking ? 'true' : 'false'">
            {{ isSpeaking ? '읽기 중지' : '낭독' }}
          </button>
          <button class="ctrl" @click="exportPDF">PDF 저장/공유</button>
        </div>
      </div>
    </div>

    <!-- 재생성 1단계 -->
    <dialog ref="confirm1" class="confirm-dialog">
      <p class="confirm-text">자서전을 다시 생성하시겠습니까?</p>
      <div class="actions">
        <button class="btn-red" @click="closeConfirm(1)">아니오</button>
        <button class="btn-purple" @click="confirmSecond">예</button>
      </div>
    </dialog>

    <!-- 재생성 2단계(삭제 경고) -->
    <dialog ref="confirm2" class="confirm-dialog">
      <p class="confirm-text">기존에 생성되었던 자서전은 삭제됩니다.</p>
      <div class="actions">
        <button class="btn-red" @click="closeConfirm(2)">아니오</button>
        <button class="btn-purple" @click="startGeneration">예</button>
      </div>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import jsPDF from 'jspdf'
import { addKoreanFont } from '@/jspdf-korean-font.js'   // ✔ 폰트 등록 헬퍼
import defaultCover from '@/assets/diary/2025-08-25.jpg'

type Book = { coverSrc: string }
const ownerName = 'OOO'
const book = ref<Book | null>(null)

/** 임시 페이지들(추후 생성형 AI로 대체) */
const pages = ref([
  { title: '손자와 함께한 강가에서의 오후', text: '손주와 낚시하며 평화로운 하루를 보냈습니다.' },
  { title: '작은 기쁨들', text: '따뜻한 차 한 잔, 산책, 해질녘 노을이 가장 큰 위로가 되었습니다.' },
  { title: '오늘의 마음', text: '행복, 평온함' },
])

/** 저장 키 & 진행 보존 */
const STORAGE_BOOK = 'autobio.book'
const STORAGE_GEN  = 'autobio.gen'   // { startedAt:number, duration:number }

/** 생성 진행 */
const GEN_DURATION = 5000
const isGenerating = ref(false)
const progress = ref(0)

function startGeneration() {
  confirm2.value?.close()
  book.value = null
  localStorage.removeItem(STORAGE_BOOK)

  localStorage.setItem(STORAGE_GEN, JSON.stringify({ startedAt: Date.now(), duration: GEN_DURATION }))
  isGenerating.value = true
  tick()
}
function tick() {
  const raw = localStorage.getItem(STORAGE_GEN)
  if (!raw) return
  const { startedAt, duration } = JSON.parse(raw) as { startedAt:number; duration:number }
  const pct = Math.min(1, (Date.now() - startedAt) / duration)
  progress.value = pct * 100
  if (pct >= 1) {
    localStorage.removeItem(STORAGE_GEN)
    isGenerating.value = false
    book.value = { coverSrc: defaultCover }
    localStorage.setItem(STORAGE_BOOK, JSON.stringify(book.value))
  } else {
    requestAnimationFrame(tick)
  }
}
document.addEventListener('visibilitychange', () => {
  if (document.visibilityState === 'visible') tick()
})

/** 생성 버튼 */
const confirm1 = ref<HTMLDialogElement | null>(null)
const confirm2 = ref<HTMLDialogElement | null>(null)
function onCreateClicked() {
  if (book.value) confirm1.value?.showModal()
  else startGeneration()
}
function confirmSecond() { confirm1.value?.close(); confirm2.value?.showModal() }
function closeConfirm(which: 1 | 2) { (which === 1 ? confirm1 : confirm2).value?.close() }

/** 전자책 뷰어 */
const viewerOpen   = ref(false)
const pagesWrap    = ref<HTMLDivElement | null>(null)
const currentPage  = ref(0)   // 0 = 표지
const totalPages   = computed(() => pages.value.length + 1)

function openViewer() {
  if (!book.value) return
  viewerOpen.value = true
  nextTick(() => {
    currentPage.value = 0
    pagesWrap.value?.scrollTo({ left: 0, behavior: 'auto' })
  })
}
function closeViewer() {
  viewerOpen.value = false
  stopSpeak()
}
function onScroll() {
  if (!pagesWrap.value) return
  const { scrollLeft, clientWidth } = pagesWrap.value
  currentPage.value = Math.round(scrollLeft / clientWidth)
}
function nextPage() {
  if (!pagesWrap.value) return
  const { clientWidth } = pagesWrap.value
  currentPage.value = Math.min(totalPages.value - 1, currentPage.value + 1)
  pagesWrap.value.scrollTo({ left: clientWidth * currentPage.value, behavior: 'smooth' })
}
function prevPage() {
  if (!pagesWrap.value) return
  const { clientWidth } = pagesWrap.value
  currentPage.value = Math.max(0, currentPage.value - 1)
  pagesWrap.value.scrollTo({ left: clientWidth * currentPage.value, behavior: 'smooth' })
}

/** 낭독(TTS) — 현재 페이지부터 */
const isSpeaking = ref(false)
let utter: SpeechSynthesisUtterance | null = null
function toggleSpeak() {
  if (isSpeaking.value || speechSynthesis.speaking) { stopSpeak(); return }
  const sliceFrom = Math.max(0, currentPage.value - 1) // 표지(0) 다음부터
  const text = (sliceFrom < 0 ? pages.value : pages.value.slice(sliceFrom))
    .map(p => `${p.title}\n${p.text}`).join('\n\n')
  utter = new SpeechSynthesisUtterance(text)
  utter.lang = 'ko-KR'; utter.rate = 1.0
  utter.onstart = () => (isSpeaking.value = true)
  utter.onend   = () => (isSpeaking.value = false)
  utter.onerror = () => (isSpeaking.value = false)
  speechSynthesis.speak(utter)
}
function stopSpeak() {
  try { speechSynthesis.cancel() } catch {}
  isSpeaking.value = false
}

/** PDF 저장/공유 */
async function exportPDF() {
  if (!book.value) return
  const doc = new jsPDF({ unit: 'pt', format: 'a4' })
  addKoreanFont(doc)             // ✔ 한글 폰트 등록
  doc.setFont('noto', 'normal')

  const pageW = doc.internal.pageSize.getWidth()
  const pageH = doc.internal.pageSize.getHeight()
  const margin = 48
  let y = margin

  // 제목
  doc.setFontSize(20)
  doc.text(`${ownerName}의 자서전`, margin, y); y += 28

  // 표지 이미지
  try {
    const dataUrl = await imageToDataURL(book.value.coverSrc)
    const imgW = pageW - margin * 2
    const imgH = (imgW * 3) / 4
    doc.addImage(dataUrl, 'JPEG', margin, y, imgW, imgH, undefined, 'FAST')
    y += imgH + 16
  } catch {}

  // 본문
  const lineW = pageW - margin * 2
  for (const p of pages.value) {
    const t1 = doc.splitTextToSize(p.title, lineW)
    const t2 = doc.splitTextToSize(p.text,  lineW)

    doc.setFontSize(14)
    if (y + t1.length * 18 > pageH - margin) { doc.addPage(); y = margin; doc.setFont('noto','normal') }
    doc.text(t1, margin, y); y += t1.length * 18 + 6

    doc.setFontSize(12)
    const need = t2.length * 16
    if (y + need > pageH - margin) {
      let i = 0
      while (i < t2.length) {
        const rest = pageH - margin - y
        const fit = Math.max(1, Math.floor(rest / 16))
        const chunk = t2.slice(i, i + fit)
        doc.text(chunk, margin, y)
        i += chunk.length
        if (i < t2.length) { doc.addPage(); y = margin; doc.setFont('noto','normal'); doc.setFontSize(12) }
        else { y += chunk.length * 16 + 12 }
      }
    } else {
      doc.text(t2, margin, y); y += need + 12
    }
  }

  doc.save('autobiography.pdf')
}

/** 이미지 → dataURL */
function imageToDataURL(src: string): Promise<string> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.crossOrigin = 'anonymous'
    img.onload = () => {
      const c = document.createElement('canvas')
      const ctx = c.getContext('2d')!
      c.width = img.naturalWidth; c.height = img.naturalHeight
      ctx.drawImage(img, 0, 0)
      resolve(c.toDataURL('image/jpeg', 0.92))
    }
    img.onerror = reject
    img.src = src
  })
}

/** 초기화 */
onMounted(() => {
  try {
    const raw = localStorage.getItem(STORAGE_BOOK)
    if (raw) book.value = JSON.parse(raw) as Book
  } catch {}
  tick() // 생성 중이면 이어서
})
</script>

<style scoped>
/* ===== 레이아웃 / 타이포 ===== */
.memoir-page{ background:#F8FAFC; min-height:100vh; }
.content{
  max-width:720px; margin:0 auto; padding:20px;
  /* 상단은 조금 여유, 하단은 푸터+FAB 공간을 고려해 넉넉히 */
  padding-top: 28px;
  padding-bottom: calc(24px + 56px + env(safe-area-inset-bottom));
}
.title{ font-size:24px; font-weight:800; margin:0 0 6px; text-align:center; }
.subtitle{ color:#666; text-align:center; margin:0 0 14px; }

/* 중앙 스테이지: 버튼과 겹치지 않게 ‘아래’ 배치 */
.stage{
  /* 화면 가운데보다 약간 아래쪽으로 배치 */
  min-height: 52vh;
  display: grid; place-items: center;
  margin-top: 8px;
}

/* 카드(작은 책) + 캡션 */
.card{
  background:#fff; border:1px solid #e5e7eb; border-radius:14px;
  padding:14px 14px 12px; width:min(78vw, 240px);
  box-shadow:0 6px 20px rgba(0,0,0,.08);
  text-align:center;
}
.cover.small{ aspect-ratio:3/4; border-radius:12px; overflow:hidden; position:relative; }
.cover-img{ width:100%; height:100%; object-fit:cover; display:block; }

/* 표지 하단 얇은 스트립(책 안쪽) */
.cover-strip{
  position:absolute; left:0; right:0; bottom:0;
  height: 36px;
  background: linear-gradient(to top, rgba(255,255,255,.92), rgba(255,255,255,.55), transparent);
  display:flex; align-items:flex-end; justify-content:center;
  padding-bottom:6px;
}
.strip-title{ font-weight:800; font-size:12px; color:#111; text-shadow:0 1px 0 rgba(255,255,255,.9); }

.book-caption{ margin-top:10px; font-weight:800; color:#111; }

/* 버튼/토스트와 겹치지 않도록 */
.bottom-spacer{ height:120px; }

/* 하단 고정(푸터 위) */
.fab-wrap{
  position:fixed; left:0; right:0;
  bottom: calc(8px + env(safe-area-inset-bottom) + 84px);
  display:flex; flex-direction:column; align-items:center; gap:10px;
  z-index:3000; pointer-events:none;
}
.fab-wrap .btn, .fab-wrap .toast{ pointer-events:auto; }

.btn{
  background:#fff; border:1px solid #e5e7eb; border-radius:12px;
  padding:12px 16px; min-width:220px;
  box-shadow:0 2px 6px rgba(0,0,0,.08);
  display:flex; align-items:center; justify-content:center; gap:8px;
  font-weight:700; cursor:pointer; transition:transform .15s ease;
}
.btn:active{ transform:scale(.97); background:#f9fafb; }
.btn .icon{ width:22px; height:22px; stroke:#4c1d95; }

.toast{
  background:#fff; border:1px solid #e5e7eb; border-radius:12px;
  box-shadow:0 8px 20px rgba(0,0,0,.12);
  padding:10px 14px; width:min(92vw, 300px);
  display:grid; gap:8px; justify-items:center;
}
.toast-title{ font-weight:800; font-size:14px; }
.progress{ width:100%; height:8px; background:#ececec; border-radius:999px; overflow:hidden; }
.progress .bar{ height:100%; background:#111; }
.pct{ font-size:12px; color:#6b7280; }

/* ===== 전자책 뷰어(풀스크린) ===== */
.viewer{
  position:fixed; inset:0; z-index:4000;
  background:#0b0c10; color:#111; display:flex; flex-direction:column;
}
.viewer-topbar{
  height:48px; display:grid; grid-template-columns:auto 1fr auto; align-items:center;
  padding:0 12px; color:#e5e7eb; background:linear-gradient(to bottom, rgba(255,255,255,.08), transparent);
}
.viewer-topbar .close{
  background:transparent; color:#e5e7eb; border:1px solid rgba(255,255,255,.2);
  padding:6px 10px; border-radius:8px; font-weight:700;
}
.viewer-topbar .viewer-title{ text-align:center; font-weight:800; letter-spacing:-.01em; }
.viewer-topbar .spacer{ width:48px; }

.pages-wrap{
  position:relative; flex:1; overflow-x:auto; overflow-y:hidden;
  display:flex; scroll-snap-type:x mandatory; scroll-behavior:smooth;
}
.vpage{
  flex: 0 0 100%;
  scroll-snap-align: start;
  display:flex; align-items:center; justify-content:center;
  padding: 24px 14px;
}
.page-inner{
  width:min(88vw, 640px);
  height:min(72vh, 560px);
  background:#fff; border-radius:14px; box-shadow:0 18px 40px rgba(0,0,0,.35);
  padding:16px 18px; overflow:auto;
}

/* 80/20 첫 페이지 레이아웃 */
.cover-first{
  display:grid; grid-template-rows: 4fr 1fr; gap:10px; align-items:center; justify-items:center;
}
.cover-first__img-wrap{
  width:100%; height:100%; display:grid; place-items:center;
}
.cover-first__img{
  width:min(70vw, 440px); aspect-ratio:3/4; object-fit:cover;
  border-radius:12px; box-shadow:0 10px 30px rgba(0,0,0,.18);
}
.cover-first__title{ display:grid; place-items:center; }
.p-title{ font-size:18px; font-weight:800; margin:4px 0 10px; color:#111827; text-align:center; }
.p-title.big{ font-size:22px; }
.p-text{ font-size:15px; line-height:1.7; color:#1f2937; }

/* 터치존 */
.zone{ position:absolute; top:0; bottom:0; width:20%; background:transparent; border:0; }
.zone.left{ left:0; } .zone.right{ right:0; }

/* 뷰어 하단 컨트롤 */
.viewer-bottom{
  height:64px; display:grid; grid-template-columns:1fr auto; align-items:center;
  padding: 0 12px 12px; color:#e5e7eb;
}
.indicator{ font-weight:800; }
.controls{ display:flex; gap:8px; }
.ctrl{
  background:#ede9fe; color:#6d28d9; border:0; border-radius:10px; padding:8px 12px; font-weight:800;
}
.ctrl:active{ background:#6d28d9; color:#fff; }

/* ===== 중앙 모달(확인창) ===== */
.confirm-dialog{
  border:0; border-radius:14px; padding:18px;
  max-width: 360px; width: 86vw; text-align:center;
  position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%);
}
.confirm-dialog::backdrop{ background: rgba(0,0,0,.45); }
.confirm-text{ font-weight:800; }
.actions{ display:flex; gap:10px; margin-top:12px; }
.btn-red{
  flex:1; background:#fee2e2; color:#b91c1c; border:none; border-radius:10px; padding:10px; font-weight:800;
}
.btn-purple{
  flex:1; background:#ede9fe; color:#6d28d9; border:none; border-radius:10px; padding:10px; font-weight:800;
}
.btn-purple:active{ background:#6d28d9; color:#fff; }
</style>
