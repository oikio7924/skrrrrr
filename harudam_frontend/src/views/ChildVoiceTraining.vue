<template>
  <header class="topbar">
    <button class="icon-btn" @click="$router.back()" aria-label="뒤로가기">
      <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="#9378d5" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15 18l-6-6 6-6" />
      </svg>
    </button>
    <h1 class="title">자녀 AI 목소리 학습</h1>
    <div style="width: 28px"></div>
  </header>

  <div v-if="step === 1" class="content">
    <div class="content-inner">
      <div class="desc-group">
        <p class="desc">음성을 녹음하거나 파일을 업로드 해주세요.</p>
        <br>
        <p class="sub-desc">조용한 환경에서 1분 이상 녹음하면<br>더 높은 품질의 목소리가 만들어집니다.</p>
      </div>

      <div class="mic-icon" @click="startRecording">🎤</div>

      <div v-if="isRecording" class="recording-box">
        <span class="record-time">⏺ {{ recordTime }}초 녹음 중...</span>
        <div class="recording-controls">
          <button class="btn danger" @click="stopRecording">녹음 중지</button>
          <button class="btn gray" @click="restartRecording">다시 시작</button>
        </div>
      </div>

      <div v-else-if="uploadedFile" class="file-info-box">
        <span class="file-icon">🎵</span>
        <span class="file-name">{{ uploadedFile.name }}</span>
        <button class="btn-delete" @click="removeFile">🗑️</button>
      </div>

      <div v-else class="button-group">
        <button class="btn-record" @click="startRecording">
          <span class="btn-icon">🎤</span>
          <span class="btn-text">녹음 시작</span>
        </button>
        <button class="btn-upload" @click="triggerFileUpload">
          <span class="btn-icon">📂</span>
          <span class="btn-text">파일 업로드</span>
        </button>
      </div>

      <input ref="fileInput" type="file" accept="audio/*" hidden @change="handleFileChange" />

      <button class="btn-train" :disabled="!hasData" @click="startTraining">
        학습하기
      </button>
    </div>
  </div>

  <div v-else-if="step === 2" class="loading-wrapper">
    <div class="loading-lp spinning"></div>
    <div class="loading-bar">
      <div class="progress" :style="{ width: progress + '%' }"></div>
    </div>
    <p class="loading-text">{{ loadingText }}</p>
  </div>

  <div v-else-if="step === 3">
    <VoicePlayer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const step = ref(1)
const uploadedFile = ref<File | null>(null)

// 녹음 관련
const isRecording = ref(false)
const recordTime = ref(0)
let mediaRecorder: MediaRecorder | null = null
let chunks: BlobPart[] = []
let timer: any = null

function startRecording() {
  navigator.mediaDevices.getUserMedia({ audio: true }).then((stream) => {
    mediaRecorder = new MediaRecorder(stream)
    chunks = []
    mediaRecorder.ondataavailable = (e) => chunks.push(e.data)
    mediaRecorder.start()
    isRecording.value = true
    recordTime.value = 0
    uploadedFile.value = null; // 녹음 시작 시 기존 파일 제거
    timer = setInterval(() => recordTime.value++, 1000)
  })
}

function stopRecording() {
  if (mediaRecorder) {
    mediaRecorder.stop()
    mediaRecorder.onstop = () => {
      const blob = new Blob(chunks, { type: 'audio/webm' })
      uploadedFile.value = new File([blob], `record-${Date.now()}.webm`, {
        type: 'audio/webm',
      })
      isRecording.value = false;
      clearInterval(timer);
    }
  }
}

function cancelRecording() {
  if (mediaRecorder && mediaRecorder.state === 'recording') {
    mediaRecorder.stop();
  }
  isRecording.value = false
  recordTime.value = 0
  chunks = []
  clearInterval(timer)
}

// [추가] 다시 시작 기능
function restartRecording() {
  cancelRecording();
  // 약간의 딜레이 후 녹음 시작
  setTimeout(startRecording, 100);
}

// 파일 업로드
const fileInput = ref<HTMLInputElement | null>(null)

function triggerFileUpload() {
  fileInput.value?.click()
}

function handleFileChange(e: Event) {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    uploadedFile.value = target.files[0]
    cancelRecording(); // 파일 업로드 시 녹음 상태 취소
  }
}

function removeFile() {
  uploadedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = '' // input 초기화
  }
}

const hasData = computed(() => uploadedFile.value !== null)

// 학습 시작
const progress = ref(0)
const loadingText = ref('AI 학습 중입니다...')

function startTraining() {
  step.value = 2
  progress.value = 0
  let toggle = false
  const textInterval = setInterval(() => {
    loadingText.value = toggle ? 'AI 학습 중입니다...' : '잠시만 기다려주세요...'
    toggle = !toggle
  }, 1500)
  const progressInterval = setInterval(() => {
    if (progress.value < 100) progress.value += 5
  }, 250)
  setTimeout(() => {
    clearInterval(textInterval)
    clearInterval(progressInterval)
    router.push('/mypage/voiceplayer')
  }, 5000)
}
</script>

<style scoped>
/* --- [개선] 전체 레이아웃 --- */
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center; /* ✅ 수직 중앙 정렬 */
  gap: 24px;
  padding: 20px;
}

.content-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 24px; /* 내부 요소들의 기본 간격 */
  transform: translateY(-30px); /* ✅ 핵심: 전체 그룹을 위로 30px 이동 */
}

.desc-group {
  text-align: center;
  margin-bottom: 8px; /* 아이콘과의 간격 */
}
.desc {
  font-size: 17px; /* 기본 설명 텍스트 크기 조정 */
  font-weight: 600;
  color: #333;
}
.sub-desc {
  font-size: 15.5px; /* 추가 설명 텍스트 */
  color: #666;
  margin-top: 8px;
  line-height: 1.5;
}

.mic-icon {
  width: 140px; /* ✅ 아이콘 크기 확대 */
  height: 140px;
  font-size: 64px;
  border-radius: 50%;
  background: #9378d5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px ۲۰px rgba(147, 120, 213, 0.4);
  cursor: pointer;
  transition: transform 0.2s ease;
}
.mic-icon:active {
  transform: scale(0.95);
}

.button-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 16px;
}

/* --- [개선] 녹음 진행 UI --- */
.recording-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  width: 100%;
}
.record-time {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.recording-controls {
  display: flex;
  gap: 16px;
}

/* --- [개선] 녹음/업로드 완료 UI --- */
.file-info-box {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  max-width: 320px;
  height: 56px;
  padding: 0 16px;
  background: #f8f5ff;
  border: 1px solid #e0c3fc;
  border-radius: 12px;
  box-sizing: border-box;
}
.file-icon { font-size: 20px; }
.file-name {
  flex-grow: 1;
  text-align: left;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.btn-delete {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
}

/* --- 버튼 스타일 (일부 통합) --- */
.btn-record, .btn-upload, .btn-train {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  max-width: 320px;
  height: 56px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-sizing: border-box;
  transition: all 0.2s ease;
}
.btn-record, .btn-upload {
  background: #fff;
  border: 2px solid #9378d5;
  color: #9378d5;
  box-shadow: 0 2px 6px rgba(147, 120, 213, 0.15);
}
.btn-train {
  background: #9378d5;
  border: none;
  color: #fff;
  box-shadow: 0 4px 12px rgba(147, 120, 213, 0.35);
}

.btn { /* 녹음 중지/삭제 등 보조 버튼 */
  padding: 10px 24px;
  font-size: 14px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}
.btn.gray { background: #e9ecef; color: #495057; }
.btn.danger { background: #ff6b6b; color: #fff; }

/* --- [개선] 로딩 화면(STEP 2) --- */
.loading-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24px;
}
.loading-lp {
  width: 180px; /* ✅ 크기 확대 */
  height: 180px;
  border-radius: 50%;
  background: conic-gradient(#e0c3fc, #8ec5fc, #fbc2eb);
}
.spinning { animation: spin 3s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading-bar {
  width: 100%; /* ✅ 너비 확대 */
  max-width: 450px;
  height: 12px;
  background: #eee;
  border-radius: 6px;
  overflow: hidden;
}
.progress {
  height: 100%;
  background: #9378d5;
  transition: width 0.3s;
}
.loading-text {
  font-size: 18px; /* ✅ 텍스트 크기 확대 */
  font-weight: 500;
  color: #333;
}

/* --- 기존 스타일 --- */
.topbar { position: sticky; top: 0; left: 0; width: 100%; margin: 0; display: flex; align-items: center; justify-content: space-between; height: 64px; padding: 0 20px; background: #fff; border-bottom: 1px solid #eee; z-index: 100; box-sizing: border-box; }
.title { font-size: 20px; font-weight: 700; }
.btn-icon { font-size: 20px; line-height: 1; }
</style>
