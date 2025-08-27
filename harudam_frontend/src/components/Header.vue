<template>
  <header class="header" :class="{ 'scrolled': isScrolled }">
    <div class="header-left">
      <img
        src="@/assets/Harudam_logo.png"
        alt="하루담 로고"
        class="header-logo"
        :class="logoEffectClass"
        @mousedown="handleLogoClick"
        @mouseup="handleLogoRelease"
      />
    </div>

    <div class="header-right">
      <button class="header-button" @click="toggleNotification">
        <svg :class="{ 'has-notification': hasNotification, 'shake-effect': hasNotification }"
             viewBox="0 0 24 24" width="28" height="28"
             fill="none" stroke="#9ca3af" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
        </svg>
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';

const isScrolled = ref(false);
const hasNotification = ref(false);
const isLogoClicked = ref(false);
const logoEffects = ['bounce-effect', 'glow-effect', 'rotate-effect'];

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50;
};

const toggleNotification = () => {
  hasNotification.value = !hasNotification.value;
};

const handleLogoClick = () => {
  isLogoClicked.value = true;
};

const handleLogoRelease = () => {
  setTimeout(() => { isLogoClicked.value = false; }, 200);
};

const logoEffectClass = computed(() => {
  if (!isLogoClicked.value) return '';
  const i = Math.floor(Math.random() * logoEffects.length);
  return logoEffects[i];
});

onMounted(() => window.addEventListener('scroll', handleScroll));
onUnmounted(() => window.removeEventListener('scroll', handleScroll));
</script>

<style scoped>
:root {
  --accent: #5b3cc4;
  --accentLight: #9d7dff;
  --ink-weak: #9ca3af;
  --notification-yellow: #f8c04f;

  /* 선 색상(눈에 잘 띄게 약간 진하게 설정) */
  --divider: rgba(15, 23, 42, 0.16);
  --divider-strong: rgba(15, 23, 42, 0.22);
}

/* Header style */
.header {
  position: fixed;
  top: 0; left: 0;
  width: 100%;
  z-index: 10;

  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 16px 20px;
  background-color: #fff;

  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;

  /* ✅ 항상 보이는 인셋 하단선 + 기존 그림자 */
  box-shadow:
    0 4px 10px rgba(0, 0, 0, 0.05),
    inset 0 -1px 0 var(--divider);

  transition: all 0.3s ease-in-out;
}

.header.scrolled {
  padding: 10px 20px;
  background-color: rgba(255, 255, 255, 0.9);
  /* ✅ 스크롤 때 선도 살짝 강조 */
  box-shadow:
    0 2px 5px rgba(0, 0, 0, 0.1),
    inset 0 -1px 0 var(--divider-strong);
}

.header-left, .header-right {
  display: flex;
  align-items: center;
}

.header-logo {
  height: 36px;
  width: auto;
  cursor: pointer;
}
.header.scrolled .header-logo {
  height: 30px;
}

/* 클릭 이펙트 */
.header-logo.bounce-effect { animation: bounce 0.4s ease-out; }
@keyframes bounce {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}
.header-logo.glow-effect { animation: glow 0.4s ease-out; }
@keyframes glow {
  0% { filter: drop-shadow(0 0 0 rgba(255, 255, 255, 0)); }
  50% { filter: drop-shadow(0 0 10px rgba(255, 255, 255, 0.8)); }
  100% { filter: drop-shadow(0 0 0 rgba(255, 255, 255, 0)); }
}
.header-logo.rotate-effect { animation: rotate 0.4s ease-out; }
@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Notification button */
.header-button {
  position: relative;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  outline: none;

  display: flex;
  align-items: center;
  justify-content: center;
}

/* Notification button SVG style */
.header-button svg {
  display: block;
  transition: stroke 0.2s ease;
}
.header-button svg.has-notification {
  stroke: var(--notification-yellow);
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
  transform: translate3d(0, 0, 0);
  backface-visibility: hidden;
  perspective: 1000px;
}
@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}
.header-button:hover svg {
  stroke: var(--accent);
}
</style>
