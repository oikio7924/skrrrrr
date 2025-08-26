<template>
    <div class="detail">
        <button class="close" @click="onClose">✕</button>

        <img v-if="meta.imageUrl" :src="meta.imageUrl" alt="" class="hero" />
        <h1 class="title">{{ meta.title ?? '제목 없음' }}</h1>

        <button class="listen" @click="speakDesc">🔊 내용 듣기</button>
        <p v-if="meta.desc" class="desc">{{ meta.desc }}</p>

        <div class="stack">
            <button class="btn" @click="onClose">돌아가기</button>
            <router-link class="btn" :to="{ name: 'main' }">처음화면 보기</router-link>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps<{ date: string }>()
const route = useRoute()
const router = useRouter()

// assets 로딩
const jsonFiles = import.meta.glob('/src/assets/diary/*.json', { eager: true, import: 'default' }) as Record<string, { title: string, desc?: string }>
const imageFiles = import.meta.glob('/src/assets/diary/*.{jpg,jpeg,png,gif}', { eager: true, import: 'default', query: '?url' })


const meta = computed(() => {
    const base = `/src/assets/diary/${props.date}`
    const j = jsonFiles[`${base}.json`]
    const img = imageFiles[`${base}.jpg`] || imageFiles[`${base}.jpeg`] || imageFiles[`${base}.png`] || imageFiles[`${base}.gif`]
    return { title: j?.title, desc: j?.desc, imageUrl: img as string | undefined }
})

function onClose() {
    if (route.matched.some(r => r.meta.modal)) router.push({ name: 'calendar' }) // 모달이면 캘린더로
    else router.back() // 단독 페이지면 뒤로가기
}

// TTS로 desc 읽기 (없으면 title 읽기)
function speakDesc() {
    const text = meta.value.desc || meta.value.title || props.date
    if (!('speechSynthesis' in window)) { alert('이 기기에서 음성합성이 지원되지 않아요.'); return }
    window.speechSynthesis.cancel()
    const u = new SpeechSynthesisUtterance(text)
    u.lang = 'ko-KR'
    window.speechSynthesis.speak(u)
}
</script>

<style scoped>
.detail {
    position: relative;
}

.close {
    position: absolute;
    right: 10px;
    top: 10px;
    width: 36px;
    height: 36px;
    border: 0;
    border-radius: 8px;
    background: #eee;
    font-size: 18px;
}

.hero {
    width: 100%;
    border-radius: 8px;
    object-fit: cover;
}

.title {
    text-align: center;
    font-size: 26px;
    margin: 12px 0;
}

.listen {
    width: 100%;
    padding: 14px;
    background: #c7f9ff;
    border-radius: 10px;
    border: 0;
    font-size: 18px;
}

.desc {
    white-space: pre-wrap;
    margin-top: 10px;
    font-size: 16px;
    line-height: 1.5;
}

.stack {
    margin-top: 14px;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.btn {
    width: 100%;
    padding: 14px;
    background: #ddd;
    border-radius: 10px;
    border: 0;
    font-size: 18px;
    text-align: center;
    text-decoration: none;
    color: #000;
}
</style>
