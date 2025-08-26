<template>
  <RouterView />
  <FooterNav v-if="route.meta.showFooter" />
</template>

<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router' // useRoute를 추가로 import
import FooterNav from '@/components/FooterNav.vue'

// 현재 페이지(라우트)의 정보를 가져옵니다
const route = useRoute()
</script>

<style>
/* 스타일은 그대로 유지합니다 */
body {
  padding-bottom: 100px;
}
</style>
