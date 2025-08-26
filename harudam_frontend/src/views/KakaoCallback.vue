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
    const res = await fetch("http://localhost:8080/api/auth/oauth/kakao/callback", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ code, redirectUri, state }),
    });

    if (!res.ok) {
      const t = await res.text().catch(() => "");
      throw new Error(`백엔드 오류: ${res.status} ${t}`);
    }

    type TokenDto = {
      accessToken: string;
      refreshToken: string;
      expiresIn: number;
      user: { id: number; email: string | null; name: string | null; needAdditionalInfo: boolean };
    };

    const data: TokenDto = await res.json();

    localStorage.setItem("access_token", data.accessToken);
    localStorage.setItem("refresh_token", data.refreshToken);

    router.replace(data.user?.needAdditionalInfo ? "/signup/child" : "/signup");
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : String(e);
  } finally {
    loading.value = false;
  }
});
</script>

