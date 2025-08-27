<template>
  <main class="min-h-screen w-screen grid place-items-center p-6 bg-slate-50">
    <section class="w-full max-w-md bg-white rounded-2xl border shadow-lg p-6 text-center">
      <h1 class="text-xl font-bold text-slate-900">LoadingOverlay 데모</h1>
      <p class="mt-1 text-slate-500 text-sm">버튼을 눌러 오버레이가 화면 위로 뜨는지 확인하세요.</p>

      <div class="mt-6 grid grid-cols-3 gap-3">
        <button class="btn" @click="run(1000)">1초</button>
        <button class="btn" @click="run(2000)">2초</button>
        <button class="btn" @click="run(3000)">3초</button>

        <button class="btn ghost col-span-2" @click="start">로딩 시작</button>
        <button class="btn ghost" @click="stop">종료</button>
      </div>

      <RouterLink to="/" class="link mt-6 inline-block">홈으로</RouterLink>
    </section>
  </main>
</template>

<script setup lang="ts">
import { useUiStore } from '@/stores/ui'
const ui = useUiStore()

function start(){ ui.start() }
function stop(){ ui.stop() }
async function run(ms:number){
  ui.start()
  await new Promise(r => setTimeout(r, ms))
  ui.stop()
}
</script>

<style scoped>
.btn{
  height: 44px; border-radius: 12px; border: 0;
  background: linear-gradient(90deg, #A78BFA, #8B5CF6);
  color:#fff; font-weight:700; cursor:pointer;
}
.btn.ghost{
  background:#fff; color:#6D28D9; border:1px solid #DDD6FE;
}
.link{ color:#6D28D9; font-weight:600; }
</style>

// 테스트용
