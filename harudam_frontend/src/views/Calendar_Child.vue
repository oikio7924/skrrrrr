<template>
  <div class="view-content">
    <h1>📅 자녀 달력 페이지</h1>
    <p>이 페이지는 /calendar_child 경로의 페이지입니다.</p>


    <!-- ✅ FooterNav -->
    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import FooterNav from "@/components/FooterNav.vue";
</script>

<style scoped>
.view-content {
  padding: 40px;
  text-align: center;
  font-size: 1.2rem;
}
</style>
