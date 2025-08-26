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
      <button
        v-for="d in weekDates"
        :key="d.key"
        class="day"
        :class="{ sun: d.day === 0, sat: d.day === 6, selected: selectedKey === d.key }"
        @click="select(d.key)"
      >
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
          <strong class="highlight">그림일기</strong><br/>채워주세요
        </p>
        <button class="no-diary-btn" @click="startChat(selectedKey)">시작하기</button>
      </div>
    </section>

    <router-link v-if="!isModal" class="homeBtn" to="/Mainparent">
      처음화면 보기
    </router-link>

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
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 모달 여부 확인
const isModal = computed(() => route.matched.some(r => r.meta.modal))

const today = new Date()
const startOfWeek = ref(getStartOfWeek(today))
const selectedKey = ref<string | null>(toKey(today))

function getStartOfWeek(d: Date) {
  const c = new Date(d)
  c.setHours(0, 0, 0, 0)
  c.setDate(c.getDate() - c.getDay())
  return c
}
function toKey(d: Date) {
  return new Date(d).toISOString().slice(0, 10)
}

const weekDates = computed(() =>
  Array.from({ length: 7 }).map((_, i) => {
    const d = new Date(startOfWeek.value)
    d.setDate(d.getDate() + i)
    return { key: toKey(d), date: d.getDate(), day: d.getDay() }
  })
)
const headerMonth = computed(() => new Date(weekDates.value[0].key).getMonth() + 1)

function moveWeek(n: number) {
  const s = new Date(startOfWeek.value)
  s.setDate(s.getDate() + 7 * n)
  startOfWeek.value = s
  selectedKey.value = weekDates.value[0].key
}
function select(key: string) { selectedKey.value = key }
function openModal(key: string) { router.push({ name: 'dayModal', params: { date: key } }) }
function startChat(key: string) { router.push({ name: 'ChatBot', params: { date: key } }) }
function closeModal() { router.push({ name: 'calendar' }) }

const jsonFiles = import.meta.glob('/src/assets/diary/*.json', { eager: true, import: 'default' }) as Record<string, { title: string, desc?: string }>
const imageFiles = import.meta.glob('/src/assets/diary/*.{jpg,jpeg,png,gif}', { eager: true, import: 'default', query: '?url' })

function getMeta(key: string) {
  const base = `/src/assets/diary/${key}`
  const json = jsonFiles[`${base}.json`]
  const imageUrl =
    imageFiles[`${base}.jpg`] ||
    imageFiles[`${base}.jpeg`] ||
    imageFiles[`${base}.png`] ||
    imageFiles[`${base}.gif`]
  return { title: json?.title, desc: json?.desc, imageUrl: imageUrl as string | undefined }
}
const meta = computed(() => (selectedKey.value ? getMeta(selectedKey.value) : null))

const dayOfWeekDrawImage = computed(() => {
  if (!selectedKey.value) return ''
  const date = new Date(selectedKey.value)
  const dayOfWeek = date.getDay()
  return `/src/assets/diary/draw${dayOfWeek + 1}.jpg`
})
</script>

<style scoped>
/* 전체 흰 배경 유지 */
.page {
  min-height: 100dvh;
  min-height: 100svh;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: max(1rem, env(safe-area-inset-top)) 1rem max(88px, env(safe-area-inset-bottom)) 1rem;
  box-sizing: border-box;
}

/* ── 헤더 ── */
.top {
  display: flex; justify-content: space-between; align-items: center;
  background: rgba(255,255,255,.6);
  backdrop-filter: blur(6px);
  padding: .8rem 1rem;
  border-radius: 1rem;
}
.month { font-weight: 700; font-size: 1.8rem; color: blue; }

/* 버튼 */
.nav {
  padding: 0.6rem 1rem;
  border-radius: 0.8rem;
  font-size: 1.4rem;
  font-weight: 700;
  color: #3c2f46;
  background: #ffffff;
  border: 1px solid #e5e7eb;
}
.top .nav:first-of-type { background: #f8fafc; }
.top .nav:last-of-type  { background: #faf5ff; }

/* ── 요일/날짜 ── */
.grid {
  --col-gap: 6px;
  display: grid;
  grid-template-columns: repeat(7, minmax(0, 1fr));
  column-gap: var(--col-gap);
  row-gap: 0;
}
.head {
  margin-top: .8rem;
  text-align: center;
  font-weight: 800;
  color: #554568;
  font-size: 1.6rem;
  margin-bottom: .4rem;
}

.sun { color: #ef476f; }
.sat { color: #5a78ff; }

.days { margin-top: .2rem; }
.day {
  display: grid;
  place-items: center;
  aspect-ratio: 1 / 1;
  min-width: 44px;
  min-height: 44px;
  font-weight: 800;
  font-size: clamp(14px, 4.2vw, 18px);
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 1rem;
}
.selected {
  background: linear-gradient(135deg, #7c4dff, #b388ff);
  color: #fff;
  border-color: #a78bfa;
}

/* ── 콘텐츠 영역 ── */
.center {
  margin-top: 1rem;
  padding: 0.3rem;
  background: rgba(247, 136, 228, 0.7);
  border-radius: 1.2rem;
}
.imgWrap {
  padding: .8rem;
  border-radius: 1rem;
  background: #f7f2ff;
  max-height: 50vh;
  overflow: hidden;
}
.imgWrap img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: .8rem;
}
.titleBox {
  margin-top: 1rem;
  padding: 2rem;
  background: #efe7ff;
  border-radius: .8rem;
  border: none;
  font-size: 1.8rem;
  font-weight: 700;
  width: 100%;
  text-align: center;
}

/* ── 비어있을 때 ── */
.no-diary-container {
  margin-top: 1.2rem;
  padding: 2rem;
  background: #f7f2ff;
  border-radius: 1.2rem;
  text-align: center;
}
.no-diary-text {
  font-size: 1.8rem;
  font-weight: 700;
  color: #6b5a7a;
  white-space: pre-line;
}
.highlight { color: #7c4dff; font-weight: 900; }
.no-diary-btn {
  display: inline-block;
  margin-top: 1rem;
  width: 80%;
  padding: 1.2rem;
  border-radius: 9999px;
  font-size: 1.6rem;
  font-weight: 800;
  color: #ffffff;
  background: #7c4dff;
  border: 1px solid #6b4ae6;
  cursor: pointer;
}

/* 홈 버튼 */
.homeBtn {
  position: sticky;
  bottom: 12px;
  z-index: 10;
  display: block;
  margin-top: 1rem;
  text-align: center;
  padding: 1.2rem;
  border-radius: 9999px;
  font-size: 1.6rem;
  font-weight: 800;
  color: #fff;
  background: #b388ff;
  text-decoration: none;
}

/* 모달 */
.modal-overlay {
  position: fixed; inset: 0;
  display: flex; align-items: center; justify-content: center;
  background: rgba(0,0,0,.45);
}
.modal-card {
  width: min(520px, 92vw);
  max-height: 90vh;
  background: #fff;
  border-radius: 1.2rem;
  padding: 1.2rem;
}

/* 페이드 */
.fade-enter-active, .fade-leave-active { transition: opacity .15s }
.fade-enter-from, .fade-leave-to { opacity: 0 }
</style>