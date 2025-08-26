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
        chatButtonStyle.value = '';
    }
};

const goToMain = () => {
    router.push({ name: 'Onboarding' });
};
</script>

<style scoped>
.chatbot-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 2rem 5rem 5rem;
    font-family: sans-serif;
    color: #333;
}

.child-face-container {
    width: 70vw;
    height: 70vw;
    max-width: 300px;
    max-height: 300px;
    border-radius: 50%;
    overflow: hidden;
    margin-bottom: 4rem;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    background-color: #f0f0f0;
}

.child-face-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.chat-button {
    padding: 1.2rem 3.5rem;
    border: none;
    border-radius: 9999px;
    cursor: pointer;
    font-size: 1.8rem;
    font-weight: bold;
    color: white;
    background-color: #42b983; /* ✅ 기본 배경색을 초록색으로 설정 */
    transition: all 0.3s ease;
    margin-bottom: 2rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.chat-button:hover {
    filter: brightness(1.1);
    transform: translateY(-2px);
}

.chatbot-area {
    width: 90%;
    max-width: 600px;
    height: 55vh;
    max-height: 600px;
    border: 2px solid #ddd;
    border-radius: 15px;
    margin-top: 2rem;
    padding: 2rem;
    overflow-y: auto;
    background-color: #f9f9f9;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
    display: flex;
    justify-content: center;
    align-items: center;
}

.chat-placeholder {
    font-size: 1.5rem;
    color: #999;
    text-align: center;
}

.main-page-button {
    margin-top: 3rem;
    padding: 1.2rem 4rem;
    border: none;
    border-radius: 9999px;
    background: linear-gradient(90deg, #ff9966 0%, #ff5e62 100%); /* ✅ 주황-분홍 그라데이션으로 변경 */
    cursor: pointer;
    font-size: 1.6rem;
    font-weight: bold;
    color: #fff;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
}

.main-page-button:hover {
    filter: brightness(1.1);
    transform: translateY(-2px);
}
</style>