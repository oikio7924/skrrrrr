<template>

  <!-- ✅ 타임라인 헤더 -->
  <header class="timeline-header">
    <button class="back-btn" @click="goBack" aria-label="뒤로가기">
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
        class="icon">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
      </svg>
    </button>
    <h1 class="title">인증코드</h1>
  </header>

  <div class="authcode-page">
    <!-- 본문 -->
    <main class="content">
      <h2 class="page-title">부모 인증코드</h2>
      <p class="subtitle">필요할 때 인증코드를 확인하거나 복사하세요.</p>

      <!-- 인증코드 카드 -->
      <div class="code-card">
        <span class="code">
          {{ showCode ? (code || "인증 코드가 없습니다.") : "••••••" }}
        </span>
        <div class="btn-group">
          <button class="btn btn-view" @click="toggleCode">
            {{ showCode ? "가리기" : "보기" }}
          </button>
          <button class="btn btn-copy" @click="copyCode">복사</button>
        </div>
      </div>

      <!-- 추가 기능 (재발급) -->
      <div class="extra-section">
        <button class="btn-secondary">코드 재발급</button>
        <p class="info-text">만료일: 회원 탈퇴 시까지 유효</p>
        <p class="info-sub">※ 재발급 시 기존 코드는 무효화됩니다.</p>
      </div>

    </main>

    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import FooterNav from "@/components/FooterNav.vue"
import apiClient from "@/api/index"

const router = useRouter()
const code = ref<string | null>(null)
const showCode = ref(false)

// ✅ DB에서 인증코드 불러오기
const loadAuthCode = async () => {
  try {
    const { data } = await apiClient.get<{ code: string }>("/api/authcode/me")
    code.value = data.code
  } catch (e) {
    console.error("인증코드 불러오기 실패", e)
    code.value = null
  }
}
onMounted(loadAuthCode)

function toggleCode() {
  showCode.value = !showCode.value
}

function copyCode() {
  if (code.value) {
    navigator.clipboard.writeText(code.value)
    alert("인증코드가 복사되었습니다.")
  }
}

function goBack() {
  router.push({ name: "mypage" })
}
</script>

<style scoped>
/* ✅ 타임라인 헤더 */
.timeline-header {
  position: fixed;
  /* 스크롤 내려도 상단 고정 */
  top: 0;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 중앙에 타이틀 */
  width: 100%;
  padding: 0.8rem 1rem;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

.back-btn {
  position: absolute;
  /* 중앙 타이틀 영향 X */
  left: 1rem;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.back-btn .icon {
  width: 28px;
  height: 28px;
  stroke: #6d28d9;
  /* 보라색 아이콘 */
}


.content {
  margin-top: 80px;
  /* 헤더 높이만큼 아래 */
  text-align: center;
  padding: 2rem 1rem;
}

/* 본문 중앙 정렬 */
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 2rem 1.5rem;
  text-align: center;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.4rem;
  color: #2e1065;
}

.subtitle {
  font-size: 0.95rem;
  color: #6b7280;
  margin-bottom: 2rem;
}

/* 인증코드 카드 */
.code-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  padding: 2rem 1.5rem;
  margin-bottom: 2rem;
  width: 100%;
  max-width: 360px;
}

/* 인증코드 박스 크기 고정 */
.code {
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: 4px;
  color: #4c1d95;
  display: block;
  margin-bottom: 1.2rem;
  min-height: 2rem;   /* ✅ 높이 고정 */
  line-height: 2rem;  /* ✅ 줄간격 맞춤 */
}

.btn-group {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn {
  padding: 0.65rem 1.2rem;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
  font-size: 0.9rem;
}

/* 보기 버튼 */
.btn-view {
  background: #f3f0ff;
  color: #6d28d9;
}

.btn-view:hover {
  background: #e9e3ff;
}

/* 복사 버튼 */
.btn-copy {
  background: #6d28d9;
  color: #fff;
}

.btn-copy:hover {
  background: #5b21b6;
}

/* 재발급 버튼 영역 */
.extra-section {
  margin-top: 2rem; /* 카드와 적당히 간격 */
  display: flex;
  flex-direction: column;
  align-items: center; /* ✅ 가운데 정렬 */
  gap: 0.6rem;
}


.btn-secondary {
  background: #fff;
  border: 1px solid #ddd;
  padding: 0.55rem 1.2rem;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.9rem;
}

.btn-secondary:hover {
  background: #f9f9f9;
}

.info-text {
  font-size: 0.85rem;
  color: #374151;
  font-weight: 600;
}

.info-sub {
  font-size: 0.75rem;
  color: #9ca3af;
}
</style>
