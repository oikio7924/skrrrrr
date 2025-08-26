<!-- src/views/Main_Parent.vue -->
<template>
  <div class="min-h-screen w-screen bg-slate-50">
    <div class="flex flex-col min-h-screen">
      <!-- 상단 로고 -->
      <header class="py-4 md:py-6 flex justify-center">
        <img
          :src="logoUrl"
          alt="하루담 로고"
          class="h-12 sm:h-14 md:h-16 object-contain"
          @error="onLogoError"
        />
      </header>

      <!-- 본문: 3개의 큰 버튼 (풀스크린) -->
      <section class="flex-1 px-4 sm:px-5 md:px-10 pb-[env(safe-area-inset-bottom)]">
        <div class="grid grid-rows-3 gap-4 sm:gap-5 md:gap-7 h-full">
          <!-- 대화하기 = 라벤더 -->
          <RouterLink
            to="/chat"
            aria-label="대화하기로 이동"
            class="rounded-3xl bg-violet-300 text-white ring-1 ring-black/10 focus:outline-none focus:ring-4 focus:ring-violet-200
                   flex items-center justify-between px-5 sm:px-6 md:px-10"
          >
            <span class="label font-extrabold tracking-tight leading-none text-[2.5rem] sm:text-[3rem] md:text-[3.75rem]">
              대화하기
            </span>
            <svg class="w-14 h-14 sm:w-16 sm:h-16 md:w-20 md:h-20 -mr-1"
                 viewBox="0 0 24 24" fill="none"
                 stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <rect x="3.5" y="3.5" width="17" height="13" rx="5"></rect>
              <path d="M9 16.5 L5 19.5 V15.8"></path>
              <circle cx="9" cy="10" r="1.4" fill="white" stroke="none"></circle>
              <circle cx="12" cy="10" r="1.4" fill="white" stroke="none"></circle>
              <circle cx="15" cy="10" r="1.4" fill="white" stroke="none"></circle>
            </svg>
          </RouterLink>

          <!-- 그림일기 = 핑크 -->
          <RouterLink
            to="/diary"
            aria-label="그림일기로 이동"
            class="rounded-3xl bg-pink-300 text-white ring-1 ring-black/10 focus:outline-none focus:ring-4 focus:ring-pink-200
                   flex items-center justify-between px-5 sm:px-6 md:px-10"
          >
            <span class="label font-extrabold tracking-tight leading-none text-[2.5rem] sm:text-[3rem] md:text-[3.75rem]">
              그림일기
            </span>
            <svg class="w-14 h-14 sm:w-16 sm:h-16 md:w-20 md:h-20 -mr-1" viewBox="0 0 24 24" fill="none"
                 stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <path d="M12 6c-2.8-1.4-5.6-1.4-8.5 0v11c2.9-1.4 5.7-1.4 8.5 0"></path>
              <path d="M12 6c2.8-1.4 5.6-1.4 8.5 0v11c-2.9-1.4-5.7-1.4-8.5 0"></path>
              <line x1="12" y1="6" x2="12" y2="17"></line>
            </svg>
          </RouterLink>

          <!-- 일정보기 = 노랑(amber-400), 알림 ON이면 rose-500 + 흔들림 -->
          <RouterLink
            to="/calendar"
            aria-label="일정보기로 이동"
            class="rounded-3xl bg-amber-400 text-white ring-1 ring-black/10 focus:outline-none focus:ring-4 focus:ring-amber-300
                   flex items-center justify-between px-5 sm:px-6 md:px-10"
          >
            <span
              class="label font-extrabold tracking-tight leading-none text-[2.5rem] sm:text-[3rem] md:text-[3.75rem]"
              :class="isAlert ? 'text-rose-600' : ''"
            >
              일정보기
            </span>
            <svg
              class="w-14 h-14 sm:w-16 sm:h-16 md:w-20 md:h-20 -mr-1"
              :class="[isAlert ? ['text-rose-600','bell-ringing'] : 'text-white']"
              viewBox="0 0 20 20" fill="currentColor" aria-hidden="true"
            >
              <path d="M10 2a6 6 0 00-6 6v3.586l-1.707 1.707A1 1 0 003 15h14a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z"/>
            </svg>
          </RouterLink>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import logoUrlSrc from '@/assets/Harudam_logo.png'

// 알림 연동 전: false (나중에 Pinia/서버 상태로 교체)
const isAlert = false

function onLogoError(e: Event) {
  const img = e.target as HTMLImageElement
  img.alt = 'Harudam'
}
const logoUrl = logoUrlSrc
</script>

<style scoped>
@keyframes bell-ring {
  0% { transform: rotate(0deg) }
  10% { transform: rotate(14deg) }
  20% { transform: rotate(-12deg) }
  30% { transform: rotate(10deg) }
  40% { transform: rotate(-8deg) }
  50% { transform: rotate(6deg) }
  60% { transform: rotate(-4deg) }
  70% { transform: rotate(2deg) }
  80% { transform: rotate(-1deg) }
  90% { transform: rotate(0.5deg) }
  100% { transform: rotate(0deg) }
}
.bell-ringing { animation: bell-ring 1.25s ease-in-out infinite; transform-origin: 50% 8% }
@media (prefers-reduced-motion: reduce) {
  .bell-ringing { animation: none }
}
.label { text-shadow: 0 1px 0 rgba(0,0,0,.16) }
</style>
