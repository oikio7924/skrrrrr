<template>
  <div class="main-child">
     <!-- ✅ 공통 헤더 추가 -->
    <Header />
    <!-- 메인 컨텐츠 -->
    <main class="content">
      <!-- 제목 -->
      <h1 class="title">아버지의 하루</h1>
      <p class="subtitle">어떤 하루를 보내시고 계실까요?</p>

      <!-- 그림일기 카드 -->
      <section class="card" @click="goToDiaryDetail('2023-08-22')">
        <span class="badge">8월 22일 그림일기</span>
        <div class="image-box">Image</div>

        <h2 class="card-title">손자와 함께한 강가에서의 오후</h2>
        <p class="card-text">
          오랜만에 손주 너석과 함께 강가에 앉아 낚싯대를 드리우니,
          세월의 흐름도 잊을 만큼 평화로운 시간이었다.
          고기는 못 잡아도 마음만은 풍족한 하루.
        </p>

        <!-- 대표 감정 -->
        <div class="emotion-box">
          <span class="emoji">😊</span>
          <div class="emotion-text">
            <p class="label">대표 감정</p>
            <p class="desc">행복, 평온함</p>
          </div>
        </div>
      </section>

      <!-- 버튼 -->
      <!-- 버튼 -->
      <section class="button-group">
        <button class="btn" @click="goToCalendar">
          <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14
               a2 2 0 002-2V7a2 2 0 00-2-2H5
               a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          달력
        </button>

        <button class="btn" @click="goToMemoir">
          <!-- 📖 책 아이콘 -->
          <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 19.5V6.5A2.5 2.5 0 016.5 4H20v15H6.5
               A2.5 2.5 0 014 19.5z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 4v15a2 2 0 01-2 2H6.5
               a2.5 2.5 0 01-2.5-2.5" />
          </svg>
          자서전
        </button>
      </section>

      <!-- 일정 -->
      <section class="card schedule-card">
        <div class="schedule-header">
          <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14
                     a2 2 0 002-2V7a2 2 0 00-2-2H5
                     a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <h3 class="card-title">최근 주요 일정</h3>
        </div>

        <ul class="schedule-list">
          <li v-if="eventStore.sortedEvents.length === 0" class="schedule-item-empty">
            등록된 일정이 없어요.
          </li>
          <li v-for="event in eventStore.sortedEvents.slice(-3)" :key="event.id" class="schedule-item"
            @click="goToDetail(event.date)">
            <span class="date">{{ formatDate(event.date) }}</span>
            <span class="text">{{ event.title }}</span>
          </li>
        </ul>
      </section>
    </main>

    <!-- ✅ FooterNav -->
    <FooterNav />
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted } from "vue";
import { useRouter } from "vue-router";
import FooterNav from "@/components/FooterNav.vue";
import Header from "@/components/Header.vue"
import { useEventStore } from '@/stores/eventStore';

export default defineComponent({
  name: "MainChild",
  components: { FooterNav,Header },
  setup() {
    const router = useRouter();
    const eventStore = useEventStore();

    onMounted(() => {
      eventStore.loadEvents(); // 컴포넌트 마운트 시 일정 불러오기
    });

    // ✅ 일정 상세 페이지 이동
    const goToDetail = (date: string) => {
      router.push({ name: 'schedule_c', params: { date } });
    };

    // ✅ 그림일기 상세 페이지 이동
    const goToDiaryDetail = (date: string) => {
      router.push({ name: "profile_diary", params: { date } });
    };

    // ✅ 달력 이동
    const goToCalendar = () => {
      router.push({ name: "calendar_child" });
    };

    // ✅ 자서전 이동
    const goToMemoir = () => {
      router.push({ name: "memoir" });
    };
    const formatDate = (dateString: string) => {
      const [year, month, day] = dateString.split('-').map(Number);
      return `${month}월 ${day}일`;
    };

    return { goToDetail, goToDiaryDetail, goToCalendar, goToMemoir, eventStore, formatDate};
  }
});
</script>


<style>
#app {
  background: #F8FAFC !important;
  min-height: 100%;
  /* 100vh 대신 100% */
  display: flex;
  justify-content: center;
}

.title {
  font-weight: bold !important;
}

html,
body {
  height: 100%;
  /* 전체 높이 */
  overflow-y: auto;
  /* ✅ 스크롤 활성화 */
  background: #F8FAFC;
  /* 전체 배경 */
}

body {
  margin: 0 !important;
  padding: 0 !important;
  background: #F8FAFC !important;
  /* 배경색 유지 */
}

.main-child {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 720px;
  /* min-height: 100vh; ✅ 삭제 */
}

#app {
  display: flex;
  justify-content: center;
  background: #F8FAFC;
  min-height: 100vh;
}

.content {
  flex: 1;
  width: 100%;
  max-width: 720px;
  padding: 20px;
  padding-top: 100px;  /* ✅ 헤더 높이 + 여유 간격 */
  padding-bottom: 100px; /* FooterNav 가리지 않게 */
}

/* 제목 */
.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.subtitle {
  color: #666;
  margin-bottom: 20px;
}

/* 카드 */
.card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.06);
  padding: 16px;
  margin-bottom: 24px;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.card:hover {
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.badge {
  display: inline-block;
  background: #f3e8ff;
  color: #9333ea;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 9999px;
}

.image-box {
  margin-top: 10px;
  margin-bottom: 16px;
  /* ✅ 이미지 아래 간격 추가 */
  height: 220px;
  background: #c4b5fd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 20px;
}

.card-title {
  margin-top: 16px;
  /* ✅ 제목 위쪽 여백 늘림 */
  margin-bottom: 10px;
  /* ✅ 제목과 본문 사이 간격 */
  font-weight: bold;
  font-size: 18px;
}

.card-text {
  margin-top: 8px;
  font-size: 15px;
  color: #555;
  line-height: 1.6;
  /* ✅ 줄 간격을 조금 더 여유있게 */
}

/* 감정 */
.emotion-box {
  margin-top: 14px;
  display: flex;
  align-items: center;
  background: #f9fafb;
  border-radius: 10px;
  padding: 10px 14px;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.06);
}

.emoji {
  font-size: 28px;
  margin-right: 12px;
}

.emotion-text .label {
  font-size: 13px;
  font-weight: bold;
  color: #374151;
}

.emotion-text .desc {
  font-size: 14px;
  color: #4b5563;
}

/* 버튼 */
.button-group {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.btn {
  flex: 1;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px 0;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transition: transform 0.15s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ✅ hover 제거 → 눌림만 */
.btn:hover {
  transform: none;
  border-color: #e5e7eb;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.btn:active {
  transform: scale(0.96);
  background: #f9fafb;
}

.icon {
  width: 28px;
  height: 28px;
  margin-bottom: 6px;
  stroke: #4c1d95;
}

/* 일정 */
.schedule-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.schedule-header .icon {
  width: 20px;
  height: 20px;
  stroke: #9333ea;
}

.schedule-header .card-title {
  font-size: 16px;
  font-weight: bold;
  color: #1f2937;
}

/* 일정 리스트 */
.schedule-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.schedule-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, transform 0.1s ease;
}

.schedule-item:hover {
  background: #f3e8ff;
  /* 연보라 배경 */
}

.schedule-item:hover .date,
.schedule-item:hover .text {
  color: #6d28d9;
  /* 진보라 글씨 */
}

.schedule-item:active {
  background: #e9d5ff;
  /* 클릭 시 더 진한 보라 */
  transform: scale(0.98);
}

.schedule-item .date {
  font-weight: 500;
  /* 덜 굵게 */
  margin-right: 10px;
  color: #111;
  min-width: 90px;
}

.schedule-item .text {
  flex: 1;
  font-size: 14px;
  color: #374151;
}
</style>
