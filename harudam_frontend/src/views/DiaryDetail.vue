<template>
  <div class="detail">
    <button class="close" @click="onClose">✕</button>

    <img v-if="meta.imageUrl" :src="meta.imageUrl" alt="" class="hero" />
    <h1 class="title">{{ meta.title ?? '제목 없음' }}</h1>

    <button class="listen interactive" @click="toggleSpeech">
      <span v-if="isSpeaking">🔇 내용 끄기</span>
      <span v-else>🔊 내용 듣기</span>
    </button>

    <p v-if="meta.desc" class="desc">{{ meta.desc }}</p>

    <div class="stack">
      <button class="btn interactive" @click="onClose">돌아가기</button>
      <router-link class="btn interactive" :to="{ name: 'MainParent' }">처음화면 가기</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps<{ date: string }>()
const route = useRoute()
const router = useRouter()

const isSpeaking = ref(false)

const jsonFiles = import.meta.glob('/src/assets/diary/*.json', { eager: true, import: 'default' }) as Record<string, { title: string, desc?: string }>
const imageFiles = import.meta.glob('/src/assets/diary/*.{jpg,jpeg,png,gif}', { eager: true, import: 'default', query: '?url' })

const meta = computed(() => {
  const base = `/src/assets/diary/${props.date}`
  const j = jsonFiles[`${base}.json`]
  const img = imageFiles[`${base}.jpg`] || imageFiles[`${base}.jpeg`] || imageFiles[`${base}.png`] || imageFiles[`${base}.gif`]
  return { title: j?.title, desc: j?.desc, imageUrl: img as string | undefined }
})

function onClose() {
  if (route.matched.some(r => r.meta.modal)) router.push({ name: 'diary_p' })
  else router.back()
}

function toggleSpeech() {
  if (isSpeaking.value) {
    window.speechSynthesis.cancel()
    isSpeaking.value = false
  } else {
    const text = meta.value.desc || meta.value.title || props.date
    if (!('speechSynthesis' in window)) {
      alert('이 기기에서 음성합성이 지원되지 않아요.')
      return
    }
    window.speechSynthesis.cancel()
    const u = new SpeechSynthesisUtterance(text)
    u.lang = 'ko-KR'
    u.onend = () => { isSpeaking.value = false }
    window.speechSynthesis.speak(u)
    isSpeaking.value = true
  }
}
</script>

<style scoped>
/* ===== 레이아웃 기본 ===== */
.detail{
  min-height: 100dvh;
  min-height: 100svh;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 12px;

  background: #fff;
  color: #2f2640;
  box-sizing: border-box;

  /* 닫기 버튼이 겹치지 않도록 상단 여백 확보 */
  padding: calc(max(16px, env(safe-area-inset-top)) + 56px) 16px max(24px, env(safe-area-inset-bottom)) 16px;
  position: relative;
}

/* 닫기 버튼 */
.close{
  position: absolute;
  right: 12px;
  top: 12px;
  width: 36px;
  height: 36px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #ffffff;
  font-size: 18px;
  line-height: 1;
  color: #4a3c78;
  box-shadow: 0 1px 4px rgba(0,0,0,.06);
}
.close:focus-visible{ outline:2px solid #7c4dff; outline-offset:2px; }

/* 메인 이미지: 비율 유지 + 과도한 확대 방지 */
.hero{
  width: 100%;
  max-height: min(52dvh, 380px);
  object-fit: contain;            /* 잘림 없이 화면에 맞게 */
  background: #f7f2ff;
  border-radius: 12px;
  border: 1px solid #efe7ff;
}

/* 제목 */
.title{
  text-align: center;
  font-size: clamp(22px, 5.4vw, 28px);
  font-weight: 800;
  margin: 8px 0 4px;
  color: #4a3c78;
  word-break: keep-all;
}

/* 듣기 버튼 */
.listen{
  width: 100%;
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #e9e3ff;
  font-size: 18px;
  font-weight: 800;
  background: #efe7ff;            /* 보라톤 서피스 */
  color: #3a2d62;
  box-shadow: 0 2px 8px rgba(124,77,255,.08);
}
.listen:focus-visible{ outline:2px solid #7c4dff; outline-offset:2px; }

/* 본문 */
.desc{
  white-space: pre-wrap;
  margin-top: 6px;
  margin-bottom: 12px;
  font-size: 16px;
  line-height: 1.6;
  color: #544a66;
}

/* 하단 버튼 묶음: 일반 흐름 (겹침 방지) */
.stack{
  margin-top: 8px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

/* 공통 버튼 */
.btn, .interactive{
  transition: transform .08s ease, box-shadow .08s ease, background-color .2s ease, color .2s ease, border-color .2s ease;
}

.btn{
  width: 100%;
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #e9e3ff;
  font-size: 18px;
  font-weight: 800;
  text-align: center;
  text-decoration: none;
  box-sizing: border-box;

  background: #7c4dff;    /* 보라 주요 버튼 */
  color: #fff;
  box-shadow: 0 4px 12px rgba(124,77,255,.18);
}
.btn:focus-visible{ outline:2px solid #7c4dff; outline-offset:2px; }
.btn:hover{ transform:translateY(-1px); box-shadow:0 8px 18px rgba(124,77,255,.22); }
.btn:active{ transform:translateY(0); }

.interactive:active{
  transform: scale(0.97);
  box-shadow: 0 4px 10px rgba(0,0,0,0.12);
}

/* 모션 최소화 선호 대응 */
@media (prefers-reduced-motion: reduce){
  *,*::before,*::after{
    animation-duration: .001ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: .001ms !important;
    scroll-behavior: auto !important;
  }
}
</style>
