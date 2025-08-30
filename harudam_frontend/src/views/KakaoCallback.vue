<!-- src/views/KakaoCallbackView.vue -->
<template>
  <div style="padding:24px">
    <p v-if="loading">카카오 로그인 처리 중…</p>
    <p v-else-if="error" style="color:#c00">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import http from '@/api/http'

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  const code = route.query.code as string | undefined;
  const state = route.query.state as string | undefined;
  const redirectUri = window.location.origin + route.path;

  if (!code) {
    error.value = "code 파라미터가 없습니다.";
    loading.value = false;
    return;
  }

  try {
    const res = await http.post('/auth/oauth/kakao/callback', {
      code,
      redirectUri,
      state,
    });

    // Axios에서는 res.status로 상태 코드 체크
    if (res.status !== 200) {
      const errorMsg = res.data?.message || "백엔드 오류";
      throw new Error(`백엔드 오류: ${res.status} ${errorMsg}`);
    }

    // 서버 응답 데이터를 그대로 사용
    type TokenDto = {
      accessToken: string;
      refreshToken: string;
      expiresIn: number;
      user: { id: number; email: string | null; name: string | null; needAdditionalInfo: boolean };
    };

    const data: TokenDto = res.data;

    // 로컬 스토리지에 액세스 토큰 저장
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);

    // 추가 정보가 필요한 경우 해당 페이지로 이동
    router.replace(data.user?.needAdditionalInfo ? "/signup/child" : "/signup");
  } catch (e: unknown) {
    // 에러 메시지 처리
    error.value = e instanceof Error ? e.message : String(e);
  } finally {
    loading.value = false;
  }
});
</script>

