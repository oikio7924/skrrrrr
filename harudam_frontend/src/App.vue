<!-- src/App.vue -->
<template>
  <!-- 라우트 화면 -->
  <RouterView />
  <!-- 하단 푸터: 라우트 메타로 제어 -->
  <FooterNav v-if="route.meta.showFooter" />
  <!-- 전역 로딩 오버레이 -->
  <LoadingOverlay :show="loading" />
</template>

<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import FooterNav from '@/components/FooterNav.vue'

/* 🔽 추가: 전역 로딩 상태 연결 */
import { storeToRefs } from 'pinia'
import LoadingOverlay from './components/LoadingOverlay.vue'
import { useUiStore } from '@/stores/ui'

const route = useRoute()
const ui = useUiStore()
const { loading } = storeToRefs(ui)

/* 콘솔에서 테스트용 (선택):
   window.ui.start(); window.ui.stop(); */
if (typeof window !== 'undefined') (window as any).ui = ui
</script>

<style>
/* 기존 스타일 유지 */
body { padding-bottom: 100px; }
</style>
