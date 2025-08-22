<!-- src/views/LoginView.vue -->
<template>
  <div class="login-page">
    <!-- 상단 뒤로가기 -->
    <button class="back-btn" aria-label="뒤로가기" @click="goBack">
      <svg viewBox="0 0 24 24" class="icon">
        <path d="M15.5 19.5 8 12l7.5-7.5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>

    <main class="container" role="main" aria-labelledby="title">
    <div class="logo-wrap">
      <img src="@/assets/harudam_logo.png" alt="하루담 로고" class="logo" />
      <p class="logo-text">하루담</p>
    </div>

      <!-- 카피 -->
      <h2 id="title" class="title">
        가장 편한 방법으로<br/>시작해 보세요!
      </h2>
      <p class="subtitle">
        <span class="highlight">1분</span>이면 회원가입 가능해요
      </p>

      <!-- 소셜 로그인 버튼들 (프론트 전용, 모의 동작) -->
<!-- 소셜 로그인 버튼들 (프론트 전용, 모의 동작) -->
<section class="buttons">
  <!-- 카카오 -->
  <button class="btn btn-kakao" :disabled="!!loading || !isKakaoReady" @click="handleSocialSignup('kakao')" aria-label="카카오톡으로 계속하기">
    <span class="btn-icon" aria-hidden="true">
      <img src="@/assets/social_login_logo/kakao_logo.png" alt="카카오톡" />
    </span>
    <span class="btn-text">카카오톡으로 계속하기</span>
  </button>

  <!-- 네이버 -->
  <button class="btn btn-naver" :disabled="!!loading" @click="handleSocialSignup('naver')" aria-label="네이버로 계속하기">
    <span class="btn-icon" aria-hidden="true">
      <img src="@/assets/social_login_logo/naver_logo.png" alt="네이버" />
    </span>
    <span class="btn-text">네이버로 계속하기</span>
  </button>

  <!-- 구글 -->
  <button class="btn btn-google" :disabled="!!loading" @click="handleSocialSignup('google')" aria-label="Google로 계속하기">
    <span class="btn-icon" aria-hidden="true">
      <img src="@/assets/social_login_logo/google_logo.png" alt="Google" />
    </span>
    <span class="btn-text">Google로 계속하기</span>
  </button>

  <!-- 애플 -->
  <button class="btn btn-apple" :disabled="!!loading" @click="handleSocialSignup('apple')" aria-label="Apple로 계속하기">
    <span class="btn-icon" aria-hidden="true">
      <img src="@/assets/social_login_logo/apple_logo.png" alt="Apple" />
    </span>
    <span class="btn-text">Apple로 계속하기</span>
  </button>
</section>


      <!-- 상태 표시 (프론트 모의) -->
      <div class="status" v-if="loading || profile || error">
        <p v-if="loading">연결 중… ({{ loading }})</p>
        <p v-else-if="profile"><b>{{ profile.name }}</b>님, {{ profile.provider }} 로그인 성공!</p>
        <p v-else-if="error" class="err">{{ error }}</p>
        <button class="mini" v-if="profile || error" @click="reset">초기화</button>
      </div>
    </main>
  </div>
</template>

<!-- ...existing code... -->
<!-- ...existing code... -->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

/** ===== 타입 정의 ===== */
type Provider = 'kakao' | 'naver' | 'google' | 'apple'
type SocialProfile = { provider: Provider; id: string; name: string; email?: string }

interface KakaoAuthResponse { access_token: string }
interface KakaoUserInfo {
  id: number
  properties?: { nickname?: string }
  kakao_account?: { email?: string }
}
interface KakaoAuth {
  login(opts: {
    scope?: string
    success: (res: KakaoAuthResponse) => void
    fail: (err: unknown) => void
  }): void
  logout(callback?: () => void): void
  getAccessToken(): string | null
}
interface KakaoAPI {
  request(opts: {
    url: string
    success: (res: KakaoUserInfo) => void
    fail: (err: unknown) => void
  }): void
}
interface KakaoSDK {
  isInitialized(): boolean
  init(appKey: string): void
  Auth: KakaoAuth
  API: KakaoAPI
}
type KakaoWindow = Window & { Kakao?: KakaoSDK }

/** ===== 공통 상태 ===== */
const router = useRouter()
const loading = ref<Provider | null>(null)
const profile = ref<SocialProfile | null>(null)
const error = ref<string | null>(null)

/** ===== Kakao SDK 준비 상태 ===== */
const isSdkLoaded = ref(false)
const hasKakaoKey = ref(false)
const isKakaoReady = computed(() => isSdkLoaded.value && hasKakaoKey.value)

/** ===== Kakao 안전 접근자 ===== */
function getKakao(): KakaoSDK | null {
  return (window as KakaoWindow).Kakao ?? null
}

/** ===== 환경변수 ===== */
function readKakaoKey(): string {
  const key = (import.meta.env.VITE_KAKAO_JS_KEY as string | undefined)?.trim() ?? ''
  hasKakaoKey.value = !!key
  if (!hasKakaoKey.value) {
    console.warn('[Kakao] VITE_KAKAO_JS_KEY가 비어있습니다. .env 설정 후 dev 서버를 재시작하세요.')
  }
  return key
}

/** ===== SDK 로더 ===== */
function loadKakaoSdk(): Promise<void> {
  return new Promise((resolve, reject) => {
    if (getKakao()) return resolve()
    const s = document.createElement('script')
    s.src = 'https://t1.kakaocdn.net/kakao_js_sdk/2.7.2/kakao.min.js'
    s.onload = () => resolve()
    s.onerror = () => reject(new Error('[Kakao] SDK load failed'))
    document.head.appendChild(s)
  })
}

/** ===== 초기화 ===== */
function initKakao(appKey: string): void {
  const Kakao = getKakao()
  if (!Kakao) return
  if (!appKey) return
  if (!Kakao.isInitialized()) {
    Kakao.init(appKey)
    console.log('[Kakao] initialized')
  }
  isSdkLoaded.value = true
}

/** ===== 마운트 ===== */
onMounted(async () => {
  const key = readKakaoKey()
  try {
    await loadKakaoSdk()
    initKakao(key)

    const Kakao = getKakao()
    if (Kakao?.Auth.getAccessToken()) {
      Kakao.Auth.logout(() => console.log('[Kakao] old token cleared'))
    }
  } catch (e) {
    console.error(e)
  }
})

/** ===== 카카오 로그인 핸들러 ===== */
async function handleSocialSignup(provider: Provider) {
  if (provider !== 'kakao') return

  if (!isKakaoReady.value) {
    alert('카카오 준비 중입니다. .env 설정/서버 재시작을 확인해주세요.')
    return
  }

  loading.value = 'kakao'
  error.value = null

  try {
    const Kakao = getKakao()
    if (!Kakao) throw new Error("SDK 없음")

    // 기존 토큰 정리
    if (Kakao.Auth.getAccessToken()) {
      await new Promise<void>((resolve) => Kakao.Auth.logout(() => resolve()))
    }

    // ✅ 로그인 시도 + 타임아웃 안전장치
    await new Promise<void>((resolve, reject) => {
      let done = false

      // 10초 후 자동 종료 (fail이 안 불릴 때 대비)
      const timer = setTimeout(() => {
        if (!done) {
          loading.value = null
          reject(new Error("카카오 로그인 응답 없음 (타임아웃)"))
        }
      }, 10000)

      Kakao.Auth.login({
        scope: 'account_email,profile_nickname',
        success: () => {
          done = true
          clearTimeout(timer)
          resolve()
        },
        fail: (err) => {
          done = true
          clearTimeout(timer)
          loading.value = null
          reject(err)
        },
      })
    })

    // 👉 여기까지 오면 로그인 성공
    console.log("카카오 로그인 성공")
    const token = Kakao.Auth.getAccessToken()
    console.log("accessToken:", token)

  } catch (err) {
    console.error("카카오 로그인 과정에서 에러 발생:", err)
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    // 버튼 원상복구
    loading.value = null
  }
}





/** ===== 유틸 ===== */
function reset() {
  profile.value = null
  error.value = null
  loading.value = null
}
function goBack() {
  router.back()
}
console.log('[env] VITE_KAKAO_JS_KEY =', (import.meta.env.VITE_KAKAO_JS_KEY ?? '(undefined)'))

</script>






<!-- ...existing code... -->

<style scoped>
/* 레이아웃 */
@import url('https://fonts.googleapis.com/css2?family=Dongle:wght@400;700&display=swap');
.login-page {
  max-width: 420px; /* 모바일에서 보기 적당한 사이즈 */
  margin: 0 auto;
  padding: 24px;
}


/* 세로 중앙 정렬 핵심 */
.container {
  flex: 1;
  max-width: 420px;
  margin: 0 auto;

  padding-left: max(16px, env(safe-area-inset-left));
  padding-right: max(16px, env(safe-area-inset-right));
  padding-top: calc(28px + env(safe-area-inset-top) + 20px);
  padding-bottom: calc(28px + env(safe-area-inset-bottom));

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;

}


/* 뒤로가기 */
.back-btn {
  position: fixed; top: 12px; left: 12px; width: 40px; height: 40px;
  display: grid; place-items: center; border: none; background: transparent;
  color: #2d251c; cursor: pointer;
}
.back-btn .icon { width: 24px; height: 24px; }

.logo {
  width: 120px;
  height: auto;
}

.logo-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 0px;
}

.logo-text {
  margin-top: 5px;
  font-size: 60px;
  font-weight: 400;
  color: #A993E8;
  font-family: 'Dongle', sans-serif;
}

.title {
  margin: 4px;
  font-size: 20px;
  text-align: center;
  color: #8869BA;
  font-weight: 900;
}
.subtitle {
  margin: 0 0 14px;
  text-align: center;
  font-size: 14px;
  color: #9b9aa1;
}

.subtitle .highlight {
  color: #8869BA;
  font-weight: 800;
}

/*버튼  */
.btn {
  width: 100%;
  height: 50px;
  border-radius: 8px;
  border: 1px solid transparent;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  padding: 0 16px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s;
  box-shadow: 0 3px 6px rgba(0,0,0,0.08);
  padding-left: 48px;
  text-align: center;
  position: relative;
  margin: 0;
}

.btn-text {
  flex: 1;              /* 남는 공간 차지 */
  text-align: center;   /* 텍스트 중앙 */
}


.btn-icon {
  position: absolute;
  left: 16px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* PNG 비율 유지 */
}
.btn-google {
  background: #fff;
  border: 1px solid #ddd;
  color: #444;
}


/* 로고/타이틀 */
.hero {
  text-align: center;
  margin-top: 60px;  /* ⬅ 아래로 내림 */
  margin-bottom: 40px;
}

/* 버튼 그룹 */
.buttons {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px; /* 버튼 간격 조금 넓힘 */
  margin: auto;
  max-width: 360px;
}

.page {
  display: flex;
  flex-direction: column;
  justify-content: center; /* 수직 중앙 */
  min-height: 100vh;
  align-items: center;
  padding: 24px 16px;       /* 안전한 여백 */
  box-sizing: border-box;
}

.page {
  padding-bottom: calc(env(safe-area-inset-bottom) + 16px);
  padding-top: calc(env(safe-area-inset-top) + 16px);
}

/* 서비스별 색상 */
.btn:hover {
  filter: brightness(95%);
}
.btn:active {
  transform: scale(0.97);
  filter: brightness(90%);
}

/* 개별 브랜드 컬러 */
.btn-kakao {
  background-color: #FEE500;
  color: #000;
}
.btn-naver {
  background-color: #03C75A;
  color: #fff;
}
.btn-google {
  background-color: #fff;
  color: #000;
  border: 1px solid #ddd;
}
.btn-apple {
  background-color: #000;
  color: #fff;
}
/* 버튼 효과 */
.btn:hover:not(:disabled) {
  filter: brightness(0.97);
}
.btn:active:not(:disabled) {
  transform: scale(0.98); /* 살짝 눌린 듯 */
  box-shadow: none;       /* ✅ 눌릴 때도 그림자 안 생기게 */
}

/* 상태 표시(작게) */
.status { margin-top: 12px; font-size: 12px; color: #444; }
.status .err { color: #c0392b; }
.mini { margin-top: 6px; border: none; padding: 6px 10px; border-radius: 8px; background: #efeff6; cursor: pointer; }

/* ====== 세로가 짧은 화면에서 더 압축 ====== */
@media (max-height: 740px) {
  .logo { width: 120px; }
  .title { font-size: 20px; margin-top: 10px; }
  .buttons { gap: 8px; }
  .btn { height: 48px; font-size: 15px; border-radius: 10px; }
}
</style>
