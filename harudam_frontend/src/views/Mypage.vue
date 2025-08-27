<template>
  <div class="mypage">
    <!-- 상단 프로필 -->
    <div class="profile-section">
      <div class="profile-photo">사진</div>
      <h2 class="profile-name">{{ user.name }}</h2>
    </div>

    <!-- 상단 메뉴 (이모지 아이콘) -->
    <div class="menu-bar">
      <div class="menu-item">
        <span class="icon">🔑</span>
        <span>인증코드</span>
      </div>
      <div class="menu-item">
        <span class="icon">📢</span>
        <span>공지사항</span>
      </div>
      <div class="menu-item">
        <span class="icon">🔔</span>
        <span>알림</span>
      </div>
    </div>

    <!-- 정보 -->
    <section class="info-card">
      <h3 class="section-title">정보</h3>
      <ul>
        <li class="info-item">내 정보 수정</li>
        <li class="info-item">부모 정보 수정</li>
        <li class="info-item">연동 계정</li>
      </ul>
    </section>

    <!-- 서비스 -->
    <section class="info-card">
      <h3 class="section-title">서비스</h3>
      <ul>
        <li class="info-item">자녀 AI 목소리 설정</li>
        <li class="info-item">자녀 AI 캐릭터 변경</li>
        <li class="info-item">자녀 항목 추가</li>
      </ul>
    </section>

    <!-- FooterNav -->
    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import FooterNav from "@/components/FooterNav.vue"

const user = ref({
  name: "이하나",
})
</script>

<style scoped>
.mypage {
  width: 100%;
  background: #f9f9fb;
  min-height: 100vh;
  padding-bottom: 5rem;
  font-family: "Inter", sans-serif;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 프로필 */
.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
  margin-bottom: 1rem;   /* ✅ 기존보다 간격 줄이기 */
}

.profile-photo {
  width: 120px;          /* ✅ 기존 100px → 확대 */
  height: 120px;
  border-radius: 50%;
  background: #d9d6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-bottom: 0.75rem;
  font-size: 1rem;        /* ✅ 글자 크기도 살짝 키움 */
  border: 4px solid #c4b5fd; /* ✅ 테두리 강조 */
  box-shadow: 0 4px 12px rgba(0,0,0,0.15); /* ✅ 입체감 */
}
.profile-name {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}
.edit-btn {
  background: #ede9fe;
  color: #6d28d9;
  font-size: 0.8rem;
  padding: 4px 12px;
  border-radius: 20px;
  border: none;
  margin-top: 0.5rem;
  cursor: pointer;
}

/* 메뉴바 */
.menu-bar {
  display: flex;
  justify-content: space-between;
  background: linear-gradient(135deg, #e0d7ff, #c4b5fd);
  border-radius: 16px;
  margin: 0.5rem auto 1.5rem; /* ✅ 위쪽 여백 줄이고 아래는 유지 */
  padding: 14px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
}
.menu-item {
  flex: 1;
  text-align: center;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 600;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 6px 0;
  border-radius: 12px;
  transition: all 0.25s ease;
  cursor: pointer;
}
.menu-item:hover {
  background: rgba(255, 255, 255, 0.2);
}
.icon {
  font-size: 1.6rem; /* ✅ 이모지 크게 */
  line-height: 1;
  filter: drop-shadow(0 2px 3px rgba(0,0,0,0.25)); /* ✅ 대비 강화 */
}

/* 카드 */
.info-card {
  width: 90%;
  max-width: 500px;
  margin: 0.8rem auto;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  background: #fff;
}
.section-title {
  margin-bottom: 0.6rem;
  color: #6d28d9;
  font-weight: bold;
}
.info-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.info-item {
  padding: 10px 0;
  font-size: 0.95rem;
  border-bottom: 1px solid #f1f1f1;
  cursor: pointer;
  transition: color 0.2s;
}
.info-item:last-child {
  border-bottom: none;
}
.info-item:hover {
  color: #6d28d9;
}
</style>
