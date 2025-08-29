<!-- src/views/MemoirMain.vue -->
<template>
  <div class="memoir-page">
    <Header />
    <div class="header-gap"></div>

    <!-- 메인 -->
    <main class="content">
      <h1 class="title">아버지의 일생</h1>
      <p class="subtitle">대화를 바탕으로 자서전을 만들어 드려요.</p>

      <section class="stage">
        <!-- 생성 완료 -->
        <div v-if="book" class="result-panel">
          <div class="ribbon" aria-hidden="true"></div>
          <div class="result-inner">
            <div class="cover-wrap" @click="openViewer">
              <div class="cover-ghost"></div>
              <img class="cover-img" :src="book.coverSrc" alt="자서전 표지" />
              <div class="cover-strip">
                <span class="strip-title">{{ ownerName }}의 자서전</span>
              </div>
            </div>

            <div class="result-info">
              <h3 class="result-title">{{ ownerName }}의 자서전</h3>
              <p class="result-desc">대화 기록을 정리해 한 권의 이야기로 담았어요.</p>

              <div class="result-actions">
                <button class="btn-ghost" @click="shareBook">PDF 저장/공유</button>
                <button class="btn-primary" @click="openViewer">열람하기</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 생성 전 -->
        <figure v-else class="card book-card">
          <div class="cover small">
            <img class="cover-img" :src="defaultCover" alt="자서전 표지" />
            <div class="cover-strip">
              <span class="strip-title">{{ ownerName }}의 자서전</span>
            </div>
          </div>
          <figcaption class="book-caption">생성하면 이렇게 보여드릴게요</figcaption>
        </figure>
      </section>

      <div class="bottom-spacer" aria-hidden="true"></div>
    </main>

    <!-- 생성 버튼 & 진행 상태 -->
    <div class="fab-wrap">
      <div v-if="isGenerating" class="toast" role="status" aria-live="polite">
        <div class="toast-title">자서전 생성 중입니다</div>
        <div class="progress">
          <div class="bar" :style="{ width: progress + '%' }"></div>
        </div>
        <div class="pct">{{ progress.toFixed(0) }}%</div>
      </div>

      <button class="fab-btn" @click="onCreateClicked">
        <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 19.5V6.5A2.5 2.5 0 016.5 4H20v15H6.5A2.5 2.5 0 014 19.5z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M20 4v15a2 2 0 01-2 2H6.5a2.5 2.5 0 01-2.5-2.5" />
        </svg>
        자서전 생성
      </button>
    </div>

    <!-- 뷰어 -->
    <div v-if="viewerOpen" class="viewer book-viewer" role="dialog" aria-modal="true">
      <div class="viewer-topbar glass">
        <button class="close" @click="closeViewer" aria-label="닫기">닫기</button>
        <div class="viewer-title">{{ ownerName }}의 자서전</div>
        <div class="spacer"></div>
      </div>

      <div class="spread">
        <div class="spine" aria-hidden="true"></div>

        <div class="pages-wrap pretty" ref="pagesWrap" @scroll.passive="onScroll">
          <!-- 표지 -->
          <section class="vpage">
            <div class="page-outer">
              <div class="paper cover">
                <img :src="book?.coverSrc" alt="표지" class="cover-img-full" />
                <div class="cover-vignette"></div>
                <div class="cover-title">{{ ownerName }}의 자서전</div>
              </div>
            </div>
          </section>

          <!-- 본문 -->
          <section v-for="(page, i) in pages" :key="i" class="vpage">
            <div class="page-outer">
              <div class="paper">
                <h3 class="p-title readable">{{ page.title }}</h3>
                <p class="p-text readable">{{ page.text }}</p>

                <div class="folio">
                  <span class="folio-title">{{ page.title }}</span>
                  <span class="folio-page">{{ i + 1 }}</span>
                </div>
              </div>
            </div>
          </section>

          <button class="zone left" @click="prevPage" aria-label="이전 페이지"></button>
          <button class="zone right" @click="nextPage" aria-label="다음 페이지"></button>
        </div>

        <button class="flip prev" @click="prevPage" aria-label="이전 페이지">‹</button>
        <button class="flip next" @click="nextPage" aria-label="다음 페이지">›</button>
      </div>

      <div class="viewer-bottom glass">
        <ul class="dots" role="tablist" aria-label="페이지 인디케이터">
          <li v-for="n in totalPages" :key="n" :class="{ active: n - 1 === currentPage }"></li>
        </ul>
        <div class="controls">
          <button class="ctrl" @click="toggleSpeak" :aria-pressed="isSpeaking ? 'true' : 'false'">
            {{ isSpeaking ? '듣기 중지' : '자서전 듣기' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 모달 -->
    <dialog ref="confirm1" class="confirm-dialog">
      <p class="confirm-text">자서전을 다시 생성하시겠습니까?</p>
      <div class="actions">
        <button class="btn-red" @click="closeConfirm(1)">아니오</button>
        <button class="btn-purple" @click="confirmSecond">예</button>
      </div>
    </dialog>

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
import { ref, computed, onMounted, nextTick } from "vue"
import jsPDF from "jspdf"
import Header from "@/components/Header.vue"
import { addKoreanFont } from "@/jspdf-korean-font.js"
import defaultCoverImg from "@/assets/diary/2025-08-25.jpg"

// 타입 정의
type Book = { coverSrc: string }
type Page = { title: string; text: string }

// 기본값
const ownerName = "OOO"
const defaultCover = defaultCoverImg
const book = ref<Book | null>(null)
const pages = ref<Page[]>([
  { title: "손자와 함께한 강가에서의 오후", text: "손주와 낚시하며 평화로운 하루를 보냈습니다." },
  { title: "작은 기쁨들", text: "따뜻한 차 한 잔, 산책, 해질녘 노을이 가장 큰 위로가 되었습니다." },
  { title: "오늘의 마음", text: "행복, 평온함" },
])

// 로컬스토리지 키
const STORAGE_BOOK = "autobio.book"
const STORAGE_GEN = "autobio.gen"
const GEN_DURATION = 5000

// 상태
const isGenerating = ref(false)
const progress = ref(0)
const confirm1 = ref<HTMLDialogElement | null>(null)
const confirm2 = ref<HTMLDialogElement | null>(null)

// 생성 프로세스
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
  const { startedAt, duration } = JSON.parse(raw) as { startedAt: number; duration: number }
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

document.addEventListener("visibilitychange", () => {
  if (document.visibilityState === "visible") tick()
})

function onCreateClicked() {
  if (book.value) confirm1.value?.showModal()
  else startGeneration()
}
function confirmSecond() {
  confirm1.value?.close()
  confirm2.value?.showModal()
}
function closeConfirm(which: 1 | 2) {
  ; (which === 1 ? confirm1 : confirm2).value?.close()
}

// 뷰어
const viewerOpen = ref(false)
const pagesWrap = ref<HTMLDivElement | null>(null)
const currentPage = ref(0)
const totalPages = computed(() => pages.value.length + 1)

function openViewer() {
  if (!book.value) return
  viewerOpen.value = true
  nextTick(() => {
    currentPage.value = 0
    pagesWrap.value?.scrollTo({ left: 0, behavior: "auto" })
  })
}
function closeViewer() {
  viewerOpen.value = false
  stopSpeak()
}
function onScroll() {
  if (!pagesWrap.value) return
  const { scrollLeft, clientWidth } = pagesWrap.value
  // 서브픽셀/경계값 안정화
  const page = Math.round(scrollLeft / Math.max(1, clientWidth))
  currentPage.value = Math.max(0, Math.min(totalPages.value - 1, page))
}
function nextPage() {
  if (!pagesWrap.value) return
  const { clientWidth } = pagesWrap.value
  currentPage.value = Math.min(totalPages.value - 1, currentPage.value + 1)
  pagesWrap.value.scrollTo({ left: clientWidth * currentPage.value, behavior: "smooth" })
}
function prevPage() {
  if (!pagesWrap.value) return
  const { clientWidth } = pagesWrap.value
  currentPage.value = Math.max(0, currentPage.value - 1)
  pagesWrap.value.scrollTo({ left: clientWidth * currentPage.value, behavior: "smooth" })
}

// 낭독 기능
const isSpeaking = ref(false)
let utter: SpeechSynthesisUtterance | null = null
function toggleSpeak() {
  if (isSpeaking.value || speechSynthesis.speaking) {
    stopSpeak()
    return
  }
  const sliceFrom = Math.max(0, currentPage.value - 1)
  const text = (sliceFrom < 0 ? pages.value : pages.value.slice(sliceFrom))
    .map((p) => `${p.title}\n${p.text}`)
    .join("\n\n")
  utter = new SpeechSynthesisUtterance(text)
  utter.lang = "ko-KR"
  utter.rate = 1.0
  utter.onstart = () => (isSpeaking.value = true)
  utter.onend = () => (isSpeaking.value = false)
  utter.onerror = () => (isSpeaking.value = false)
  speechSynthesis.speak(utter)
}
function stopSpeak() {
  try {
    speechSynthesis.cancel()
  } catch { }
  isSpeaking.value = false
}

// 공유 기능
async function shareBook() {
  const shareTitle = `${ownerName}의 자서전`
  const shareText = "우리 대화를 바탕으로 만든 자서전을 공유해요."
  const shareUrl = location.href

  // 1) Web Share API
  if ("share" in navigator) {
    try {
      await (navigator as any).share({ title: shareTitle, text: shareText, url: shareUrl })
      return
    } catch {
      // 취소 등은 무시
    }
  }

  // 2) Clipboard API
  if ("clipboard" in navigator) {
    try {
      const nav = navigator as Navigator & { clipboard: Clipboard }
      await nav.clipboard.writeText(shareUrl)
      alert("공유 링크를 클립보드에 복사했어요.")
      return
    } catch (err) {
      console.error("Clipboard API 실패:", err)
    }
  }

  // 3) Fallback
  fallbackCopyText(shareUrl)
}

// fallback: execCommand 사용 (구형 브라우저용)
function fallbackCopyText(text: string) {
  const textarea = document.createElement("textarea")
  textarea.value = text
  textarea.style.position = "fixed"
  textarea.style.left = "-9999px"
  document.body.appendChild(textarea)
  textarea.focus()
  textarea.select()
  try {
    document.execCommand("copy")
    alert("공유 링크를 클립보드에 복사했어요. (fallback)")
  } catch {
    alert("공유 기능을 사용할 수 없어요. 브라우저를 업데이트해 주세요.")
  }
  document.body.removeChild(textarea)
}

// PDF 내보내기 (옵션: 현재 UI에서는 버튼 미노출)
async function exportPDF() {
  if (!book.value) return
  const doc = new jsPDF({ unit: "pt", format: "a4" })
  addKoreanFont(doc)
  doc.setFont("noto", "normal")

  const pageW = doc.internal.pageSize.getWidth()
  const pageH = doc.internal.pageSize.getHeight()
  const margin = 48
  let y = margin

  doc.setFontSize(20)
  doc.text(`${ownerName}의 자서전`, margin, y)
  y += 28

  try {
    const dataUrl = await imageToDataURL(book.value.coverSrc)
    const imgW = pageW - margin * 2
    const imgH = (imgW * 3) / 4
    doc.addImage(dataUrl, "JPEG", margin, y, imgW, imgH, undefined, "FAST")
    y += imgH + 16
  } catch { }

  const lineW = pageW - margin * 2
  for (const p of pages.value) {
    const t1 = doc.splitTextToSize(p.title, lineW)
    const t2 = doc.splitTextToSize(p.text, lineW)
    doc.setFontSize(14)
    if (y + t1.length * 18 > pageH - margin) {
      doc.addPage()
      y = margin
      doc.setFont("noto", "normal")
    }
    doc.text(t1, margin, y)
    y += t1.length * 18 + 6

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
        if (i < t2.length) {
          doc.addPage()
          y = margin
          doc.setFont("noto", "normal")
          doc.setFontSize(12)
        } else {
          y += chunk.length * 16 + 12
        }
      }
    } else {
      doc.text(t2, margin, y)
      y += need + 12
    }
  }

  doc.save("autobiography.pdf")
}
function imageToDataURL(src: string): Promise<string> {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.crossOrigin = "anonymous"
    img.onload = () => {
      const c = document.createElement("canvas")
      const ctx = c.getContext("2d")!
      c.width = img.naturalWidth
      c.height = img.naturalHeight
      ctx.drawImage(img, 0, 0)
      resolve(c.toDataURL("image/jpeg", 0.92))
    }
    img.onerror = reject
    img.src = src
  })
}

// 마운트 시 기존 데이터 복원
onMounted(() => {
  try {
    const raw = localStorage.getItem(STORAGE_BOOK)
    if (raw) book.value = JSON.parse(raw) as Book
  } catch { }
  tick()
})

/**
 * 참고: 만약 빌드 환경에서 간혹 navigator.clipboard 타입 인식 문제가 있으면
 * src/types/dom-clipboard.d.ts 파일을 추가하고 아래 보강 선언을 넣어주세요.
 *
 * interface NavigatorClipboard { clipboard: Clipboard }
 * interface Navigator extends NavigatorClipboard {}
 */
</script>

<style scoped>
:root {
  --header-h: 76px;
  /* 실제 Header 높이 */
  --hero-extra: 400px;
  /* 추가 내림 양 */
  --footer-h: 84px;
  --fab-gap: 16px;
  --radius: 14px;
  --shadow: 0 6px 20px rgba(0, 0, 0, .08);
  --purple: #6d28d9;
  --lav: #ede9fe;
}

/* ===== 메인 페이지 (네가 준 버전 그대로) ===== */
.memoir-page {
  background: #F8FAFC;
  min-height: 100vh;
}

.header-gap {
  height: calc(var(--header-h) + var(--hero-extra));
}

.content {
  max-width: 720px;
  margin: 0 auto;
  padding: 20px;
  padding-top: 70px;
  padding-bottom: calc(120px + var(--footer-h) + env(safe-area-inset-bottom));
}

.title {
  font-size: 24px;
  font-weight: 800;
  margin: 0 0 6px;
  text-align: center;
}

.subtitle {
  color: #666;
  text-align: center;
  margin: 0 0 14px;
}

.stage {
  min-height: 52vh;
  display: grid;
  place-items: center;
  margin-top: 8px;
}

.card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: var(--radius);
  padding: 14px 14px 12px;
  width: min(78vw, 240px);
  box-shadow: var(--shadow);
  text-align: center;
}

.cover.small {
  aspect-ratio: 3/4;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.cover-strip {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 36px;
  background: linear-gradient(to top, rgba(255, 255, 255, .92), rgba(255, 255, 255, .55), transparent);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 6px;
}

.strip-title {
  font-weight: 800;
  font-size: 12px;
  color: #111;
  text-shadow: 0 1px 0 rgba(255, 255, 255, .9);
}

.book-caption {
  margin-top: 10px;
  font-weight: 800;
  color: #111;
}

.result-panel {
  width: min(92vw, 720px);
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(0, 0, 0, .10);
  position: relative;
  overflow: hidden;
}

.ribbon {
  position: absolute;
  inset: 0 0 auto 0;
  height: 84px;
  background: radial-gradient(120% 100% at 0% 0%, #f3e8ff 0%, #ffffff 60%),
    linear-gradient(90deg, #ede9fe 0%, #ffffff 70%);
  pointer-events: none;
}

.result-inner {
  position: relative;
  padding: 60px 16px 16px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width:640px) {
  .result-inner {
    grid-template-columns: 280px 1fr;
    align-items: center;
  }
}

.cover-wrap {
  position: relative;
  width: min(72vw, 280px);
  margin: 0 auto;
  cursor: pointer;
}

.cover-ghost {
  position: absolute;
  inset: auto 12px -12px 12px;
  height: 18px;
  border-radius: 999px;
  background: radial-gradient(60% 100% at 50% 0%, rgba(0, 0, 0, .18), transparent);
  filter: blur(6px);
}

.cover-wrap .cover-img {
  width: 100%;
  aspect-ratio: 3/4;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 14px 34px rgba(0, 0, 0, .22);
  display: block;
}

.cover-wrap .cover-strip {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 40px;
  background: linear-gradient(to top, rgba(255, 255, 255, .92), rgba(255, 255, 255, .55), transparent);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 6px;
}

.result-info {
  text-align: center;
}

@media (min-width:640px) {
  .result-info {
    text-align: left;
    padding-right: 10px;
  }
}

.result-title {
  font-size: 20px;
  font-weight: 900;
  margin: 0 0 6px;
  color: #111827;
}

.result-desc {
  color: #4b5563;
  margin: 0 0 14px;
}

.result-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

@media (min-width:640px) {
  .result-actions {
    justify-content: flex-start;
  }
}

.btn-primary {
  background: var(--lav);
  color: var(--purple);
  border: 1px solid #d8b4fe;
  border-radius: 12px;
  padding: 10px 14px;
  font-weight: 800;
  cursor: pointer;
}

.btn-primary:active {
  background: var(--purple);
  color: #fff;
}

.btn-ghost {
  background: #fff;
  color: #374151;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 10px 14px;
  font-weight: 800;
  cursor: pointer;
}

.btn-ghost:active {
  background: #f9fafb;
}

.bottom-spacer {
  height: calc(120px + var(--footer-h));
}
.fab-wrap{
  position: sticky;
  bottom: calc(env(safe-area-inset-bottom) + var(--footer-h) + var(--fab-gap));
  display:flex; flex-direction:column; align-items:center; gap:10px;
  z-index: 3000;
  pointer-events: none; /* 그대로 두고… */
}
.fab-wrap .fab-btn,
.fab-wrap .toast { pointer-events:auto; }

.fab-btn {
  margin-top: 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px 16px;
  min-width: 220px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, .08);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 700;
  cursor: pointer;
  transition: transform .15s ease;
}

.fab-btn:active {
  transform: scale(.97);
  background: #f9fafb;
}

.fab-btn .icon {
  width: 22px;
  height: 22px;
  stroke: #4c1d95;
}

.toast {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, .12);
  padding: 10px 14px;
  width: min(92vw, 300px);
  display: grid;
  gap: 8px;
  justify-items: center;
}

.toast-title {
  font-weight: 800;
  font-size: 14px;
}

.progress {
  width: 100%;
  height: 8px;
  background: #ececec;
  border-radius: 999px;
  overflow: hidden;
}

.progress .bar {
  height: 100%;
  background: #111;
}

.pct {
  font-size: 12px;
  color: #6b7280;
}

/* ===== 뷰어(책 UI/UX) — 리디자인 전용. 메인과 충돌 없도록 클래스 한정 ===== */
.viewer.book-viewer {
  position: fixed;
  inset: 0;
  z-index: 4000;
  background: radial-gradient(100% 120% at 50% 0%, #0b0c10 0%, #0a0b0f 60%, #090a0d 100%);
  color: #111;
  display: flex;
  flex-direction: column;
}

.glass {
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  background: linear-gradient(to bottom, rgba(255, 255, 255, .08), rgba(255, 255, 255, .02));
  border-bottom: 1px solid rgba(255, 255, 255, .06);
}

.viewer-topbar.glass {
  height: 56px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  padding: 0 12px;
  color: #e5e7eb;
}

.viewer-topbar .close {
  background: rgba(255, 255, 255, .06);
  color: #e5e7eb;
  border: 1px solid rgba(255, 255, 255, .18);
  padding: 6px 12px;
  border-radius: 10px;
  font-weight: 700;
}

.viewer-topbar .viewer-title {
  text-align: center;
  font-weight: 900;
  letter-spacing: -.01em;
}

.viewer-topbar .spacer {
  width: 56px;
}

.spread {
  position: relative;
  flex: 1;
  display: grid;
  place-items: center;
  padding: 18px 8px 12px;
}

.spine {
  position: absolute;
  inset: 0;
  background: radial-gradient(60% 80% at 50% 10%, rgba(255, 255, 255, .06), transparent 60%);
  pointer-events: none;
}

.spine::after {
  content: "";
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 2px;
  transform: translateX(-50%);
  background: linear-gradient(to bottom, rgba(255, 255, 255, .08), rgba(0, 0, 0, .35));
  filter: blur(.2px);
  opacity: .5;
}

.pages-wrap.pretty {
  position: relative;
  width: 100%;
  max-width: 980px;
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
  display: flex;
  scroll-snap-type: x mandatory;
  scroll-behavior: smooth;
  padding: 8px 12px 24px;
}

.vpage {
  flex: 0 0 100%;
  scroll-snap-align: start;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.page-outer {
  perspective: 1200px;
  width: min(92vw, 760px);
}

.paper {
  position: relative;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 10px 28px rgba(0, 0, 0, .35), inset 0 1px 0 rgba(255, 255, 255, .8);
  width: 100%;
  min-height: min(74vh, 580px);
  padding: 20px 22px;
  overflow: hidden;
  background-image: radial-gradient(1px 1px at 12% 18%, rgba(0, 0, 0, .02) 0, transparent 100%), radial-gradient(1px 1px at 78% 64%, rgba(0, 0, 0, .02) 0, transparent 100%), linear-gradient(transparent 0, transparent 32px, rgba(17, 24, 39, .04) 33px);
  background-size: auto, auto, 100% 34px;
}

.paper.cover {
  display: grid;
  place-items: center;
  padding: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, #f3e8ff, #ffffff 60%);
}

.cover-img-full {
  width: min(70vw, 460px);
  max-width: 88%;
  aspect-ratio: 3/4;
  object-fit: cover;
  border-radius: 14px;
  box-shadow: 0 14px 38px rgba(0, 0, 0, .25);
}

.cover-vignette {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background: radial-gradient(80% 60% at 50% 50%, rgba(0, 0, 0, .06), transparent 70%);
}

.cover-title {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
  font-weight: 900;
  letter-spacing: -.01em;
  color: #1f2937;
  font-size: clamp(18px, 3.2vw, 26px);
}

.readable.p-title {
  font-size: clamp(18px, 3.2vw, 22px);
  font-weight: 900;
  color: #111827;
  margin: 6px 0 12px;
  text-align: center;
}

.readable.p-text {
  font-size: clamp(14px, 2.6vw, 16px);
  line-height: 1.8;
  color: #1f2937;
  white-space: pre-wrap;
  text-align: justify;
  text-justify: inter-word;
}

.folio {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 10px;
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  padding: 0 16px;
  color: #6b7280;
  font-size: 12px;
  letter-spacing: .02em;
}

.folio-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.folio-page {
  font-variant-numeric: tabular-nums;
}

.flip {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, .24);
  background: rgba(255, 255, 255, .08);
  color: #e5e7eb;
  font-size: 24px;
  display: grid;
  place-items: center;
  cursor: pointer;
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
}

.flip.prev {
  left: 18px;
}

.flip.next {
  right: 18px;
}

.flip:active {
  background: rgba(255, 255, 255, .16);
}

.viewer-bottom.glass {
  height: 64px;
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  padding: 0 14px 10px;
  color: #e5e7eb;
  gap: 12px;
  border-top: 1px solid rgba(255, 255, 255, .06);
}

.dots {
  display: flex;
  gap: 6px;
  list-style: none;
  padding: 0;
  margin: 0;
  align-items: center;
}

.dots li {
  width: 6px;
  height: 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, .28);
  transition: transform .2s ease, background .2s ease;
}

.dots li.active {
  transform: scale(1.4);
  background: #ede9fe;
}

.controls {
  display: flex;
  gap: 8px;
}

.ctrl {
  background: #ede9fe;
  color: #6d28d9;
  border: 0;
  border-radius: 12px;
  padding: 8px 12px;
  font-weight: 900;
}

.ctrl:active {
  background: #6d28d9;
  color: #fff;
}

.zone {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 18%;
  background: transparent;
  border: 0;
}

.zone.left {
  left: 0;
}

.zone.right {
  right: 0;
}

.paper::after {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(to right, rgba(0, 0, 0, .06), transparent 18%) left top/50% 100% no-repeat,
    linear-gradient(to left, rgba(0, 0, 0, .06), transparent 18%) right top/50% 100% no-repeat;
  opacity: .06;
  pointer-events: none;
}

@media (min-width:840px) {
  .flip.prev {
    left: 24px;
  }

  .flip.next {
    right: 24px;
  }
}

/* ===== 확인 모달 & 큰 화면 보정 (그대로) ===== */
.confirm-dialog {
  border: 0;
  border-radius: 14px;
  padding: 18px;
  max-width: 360px;
  width: 86vw;
  text-align: center;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.confirm-dialog::backdrop {
  background: rgba(0, 0, 0, .45);
}

.confirm-text {
  font-weight: 800;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.btn-red {
  flex: 1;
  background: #fee2e2;
  color: #b91c1c;
  border: none;
  border-radius: 10px;
  padding: 10px;
  font-weight: 800;
}

.btn-purple {
  flex: 1;
  background: var(--lav);
  color: var(--purple);
  border: none;
  border-radius: 10px;
  padding: 10px;
  font-weight: 800;
}

.btn-purple:active {
  background: var(--purple);
  color: #fff;
}

/* Compact(세로짧음) 환경: FAB을 흐름 안에 배치해서 겹침 완전 방지 */
@media (max-height: 780px) {
  .fab-wrap{
    position: static;   /* fixed/sticky 해제 */
    margin-top: 12px;
  }

  /* 본문 아래 공간을 넉넉히 확보 (FAB 높이 + 여유) */
  .content{
    padding-bottom: calc(140px + var(--footer-h) + env(safe-area-inset-bottom));
  }

  /* 카드/헤더 리본도 살짝 컴팩트화해서 위쪽도 여유 확보(선택) */
  .result-inner{ padding: 44px 14px 14px; }
  .cover-wrap{ width: min(68vw, 240px); }
  .btn-primary, .btn-ghost{ padding: 8px 12px; }
}

</style>
