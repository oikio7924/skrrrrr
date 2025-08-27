<template>
  <div
    id="event-modal"
    :class="{ show: show }"
    aria-hidden="true"
    role="dialog"
  >
    <div class="backdrop" @click="$emit('close')"></div>
    <div class="sheet p-4">
      <div class="h-1 w-10 bg-gray-300 rounded-full mx-auto mb-3"></div>
      <div class="flex items-center justify-between mb-3">
        <h3 class="text-lg font-bold">
          {{ formData.id ? '일정 수정' : '일정 등록' }}
        </h3>
        <button
          class="close-button"
          aria-label="닫기"
          @click="$emit('close')"
        >
          <span class="sr-only">닫기</span>
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="w-5 h-5">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>

      <form class="space-y-3" @submit.prevent="saveEvent">
        <div>
          <label class="label">날짜</label>
          <input
            v-model="formData.date"
            type="date"
            class="input"
            required
          />
        </div>
        <div>
          <label class="label">시간</label>
          <input
            v-model="formData.time"
            type="time"
            class="input"
          />
        </div>
        <div>
          <label class="label">제목</label>
          <input
            v-model.trim="formData.title"
            type="text"
            class="input"
            placeholder="예: 예방접종, 내과 외래"
            required
          />
        </div>
        <div>
          <label class="label">메모</label>
          <textarea
            v-model="formData.note"
            class="input"
            rows="3"
            placeholder="메모를 입력하세요(선택)"
          ></textarea>
        </div>

        <div class="pt-2 flex gap-2">
          <button
            type="button"
            class="flex-1 h-10 rounded-xl border hover:bg-gray-50"
            @click="$emit('close')"
          >
            취소
          </button>
          <button
            type="submit"
            class="flex-1 h-10 rounded-xl bg-[var(--lav-500)] text-white hover:brightness-110"
          >
            {{ formData.id ? '수정' : '저장' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue';

type FormData = {
  id?: string;
  date: string;
  time: string;
  title: string;
  note: string;
};

const props = defineProps<{
  show: boolean;
  initialFormData: Partial<FormData>;
}>();

const emit = defineEmits(['close', 'save']);

const formData = reactive<FormData>({
  date: '',
  time: '',
  title: '',
  note: ''
});

watch(() => props.initialFormData, (newVal) => {
  if (newVal) {
    Object.assign(formData, { ...newVal, date: newVal.date || toInputDate(new Date()) });
  }
}, { immediate: true, deep: true });

function toInputDate(d: Date): string {
  const pad2 = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;
}

const saveEvent = () => {
  emit('save', formData);
};
</script>

<style scoped>
#event-modal {
  position: fixed;
  inset: 0;
  display: none;
  z-index: 5000;
}
#event-modal.show {
  display: block;
}
#event-modal .backdrop {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
}
#event-modal .sheet {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -10px 30px rgba(0, 0, 0, 0.2);
  transform: translateY(100%);
  transition: transform 0.28s ease;
}
#event-modal.show .sheet {
  transform: translateY(0);
}

.input {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 10px 12px;
}
.label {
  font-size: 0.9rem;
  color: #374151;
  margin-bottom: 0.35rem;
  display: block;
  font-weight: 500;
}

.close-button {
  width: 36px;
  height: 36px;
  border-radius: 9999px;
  border: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  transition: background 0.2s ease;
}

.close-button:hover {
  background: #f3f4f6;
}
</style>