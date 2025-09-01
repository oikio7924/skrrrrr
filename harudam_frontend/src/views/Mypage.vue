<template>
  <div class="mypage">
    <header class="mypage-header">
      <div class="logo" @click="goHome">
        <img src="@/assets/Harudam_logo.png" alt="하루담 로고" />
        <span class="logo-text">하루담</span>
      </div>
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

    <main class="content">
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

      <section class="info-card">
        <h3 class="section-title">정보</h3>
        <ul>
          <li class="info-item">내 정보 수정</li>
          <li class="info-item">부모 정보 수정</li>
          <li class="info-item">연동 계정</li>
        </ul>
      </section>

      <section class="info-card">
        <h3 class="section-title">자녀 설정</h3>
        <ul>
          <li class="info-item" @click="$router.push({ name: 'ChildVoiceTraining' })">
            자녀 AI 목소리 설정
          </li>
          <li class="info-item" @click="$router.push({ name: 'ChildCharacter' })">
            자녀 AI 캐릭터 변경
          </li>
        </ul>
      </section>

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
    </main>

    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import FooterNav from "@/components/FooterNav.vue"
import apiClient from "@/api/index"

type UserInfo = {
  id: number
  name: string
  email: string
  profileImage?: string
}

const router = useRouter()
const user = ref<UserInfo | null>(null)

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
  router.push("/main_child")
}
</script>

<style scoped>
.mypage {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f9f9fb;
  font-family: "Noto Sans KR", sans-serif;
  position: relative; /* ✅ FooterNav의 absolute 기준점을 위해 추가 */
}

.mypage-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo img {
  width: 42px;
  height: 42px;
}

.logo-text {
  font-family: "Dongle", sans-serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: #9a7dff;
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
  transition: transform 0.2s ease, color 0.2s ease;
}

.settings-btn:hover .settings-icon {
  transform: rotate(30deg);
  color: #6d28d9;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem 0;
  /* ✅ [수정] FooterNav 높이(약 6rem) + 여백을 고려하여 충분히 여백 추가 */
  padding-bottom: 7rem;
}

.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
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
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-name {
  font-size: 1.2rem;
  font-weight: 700;
  margin-top: 1rem;
}

.menu-bar {
  display: flex;
  justify-content: space-between;
  /* ✅ [복원] 그라데이션 배경색 다시 추가 */
  background: linear-gradient(135deg, #e0d7ff, #c4b5fd);
  border-radius: 16px;
  margin: 1.5rem auto;
  padding: 14px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.menu-item {
  flex: 1;
  text-align: center;
  color: #fff; /* ✅ 텍스트 색상을 흰색으로 변경 */
  font-size: 0.85rem;
  font-weight: 500;
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
  background: rgba(255, 255, 255, 0.2); /* 호버 시 배경색 변경 */
  color: #fff; /* 호버 시 텍스트 색상 유지 */
}

.icon {
  font-size: 1.6rem;
  line-height: 1;
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.25)); /* 아이콘 그림자 유지 */
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
  padding: 12px 4px;
  font-size: 0.95rem;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: color 0.2s, background-color 0.2s;
  border-radius: 4px;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item:hover {
  color: #6d28d9;
  background-color: #f9fafb;
}

/* FooterNav가 페이지 최하단에 위치하도록 z-index 조정 */
:deep(.footer-nav) {
  z-index: 20; /* 다른 콘텐츠 위에 표시되도록 z-index 높임 */
}
</style>
