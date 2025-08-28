<template>
  <div class="mypage">
    <!-- ✅ 헤더 -->
    <header class="mypage-header">
      <!-- 로고+텍스트 전체를 클릭 가능하게 -->
      <div class="logo" @click="goHome">
        <img src="@/assets/harudam_logo.png" alt="하루담 로고" />
        <span class="logo-text">하루담</span>
      </div>

      <!-- 설정 버튼 -->
      <button class="settings-btn" @click="$router.push({ name: 'setting' })" aria-label="설정">
        <svg class="settings-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.8"
          stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.52-.878 3.348.95
               2.47 2.47a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924
               0 3.35a1.724 1.724 0 00-1.066 2.573c.878 1.52-.95
               3.348-2.47 2.47a1.724 1.724 0 00-2.573 1.066c-.426
               1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.52.878-3.348-.95-2.47-2.47a1.724
               1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924
               0-3.35a1.724 1.724 0 001.066-2.573c-.878-1.52.95-3.348
               2.47-2.47.996.575 2.229.146 2.573-1.066z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      </button>
    </header>


    <!-- 상단 프로필 -->
    <div class="profile-section">
      <div class="profile-photo">
        <template v-if="user?.profileImage">
          <img :src="user.profileImage" alt="프로필 이미지" class="photo-img" />
        </template>
        <template v-else>
          사진
        </template>
      </div>
      <h2 class="profile-name">{{ user ? user.name : "이름 없음" }}</h2>
    </div>

    <!-- 상단 메뉴 (이모지 아이콘) -->
    <div class="menu-bar">
      <div class="menu-item" @click="$router.push({ name: 'authentication' })">
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

    <!-- 자녀 설정 -->
    <section class="info-card">
      <h3 class="section-title">자녀 설정</h3>
      <ul>
        <li class="info-item" @click="$router.push({ name: 'ChildVoiceTraining' })">
          자녀 AI 목소리 설정
        </li>
        <li class="info-item" @click="$router.push({ name: 'child-character' })">
          자녀 AI 캐릭터 변경
        </li>
      </ul>
    </section>

    <!-- 부모 설정 -->
    <section class="info-card">
      <h3 class="section-title">부모 설정</h3>
      <ul>
        <li class="info-item" @click="$router.push({ name: 'parent-voice' })">
          부모 AI 목소리 설정
        </li>
        <li class="info-item" @click="$router.push({ name: 'parent-character' })">
          부모 AI 캐릭터 변경
        </li>
      </ul>
    </section>

    <!-- FooterNav -->
    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import FooterNav from "@/components/FooterNav.vue"
import apiClient from "@/api/index" // ✅ axios 인스턴스

type UserInfo = {
  id: number
  name: string
  email: string
  profileImage?: string // ✅ 선택적 필드
}

const router = useRouter()
const user = ref<UserInfo | null>(null)

// ✅ 사용자 정보 불러오기
const loadUserInfo = async () => {
  try {
    const { data } = await apiClient.get<UserInfo>("/api/users/me")
    user.value = data
  } catch (e) {
    console.error("사용자 정보 불러오기 실패", e)
  }
}

onMounted(() => {
  loadUserInfo()
})

function goSettings() {
  router.push("/setting")
}

function goHome() {
  router.push("/main_child") // ✅ 메인 페이지로 이동
}
</script>

<style scoped>
.mypage {
  width: 100%;
  background: #f9f9fb;
  min-height: 100vh;
  padding-top: 70px;
  /* 헤더 높이 */
  padding-bottom: 6rem;
  /* ✅ FooterNav 높이만큼 여백 확보 */
  font-family: "Inter", sans-serif;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-y: auto;
}

/* ✅ 헤더 */
.mypage-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  /* ✅ 공통 헤더 크기 */
  background: #fff;
  border-bottom-left-radius: 12px;
  /* ✅ 라운드 처리 */
  border-bottom-right-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  /* ✅ 부드러운 그림자 */
  position: fixed;
  top: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  /* ✅ 글씨랑 로고 사이 여백 확대 */
}

/* ✅ 헤더 스타일 */
.logo img {
  width: 42px;
  height: 42px;
}

.logo-text {
  font-family: "Dongle", sans-serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: #9a7dff;
  /* ✅ 조금 더 부드러운 연보라 */
  line-height: 1;
  transform: translateY(6px);
}

.settings-btn {
  background: none;
  border: none;
  cursor: pointer;
}

.settings-icon {
  width: 34px;
  height: 34px;
  color: #9a7dff;
  /* ✅ 로고색과 톤 맞춤 */
  transition: transform 0.2s ease, color 0.2s ease;
}

.settings-btn:hover .settings-icon {
  transform: rotate(30deg);
  color: #6d28d9;
}

/* 이하 기존 스타일 동일 */
.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 2rem;
  margin-bottom: 1rem;
}

.profile-photo {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: #d9d6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  /* ✅ 이미지가 영역 넘치지 않게 */
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  /* ✅ 원형 안에 꽉 차게 */
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

.menu-bar {
  display: flex;
  justify-content: space-between;
  background: linear-gradient(135deg, #e0d7ff, #c4b5fd);
  border-radius: 16px;
  margin: 0.5rem auto 1.5rem;
  padding: 14px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
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
  font-size: 1.6rem;
  line-height: 1;
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.25));
}

.info-card {
  width: 90%;
  max-width: 500px;
  margin: 0.8rem auto;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
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
