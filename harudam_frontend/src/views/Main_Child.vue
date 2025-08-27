<template>
  <div class="main-child">
    <Header />
    <main class="content">
      <h1 class="title">아버지의 하루</h1>
      <p class="subtitle">어떤 하루를 보내시고 계실까요?</p>

      <section class="card" @click="goToDiaryDetail('2023-08-22')">
        <span class="badge">8월 22일 그림일기</span>
        <div class="image-box">Image</div>

        <h2 class="card-title">손자와 함께한 강가에서의 오후</h2>
        <p class="card-text">
          오랜만에 손주 너석과 함께 강가에 앉아 낚싯대를 드리우니,
          세월의 흐름도 잊을 만큼 평화로운 시간이었다.
          고기는 못 잡아도 마음만은 풍족한 하루.
        </p>

        <div class="emotion-box">
          <span class="emoji">😊</span>
          <div class="emotion-text">
            <p class="label">대표 감정</p>
            <p class="desc">행복, 평온함</p>
          </div>
        </div>
      </section>

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
          <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 19.5V6.5A2.5 2.5 0 016.5 4H20v15H6.5
                A2.5 2.5 0 014 19.5z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 4v15a2 2 0 01-2 2H6.5
                a2.5 2.5 0 01-2.5-2.5" />
          </svg>
          자서전
        </button>
      </section>

      <section class="card schedule-card">
        <div class="schedule-header">
          <svg xmlns="http://www.w3.org/2000/svg" class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14
                     a2 2 0 002-2V7a2 2 0 00-2-2H5
                     a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <h3 class="card-title">최근 주요 일정</h3>

          <!-- ✅ 오른쪽 끝 일정추가하기 버튼 -->
          <button class="add-btn" @click="goToDetail(todayStr)">
            일정추가하기
          </button>
        </div>

        <ul class="schedule-list">
          <li v-if="eventStore.sortedEvents.length === 0" class="schedule-item-empty">
            등록된 일정이 없어요.
          </li>
          <li
            v-for="event in eventStore.sortedEvents.slice(-3)"
            :key="event.id"
            class="schedule-item"
            @click="goToDetail(event.date)"
          >
            <span class="date">{{ formatDate(event.date) }}</span>
            <span class="text">{{ event.title }}</span>
          </li>
        </ul>
      </section>
    </main>

    <FooterNav />
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted } from "vue";
import { useRouter } from "vue-router";
import FooterNav from "@/components/FooterNav.vue";
import Header from "@/components/Header.vue";
import { useEventStore } from "@/stores/eventStore";

export default defineComponent({
  name: "MainChild",
  components: { FooterNav, Header },
  setup() {
    const router = useRouter();
    const eventStore = useEventStore();

    onMounted(() => {
      eventStore.loadEvents(); // 컴포넌트 마운트 시 일정 불러오기
    });

    // ✅ 일정 상세 페이지 이동
    const goToDetail = (date: string) => {
      router.push({ name: "schedule_c", params: { date } });
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

    // ✅ YYYY-MM-DD → "M월 D일"
    const formatDate = (dateString: string) => {
      const [year, month, day] = dateString.split("-").map(Number);
      return `${month}월 ${day}일`;
    };

    // ✅ 오늘 날짜(클라이언트 기준) → YYYY-MM-DD
    const todayStr = (() => {
      const d = new Date();
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, "0");
      const day = String(d.getDate()).padStart(2, "0");
      return `${y}-${m}-${day}`;
    })();

    return {
      goToDetail,
      goToDiaryDetail,
      goToCalendar,
      goToMemoir,
      eventStore,
      formatDate,
      todayStr,
    };
  },
});
</script>

<style>
#app {
  background: #f8fafc !important;
  min-height: 100%;
  display: flex;
  justify-content: center;
}

.title {
  font-weight: bold !important;
}

html,
body {
  height: 100%;
  overflow-y: auto;
  background: #f8fafc;
}

body {
  margin: 0 !important;
  padding: 0 !important;
  background: #f8fafc !important;
}

.main-child {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 720px;
}

#app {
  display: flex;
  justify-content: center;
  background: #f8fafc;
  min-height: 100vh;
}

.content {
  flex: 1;
  width: 100%;
  max-width: 720px;
  padding: 20px;
  padding-top: 100px;
  padding-bottom: 100px;
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
  margin-bottom: 10px;
  font-weight: bold;
  font-size: 18px;
}

.card-text {
  margin-top: 8px;
  font-size: 15px;
  color: #555;
  line-height: 1.6;
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

/* ✅ 일정추가하기 버튼 (헤더 오른쪽 끝) */
.add-btn {
  margin-left: auto;           /* 오른쪽 끝으로 밀기 */
  padding: 6px 10px;
  font-size: 12px;
  line-height: 1;
  border: 1px solid #d8b4fe;   /* 라일락 보더 */
  color: #6d28d9;              /* 진보라 텍스트 */
  background: #ffffff;
  border-radius: 9999px;
  cursor: pointer;
  white-space: nowrap;         /* 한 줄 유지 (넘침 방지) */
  transition: background 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease, color 0.2s ease, border-color 0.2s ease;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}

.add-btn:hover {
  background: #f3e8ff;        /* 연보라 배경 */
  border-color: #c084fc;
  color: #5b21b6;
}

.add-btn:active {
  transform: scale(0.98);
  background: #e9d5ff;
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
}

.schedule-item:hover .date,
.schedule-item:hover .text {
  color: #6d28d9;
}

.schedule-item:active {
  background: #e9d5ff;
  transform: scale(0.98);
}

.schedule-item .date {
  font-weight: 500;
  margin-right: 10px;
  color: #111;
  min-width: 90px;
}

.schedule-item .text {
  flex: 1;
  font-size: 14px;
  color: #374151;
}

.schedule-item-empty {
  padding: 10px 12px;
  color: #6b7280;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.1s ease;
}

.schedule-item-empty:hover {
  background: #f3f4f6;
  border-radius: 8px;
}

.schedule-item-empty:active {
  transform: scale(0.98);
}
</style>
