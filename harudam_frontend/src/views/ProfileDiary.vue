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
    <h2 class="profile-name">{{ parent.name }}</h2>
    <p class="profile-sub">{{ parent.birth }}</p>
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


import { ref } from "vue"
import { useRouter } from "vue-router"
import FooterNav from "@/components/FooterNav.vue"

function goBack() {
  router.push({ name: "main_child"})
}

const parent = ref({
  name: "아버지",
  birth: "1960-05-12",
})

const router = useRouter()

const joinedDate = new Date("2025-08-14")
const today = new Date()
const diffTime = today.getTime() - joinedDate.getTime()
const dDay = Math.floor(diffTime / (1000 * 60 * 60 * 24))

// ✅ 12 → 1 최신순 배열 생성
const diaries = ref([...Array(12).keys()].map(n => 12 - n))

function goToDiary(id: number) {
  console.log("goToDiary clicked with id:", id)
  router.push({ name: "profileDiaryDetail", params: { id } }) // ✅ 이름 일치
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
  position: sticky;              /* 스크롤 내려도 상단 고정 */
  top: 0;
  z-index: 100;
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

/* 프로필 */
.profile-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: -80px;
  text-align: center;
}

.profile-photo {
  width: clamp(150px, 20vw, 180px);
  height: clamp(150px, 20vw, 180px);
  border-radius: 50%;
  background: #fff;
  border: 5px solid #c7b9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #555;
  margin-bottom: 1rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.profile-name {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: #222;
}

.profile-sub {
  margin-top: 0.3rem;
  font-size: 1rem;
  color: #666;
}

/* 통계 */
.profile-stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
  margin: 1.5rem 0;
  text-align: center;
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
