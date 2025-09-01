<template>
  <header class="header">
    <!-- 왼쪽: 로고 + 텍스트 (클릭 시 홈으로 이동) -->
    <div class="header-left" @click="goHome">
      <img src="@/assets/Harudam_logo.png" alt="하루담 로고" class="header-logo" />
      <span class="header-logo-text">하루담</span>
    </div>

    <!-- 오른쪽: 알림 버튼 (기존 코드 기능 유지) -->
    <div class="header-right">
      <button class="header-button" @click="toggleNotification" aria-label="알림">
        <svg class="bell" :class="{ 'has-notification': hasNotification }" viewBox="0 0 24 24" width="28" height="28"
          fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
        </svg>
        <span v-if="hasNotification" class="dot"></span>
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const hasNotification = ref(true); // 알림이 있는 상태를 기본으로 설정

const goHome = () => {
  router.push('/main_child');
};

const toggleNotification = () => {
  hasNotification.value = !hasNotification.value;
};
</script>

<style scoped>
/* Google Fonts Dongle import */
@import url('https://fonts.googleapis.com/css2?family=Dongle:wght@700&display=swap');

/* ===== 공통 헤더 스타일 ===== */
.header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  /* 마이페이지 헤더와 패딩 통일 */
  background-color: #fff;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
}

.header-left {
  gap: 10px;
  cursor: pointer;
}

/* ✅ 로고 크기를 마이페이지 헤더와 동일하게 42px로 맞춤 */
.header-logo {
  height: 42px;
  width: 42px;
}

/* ✅ 로고 텍스트 스타일 통일 */
.header-logo-text {
  font-family: "Dongle", sans-serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: #9a7dff;
  /* 하루담 브랜드 색상 */
  line-height: 1;
  transform: translateY(6px);
}

/* ===== 알림 버튼 (기존 코드 디자인 유지) ===== */
.header-button {
  position: relative;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  outline: none;
}

.bell {
  color: #9ca3af;
  /* 기본 회색 아이콘 */
  transition: color .2s ease;
}

.bell.has-notification {
  color: #f59e0b;
  /* 알림 있을 때 노란색 */
  animation: shake .5s cubic-bezier(.36, .07, .19, .97) both;
}

@keyframes shake {
  10%, 90% { transform: translateX(-1px); }
  20%, 80% { transform: translateX(2px); }
  30%, 50%, 70% { transform: translateX(-4px); }
  40%, 60% { transform: translateX(4px); }
}

.dot {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: #f59e0b;
  box-shadow: 0 0 0 0 rgba(245, 158, 11, .6);
  animation: ping 1.6s infinite;
}

@keyframes ping {
  0% { box-shadow: 0 0 0 0 rgba(245, 158, 11, .6); }
  70% { box-shadow: 0 0 0 8px rgba(245, 158, 11, 0); }
  100% { box-shadow: 0 0 0 0 rgba(245, 158, 11, 0); }
}
</style>

