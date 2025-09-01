<template>
  <!-- ✅ 전체 페이지를 감싸는 최상위 컨테이너 -->
  <div class="diary-feed-page-wrapper">
    <!-- ✅ 타임라인 헤더 (기존 코드와 동일하게 유지) -->
    <header class="timeline-header">
      <button class="back-btn" @click="goBack" aria-label="뒤로가기">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
          class="icon">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <h1 class="title">그림일기 피드</h1>
    </header>

    <!-- ✅ [수정] 스크롤이 필요한 콘텐츠를 main 태그로 감쌈 -->
    <main class="content">
      <div class="profile-banner"></div>

      <!-- 프로필 -->
      <div class="profile-info">
        <div class="profile-photo">
          <img v-if="parent?.profileImage" :src="parent.profileImage" alt="프로필 사진" />
          <span v-else>사진</span>
        </div>
        <h2 class="profile-name">{{ parent?.name || "이름 없음" }}</h2>
        <p class="profile-sub">{{ parent?.birth || "생년월일 없음" }}</p>
      </div>

      <!-- 통계 -->
      <div class="profile-stats">
        <div class="stat">
          <strong>{{ diaries.length }}</strong>
          <span>그림일기</span>
        </div>
        <div class="stat">
          <strong>D+{{ dDay }}</strong>
          <span>접속일</span>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 그림일기 피드 -->
      <section class="diary-feed">
        <div v-for="item in diaries" :key="item.id" class="diary-card" @click="goToDiary(item)">
          <div class="diary-thumb">
            <img v-if="item.imageUrl" :src="item.imageUrl" alt="그림일기 썸네일" />
          </div>
          <p class="diary-caption">{{ item.title }}</p>
        </div>
      </section>
    </main>

    <!-- Footer -->
    <FooterNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue"
import { useRouter } from "vue-router"
import FooterNav from "@/components/FooterNav.vue"
import apiClient from "@/api/index"

const router = useRouter()

// 부모 정보 타입
type ParentInfo = {
  id: number
  name: string
  gender: "M" | "F"
  birth: string
  profileImage?: string
  joinedDate: string
}

// 그림일기 타입
type Diary = {
  id: number
  title: string
  date: string
  imageUrl: string
}

// ✅ 상태
const parent = ref<ParentInfo | null>(null)
const diaries = ref<Diary[]>([])

// ✅ 접속일(D+N)
const dDay = computed(() => {
  if (!parent.value?.joinedDate) return 0
  const joined = new Date(parent.value.joinedDate)
  const today = new Date()
  const diff = today.getTime() - joined.getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
})

// ✅ API 호출 (기존 코드 복원)
const loadParentInfo = async () => {
  try {
    const { data } = await apiClient.get<ParentInfo>("/api/parent/1")
    parent.value = data
  } catch (e) {
    console.error("부모 정보 불러오기 실패", e)
  }
}

const loadDiaries = async () => {
  try {
    const { data } = await apiClient.get<Diary[]>("/api/diaries/parent/1")
    diaries.value = data
  } catch (e) {
    console.error("그림일기 불러오기 실패", e)
  }
}

onMounted(() => {
  loadParentInfo()
  loadDiaries()
})

// ✅ 라우팅
function goBack() {
  router.push({ name: "main_child" })
}

function goToDiary(diary: Diary) {
  router.push({ name: "profileDiaryDetail", params: { id: diary.id } })
}
</script>


<style scoped>
/* ===== 전체 레이아웃 구조 ===== */
.diary-feed-page-wrapper {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #fff;
  position: relative;
  overflow: hidden; /* 내부의 absolute 요소가 밖으로 나가지 않도록 */
}

.content {
  flex: 1;
  overflow-y: auto;
  position: relative;
  z-index: 1;
  /* ✅ [수정] 좌우 패딩 제거 */
  padding-left: 0;
  padding-right: 0;
}

/* ===== 헤더 스타일 ===== */
.timeline-header {
  position: absolute; /* ✅ wrapper를 기준으로 상단에 고정 */
  top: 0;
  left: 0;
  width: 100%;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.8rem 1rem;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
}

.back-btn {
  position: absolute;
  left: 1rem;
  background: none;
  border: none;
  cursor: pointer;
}

.back-btn .icon {
  width: 28px;
  height: 28px;
  stroke: #6d28d9;
}

.timeline-header .title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #333;
}

/* ===== 프로필 상단 영역 (기존 디자인 유지) ===== */
.profile-banner {
  width: 100%;
  height: 220px;
  background: linear-gradient(135deg, #e8e1ff, #c7b9ff, #dcd0ff);
  border-bottom-left-radius: 50% 20%;
  border-bottom-right-radius: 50% 20%;
}

.profile-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: -100px;
  position: relative;
  z-index: 2;
  /* ✅ [추가] 좌우 패딩 추가 */
  padding: 0 1rem;
  box-sizing: border-box;
}

.profile-photo {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  background: #fff;
  border: 6px solid #c7b9ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.6rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  color: #aaa;
}

.profile-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}


.profile-name {
  font-size: 1.5rem;
  font-weight: 700;
  margin-top: 0.2rem;
}

.profile-sub {
  font-size: 0.9rem;
  margin-top: 0.1rem;
  color: #666;
}

.profile-stats {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin: 0.8rem 0;
  text-align: center;
  /* ✅ [추가] 좌우 패딩 추가 */
  padding: 0 1rem;
  box-sizing: border-box;
}

.stat strong {
  display: block;
  font-size: 1.4rem;
  font-weight: 700;
  color: #4b3fae;
}

.stat span {
  font-size: 0.9rem;
  color: #666;
}

.divider {
  /* ✅ [수정] 너비 계산 방식 변경 */
  width: calc(100% - 2rem);
  height: 1px;
  background: #e5e7eb;
  margin: 1rem auto 1.5rem;
}

.diary-feed {
  width: 100%;
  padding: 0 1rem 1rem;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  box-sizing: border-box;
}

.diary-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: transform 0.2s;
}

.diary-card:hover {
  transform: translateY(-4px);
}

.diary-thumb {
  background: #e8e1ff;
  aspect-ratio: 1 / 1;
}

.diary-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.diary-caption {
  margin: 0.5rem;
  font-size: 0.9rem;
  color: #333;
}
</style>

