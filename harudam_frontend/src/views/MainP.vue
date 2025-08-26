<template>
  <main style="min-height:100svh; display:grid; place-items:center;">
    <div>
      <h1>메인 화면</h1>
       <RouterLink to="/chat">챗봇 페이지 열기</RouterLink>
       <router-link class="btn" to="/calendar">주간 캘린더로 가기</router-link>
    </div>
  </main>
</template>

<style scoped>
.page { max-width: 520px; margin: 0 auto; padding: 16px; }
.btn { display:inline-block; padding:12px 16px; background:#ddd; border-radius:10px; text-decoration:none; color:#000; }
</style>