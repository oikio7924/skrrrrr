<template>
  <div class="page">
    <!-- Top Bar -->
    <header class="topbar">
      <button class="icon-btn" @click="$router.back()" aria-label="뒤로가기">
        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="#9378d5" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 18l-6-6 6-6" />
        </svg>
      </button>
      <h1 class="title">자녀 얼굴 학습</h1>
      <div style="width:28px"></div>
    </header>

    <!-- STEP 1: 사진 준비 -->
    <main v-if="step === 1" class="container">
      <section class="card" aria-labelledby="sec-photo">
        <div class="card-head">
          <h2 id="sec-photo">사진 업로드/촬영</h2>
          <p class="hint">정면에서 선명하게 나온 1장의 사진을 권장해요. (512×512 이상)</p>
        </div>

        <div class="photo-row">
          <div
            class="avatar-dropzone"
            role="button"
            tabindex="0"
            @keydown.enter.prevent="triggerImageUpload"
            @click="triggerImageUpload"
            @dragover.prevent
            @drop.prevent="onDrop"
            aria-label="자녀 사진 업로드"
          >
            <div v-if="!photoDataUrl" class="placeholder">
              <div class="cam">📷</div>
              <div class="placeholder-text">여기를 눌러 사진 업로드<br/>또는 이미지를 끌어 놓기</div>
            </div>
            <img v-else :src="photoDataUrl" alt="자녀 사진 미리보기" class="avatar"/>
            <input
              ref="imgInput"
              type="file"
              accept="image/*"
              capture="environment"
              hidden
              @change="onImageChange"
            />
          </div>

          <div class="photo-actions">
            <button class="btn" @click="triggerImageUpload">
              <span class="btn-icon">📂</span>
              <span>사진 선택/촬영</span>
            </button>
            <button class="btn gray" :disabled="!photoDataUrl" @click="removePhoto">
              <span class="btn-icon">🗑️</span>
              <span>사진 삭제</span>
            </button>
            <p v-if="photoError" class="error">{{ photoError }}</p>
          </div>
        </div>
      </section>

      <section class="card" aria-labelledby="sec-tips">
        <div class="card-head">
          <h2 id="sec-tips">촬영 팁</h2>
          <ul class="tips">
            <li>밝고 균일한 조명에서 촬영하세요.</li>
            <li>정면을 바라본 사진(모자/마스크 X)을 권장해요.</li>
            <li>흔들린 사진은 피해주세요.</li>
          </ul>
        </div>
      </section>

      <div class="actions">
        <!-- 실제 비활성은 아니고 경고 모달을 띄우기 위해 class로만 흐리게 처리 -->
        <button class="btn big primary" :class="{ disabled: !canTrain || training }" @click="onClickTrain">
          <span class="btn-icon">🧠</span>
          <span>{{ training ? '준비 중…' : '학습하기' }}</span>
        </button>
      </div>
    </main>

    <!-- STEP 2: 학습 진행 -->
    <main v-else-if="step === 2" class="loading-wrapper">
      <div class="loading-lp spinning"></div>
      <div class="loading-bar">
        <div class="progress" :style="{ width: progress + '%' }"></div>
      </div>
      <p class="loading-text">{{ loadingText }}</p>
    </main>

    <!-- STEP 3: 완료 -->
    <main v-else-if="step === 3" class="done-wrapper">
      <div class="check">✅</div>
      <h2 class="done-title">얼굴 학습이 완료되었어요</h2>
      <p class="done-desc">이제 AI 챗봇이 자녀의 얼굴 캐릭터로 함께 대화할 수 있어요.</p>
      <div class="actions">
        <button class="btn big primary" @click="finish">확인</button>
      </div>
    </main>

    <!-- Centered Alert Modal -->
    <div
      v-if="alert.open"
      class="modal-overlay"
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
      @keydown.esc="closeAlert"
      @click="closeAlert"
    >
      <div class="modal" @click.stop>
        <h3 id="modal-title" class="modal-title">알림</h3>
        <p class="modal-body">{{ alert.message }}</p>
        <div class="modal-actions">
          <button class="btn primary" @click="closeAlert">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Step
const step = ref<1 | 2 | 3>(1)

// Photo state
const imgInput = ref<HTMLInputElement | null>(null)
const photoDataUrl = ref<string | null>(null)
const photoError = ref<string>('')

function triggerImageUpload(): void {
  imgInput.value?.click()
}

function onImageChange(e: Event): void {
  photoError.value = ''
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) {
    photoError.value = '이미지 파일만 업로드할 수 있어요.'
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    photoError.value = '파일 용량이 너무 큽니다. 10MB 이하로 업로드해 주세요.'
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    photoDataUrl.value = reader.result as string
    persist()
  }
  reader.readAsDataURL(file)
}

function onDrop(e: DragEvent): void {
  const file = e.dataTransfer?.files?.[0]
  if (!file) return
  const fake = { target: { files: [file] } } as unknown as Event
  onImageChange(fake)
}

function removePhoto(): void {
  photoDataUrl.value = null
  if (imgInput.value) imgInput.value.value = ''
  persist()
}

const canTrain = computed<boolean>(() => !!photoDataUrl.value)

// Persist locally
function persist(): void {
  try { localStorage.setItem('childFaceImage', photoDataUrl.value || '') } catch {}
}

onMounted(() => {
  try {
    const saved = localStorage.getItem('childFaceImage')
    if (saved) photoDataUrl.value = saved
  } catch {}
})

// Training state & timers (typed)
const training = ref<boolean>(false)
const progress = ref<number>(0)
const loadingText = ref<string>('AI 얼굴 모델을 준비하고 있어요…')

// 타이머 id (브라우저 환경: number)
let textIntervalId: number | null = null
let progressIntervalId: number | null = null

function clearTimers(): void {
  if (textIntervalId !== null) {
    window.clearInterval(textIntervalId)
    textIntervalId = null
  }
  if (progressIntervalId !== null) {
    window.clearInterval(progressIntervalId)
    progressIntervalId = null
  }
}

onBeforeUnmount(() => clearTimers())

// Center alert state
const alert = ref<{ open: boolean; message: string }>({ open: false, message: '' })
function openAlert(message: string): void {
  alert.value = { open: true, message }
  // 모달 열릴 때 확인 버튼 포커스
  window.setTimeout(() => {
    const btn = document.querySelector<HTMLButtonElement>('.modal-actions .btn')
    btn?.focus()
  }, 0)
}
function closeAlert(): void { alert.value.open = false }

function onClickTrain(): void {
  if (!canTrain.value || training.value) {
    openAlert('사진을 업로드해 주세요.')
    return
  }
  startTraining()
}

function startTraining(): void {
  training.value = true
  step.value = 2
  progress.value = 0
  clearTimers()

  let toggle = false
  textIntervalId = window.setInterval(() => {
    loadingText.value = toggle ? '얼굴 특징을 분석 중…' : '잠시만 기다려주세요…'
    toggle = !toggle
  }, 1500)

  progressIntervalId = window.setInterval(() => {
    if (progress.value < 100) progress.value += 4
  }, 200)

  // 실제 API 완료 시점에 맞춰 아래 로직을 호출하세요.
  window.setTimeout((): void => {
    clearTimers()
    step.value = 3
    training.value = false
  }, 5200)
}

function finish(): void {
  router.push('/mypage/CharacterTraining_Child')
}
</script>

<style scoped>
/* Layout & Topbar */
.page { display:flex; flex-direction:column; min-height:100vh; background:#faf9ff; padding-top:calc(env(safe-area-inset-top, 0px) + 6px); }
.topbar { position:sticky; top:0; left:0; width:100%; display:flex; align-items:center; justify-content:space-between; height:64px; padding:0 20px; background:#fff; border-bottom:1px solid #eee; z-index:10; }
.title { font-size:20px; font-weight:700; }
.icon-btn { background:none; border:none; padding:4px; cursor:pointer; }

/* Content Cards */
.container { width:100%; max-width:720px; margin:14px auto 0; padding:20px; display:flex; flex-direction:column; gap:18px; }
.card { background:#fff; border:1px solid #eee; border-radius:16px; padding:16px; box-shadow:0 4px 16px rgba(147,120,213,0.08); }
.card-head { display:flex; flex-direction:column; gap:6px; margin-bottom:12px; }
.card h2 { font-size:18px; margin:0; }
.hint { color:#666; font-size:14px; }

/* Photo row: keep actions to the right side on all sizes */
.photo-row { display:flex; flex-wrap:nowrap; align-items:stretch; gap:16px; }
.avatar-dropzone { flex:0 0 auto; width:clamp(140px, 40%, 200px); height:clamp(140px, 40vw, 200px); border-radius:16px; background:#f7f2ff; border:2px dashed #cdb8ff; display:flex; align-items:center; justify-content:center; overflow:hidden; }
.placeholder { display:flex; flex-direction:column; align-items:center; gap:8px; text-align:center; color:#7a6aa5; padding:8px; }
.placeholder .cam { font-size:24px; }
.placeholder-text { font-size:13px; line-height:1.4; }
.avatar { width:100%; height:100%; object-fit:cover; }
.photo-actions { flex:1; display:flex; flex-direction:column; justify-content:center; gap:10px; min-width:120px; }
.error { color:#e03131; font-size:14px; }

/* Tips */
.tips { margin:0; padding-left:18px; color:#444; line-height:1.6; }

/* Buttons */
.actions { display:flex; justify-content:center; margin-top:8px; }
.btn { display:inline-flex; align-items:center; justify-content:center; gap:8px; height:56px; padding:0 20px; border-radius:12px; border:2px solid #9378d5; background:#fff; color:#9378d5; font-size:16px; font-weight:700; cursor:pointer; box-shadow:0 2px 6px rgba(147,120,213,0.12); }
.btn.primary { background:#9378d5; color:#fff; border:none; box-shadow:0 4px 12px rgba(147,120,213,0.35); }
.btn.disabled { opacity:0.5; cursor:not-allowed; }
.btn.gray { border-color:#e9ecef; color:#495057; }
.btn.big { width:100%; max-width:360px; }
.btn-icon { font-size:18px; line-height:1; }

/* Loading */
.loading-wrapper { flex:1; display:flex; flex-direction:column; align-items:center; justify-content:center; gap:24px; padding:24px; }
.loading-lp { width:180px; height:180px; border-radius:50%; background: conic-gradient(#e0c3fc, #8ec5fc, #fbc2eb); }
.spinning { animation: spin 3s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading-bar { width:100%; max-width:460px; height:12px; background:#eee; border-radius:6px; overflow:hidden; }
.progress { height:100%; background:#9378d5; transition: width 0.3s; }
.loading-text { font-size:18px; font-weight:500; color:#333; }

/* Done */
.done-wrapper { flex:1; display:flex; flex-direction:column; align-items:center; justify-content:center; gap:12px; padding:24px; text-align:center; }
.check { font-size:42px; }
.done-title { margin:0; font-size:20px; }
.done-desc { color:#555; }

/* Responsive tweaks */
@media (max-width: 430px) {
  .photo-row { flex-direction:row; align-items:center; }
  .avatar-dropzone { width:44%; height:auto; aspect-ratio:1/1; }
  .photo-actions { flex:1; }
}

/* Modal */
.modal-overlay { position:fixed; inset:0; background:rgba(0,0,0,0.35); display:flex; align-items:center; justify-content:center; z-index:200; padding:16px; }
.modal { width:min(92vw, 420px); background:#fff; border-radius:16px; box-shadow:0 24px 60px rgba(0,0,0,0.18); border:1px solid #eee; padding:18px; }
.modal-title { margin:0 0 8px; font-size:18px; font-weight:700; color:#333; }
.modal-body { margin:0 0 14px; color:#444; line-height:1.6; }
.modal-actions { display:flex; justify-content:flex-end; }
</style>
