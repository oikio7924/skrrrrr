<template>
  <div class="main-page">
    <!-- 첫 번째 화면 -->
    <section class="screen">
      <div class="content">
        <h1 class="title">아버지의 하루</h1>
        <p class="subtitle">어떤 하루를 보내시고 계실까요?</p>

        <!-- 그림일기 -->
        <div class="card">
          <span class="badge">8월 22일 그림일기</span>
          <div class="image-box">Image</div>
          <h2 class="card-title">손자와 함께한 강가에서의 오후</h2>
          <p class="card-text">
            오랜만에 손주 너석과 함께 강가에 앉아 낚싯대를 드리우니,
            세월의 흐름도 잊을 만큼 평화로운 시간이었다.
          </p>

          <!-- 감정 -->
          <div class="emotion-box">
            <span class="emoji">😊</span>
            <div class="emotion-text">
              <p class="label">대표 감정</p>
              <p class="desc">행복, 평온함</p>
            </div>
          </div>
        </div>

        <!-- 버튼 -->
        <div class="button-group">
          <button class="btn">달력</button>
          <button class="btn">자서전</button>
        </div>
      </div>
    </section>

    <!-- 두 번째 화면 -->
    <section class="screen">
      <div class="content">
        <div class="card">
          <h3 class="card-title">최근 주요 일정</h3>
          <ul class="schedule-list">
            <li><span class="date">8/24 (일)</span> 딸에게 전화하기</li>
            <li><span class="date">8/23 (토)</span> 동호회 모임 참석하기</li>
            <li><span class="date">8/22 (금)</span> 손자와 낚시</li>
          </ul>
        </div>
      </div>
    </section>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({ name: "MainPage" });
</script>

<style scoped>
/* 전체 */
/* 전체 */
.main-page {
 width: 100%;
  /* display, flex-direction, align-items 속성 제거 */
}

/* 화면 단위 */
.screen {
  width: 100%;
  height: 100vh;          /* 한 화면 꽉 채움 */
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 중앙 컨텐츠 */
.content {
  width: 100%;
  max-width: 720px;       /* PC에서 카드 폭 */
  padding: 20px;
}

/* 카드 */
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
  padding: 16px;
  margin-bottom: 20px;
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

/* 감정 */
.emotion-box {
  margin-top: 14px;
  display: flex;
  align-items: center;
  background: #f9fafb;
  border-radius: 10px;
  padding: 10px 14px;
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.06);
}
.emoji { font-size: 28px; margin-right: 12px; }

/* 버튼 */
.button-group {
  display: flex;
  gap: 16px;
}
.btn {
  flex: 1;
  background: #fff;
  border: none;
  border-radius: 12px;
  padding: 14px 0;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
}
.btn:hover { transform: translateY(-1px); }

/* 일정 */
.schedule-list { margin-top: 10px; font-size: 14px; color: #333; }
.schedule-list li { margin-bottom: 6px; }
.date { color: #888; margin-right: 6px; }
</style>
