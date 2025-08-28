<template>
  <!-- ✅ 타임라인 헤더 -->
  <header class="timeline-header">
    <button class="back-btn" @click="goBack" aria-label="뒤로가기">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
        class="icon">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
      </svg>
    </button>
    <h1 class="title">그림일기 피드</h1>
  </header>
  <div class="profile-banner"></div>

  <!-- 프로필 -->
  <div class="profile-info">
    <div class="profile-photo">사진</div>
    <h2 class="profile-name">{{ parent?.name || "이름 없음" }}</h2>
    <p class="profile-sub">{{ parent?.birth || "생년월일 없음" }}</p>
  </div>

  <!-- 통계 -->
  <div class="profile-stats">
    <div class="stat">
      <strong>{{ diaries.length }}</strong>
      <span>그림일기</span>
    </div>
    <div class="stat">
      <strong>D+{{ dDay }}</strong>
      <span>접속일</span>
    </div>
  </div>

  <div class="divider"></div>

  <!-- 그림일기 피드 -->
  <section class="diary-feed">
    <div v-for="(item, idx) in diaries" :key="idx" class="diary-card" @click="goToDiary(item)">
      <div class="diary-thumb"></div>
      <p class="diary-caption">그림일기 {{ item }}</p>
    </div>
  </section>

  <!-- Footer -->
  <FooterNav />
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue"
import { useRouter } from "vue-router"
import FooterNav from "@/components/FooterNav.vue"
import apiClient from "@/api/index"

const router = useRouter()

// 부모 정보 타입
type ParentInfo = {
  id: number
  name: string
  gender: "M" | "F"
  birth: string
  profileImage?: string
  joinedDate: string
}

// 그림일기 타입
type Diary = {
  id: number
  title: string
  date: string
  imageUrl: string
}

// ✅ 상태
const parent = ref<ParentInfo | null>(null)
const diaries = ref<Diary[]>([])

// ✅ 접속일(D+N)
const dDay = computed(() => {
  if (!parent.value?.joinedDate) return 0
  const joined = new Date(parent.value.joinedDate)
  const today = new Date()
  const diff = today.getTime() - joined.getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
})

// ✅ API 호출
const loadParentInfo = async () => {
  try {
    const { data } = await apiClient.get<ParentInfo>("/api/parent/1")
    parent.value = data
  } catch (e) {
    console.error("부모 정보 불러오기 실패", e)
  }
}

const loadDiaries = async () => {
  try {
    const { data } = await apiClient.get<Diary[]>("/api/diaries/parent/1")
    diaries.value = data
  } catch (e) {
    console.error("그림일기 불러오기 실패", e)
  }
}

onMounted(() => {
  loadParentInfo()
  loadDiaries()
})

// ✅ 라우팅
function goBack() {
  router.push({ name: "main_child" })
}

function goToDiary(diary: Diary) {
  router.push({ name: "profileDiaryDetail", params: { id: diary.id } })
}
</script>


<style>
/* 전역 리셋 */
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 100vh;
  background: #fff;
  overflow-x: hidden;
  font-family: 'Inter', sans-serif;
}

#app {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow-x: hidden;
}

/* 상단 배너 */
.profile-banner {
  width: 100%;
  height: 280px;
  background: linear-gradient(135deg, #e8e1ff, #c7b9ff, #dcd0ff);
  background-size: 200% 200%;
  animation: aurora 6s infinite alternate;
  border-bottom-left-radius: 50% 20%;
  border-bottom-right-radius: 50% 20%;
}

@keyframes aurora {
  0% {
    background-position: 0% 50%;
  }

  100% {
    background-position: 100% 50%;
  }
}

/* ✅ 타임라인 헤더 */
.timeline-header {
  position: fixed;            /* 스크롤 내려도 상단 고정 */
  top: 0;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;       /* 중앙에 타이틀 */
  width: 100%;
  padding: 0.8rem 1rem;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

.back-btn {
  position: absolute;            /* 중앙 타이틀 영향 X */
  left: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.back-btn .icon {
  width: 28px;
  height: 28px;
  stroke: #6d28d9;   /* 보라색 아이콘 */
}

.timeline-header .title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #333;
}

/* ✅ 타임라인 본문 */
.timeline {
  height: 100vh;
  overflow-y: scroll;
  scroll-snap-type: y mandatory;
  -webkit-overflow-scrolling: touch;
  padding: 0;
  margin: 0;
}

/* 프로필 섹션 */
.profile-info {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: -100px; /* ✅ 기존 -70px → 더 올림 */
  text-align: center;
}

/* 프로필 사진 원 */
.profile-photo {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  background: #fff;
  border: 6px solid #c7b9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.6rem; /* ✅ 이름이랑 간격 줄임 */
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

/* 이름, 생년월일 */
.profile-name {
  font-size: 1.5rem;
  font-weight: 700;
  margin-top: 0.2rem;  /* ✅ 이름 위쪽 간격 줄임 */
}

.profile-sub {
  font-size: 0.9rem;
  margin-top: 0.1rem; /* ✅ 조금 더 위로 */
  color: #666;
}

/* 통계 (그림일기/접속일) */
.profile-stats {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin: 0.8rem 0; /* ✅ 위/아래 간격 줄여서 올림 */
  text-align: center;
}

/* 상단 배너 */
.profile-banner {
  width: 100%;
  height: 220px;
  background: linear-gradient(135deg, #e8e1ff, #c7b9ff, #dcd0ff);
  border-bottom-left-radius: 50% 20%;
  border-bottom-right-radius: 50% 20%;
  position: absolute;   /* 화면 상단 기준 */
  top: 0;               /* 맨 위부터 */
  left: 0;
  z-index: 0;           /* 헤더 뒤에 깔림 */
}

.stat strong {
  display: block;
  font-size: 1.4rem;
  font-weight: 700;
  color: #4b3fae;
}

.stat span {
  font-size: 0.9rem;
  color: #666;
}

/* 구분선 */
.divider {
  width: 90%;
  height: 1px;
  background: #e5e7eb;
  margin: 0 auto 1.5rem;
}

/* 그림일기 피드 */
.diary-feed {
  width: 100%;
  padding: 0 1rem 5rem;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.diary-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: transform 0.2s;
}

.diary-card:hover {
  transform: translateY(-4px);
}

.diary-thumb {
  background: #e8e1ff;
  aspect-ratio: 1 / 1;
}

.diary-caption {
  margin: 0.5rem;
  font-size: 0.9rem;
  color: #333;
}
</style>
