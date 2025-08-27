<template>
  <header class="header" :class="{ 'scrolled': isScrolled }">
    <div class="header-left">
      <!-- 로고 -->
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
      <!-- 알림 버튼 -->
      <button class="header-button" @click="toggleNotification" aria-label="알림">
        <svg class="bell" :class="{ 'has-notification': hasNotification }"
             viewBox="0 0 24 24" width="28" height="28"
             fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
        </svg>
        <!-- ✅ 알림 dot -->
        <span v-if="hasNotification" class="dot"></span>
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

const handleScroll = () => { isScrolled.value = window.scrollY > 50; };
const toggleNotification = () => { hasNotification.value = !hasNotification.value; };
const handleLogoClick = () => { isLogoClicked.value = true; };
const handleLogoRelease = () => { setTimeout(() => { isLogoClicked.value = false; }, 200); };

const logoEffectClass = computed(() => {
  if (!isLogoClicked.value) return '';
  const idx = Math.floor(Math.random() * logoEffects.length);
  return logoEffects[idx];
});

onMounted(() => window.addEventListener('scroll', handleScroll));
onUnmounted(() => window.removeEventListener('scroll', handleScroll));
</script>

<style scoped>
:root {
  --accent: #5b3cc4;
  --accentLight: #9d7dff;
  --ink-weak: #9ca3af;
  --notification: #f59e0b;
  --divider: rgba(15, 23, 42, 0.16);
  --divider-strong: rgba(15, 23, 42, 0.22);
}

/* ===== Header ===== */
.header {
  position: fixed;
  top: 0; left: 0; width: 100%;
  z-index: 10;
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 20px;
  background-color: #fff;
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
  box-shadow:
    0 4px 10px rgba(0,0,0,0.05),
    inset 0 -1px 0 var(--divider);
  transition: all 0.3s ease-in-out;
}
.header.scrolled {
  padding: 10px 20px;
  background-color: rgba(255,255,255,0.9);
  box-shadow:
    0 2px 5px rgba(0,0,0,0.1),
    inset 0 -1px 0 var(--divider-strong);
}

.header-left, .header-right { display:flex; align-items:center; }

/* ===== Logo ===== */
.header-logo { height:36px; width:auto; cursor:pointer; }
.header.scrolled .header-logo { height:30px; }
.header-logo.bounce-effect { animation: bounce .4s ease-out; }
@keyframes bounce { 0%{transform:scale(1)} 50%{transform:scale(1.1)} 100%{transform:scale(1)} }
.header-logo.glow-effect { animation: glow .4s ease-out; }
@keyframes glow {
  0%{filter:drop-shadow(0 0 0 rgba(255,255,255,0))}
  50%{filter:drop-shadow(0 0 10px rgba(255,255,255,.8))}
  100%{filter:drop-shadow(0 0 0 rgba(255,255,255,0))}
}
.header-logo.rotate-effect { animation: rotate .4s ease-out; }
@keyframes rotate { 0%{transform:rotate(0deg)} 100%{transform:rotate(360deg)} }

/* ===== Notification Button ===== */
.header-button {
  position: relative;
  background:none; border:none;
  padding:0; cursor:pointer; outline:none;
  display:flex; align-items:center; justify-content:center;
}
.bell { transition: color .2s ease; color: var(--ink-weak); }
.bell.has-notification {
  color: var(--notification);
  animation: shake .5s cubic-bezier(.36,.07,.19,.97) both;
}
@keyframes shake {
  10%,90%{transform:translateX(-1px)}
  20%,80%{transform:translateX(2px)}
  30%,50%,70%{transform:translateX(-4px)}
  40%,60%{transform:translateX(4px)}
}
/* ✅ 알림 점 스타일 */
.dot {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 9px; height: 9px;
  border-radius: 50%;
  background: var(--notification);
  box-shadow: 0 0 0 0 rgba(245,158,11,.6);
  animation: ping 1.6s infinite;
}
@keyframes ping {
  0% { box-shadow: 0 0 0 0 rgba(245,158,11,.6); }
  70% { box-shadow: 0 0 0 8px rgba(245,158,11,0); }
  100%{ box-shadow: 0 0 0 0 rgba(245,158,11,0); }
}
</style>
