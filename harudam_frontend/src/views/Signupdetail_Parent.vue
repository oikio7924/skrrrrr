<template>
  <div class="signup-overlay">
    <div class="signup-card" role="dialog" aria-labelledby="signupTitle">

      <div class="signup-header">
        <button class="back-btn" aria-label="뒤로가기" @click="$router.push({ name: 'Signupdetail_child' })">
          <svg viewBox="0 0 24 24" class="icon">
            <path d="M15.5 19.5 8 12l7.5-7.5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
          </svg>
        </button>
        <h1 id="signupTitle" class="title">회원가입</h1>
      </div>

      <form class="form" @submit.prevent="onSubmit">
        <label class="field">
          <span class="label">부모님 성함</span>
          <input v-model.trim="form.name" type="text" placeholder="이름을 입력하세요." required @blur="touched.name = true" />
          <p v-if="touched.name && !valid.name" class="hint invalid">
            2글자 이상 입력해주세요.
          </p>
        </label>

        <div class="field">
          <label class="label">부모님 휴대폰 번호</label>
          <div class="row">
            <input v-model="form.phone" type="tel" inputmode="numeric" maxlength="13" placeholder="예) 010-1234-5678"
              required @input="maskPhone" @blur="touched.phone = true" />
            <button type="button" class="ghost-btn" :disabled="!valid.phone || sending || countdown > 0"
              @click="openConsentModal">
              <template v-if="countdown > 0">{{ countdown }}초</template>
              <template v-else>인증번호 전송</template>
            </button>
          </div>
          <p v-if="touched.phone && !valid.phone" class="hint invalid">
            휴대폰 번호 형식을 확인해주세요.
          </p>
        </div>

        <div class="field">
          <label class="label">인증번호</label>
          <div class="row">
            <input v-model.trim="form.code" type="text" inputmode="numeric" maxlength="6" placeholder="인증번호 입력"
              :disabled="!codeSent || codeVerified" required @blur="touched.code = true" />
            <button type="button" class="ghost-btn" :disabled="!valid.code || verifyingCode || codeVerified"
              @click="verifySMS">
              <template v-if="codeVerified">인증 완료</template>
              <template v-else>인증 확인</template>
            </button>
          </div>
          <p v-if="touched.code && !valid.code" class="hint invalid">
            6자리 숫자를 입력해주세요.
          </p>
          <p v-if="verificationStatus.message" :class="['hint', verificationStatus.type]">
            {{ verificationStatus.message }}
          </p>
        </div>

        <label class="field">
          <span class="label">생년월일</span>
          <input v-model="form.birth" type="date" placeholder="생년월일 입력" required @blur="touched.birth = true" />
          <p v-if="touched.birth && !valid.birth" class="hint invalid">
            생년월일을 선택해주세요.
          </p>
        </label>

        <div class="field">
          <label class="label">부모님 사진</label>
          <div class="file-input-wrapper" @click="triggerFileInput">
            <span :class="['file-placeholder', { 'has-file': form.photo }]">
              {{ form.photo ? form.photo.name : 'ex: 정면을 응시한 사진' }}
            </span>
            <button type="button" class="ghost-btn">첨부하기</button>
            <input ref="fileInputRef" type="file" @change="onFileChange" accept="image/*" hidden />
          </div>
          <div v-if="photoPreviewUrl" class="photo-preview-box">
            <img :src="photoPreviewUrl" alt="부모님 사진 미리보기" class="photo-preview" />
            <button type="button" class="ghost-btn danger preview-action" @click="removePhoto">
              삭제
            </button>
          </div>
        </div>

        <fieldset class="field">
          <legend class="label">주소(선택)</legend>
          <input v-model.trim="form.addr1" type="text" placeholder="주소 검색" readonly @click="openAddressSearch" />
          <input v-model.trim="form.addr2" type="text" placeholder="상세 주소 입력(동, 호수)" class="mt8" />
        </fieldset>

        <fieldset class="field">
          <legend class="label">성별</legend>
          <div class="radio-row" role="radiogroup" aria-label="성별">
            <label class="radio">
              <input type="radio" value="F" v-model="form.gender" />
              <span>여성</span>
            </label>
            <label class="radio">
              <input type="radio" value="M" v-model="form.gender" />
              <span>남성</span>
            </label>
          </div>
          <p v-if="touched.gender && !valid.gender" class="hint invalid">
            성별을 선택해주세요.
          </p>
        </fieldset>

        <button type="submit" class="primary-btn submit-btn" :disabled="!formValid || submitting">
          가입하기
        </button>
      </form>
    </div>
  </div>
  <div v-if="showConsentModal" class="modal-overlay" @click.self="closeConsentModal">
    <div class="modal-card">
      <h3 class="modal-title">부모님 개인정보 이용 동의</h3>
      <div class="modal-content">
        <p class="guidance">
          💡 부모님께 전송된 문자 링크를 클릭해 개인정보 이용 동의를 완료하면 인증번호가 발급됩니다. 💡
        </p>
        <ul>
          <li><span class="list-title">수집 목적:</span> 자녀의 회원가입 및 본인 확인</li>
          <li><span class="list-title">수집 항목:</span> 휴대폰 번호</li>
          <li><span class="list-title">이용 및 보관 기간:</span> 회원 탈퇴 시까지</li>
        </ul>
        <p class="details">
          자세한 내용은
          <a href="#" @click.prevent="onDetailsClick">개인정보처리방침</a>에서 확인하실 수 있습니다.
        </p>
      </div>
      <div class="modal-actions">
        <button class="secondary-btn" @click="closeConsentModal">취소</button>
        <button class="primary-btn" @click="onConsentAgree">
          부모님 동의 후<br />인증 진행
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, ref, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router' // ✅ useRoute 추가
import { sendVerificationCode, verifyCode } from '@/api/verification'

const router = useRouter()
const route = useRoute()

const childIdForVerification = computed(() =>
  Number(route.params.childId || localStorage.getItem('childId') || 0)
)

const showConsentModal = ref(false)

/* 사진 관련 */
const fileInputRef = ref<HTMLInputElement | null>(null)
const photoPreviewUrl = ref<string | null>(null)

/* form 상태 */
const form = reactive({
  name: '',
  phone: '',
  code: '',
  birth: '',
  photo: null as File | null,
  addr1: '',
  addr2: '',
  gender: 'F' as 'F' | 'M' | '',
})

const touched = reactive({ name: false, phone: false, code: false, birth: false, gender: false })

const sending = ref(false)
const submitting = ref(false)
const codeSent = ref(false)
const countdown = ref(0)
let timer: number | null = null

const verifyingCode = ref(false)
const codeVerified = ref(false)
const verificationStatus = reactive<{ message: string; type: 'success' | 'invalid' | '' }>({
  message: '',
  type: '',
})


/* 유효성 체크 */
const phoneRegex = /^01[016789]-\d{3,4}-\d{4}$/
const valid = reactive({
  get name() { return form.name.length >= 2 },
  get phone() { return phoneRegex.test(form.phone) },
  get code() { return /^\d{6}$/.test(form.code) },
  get birth() { return !!form.birth },
  get gender() { return form.gender === 'F' || form.gender === 'M' },
})
// 제출 버튼 활성화는 인증 완료까지 요구
const formValid = computed(() =>
  valid.name && valid.phone && valid.birth && valid.gender && codeVerified.value
)

/* 동의 모달 */
function openConsentModal() {
  if (!valid.phone) { touched.phone = true; return }
  showConsentModal.value = true
}
function closeConsentModal() { showConsentModal.value = false }
function onConsentAgree() { closeConsentModal(); sendSMS() } // 👈 함수명 변경
function onDetailsClick() { alert('[안내] 개인정보처리방침 페이지로 이동합니다. (구현 필요)') }

// ✅ 카운트다운 함수 누락 보완
function startCountdown(sec: number) {
  countdown.value = sec
  if (timer) window.clearInterval(timer)
  timer = window.setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0 && timer) {
      window.clearInterval(timer)
      timer = null
    }
  }, 1000)
}

// ▼ 인증번호 전송
async function sendSMS() {
  if (!valid.phone || countdown.value > 0) return
  if (!childIdForVerification.value) {
    alert('자녀 ID가 없습니다. 이전 단계에서 자녀 생성/선택을 먼저 해주세요.')
    return
  }
  sending.value = true
  verificationStatus.message = '인증번호를 전송 중입니다...'
  verificationStatus.type = ''
  try {
    const digits = form.phone.replace(/\D/g, '')
    const res = await sendVerificationCode({
      childId: childIdForVerification.value, // <- .value 사용
      phone: digits,
    })
    if (res.success) {
      verificationStatus.message = res.message || '인증번호를 전송했습니다. 3분 이내에 입력해주세요.'
      verificationStatus.type = 'success'
      codeSent.value = true
      startCountdown(180)
    } else {
      verificationStatus.message = res.message || '전송 실패'
      verificationStatus.type = 'invalid'
    }
  } catch (error: any) {
    console.error('인증번호 전송 오류:', error)
    verificationStatus.message = error?.response?.data?.message || '인증번호 전송 중 오류가 발생했습니다.'
    verificationStatus.type = 'invalid'
  } finally {
    sending.value = false
  }
}

// ▼ 인증번호 확인
async function verifySMS() {
  if (!valid.code) { touched.code = true; return }
  verifyingCode.value = true
  verificationStatus.message = ''
  try {
    const digits = form.phone.replace(/\D/g, '')
    const res = await verifyCode({ phone: digits, code: form.code })
    if (res.success) {
      codeVerified.value = true
      verificationStatus.message = res.message || '인증되었습니다.'
      verificationStatus.type = 'success'
      if (timer) clearInterval(timer)
      countdown.value = 0
    } else {
      codeVerified.value = false
      verificationStatus.message = res.message || '인증번호가 올바르지 않습니다.'
      verificationStatus.type = 'invalid'
    }
  } catch (e: any) {
    codeVerified.value = false
    verificationStatus.message = e?.response?.data?.message || '인증 처리 중 오류가 발생했습니다.'
    verificationStatus.type = 'invalid'
  } finally {
    verifyingCode.value = false
  }
}


/* 주소 검색 */
interface DaumPostcodeData { userSelectedType: 'R' | 'J'; roadAddress: string; jibunAddress: string }
type DaumNS = { Postcode: new (opts: { oncomplete: (data: DaumPostcodeData) => void }) => { open: () => void } }
type WindowWithDaum = Window & { daum?: DaumNS }
const getDaum = (): DaumNS | undefined => (window as WindowWithDaum).daum

function execDaumPostcode() {
  const daum = getDaum(); if (!daum) return
  new daum.Postcode({
    oncomplete: (data) => {
      form.addr1 = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress
    },
  }).open()
}
function openAddressSearch() {
  const daum = getDaum()
  if (daum?.Postcode) { execDaumPostcode(); return }
  const script = document.createElement('script')
  script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js'
  script.onload = () => execDaumPostcode()
  document.head.appendChild(script)
}

/* 핸드폰 번호 마스킹 (+ 번호 바꾸면 인증상태 리셋 권장) */
function maskPhone(e: Event) {
  const input = (e.target as HTMLInputElement)
  let digits = input.value.replace(/\D/g, '')
  if (digits.length > 11) digits = digits.slice(0, 11)
  let masked = ''
  if (digits.length > 7) {
    const mid = digits.length === 11 ? 4 : 3
    masked = `${digits.slice(0, 3)}-${digits.slice(3, 3 + mid)}-${digits.slice(3 + mid)}`
  } else if (digits.length > 3) {
    masked = `${digits.slice(0, 3)}-${digits.slice(3)}`
  } else masked = digits
  form.phone = masked

  // 번호 변경 시 인증 초기화(권장)
  codeSent.value = false
  codeVerified.value = false
  verificationStatus.message = ''
}

/* 사진 업로드 */
function triggerFileInput() { fileInputRef.value?.click() }
function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (photoPreviewUrl.value) URL.revokeObjectURL(photoPreviewUrl.value)
  if (file) {
    form.photo = file
    photoPreviewUrl.value = URL.createObjectURL(file)
  } else {
    form.photo = null
    photoPreviewUrl.value = null
  }
}
function removePhoto() {
  if (photoPreviewUrl.value) { URL.revokeObjectURL(photoPreviewUrl.value) }
  photoPreviewUrl.value = null
  form.photo = null
  if (fileInputRef.value) fileInputRef.value.value = ''
}



// 최종 제출
async function onSubmit() {
  submitting.value = true
  try {
    // TODO: 실제 회원가입 API 호출
    await new Promise<void>(r => setTimeout(r, 600))
    alert('회원가입이 완료되었습니다.')
    router.push({ name: 'signupcomplete' })
  } finally {
    submitting.value = false
  }
}


/* 언마운트 시 정리 */
onUnmounted(() => {
  if (timer) window.clearInterval(timer)
  if (photoPreviewUrl.value) URL.revokeObjectURL(photoPreviewUrl.value)
})
</script>


<style scoped>
/* 스타일 코드는 제공해주신 그대로 유지됩니다. */
*,
*::before,
*::after {
  box-sizing: border-box;
}

.signup-header {
  height: 60px;
  margin-bottom: 16px;
}

.title {
  font-size: 20px;
  font-weight: 700;
}

.back-btn {
  position: absolute;
  left: 12px;
  border: none;
  background: none;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.back-btn .icon {
  width: 24px;
  height: 24px;
  stroke: currentColor;
}

:root {
  --brand: #A993E8;
  --text: #222;
  --muted: #777;
  --line: #eaeaf0;
}

.hint.success {
  color: #28a745;
}

.guidance {
  text-align: center;
  background-color: #f6f4ff;
  border: 1px solid #e0d9ff;
  padding: 14px;
  border-radius: 10px;
  margin-bottom: 16px !important;
  line-height: 1.5;
  font-size: 15px;
  font-weight: 600;
  color: #000000;
  word-break: keep-all;
}

.modal-actions button {
  height: auto;
  min-height: 48px;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s, transform 0.1s;
  line-height: 1.4;
}

.signup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  background: #f7f7fb;
  padding: 20px;
  box-sizing: border-box;
  overflow-y: auto;
}

.signup-card {
  width: clamp(320px, 92vw, 420px);
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
  padding: 28px 20px 32px;
  position: relative;
}

@media (min-width: 768px) {
  .signup-card {
    width: 420px;
  }
}

.title {
  text-align: center;
  font-size: 18px;
  font-weight: 700;
  margin: 6px 0 16px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field {
  display: flex;
  flex-direction: column;
  border: none;
  padding: 0;
  margin: 0;
}

.label {
  display: inline-block;
  font-size: 13px;
  color: #6b6b6b;
  margin-bottom: 6px;
}

input[type='text'],
input[type='tel'],
input[type='date'] {
  width: 100%;
  height: 44px;
  border: 1px solid #e5e6ec;
  border-radius: 12px;
  padding: 0 14px;
  font-size: 14px;
  outline: none;
  background: #fff;
  transition: border-color 0.2s, box-shadow 0.2s;
}

input:focus {
  border-color: #9b8cff;
  box-shadow: 0 0 0 3px rgba(155, 140, 255, 0.15);
}

.row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  align-items: center;
}

.radio-row {
  display: flex;
  gap: 18px;
}

.radio {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}

.radio input {
  width: 18px;
  height: 18px;
}

.hint {
  margin-top: 6px;
  font-size: 12px;
  color: #8e8e98;
}

.invalid {
  color: #e45858;
}

.mt8 {
  margin-top: 8px;
}


.ghost-btn {
  height: 40px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid #d9d9e6;
  background: #fff;
  color: #6b6b7a;
  font-size: 13px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.file-input-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 44px;
  border: 1px solid #e5e6ec;
  border-radius: 12px;
  padding: 0 4px 0 14px;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.file-input-wrapper:hover {
  border-color: #9b8cff;
}

.file-placeholder {
  color: #a0a0a0;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-right: 10px;
}

.file-placeholder.has-file {
  color: #333;
}

.photo-preview {
  display: block;
  width: 100%;
  height: auto;
  max-height: 220px;
  object-fit: cover;
  border: 1px solid #e5e6ec;
  border-radius: 12px;
}

.photo-preview-box {
  position: relative;
  margin-top: 12px;
  border-radius: 12px;
  overflow: hidden;
}

.preview-action {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: saturate(120%) blur(2px);
}


.submit-btn {
  margin-top: 8px;
  height: 48px;
  border: none;
  border-radius: 14px;
  background: #9b8cff;
  color: #fff;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  box-shadow: 0 6px 16px rgba(155, 140, 255, 0.35);
  transition: opacity 0.2s, box-shadow 0.2s;
}

.ghost-btn.danger {
  border-color: #e45858;
  color: #e45858;
}

.photo-preview-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.ghost-btn:disabled,
.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: grid;
  place-items: center;
  z-index: 1000;
}

.modal-card {
  width: 340px;
  background: #fff;
  border-radius: 16px;
  padding: 28px 24px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.modal-title {
  text-align: center;
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 20px;
}

.modal-content {
  font-size: 14px;
  color: #444;
  line-height: 1.6;
  flex-grow: 1;
}


.modal-content p {
  margin: 0 0 12px;
}

.modal-content ul {
  list-style: none;
  padding: 10px 15px;
  margin: 0 0 12px;
  background-color: #f7f7fb;
  border-radius: 8px;
}

.modal-content li {
  margin-bottom: 4px;
}

.modal-content li:last-child {
  margin-bottom: 0;
}

.list-title {
  font-weight: 500;
  color: #333;
  margin-right: 5px;
}

.modal-content .details {
  text-align: center;
  font-size: 13px;
  margin-top: 16px;
}

.modal-content a {
  color: #8573f2;
  font-weight: 600;
  text-decoration: underline;
}

.modal-actions {
  margin-top: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.modal-actions button:active {
  transform: scale(0.98);
}

.secondary-btn {
  background: #f0f0f5;
  color: #555;
}

.modal-actions .primary-btn {
  background: #9b8cff;
  color: #fff;
}
</style>
