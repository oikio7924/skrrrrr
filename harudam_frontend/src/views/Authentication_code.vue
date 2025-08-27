<template>
  <div class="authcode-page">
    <!-- ✅ 공통 헤더 -->
    <Header />

    <!-- 본문 -->
    <main class="content">
      <h2 class="page-title">부모 인증코드</h2>
      <p class="subtitle">필요할 때 인증코드를 확인하거나 복사하세요.</p>

      <!-- 인증코드 카드 -->
      <div class="code-card">
        <span class="code">{{ showCode ? code : "••••••" }}</span>
        <div class="btn-group">
          <button class="btn btn-view" @click="toggleCode">
            {{ showCode ? "가리기" : "보기" }}
          </button>
          <button class="btn btn-copy" @click="copyCode">복사</button>
        </div>
      </div>

      <!-- 추가 기능 (UI만) -->
      <div class="extra-section">
        <button class="btn btn-secondary">코드 재발급</button>
        <p class="info-text">만료일: 회원 탈퇴 시까지 유효</p>
        <p class="info-sub">※ 재발급 시 기존 코드는 무효화됩니다.</p>
      </div>
    </main>

    <!-- ✅ 공통 Footer -->
    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import Header from "@/components/Header.vue"
import FooterNav from "@/components/FooterNav.vue"

const code = ref("ABCD1234") // ✅ 예시 코드
const showCode = ref(false)

function toggleCode() {
  showCode.value = !showCode.value
}

function copyCode() {
  navigator.clipboard.writeText(code.value)
  alert("인증코드가 복사되었습니다.")
}
</script>

<style scoped>
.authcode-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #fafafa;
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
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  padding: 2rem 1.5rem;
  margin-bottom: 2rem;
  width: 100%;
  max-width: 360px;
}

.code {
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: 4px;
  color: #4c1d95;
  display: block;
  margin-bottom: 1.2rem;
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

/* 추가 기능 */
.extra-section {
  margin-top: 1.5rem;
  text-align: center;
}

.btn-secondary {
  background: #fff;
  border: 1px solid #ddd;
  padding: 0.55rem 1.2rem;
  border-radius: 10px;
  cursor: pointer;
  margin-bottom: 1rem;
  font-size: 0.85rem;
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
