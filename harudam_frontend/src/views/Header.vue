<template>
  <div class="app-container">
    <header class="header">
      <!-- 왼쪽 로고 이미지 -->
      <img src="@/assets/Harudam_logo.png" alt="Harudam Logo" class="header-logo" />

      <!-- 오른쪽 알림 버튼 -->
      <button class="header-button">
        <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c-2 5-3 7-3 7h18s-1.1-2-3-7z"></path>
          <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
        </svg>
      </button>
    </header>
   
  </div>
</template>

<script setup>
// 이 부분은 비워두거나 필요한 로직만 추가하면 됩니다.
// defineComponent, export default 등은 필요 없습니다.
</script>

<style scoped>
/* 전체 애플리케이션 컨테이너 스타일 */
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 200vh; /* 스크롤을 만들기 위해 충분한 높이를 설정 */
  font-family: 'Inter', sans-serif;
  color: #333;
}

/* 헤더 스타일 */
.header {
  position: sticky;
  top: 0;
  left: 0;
  z-index: 50;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 12px 16px;
  background: linear-gradient(#e7ebf3, #eef1f6);
  border-bottom: 1px solid #d8dee8;
  box-shadow: 0 2px 10px rgba(22, 27, 45, 0.08);
}

.header-logo {
  height: 32px; /* 로고 크기 조절 */
  width: auto;
}

.header-button {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  outline: none;
}

.header-button svg {
  display: block;
  color: #7e87a1;
}

/* 본문 내용 스타일 */
.content {
  flex-grow: 1;
  padding: 20px;
  line-height: 1.6;
}
</style>
