<template>
  <div class="events-page mobile-screen flex flex-col mx-auto min-h-screen">
    <header class="flex items-center justify-between p-4 bg-gray-100 border-b">
      <button class="back-button" @click="goBack" aria-label="뒤로 가기">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-6 h-6">
          <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
        </svg>
      </button>

      <h1 class="text-xl font-bold flex-1 text-center">일정 관리</h1> <button
        class="add-button"
        @click="openEventModal"
      >
        일정 추가
      </button>
    </header>

    <main class="flex-grow p-4 bg-white overflow-auto relative">
      <div v-if="loading" class="text-center text-gray-500 mt-10">
        일정을 불러오는 중입니다...
      </div>
      <div v-else-if="eventsByDate.length === 0" class="text-center text-gray-500 mt-10">
        등록된 일정이 없어요. 😊
      </div>
      <div v-else class="space-y-6 pb-20">
        <div v-for="(day, index) in eventsByDate" :key="index" class="day-card">
          <div class="date-header">
            <span class="date-text">{{ formatDate(day.date) }}</span>
          </div>
          <ul class="schedule-list">
            <li v-for="event in day.events" :key="event.id" class="schedule-item">
              <span class="time-label">{{ event.time }}</span>
              <div class="event-details flex-1">
                <span class="event-title">{{ event.title }}</span>
                <p v-if="event.note" class="event-note">{{ event.note }}</p>
              </div>
              <button class="edit-btn" @click="editEvent(event)">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5">
                  <path d="M13.586 3.586a2 2 0 112.828 2.828l-7.293 7.293a1 1 0 01-.39.24l-3 1a1 1 0 01-1.253-1.253l1-3a1 1 0 01.24-.39l7.293-7.293zM15 6L13.5 4.5l-6.5 6.5L6 11.5l1.5 1.5 6.5-6.5z" />
                </svg>
              </button>
            </li>
          </ul>
        </div>
      </div>
    </main>

    <EventModal
      :show="showModal"
      :initial-form-data="currentEvent"
      @close="closeEventModal"
      @save="saveEvent"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // useRouter 임포트
import EventModal from '@/components/EventModal.vue';
import { v4 as uuidv4 } from 'uuid';

type EventItem = {
  id: string;
  date: string;
  time: string;
  title: string;
  note: string;
};

type EventsByDate = {
  date: string;
  events: EventItem[];
};

const router = useRouter(); // useRouter 인스턴스 생성
const showModal = ref(false);
const loading = ref(true);
const eventsByDate = ref<EventsByDate[]>([]);
const currentEvent = ref<Partial<EventItem>>({});

const LS_KEY = 'harudam.events.local.v1';

const openEventModal = () => {
  currentEvent.value = {};
  showModal.value = true;
};

const closeEventModal = () => {
  showModal.value = false;
};

const saveEvent = (formData: Omit<EventItem, 'id'>) => {
  let storedEvents: EventItem[] = JSON.parse(localStorage.getItem(LS_KEY) || '[]');
  
  if (currentEvent.value.id) {
    // 수정
    const index = storedEvents.findIndex(e => e.id === currentEvent.value.id);
    if (index !== -1) {
      storedEvents[index] = { ...storedEvents[index], ...formData };
    }
  } else {
    // 등록
    const newEvent: EventItem = {
      id: uuidv4(),
      ...formData
    };
    storedEvents.push(newEvent);
  }

  // 시간순 및 날짜순 정렬
  storedEvents.sort((a, b) => {
    const dateA = a.date.replace(/-/g, '');
    const dateB = b.date.replace(/-/g, '');
    const timeA = a.time.replace(':', '');
    const timeB = b.time.replace(':', '');
    return (dateA + timeA).localeCompare(dateB + timeB);
  });

  localStorage.setItem(LS_KEY, JSON.stringify(storedEvents));
  loadEvents();
  closeEventModal();
};

const editEvent = (event: EventItem) => {
  currentEvent.value = { ...event };
  showModal.value = true;
};

const loadEvents = () => {
  loading.value = true;
  const storedEvents: EventItem[] = JSON.parse(localStorage.getItem(LS_KEY) || '[]');
  
  const grouped: Record<string, EventItem[]> = {};
  storedEvents.forEach(event => {
    if (!grouped[event.date]) {
      grouped[event.date] = [];
    }
    grouped[event.date].push(event);
  });
  
  eventsByDate.value = Object.keys(grouped).sort().map(date => ({
    date,
    events: grouped[date]
  }));
  
  loading.value = false;
};

const formatDate = (dateString: string) => {
  const [year, month, day] = dateString.split('-').map(Number);
  const date = new Date(year, month - 1, day);
  const dayOfWeek = date.toLocaleDateString('ko-KR', { weekday: 'short' });
  return `${month}월 ${day}일 (${dayOfWeek})`;
};

// ✅ 뒤로 가기 기능 추가
const goBack = () => {
  router.back(); // 이전 페이지로 이동
};

onMounted(() => {
  loadEvents();
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');

.events-page {
  font-family: 'Jua', 'Noto Sans KR', sans-serif;
  font-variant-numeric: tabular-nums;
  --lav-50: #F5F3FF;
  --lav-100: #EDE9FE;
  --lav-200: #DDD6FE;
  --lav-400: #A78BFA;
  --lav-500: #8B5CF6;
}

.mobile-screen {
  width: 100%;
  max-width: 480px;
  min-height: 100vh;
  margin: 0 auto;
  border: none;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
  overflow: visible;
  position: relative;
}

/* ✅ 뒤로 가기 버튼 스타일 */
.back-button {
  background: none;
  border: none;
  color: #374151; /* 어두운 회색 */
  padding: 8px;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: flex; /* SVG 중앙 정렬 */
  align-items: center;
  justify-content: center;
  width: 40px; /* 터치 영역 확보 */
  height: 40px; /* 터치 영역 확보 */
}

.back-button:hover {
  background-color: rgba(0, 0, 0, 0.05); /* 호버 시 살짝 배경색 */
}

.add-button {
  background: var(--lav-500);
  color: #fff;
  padding: 8px 16px;
  border-radius: 9999px;
  font-size: 0.9rem;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.25);
  transition: transform 0.1s ease;
}

.add-button:active {
  transform: scale(0.98);
  box-shadow: none;
}

.day-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.06);
}

.date-header {
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f3f4f6;
}

.date-text {
  font-size: 1.1rem;
  font-weight: bold;
  color: #374151;
}

.schedule-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.schedule-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f9fafb;
}

.schedule-item:last-child {
  border-bottom: none;
}

.time-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #6d28d9;
  min-width: 60px;
}

.event-title {
  font-size: 1rem;
  font-weight: 500;
  color: #1f2937;
}

.event-note {
  font-size: 0.8rem;
  color: #6b7280;
  margin-top: 4px;
}

.edit-btn {
  background: #f5f3ff;
  color: #8b5cf6;
  border-radius: 9999px;
  padding: 8px;
  transition: background 0.2s ease;
}

.edit-btn:hover {
  background: #ede9fe;
}
</style>