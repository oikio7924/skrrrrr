<!-- src/views/SignupdetailView_Child.vue -->
<template>
  <div class="viewport">
    <div class="centerer">
      <div class="app-container">
        <div class="page">
          <!-- 헤더 -->
          <header class="topbar">
            <button class="icon-btn" @click="goBack" aria-label="뒤로가기">✕</button>
            <h1 class="title">회원가입</h1>
            <div class="spacer" />
          </header>

          <!-- 폼 -->
          <form class="scroll-container" @submit.prevent="submit">
            <div class="content-wrapper">
              <!-- 1) 회원 정보 카드 -->
              <section class="card" aria-labelledby="sec1">
                <h2 id="sec1" class="sr-only">회원 정보 입력</h2>

                <!-- 아이디 -->
                <label class="row">
                  <span class="label">아이디(이메일)</span>
                  <div class="field">
                    <input v-model.trim="form.email" type="email" inputmode="email" placeholder="이메일 입력"
                      :readonly="!!prefilled.email" autocomplete="email" />
                    <button class="micro-btn" type="button" @click="checkEmail" :disabled="!form.email">
                      중복확인
                    </button>
                  </div>
                </label>

                <!-- 비밀번호 -->
                <label class="row">
                  <span class="label">비밀번호</span>
                  <div class="field">
                    <input :type="showPw ? 'text' : 'password'" v-model="form.password" placeholder="비밀번호"
                      autocomplete="new-password" />
                    <button class="icon-btn ghost" type="button" @click="showPw = !showPw" :aria-pressed="showPw"
                      aria-label="비밀번호 표시 토글">
                      <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
                        <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" fill="none" stroke="currentColor"
                          stroke-width="1.5" />
                        <circle :fill="showPw ? 'currentColor' : 'none'" :stroke="showPw ? 'none' : 'currentColor'"
                          cx="12" cy="12" r="3" stroke-width="1.5" />
                      </svg>
                    </button>
                  </div>
                  <p class="hint">영문/숫자 조합 8자 이상 권장</p>
                </label>

                <!-- 비밀번호 확인 -->
                <label class="row">
                  <span class="label">비밀번호 확인</span>
                  <div class="field">
                    <input :type="showPw2 ? 'text' : 'password'" v-model="form.passwordConfirm" placeholder="비밀번호 확인"
                      autocomplete="new-password" />
                    <button class="icon-btn ghost" type="button" @click="showPw2 = !showPw2" :aria-pressed="showPw2"
                      aria-label="비밀번호 표시 토글">
                      <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
                        <path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12Z" fill="none" stroke="currentColor"
                          stroke-width="1.5" />
                        <circle :fill="showPw2 ? 'currentColor' : 'none'" :stroke="showPw2 ? 'none' : 'currentColor'"
                          cx="12" cy="12" r="3" stroke-width="1.5" />
                      </svg>
                    </button>
                  </div>
                  <p class="error"
                    v-if="form.password && form.passwordConfirm && form.password !== form.passwordConfirm">
                    비밀번호가 일치하지 않습니다.
                  </p>
                </label>

                <!-- 이름 -->
                <label class="row">
                  <span class="label">이름</span>
                  <div class="field">
                    <input v-model.trim="form.name" type="text" placeholder="실명 입력" :readonly="!!prefilled.name" />
                  </div>
                </label>

                <!-- 생년월일 -->
                <label class="row">
                  <span class="label">생년월일</span>
                  <div class="field">
                    <input v-model="form.birthday" type="date" placeholder="YYYY-MM-DD" />
                  </div>
                </label>

                <!-- 성별 -->
                <fieldset class="row">
                  <legend class="label">성별</legend>
                  <div class="gender-selector">
                    <label :class="['gender-option', { active: form.gender === 'F' }]">
                      <input type="radio" value="F" v-model="form.gender" class="sr-only" />
                      <span>여성</span>
                    </label>
                    <label :class="['gender-option', { active: form.gender === 'M' }]">
                      <input type="radio" value="M" v-model="form.gender" class="sr-only" />
                      <span>남성</span>
                    </label>
                  </div>
                </fieldset>

                <!-- 휴대폰 -->
                <label class="row">
                  <span class="label">휴대폰 번호</span>
                  <div class="field">
                    <input v-model.trim="form.phone" type="tel" inputmode="numeric" placeholder="'-' 없이 입력" />
                    <button class="micro-btn" type="button" @click="sendCode" :disabled="!phoneValid">인증번호 전송</button>
                  </div>
                </label>

                <!-- 인증번호 -->
                <label class="row">
                  <span class="label">인증번호</span>
                  <div class="field">
                    <input v-model.trim="form.smsCode" type="text" inputmode="numeric" placeholder="인증번호 입력" />
                    <button class="micro-btn" type="button" @click="verifyCode" :disabled="!form.smsCode">인증확인</button>
                  </div>
                  <p class="hint" v-if="smsInfo">{{ smsInfo }}</p>
                </label>

                <!-- 본인 사진 -->
                <!-- 📌 사진 첨부 -->
                <div class="row">
                  <span class="label">본인 사진</span>
                  <label class="field photo-field">
                    <span class="placeholder">
                      {{ form.childPhoto ? form.childPhoto.name : 'ex: 정면을 응시한 사진' }}
                    </span>
                    <input type="file" @change="handlePhotoUpload" accept="image/*" class="sr-only" />
                    <span class="micro-btn">첨부하기</span>
                  </label>

                  <!-- 미리보기 + 삭제 -->
                  <div v-if="photoPreviewUrl" class="photo-preview-wrapper">
                    <img :src="photoPreviewUrl" alt="자녀 사진 미리보기" class="photo-preview" />
                    <button type="button" class="micro-btn danger" @click="removePhoto">삭제</button>
                  </div>
                </div>

                <!-- 📌 목소리 녹음 -->
                <div class="row">
                  <span class="label">본인 목소리</span>
                  <div class="field voice-field">
                    <span class="placeholder">
                      <span v-if="isRecording">녹음 중... 🎙️</span>
                      <span v-else-if="recordedAudioUrl">녹음 완료! 아래에서 확인하세요.</span>
                      <span v-else>본인의 목소리를 녹음해주세요.</span>
                    </span>
                    <button v-if="!isRecording && !recordedAudioUrl" @click="startRecording" type="button"
                      class="micro-btn">녹음 시작</button>
                    <button v-if="isRecording" @click="stopRecording" type="button" class="micro-btn stop">녹음
                      중지</button>
                    <button v-if="!isRecording && recordedAudioUrl" @click="resetRecording" type="button"
                      class="micro-btn">다시 녹음</button>
                    <button v-if="!isRecording && recordedAudioUrl" @click="removeRecording" type="button"
                      class="micro-btn danger">삭제</button>
                  </div>

                  <div v-if="recordedAudioUrl" class="audio-player-wrapper">
                    <audio :src="recordedAudioUrl" controls></audio>
                  </div>
                </div>

                <!-- 주소 -->
                <div class="row">
                  <span class="label">주소(선택)</span>
                  <div class="field">
                    <input v-model.trim="form.address" type="text" placeholder="주소 검색" readonly
                      @click="handleAddressSearch" />
                    <button class="micro-btn" type="button" @click="handleAddressSearch">주소 검색</button>
                  </div>
                </div>
                <label class="row">
                  <span class="label">상세주소</span>
                  <div class="field">
                    <input v-model.trim="form.addressDetail" type="text" placeholder="예: 00동 000호, 건물명" />
                  </div>
                </label>
              </section>

              <!-- 2) 약관 카드 -->
              <section class="card" aria-labelledby="sec3">
                <h2 id="sec3" class="agreements-title">약관 동의</h2>
                <div class="agreements-body">
                  <label class="check big">
                    <input type="checkbox" v-model="allAgreed" />
                    <span class="custom-checkbox"></span>
                    <span>모두 동의합니다</span>
                  </label>
                  <hr class="divider" />

                  <label class="check">
                    <input type="checkbox" v-model="agreements.termsRequired" />
                    <span class="custom-checkbox"></span>
                    <span>이용약관 동의 (필수)</span>
                  </label>

                  <label class="check">
                    <input type="checkbox" v-model="agreements.privacyRequired" />
                    <span class="custom-checkbox"></span>
                    <span>개인정보 처리방침 동의 (필수)</span>
                    <button type="button" class="link" @click.stop="openPolicy('privacy')">보기</button>
                  </label>

                  <label class="check">
                    <input type="checkbox" v-model="agreements.marketingOptional" />
                    <span class="custom-checkbox"></span>
                    <span>마케팅 정보 수신 동의 (선택)</span>
                    <button type="button" class="link" @click.stop="openPolicy('marketing')">보기</button>
                  </label>
                </div>
              </section>

              <!-- CTA 버튼 -->
              <button type="submit" class="cta" aria-label="부모정보 입력하기">
                부모정보 입력하기
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios';

const router = useRouter()

const prefilled = reactive<{ email?: string; name?: string }>({
  email: '',
  name: '',
})

const form = reactive({
  email: '',
  password: '',
  passwordConfirm: '',
  name: '',
  phone: '',
  smsCode: '',
  birthday: '',
  address: '',
  addressDetail: '',
  gender: 'F' as 'F' | 'M',
  phoneVerified: false,
  childPhoto: null as File | null,
  childVoice: null as Blob | null,
})

const agreements = reactive({
  termsRequired: false,
  privacyRequired: false,
  marketingOptional: false,
})

const showPw = ref(false)
const showPw2 = ref(false)
const smsInfo = ref('')

const phoneValid = computed(() => /^\d{10,11}$/.test(form.phone))
const passwordsOk = computed(() => !!form.password && form.password === form.passwordConfirm)
const requiredAgreed = computed(() => agreements.termsRequired && agreements.privacyRequired)

const allAgreed = computed({
  get: () => requiredAgreed.value && agreements.marketingOptional,
  set: (val: boolean) => {
    agreements.termsRequired = val
    agreements.privacyRequired = val
    agreements.marketingOptional = val
  },
})

onMounted(() => {
  if (prefilled.email) form.email = prefilled.email
  if (prefilled.name) form.name = prefilled.name
})

function goBack() {
  if (history.length > 1) router.back()
  else router.push('/')
}
function checkEmail() { alert(`(예시) ${form.email} 사용 가능 여부 확인`) }

// 사진 관련 상태 및 로직
const photoPreviewUrl = ref<string | null>(null)

function handlePhotoUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    form.childPhoto = file
    photoPreviewUrl.value = URL.createObjectURL(file)
  }
}

// 목소리 녹음 관련 상태 및 로직
const isRecording = ref(false)
const recordedAudioUrl = ref<string | null>(null)
let mediaRecorder: MediaRecorder | null = null
let audioChunks: Blob[] = []

async function startRecording() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder = new MediaRecorder(stream)
    mediaRecorder.ondataavailable = (event) => {
      audioChunks.push(event.data)
    }
    mediaRecorder.onstop = () => {
      const audioBlob = new Blob(audioChunks, { type: 'audio/wav' })
      form.childVoice = audioBlob
      recordedAudioUrl.value = URL.createObjectURL(audioBlob)
      audioChunks = []
    }
    mediaRecorder.start()
    isRecording.value = true
  } catch (err) {
    console.error("마이크 접근 오류:", err)
    alert("마이크를 사용할 수 없습니다. 브라우저의 마이크 접근 권한을 확인해주세요.")
  }
}

function stopRecording() {
  if (mediaRecorder) {
    mediaRecorder.stop()
    isRecording.value = false
  }
}

function resetRecording() {
  recordedAudioUrl.value = null
  form.childVoice = null
}

async function sendCode() {
  if (!phoneValid.value) return
  smsInfo.value = '인증번호를 전송했습니다. 3분 이내에 입력해 주세요.'
  alert('(예시) 인증번호를 전송했습니다.')
  try {
    const response = await axios.post('/api/send-verification-code', {
      phone: form.phone
    });
    if (response.data.success) {
      smsInfo.value = '인증번호를 카카오톡으로 전송했습니다. 3분 이내에 입력해 주세요.';
      alert('인증번호를 전송했습니다.');
    } else {
      smsInfo.value = '';
      alert(response.data.message || '인증번호 전송에 실패했습니다. 잠시 후 다시 시도해주세요.');
    }
  } catch (error) {
    console.error('인증번호 전송 API 호출 오류:', error);
    smsInfo.value = '';
    alert('인증번호 전송 중 오류가 발생했습니다.');
  }
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

interface DaumPostcodeData { roadAddress: string; jibunAddress: string; userSelectedType: 'R' | 'J'; }
interface DaumPostcode { open(): void; }
interface Daum { Postcode: new (options: { oncomplete: (data: DaumPostcodeData) => void; }) => DaumPostcode; }
declare global { interface Window { daum?: Daum; } }

function execDaumPostcode() {
  new window.daum!.Postcode({
    oncomplete: (data: DaumPostcodeData) => {
      form.address = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress
    }
  }).open()
}
function handleAddressSearch() {
  if (window.daum && window.daum.Postcode) execDaumPostcode()
  else {
    const script = document.createElement('script')
    script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
    script.onload = () => execDaumPostcode()
    document.head.appendChild(script)
  }
}

function submit() {
  if (!form.email) {
    alert('아이디(이메일)를 입력해주세요.');
    return;
  }
  if (!passwordsOk.value) {
    alert('비밀번호를 확인해주세요. (입력 및 일치 여부)');
    return;
  }
  if (!form.name) {
    alert('이름을 입력해주세요.');
    return;
  }
  if (!form.birthday) {
    alert('생년월일을 입력해주세요.');
    return;
  }
  if (!form.gender) {
    alert('성별을 선택해주세요.');
    return;
  }
  if (!phoneValid.value) {
    alert('올바른 휴대폰 번호를 입력해주세요.');
    return;
  }
  if (!form.phoneVerified) {
    alert('휴대폰 인증을 완료해주세요.');
    return;
  }
  if (!requiredAgreed.value) {
    alert('필수 약관에 동의해주세요.');
    return;
  }

  const payload = { ...form, agreements: { ...agreements } }
  console.log('submit payload', payload)
  alert(' 부모정보 입력 화면으로 이동합니다.')
  router.push({ name: 'Singupdetail_parent' })
}

function removePhoto() {
  form.childPhoto = null
  photoPreviewUrl.value = null
}

function removeRecording() {
  form.childVoice = null
  recordedAudioUrl.value = null
}

</script>

<style scoped>
*,
*::before,
*::after {
  box-sizing: border-box;
}

:root {
  --brand: #A993E8;
  --text: #222;
  --muted: #777;
  --line: #eaeaf0;
}

/* 🔥 부모 레이아웃 영향 차단 + 화면 전체 점유 */
.viewport {
  position: fixed;
  inset: 0;
  /* top/right/bottom/left = 0 */
  overflow: auto;
  /* 내용 길면 스크롤 */
  background: #f9f9fb;
}

/* 가운데 정렬 + 상하 패딩(안전영역 포함) */
.centerer {
  min-height: 100%;
  display: flex;
  justify-content: center;
  /* 가로 중앙 */
  align-items: center;
  /* 세로 중앙(공간 남을 때) */
  padding-block: max(24px, env(safe-area-inset-top)) max(24px, env(safe-area-inset-bottom));
  padding-inline: 16px;
}

/* 창 높이가 낮거나 내용이 긴 경우, 위에서 시작하도록 */
@media (max-height: 720px) {
  .centerer {
    align-items: flex-start;
  }
}

/* 카드 폭/스타일 */
.app-container {
  width: min(430px, 100%);
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, .08);
  overflow: hidden;
}

/* 내부 레이아웃은 높이 고정하지 않도록 */
.page {
  display: flex;
  flex-direction: column;
  height: auto;
  background: #fafafa;
}


.page {
  display: flex;
  flex-direction: column;
  height: auto;
  background: #fafafa;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  height: 52px;
  display: grid;
  grid-template-columns: 52px 1fr 52px;
  align-items: center;
  padding: 0 4px;
  background: #fff;
  border-bottom: 1px solid var(--line);
  flex-shrink: 0;
}

.title {
  text-align: center;
  font-size: 16px;
  font-weight: 700;
}

.icon-btn {
  background: none;
  border: 0;
  padding: 8px;
  font-size: 18px;
  cursor: pointer;
}

.icon-btn.ghost {
  color: #52525b;
}

.spacer {
  width: 48px;
}

.scroll-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 100%;
  padding: 0 12px;
  margin: 0;
}

.card {
  width: 100%;
  max-width: none;
  border-radius: 0;
  border-left: none;
  border-right: none;
  padding: 14px 16px;
  box-shadow: none;
}

.row {
  display: block;
  margin: 10px 0;
}

fieldset.row {
  border: none;
  padding: 0;
}

.label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 6px;
  color: var(--text);
}

.field {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f8f8fb;
  border: 1px solid #ececf2;
  border-radius: 12px;
  padding: 0 12px;
  height: 48px;
}

.field input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 15px;
  width: 100%;
}

.field input[readonly] {
  color: var(--muted);
  cursor: pointer;
}

.field input[type="date"] {
  position: relative;
}

.field input[type="date"]::-webkit-calendar-picker-indicator {
  position: absolute;
  right: 0;
  top: 0;
  width: 100%;
  height: 100%;
  padding: 0;
  color: transparent;
  background: transparent;
  cursor: pointer;
}

.micro-btn {
  border: 1px solid #c9b7ff;
  background: #fff;
  border-radius: 14px;
  padding: 7px 12px;
  font-size: 12px;
  color: var(--brand);
  cursor: pointer;
  flex-shrink: 0;
}

.micro-btn:disabled {
  opacity: .5;
  cursor: not-allowed;
}

.micro-btn.danger {
  border-color: #e04545;
  color: #e04545;
}

.photo-preview-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}


.hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--muted);
}

.error {
  margin-top: 6px;
  font-size: 12px;
  color: #e04545;
}

.gender-selector {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  background: #f8f8fb;
  border-radius: 12px;
  padding: 4px;
  border: 1px solid #ececf2;
  overflow: hidden;
}

.gender-option {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  text-align: center;
  font-weight: 600;
  color: var(--muted);
  transition: background-color 0.3s ease, color 0.3s ease, box-shadow 0.3s ease, transform 0.2s ease;
}

.gender-option.active {
  background-color: #C5B8FF;
  color: #fff;
  box-shadow: 0 2px 4px rgba(100, 80, 150, 0.2);
  transform: scale(1.05);
}

.photo-field {
  cursor: pointer;
  justify-content: space-between;
}

.photo-field .placeholder {
  flex: 1;
  color: #777;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 10px;
}

.photo-field .micro-btn {
  flex-shrink: 0;
}

.photo-preview {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid var(--line);
  margin-top: 10px;
}

.voice-field .placeholder {
  flex: 1;
  color: #777;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 10px;
}

.voice-field .micro-btn.stop {
  border-color: #e04545;
  color: #e04545;
}

.audio-player-wrapper {
  margin-top: 10px;
}

.audio-player-wrapper audio {
  width: 100%;
  height: 40px;
}

.audio-player audio {
  flex-grow: 1;
  height: 38px;
}

.agreements-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 10px;
}

.agreements-body {
  padding-top: 5px;
}

.divider {
  border: none;
  border-top: 1px solid var(--line);
  margin: 10px 0;
}

.check {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 12px 0;
  font-size: 14px;
  cursor: pointer;
}

.check.big {
  padding: 4px 0;
  font-size: 15px;
  font-weight: 700;
}

.link {
  margin-left: auto;
  background: none;
  border: none;
  color: var(--brand);
  text-decoration: underline;
  cursor: pointer;
}

.cta {
  display: block;
  width: 100%;
  max-width: 560px;
  height: 50px;
  margin: 0 auto;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 15px;
  color: #fff !important;
  background-color: #9378d5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
