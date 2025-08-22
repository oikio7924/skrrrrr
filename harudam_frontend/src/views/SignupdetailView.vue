<!-- src/views/PostSocialProfileForm.vue -->
<template>
  <div class="page">
    <!-- 헤더 -->
    <header class="topbar">
      <button class="icon-btn" @click="goBack" aria-label="뒤로가기">✕</button>
      <h1 class="title">회원가입</h1>
      <div class="spacer" />
    </header>

    <!-- 스크롤 영역 -->
    <main class="scroll">
      <!-- 1번 섹션 -->
      <section class="card" aria-labelledby="sec1">
        <h2 id="sec1" class="sr-only">기본 정보</h2>

        <label class="row">
          <span class="label">아이디(이메일)</span>
          <div class="field">
            <input
              v-model.trim="form.email"
              type="email"
              inputmode="email"
              placeholder="이메일 입력"
              :readonly="!!prefilled.email"
              autocomplete="email"
            />
            <button class="micro-btn" type="button" @click="checkEmail" :disabled="!form.email">중복확인</button>
          </div>
        </label>

        <label class="row">
          <span class="label">비밀번호</span>
          <div class="field">
            <input
              :type="showPw ? 'text' : 'password'"
              v-model="form.password"
              placeholder="비밀번호"
              autocomplete="new-password"
            />
            <button class="icon-btn ghost" type="button" @click="showPw = !showPw" :aria-pressed="showPw" aria-label="비밀번호 표시 토글">
              <!-- 깔끔한 아이콘 -->
              <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
                <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" fill="none" stroke="currentColor" stroke-width="1.5"/>
                <circle :fill="showPw ? 'currentColor' : 'none'" :stroke="showPw ? 'none' : 'currentColor'" cx="12" cy="12" r="3" stroke-width="1.5"/>
              </svg>
            </button>
          </div>
          <p class="hint">영문/숫자 조합 8자 이상 권장</p>
        </label>

        <label class="row">
          <span class="label">비밀번호 확인</span>
          <div class="field">
            <input
              :type="showPw2 ? 'text' : 'password'"
              v-model="form.passwordConfirm"
              placeholder="비밀번호 확인"
              autocomplete="new-password"
            />
            <button class="icon-btn ghost" type="button" @click="showPw2 = !showPw2" :aria-pressed="showPw2" aria-label="비밀번호 표시 토글">
              <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
                <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" fill="none" stroke="currentColor" stroke-width="1.5"/>
                <circle :fill="showPw2 ? 'currentColor' : 'none'" :stroke="showPw2 ? 'none' : 'currentColor'" cx="12" cy="12" r="3" stroke-width="1.5"/>
              </svg>
            </button>
          </div>
          <p class="error" v-if="form.password && form.passwordConfirm && form.password !== form.passwordConfirm">
            비밀번호가 일치하지 않습니다.
          </p>
        </label>

        <label class="row">
          <span class="label">이름</span>
          <div class="field">
            <input v-model.trim="form.name" type="text" placeholder="실명 입력" :readonly="!!prefilled.name" />
          </div>
        </label>

        <label class="row">
          <span class="label">휴대폰 번호</span>
          <div class="field">
            <input v-model.trim="form.phone" type="tel" inputmode="numeric" placeholder="'-' 없이 입력" />
            <button class="micro-btn" type="button" @click="sendCode" :disabled="!phoneValid">인증번호 전송</button>
          </div>
        </label>
      </section>

      <!-- 2번 섹션 -->
      <section class="card" aria-labelledby="sec2">
        <h2 id="sec2" class="sr-only">추가 정보</h2>

        <label class="row">
          <span class="label">인증번호</span>
          <div class="field">
            <input v-model.trim="form.smsCode" type="text" inputmode="numeric" placeholder="인증번호 입력" />
            <button class="micro-btn" type="button" @click="verifyCode" :disabled="!form.smsCode">인증확인</button>
          </div>
          <p class="hint" v-if="smsInfo">{{ smsInfo }}</p>
        </label>

        <label class="row">
          <span class="label">생년월일</span>
          <div class="field">
            <input v-model="form.birthday" type="date" placeholder="YYYY-MM-DD" />
          </div>
        </label>

        <label class="row">
          <span class="label">주소(선택)</span>
          <div class="field">
            <input v-model.trim="form.address" type="text" placeholder="건물, 지번 또는 도로명 검색" />
          </div>
          <p class="hint">선택 입력</p>
        </label>

        <fieldset class="row">
          <legend class="label">성별</legend>
          <div class="radios">
            <label class="radio">
              <input type="radio" value="F" v-model="form.gender" /> <span>여성</span>
            </label>
            <label class="radio">
              <input type="radio" value="M" v-model="form.gender" /> <span>남성</span>
            </label>
          </div>
        </fieldset>

        <!-- 약관 바텀시트 열기 -->
        <button type="button" class="sheet-open" @click="openSheet = true">약관 동의하기</button>
      </section>

      <!-- 여백(푸터/시트와 겹치지 않게) -->
      <div style="height: 140px" />
    </main>

    <!-- 하단 고정 푸터: 앱 일체감 -->
    <footer class="app-footer" role="contentinfo">
      <button
        class="cta"
        :disabled="!canSubmit"
        @click="submit"
        aria-label="부모정보 입력하기"
      >
        부모정보 입력하기
      </button>
    </footer>

    <!-- 약관 바텀시트 -->
    <teleport to="body">
      <div v-if="openSheet" class="sheet-backdrop" @click.self="closeSheet">
        <section
          class="sheet"
          role="dialog"
          aria-modal="true"
          aria-labelledby="agreementsTitle"
          @keydown.esc="closeSheet"
        >
          <div class="sheet-handle" aria-hidden="true"></div>
          <header class="sheet-header">
            <h3 id="agreementsTitle">약관 동의</h3>
            <button class="icon-btn ghost" @click="closeSheet" aria-label="닫기">✕</button>
          </header>

          <div class="sheet-body">
            <label class="check big">
              <input type="checkbox" :checked="allAgreed" @change="toggleAll($event)">
              <span>모두 동의합니다</span>
            </label>

            <label class="check">
              <input type="checkbox" v-model="agreements.termsRequired" />
              <span>이용약관 동의 (필수)</span>
              <button type="button" class="link" @click="openPolicy('terms')">보기</button>
            </label>

            <label class="check">
              <input type="checkbox" v-model="agreements.privacyRequired" />
              <span>개인정보 처리방침 동의 (필수)</span>
              <button type="button" class="link" @click="openPolicy('privacy')">보기</button>
            </label>

            <label class="check">
              <input type="checkbox" v-model="agreements.marketingOptional" />
              <span>마케팅 정보 수신 동의 (선택)</span>
              <button type="button" class="link" @click="openPolicy('marketing')">보기</button>
            </label>
          </div>

          <div class="sheet-footer">
            <button class="cta" :disabled="!requiredAgreed" @click="closeSheet">
              동의하고 계속하기
            </button>
          </div>
        </section>
      </div>
    </teleport>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const prefilled = reactive<{ email?: string; name?: string }>({
  email: '',
  name: '',
})

const form = reactive({
  email: prefilled.email ?? '',
  password: '',
  passwordConfirm: '',
  name: prefilled.name ?? '',
  phone: '',
  smsCode: '',
  birthday: '',
  address: '',
  gender: 'F' as 'F' | 'M',
  phoneVerified: false,
})

const agreements = reactive({
  termsRequired: false,
  privacyRequired: false,
  marketingOptional: false,
})

const showPw = ref(false)
const showPw2 = ref(false)
const smsInfo = ref('')
const openSheet = ref(false)

const phoneValid = computed(() => /^\d{9,11}$/.test(form.phone))
const passwordsOk = computed(() => !!form.password && form.password === form.passwordConfirm)
const requiredAgreed = computed(() => agreements.termsRequired && agreements.privacyRequired)
const allAgreed = computed(() => requiredAgreed.value && agreements.marketingOptional)

const canSubmit = computed(() =>
  !!form.email &&
  passwordsOk.value &&
  !!form.name &&
  phoneValid.value &&
  form.phoneVerified &&
  !!form.birthday &&
  !!form.gender &&
  requiredAgreed.value
)

onMounted(() => {
  if (prefilled.email) form.email = prefilled.email
  if (prefilled.name) form.name = prefilled.name
})

watch(openSheet, (v) => {
  // 바텀시트 열릴 때 배경 스크롤 잠금
  document.body.style.overflow = v ? 'hidden' : ''
})

function goBack() {
  if (history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}
function checkEmail() { alert(`(예시) ${form.email} 사용 가능 여부 확인`) }

async function sendCode() {
  if (!phoneValid.value) return
  smsInfo.value = '인증번호를 전송했습니다. 3분 이내에 입력해 주세요.'
  alert('(예시) 인증번호를 전송했습니다.')
}
function verifyCode() {
  if (!form.smsCode) return
  form.phoneVerified = true
  smsInfo.value = '인증이 완료되었습니다.'
  alert('(예시) 휴대폰 인증이 완료되었습니다.')
}
function openPolicy(kind: 'terms' | 'privacy' | 'marketing') {
  alert(`(예시) ${kind} 약관 모달/페이지로 이동`)
}
function toggleAll(e: Event) {
  const checked = (e.target as HTMLInputElement).checked
  agreements.termsRequired = checked
  agreements.privacyRequired = checked
  agreements.marketingOptional = checked
}
function closeSheet() { openSheet.value = false }

function submit() {
  if (!canSubmit.value) {
    openSheet.value = true // 필수 동의 안됐으면 시트부터 유도
    return
  }
  const payload = { ...form, agreements: { ...agreements } }
  console.log('submit payload', payload)
  alert('제출 완료! 부모정보 입력 화면으로 이동합니다.')
  router.push('/parent-info')
}
</script>

<style scoped>
:root { --brand:#7b61ff; --text:#222; --muted:#777; --line:#eaeaf0; }
.page {
  min-height: 100dvh;
  display: grid;
  grid-template-rows: auto 1fr auto;
  background: #fafafa;
}
.topbar {
  height: 52px;
  display: grid;
  grid-template-columns: 52px 1fr 52px;
  align-items: center;
  padding: 0 4px;
  background: #fff;
  border-bottom: 1px solid var(--line);
}
.title { text-align: center; font-size: 16px; font-weight: 700; }
.icon-btn { background: none; border: 0; padding: 8px; font-size: 18px; cursor: pointer; }
.icon-btn.ghost { color:#52525b; }
.spacer { width: 48px; }

/* 가운데 정렬 + 폭 안정화 */
.scroll {
  overflow: auto;
  padding: 16px 12px 0;
  display: grid;
  justify-content: center;
}
.card {
  width: min(560px, 100%);
  background: #fff;
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 1px 0 rgba(0,0,0,0.04);
  border: 1px solid var(--line);
}
.card + .card { margin-top: 14px; }

/* 폼 */
.row { display: block; margin: 14px 0; }
.label { display: block; font-size: 13px; font-weight: 700; margin-bottom: 6px; color: var(--text); }
.field {
  display: flex; align-items: center; gap: 8px;
  background: #f8f8fb; border: 1px solid #ececf2; border-radius: 12px; padding: 12px;
}
.field input { flex: 1; border: none; background: transparent; outline: none; font-size: 15px; }
.micro-btn {
  border: 1px solid #c9b7ff; background: #fff; border-radius: 14px;
  padding: 7px 12px; font-size: 12px; color: var(--brand); cursor: pointer;
}
.micro-btn:disabled { opacity: .5; cursor: not-allowed; }
.hint { margin-top: 6px; font-size: 12px; color: var(--muted); }
.error { margin-top: 6px; font-size: 12px; color: #e04545; }

.radios { display: flex; gap: 16px; }
.radio { display: inline-flex; align-items: center; gap: 6px; font-size: 14px; }

/* 약관 버튼 */
.sheet-open {
  margin-top: 6px; width: 100%;
  height: 44px; border-radius: 10px; border: 1px solid var(--line);
  background:#fff; font-weight:600; cursor:pointer;
}

/* 하단 앱 푸터: 일체감 + 안전영역 */
.app-footer{
  position: fixed; left: 0; right: 0; bottom: 0;
  backdrop-filter: saturate(180%) blur(10px);
  background: rgba(255,255,255,.9);
  border-top: 1px solid var(--line);
  padding: 10px clamp(12px, 4vw, 24px) calc(10px + env(safe-area-inset-bottom));
  display: grid; grid-template-columns: 1fr; justify-items:center;
  box-shadow: 0 -8px 24px rgba(0,0,0,.06);
}
.app-footer .cta{
  width: min(560px, 100%);
  height: 50px;
  border: none; border-radius: 12px;
  font-weight: 700; font-size: 15px; color: #fff;
  background: var(--brand);
  cursor: pointer;
}
.app-footer .cta:disabled { opacity: .4; cursor: not-allowed; }

/* 바텀시트 */
.sheet-backdrop{
  position: fixed; inset: 0; background: rgba(0,0,0,.35);
  display: grid; place-items: end center;
  z-index: 50;
}
.sheet{
  width: 100%; max-width: 720px;
  background: #fff; border-radius: 18px 18px 0 0;
  box-shadow: 0 -12px 32px rgba(0,0,0,.15);
  border-top: 1px solid var(--line);
  max-height: 80dvh; /* 위로 끌어올라오는 느낌 */
  overflow: hidden; display: grid;
  grid-template-rows: auto 1fr auto;
  animation: slideUp .16s ease-out;
}
@keyframes slideUp { from { transform: translateY(20px); opacity:.9 } to { transform: translateY(0); opacity:1 } }
.sheet-handle{
  width: 48px; height: 5px; background:#ddd; border-radius: 999px;
  margin: 10px auto 0;
}
.sheet-header{
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 16px 6px;
}
.sheet-header h3{ font-size: 16px; font-weight: 700; }
.sheet-body{
  overflow: auto; padding: 6px 16px 16px;
}
.sheet-footer{
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  border-top: 1px solid var(--line);
  background: #fff;
}
.sheet-footer .cta{
  width: 100%; height: 48px;
  border: none; border-radius: 12px; font-weight: 700; color: #fff; background: var(--brand);
}

.check { display: flex; align-items: center; gap: 10px; margin: 10px 0; font-size: 14px; }
.check.big{ padding: 4px 0; font-size: 15px; font-weight: 700; }
.link { margin-left: auto; background: none; border: none; color: var(--brand); text-decoration: underline; cursor: pointer; }

/* A11y */
.sr-only {
  position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden;
  clip: rect(0, 0, 0, 0); white-space: nowrap; border: 0;
}
</style>
