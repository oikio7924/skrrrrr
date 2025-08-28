<template>
    <header class="topbar">
    <button class="icon-btn" @click="$router.back()" aria-label="뒤로가기">
      <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="#9378d5" stroke-width="2">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15 18l-6-6 6-6" />
      </svg>
    </button>
    <h1 class="title">자녀 AI 목소리 학습</h1>
    <div style="width: 28px"></div>
  </header>
  <div class="voice-player-page">
    <main class="content-wrapper">
      <div class="track-info">
        <h2 class="track-title">{{ currentFile.name }}</h2>
      </div>

      <div class="lp-wrapper">
        <div class="lp" :class="{ spinning: isPlaying }">
          <div class="lp-hole"></div>
        </div>
      </div>

      <div class="waveform">
        <div v-for="(bar, i) in bars" :key="i" class="bar" :style="{
          height: bar + 'px',
          background: i <= playedBars ? '#b89cff' : '#e8ddff'
        }"></div>
      </div>

      <div class="time-display">
        <span>{{ formatTime(currentTime) }}</span>
        <span>{{ formatTime(duration) }}</span>
      </div>

      <div class="controls">
        <div class="control-item">
          <button class="icon-btn" @click="togglePlay">
            <svg v-if="!isPlaying" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" width="36" height="36"><path stroke-linecap="round" stroke-linejoin="round" d="M5 3l14 9-14 9V3z" /></svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" width="36" height="36"><path stroke-linecap="round" stroke-linejoin="round" d="M6 4h4v16H6zM14 4h4v16h-4z" /></svg>
          </button>
          <span class="label">재생/일시정지</span>
        </div>
        <div class="control-item">
          <button class="icon-btn" @click="showList = true">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" width="36" height="36"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" /></svg>
          </button>
          <span class="label">목록</span>
        </div>
        <div class="control-item">
          <button class="icon-btn primary" @click="useThisVoice">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.2" width="36" height="36"><path stroke-linecap="round" stroke-linejoin="round" d="M11 5l-6 6h-3v2h3l6 6V5zM19.07 4.93a10 10 0 010 14.14M16.24 7.76a6 6 0 010 8.48" /></svg>
          </button>
          <span class="label">이 목소리로 사용</span>
        </div>
      </div>

      <div class="extra-btn">
        <button class="btn-cta" @click="goToExtraTraining">추가 학습하기</button>
      </div>
    </main>

    <div v-if="showList" class="modal-backdrop" @click.self="showList = false">
      <div class="modal">
        <h3 class="modal-title">학습된 목소리 목록</h3>
        <ul class="file-list">
          <li v-if="savedFiles.length === 0" class="empty-list">
            저장된 목소리가 없습니다.
          </li>
          <li v-for="file in savedFiles" :key="file.id" class="file-item" @click="playFile(file)">
            <span>{{ file.name }}</span>
            <span>{{ formatTime(file.length) }}</span>
          </li>
        </ul>
        <button class="btn-cta close-btn" @click="showList = false">닫기</button>
      </div>
    </div>

    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import { useRouter } from 'vue-router'
import FooterNav from "@/components/FooterNav.vue"

const router = useRouter()

const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0) // ✅ 초기값 0으로 설정
const audio = ref<HTMLAudioElement | null>(null)

const showList = ref(false)
const currentFile = ref({ id: 1, name: "2025-08-29 14:21", length: 200, url: "/voice1.wav" })
const savedFiles = ref<any[]>([]) // ✅ 초기값 빈 배열로 설정

// [추가] DB에서 데이터를 가져오는 것을 시뮬레이션
onMounted(() => {
  // 실제로는 여기서 axios 등으로 API를 호출합니다.
  // 예시: const response = await api.get('/voices'); savedFiles.value = response.data;
  savedFiles.value = [
    { id: 1, name: "2025-08-29 14:21", length: 200, url: "/voice1.wav" },
    { id: 2, name: "2025-08-28 19:10", length: 180, url: "/voice2.wav" },
    { id: 3, name: "2025-08-27 11:45", length: 240, url: "/voice3.wav" },
  ];
  // 만약 DB에서 받아온 데이터가 없다면, savedFiles.value는 빈 배열([])이 됩니다.
});


// 파형
const bars = Array.from({ length: 40 }, () => Math.floor(Math.random() * 60) + 20)
const playedBars = computed(() =>
  duration.value > 0 ? Math.floor((currentTime.value / duration.value) * bars.length) : 0
)

function goToExtraTraining() {
  router.push('/mypage/childvoicetraining')
}

function togglePlay() {
  if (!audio.value) {
    audio.value = new Audio(currentFile.value.url)
    // ✅ 오디오 파일의 메타데이터가 로드되면 실제 길이를 duration에 반영
    audio.value.addEventListener("loadedmetadata", () => {
      duration.value = audio.value!.duration
    })
    audio.value.addEventListener("timeupdate", () => {
      currentTime.value = audio.value!.currentTime
    })
    audio.value.addEventListener("ended", () => {
      isPlaying.value = false
      currentTime.value = 0
    })
  }
  if (isPlaying.value) {
    audio.value.pause()
  } else {
    audio.value.play()
  }
  isPlaying.value = !isPlaying.value
}

function formatTime(sec: number) {
  if (!sec || isNaN(sec)) return "0:00"
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${m}:${s.toString().padStart(2, "0")}`
}

function useThisVoice() { alert("이 음성을 최종 선택했습니다") }

// [수정] 목록에서 파일 재생 시 모달 닫기
function playFile(file: any) {
  if (audio.value) {
    audio.value.pause()
    isPlaying.value = false
  }
  currentFile.value = file
  // 새 파일을 재생하기 위해 togglePlay 호출
  togglePlay()
  showList.value = false // ✅ 모달 닫기
}
</script>

<style scoped>

.topbar { position: sticky; top: 0; left: 0; width: 100%; margin: 0; display: flex; align-items: center; justify-content: space-between; height: 64px; padding: 0 20px; background: #fff; border-bottom: 1px solid #eee; z-index: 100; box-sizing: border-box; }
.title { font-size: 20px; font-weight: 700; }
.voice-player-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fafafa;
}

/* [추가] 컨텐츠를 감싸고 레이아웃 조정 */
.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: 20px; /* ✅ 전체적으로 살짝 내림 */
  overflow-y: auto;
}

.topbar, .track-info, .lp-wrapper, .waveform, .time-display, .controls, .extra-btn {
  width: 100%;
  max-width: 480px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 20px;
  padding-right: 20px;
  box-sizing: border-box;
}

/* ... 기존 스타일 ... */

/* [추가] 모달 스타일 */
.modal-backdrop {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.3);
}
.modal-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 20px;
  text-align: center;
}
.file-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 50vh;
  overflow-y: auto;
}
.file-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 15px;
  cursor: pointer;
}
.file-item:hover {
  background: #f8f5ff;
}
.empty-list {
  text-align: center;
  color: #888;
  padding: 20px 0;
}
.close-btn {
  margin-top: 24px;
  height: 48px;
}

/* --- 기존 스타일에서 일부 가져옴 --- */
.track-info { text-align: center; margin: 16px 0; }
.track-title { font-size: 18px; font-weight: 600; color: #333; }
.lp-wrapper { display: flex; justify-content: center; margin: 20px 0; }
.lp { width: 220px; height: 220px; border-radius: 50%; background: conic-gradient(from 0deg, #e0c3fc, #8ec5fc, #fbc2eb, #a1c4fd, #c2e9fb, #e0c3fc); animation: aurora 8s ease-in-out infinite alternate; display: flex; align-items: center; justify-content: center; box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2); }
.lp-hole { width: 40px; height: 40px; background: #fff; border-radius: 50%; }
.spinning { animation: spin 6s linear infinite, aurora 8s ease-in-out infinite alternate; }
@keyframes spin { 0% { transform: rotate(0); } 100% { transform: rotate(360deg); } }
@keyframes aurora { 0% { filter: hue-rotate(0deg) brightness(1); } 50% { filter: hue-rotate(50deg) brightness(1.1); } 100% { filter: hue-rotate(100deg) brightness(0.95); } }
.waveform { display: flex; justify-content: center; align-items: flex-end; gap: 4px; height: 80px; }
.bar { width: 5px; border-radius: 2px; transition: background 0.3s; }
.time-display { display: flex; justify-content: space-between; font-size: 14px; color: #555; margin-top: 8px; margin-bottom: 20px;}
.controls { display: flex; justify-content: center; gap: 60px; margin-bottom: 24px; }
.control-item { display: flex; flex-direction: column; align-items: center; gap: 6px; }
.label { font-size: 13px; color: #444; font-weight: 500; text-align: center; }
.icon-btn { background: none; border: none; color: #9378d5; cursor: pointer; }
.icon-btn.primary { color: #9378d5; }
.extra-btn { text-align: center; margin-top: 10px; margin-bottom: 24px; }
.btn-cta { width: 100%; max-width: 360px; padding: 16px 0; border: none; border-radius: 12px; background: #9378d5; color: #fff; font-size: 18px; font-weight: 700; cursor: pointer; box-shadow: 0 6px 18px rgba(147, 120, 213, 0.4); }
</style>
