import { createRouter, createWebHistory } from 'vue-router'
// HomeView import를 삭제했습니다.
import Signup from '@/views/Signup.vue'
import Login from '@/views/Login.vue' //자녀용
import ParentLogin from '@/views/ParentLogin.vue' // 부모용
import ChatBot from '@/views/ChatBot.vue'
import PictureDiary from '@/views/PictureDiary.vue'
import DiaryDetail from '@/views/DiaryDetail.vue'
import Footer from '@/views/Footer.vue'


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
      component: Login,
    },
    {
      path: '/signup',
      name: 'signup',
      component: Signup,
    },
    // '/about' 경로와 주석 처리된 '/login' 경로는 완전히 삭제했습니다.
    {
      path: '/onboarding',
      name: 'Onboarding',
      component: () => import('@/views/Onboarding.vue'),
    },
    {
      path: '/start',
      name: 'Start',
      component: () => import('@/views/Start.vue'),
    },
    {
      path: '/parent-login',
      name: 'parentlogin',
      component: ParentLogin,
    },

    {
      path : '/Singupdetail_child',
      name : 'Singupdetail_child',
      component : () => import('@/views/Signupdetail_Child.vue')
    },

    {
      path : '/Singupdetail_parent',
      name : 'Singupdetail_parent',
      component : () => import('@/views/Signupdetail_Parent.vue')
    },

    { path: '/mainparent', name: 'MainParent', component: () => import('@/views/Main_Parent.vue')},


    {
      path: '/chat',
      name: 'ChatBot',
      component: ChatBot
    },
    {
      path: '/diary_p',
      name: 'diary_p',
      component: PictureDiary,
      children: [
        {
          path: 'day/:date',
          name: 'dayModal',
          component: DiaryDetail,
          props: true,
          meta: { modal: true }   // ← 모달 표시 플래그
        }
      ]
    },
    { path: '/day/:date', name: 'dayPage', component: DiaryDetail, props: true },

    {
      path: '/footer',
      name: 'footer',
      component: Footer,
    },
    { path :'/schedule_p',
      name : 'schedule_p',
      component : ()=> import('@/views/Schedule_Parent.vue')
    },
    {
      path : '/header',
      name : 'header',
      component : () => import('@/views/Header.vue')
    }

  ],


 
    


  scrollBehavior() { return { top: 0 } }  

})

export default router
