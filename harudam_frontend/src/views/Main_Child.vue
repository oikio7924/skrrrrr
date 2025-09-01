<template>
  <div class="main-child">
    <Header />
    <main class="content">
      <h1 class="title">
        {{ parent ? (parent.gender === "M" ? "아버지" : "어머니") + "의 하루" : "부모님의 하루" }}
      </h1>
      <p class="subtitle">어떤 하루를 보내시고 계실까요?</p>

      <!-- 그림일기 카드 -->
      <section class="card" @click="goToDiaryFeed">
        <span class="badge">
          {{ diary ? formatDate(diary.date) + " 그림일기" : "그림일기" }}
        </span>

        <!-- 이미지 영역 -->
        <div class="image-box">
          <img v-if="diary && diary.imageUrl" :src="diary.imageUrl" alt="그림일기 이미지" />
          <span v-else>이미지가 없습니다.</span>
        </div>

        <!-- 제목 -->
        <h2 class="card-title">
          {{ diary ? diary.title : "제목이 없습니다." }}
        </h2>

        <!-- 본문 -->
        <p class="card-text">
          {{ diary ? diary.content : "등록된 그림일기가 없어요." }}
        </p>

        <!-- 감정 -->
        <div class="emotion-box">
          <span class="emoji">{{ diary ? diary.emoji : "😶" }}</span>
          <div class="emotion-text">
            <p class="label">대표 감정</p>
            <p class="desc">{{ diary ? diary.emotion : "감정 정보 없음" }}</p>
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
          <h3 class="card-title">최근 주요 일정</h3>
          <button class="add-btn" @click="goToDetail(new Date().toISOString().slice(0, 10))">
            일정추가하기
          </button>
        </div>
        <ul class="schedule-list">
          <li v-if="events.length === 0" class="schedule-item-empty">등록된 일정이 없어요.</li>
          <li v-for="event in events.slice(-3)" :key="event.id" class="schedule-item"
            @click="goToDetail(event.date)">
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
import { defineComponent, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import FooterNav from "@/components/FooterNav.vue";
import Header from "@/components/Header.vue";
import apiClient from "@/api/index"; // axios 인스턴스

type ParentInfo = {
  id: number;
  name: string;
  gender: "M" | "F";
  birth: string;
};

type Diary = {
  id: number;
  date: string;
  title: string;
  content: string;
  emotion: string;
  emoji: string;
  imageUrl: string;
};

type Event = {
  id: number;
  date: string;
  title: string;
};

export default defineComponent({
  name: "MainChild",
  components: { FooterNav, Header },
  setup() {
    const router = useRouter();

    // ✅ 부모 정보
    const parent = ref<ParentInfo | null>(null);
    const loadParentInfo = async () => {
      try {
        const { data } = await apiClient.get<ParentInfo>("/api/parent/1");
        parent.value = data;
      } catch (e) {
        console.error("부모 정보 불러오기 실패", e);
      }
    };

    // ✅ 그림일기
    const diary = ref<Diary | null>(null);
    const loadDiary = async () => {
      try {
        const { data } = await apiClient.get<Diary>("/api/diaries/child/1");
        diary.value = data;
      } catch (e) {
        console.error("그림일기 불러오기 실패", e);
      }
    };

    // ✅ 일정
    const events = ref<Event[]>([]);
    const loadEvents = async () => {
      try {
        const { data } = await apiClient.get<Event[]>("/api/events/child/1");
        events.value = data;
      } catch (e) {
        console.error("일정 불러오기 실패", e);
      }
    };

    // ✅ 마운트 시 불러오기
    onMounted(() => {
      loadParentInfo();
      loadDiary();
      loadEvents();
    });

    // ✅ 라우팅
    const goToDetail = (date: string) => {
      router.push({ name: "schedule_c", params: { date } });
    };
    const goToDiaryFeed = () => {
      router.push({ name: "profile_diary" }); // ✅ 그림일기 피드로 이동
    };
    const goToCalendar = () => router.push({ name: "calendar_child" });
    const goToMemoir = () => router.push({ name: "memoir" });

    // ✅ 날짜 포맷
    const formatDate = (dateString: string) => {
      const [y, m, d] = dateString.split("-").map(Number);
      return `${m}월 ${d}일`;
    };

    return {
      parent,
      diary,
      events,
      goToDetail,
      goToDiaryFeed,
      goToCalendar,
      goToMemoir,
      formatDate,
    };
  },
});
</script>




<style>
/*--- ✅ [수정] 전체 레이아웃 구조 정리 ---*/
/* 브라우저 기본 스타일 초기화 */
html,
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  background: #f8fafc;
}

/* 앱 전체를 감싸는 캔버스 */
#app {
  width: 100%;
  height: 100%;
}

/* 메인 컨테이너 (모바일 화면 크기 + 중앙 정렬) */
.main-child {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  background: #f8fafc;
}

/* 실제 콘텐츠 영역 (가장 중요!) */
.content {
  flex: 1;
  overflow-y: auto;
  /* ✅ [수정] padding-top 값을 줄여서 헤더와의 간격 조정 */
  padding: 24px 20px 80px 20px;
  box-sizing: border-box;
}

/* --- 이하 기존 디자인 스타일 유지 ---*/

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
  margin-left: auto;
  /* 오른쪽 끝으로 밀기 */
  padding: 6px 10px;
  font-size: 12px;
  line-height: 1;
  border: 1px solid #d8b4fe;
  /* 라일락 보더 */
  color: #6d28d9;
  /* 진보라 텍스트 */
  background: #ffffff;
  border-radius: 9999px;
  cursor: pointer;
  white-space: nowrap;
  /* 한 줄 유지 (넘침 방지) */
  transition: background 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease, color 0.2s ease, border-color 0.2s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.add-btn:hover {
  background: #f3e8ff;
  /* 연보라 배경 */
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

