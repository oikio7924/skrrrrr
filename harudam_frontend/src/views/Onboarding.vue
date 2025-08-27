<template>
    <!-- 마지막 장이면 is-last 클래스를 붙여 트랙 높이를 꽉 채움 -->
    <main class="ob-screen" :class="{ 'is-last': isLast }">
        <header class="top">
            <button v-if="!isLast" class="skip" @click="goLast">건너뛰기</button>
        </header>

        <!-- 가로 트랙: step으로 장 이동 -->
        <div class="track" :style="{ transform: `translateX(-${step * 100}%)` }" @pointerdown.passive="onDown"
            @pointermove.passive="onMove" @pointerup.passive="onUp" @pointercancel.passive="onUp"
            @pointerleave.passive="onUp">
            <!-- 1. 마이크 -->
            <section class="slide">
                <div class="copy">
                    <h2>음성으로 대화 !</h2>
                    <p>버튼만 누르면 바로 말할 수 있어요.</p>
                </div>
                <img :src="imgs.mic" alt="음성 대화" class="hero" />
            </section>

            <!-- 2. 요약/기록 -->
            <section class="slide">
                <div class="copy">
                    <h2>대화 자동 요약/기록 !</h2>
                    <p>긴 대화도 핵심 쏙 정리해드려요.</p>
                </div>
                <img :src="imgs.memo" alt="요약/기록" class="hero" />
            </section>

            <!-- 3. 알림 -->
            <section class="slide">
                <div class="copy">
                    <h2>가족 소통의<br />실시간 연결 고리 !</h2>
                    <p>실시간 알림으로 가족과 더 가까이.</p>
                </div>
                <img :src="imgs.bell" alt="알림" class="hero" />
            </section>

            <!-- 4. 마지막: 로고 중앙 / 문구 바로 아래 / 버튼 하단 -->
            <section class="slide slide--final">
                <div class="final-center">
                    <div class="brand">
                        <img :src="imgs.book" alt="하루담" class="logo" />
                        <strong class="brand-name">하루담</strong>
                    </div>
                    <p class="final-copy">대화는 기록되고<br />마음은 전해집니다.</p>
                </div>

                <button class="cta" @click="finish">시작하기</button>
            </section>
        </div>

        <!-- 인디케이터/컨트롤은 마지막 장에서 렌더하지 않음 -->
        <nav class="dots" v-if="!isLast" aria-label="onboarding steps">
            <button v-for="i in COUNT" :key="i" class="dot" :class="{ active: i - 1 === step }" @click="go(i - 1)"
                :aria-label="`step ${i}`" />
        </nav>

        <footer class="controls" v-if="!isLast">
            <button class="ghost" :disabled="step === 0" @click="prev">이전</button>
            <button class="primary" @click="next">다음</button>
        </footer>
    </main>
</template>

<script setup lang="ts">
defineOptions({ name: 'OnboardingView' })   // 👈 ESLint 경고 해결(두 단어 이름)
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

// 이미지 경로(프로젝트에 맞게 넣어두면 됨)
import micPng from '@/assets/onboarding/1.png'
import memoPng from '@/assets/onboarding/2.png'
import bellPng from '@/assets/onboarding/3.png'
import bookPng from '@/assets/onboarding/4.png'

const imgs = { mic: micPng, memo: memoPng, bell: bellPng, book: bookPng }

const COUNT = 4
const step = ref(0)
const isLast = computed(() => step.value === COUNT - 1)
const router = useRouter()

function go(i: number) { step.value = Math.min(Math.max(i, 0), COUNT - 1) }
function next() { go(step.value + 1) }
function prev() { go(step.value - 1) }

function goLast() {
    step.value = COUNT - 1
}

function finish() {
    localStorage.setItem('harudam_onboarded', '1')
    router.replace({ name: 'Start' })   // 필요 시 목적지 바꾸기
}

/* 스와이프 */
let dragging = false
let startX = 0, deltaX = 0
function onDown(e: PointerEvent) { dragging = true; startX = e.clientX; deltaX = 0 }
function onMove(e: PointerEvent) { if (dragging) deltaX = e.clientX - startX }
function onUp() {
    if (!dragging) return
    dragging = false
    const TH = 60
    if (deltaX < -TH) next()
    else if (deltaX > TH) prev()
    deltaX = 0
}

/* 온보딩 동안 스크롤바 숨김(PC에서 1px 밀림 방지) */
onMounted(() => {
    document.documentElement.style.overflowX = 'hidden'
    document.body.style.overflowX = 'hidden'
})
onBeforeUnmount(() => {
    document.documentElement.style.overflowX = ''
    document.body.style.overflowX = ''
})
</script>

<style scoped>
/* ───────── 컨테이너: 기본은 4행(헤더/트랙/점/컨트롤) ───────── */
.ob-screen {
    position: fixed;
    inset: 0;
    width: 100vw;
    height: 100dvh;
    /* 필요하면 100vh + JS 보정 사용 */
    background: #fff;
    overflow: hidden;
    display: grid;
    grid-template-rows: auto 1fr auto auto;
    overscroll-behavior: none;
    touch-action: pan-y;
}

/* 마지막 장이면 2행(헤더/트랙) → 트랙이 화면을 전부 가짐 */
.ob-screen.is-last {
    grid-template-rows: auto 1fr;
}

.top {
    height: 100px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 0 12px;
}

.skip {
    background: none;
    border: 0;
    color: #7a54cf;
    font-weight: 700;
    padding: 8px 10px;
    font-size: 18px;
    line-height: 1.1;
    white-space: nowrap;
}

/* ───────── 트랙/슬라이드 ───────── */
.track {
    width: 100%;
    height: 100%;
    display: grid;
    grid-auto-flow: column;
    grid-auto-columns: 100%;
    /* 한 장 = 컨테이너 100% */
    transition: transform .35s ease;
}
.track > .slide:nth-child(3) .hero {
  transform: translateX(20px);
}

.slide {
    width: 100%;
    height: 100%;
    display: grid;
    grid-template-rows: 1fr auto auto 1fr;
    /* 위 여백 / 텍스트 / 이미지 / 아래 여백 */
    justify-items: center;
    align-items: center;
    row-gap: 12px;
    padding: 0 16px;
}

.copy {
    grid-row: 2;
    text-align: center;
    margin: 0;
}

h2 {
    font-size: 28px;
    line-height: 1.25;
    margin: 0 0 8px;
    font-weight: 900;
    color: #111;
    text-align: center;
}

p {
    font-size: 15px;
    color: #555;
    margin: 0;
    text-align: center;
}

.hero {
    grid-row: 3;
    width: min(300px, 72%);
    max-width: 320px;
    display: block;
}

/* ───────── 마지막 슬라이드 전용: 중앙/하단 고정 ───────── */
.slide--final {
    position: relative;
    padding: 0 16px;
}

/* 중앙(로고 + 멘트)을 화면 정중앙에 */
.slide--final .final-center {
    position: absolute;
    inset: 0;
    text-align: center;
}

.slide--final .brand {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    /* 문자 중심을 정확히 중앙에 */
    font-size: 28px;
    font-weight: 800;
    color: #A993E8;
}

.slide--final .logo {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    bottom: calc(100% + 12px);
    width: 160px;
    height: auto;
}

.slide--final .brand-name {
    /* position: absolute; */
    color: #A993E8;
    top: 50%;
    left: 50%;
    font-size: 28px;
    font-weight: 800;
    transform: translate(-50%, -50%);
}

.slide--final .final-copy {
    position: absolute;
    left: 50%; transform: translateX(-50%);
    top: calc(50% + 30px);
    margin: 0;
    line-height: 1.6;
    font-size: 18px;
    font-weight: bold;
}

/* 하단 버튼(안전영역 포함 살짝 띄움) */
.slide--final .cta {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    bottom: calc(env(safe-area-inset-bottom, 0px) + 18px);
    width: min(320px, 88vw);
    padding: 14px 18px;
    border-radius: 16px;
    background: #B487FF;
    color: #fff;
    border: 0;
    font-weight: 900;
    font-size: 18px;
}

/* ───────── dots/controls ───────── */
.dots {
    display: flex;
    justify-content: center;
    gap: 8px;
    padding: 8px 0;
}

.dot {
    width: 8px;
    height: 8px;
    border-radius: 999px;
    border: 0;
    background: #d8cdf9;
}

.dot.active {
    width: 22px;
    background: #7a54cf;
    transition: width .2s;
}

.controls {
    display: flex;
    justify-content: center;
    gap: 12px;
    padding-bottom: 8px;
}

.primary,
.ghost {
    min-width: 120px;
    padding: 10px 16px;
    border-radius: 12px;
    font-weight: 800;
    cursor: pointer;
}

.primary {
    background: #A993E8;
    color: #fff;
    border: 0;
}

.ghost {
    background: transparent;
    border: 1px solid #d4c8ff;
    color: #7a54cf;
}

/* 데스크톱/다크 모드 보완 */
@media (prefers-color-scheme: dark) {
    .ob-screen {
        background: #0f0f14;
    }

    h2 {
        color: #f5f1ff;
    }

    p {
        color: #cbbff6;
    }

    .final-copy {
        color: #e9e2ff;
    }

    .ghost {
        border-color: #7158dd;
        color: #cbbff6;
    }
}
</style>
