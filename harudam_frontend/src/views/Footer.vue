<template>
  <nav class="tabbar" role="tablist">
    <!-- 공(Indicator) -->
    <div class="tabball" :style="ballStyle" aria-hidden="true">
      <div class="ball-shadow"></div>
      <div class="ball-core"></div>
    </div>

    <RouterLink
      v-for="(item, i) in items"
      :key="item.name"
      :to="item.to"
      class="tab"
      role="tab"
      :aria-selected="activeIndex === i"
      :class="{ active: activeIndex === i }"
      @click="setActive(i)"
    >
      <!-- eslint-disable-next-line vue/no-v-html -->
      <span class="icon" v-html="item.icon" />
      <span class="label">{{ item.label }}</span>
    </RouterLink>
  </nav>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import type { RouteLocationRaw, RouteLocationNamedRaw, RouteRecordName } from 'vue-router'

/** 탭 아이템 타입 */
type Item = {
  name: string
  label: string
  to: RouteLocationRaw
  icon: string
}

/** 탭 항목 정의 */
const items: Item[] = [
  {
    name: 'home',
    label: '홈',
    to: { name: 'home' },
    icon: `<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 10.5 12 3l9 7.5"/><path d="M5 9.5V21h14V9.5"/></svg>`
  },
  {
    name: 'calendar',
    label: '달력',
    to: { name: 'calendar' },
    icon: `<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>`
  },
  {
    name: 'memoir',
    label: '자서전',
    to: { name: 'memoir' },
    icon: `<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19V5a3 3 0 0 1 3-3h10v20H7a3 3 0 0 1-3-3z"/><path d="M7 6h7"/></svg>`
  },
  {
    name: 'mypage',
    label: '마이페이지',
    to: { name: 'mypage' },
    icon: `<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg>`
  }
]

/** 타입가드: to가 { name: ... } 형태인지 판별 */
function isNamedRoute(to: RouteLocationRaw): to is RouteLocationNamedRaw {
  return typeof to === 'object' && to !== null && 'name' in to
}

const route = useRoute()
const activeIndex = ref(0)

/** route.name 안전하게 가져오기 */
const currentName = computed<RouteRecordName | null>(() => route.name ?? null)

/** 현재 라우트와 탭 동기화 */
const syncByRoute = () => {
  const idx = items.findIndex(i => isNamedRoute(i.to) && i.to.name === currentName.value)
  if (idx !== -1) activeIndex.value = idx
}

syncByRoute()
watch(currentName, syncByRoute)

/** 공 애니메이션 위치 */
const ballStyle = computed(() => {
  const step = 100 / items.length
  return { transform: `translateX(${activeIndex.value * step}%)` }
})

function setActive(i: number) {
  activeIndex.value = i
}
</script>

<style scoped>
.tabbar {
  position: sticky;
  bottom: 0;
  left: 0;
  z-index: 50;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  align-items: end;
  width: 100%;
  background: linear-gradient(#eef1f6, #e7ebf3);
  padding: 10px 12px calc(10px + env(safe-area-inset-bottom));
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  box-shadow: 0 -6px 20px rgba(22,27,45,0.08) inset;
}

.tab {
  text-align: center;
  padding: 12px 0 6px;
  color: #7e87a1;
  font-size: 12px;
  text-decoration: none;
  user-select: none;
}
.tab.active { color: #5b61ff; }

.icon {
  display: block;
  margin: 0 auto 6px;
}

.tabball {
  position: absolute;
  left: 12px;
  bottom: calc(14px + env(safe-area-inset-bottom));
  width: calc((100% - 24px) / 4);
  height: 44px;
  transition: transform .45s cubic-bezier(.22,.61,.36,1);
  pointer-events: none;
}

.ball-shadow {
  position: absolute;
  inset: 0;
  transform: translateY(6px);
  filter: blur(10px);
  border-radius: 999px;
  background: radial-gradient(closest-side, rgba(91,97,255,.45), rgba(91,97,255,0));
}

.ball-core {
  position: absolute;
  inset: 0;
  margin: auto;
  width: 36px;
  height: 36px;
  border-radius: 999px;
  background: radial-gradient(circle at 35% 30%, #b8b9ff, #6a6fff 55%, #5959ff);
  box-shadow:
    0 10px 20px rgba(90,95,255,.35),
    0 2px 6px rgba(0,0,0,.06);
}
</style>
