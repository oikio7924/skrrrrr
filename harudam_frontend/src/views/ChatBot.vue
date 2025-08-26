<template>
  <div id="chatbot-page" class="page" @keyup.esc="closeChat" tabindex="0">
    <!-- 중앙에 얼굴 크게 -->
    <div class="face-area">
      <img :src="childFaceImage" alt="자녀 얼굴" class="face-img" />
    </div>

    <!-- 대화 시작/종료 버튼 -->
    <main class="content">
      <button
        class="chat-cta"
        :class="showChat ? 'end' : 'start'"
        :aria-pressed="showChat.toString()"
        :aria-expanded="showChat.toString()"
        @click="toggleChat"
      >
        <span class="label">
          <span v-if="!showChat">▶️ 대화 시작</span>
          <span v-else>⏹️ 대화 종료</span>
        </span>
      </button>

      <section
        v-if="showChat"
        class="chat-panel"
        aria-label="대화 영역"
        role="region"
      >
        <div class="panel-header">
          <strong>대화창</strong>
          <button class="mini-close" @click="closeChat" aria-label="대화 종료">✕</button>
        </div>
        <div class="panel-body" role="log" aria-live="polite">
          <div class="placeholder">챗봇 기능 구현 자리입니다.</div>
        </div>
      </section>
    </main>

    <!-- 하단 고정: 처음 화면 보기 -->
    <footer class="bottombar">
      <button class="home" @click="goToMain">처음화면 보기</button>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

// 이미지 경로 (Vite)
const childFaceImage = new URL('@/assets/child/child-face.png', import.meta.url).href

const router = useRouter()
const showChat = ref(false)
const chatButtonText = ref('대화 시작')

const toggleChat = () => {
  showChat.value = !showChat.value
  chatButtonText.value = showChat.value ? '대화 종료' : '대화 시작'
}

const closeChat = () => {
  if (showChat.value) {
    showChat.value = false
    chatButtonText.value = '대화 시작'
  }
}

const goToMain = () => {
  router.push({ name: 'MainParent' })
}
</script>

<style scoped>
:root {
  --bg-color: #f6f7fb;
  --danger: #ef4444;
  --success: #22c55e;
  --text-light: #fff;
  --text-dark: #1f2937;
  --border: #e5e7eb;
  --shadow-md: 0 6px 18px rgba(0,0,0,.08);
  --radius-xl: 22px;
}

.page {
  min-height: 100dvh;
  max-width: 640px;
  margin: 0 auto;
  display: grid;
  grid-template-rows: 1fr auto auto;
  background: var(--bg-color);
}

/* 얼굴 크게 */
.face-area {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}
.face-img {
  width: 80vw;
  height: 80vw;
  max-width: 360px;
  max-height: 360px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 8px 16px rgba(0,0,0,0.15);
}

/* 버튼 */
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 20px;
}
.chat-cta {
  width: 100%;
  max-width: 620px;
  min-height: 64px;
  appearance: none;
  border: none;
  border-radius: 20px;
  font-size: 20px;
  font-weight: bold;
  color: var(--text-light, #fff);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-md);
}
.chat-cta.start {
  background-color: var(--success, #22c55e);
}
.chat-cta.end {
  background-color: var(--danger, #ef4444);
}
.chat-cta:hover {
  filter: brightness(1.1);
}

/* 챗 패널 */
.chat-panel {
  width: 100%;
  max-width: 620px;
  height: min(56vh, 520px);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  background: #fff;
  box-shadow: var(--shadow-md);
  overflow: auto; /* 내부 스크롤만 허용 */
  display: grid;
  grid-template-rows: auto 1fr;
}
.panel-header {
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:12px 16px;
  border-bottom: 1px solid var(--border);
  font-weight:700;
}
.mini-close {
  border:none;
  background:transparent;
  font-size:18px;
  cursor:pointer;
  color: #666;
}
.panel-body {
  padding: 18px;
  display:flex;
  align-items:center;
  justify-content:center;
  color: #999;
}

/* 하단바 */
.bottombar {
  position: sticky;
  bottom: 0;
  background: #fff;
  padding: 14px 16px 18px;
  box-shadow: 0 -1px 6px rgba(0,0,0,0.08);
}
.home {
  width: 100%;
  min-height: 64px;
  border: none;
  border-radius: 18px;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  background: linear-gradient(135deg, #ff8a4d, #ff6b2c);
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 107, 44, 0.35);
}
.home:hover {
  filter: brightness(1.1);
}
</style>
