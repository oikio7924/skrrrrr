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
            <router-link class="btn interactive" :to="{ name: 'mainP' }">처음화면 보기</router-link>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps<{ date: string }>()
const route = useRoute()
const router = useRouter()

// ✅ 수정된 부분: 음성 재생 상태를 추적하는 변수 추가
const isSpeaking = ref(false)

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

// ✅ 수정된 부분: 음성 재생/중지 로직을 담은 함수
function toggleSpeech() {
    if (isSpeaking.value) {
        // 음성 재생 중이면 중지
        window.speechSynthesis.cancel()
        isSpeaking.value = false
    } else {
        // 음성 재생 중이 아니면 시작
        const text = meta.value.desc || meta.value.title || props.date
        if (!('speechSynthesis' in window)) {
            alert('이 기기에서 음성합성이 지원되지 않아요.');
            return;
        }
        window.speechSynthesis.cancel() // 기존 음성 중지
        const u = new SpeechSynthesisUtterance(text)
        u.lang = 'ko-KR'

        // 음성이 끝나면 상태를 false로 변경
        u.onend = () => {
            isSpeaking.value = false;
        };

        window.speechSynthesis.speak(u)
        isSpeaking.value = true // 상태를 재생 중으로 변경
    }
}
</script>

<style scoped>
.detail {
    position: relative;
    background-color: #fff;
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

/* 클릭 효과를 위한 스타일 */
.interactive {
    transition: transform 0.1s ease-in-out, box-shadow 0.1s ease-in-out;
}

.interactive:active {
    transform: scale(0.95);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
</style>