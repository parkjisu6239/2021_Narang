<template>
  <div>
    <Title/>
    <Information/>

    <div v-if="!state.isLoggedIn">
      <el-switch
        style="display: inline-block"
        v-model="state.value"
        active-color="#13ce66"
        inactive-color="#ff4949"
        active-text="회원가입"
        inactive-text="로그인"
      >
      </el-switch>
      <LogIn v-if="!state.value"/>
      <SignUp v-else @signupSuccess="state.value = false"/>
    </div>
    <div v-else>
      <GameStart/>
    </div>
  </div>
</template>

<script>
import Title from './components/title'
import Information from './components/information'
import LogIn from './components/login'
import SignUp from './components/signup'
import GameStart from './components/game-start'

import { reactive, computed } from 'vue'
import { useStore } from 'vuex'

export default {
  name: "home",

  components: {
    Title,
    Information,
    LogIn,
    SignUp,
    GameStart,
  },

  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
      value: false,
      isLoggedIn: computed(() => store.getters['root/isLoggedIn']),
    })

    return { state }
  }

}
</script>

<style>

</style>
