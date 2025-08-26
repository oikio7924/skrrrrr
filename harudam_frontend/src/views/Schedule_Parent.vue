<template>
  <div id="app" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
    <div class="fixed-top-container">
      <div class="header-section">
        <div class="date-circle" :class="{ 'selected': selectedOffset === 0 }">{{ dayNumber }}일</div>
        <h2 class="date-text">{{ dateText }}</h2>
      </div>
    </div>

    <div class="agenda-list-wrapper">
      <div class="agenda-list">
        <div
          v-for="(item, index) in agendaList"
          :key="index"
          class="agenda-item-card"
          :class="{ 'has-border': isTodayAndNotPassed(item.time) }"
        >
          <div class="time-section">
            <span class="icon">{{ item.icon }}</span>
            <span class="time">{{ item.time }}시</span>
          </div>
          <div class="title-section">{{ item.title }}</div>
        </div>
        <p v-if="agendaList.length === 0" class="no-agenda">일정이 없습니다.</p>
      </div>
    </div>

    <div class="fixed-bottom-container">
      <div class="navigation-buttons">
        <button
          class="nav-button prev"
          @click="changeDay(-1)"
          :class="{ 'selected': selectedOffset === -1 }"
        >
          어제
        </button>
        <button
          class="nav-button today"
          @click="changeDay(0)"
          :class="{ 'selected': selectedOffset === 0 }"
        >
          오늘
        </button>
        <button
          class="nav-button next"
          @click="changeDay(1)"
          :class="{ 'selected': selectedOffset === 1 }"
        >
          내일
        </button>
      </div>

      <button class="home-button" @click="goHome">
        처음화면 보기
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 현재 날짜 상태
const today = new Date();
const currentDate = ref(new Date(today));
const selectedOffset = ref(0);

// 스와이프 관련 변수
const touchStartX = ref(0);
const touchStartY = ref(0);
const isClickEvent = ref(false);

// 날짜 데이터 계산
const dayNumber = computed(() => currentDate.value.getDate());
const dateText = computed(() => {
  const options = { year: 'numeric', month: 'long' };
  return currentDate.value.toLocaleDateString('ko-KR', options);
});

// 일정 데이터 (DB에서 불러올 부분)
const allAgenda = {
  [new Date(today.getFullYear(), today.getMonth(), today.getDate() - 1).toISOString().slice(0, 10)]: [
    { time: 9, title: '약 먹기', icon: '☀️' },
    { time: 12, title: '경로당 회의', icon: '' },
    { time: 17, title: '물주기', icon: '🌙' },
  ],
  [today.toISOString().slice(0, 10)]: [
    { time: 9, title: '아침 식사', icon: '☀️' },
    { time: 14, title: '병원 방문', icon: '' },
    { time: 19, title: 'TV 시청', icon: '🌙' },
    { time: 21, title: '독서', icon: '🌙' },
  ],
  [new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1).toISOString().slice(0, 10)]: [
    { time: 10, title: '시장 보기', icon: '☀️' },
    { time: 16, title: '손주 전화', icon: '' },
    { time: 18, title: '저녁 식사', icon: '🌙' },
    { time: 20, title: '산책', icon: '🌙' },
    { time: 22, title: '취침 준비', icon: '🌙' },
  ],
};

const agendaList = computed(() => {
  const key = currentDate.value.toISOString().slice(0, 10);
  return allAgenda[key] || [];
});

// 현재 시간에 따라 테두리 여부 결정
const isTodayAndNotPassed = (itemTime) => {
  const isToday = currentDate.value.toDateString() === today.toDateString();
  const currentHour = today.getHours();
  return isToday && itemTime > currentHour;
};

// 날짜 변경 함수
const changeDay = (offset) => {
  isClickEvent.value = true;
  selectedOffset.value = offset;
  const newDate = new Date(today);
  newDate.setDate(today.getDate() + selectedOffset.value);
  currentDate.value = newDate;

  setTimeout(() => {
    isClickEvent.value = false;
  }, 300);
};

// 스와이프 시작
const handleTouchStart = (event) => {
  touchStartX.value = event.touches[0].clientX;
  touchStartY.value = event.touches[0].clientY;
};

// 스와이프 끝
const handleTouchEnd = (event) => {
  if (isClickEvent.value) {
    return;
  }
  const touchEndX = event.changedTouches[0].clientX;
  const touchEndY = event.changedTouches[0].clientY;

  const swipeHorizontalDistance = touchEndX - touchStartX.value;
  const swipeVerticalDistance = touchEndY - touchStartY.value;

  // 수평 이동 거리가 수직 이동 거리보다 더 크고, 50px 이상일 때만 스와이프 동작
  if (Math.abs(swipeHorizontalDistance) > Math.abs(swipeVerticalDistance) && Math.abs(swipeHorizontalDistance) > 50) {
    if (swipeHorizontalDistance > 0 && selectedOffset.value > -1) {
      changeDay(selectedOffset.value - 1);
    } else if (swipeHorizontalDistance < 0 && selectedOffset.value < 1) {
      changeDay(selectedOffset.value + 1);
    }
  }
};

const goHome = () => {
  router.push({ name: 'MainParent' });
};
</script>

<style scoped>
:root {
  /* 팔레트 & 톤 */
  --bg-color: #f6f7fb;
  --card-bg-color: #ffffff;
  --primary-color: #4f46e5;     /* 인디고 */
  --accent-color: #ff8a4d;      /* 살구 오렌지 */
  --text-dark: #1f2937;
  --text-mid: #6b7280;
  --text-light: #ffffff;
  --border: #e5e7eb;

  /* 효과 */
  --shadow-sm: 0 2px 8px rgba(0,0,0,.06);
  --shadow-md: 0 6px 18px rgba(0,0,0,.08);
  --shadow-lg: 0 10px 30px rgba(0,0,0,.1);
  --radius-lg: 18px;
  --radius-xl: 22px;

  /* 타이포 */
  --h1: clamp(24px, 4.8vw, 32px);
  --h2: clamp(18px, 3.8vw, 24px);
  --body: clamp(15px, 3.5vw, 18px);
}

@media (prefers-color-scheme: dark) {
  :root {
    --bg-color: #0b1020;
    --card-bg-color: #131a2a;
    --primary-color: #8b93ff;
    --accent-color: #ff9f6e;
    --text-dark: #e5e7eb;
    --text-mid: #a2adc5;
    --text-light: #ffffff;
    --border: #23314d;
    --shadow-sm: 0 2px 8px rgba(0,0,0,.35);
    --shadow-md: 0 6px 20px rgba(0,0,0,.45);
    --shadow-lg: 0 10px 30px rgba(0,0,0,.55);
  }
}

*,
*::before,
*::after { box-sizing: border-box; }

body {
  font-family: 'Pretendard', 'Noto Sans KR', system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
  background: radial-gradient(1200px 800px at 50% -200px, rgba(79,70,229,.08), transparent 60%),
              var(--bg-color);
  color: var(--text-dark);
  margin: 0;
}

#app {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  max-width: 640px;
  margin: 0 auto;
  position: relative;
  isolation: isolate;
}

/* 상단 고정 헤더 */
.fixed-top-container {
  position: fixed;
  inset: 0 0 auto 0;
  margin: 0 auto;
  max-width: 640px;
  background: color-mix(in srgb, var(--bg-color) 85%, transparent);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 18px 20px;
  box-shadow: 0 1px 0 var(--border);
  z-index: 100;
}

.header-section {
  display: grid;
  grid-template-columns: auto 1fr;
  align-items: center;
  gap: 18px;
}

.date-circle {
  width: clamp(72px, 18vw, 96px);
  aspect-ratio: 1/1;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: clamp(28px, 7vw, 40px);
  font-weight: 800;
  color: var(--text-light);
  background:
    radial-gradient(120% 120% at 80% 20%, rgba(255,255,255,.18), transparent 40%),
    linear-gradient(135deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 78%, #000 22%));
  box-shadow: var(--shadow-md);
  border: 3px solid color-mix(in srgb, var(--primary-color) 40%, #fff 60%);
  transition: transform .25s ease, box-shadow .25s ease, background .25s ease;
  will-change: transform;
}

/* 오늘(선택됨) – 숫자가 절대 가려지지 않는 강조 */
.date-circle.selected {
  /* 배경 */
  background:
    radial-gradient(120% 120% at 80% 20%, rgba(255,255,255,.2), transparent 40%),
    linear-gradient(135deg, var(--accent-color), #ff7a2a);

  /* 텍스트 가독성 극대화 */
  color: #343434 !important;
  text-shadow: 0 1px 2px rgba(0,0,0,.55), 0 0 1px rgba(0,0,0,.35);
  -webkit-text-fill-color: currentColor;   /* 혹시 webkit에서 투명 처리된 경우 복구 */
  opacity: 1 !important;                   /* 애니메이션/상속으로 투명해지는 경우 방지 */
  mix-blend-mode: normal;                  /* 블렌드로 사라지는 경우 방지 */

  /* 링: 오버레이 없이 box-shadow만 사용 */
  position: relative;
  box-shadow:
    0 0 0 6px rgba(255,145,77,0.55),  /* 링 */
    0 8px 20px rgba(0,0,0,0.25);      /* 그림자 */
  transition: box-shadow .3s ease;
}

/* 혹시 남아 있을지 모를 모든 after 링 비활성화(완전 안전장치) */
.date-circle.selected::after { content: none !important; }




@keyframes pulseRing {
  0%, 100% {
    transform: scale(1);
    opacity: 0.9;
  }
  50% {
    transform: scale(1.08);
    opacity: 0.5;
  }
}

.date-text {
  font-size: var(--h1);
  font-weight: 800;
  letter-spacing: -0.02em;
}

/* 스크롤 가능한 목록 래퍼 */
.agenda-list-wrapper {
  flex: 1 1 auto;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  margin: clamp(110px, 22vh, 150px) 0 190px;
  padding: 0 18px;
  scroll-behavior: smooth;
}

/* 스크롤바 */
.agenda-list-wrapper::-webkit-scrollbar { width: 10px; }
.agenda-list-wrapper::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, var(--primary-color), color-mix(in srgb, var(--primary-color) 70%, #000 30%));
  border-radius: 999px;
  border: 3px solid transparent;
  background-clip: padding-box;
}
.agenda-list-wrapper::-webkit-scrollbar-track { background: transparent; }
.agenda-list-wrapper { scrollbar-color: var(--primary-color) transparent; scrollbar-width: thin; }

/* 일정 카드 */
.agenda-list {
  display: grid;
  gap: 14px;
  padding: 8px 2px 24px;
}

.agenda-item-card {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: 16px;
  background: linear-gradient(180deg, color-mix(in srgb, var(--card-bg-color) 92%, transparent), var(--card-bg-color));
  padding: 18px 20px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease, background .18s ease;
}

.agenda-item-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.agenda-item-card.has-border {
  border-color: var(--accent-color);
  box-shadow: 0 0 0 4px rgba(255, 145, 77, 0.15), 
              0 6px 16px rgba(255, 145, 77, 0.25);
  background: linear-gradient(
      180deg,
      var(--card-bg-color),
      color-mix(in srgb, var(--card-bg-color) 95%, var(--accent-color) 5%)
  );
}

.time-section {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: var(--primary-color);
  font-size: clamp(18px, 4.2vw, 22px);
  font-weight: 800;
  letter-spacing: .2px;
}

.icon { font-size: 1.4em; line-height: 1; }

.time { opacity: .95; }

.title-section {
  justify-self: end;
  text-align: right;
  font-size: clamp(17px, 4.3vw, 21px);
  font-weight: 600;
  color: var(--text-dark);
  word-break: keep-all;
}

/* 일정 없음 */
.no-agenda {
  text-align: center;
  color: var(--text-mid);
  font-size: var(--h2);
  padding: 28px 6px 6px;
}

/* 하단 고정 네비게이션 */
.fixed-bottom-container {
  position: fixed;
  inset: auto 0 0 0;
  margin: 0 auto;
  max-width: 640px;
  padding: 14px 16px 18px;
  background: color-mix(in srgb, var(--bg-color) 85%, transparent);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 -1px 0 var(--border);
  z-index: 200;
  display: grid;
  gap: 12px;
}

.navigation-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

/* 버튼 공통 */
.nav-button,
.home-button {
  appearance: none;
  border: 1px solid var(--border);
  background: var(--card-bg-color);
  color: var(--text-dark);
  /* padding: 14px  */
}/* 하단 고정 컨테이너 */
.fixed-bottom-container {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 600px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 14px;
  z-index: 200;
  border-top: 1px solid rgba(0,0,0,0.08);
}

/* 네비게이션 버튼 영역 */
.navigation-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

/* 버튼 공통 스타일 */
.nav-button,
.home-button {
  border: none;
  border-radius: 14px;
  padding: 14px 0;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  background: #ffffff;
  color: #333;
  box-shadow: 0 3px 8px rgba(0,0,0,0.06);
  transition: all 0.2s ease;
}

.nav-button:hover,
.home-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(0,0,0,0.12);
}

.nav-button:active,
.home-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
}

/* 선택된 버튼 */
.nav-button.selected {
  background: linear-gradient(135deg, #4f46e5, #6366f1);
  color: #fff;
  box-shadow: 0 4px 10px rgba(79, 70, 229, 0.35);
}

/* 처음화면 버튼 */
.home-button {
  width: 100%;
  background: linear-gradient(135deg, #ff8a4d, #ff6b2c);
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 107, 44, 0.35);
}
/* 1) 하단 버튼 더 크게 */
.fixed-bottom-container {
  padding: 18px 18px 22px;                /* 살짝 키움 */
}
.navigation-buttons {
  gap: 14px;                               /* 버튼 간격 소폭 확대 */
}
.nav-button,
.home-button {
  font-size: clamp(18px, 4.4vw, 22px);     /* 글자 크게 */
  padding: 16px 18px;                       /* 터치 타깃 56~60px 확보 */
  min-height: 60px;
  border-radius: 18px;
}

/* Home(처음화면 보기)도 동일하게 더 큼 */
.home-button {
  font-size: clamp(18px, 4.4vw, 22px);
  min-height: 64px;
}

/* 하단이 커진 만큼 스크롤 여유 살짝 추가 */
.agenda-list-wrapper {
  margin-bottom: 230px;  /* 기존 200px → 230px 정도로 */
}

/* 2) "2025년 8월" 텍스트(헤더 월/년) 조금 더 크게 */
.date-text {
  font-size: clamp(26px, 6.2vw, 36px); /* 상한 32 → 36 정도로 업 */
  font-weight: 800;
  letter-spacing: -0.02em;
}

/* 3) 달력 숫자와 일정 칸 사이에 희미한 실선 */
.fixed-top-container { 
  position: fixed; /* 이미 fixed지만 의도 명확화 */
}
.fixed-top-container::after {
  content: "";
  position: absolute;
  left: 16px;
  right: 16px;
  bottom: 0;
  height: 1px;
  background: linear-gradient(
    to right,
    transparent 0%,
    rgba(0,0,0,0.08) 15%,
    rgba(0,0,0,0.12) 50%,
    rgba(0,0,0,0.08) 85%,
    transparent 100%
  );
  pointer-events: none;
}

/* 다크 모드에서 구분선 대비 조정(선택 사항) */
@media (prefers-color-scheme: dark) {
  .fixed-top-container::after {
    background: linear-gradient(
      to right,
      transparent 0%,
      rgba(255,255,255,0.10) 15%,
      rgba(255,255,255,0.18) 50%,
      rgba(255,255,255,0.10) 85%,
      transparent 100%
    );
  }
}

/* 아주 작은 화면에서 버튼이 너무 커지지 않도록 살짝 보정 */
@media (max-width: 360px) {
  .nav-button, .home-button {
    font-size: 17px;
    min-height: 56px;
  }
}

</style>