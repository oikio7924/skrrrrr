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
            <div v-if="meta?.imageUrl">
                <div class="imgWrap" @click="openModal(selectedKey)">
                    <img :src="meta.imageUrl" alt="" />
                </div>
                <button class="titleBox" @click="openModal(selectedKey)">
                    {{ meta.title }}
                </button>
            </div>
            <div v-else class="no-diary-container">
                <img :src="dayOfWeekDrawImage" alt="그림일기 채우기" class="no-diary-image" />
                <p class="no-diary-text">
                    <strong class="no-diary-em">그림일기</strong>를 채워주세요
                </p>
                <button class="no-diary-btn" @click="startChat(selectedKey)">시작하기</button>
            </div>
        </section>

        <router-link class="homeBtn" to="/mainP">처음화면 보기</router-link>

        <router-view v-slot="{ Component, route }">
            <Teleport to="body">
                <transition name="fade">
                    <div v-if="route.meta.modal" class="modal-overlay" @click.self="closeModal">
                        <component :is="Component" class="modal-card" @close="closeModal" />
                    </div>
                </transition>
            </Teleport>
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
    selectedKey.value = weekDates.value[0].key
}
function select(key: string) { selectedKey.value = key }

function openModal(key: string) {
    router.push({ name: 'dayModal', params: { date: key } })
}

function startChat(key: string) {
    router.push({ name: 'ChatBot', params: { date: key } });
}

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

const dayOfWeekDrawImage = computed(() => {
    if (!selectedKey.value) return '';
    const date = new Date(selectedKey.value);
    const dayOfWeek = date.getDay();
    return `/src/assets/diary/draw${dayOfWeek + 1}.jpg`;
});
</script>

<style scoped>
html {
    font-size: calc(10px + 0.5vw);
}

.page {
    width: 100%;
    max-width: 520px;
    margin: 0 auto;
    padding: 1rem;
    box-sizing: border-box;
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
    grid-template-columns: repeat(7, minmax(0, 1fr));
    gap: 0.1rem;
}

.head {
    margin-top: 0.8rem;
    text-align: center;
    font-weight: 600;
}

.head>div {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 1.4rem;
}

.days {
    margin-top: 0.4rem;
}

.day {
    padding: 0.5rem 0;
    border-radius: 0.8rem;
    background: #f2f2f2;
    font-size: 1.6rem;
    aspect-ratio: 1;
    display: flex;
    justify-content: center;
    align-items: center;
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

.no-diary-container {
    margin-top: 2rem;
    padding: 3rem 2.5rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    background: #f0f0f5;
    border-radius: 1rem;
    text-align: center;
    gap: 1.2rem;
}

.no-diary-image {
    max-width: 280px;
    width: 80%;
    height: auto;
    opacity: 0.8;
    cursor: pointer;
}

.no-diary-text {
    font-size: 1.8rem;
    color: #555;
    font-weight: 600;
    margin: 0;
    cursor: pointer;
}

.no-diary-em {
    font-weight: 900;
    color: #6e4e99;
}

.no-diary-btn {
    width: 80%;
    padding: 1.2rem;
    border: 1px solid #fff;
    border-radius: 9999px;
    font-size: 1.6rem;
    font-weight: 700;
    color: #fff;
    background:  #9ec7ff ;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: transform 0.1s ease-in-out;
}

.no-diary-btn:active {
    transform: scale(0.95);
}

.center {
    margin-top: 2rem;
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
    aspect-ratio: 4/5;
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
    margin-top: 2rem;
    text-align: center;
    padding: 1.2rem;
    border-radius: 9999px;
    text-decoration: none;
    font-size: 1.6rem;
    font-weight: 700;
    color: #fff;
    background: #c7a4ff 100%;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-overlay {
    position: fixed;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, .45);
    z-index: 10000;
}

.modal-card {
    width: min(520px, 92vw);
    max-height: 90svh;
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