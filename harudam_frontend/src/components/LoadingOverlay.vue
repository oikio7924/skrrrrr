<!-- src/components/LoadingOverlay.vue -->
<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="show" class="overlay" role="status" aria-live="polite">
        <div class="stack">
          <img :src="logo" alt="하루담 로고" class="logo" draggable="false" />
          <h1 class="brand">하루담</h1>
          <p class="msg">LODING...</p>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
defineProps<{ show: boolean }>()
import logo from '@/assets/Harudam_logo.png'
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');

/* 화면 전체를 살짝 덮되 뒤가 비치도록 */
.overlay{
  position: fixed; inset: 0; z-index: 9000;
  background: rgba(255,255,255,.36);   /* ← 투명도: 더 투명하게 하려면 0.28 정도 */
  -webkit-backdrop-filter: blur(8px);
  backdrop-filter: blur(8px);
  display: grid; place-items: center;
  padding: 24px;
}

/* ✅ 카드 제거: 경계가 안 보이게 완전 투명한 컨테이너 */
.stack{
  display:flex; flex-direction:column; align-items:center; text-align:center;
  gap: 10px;
  background: transparent;   /* no card */
  border: none;
  box-shadow: none;
  padding: 0;
}

/* 로고 모션(변수로 강도 조절 가능) */
.logo{
  --amp: 28px; --rot: 4deg; --scale: 1.06;
  width: clamp(140px, 32vw, 260px);
  max-height: 36vh;
  transform-origin: 50% 75%;
  will-change: transform, filter;
  animation: floatXL 2.6s cubic-bezier(.34,1.56,.64,1) infinite;
  filter: drop-shadow(0 10px 16px rgba(124,58,237,.22));
  user-select:none;
}
@keyframes floatXL{
  0%{transform:translateY(0) rotate(0) scale(1)}
  20%{transform:translateY(calc(-1*var(--amp))) rotate(calc(-1*var(--rot)))}
  50%{transform:translateY(calc(-.45*var(--amp))) rotate(calc(.5*var(--rot))) scale(var(--scale))}
  80%{transform:translateY(calc(-1*var(--amp))) rotate(var(--rot))}
  100%{transform:translateY(0) rotate(0) scale(1)}
}

/* 텍스트는 카드 없이도 읽히도록 살짝 그림자만 */
.brand{
  margin-top: 6px;
  font-family:'Jua','Noto Sans KR',system-ui,-apple-system,Segoe UI,Roboto,'Apple SD Gothic Neo','Malgun Gothic',sans-serif;
  font-weight:800; font-size:28px; color:#7c3aed;
  text-shadow: 0 1px 2px rgba(255,255,255,.6);
  letter-spacing:.02em;
}
.msg{
  font-family:'Jua','Noto Sans KR',system-ui,-apple-system,Segoe UI,Roboto,'Apple SD Gothic Neo','Malgun Gothic',sans-serif;
  font-size:15px; color:#0f172a; opacity:.95;
  text-shadow: 0 1px 2px rgba(255,255,255,.5);
}

/* 페이드 인/아웃 */
.fade-enter-from,.fade-leave-to{ opacity:0 }
.fade-enter-active,.fade-leave-active{ transition: opacity .25s ease }

/* 모션 최소화 환경 존중 */
@media (prefers-reduced-motion: reduce){
  .logo{ animation:none; filter:none }
}
</style>
