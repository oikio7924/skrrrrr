<template>
  <!-- ✅ 타임라인 헤더 -->
  <header class="timeline-header">
    <button class="back-btn" @click="goBack" aria-label="뒤로가기">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
           stroke-width="2" stroke="currentColor" class="icon">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
      </svg>
    </button>
    <h1 class="title">타임라인</h1>
  </header>

  <!-- ✅ 타임라인 리스트 -->
  <div class="timeline">
    <div
      v-for="(diary, idx) in diaries"
      :key="diary.id"
      class="timeline-item"
    >
      <!-- 이미지 -->
      <div class="image-box">
        <img :src="diary.img" alt="그림일기 이미지" />
      </div>

      <!-- 텍스트 -->
      <div class="content-box">
        <h2 class="content-title">{{ diary.title }}</h2>
        <p class="date">{{ diary.date }}</p>
        <p class="text">{{ diary.content }}</p>
      </div>
    </div>
  </div>

  <!-- ✅ Footer -->
  <FooterNav />
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import FooterNav from "@/components/FooterNav.vue"

const router = useRouter()

const diaries = ref(
  Array.from({ length: 12 }, (_, i) => {
    const id = 12 - i
    return {
      id,
      date: `2025년 8월 ${id}일`,
      title: `그림일기 ${id}`,
      content: `${id}일의 이야기입니다.`,
      img: `https://picsum.photos/800/500?random=${id}`,
    }
  })
)

function goBack() {
  router.back()
}
</script>

<style scoped>
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

/* 카드 하나 */
.timeline-item {
  scroll-snap-align: center;
  width: 95%;
  max-width: 700px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin: 1rem auto;

  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 이미지 영역 */
.image-box {
  margin: 0 0 1rem 0;
  width: 100%;
  height: 250px;
  display: flex;
  justify-content: center;
  overflow: hidden;
  background: #fff;
}

.image-box img {
  width: 95%;
  height: 90%;
  object-fit: cover;
  border-radius: 12px;
}

/* 텍스트 영역 */
.content-box {
  padding: 0.8rem 1.2rem 1.2rem;
}
.content-title {
  font-size: 1.2rem;
  font-weight: 700;
  margin: 0.3rem 0;
  color: #4c1d95;
}
.date {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 0.5rem;
}
.text {
  font-size: 0.95rem;
  color: #333;
  line-height: 1.4;
  margin: 0;
}

/* ✅ 반응형 - PC 화면 */
@media (min-width: 1024px) {
  .timeline-item {
    max-width: 900px;
  }
  .image-box {
    height: 300px;
  }
}
</style>
