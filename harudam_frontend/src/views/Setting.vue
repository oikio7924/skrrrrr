<template>
  <div class="settings">
    <!-- 헤더 -->
    <h2 class="header">환경설정</h2>

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
</script>

<style scoped>
.settings {
  width: 100%;
  background: #f9f9fb;
  min-height: 100vh;
  padding: 2rem 1rem 6rem;
  font-family: "Inter", sans-serif;
}

/* 헤더 */
.header {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  color: #4c1d95;
}

/* 리스트 카드 */
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

/* 버전 */
.version-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  margin-bottom: 1.5rem;
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

/* 로그아웃 카드 */
.logout-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  margin-bottom: 2rem;
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

/* 회원탈퇴 */
.withdraw {
  text-align: center;
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
