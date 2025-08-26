<template>
  <div class="chatbot-container">
    <div class="child-face-container">
      <img :src="childFaceImage" alt="자녀 얼굴" class="child-face-img">
    </div>

    <button
      :style="{ backgroundColor: chatButtonStyle }"
      @click="toggleChat"
      class="chat-button"
    >
      {{ chatButtonText }}
    </button>

    <div v-if="showChat" class="chatbot-area">
      <div class="chat-placeholder">챗봇 기능 구현 자리입니다.</div>
    </div>

    <button @click="goToMain" class="main-page-button">
      처음 화면 보기
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

// 이미지 경로를 동적으로 가져오는 Vite/Vue3 방식
const childFaceImage = new URL('@/assets/child/child-face.png', import.meta.url).href;

const router = useRouter();
const showChat = ref(false);
const chatButtonText = ref('대화 시작');
const chatButtonStyle = ref('');

const toggleChat = () => {
  showChat.value = !showChat.value;
  if (showChat.value) {
    chatButtonText.value = '대화 종료';
    chatButtonStyle.value = 'red';
  } else {
    chatButtonText.value = '대화 시작';
    chatButtonStyle.value = ''; // 기본 스타일로 돌아감
  }
};

const goToMain = () => {
  // 'Onboarding'은 router/index.ts 파일에 설정된 라우트의 name입니다.
  router.push({ name: 'Onboarding' }); 
};
</script>

<style scoped>
.chatbot-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 5rem; /* 전체 여백을 rem으로 조정 */
  font-family: sans-serif;
  color: #333;
}

.child-face-container {
  width: 70vw; /* 화면 너비의 70% */
  height: 70vw; /* 너비와 동일하게 설정하여 항상 정원 유지 */
  max-width: 300px; /* 너무 커지지 않도록 최대값 설정 */
  max-height: 300px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 4rem; /* rem 단위로 조정 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  background-color: #f0f0f0;
}

.child-face-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-button {
  padding: 1.5rem 3rem; /* rem 단위로 조정 */
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 2rem; /* rem 단위로 조정 */
  font-weight: bold;
  color: white;
  background-color: #42b983; /* Vue 초록색 */
  transition: background-color 0.3s ease;
  margin-bottom: 2rem;
}

.chat-button:hover {
  filter: brightness(1.1);
}

.chatbot-area {
  width: 90%; /* 화면 너비의 90%를 사용 */
  max-width: 600px; /* 너무 커지지 않도록 최대값 설정 */
  height: 50vh; /* 화면 높이의 50%를 사용 */
  max-height: 500px; /* 너무 커지지 않도록 최대값 설정 */
  border: 2px solid #ddd;
  border-radius: 15px;
  margin-top: 2rem;
  padding: 2rem;
  overflow-y: auto;
  background-color: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-placeholder {
  font-size: 1.5rem; /* rem 단위로 조정 */
  color: #999;
  text-align: center;
}

.main-page-button {
  margin-top: 3rem;
  padding: 1.5rem 4rem;
  border: 1px solid #ccc;
  border-radius: 10px;
  background-color: #f7f7f7;
  cursor: pointer;
  font-size: 1.5rem;
  font-weight: bold;
  color: #555;
}

.main-page-button:hover {
  background-color: #e0e0e0;
}
</style>