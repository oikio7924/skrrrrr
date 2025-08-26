import { createRouter, createWebHistory } from 'vue-router'
import Signup from '@/views/Signup.vue'
import ChildLogin from '@/views/Login.vue' //자녀용
import ParentLogin from '@/views/ParentLogin.vue' // 부모용
import ChatBot from '@/views/ChatBot.vue'
import PictureDiary from '@/views/PictureDiary.vue'
import DiaryDetail from '@/views/DiaryDetail.vue'
import Footer from '@/views/Footer.vue'
import Onboarding from '@/views/Onboarding.vue'
import Start from '@/views/Start.vue'
import Signupdetail_Child from '@/views/Signupdetail_Child.vue'
import Signupdetail_Parent from '@/views/Signupdetail_Parent.vue'
// import Main_Parent from '@/views/Main_Parent.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/', // 앱 첫 화면 온보딩화면
      redirect: { name: 'Onboarding' }
    },
    {
      path: '/onboarding',
      name: 'Onboarding',
      component: () => Onboarding,
    },
    {
      path: '/login',
      name: 'login',
      component: () => ChildLogin,
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => Signup,
    },
    {
      path: '/start',
      name: 'Start',
      component: () => Start,
    },
    {
      path: '/parent-login',
      name: 'parentlogin',
      component: () => ParentLogin,
    },

    {
      path : '/Signupdetail_child',
      name : 'Signupdetail_child',
      component : () => Signupdetail_Child
    },

    {
      path : '/Signupdetail_parent',
      name : 'Signupdetail_parent',
      component : () => Signupdetail_Parent
    },

    { path: '/mainparent', name: 'MainParent', component: () => import('@/views/Main_Parent.vue')},


    {
      path: '/chat',
      name: 'ChatBot',
      component: () => ChatBot,
    },
    {
      path: '/diary_p',
      name: 'diary_p',
      component: PictureDiary,
      children: [
        {
          path: 'day/:date',
          name: 'dayModal',
          component: () => DiaryDetail,
          props: true,
          meta: { modal: true }   // ← 모달 표시 플래그
        }
      ]
    },
    { path: '/day/:date', name: 'dayPage', component: DiaryDetail, props: true },

    {
      path: '/footer',
      name: 'footer',
      component: () => Footer,
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
