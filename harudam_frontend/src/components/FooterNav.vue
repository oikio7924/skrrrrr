<template>
  <nav class="tabbar" role="tablist">
    <div class="indicator" :style="indStyle" aria-hidden="true">
      <div class="disc">
        <span class="ring"></span>
        <span class="shine"></span>
      </div>
      <div class="glow"></div>
    </div>

    <router-link
      v-for="(item, i) in items"
      :key="item.name"
      :to="item.path"
      class="tab"
      role="tab"
      :aria-selected="activeIndex === i"
      :class="{ active: activeIndex === i }"
    >
      <div class="icon-wrap">
        <span class="icon" v-html="item.icon" />
      </div>
      <span class="label">{{ item.label }}</span>
    </router-link>
  </nav>
</template>

<script setup lang="ts">
// nextTick을 vue에서 추가로 가져옵니다.
import { computed, ref, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'

type Item = { name: string; label: string; icon: string; path: string }

const items: Item[] = [
  {
    name: 'home',
    label: '홈',
    path: '/main_child',
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 10.5 12 3l9 7.5"/><path d="M5 10v10h14V10"/></svg>`
  },
  {
    name: 'calendar',
    label: '달력',
    path: '/calendar_child',
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="16" rx="3"/><path d="M16 3v4M8 3v4M3 10h18"/></svg>`
  },
  {
    name: 'memoir',
    label: '자서전',
    path: '/memoir',
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="4" width="12" height="16" rx="2.5"/><path d="M8 17.5h8"/></svg>`
  },
  {
    name: 'mypage',
    label: '마이페이지',
    path: '/mypage',
    icon: `<svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg>`
  }
]

const activeIndex = ref(0)
const indStyle = computed(() => ({
  '--x': `${activeIndex.value * 100}%`
}))

const route = useRoute()
watch(
  () => route.path,
  async (newPath) => {
    // 👇 activeIndex를 업데이트하기 전에 nextTick으로 한번 기다려줍니다.
    await nextTick()

    const newIndex = items.findIndex((item) => item.path === newPath)
    if (newIndex !== -1) {
      activeIndex.value = newIndex
    }
  },
  { immediate: true, flush: 'post' } // flush 옵션을 추가하여 렌더링 후에 watch가 실행되도록 합니다.
)
</script>

<style scoped>
:root {
  --accent: #5b3cc4;
  --accentLight: #9d7dff;
  --ink-weak: #9ca3af;
}

.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 10;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: #fff;
  padding: 16px 16px calc(16px + env(safe-area-inset-bottom));
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
  box-shadow:
    0 -1px 0 rgba(20, 25, 45, .06) inset,
    0 -14px 36px rgba(20, 25, 45, .06);
  overflow: visible;
}

.tab {
  appearance: none;
  background: transparent;
  border: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--ink-weak);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: color .2s ease;
  text-decoration: none;
  padding: 0;
  margin: 0;
  text-align: center;
}

.icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 32px;
}

/* ▼ 모든 애니메이션을 제어하는 핵심 부분 ▼ */
.tab .icon,
.tab .label {
  transition: transform .45s cubic-bezier(.22, .61, .36, 1), color .2s;
}

.tab svg {
  stroke: var(--ink-weak);
  transition: stroke .2s ease;
}

.indicator {
  position: absolute;
  left: 16px;
  bottom: calc(40px + env(safe-area-inset-bottom));
  width: calc((100% - 32px) / 4);
  display: grid;
  place-items: center;
  transform: translateX(var(--x, 0));
  transition: transform .45s cubic-bezier(.22, .61, .36, 1);
}
/* ▲ 모든 애니메이션을 제어하는 핵심 부분 ▲ */


/* ▼ 활성화 상태 스타일 ▼ */
.tab.active {
  color: var(--accent);
}

.tab.active .icon {
  transform: translateY(-16px) scale(1.1);
}

.tab.active svg {
  stroke: var(--accent);
}
/* ▲ 활성화 상태 스타일 ▲ */


/* ▼ Indicator 상세 디자인 ▼ */
.indicator .glow {
  position: absolute;
  width: 82px;
  height: 82px;
  border-radius: 999px;
  filter: blur(10px);
  background: radial-gradient(closest-side, rgb(139 125 255 / 70%), transparent);
}
.indicator .disc {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 999px;
  background: #fff;
  box-shadow:
    0 16px 28px rgba(108, 91, 255, .18),
    0 4px 10px rgba(0, 0, 0, .06),
    inset 0 1px 2px rgba(255, 255, 255, .9);
}
.indicator .disc .ring {
  position: absolute;
  inset: -2px;
  border-radius: 999px;
  background: conic-gradient(from 0deg, var(--accentLight), var(--accent) 60%, #5b49ff 100%);
  -webkit-mask: radial-gradient(farthest-side, transparent calc(100% - 3px), #000 0);
          mask: radial-gradient(farthest-side, transparent calc(100% - 3px), #000 0);
}
.indicator .disc .shine {
  position: absolute;
  inset: 0;
  border-radius: 999px;
  background: radial-gradient(90px 40px at 50% 6px, rgba(255, 255, 255, .95), transparent);
}
</style>
