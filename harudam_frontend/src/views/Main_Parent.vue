<template>
  <div class="min-h-screen w-full bg-slate-50 overflow-x-hidden">
    <div class="flex flex-col min-h-screen">

      <!-- 로고 -->
      <header class="py-10 md:py-10 flex justify-center">
        <img :src="logoUrl" alt="하루담 로고" class="h-[10rem] sm:h-32 md:h-40 object-contain" @error="onLogoError" />
      </header>

      <!-- 본문 -->
      <section class="flex-1 flex flex-col items-center justify-center
              px-4 sm:px-6 md:px-10 pb-[env(safe-area-inset-bottom)]">
        <div class="w-full max-w-[420px] flex flex-col gap-6">

          <!-- 대화하기 -->
          <RouterLink to="/chat" aria-label="대화하기로 이동" class="h-[10rem] rounded-3xl bg-violet-300 text-white
          flex items-center justify-between px-8
          shadow-md focus:outline-none focus:ring-4 focus:ring-violet-200">
            <span class="font-extrabold tracking-tight leading-none text-5xl sm:text-6xl md:text-7xl">
              대화하기
            </span>
            <svg class="w-14 h-14 sm:w-16 sm:h-16 md:w-20 md:h-20 -mr-1" viewBox="0 0 24 24" fill="none" stroke="white"
              stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3.5" y="3.5" width="17" height="13" rx="5"></rect>
              <path d="M9 16.5 L5 19.5 V15.8"></path>
              <circle cx="9" cy="10" r="1.6" fill="white" stroke="none"></circle>
              <circle cx="12" cy="10" r="1.6" fill="white" stroke="none"></circle>
              <circle cx="15" cy="10" r="1.6" fill="white" stroke="none"></circle>
            </svg>
          </RouterLink>

          <!-- 그림일기 -->
          <RouterLink to="/diary_p" aria-label="그림일기로 이동" class="h-[10rem] rounded-3xl bg-pink-300 text-white
                  flex items-center justify-between px-8
                  shadow-md focus:outline-none focus:ring-4 focus:ring-pink-200">
            <span class="font-extrabold tracking-tight leading-none text-5xl sm:text-6xl md:text-7xl">
              그림일기
            </span>
            <svg class="w-14 h-14 sm:w-16 sm:h-16 md:w-20 md:h-20 -mr-1" viewBox="0 0 24 24" fill="none" stroke="white"
              stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 6c-2.8-1.4-5.6-1.4-8.5 0v11c2.9-1.4 5.7-1.4 8.5 0"></path>
              <path d="M12 6c2.8-1.4 5.6-1.4 8.5 0v11c-2.9-1.4-5.7-1.4-8.5 0"></path>
              <line x1="12" y1="6" x2="12" y2="17"></line>
            </svg>
          </RouterLink>

          <!-- 일정보기 -->
          <RouterLink to="/schedule_p" aria-label="일정보기로 이동" class="h-[10rem] rounded-3xl bg-amber-400 text-white
                  flex items-center justify-between px-8
                  shadow-md focus:outline-none focus:ring-4 focus:ring-amber-200">
            <span class="font-extrabold tracking-tight leading-none text-5xl sm:text-6xl md:text-7xl">
              일정보기
            </span>
            <svg class="w-14 h-14 sm:w-16 sm:h-16 md:w-20 md:h-20 -mr-1" viewBox="0 0 20 20" fill="currentColor">
              <path
                d="M10 2a6 6 0 00-6 6v3.586l-1.707 1.707A1 1 0 003 15h14a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
            </svg>
          </RouterLink>

        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import logoUrlSrc from '@/assets/Harudam_logo.png'
const logoUrl = logoUrlSrc
function onLogoError(e: Event) {
  (e.target as HTMLImageElement).alt = 'Harudam'
}
</script>

<style scoped>
.label {
  text-shadow: 0 1px 0 rgba(0, 0, 0, .16)
}
</style>
