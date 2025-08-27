<!-- src/views/Memoir.vue -->
<template>
  <div class="page" :class="{ 'modal-open': isReader || view === 'generating' }">
    <main class="content">
      <h1 class="page-title">아버지의 일생</h1>

      <!-- 목록: 책 카드 + 아래 생성 버튼 -->
      <section class="list-view">
        <div v-if="book" class="book-slot">
          <div
            class="book size-md"
            role="button"
            tabindex="0"
            aria-expanded="false"
            :style="bookStyle"
            @click="openReading"
          >
            <div class="cover">
              <img class="cover-img" :src="book.coverSrc" alt="자서전 표지" />
              <div class="cover-title">
                <div class="name">{{ ownerName }}의</div>
                <div class="main">자서전</div>
                <div class="sub">한 페이지가 다 자서전임</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 생성 버튼 -->
        <div class="actions-col">
          <button
            class="btn"
            :class="hasGenerated ? 'create small' : 'create'"
            type="button"
            @click="onCreateClicked"
          >자서전 생성</button>
          <p v-if="!hasGenerated" class="hint">지금까지의 대화를 요약해 한 권의 책으로 만들어 드려요.</p>
        </div>
      </section>
    </main>

    <!-- 생성 로딩 -->
    <div v-if="view === 'generating'" class="scrim" @click.self>
      <div class="loading-card">
        <div class="skeleton cover-skeleton"></div>
        <div class="progress"><div class="bar" :style="{ width: progress + '%' }"></div></div>
        <p class="status">자서전 생성 중… <strong>{{ progress.toFixed(0) }}%</strong></p>
      </div>
    </div>

    <!-- 읽기(펼침) 오버레이 -->
    <div v-if="isReader" class="reader-layer" @click.self="closeReading">
      <!-- 우상단 뒤로가기 (오버레이 최상단) -->
      <button class="overlay-back" type="button" @click="backToMemoir" aria-label="뒤로가기">←</button>

      <div class="reader-card">
        <!-- 책(작게) + 느린 펼침 -->
        <div class="book size-sm" :class="{ open: isBookOpen }" :style="bookStyle">
          <div class="cover">
            <img class="cover-img" :src="book!.coverSrc" alt="자서전 표지" />
            <div class="cover-title">
              <div class="name">{{ ownerName }}의</div>
              <div class="main">자서전</div>
              <div class="sub">한 페이지가 다 자서전임</div>
            </div>
          </div>

          <!-- 표지와 ‘붙어’ 나오는 페이지 -->
          <div class="pages" id="autobio-pages">
            <div class="page-shadow"></div>
            <article class="page-body">
              <h2 class="chapter">손자와 함께한 강가에서의 오후</h2>
              <p class="para">
                오랜만에 손주와 강가에 앉아 낚싯대를 드리우니 마음이 고요해졌습니다.
                고기는 못 잡아도 웃음과 대화가 가득한, 세월이 잠시 멈춘 듯한 시간.
              </p>
              <h3 class="chapter">작은 기쁨들</h3>
              <p class="para">따뜻한 차 한 잔, 느릿한 산책, 해질녘 노을.</p>
              <h3 class="chapter">오늘의 마음</h3>
              <p class="para">행복, 평온함</p>
              <div class="spacer"></div>
            </article>
          </div>
        </div>

        <!-- 플레이/공유: 항상 보임 -->
        <div class="player">
          <button class="icon" type="button" @click="togglePlay" :aria-pressed="isPlaying ? 'true' : 'false'">
            <img :src="playIcon" alt="재생" />
          </button>
          <div class="timeline" @click="scrub($event)">
            <div class="timeline__bar" :style="{ width: playProgress + '%' }"></div>
          </div>
          <div class="time">{{ elapsedLabel }} / {{ durationLabel }}</div>
          <button class="icon" type="button" @click="share">
            <img :src="shareIcon" alt="공유" />
          </button>
        </div>
      </div>
    </div>

    <!-- 재생성 확인 모달 -->
    <div v-if="showConfirm" class="modal-scrim" @click.self="showConfirm=false">
      <div class="modal">
        <h3>자서전을 다시 생성하시겠습니까?</h3>
        <div class="modal__actions">
          <button class="btn yes" @click="confirmRegenerate">예</button>
          <button class="btn no"  @click="showConfirm=false">아니오</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onActivated, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

type Theme = { a: string; b: string; ink: string }
type Book  = { id: string; title: string; coverSrc: string; theme: Theme; createdAt: number }

const STORAGE_KEY = 'autobio.book.v1'
const loadBook = (): Book | null => { try { const raw = localStorage.getItem(STORAGE_KEY); return raw ? JSON.parse(raw) as Book : null } catch { return null } }
const saveBook = (b: Book) => localStorage.setItem(STORAGE_KEY, JSON.stringify(b))

type View = 'list' | 'generating'
const view = ref<View>('list')
const book = ref<Book | null>(null)
const hasGenerated = ref(false)
const ownerName = 'OOO'

/* --- 오버레이/애니메이션 --- */
const isReader   = ref(false)   // 오버레이 열림
const isBookOpen = ref(false)   // 표지 펼침 클래스

const router = useRouter()
const backToMemoir = () => {
  try { router.push({ name: 'memoir' }) } catch { try { router.push('/memoir') } catch {} }
  closeReading()
}
const openReading = async () => {
  if (!book.value) return
  isReader.value = true
  isBookOpen.value = false
  await nextTick()
  requestAnimationFrame(() => { isBookOpen.value = true })  // 다음 프레임에 펼치기 시작
}
const closeReading = () => {
  isBookOpen.value = false
  window.setTimeout(() => { isReader.value = false }, 1200) // transition 끝난 뒤 닫기
}

/* --- 팔레트/스타일 --- */
const PALETTES: Theme[] = [
  { a: '#fbc2eb', b: '#a6c1ee', ink: '#111' },
  { a: '#8ec5fc', b: '#e0c3fc', ink: '#101828' },
  { a: '#a1c4fd', b: '#c2e9fb', ink: '#0f172a' },
  { a: '#ffecd2', b: '#fcb69f', ink: '#0b0b0b' },
  { a: '#d4fc79', b: '#96e6a1', ink: '#081a10' },
  { a: '#f6d365', b: '#fda085', ink: '#0f172a' },
]
const pickTheme = (): Theme => PALETTES[Math.floor(Math.random() * PALETTES.length)]
const bookStyle = computed(() => book.value ? ({ '--c1': book.value.theme.a, '--c2': book.value.theme.b, '--ink': book.value.theme.ink }) : {})

/* --- 생성 흐름 --- */
const progress = ref(0)
let genTimer: number | null = null, genTick: number | null = null

const startGenerate = () => {
  progress.value = 0
  view.value = 'generating'

  const total = 5000
  const start = performance.now()
  genTick = window.setInterval(() => {
    progress.value = Math.min(100, ((performance.now() - start) / total) * 100)
  }, 100)

  genTimer = window.setTimeout(() => {
    clearTimers()
    const theme = pickTheme()
    const newBook: Book = {
      id: crypto.randomUUID?.() ?? String(Date.now()),
      title: `${ownerName}의 자서전`,
      coverSrc: '@/assets/diary/2025-08-25.jpg',
      theme,
      createdAt: Date.now(),
    }
    book.value = newBook
    saveBook(newBook)
    hasGenerated.value = true

    view.value = 'list'
    openReading()  // 완료 즉시 펼침
  }, total)
}
const clearTimers = () => { if (genTimer) window.clearTimeout(genTimer); if (genTick) window.clearInterval(genTick); genTimer = null; genTick = null }

/* --- 생성 버튼 --- */
const showConfirm = ref(false)
const onCreateClicked = () => { hasGenerated.value ? showConfirm.value = true : startGenerate() }
const confirmRegenerate = () => { showConfirm.value = false; startGenerate() }

/* --- 진입/복귀 --- */
onMounted(() => {
  const saved = loadBook()
  if (saved) { book.value = saved; hasGenerated.value = true }
  view.value = 'list'
  isReader.value = false
  isBookOpen.value = false
})
onActivated(() => { view.value = 'list'; isReader.value = false; isBookOpen.value = false })
onUnmounted(() => clearTimers())

/* --- 플레이어 --- */
const isPlaying = ref(false)
const elapsed = ref(0)
const duration = ref(85)
let ticker: number | null = null
const tick = () => { elapsed.value = Math.min(duration.value, elapsed.value + 1); if (elapsed.value >= duration.value) pause() }
const play = () => { if (!isPlaying.value) { isPlaying.value = true; ticker = window.setInterval(tick, 1000) } }
const pause = () => { isPlaying.value = false; if (ticker) window.clearInterval(ticker); ticker = null }
const togglePlay = () => (isPlaying.value ? pause() : play())
const playProgress = computed(() => Math.max(0, Math.min(100, (elapsed.value / duration.value) * 100)))
const pad = (n: number) => String(Math.floor(n)).padStart(2, '0')
const toMMSS = (s: number) => `${pad(Math.floor(s / 60))} : ${pad(s % 60)}`
const elapsedLabel = computed(() => toMMSS(elapsed.value))
const durationLabel = computed(() => toMMSS(duration.value))
const scrub = (e: MouseEvent) => {
  const el = e.currentTarget as HTMLElement
  const rect = el.getBoundingClientRect()
  const ratio = (e.clientX - rect.left) / rect.width
  elapsed.value = Math.round(duration.value * Math.max(0, Math.min(1, ratio)))
}

/* --- 공유 --- */
const playIcon = '@/assets/play_icon.png'
const shareIcon = '@/assets/upload_icon.png'
const share = async () => {
  const data = { title: book.value?.title ?? '자서전', text: '대화 요약 자서전' }
  if (navigator.share) { try { await navigator.share(data) } catch {} }
  else { alert('이 브라우저는 시스템 공유를 지원하지 않습니다.') }
}
</script>

<style scoped>
/* 페이지 약간 내리기 */
.page { min-height: 100svh; background: #eef1f4; }
.content { padding: 24px 16px 24px; }
.page-title { margin: 0 0 12px; font-size: 18px; font-weight: 700; letter-spacing: -0.02em; }

/* 목록 */
.book-slot { display: grid; place-items: center; margin: 8px 0 12px; }
.book { perspective: 1600px; position: relative; user-select: none; width: 100%; max-width: 420px; display: flex; flex-direction: column; }
.book.size-md { max-width: 360px; }
.book.size-sm { max-width: 280px; } /* 읽기: 더 작게 */

/* ===== 표지/페이지(리얼북 스타일) ===== */
.cover, .pages { width: 100%; border-radius: 18px; border: 1px solid #e6e7ea; box-shadow: 0 14px 28px rgba(0,0,0,.10); }
.cover {
  position: relative;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  transform-origin: left center;
  transform-style: preserve-3d;
  transition: transform 1.5s cubic-bezier(.2,.65,.15,1); /* 느리게 */
  overflow: hidden;
  aspect-ratio: 3/4;
  backface-visibility: hidden;
  will-change: transform;
}
/* 스파인(책등) */
.cover::before{
  content:"";
  position:absolute; left:0; top:0; bottom:0; width:18px;
  background: linear-gradient(to right, rgba(0,0,0,.18), rgba(0,0,0,.06), transparent);
  mix-blend-mode: multiply;
}
/* 글로시 + 안쪽 그림자 */
.cover::after{
  content:"";
  position:absolute; inset:0;
  background:
    linear-gradient(to bottom, rgba(255,255,255,.25), transparent 40%, rgba(0,0,0,.06) 60%, transparent 100%),
    radial-gradient(120% 60% at 10% 0%, rgba(255,255,255,.35), transparent 60%);
  pointer-events:none;
}
.cover-img { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; mix-blend-mode: multiply; opacity: .6; }
.cover-title { position: absolute; inset: 0; display: grid; align-content: center; justify-items: center; text-align: center; font-weight: 800; letter-spacing: -0.01em; color: var(--ink); padding: 10% 8%; }
.cover-title .name { font-size: clamp(16px, 5.2vw, 22px); }
.cover-title .main { font-size: clamp(26px, 9vw, 36px); margin-top: 8px; }
.cover-title .sub  { font-size: 11px; margin-top: auto; margin-bottom: 16px; font-weight: 700; color: rgba(0,0,0,.66); }

/* 페이지: 펼칠 때만 키움 */
.pages {
  background: #fff;
  margin-top: 8px;
  max-height: 0;
  overflow: hidden;
  transition: max-height 1.4s ease; /* 느리게 */
  position: relative;
}
.page-shadow { position: absolute; left: 0; top: 0; bottom: 0; width: 10px; background: linear-gradient(to right, rgba(0,0,0,.14), transparent); opacity: 0; transition: opacity .6s ease; }
.page-body { padding: 16px 16px 18px; }
.chapter { margin: 6px 0 8px; font-size: 16px; font-weight: 800; color: #1f2a37; }
.para { margin: 0 0 8px; color: #374151; line-height: 1.58; font-size: 14px; }
.spacer { height: 120px; }

.book.open .cover  { transform: rotateY(-170deg); } /* 펼침 */
.book.open .pages  { max-height: 40vh; overflow: auto; } /* 높이 제한 (버튼 보이게) */
.book.open .page-shadow { opacity: 1; }

/* 생성 버튼 */
.actions-col { display: grid; place-items: center; gap: 6px; margin: 8px 0 8px; }
.btn.create { width: 72vw; max-width: 300px; padding: 10px 14px; border-radius: 12px; border: 0; font-weight: 800; color: #fff; background: #111; }
.btn.create.small { width: 56vw; max-width: 240px; padding: 8px 12px; font-size: 13px; }
.hint { margin-top: 6px; font-size: 12px; color: #666; }

/* ===== 오버레이(항상 정가운데) ===== */
.reader-layer{
  position:fixed; inset:0;
  background: rgba(0,0,0,.45);
  z-index: 2147483000; /* 최상단 */
}
.reader-card{
  position:fixed; left:50%; top:50%;
  transform:translate(-50%,-50%);
  width:min(92vw, 340px);
  display:flex; flex-direction:column; align-items:center; gap:12px;
}
.overlay-back{
  position:fixed; right:12px; top:12px;
  z-index: 2147483001;
  background:#fff; border:1px solid #e6e7ea; border-radius:12px;
  padding:8px 10px; font-weight:800; box-shadow:0 8px 18px rgba(0,0,0,.12);
}

/* 플레이어 (항상 보임) */
.player { width: 100%; max-width: 280px; display: grid; grid-template-columns: 40px 1fr auto 40px; align-items: center; gap: 8px; }
.icon { width: 36px; height: 36px; display: grid; place-items: center; border: 0; background: transparent; }
.icon img { width: 22px; height: 22px; object-fit: contain; }
.timeline { height: 8px; background: #c8c8c8; border-radius: 999px; position: relative; overflow: hidden; cursor: pointer; }
.timeline__bar { position: absolute; inset: 0 auto 0 0; width: 0; background: #111; }
.time { font-size: 12px; min-width: 88px; text-align: right; padding-right: 4px; color: #333; }

/* 생성/로딩 공통 */
.scrim { position: fixed; inset: 0; background: rgba(0,0,0,.45); display: grid; place-items: center; z-index: 1000; padding: 12px; }
.loading-card { width: 100%; max-width: 360px; background: #fff; border: 1px solid #e5e7eb; border-radius: 16px; padding: 14px; box-shadow: 0 12px 24px rgba(0,0,0,.18); text-align: center; }
.skeleton { background: linear-gradient(90deg,#eee,#f6f6f6,#eee); background-size: 200% 100%; animation: shimmer 1.2s infinite linear; border-radius: 12px; }
.cover-skeleton { width: 100%; aspect-ratio: 3/4; }
@keyframes shimmer { 0%{background-position:200% 0}100%{background-position:-200% 0} }
.progress { margin-top: 12px; height: 8px; background: #ececec; border-radius: 999px; overflow: hidden; }
.progress .bar { height: 100%; background: #111; width: 0; transition: width .15s linear; }
.status { margin: 8px 2px 0; font-size: 13px; color: #6b7280; }

/* 모달 */
.modal-scrim { position: fixed; inset: 0; background: rgba(0,0,0,.45); display: grid; place-items: center; z-index: 1000; }
.modal { width: 86vw; max-width: 360px; background: #fff; border-radius: 14px; padding: 16px; box-shadow: 0 12px 24px rgba(0,0,0,.18); text-align: center; }
.modal__actions { margin-top: 12px; display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.modal .btn { padding: 10px 0; border-radius: 10px; border: 0; color: #fff; font-weight: 800; }
.modal .btn.yes { background: #2563eb; }
.modal .btn.no  { background: #dc2626; }

/* 스크롤 잠금 */
.modal-open { overflow: hidden; }
</style>
