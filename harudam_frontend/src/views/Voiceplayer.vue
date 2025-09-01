<template>
  <!-- ✅ [신규] 전체 페이지를 감싸는 최상위 컨테이너 -->
  <div class="voice-player-container">
    <header class="topbar">
      <button class="icon-btn" @click="$router.back()" aria-label="뒤로가기">
        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="#9378d5"
          stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 18l-6-6 6-6" />
        </svg>
      </button>
      <h1 class="title">자녀 AI 목소리 학습</h1>
      <div style="width: 28px"></div>
    </header>

    <!-- ✅ [수정] 기존 voice-player-page를 main-content로 변경하여 스크롤 영역 지정 -->
    <main class="main-content">
      <div class="track-info">
        <h2 class="track-title">{{ currentFile.name }}</h2>
      </div>

      <div class="lp-wrapper">
        <div class="lp" :class="{ spinning: isPlaying }">
          <div class="lp-hole"></div>
        </div>
      </div>

      <!-- ✅ [수정] waveform과 time-display를 하나의 그룹으로 묶음 -->
      <div class="playback-progress">
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
      </div>


      <div class="controls">
        <div class="control-item">
          <button class="icon-btn" @click="togglePlay">
            <svg v-if="!isPlaying" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
              stroke="currentColor" stroke-width="2.5" width="36" height="36">
              <path stroke-linecap="round" stroke-linejoin="round" d="M5 3l14 9-14 9V3z" />
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"
              stroke-width="2.5" width="36" height="36">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 4h4v16H6zM14 4h4v16h-4z" />
            </svg>
          </button>
          <span class="label">재생/일시정지</span>
        </div>
        <div class="control-item">
          <button class="icon-btn" @click="showList = true">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor"
              stroke-width="2.5" width="36" height="36">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <span class="label">목록</span>
        </div>
        <div class="control-item">
          <!-- ✅ [수정] 아이콘을 체크 표시 아이콘으로 변경 -->
          <button class="icon-btn primary" @click="useThisVoice">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.2"
              stroke="currentColor" width="36" height="36">
              <path stroke-linecap="round" stroke-linejoin="round"
                d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
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
import apiClient from "@/api/http"

const router = useRouter()

const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const audio = ref<HTMLAudioElement | null>(null)


const showList = ref(false)
import { useRoute } from "vue-router"
const route = useRoute()

const currentFile = ref({
  id: 1,
  name: (route.query.name as string) || "테스트 음성",
  url: route.query.url as string || ""
})

const savedFiles = ref<any[]>([])

onMounted(async () => {
  // 실제로는 여기서 API를 호출하여 학습된 목소리 목록을 가져옵니다.
  // 예:
  // try {
  //   const response = await apiClient.get('/voices');
  //   savedFiles.value = response.data;
  //   if (savedFiles.value.length > 0) {
  //     currentFile.value = savedFiles.value[0]; // 첫 번째 파일을 기본으로 재생
  //   }
  // } catch (error) {
  //   console.error("목소리 목록 로딩 실패:", error);
  // }

  // 아래는 API 호출을 시뮬레이션하는 더미 데이터입니다.
  savedFiles.value = [
    { id: 1, name: "2025-08-29 14:21", length: 200, url: "/voice1.wav" },
    { id: 2, name: "2025-08-28 19:10", length: 180, url: "/voice2.wav" },
    { id: 3, name: "2025-08-27 11:45", length: 240, url: "/voice3.wav" },
  ];
});

const bars = Array.from({ length: 40 }, () => Math.floor(Math.random() * 60) + 20)
const playedBars = computed(() =>
  duration.value > 0 ? Math.floor((currentTime.value / duration.value) * bars.length) : 0
)

function goToExtraTraining() {
  router.push('/mypage/childvoicetraining')
}

function setupAudioEvents() {
  if (!audio.value) return;
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



const voiceId = route.query.voiceId as string

async function togglePlay() {
  if (!audio.value) {
    try {
      // FastAPI에서 음성 파일 받아오기
      const res = await apiClient.get("/speak", {
        params: {
          voice_id: voiceId,
          text: "안녕하세요. 학습된 제 목소리입니다."
        },
        responseType: "blob"
      })

      const blob = new Blob([res.data], { type: "audio/mpeg" })
      const url = URL.createObjectURL(blob)
      audio.value = new Audio(url)
      setupAudioEvents()
    } catch (err) {
      console.error("음성 재생 실패:", err)
      return
    }
  }

  if (isPlaying.value) {
    audio.value.pause()
  } else {
    await audio.value.play()
  }
  isPlaying.value = !isPlaying.value
}


function formatTime(sec: number) {
  if (!sec || isNaN(sec)) return "0:00"
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${m}:${s.toString().padStart(2, "0")}`
}

function useThisVoice() {
  alert(`${currentFile.value.name} 음성을 최종 선택했습니다.`)
  // 실제로는 여기서 API로 선택 정보를 서버에 전송합니다.
  // 예: await apiClient.post('/voices/select', { voiceId: currentFile.value.id });
}

function playFile(file: any) {
  currentFile.value = file
  isPlaying.value = false; // 재생 상태 초기화
  togglePlay() // 새 파일 재생
  showList.value = false
}
</script>

<style scoped>
/* ===== ✅ [신규] 전체 레이아웃 구조 ===== */
.voice-player-container {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.topbar {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  z-index: 10;
  box-sizing: border-box;
}

.title {
  font-size: 20px;
  font-weight: 700;
}

/* ✅ [수정] 메인 콘텐츠 영역 (스크롤 담당) */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow-y: auto;
  padding-bottom: 7rem;
  /* FooterNav 높이 확보 */
  /* ✅ [추가] 내부 요소들을 수평 중앙 정렬 */
  align-items: center;
}

.track-info,
.lp-wrapper,
.playback-progress,
/* ✅ [수정] .waveform과 .time-display 대신 부모 요소를 사용 */
.controls,
.extra-btn {
  width: 100%;
  max-width: 480px;
  box-sizing: border-box;
}


/* --- 기존 스타일 유지 --- */
.track-info {
  text-align: center;
  margin: 16px 0;
}

.track-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.lp-wrapper {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.lp {
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: conic-gradient(from 0deg, #e0c3fc, #8ec5fc, #fbc2eb, #a1c4fd, #c2e9fb, #e0c3fc);
  animation: aurora 8s ease-in-out infinite alternate;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.lp-hole {
  width: 40px;
  height: 40px;
  background: #fff;
  border-radius: 50%;
}

.spinning {
  animation: spin 6s linear infinite, aurora 8s ease-in-out infinite alternate;
}

@keyframes spin {
  0% {
    transform: rotate(0);
  }

  100% {
    transform: rotate(360deg);
  }
}

@keyframes aurora {
  0% {
    filter: hue-rotate(0deg) brightness(1);
  }

  50% {
    filter: hue-rotate(50deg) brightness(1.1);
  }

  100% {
    filter: hue-rotate(100deg) brightness(0.95);
  }
}

/* ✅ [수정] 재생 바 영역 그룹 */
.playback-progress {
  display: flex;
  flex-direction: column;
  /* ✅ [수정] 컨트롤 버튼과의 간격을 더 확보 */
  margin-bottom: 40px;
  position: relative;
  padding: 0 20px;
  /* 좌우 여백 확보 */
}


.waveform {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 4px;
  height: 80px;
}

.bar {
  width: 5px;
  border-radius: 2px;
  transition: background 0.3s;
}

/* ✅ [수정] time-display를 absolute로 변경하여 위치 조정 */
.time-display {
  position: absolute;
  bottom: -20px;
  /* waveform 아래로 20px 내림 */
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #555;
  padding: 0 20px;
  /* 부모와 동일한 좌우 여백 */
}

.controls {
  display: grid;
  /* ✅ [수정] Grid layout으로 변경하여 완벽한 중앙 정렬 */
  grid-template-columns: repeat(3, 1fr);
  /* 3개의 동일한 너비의 열 생성 */
  justify-items: center;
  /* 각 열의 아이템을 수평 중앙 정렬 */
  margin-bottom: 24px;
}

.control-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.label {
  font-size: 13px;
  color: #444;
  font-weight: 500;
  text-align: center;
}

.icon-btn {
  background: none;
  border: none;
  color: #9378d5;
  cursor: pointer;
}

.icon-btn.primary {
  color: #9378d5;
}

.extra-btn {
  text-align: center;
  margin-top: 10px;
  margin-bottom: 24px;
}

.btn-cta {
  width: 100%;
  max-width: 360px;
  padding: 16px 0;
  border: none;
  border-radius: 12px;
  background: #9378d5;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 6px 18px rgba(147, 120, 213, 0.4);
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
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
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
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
</style>
