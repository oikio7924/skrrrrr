<template>
  <!-- ✅ [수정] 전체 페이지를 감싸는 최상위 컨테이너 추가 -->
  <div class="voice-training-container">
    <!-- 기존 헤더 (topbar) -->
    <header class="topbar">
      <button class="icon-btn" @click="$router.back()" aria-label="뒤로가기">
        <svg xmlns="http://www.w.w3.org/2000/svg" width="28" height="28" fill="none" viewBox="0 0 24 24"
          stroke="#9378d5" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 18l-6-6 6-6" />
        </svg>
      </button>
      <h1 class="title">자녀 AI 목소리 학습</h1>
      <div style="width: 28px"></div>
    </header>

    <!-- ✅ [수정] 헤더를 제외한 모든 콘텐츠를 main 태그로 감싸 스크롤 영역을 분리 -->
    <main class="main-content">
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
        <!-- VoicePlayer 컴포넌트가 렌더링될 자리 (라우팅으로 대체됨) -->
        <p>학습 완료! 페이지 이동 중...</p>
      </div>
    </main>
    <!-- ✅ [추가] FooterNav 컴포넌트 추가 -->
    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
// ✅ [추가] FooterNav 컴포넌트 import
import FooterNav from '@/components/FooterNav.vue'
// VoicePlayer 컴포넌트가 별도 파일이라면 import 해야 합니다.
// import VoicePlayer from '@/components/VoicePlayer.vue'
import apiClient from "@/api/http"

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

function restartRecording() {
  cancelRecording();
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

async function startTraining() {
  if (!uploadedFile.value) {
    alert("파일이 없습니다!")
    return
  }

  step.value = 2 // 로딩 시작

  const formData = new FormData()
  formData.append("file", uploadedFile.value)

  try {
    formData.append("name", "child_voice")
    const res = await apiClient.post("/train-voice", formData, {
      headers: { "Content-Type": "multipart/form-data" }
    })

    const { voice_id, name } = res.data

    // 학습 완료 후 라우터 이동 (voice_id 전달)
    router.push({
      path: "/mypage/voiceplayer",
      query: { voiceId: voice_id, name }
    })
  } catch (e) {
    console.error("업로드 실패", e)
    alert("학습 중 오류 발생")
    step.value = 1
  }
}
</script>

<style scoped>
/* ===== ✅ [신규] 전체 레이아웃 구조 ===== */
.voice-training-container {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
  /* ✅ [수정] 배경색을 다른 페이지와 통일 */
  background-color: #f8fafc;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  /* ✅ [추가] FooterNav에 가려지지 않도록 하단 여백 추가 */
  padding-bottom: 7rem;
}

/* ===== ✅ [수정] 헤더 스타일 조정 ===== */
.topbar {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  z-index: 10;
  box-sizing: border-box;
  /* position: sticky 제거 */
}

/* --- 기존 스타일 유지 --- */

.title {
  font-size: 20px;
  font-weight: 700;
}

/* --- [수정] flex: 1 속성 제거 (부모가 제어) --- */
.content,
.loading-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  flex-grow: 1;
  /* ✅ 자식 요소가 main-content를 채우도록 */
}

.content-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 24px;
  /* ✅ [수정] 콘텐츠를 위로 올리는 transform 속성 제거 */
  /* transform: translateY(-30px); */
}

.desc-group {
  text-align: center;
  margin-bottom: 8px;
}

.desc {
  font-size: 17px;
  font-weight: 600;
  color: #333;
}

.sub-desc {
  font-size: 15.5px;
  color: #666;
  margin-top: 8px;
  line-height: 1.5;
}

.mic-icon {
  width: 140px;
  height: 140px;
  font-size: 64px;
  border-radius: 50%;
  background: #9378d5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 20px rgba(147, 120, 213, 0.4);
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

.file-icon {
  font-size: 20px;
}

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

.btn-record,
.btn-upload,
.btn-train {
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

.btn-record,
.btn-upload {
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

.btn {
  padding: 10px 24px;
  font-size: 14px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}

.btn.gray {
  background: #e9ecef;
  color: #495057;
}

.btn.danger {
  background: #ff6b6b;
  color: #fff;
}

.loading-lp {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: conic-gradient(#e0c3fc, #8ec5fc, #fbc2eb);
  /* ✅ [수정] 로딩 바와 간격을 주기 위해 margin-bottom 추가 */
  margin-bottom: 24px;
}

.spinning {
  animation: spin 3s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-bar {
  width: 100%;
  /* ✅ [수정] 너비 축소 */
  max-width: 360px;
  height: 12px;
  background: #e9ecef;
  /* 배경색 살짝 어둡게 */
  border-radius: 6px;
  overflow: hidden;
}

.progress {
  height: 100%;
  /* ✅ [수정] 세련된 그라데이션 및 애니메이션 효과 추가 */
  background: linear-gradient(90deg, #d8b4fe, #a78bfa);
  background-size: 200% 200%;
  animation: gradient-flow 2s ease infinite;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  /* 부드러운 전환 효과 */
}

/* ✅ [신규] 그라데이션 애니메이션 */
@keyframes gradient-flow {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}


.loading-text {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  /* ✅ [수정] 로딩바와 간격을 주기 위해 margin-top 추가 */
  margin-top: 16px;
}

.btn-icon {
  font-size: 20px;
  line-height: 1;
}
</style>
