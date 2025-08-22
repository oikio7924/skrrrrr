import { createRouter, createWebHistory } from 'vue-router'
// HomeView import를 삭제했습니다.
import SignupView from '@/views/SignupView.vue'
import LoginView from '@/views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/', // 앱의 첫 화면은 LoginView 입니다.
      name: 'login',
      component: LoginView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
    },
    // '/about' 경로와 주석 처리된 '/login' 경로는 완전히 삭제했습니다.
  ],
})

export default router