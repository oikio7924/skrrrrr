import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // path 모듈 import (alias를 위해)

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    }
  },
})
