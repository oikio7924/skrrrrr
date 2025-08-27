// src/stores/eventStore.ts
import { defineStore } from 'pinia';
import { v4 as uuidv4 } from 'uuid';

export type EventItem = {
  id: string;
  date: string;
  time: string;
  title: string;
  note: string;
};

export const useEventStore = defineStore('eventStore', {
  state: () => ({
    events: [] as EventItem[],
  }),
  actions: {
    loadEvents() {
      // 로컬 스토리지에서 데이터 불러오기
      try {
        const storedEvents = localStorage.getItem('harudam.events.local.v1');
        if (storedEvents) {
          this.events = JSON.parse(storedEvents) as EventItem[];
        }
      } catch (e) {
        console.error('Failed to load events from localStorage:', e);
        this.events = [];
      }
    },
    saveEvent(formData: Omit<EventItem, 'id'>) {
      const newEvent: EventItem = {
        id: uuidv4(),
        ...formData,
      };
      this.events.push(newEvent);
      this.persistEvents();
    },
    updateEvent(updatedEvent: EventItem) {
      const index = this.events.findIndex(e => e.id === updatedEvent.id);
      if (index !== -1) {
        this.events[index] = updatedEvent;
      }
      this.persistEvents();
    },
    persistEvents() {
      // 로컬 스토리지에 데이터 저장
      localStorage.setItem('harudam.events.local.v1', JSON.stringify(this.events));
    },
  },
  getters: {
    // 날짜를 기준으로 정렬된 일정 목록
    sortedEvents(state) {
      return [...state.events].sort((a, b) => {
        const dateA = a.date.replace(/-/g, '');
        const dateB = b.date.replace(/-/g, '');
        const timeA = a.time.replace(':', '');
        const timeB = b.time.replace(':', '');
        return (dateA + timeA).localeCompare(dateB + timeB);
      });
    },
    // 최근 3개의 주요 일정 가져오기
    recentEvents(state): EventItem[] {
      const today = new Date().toISOString().slice(0, 10);
      return this.sortedEvents
        .filter(event => event.date <= today)
        .slice(-3); // 최근 3개
    },
    // 앞으로 다가올 일정 가져오기
    upcomingEvents(state): EventItem[] {
      const today = new Date().toISOString().slice(0, 10);
      return this.sortedEvents
        .filter(event => event.date >= today)
        .slice(0, 3); // 다가올 3개
    },
  },
});