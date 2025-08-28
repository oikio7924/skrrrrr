<template>
  <div class="welcome-container">
    <div v-if="isLoading" class="loading-state">
      <p>정보를 불러오는 중입니다...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <h2>오류</h2>
      <p>{{ error }}</p>
      <button @click="startService" class="btn-primary">메인으로 돌아가기</button>
    </div>

    <div v-else class="content-wrapper">
      <svg class="hero-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
        stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round"
          d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <h1>회원가입을 환영합니다!</h1>
      <div class="description-group">
        <p>{{ parentName }} 님을 위한 서비스 신청이 완료되었습니다.</p>
        <p>서비스 이용에 필요한 <strong>부모님 고유 코드</strong>입니다.</p>
      </div>
      <div class="code-box">
        <span id="parent-code">{{ parentCode }}</span>
        <button @click="copyCode" class="btn-copy">{{ copyButtonText }}</button>
      </div>
      <div class="notice">
        <p>※ 이 코드는 <strong>[마이페이지 > 인증코드]</strong>에서 다시 확인할 수 있습니다.</p>
      </div>
      <div class="button-wrapper" v-if="!isTraining">
        <button @click="startService" class="btn-primary">서비스 시작하기</button>
      </div>

      <!-- ✅ AI 학습 로딩 UI -->
      <div v-else class="training-wrapper">
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progress + '%' }"></div>
        </div>
        <p class="training-message">{{ trainingMessage }}</p>
      </div>
    </div>

    <!-- ✅ 학습 완료 모달 -->
    <transition name="fade">
      <div v-if="showModal" class="modal-overlay">
        <div class="modal-content">
          <h2>학습 완료 🎉</h2>
          <p>AI 학습이 정상적으로 완료되었습니다.</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const isLoading = ref(false)
const error = ref(null)
const parentName = ref('홍길동')
const parentCode = ref('ABC123XYZ')
const copyButtonText = ref('복사')

const router = useRouter()

// 학습 상태
const isTraining = ref(false)
const progress = ref(0)
const trainingMessage = ref('')

// 모달
const showModal = ref(false)

const copyCode = () => {
  if (!parentCode.value) return
  navigator.clipboard.writeText(parentCode.value).then(() => {
    copyButtonText.value = '복사 완료!'
    setTimeout(() => { copyButtonText.value = '복사' }, 2000)
  })
}

const startService = () => {
  isTraining.value = true
  progress.value = 0
  trainingMessage.value = 'AI 학습중...'

  // 진행바 애니메이션
  const progressInterval = setInterval(() => {
    if (progress.value < 100) {
      progress.value += 2
    }
    // 6초 이상 걸리면 문구 바꿔줌
    if (progress.value > 60 && trainingMessage.value !== '잠시만 기다려주세요...') {
      trainingMessage.value = '잠시만 기다려주세요...'
    }
  }, 150)

  // 8초 뒤 학습 완료
  setTimeout(() => {
    clearInterval(progressInterval)
    progress.value = 100
    isTraining.value = false
    showModal.value = true

    // 2초 뒤 메인 페이지로 이동
    setTimeout(() => {
      router.push('/main_child')
    }, 2000)
  }, 8000)
}
</script>

<style scoped>
.welcome-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  font-family: sans-serif;
  background: #fcfcff;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  transform: translateY(-40px);
}

.hero-icon {
  width: 80px;
  height: 80px;
  color: #7f5bff;
  margin-bottom: 24px;
}

h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 16px;
  word-break: keep-all;
}

.description-group {
  color: #555;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 32px;
}

.code-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 420px;
  min-height: 80px;
  padding: 0 16px 0 32px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #eee;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

#parent-code {
  font-size: 26px;
  font-weight: 700;
  color: #212529;
  letter-spacing: 1px;
}

.btn-copy {
  font-size: 15px;
  font-weight: 600;
  color: #7f5bff;
  background-color: #f5f3ff;
  border: none;
  border-radius: 10px;
  padding: 12px 20px;
  cursor: pointer;
  margin-left: 24px;
}

.notice {
  font-size: 13px;
  color: #6c757d;
  margin-top: 24px;
}

.button-wrapper {
  margin-top: 32px;
}

.btn-primary {
  background-color: #7f5bff;
  color: white;
  border: none;
  padding: 16px 40px;
  font-weight: 700;
  font-size: 16px;
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(127, 91, 255, 0.3);
  transition: all 0.2s ease-in-out;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(127, 91, 255, 0.4);
}

/* 로딩바 */
.training-wrapper {
  margin-top: 40px;
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.progress-bar {
  width: 100%;
  height: 14px;
  background: #eee;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #7f5bff, #9f7fff);
  transition: width 0.2s ease;
}

.training-message {
  font-size: 15px;
  color: #444;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 40px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.3);
}

.modal-content h2 {
  font-size: 24px;
  margin-bottom: 12px;
  color: #7f5bff;
}

.modal-content p {
  color: #555;
  font-size: 15px;
}


/* ✅ 모달 페이드 인/아웃 애니메이션 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.fade-enter-to, .fade-leave-from {
  opacity: 1;
}
</style>
