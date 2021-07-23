<template>
  <el-button type="primary" round @click="clickGameStart">Game Start</el-button>
  <el-button type="success" round @click="clickMyPage">My page</el-button>
  <el-button type="danger" round @click="clickLogOut">Logout</el-button>
</template>

<script>
import { reactive, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: "gameStart",

  setup(props, { emit }) {
    const store = useStore()
    const router = useRouter()

    const state = reactive({
      isLoggedIn: computed(() => store.getters['root/isLoggedIn']),
      email: computed(() => store.getters['root/email']),
    })

    const clickLogOut = () => {
      store.dispatch('root/requestLogout')
      store.commit('root/deleteToken')
      router.push({
        name: 'home'
      })
    }

    const clickGameStart = () => {
      router.push({
        name: 'waitingRoom'
      })
    }

    const clickMyPage = () => {
      store.commit('root/setUserEmail')
      router.push({
        name: 'mypage',
      })
    }

    return { state, clickLogOut, clickGameStart, clickMyPage }
  }
}
</script>

<style>

</style>
