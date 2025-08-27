<template>
  <div class="welcome-container">
    <div v-if="isLoading" class="loading-state">
      <p>정보를 불러오는 중입니다...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <h2>오류</h2>
      <p>{{ error }}</p>
      <button @click="startService" class="btn-primary">메인으로 돌아가기</button>
    </div>

    <div v-else class="content-wrapper">
      <svg class="hero-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <h1>회원가입을 환영합니다!</h1>
      <div class="description-group">
        <p>{{ parentName }} 님을 위한 서비스 신청이 완료되었습니다.</p>
        <p>서비스 이용에 필요한 <strong>부모님 고유 코드</strong>입니다.</p>
      </div>
      <div class="code-box">
        <span id="parent-code">{{ parentCode }}</span>
        <button @click="copyCode" class="btn-copy">{{ copyButtonText }}</button>
      </div>
      <div class="notice">
        <p>※ 이 코드는 <strong>[마이페이지 > 인증코드]</strong>에서 다시 확인할 수 있습니다.</p>
      </div>
      <div class="button-wrapper">
        <button @click="startService" class="btn-primary">서비스 시작하기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios'; // API 통신을 위한 axios

// --- 1. 상태 변수 정의 ---
const isLoading = ref(true); // 로딩 상태
const error = ref(null); // 에러 메시지
const parentName = ref(''); // 부모님 성함
const parentCode = ref(''); // 부모님 고유 코드
const copyButtonText = ref('복사'); // 복사 버튼 텍스트

const router = useRouter(); // 페이지 이동을 위한 Vue Router

// --- 2. 데이터 로딩 (API 호출) ---
onMounted(async () => {
  try {
    // [수정 필요] 실제 백엔드 API 엔드포인트 주소를 입력하세요.
    const response = await axios.get('/api/v1/signup-complete-info');

    // API 호출 성공 시
    if (response.data && response.data.success) {
      parentName.value = response.data.parentName;
      parentCode.value = response.data.parentCode;
    } else {
      // API가 success: false를 반환한 경우
      throw new Error(response.data.message || '정보를 불러오는데 실패했습니다.');
    }
  } catch (err) {
    // API 호출 자체가 실패한 경우 (네트워크 오류, 서버 에러 등)
    // 이 페이지에 재접속 시 백엔드에서 에러를 반환하면 이 부분이 실행됩니다.
    error.value = err.response?.data?.message || '페이지를 표시할 수 없습니다. 이미 확인하셨거나 잘못된 접근입니다.';
  } finally {
    isLoading.value = false; // 로딩 상태 종료
  }
});

// --- 3. 기능 함수 구현 ---

/**
 * 코드를 클립보드에 복사하는 함수
 */
const copyCode = () => {
  if (!parentCode.value) return;

  navigator.clipboard.writeText(parentCode.value)
    .then(() => {
      // 복사 성공 시
      copyButtonText.value = '복사 완료!';
      setTimeout(() => {
        copyButtonText.value = '복사';
      }, 2000); // 2초 후에 버튼 텍스트를 원래대로 되돌림
    })
    .catch(err => {
      // 복사 실패 시
      console.error('복사 실패:', err);
      alert('코드 복사에 실패했습니다.');
    });
};

/**
 * '서비스 시작하기' 버튼 클릭 시 페이지를 이동하는 함수
 */
const startService = () => {
  // [수정 필요] 이동할 페이지의 실제 경로를 입력하세요. (예: '/main' 또는 '/')
  router.push('/');
};

</script>

<style scoped>
/* CSS 스타일은 이전과 동일하게 유지됩니다. */
.welcome-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; font-family: sans-serif; background: #fcfcff; }
.content-wrapper { display: flex; flex-direction: column; align-items: center; text-align: center; transform: translateY(-40px); }
.hero-icon { width: 80px; height: 80px; color: #7f5bff; margin-bottom: 24px; }
h1 { font-size: 28px; font-weight: 700; margin-bottom: 16px; word-break: keep-all; }
.description-group { color: #555; font-size: 15px; line-height: 1.6; margin-bottom: 32px; }
.description-group p:first-child { margin-bottom: 4px; }
.code-box { display: flex; justify-content: space-between; align-items: center; width: 100%; max-width: 420px; min-height: 80px; padding: 0 16px 0 32px; background-color: #ffffff; border-radius: 16px; border: 1px solid #eee; box-shadow: 0 4px 16px rgba(0,0,0,0.05); }
#parent-code { font-size: 26px; font-weight: 700; color: #212529; white-space: nowrap; letter-spacing: 1px; }
.btn-copy { font-size: 15px; font-weight: 600; color: #7f5bff; background-color: #f5f3ff; border: none; border-radius: 10px; padding: 12px 20px; cursor: pointer; white-space: nowrap; margin-left: 24px; transition: background-color 0.2s; }
.btn-copy:hover { background-color: #ede9fe; }
.notice { font-size: 13px; color: #6c757d; margin-top: 24px; }
.button-wrapper { margin-top: 32px; }
.btn-primary { background-color: #7f5bff; color: white; border: none; padding: 16px 40px; font-weight: 700; font-size: 16px; border-radius: 12px; cursor: pointer; box-shadow: 0 4px 20px rgba(127, 91, 255, 0.3); transition: all 0.2s ease-in-out; }
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 24px rgba(127, 91, 255, 0.4); }
.loading-state, .error-state { text-align: center; }
.error-state h2 { margin-bottom: 1rem; color: #d32f2f; }
</style>
