<template>
  <main class="pl-screen">
    <header class="pl-header">
      <button class="pl-back" @click="goBack" aria-label="뒤로가기">
        <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
          <path
            d="M15 18l-6-6 6-6"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </button>
    </header>

    <section class="pl-hero">
      <img :src="bookPng" alt="하루담 로고" class="pl-logo" />
      <div class="pl-brand-group">
        <p class="pl-tagline">하루의 이야기를 담는다</p>
        <strong class="pl-brand">하루담</strong>
      </div>
    </section>

    <div class="pl-form-container">
      <p class="pl-guide">
        서비스 이용을 위한 <strong class="pl-em">인증코드</strong>를 아래에 입력해주세요.
      </p>

      <div class="pl-form" @submit.prevent="onVerify">
        <input
          id="code"
          v-model.trim="code"
          class="pl-input"
          type="text"
          placeholder="예시) VAS8456"
          maxlength="8"
          @input="code = code.toUpperCase()"
        />
        <button class="pl-btn" type="submit" :disabled="!isValid">인증하기</button>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import bookPng from '@/assets/onboarding/4.png'; // 경로 확인 필요

const code = ref('');
const isValid = computed(() => /^[A-Z0-9]{4,7}$/.test(code.value));

const router = useRouter();
function onVerify() {
  if (!isValid.value) return;
  // TODO: 실제 인증 로직을 붙이세요.
  router.push({ name: 'signup' });
}
function goBack() {
  if (window.history.length > 1) router.back();
  else router.push({ name: 'Onboarding' });
}
</script>

<style>
/* Dongle 폰트 import */
@import url('https://fonts.googleapis.com/css2?family=Dongle:wght@400;700&display=swap');
</style>

<style scoped>
.pl-screen {
  position: fixed;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #fff;
  padding: 20px;
}

.pl-header {
  position: absolute;
  top: 10px;
  left: 12px;
  z-index: 10;
}

.pl-back {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: 9999px;
  background: #ffffff;
  border: 1px solid #e5e5e5;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
  color: #3a3a3a;
  cursor: pointer;
}

.pl-hero {
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  gap: 12px;
  margin-bottom: 60px;
}

.pl-logo {
  width: 110px;
  height: auto;
}

.pl-brand-group {
  display: flex;
  flex-direction: column;
}

.pl-tagline {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: #5f5a6b;
}

.pl-brand {
  font-family: 'Dongle', sans-serif;
  font-size: 90px;
  font-weight: 700;
  color: #8b6be2;
  margin-top: 2px;
}

.pl-form-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 360px;
  text-align: center;
}

.pl-guide {
  font-size: 16px;
  color: #111;
  line-height: 1.5;
  margin-bottom: 24px;
}

.pl-guide .pl-em {
  font-weight: 900;
}

.pl-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
  width: 100%;
}

.pl-input {
  padding: 16px 20px;
  border: 1px solid #ddd;
  border-radius: 12px;
  font-size: 16px;
  box-sizing: border-box;
  text-align: center;
}

.pl-input::placeholder {
  color: #bdbdbd;
  font-weight: 700;
}

.pl-btn {
  padding: 12px 0; /* 상하 패딩을 줄여 높이 감소 */
  border: 0;
  border-radius: 9999px;
  font-family: 'Dongle', system-ui, sans-serif;
  font-size: 36px; /* 폰트 크기를 줄여 전체적인 크기 감소 */
  font-weight: 700;
  color: #000000;
  background: linear-gradient(90deg, #9ec7ff 0%, #c7a4ff 100%);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.pl-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (prefers-color-scheme: dark) {
  .pl-back {
    background: #15151a;
    border-color: #2a2a35;
    color: #e7e6ff;
  }
}
</style>
