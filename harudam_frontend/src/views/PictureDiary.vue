<template>
    <div class="page">
        <header class="top">
            <button class="nav" @click="moveWeek(-1)">이전주</button>
            <div class="month">{{ headerMonth }}월</div>
            <button class="nav" @click="moveWeek(1)">다음주</button>
        </header>

        <div class="grid head">
            <div class="sun">일</div>
            <div>월</div>
            <div>화</div>
            <div>수</div>
            <div>목</div>
            <div>금</div>
            <div class="sat">토</div>
        </div>

        <div class="grid days">
            <button v-for="d in weekDates" :key="d.key" class="day"
                :class="{ sun: d.day === 0, sat: d.day === 6, selected: selectedKey === d.key }" @click="select(d.key)">
                {{ d.date }}
            </button>
        </div>

        <section class="center" v-if="selectedKey">
            <div class="imgWrap" @click="openModal(selectedKey)">
                <img v-if="meta?.imageUrl" :src="meta.imageUrl" alt="" />
                <div v-else class="imgPlaceholder">그림</div>
            </div>

            <button class="titleBox" @click="openModal(selectedKey)">
                {{ meta?.title ?? '제목 없음' }}
            </button>
        </section>

        <router-link class="homeBtn" to="/main">처음화면 보기</router-link>

        <router-view v-slot="{ Component, route }">
            <transition name="fade">
                <div v-if="route.meta.modal" class="modal" @click.self="closeModal">
                    <component :is="Component" class="card" @close="closeModal" />
                </div>
            </transition>
        </router-view>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

// 오늘 주(일요일 시작)
const today = new Date()
const startOfWeek = ref(getStartOfWeek(today))
const selectedKey = ref<string | null>(toKey(today))

function getStartOfWeek(d: Date) {
    const c = new Date(d); c.setHours(0, 0, 0, 0); c.setDate(c.getDate() - c.getDay()); return c
}
function toKey(d: Date) { return new Date(d).toISOString().slice(0, 10) }

const weekDates = computed(() =>
    Array.from({ length: 7 }).map((_, i) => {
        const d = new Date(startOfWeek.value); d.setDate(d.getDate() + i)
        return { key: toKey(d), date: d.getDate(), day: d.getDay() }
    })
)
const headerMonth = computed(() => new Date(weekDates.value[0].key).getMonth() + 1)

function moveWeek(n: number) {
    const s = new Date(startOfWeek.value); s.setDate(s.getDate() + 7 * n)
    startOfWeek.value = s
    selectedKey.value = weekDates.value[0].key // 주 이동 시 첫날 선택
}
function select(key: string) { selectedKey.value = key }
function openModal(key: string) { router.push({ name: 'dayModal', params: { date: key } }) }
function closeModal() { router.push({ name: 'calendar' }) }

// --- assets/diary 에서 JSON/이미지 읽기 ---
const jsonFiles = import.meta.glob('/src/assets/diary/*.json', { eager: true, import: 'default' }) as Record<string, { title: string, desc?: string }>
const imageFiles = import.meta.glob('/src/assets/diary/*.{jpg,jpeg,png,gif}', { eager: true, import: 'default', query: '?url' })

function getMeta(key: string) {
    const base = `/src/assets/diary/${key}`
    const json = jsonFiles[`${base}.json`]
    const imageUrl = imageFiles[`${base}.jpg`] || imageFiles[`${base}.jpeg`] || imageFiles[`${base}.png`] || imageFiles[`${base}.gif`]
    return { title: json?.title, desc: json?.desc, imageUrl: imageUrl as string | undefined }
}
const meta = computed(() => selectedKey.value ? getMeta(selectedKey.value) : null)
</script>

<style scoped>
html {
    /* 뷰포트 너비에 따라 기본 폰트 크기 조절 */
    font-size: calc(10px + 0.5vw);
}

.page {
    width: 100vw;
    max-width: 520px;
    margin: 0 auto;
    padding: 1rem;
    box-sizing: border-box; /* 패딩이 전체 너비에 포함되도록 */
}

.top {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.month {
    font-weight: 700;
    font-size: 1.8rem;
}

.nav {
    padding: 0.6rem 1rem;
    background: #e5e5e5;
    border-radius: 0.6rem;
    font-size: 1.4rem;
}

.grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 0.6rem;
}

.head {
    margin-top: 0.8rem;
    text-align: center;
    font-weight: 600;
}

.days {
    margin-top: 0.4rem;
}

.day {
    padding: 1.4rem 0;
    border-radius: 0.8rem;
    background: #f2f2f2;
    font-size: 1.8rem;
    aspect-ratio: 1; /* 가로세로 비율 1:1 유지 */
}

.selected {
    background: #9b5de5;
    color: #fff;
}

.sun {
    color: #e11d48;
}

.sat {
    color: #2563eb;
}

.center {
    margin-top: 1rem;
    padding: 1rem;
    background: #e5e7eb;
    border-radius: 1rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.imgWrap {
    background: #cffafe;
    padding: 0.6rem;
    border-radius: 0.8rem;
    cursor: pointer;
}

.imgWrap img {
    width: 100%;
    border-radius: 0.6rem;
    object-fit: cover;
}

.imgPlaceholder {
    width: 100%;
    height: auto;
    aspect-ratio: 16/9; /* 이미지 플레이스홀더 비율 유지 */
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2.8rem;
    background: #cffafe;
    border-radius: 0.6rem;
}

.titleBox {
    width: 100%;
    padding: 1.4rem;
    background: #cffafe;
    border-radius: 0.8rem;
    border: 0;
    font-size: 2.2rem;
    cursor: pointer;
}

.homeBtn {
    display: block;
    margin-top: 1rem;
    text-align: center;
    padding: 1.2rem;
    background: #ddd;
    border-radius: 1rem;
    text-decoration: none;
    color: #000;
    font-size: 1.6rem;
}

.modal {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, .45);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 50;
}

.card {
    width: min(520px, 92vw);
    max-height: 90vh;
    overflow: auto;
    background: #fff;
    border-radius: 1.2rem;
    padding: 1.2rem;
    box-shadow: 0 1rem 3rem rgba(0, 0, 0, .25);
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity .15s
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0
}
</style>