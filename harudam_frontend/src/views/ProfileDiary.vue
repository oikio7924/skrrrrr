<template>
  <!-- 상단 배너 -->
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

  <!-- 그림일기 피드 -->
  <section class="diary-feed">
    <div v-for="n in 12" :key="n" class="diary-card">
      <div class="diary-thumb"></div>
      <p class="diary-caption">그림일기 {{ n }}</p>
    </div>
  </section>

  <!-- Footer -->
  <FooterNav />
</template>

<script setup lang="ts">
import { ref } from "vue"
import FooterNav from "@/components/FooterNav.vue"

const parent = ref({
  name: "아버지",
  birth: "1960-05-12",
})

const joinedDate = new Date("2025-08-14")
const today = new Date()
const diffTime = today.getTime() - joinedDate.getTime()
const dDay = Math.floor(diffTime / (1000 * 60 * 60 * 24))

const diaries = ref(new Array(12).fill(null))
</script>

<style>
/* 전역 리셋 */
html, body, #app {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 100vh;
  background: #fff;
  overflow-x: hidden;
  font-family: 'Inter', sans-serif;
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
  0%   { background-position: 0% 50%; }
  100% { background-position: 100% 50%; }
}

/* 프로필 */
.profile-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: -80px; /* 배너와 겹치게 */
  text-align: center;
}
.profile-photo {
  width: clamp(120px, 20vw, 180px);
  height: clamp(120px, 20vw, 180px);
  border-radius: 50%;
  background: #fff;
  border: 5px solid #c7b9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #555;
  margin-bottom: 1rem;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
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

/* 그림일기 피드 (반응형 그리드) */
/* 그림일기 피드 */
.diary-feed {
  width: 100%;
  padding: 0 1rem 5rem;
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* ✅ 무조건 2열 고정 */
  gap: 12px;
}

.diary-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
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
