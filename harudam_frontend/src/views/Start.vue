<template>
  <main class="hd-screen">
    <section class="hd-hero">
      <img :src="imgs.book" alt="하루담 로고" class="hd-logo" />
    </section>

    <h1 class="hd-tagline">하루의 이야기를 담는다</h1>

    <div class="hd-flex-spacer" aria-hidden="true"></div>

    <nav class="hd-actions" aria-label="사용자 유형 선택">
      <button class="hd-btn hd-btn--parent" @click="goParent">부모용</button>
      <button class="hd-btn hd-btn--child" @click="goChild">자녀용</button>
    </nav>

    <div class="hd-safe" aria-hidden="true"></div>
  </main>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import bookPng from '@/assets/onboarding/4.png'

const imgs = { book: bookPng }
const router = useRouter()

// 프로젝트 라우터 name에 맞게 수정
const PARENT_ROUTE = 'parentlogin'
const CHILD_ROUTE = 'login'

function goParent() { router.push({ name: PARENT_ROUTE }) }
function goChild() { router.push({ name: CHILD_ROUTE }) }
</script>

<style scoped>
.hd-screen {
  position: fixed;
  inset: 0;
  display: flex; /* flexbox로 변경 */
  flex-direction: column; /* 세로 정렬 */
  justify-content: center; /* 세로 중앙 정렬 */
  align-items: center; /* 가로 중앙 정렬 */
  height: 100vh; /* 화면 전체 높이 사용 */
  padding: 20px;
  background: #fff;
}

/* ─── 로고 ─── */
.hd-hero {
  /* 로고와 슬로건이 같이 상단에 위치하도록 변경 */
  margin-bottom: 20px;
}

.hd-logo {
  width: 150px;
  height: auto;
  display: block;
}

/* ─── 문구 ─── */
.hd-tagline {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
  color: #8a6bd6; /* 이미지와 유사한 보라색 */
  text-align: center;
}

/* ─── 버튼 영역 ─── */
.hd-actions {
  width: 100%;
  max-width: 300px;
  display: flex;
  flex-direction: column;
  gap: 20px; /* 버튼 간 간격 */
  margin-top: 50px; /* 로고와 버튼 사이 간격 */
}

/* 버튼 */
.hd-btn {
  display: block;
  width: 100%;
  padding: 15px 0;
  border-radius: 30px;
  border: none;
  font-size: 1.1rem;
  font-weight: bold;
  color: #1f1f1f;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

/* 부모용: 노랑 그라데이션 */
.hd-btn--parent {
  background-color: #ffa843; /* 이미지에 가까운 노란색 */
}

/* 자녀용: 보라 그라데이션 */
.hd-btn--child {
  background-color: #9570ff; /* 이미지에 가까운 보라색 */
  color: #fff; /* 글자색을 흰색으로 변경 */
}
</style>
