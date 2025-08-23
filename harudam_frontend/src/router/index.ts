import { createRouter, createWebHistory } from 'vue-router'
// HomeView import를 삭제했습니다.
import SignupView from '@/views/SignupView.vue'
import LoginView from '@/views/LoginView.vue' //자녀용
import ParentLogin from '@/views/ParentLogin.vue' // 부모용

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/', // 앱 첫 화면 온보딩화면
      redirect: { name: 'Onboarding' }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
    },
    // '/about' 경로와 주석 처리된 '/login' 경로는 완전히 삭제했습니다.
    {
      path: '/onboarding',
      name: 'Onboarding',
      component: () => import('@/views/Onboarding.vue'),
    },
    {
      path: '/start',
      name: 'StartView',
      component: () => import('@/views/StartView.vue'),
    },
    {
      path: '/parent-login',
      name: 'parentlogin',
      component: ParentLogin,
    },


  ],
})

export default router