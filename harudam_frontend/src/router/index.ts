import { createRouter, createWebHistory } from 'vue-router'
// HomeView import를 삭제했습니다.
import SignupView from '@/views/SignupView.vue'
import LoginView from '@/views/LoginView.vue' //자녀용
import ParentLogin from '@/views/ParentLogin.vue' // 부모용
import ChatBot from '@/views/ChatBot.vue'
import PictureDiary from '@/views/PictureDiary.vue'
import DiaryDetail from '@/views/DiaryDetail.vue'
import MainP from '@/views/MainP.vue'

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

    {
      path : '/Singupdetail_child',
      name : 'Singupdetail_child',
      component : () => import('@/views/SignupdetailView_Child.vue')
    },

    {
      path : '/Singupdetail_parent',
      name : 'Singupdetail_parent',
      component : () => import('@/views/SignupdetailView_Parent.vue')
    },


    {
      path: '/chat',
      name: 'ChatBot',
      component: ChatBot
    },
    {
      path: '/calendar',
      name: 'calendar',
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
      path : '/mainP',
      name : 'mainP',
      component : MainP
    }
  ],


  scrollBehavior() { return { top: 0 } }  

})

export default router
