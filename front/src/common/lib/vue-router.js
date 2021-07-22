import {createRouter, createWebHistory} from 'vue-router'
import Home from '@/views/home/home'
import Mypage from '@/views/mypage/mypage'


function makeRoutesFromMenu() {
  let routes = []
  // menu 자체에는 나오지 않는 페이지 라우터에 추가(방 상세보기)
  routes.push({
      path: '/home',
      name: 'home',
      component: Home
    },
    {
      path: 'mypage/:userId',
      name: 'mypage',
      component: Mypage
    },
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

export default router
