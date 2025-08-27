<!-- src/views/Update.vue -->
<template>
  <main
    class="update-root min-h-screen w-screen bg-white flex items-center justify-center p-6 pb-[env(safe-area-inset-bottom)]"
  >
    <div class="wrap">
      <!-- 로고 -->
      <img
        :src="logo"
        alt="하루담 로고"
        class="logo"
        draggable="false"
      />

      <!-- 안내 문구 -->
      <h1 class="headline">페이지를 준비 중입니다.</h1>
      <p class="sub">추후 업데이트 진행 예정</p>

      <!-- 메인으로 돌아가기 -->
      <button type="button" class="btn-main mt-6" @click="goMain">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"
             fill="currentColor" class="w-5 h-5">
          <path fill-rule="evenodd"
                d="M9.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4A1 1 0 0110 6.586L7.414 9H16a1 1 0 110 2H7.414L10 13.414a1 1 0 01-.293 1.293z"
                clip-rule="evenodd"/>
        </svg>
        <span>메인으로 돌아가기</span>
      </button>
    </div>
  </main>
</template>

<script setup lang="ts">
import logo from '@/assets/Harudam_logo.png'
import { useRouter } from 'vue-router'

const router = useRouter()
const MAIN_PATH = '/main_child' // 프로젝트에 맞게 수정 가능

function goMain() {
  router.push(MAIN_PATH)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');

.update-root{
  font-family:'Jua','Noto Sans KR',-apple-system,BlinkMacSystemFont,
              'Apple SD Gothic Neo','Segoe UI',Roboto,'Helvetica Neue',
              Arial,'Malgun Gothic',sans-serif;
  font-variant-numeric: tabular-nums;
  --lav-400:#A78BFA;  /* violet-400 */
  --lav-500:#8B5CF6;  /* violet-500 */
}

.wrap{
  display:flex; flex-direction:column; align-items:center; text-align:center;
}

/* ========= 더 큰 애니메이션 =========
   - --amp: 상하 진폭(px)
   - --rot: 회전 각도(deg)
   - --scale: 호흡 스케일 배율
*/
.logo{
  --amp: 32px;     /* 20 → 32로 키움 (더 크게 하려면 40px 등으로) */
  --rot: 4deg;     /* 회전 폭 (5deg까지 권장) */
  --scale: 1.08;   /* 호흡 확대 비율 */

  width: clamp(150px, 34vw, 280px);
  max-height: 30vh;
  transform-origin: 50% 75%;
  will-change: transform, filter;
  animation: floatXL 2.8s cubic-bezier(.34,1.56,.64,1) infinite;
  filter: drop-shadow(0 10px 16px rgba(124, 58, 237, 0.22));
  user-select:none;
}

/* 하나의 키프레임에서 translate + rotate + scale을 함께 적용 */
@keyframes floatXL {
  0%   { transform: translateY(0) rotate(0) scale(1);
         filter: drop-shadow(0 10px 16px rgba(124,58,237,.22)); }
  20%  { transform: translateY(calc(-1 * var(--amp))) rotate(calc(-1 * var(--rot))) scale(1); }
  50%  { transform: translateY(calc(-0.45 * var(--amp))) rotate(calc(0.5 * var(--rot))) scale(var(--scale));
         filter: drop-shadow(0 16px 22px rgba(124,58,237,.30)); }
  80%  { transform: translateY(calc(-1 * var(--amp))) rotate(var(--rot)) scale(1); }
  100% { transform: translateY(0) rotate(0) scale(1);
         filter: drop-shadow(0 10px 16px rgba(124,58,237,.22)); }
}

/* 캘린더 느낌 타이포 */
.headline{
  margin-top: 2.5rem;              /* mt-10 */
  font-weight: 800;
  font-size: clamp(1.125rem, 1.4vw, 1.25rem);
  color:#0f172a;                   /* slate-900 */
  letter-spacing:-0.015em;
  line-height:1.2;
}
.sub{
  margin-top:.375rem;
  font-size:.875rem;               /* text-sm */
  color:#64748b;                   /* slate-500 */
  letter-spacing:.02em;
  font-weight:500;
}

/* 메인으로 돌아가기 버튼 */
.btn-main{
  display:flex; align-items:center; gap:.5rem;
  height: 44px; padding: 0 16px;
  border: 0; border-radius: 12px;
  background: linear-gradient(90deg, var(--lav-400), var(--lav-500));
  color: #fff; font-weight: 700;
  box-shadow: 0 10px 18px rgba(139,92,246,.28);
  cursor: pointer;
  transition: transform .06s ease, filter .2s ease, box-shadow .2s ease;
}
.btn-main:hover{ filter: brightness(1.06); box-shadow:0 12px 22px rgba(139,92,246,.32); }
.btn-main:active{ transform: translateY(1px); }

/* 모바일 100dvh 보정 */
@supports (height: 100dvh) { .min-h-screen { min-height: 100dvh; } }

/* 접근성: 모션 최소화 */
@media (prefers-reduced-motion: reduce) {
  .logo { animation: none; filter: none }
}
</style>
