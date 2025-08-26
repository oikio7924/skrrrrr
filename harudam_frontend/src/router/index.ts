import { createRouter, createWebHistory } from 'vue-router'
// HomeView import를 삭제했습니다.
import Signup from '@/views/Signup.vue'
import Login from '@/views/Login.vue' //자녀용
import ParentLogin from '@/views/ParentLogin.vue' // 부모용
import ChatBot from '@/views/ChatBot.vue'
import PictureDiary from '@/views/PictureDiary.vue'
import DiaryDetail from '@/views/DiaryDetail.vue'
<<<<<<< Updated upstream
import Main_Child from '@/views/Main_Child.vue'
import FooterNav from '@/components/FooterNav.vue'
import Calendar_Child from '@/views/Calendar_Child.vue'
import ProfileDiary from '@/views/ProfileDiary.vue'
=======
import MainP from '@/views/MainP.vue'
import Main_Child from '@/views/Main_Child.vue'
import FooterNav from '@/views/FooterNav.vue'
// import FooterNav from '@/views/FooterNav.vue'
>>>>>>> Stashed changes


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
      path: '/footer',
      name: 'footer',
      component: FooterNav,
    },

    {
      path: '/main_child',
      name: 'main_child',
<<<<<<< Updated upstream
      component: Main_Child,
      meta: { showFooter: true }
    },

    {
      path: '/calendar_child',
      name: 'calendar_child',
      component: Calendar_Child,
      meta: { showFooter: true }
    },

    {
      path: '/profile_diary',
      name: 'profile_diary',
      component: ProfileDiary,
      meta: { showFooter: true }
=======
      component: Main_Child
>>>>>>> Stashed changes
    },
  ],


  scrollBehavior() { return { top: 0 } }

})

export default router
