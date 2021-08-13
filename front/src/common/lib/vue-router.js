import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

import home from '@/views/home/home'
import waitingRoom from '@/views/waiting-room/waiting-room'
import gameRoom from '@/views/game-room/game-room'
import Mypage from '@/views/mypage/mypage'


/* navigation guard beforeEnter func */
const requireMafia = () => (to, from, next) => {
  if (from.name !== 'gameRoom') {
    ElMessage({
      type: 'error',
      message: '접근이 불가능 합니다.'
    })
    return next(from)
  } else {
    return next()
  }
}

const requireGameRoom = () => (to, from, next) => {
  if (from.name !== 'waitingRoom' && from.name !== 'mafia' && from.name !== 'callmy') {
    ElMessage({
      type: 'error',
      message: '접근이 불가능 합니다.'
    })
    return next(from)
  } else {
    return next()
  }
}

function makeRoutesFromMenu() {
  let routes = []
  // menu 자체에는 나오지 않는 페이지 라우터에 추가(방 상세보기)
  routes.push(
  {
    path: '/',
    name: 'home',
    component: home
  },
  {
    path: '/waiting-room',
    name: 'waitingRoom',
    component: waitingRoom,
    beforeEnter: [],
  },
  {
    path: '/mypage',
    name: 'mypage',
    component: Mypage
  },
  {
    path: '/game-room/:roomId',
    name: 'gameRoom',
    component: gameRoom,
    beforeEnter: requireGameRoom()
  },
  {
    path: '/game-room/:roomId/mafia',
    name: 'mafia',
    component: () => import('@/views/mafia/mafia.vue'),
    beforeEnter: requireMafia()
  },
  {
    path: '/game-room/:roomId/callmy',
    name: 'callmy',
    component: () => import('@/views/callmy/callmy.vue'),
  }
  )
  return routes
}

const routes = makeRoutesFromMenu()

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  console.log(to)
})

router.beforeEach((to, from) => {
  if (to.name !== 'home') {
    if (!isLogedin()) {
      ElMessage({
        type: 'error',
        message: '미로그인 사용자는 접근할 수 없습니다.'
      })
      return '/'
    } else {
      return true
    }
  }

  return true
})

const isLogedin = () => {
  return localStorage.getItem('username') ? true : false
}

export default router
