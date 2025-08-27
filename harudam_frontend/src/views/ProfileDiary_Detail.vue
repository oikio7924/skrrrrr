<template>
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
        <h2 class="title">{{ diary.title }}</h2>
        <p class="date">{{ diary.date }}</p>
        <p class="text">{{ diary.content }}</p>
      </div>
    </div>
  </div>

  <!-- Footer -->
  <FooterNav />
</template>

<script setup lang="ts">
import { ref } from "vue"
import FooterNav from "@/components/FooterNav.vue"

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
</script>

<style scoped>
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
  width: 95%;                /* ✅ 모바일에서 거의 꽉 차게 */
  max-width: 700px;          /* ✅ PC에서도 크게 보이도록 */
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin: 1rem auto;         /* ✅ 카드 사이 여백 줄임 */

  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 이미지 영역 */
.image-box {
  margin: 0 0 1rem 0;        /* ✅ 위쪽 여백 제거, 아래만 간격 */
  width: 100%;
  height: 250px;             /* ✅ 모바일에서 보기 좋은 높이 */
  display: flex;
  justify-content: center;
  overflow: hidden;
  background: #fff;
}

.image-box img {
  width: 95%;                /* ✅ 이미지 좌우 여백 조금만 */
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

/* 텍스트 영역 */
.content-box {
  padding: 0.8rem 1.2rem 1.2rem;
}
.title {
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
    max-width: 900px;   /* ✅ 더 크게 */
  }
  .image-box {
    height: 300px;      /* ✅ 이미지도 조금 더 크게 */
  }
}
</style>
