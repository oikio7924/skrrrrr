// src/stores/ui.ts
import { defineStore } from 'pinia'

export const useUiStore = defineStore('ui', {
  state: () => ({ pending: 0 }),
  getters: {
    loading: (s) => s.pending > 0,
  },
  actions: {
    start() {
      if (this.pending === 0 && document?.body) document.body.style.overflow = 'hidden'
      this.pending++
    },
    stop() {
      this.pending = Math.max(0, this.pending - 1)
      if (this.pending === 0 && document?.body) document.body.style.overflow = ''
    },
    reset() {
      this.pending = 0
      if (document?.body) document.body.style.overflow = ''
    },
  },
})
