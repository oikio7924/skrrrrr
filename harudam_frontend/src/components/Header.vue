<template>
  <div class="app-container">
    <header class="header">
      <!-- 왼쪽 로고 이미지 -->
      <img src="@/assets/Harudam_logo.png" alt="하루담 로고" class="header-logo" />

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
// 필요한 스크립트 로직은 여기에 추가할 수 있습니다.
</script>

<style scoped>
/* 전체 애플리케이션 컨테이너 스타일 */
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 200vh; /* 스크롤을 만들기 위해 충분한 높이를 설정 */
  font-family: 'Inter', sans-serif;
  color: #333;
  background-color: #eef1f6; /* 이미지 배경과 유사한 색상으로 설정 */
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
  padding: 12px 20px;
  background-color: #fff;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05); /* 부드러운 그림자 */
}

.header-logo {
  height: 36px;
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


</style>
