<template>
  <div class="settings">
    <!-- ✅ 헤더 -->
    <header class="settings-header">
      <button class="back-btn" @click="goBack" aria-label="뒤로가기">
        <!-- 화살표 SVG -->
        <svg xmlns="http://www.w3.org/2000/svg" fill="none"
             viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
             class="arrow-icon">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
        </svg>
      </button>
      <h2 class="header-title">환경설정</h2>
    </header>

    <!-- 리스트 -->
    <section class="settings-card">
      <ul>
        <li class="settings-item">고객센터</li>
        <li class="settings-item">자주 묻는 질문</li>
        <li class="settings-item">서비스 이용방법</li>
      </ul>
    </section>

    <!-- 버전 -->
    <section class="version-card">
      <div class="version-row">
        <span class="label">버전</span>
        <span class="value">96.05.30 v</span>
        <span class="desc">최신 버전 입니다.</span>
      </div>
    </section>

    <!-- 로그아웃 -->
    <section class="logout-card">
      <button class="logout-btn">로그아웃</button>
    </section>

    <!-- 회원탈퇴 -->
    <div class="withdraw">
      <button class="withdraw-btn">회원탈퇴</button>
    </div>

    <!-- FooterNav -->
    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import FooterNav from "@/components/FooterNav.vue"
import { useRouter } from "vue-router"

const router = useRouter()
function goBack() {
  router.push("/mypage") // ✅ 마이페이지로 이동
}
</script>

<style scoped>
.settings {
  width: 100%;
  background: #f9f9fb;
  min-height: 100vh;
  padding: 2rem 1rem 6rem;
  font-family: "Inter", sans-serif;
}

/* ✅ 헤더 */
.settings-header {
  display: flex;
  align-items: center;
  justify-content: center; /* 가운데 제목 정렬 */
  position: relative;
  padding: 1rem;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
  margin-bottom: 1.5rem;
}

.back-btn {
  position: absolute;
  left: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
}

.arrow-icon {
  width: 28px;
  height: 28px;
  color: #9a7dff;
}

.header-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #6d28d9;
}

/* 기존 스타일 유지 */
.settings-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  margin-bottom: 1.5rem;
}
.settings-card ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.settings-item {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background 0.2s;
}
.settings-item:last-child {
  border-bottom: none;
}
.settings-item:hover {
  background: #f3f0ff;
}

.version-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  margin-bottom: 2.5rem;
  padding: 1rem;
  font-size: 0.9rem;
}
.version-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.label {
  font-weight: 600;
  color: #333;
}
.value {
  color: #6d28d9;
  font-weight: 600;
  margin-left: 0.5rem;
}
.desc {
  font-size: 0.85rem;
  color: #666;
  margin-left: auto;
}

.logout-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  margin-bottom: 2.5rem;
}
.logout-btn {
  width: 100%;
  padding: 1rem;
  font-size: 0.95rem;
  font-weight: 600;
  border: none;
  background: none;
  color: #4c1d95;
  text-align: left;
  cursor: pointer;
  border-radius: 12px;
  transition: background 0.2s;
}
.logout-btn:hover {
  background: #f3f0ff;
}

.withdraw {
  text-align: center;
  margin-top: 2rem; /* ✅ 회원탈퇴 버튼 위에 여백 */
}
.withdraw-btn {
  background: none;
  border: none;
  font-size: 0.9rem;
  font-weight: 600;
  color: #dc2626;
  cursor: pointer;
}
</style>
