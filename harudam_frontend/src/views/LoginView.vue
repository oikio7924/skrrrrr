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
          <input type="text" v-model="email" placeholder="이메일 주소 또는 아이디" required />
        </div>
        <div class="input-group">
          <input type="password" v-model="password" placeholder="패스워드" required />
        </div>
        <div class="options">
          <label class="checkbox-label">
            <input type="checkbox" v-model="keepLoggedIn" />
            로그인 상태 유지
          </label>
        </div>
        <button type="submit" class="login-button">로그인</button>
      </form>

    <div class="links">
      <a href="#" @click.prevent="onFindId">아이디 찾기</a>
        <span class="separator">|</span>
      <a href="#" @click.prevent="onFindPw">비밀번호 찾기</a>
        <span class="separator">|</span>
      <a href="#" @click.prevent="goToSignUp">회원가입</a>
    </div>

      <div class="social-login">
        <button class="social-btn kakao" aria-label="카카오톡으로 로그인" @click="handleKakaoLogin">
          <img src="@/assets/social_login_logo/kakao_logo.png" alt="카카오톡" />
        </button>
        <button class="social-btn naver" aria-label="네이버로 로그인">
          <img src="@/assets/social_login_logo/naver_logo.png" alt="네이버" />
        </button>
        <button class="social-btn google" aria-label="구글로 로그인">
          <img src="@/assets/social_login_logo/google_logo.png" alt="구글" />
        </button>
        <button class="social-btn apple" aria-label="애플로 로그인">
          <img src="@/assets/social_login_logo/apple_logo.png" alt="애플" />
        </button>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// --- 타입 정의 ---
interface KakaoAuthResponse {
  access_token: string;
}
interface KakaoUserInfo {
  id: number;
  properties: {
    nickname: string;
  };
  kakao_account: {
    email?: string;
  };
}

// --- 상태 변수 ---
const router = useRouter();
const isSdkLoaded = ref(false);
const email = ref('');
const password = ref('');
const keepLoggedIn = ref(false);

// --- 새로운 함수: 카카오 SDK 로딩을 기다리는 Promise ---
function loadKakaoSDK(): Promise<void> {
  return new Promise((resolve, reject) => {
    // 이미 SDK가 로드되었다면 즉시 해결
    if (window.Kakao) {
      resolve();
      return;
    }

    // 10초 동안 100ms 간격으로 SDK 로딩을 확인
    let timeout = 10000; // 10초
    const interval = setInterval(() => {
      if (window.Kakao) {
        clearInterval(interval);
        resolve();
      }
      timeout -= 100;
      if (timeout <= 0) {
        clearInterval(interval);
        reject(new Error('Kakao SDK를 불러오는 데 실패했습니다.'));
      }
    }, 100);
  });
}

// --- 생명주기 훅 ---
// onMounted를 async 함수로 변경
onMounted(async () => {
  try {
    // 위에서 만든 SDK 로딩 함수를 호출하고 기다립니다.
    await loadKakaoSDK();

    // SDK가 성공적으로 로드된 후 초기화 로직 실행
    if (!window.Kakao.isInitialized()) {
      const kakaoKey = "b75619eccd59413f7bb2cec4dab7cd44"; // 본인의 JavaScript 키
      window.Kakao.init(kakaoKey);
      console.log('카카오 SDK 초기화 완료');
    }
    isSdkLoaded.value = true;

  } catch (error) {
    console.error(error);
    isSdkLoaded.value = false;
  }
});


// --- 메소드 (함수) ---

// 일반 이메일 로그인 핸들러
function handleLogin() {
  console.log('Email:', email.value, 'Password:', password.value);
  alert(`${email.value} 계정으로 로그인을 시도합니다.`);
}

// 카카오 로그인 핸들러
async function handleKakaoLogin() {
  if (!isSdkLoaded.value) {
    alert('카카오 SDK가 로드되지 않았습니다. 잠시 후 다시 시도해주세요.');
    return;
  }

  try {
    // 1. 로그인 시도
    const authObj: KakaoAuthResponse = await new Promise((resolve, reject) => {
      window.Kakao.Auth.login({
        scope: 'profile_nickname, account_email',
        success: (res: KakaoAuthResponse) => resolve(res),
        fail: (err: unknown) => reject(err),
      });
    });
    console.log('카카오 로그인 성공:', authObj);

    // 2. 토큰 저장
    window.Kakao.Auth.setAccessToken(authObj.access_token);

    // 3. 사용자 정보 요청
    const userInfo: KakaoUserInfo = await new Promise((resolve, reject) => {
      window.Kakao.API.request({
        url: '/v2/user/me',
        success: (res: KakaoUserInfo) => resolve(res),
        fail: (err: unknown) => reject(err),
      });
    });
    console.log('카카오 사용자 정보:', userInfo);

    alert(`카카오 로그인 성공! \n이름: ${userInfo.properties.nickname} \n이메일: ${userInfo.kakao_account?.email || '없음'}`);
  } catch (error) {
    console.error('카카오 로그인 과정에서 에러 발생:', error);
    alert('카카오 로그인에 실패했습니다. 개발자 콘솔을 확인해주세요.');
  }
}


// 기타 페이지 이동 함수
function onFindId() { alert("아이디 찾기 페이지로 이동합니다.") }
function onFindPw() { alert("비밀번호 찾기 페이지로 이동합니다.") }
function goToSignUp() { router.push("/signup") }
function goBack() { router.back() }


</script>

<style scoped>
/* 전체적인 레이아웃과 폰트 설정 */
.login-container {
  max-width: 420px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  color: #333;
}

.header {
  position: relative;
  height: 44px;
  display: flex;
  align-items: center;
}

.back-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 10px;
  position: absolute;
  left: -10px; /* 아이콘 위치 조절 */
}

.main-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 40px;
}

.logo-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.logo {
  width: 120px;
  height: auto;
}

.logo-text {
  margin-top: 5px;
  font-size: 60px;
  font-weight: 400;
  color: #A993E8;
  font-family: 'Dongle', sans-serif;
}

form {
  width: 100%;
}

.input-group {
  margin-bottom: 12px;
}

/* 입력창 스타일 */
input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
}

input::placeholder {
  color: #aaa;
}

.options {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #555;
}

input[type="checkbox"] {
  margin-right: 8px;

}

/* 로그인 버튼 스타일 */
.login-button {
  width: 100%;
  padding: 15px;
  background-color: #A993E8;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #9378d5;
}

.links {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  font-size: 14px;
  color: #777;
  justify-content: center;
}

.links a {
  color: inherit;
  text-decoration: none;
}
.links a:hover {
  text-decoration: underline;
}

.separator {
  color: #ddd;
}

/* 소셜 로그인 버튼 그룹 */
.social-login {
  margin-top: 40px;
  display: flex;
  gap: 20px;
}

.social-btn img {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.social-btn {
  width: 50px;
  height: 50px;
  border-radius: 50%; /* 원형 버튼 */
  border: 1px solid #eee;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  background-color: white;
  transition: opacity 0.2s;
}
.social-btn:hover {
  opacity: 0.8;
}

.social-btn img {
  width: 28px; /* 아이콘 크기 조절 */
  height: 28px;
}

/* 각 소셜 버튼별 배경색 */
.social-btn.kakao { background-color: #FEE500; border: none; }
.social-btn.naver { background-color: #03C75A; border: none; }
.social-btn.apple { background-color: #000000; border: none; }
.social-btn.google { background-color: #FFFFFF; }
</style>
