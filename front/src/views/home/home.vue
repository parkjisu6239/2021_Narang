<template>
  <div class="home">
    <div class="home-left-side">
      <Title class="title"/>
      <Information class="information"/>
    </div>
    <div class="home-right-side">
      <div class="home-right-side-container">
        <div :class="{'img-neorang': true, 'right': state.value }">
          <img :src="require('@/assets/images/Neurang.png')" alt="">
        </div>
        <div class="form">
          <div class="form-login-signup" v-if="!state.isLoggedIn">
            <div class="form-toggle">
              <div :class="{'toggle-login': true, 'active': !state.value}" @click="state.value = !state.value">Login</div>
              <div :class="{'toggle-signup': true, 'active': state.value}" @click="state.value = !state.value">Signup</div>
            </div>
            <LogIn v-if="!state.value"/>
            <SignUp v-else @signupSuccess="state.value = false"/>
          </div>
          <div v-else>
            <GameStart/>
          </div>
        </div>
      </div>
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

<style scoped>
  @import url('./home.css');
</style>
