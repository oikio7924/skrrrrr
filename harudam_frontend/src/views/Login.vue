<template>
  <div class="login-container">
    <header class="header">
      <button class="back-button" @click="goBack" aria-label="뒤로가기">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>
      </button>
    </header>

    <main class="main-content">
      <div class="logo-wrap">
        <img src="@/assets/harudam_logo.png" alt="하루담 로고" class="logo" />
        <p class="logo-text">하루담</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <input type="text" v-model.trim="email" placeholder="이메일 주소 또는 아이디" required />
        </div>
        <div class="input-group">
          <input :type="showPw ? 'text' : 'password'" v-model="password" placeholder="패스워드" required />
          <button type="button" class="eye" @click="showPw = !showPw" aria-label="비밀번호 보기 토글">
            <svg viewBox="0 0 24 24" width="18" height="18"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" fill="none" stroke="currentColor" stroke-width="1.5"/><circle :fill="showPw?'currentColor':'none'" :stroke="showPw?'none':'currentColor'" cx="12" cy="12" r="3" stroke-width="1.5"/></svg>
          </button>
        </div>

        <div class="options">
          <label class="checkbox-label">
            <input type="checkbox" v-model="keepLoggedIn" />
            로그인 상태 유지
          </label>
        </div>

        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? '로그인 중...' : '로그인' }}
        </button>

        <p class="error" v-if="errorMsg">{{ errorMsg }}</p>
      </form>

      <div class="links">
        <a href="#" @click.prevent="onFindId">아이디 찾기</a>
        <span class="separator">|</span>
        <a href="#" @click.prevent="onFindPw">비밀번호 찾기</a>
        <span class="separator">|</span>
        <a href="#" @click.prevent="goToSignUp">회원가입</a>
      </div>

      <div class="social-login">
        <button class="social-btn kakao" aria-label="카카오톡으로 로그인" @click="handleKakaoLogin" :disabled="loading">
          <img src="@/assets/social_login_logo/kakao_logo.png" alt="카카오톡" />
        </button>
        <button class="social-btn naver" aria-label="네이버로 로그인" disabled>
          <img src="@/assets/social_login_logo/naver_logo.png" alt="네이버" />
        </button>
        <button class="social-btn google" aria-label="구글로 로그인" disabled>
          <img src="@/assets/social_login_logo/google_logo.png" alt="구글" />
        </button>

      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/api/index'
import type { AxiosError } from 'axios'
import type { KakaoAuthResponse } from '@/kakao';

type LoginRes = {
  accessToken: string
  refreshToken?: string
  user?: { id: number; email: string; name?: string; needAdditionalInfo?: boolean }
}

const router = useRouter()
const email = ref('')
const password = ref('')
const keepLoggedIn = ref(false)
const showPw = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const isSdkLoaded = ref(false)

/** 유틸: AxiosError 메시지 추출 (any 금지) */
function extractAxiosMsg(err: unknown, fallback = '요청 중 오류가 발생했습니다.') {
  const ax = err as AxiosError<{ message?: string }>
  return ax?.response?.data?.message ?? fallback
}

/** Kakao 얻기 (undefined 안전) */
function getKakao() {
  return window.Kakao
}

/** Kakao SDK 로드 대기 */
function waitKakaoSDK(): Promise<void> {
  return new Promise((resolve, reject) => {
    if (getKakao()) return resolve()
    let left = 10000
    const t = setInterval(() => {
      if (getKakao()) { clearInterval(t); resolve() }
      left -= 100
      if (left <= 0) { clearInterval(t); reject(new Error('Kakao SDK 로드 실패')) }
    }, 100)
  })
}

onMounted(async () => {
  try {
    await waitKakaoSDK()
    const Kakao = getKakao()
    if (Kakao && !Kakao.isInitialized()) {
      Kakao.init(import.meta.env.VITE_KAKAO_JS_KEY as string)
    }
    isSdkLoaded.value = true
  } catch {
    isSdkLoaded.value = false
  }
})

/** 토큰 저장: 인터셉터가 localStorage만 보므로 accessToken은 반드시 거기에 넣는다 */
function persistTokens(accessToken: string, refreshToken?: string) {
  localStorage.setItem('accessToken', accessToken)
  if (refreshToken) localStorage.setItem('refreshToken', refreshToken)
  if (!keepLoggedIn.value) {
    sessionStorage.setItem('accessToken', accessToken)
    if (refreshToken) sessionStorage.setItem('refreshToken', refreshToken)
  }
}

/** 이메일/비번 로그인 */
async function handleLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    const { data } = await apiClient.post<LoginRes>('/auth/login', {
      email: email.value,
      password: password.value,
    })
    persistTokens(data.accessToken, data.refreshToken)
    if (data.user?.needAdditionalInfo) router.push('/signup-detail')
    else router.push('/')
  } catch (err) {
    errorMsg.value = extractAxiosMsg(err, '로그인에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

// 에러 객체의 타입을 명확하게 정의합니다.
interface KakaoLoginError {
  error: string;
  error_description: string;
}

/** 카카오 로그인 */
/** 카카오 로그인 */
function handleKakaoLogin() {
  console.log('1. handleKakaoLogin 함수 시작됨');

  if (!isSdkLoaded.value) {
    errorMsg.value = '카카오 SDK가 준비되지 않았습니다.';
    return;
  }
  const Kakao = getKakao();
  if (!Kakao) {
    errorMsg.value = 'Kakao 객체를 찾을 수 없습니다.';
    return;
  }

  errorMsg.value = '';
  loading.value = true;
  console.log('2. loading 상태를 true로 변경함');

  Kakao.Auth.login({
    scope: 'profile_nickname, account_email',
    success: async (kakaoAuth: KakaoAuthResponse) => {
      console.log('3-success. 카카오 로그인 성공!', kakaoAuth);
      try {
        // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 이 부분이 수정되었습니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼

        // 1. 실제 백엔드 API 호출을 주석 처리합니다. (CORS 에러 방지)
        /*
        const { data } = await apiClient.post<LoginRes>('/auth/oauth/kakao', {
          accessToken: kakaoAuth.access_token,
        });
        */

        // 2. 백엔드에서 성공적으로 받았다고 가정한 '가짜 데이터'를 만듭니다.
        const mockData: LoginRes = {
          accessToken: '이것은_백엔드가_만들어준_가짜_JWT_액세스_토큰입니다',
          refreshToken: '이것은_가짜_리프레시_토큰입니다_ABCDEFG',
          user: {
            id: 100,
            email: 'kakao_user@example.com',
            name: '김카카오',
            // 이 값을 true로 바꾸면 상세정보 페이지로 이동하는지 테스트할 수 있습니다.
            needAdditionalInfo: false
          }
        };
        // 원래 코드의 const { data } = ... 구조와 맞추기 위해 아래와 같이 할당합니다.
        const data = mockData;

        // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲ 수정된 부분 끝 ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲


        // --- 여기부터는 원래 코드 그대로, 위에서 만든 가짜 데이터를 사용합니다 ---
        console.log('가짜 데이터로 로그인 후 로직을 처리합니다:', data);
        persistTokens(data.accessToken, data.refreshToken);

        if (data.user?.needAdditionalInfo) {
          router.push('/signup-detail');
        } else {
          router.push('/');
        }

      } catch (err) {
        // 실제 네트워크 에러가 아니므로 에러 메시지 설정을 비활성화합니다.
        // errorMsg.value = extractAxiosMsg(err, '카카오 로그인 실패');
        console.error("테스트 중 에러 발생 (이 메시지는 거의 볼 일 없음):", err);
      } finally {
        loading.value = false;
        console.log('4-success. success 블록 finally 실행, loading을 false로 변경');
      }
    },
    fail: (error: KakaoLoginError) => {
      console.log('3-fail. 카카오 로그인 실패 또는 취소됨', error);
      errorMsg.value = '카카오 로그인을 취소했습니다.';
      loading.value = false;
      console.log('4-fail. fail 블록 실행, loading을 false로 변경');
    },
  });
}


/** 기타 이동 */
function onFindId() { alert('아이디 찾기 페이지로 이동합니다.') }
function onFindPw() { alert('비밀번호 찾기 페이지로 이동합니다.') }
function goToSignUp() { router.push('/signup') }
function goBack() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}
</script>

<style scoped>
/* 기존 스타일 유지 + 눈아이콘 위치 고정 */
.login-container{ max-width:420px; margin:0 auto; padding:20px; color:#333; }
.header{ position:relative; height:44px; display:flex; align-items:center; }
.back-button{ background:none; border:none; cursor:pointer; padding:10px; position:absolute; left:-10px; }
.main-content{ display:flex; flex-direction:column; align-items:center; padding-top:40px; }
.logo-wrap{ display:flex; flex-direction:column; align-items:center; margin-bottom:30px; }
.logo{ width:120px; height:auto; }
.logo-text{ margin-top:5px; font-size:60px; font-weight:400; color:#A993E8; font-family:'Dongle', sans-serif; }
form{ width:100%; }
.input-group{ position:relative; margin-bottom:12px; }
.input-group input{ width:100%; padding:14px 40px 14px 14px; border:1px solid #e0e0e0; border-radius:10px; font-size:16px; }
.input-group .eye{ position:absolute; top:50%; right:10px; transform:translateY(-50%); border:0; background:transparent; padding:6px; cursor:pointer; color:#666; }
.options{ display:flex; align-items:center; margin:10px 0 18px; }
.checkbox-label{ display:flex; align-items:center; cursor:pointer; font-size:14px; color:#555; }
.checkbox-label input{ margin-right:8px; }
.login-button{ width:100%; padding:14px; background:#A993E8; color:#fff; border:0; border-radius:10px; font-size:17px; font-weight:700; cursor:pointer; transition:background .2s; }
.login-button:hover{ background:#9378d5; }
.login-button:disabled{ opacity:.6; cursor:not-allowed; }
.error{ margin-top:10px; color:#e04545; font-size:14px; text-align:center; }
.links{ margin-top:16px; display:flex; gap:10px; font-size:14px; color:#777; justify-content:center; }
.links a{ color:inherit; text-decoration:none; }
.links a:hover{ text-decoration:underline; }
.separator{ color:#ddd; }
.social-login{ margin-top:32px; display:flex; gap:16px; }
.social-btn{ width:50px; height:50px; border-radius:50%; border:1px solid #eee; display:flex; justify-content:center; align-items:center; cursor:pointer; background:#fff; transition:opacity .2s; }
.social-btn:hover{ opacity:.85; }
.social-btn img{ width:28px; height:28px; object-fit:contain; }
.social-btn.kakao{ background:#FEE500; border:none; }
.social-btn.naver{ background:#03C75A; border:none; }
.social-btn.apple{ background:#000; border:none; }
.social-btn.google{ background:#fff; }
</style>
