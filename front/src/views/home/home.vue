<template>
  <div class="home">
    <Title class="title"/>
    <div class="content">
      <Information class="information"/>
      <img :class="{'img-neorang': true, 'right': state.value }" :src="require('@/assets/images/Neurang.png')" alt="">
      <div class="form">
        <div v-if="!state.isLoggedIn">
          <el-switch
            style="display: inline-block"
            v-model="state.value"
            active-color="#FF5BF8"
            inactive-color="#007BFF"
            active-text="회원가입"
            inactive-text="로그인"
          ></el-switch>
          <LogIn v-if="!state.value"/>
          <SignUp v-else @signupSuccess="state.value = false"/>
        </div>
        <div v-else>
          <GameStart/>
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
.home {
  margin: 80px 100px;
}

.content {
  display: grid;
  grid-template-columns: 6fr 3fr;
  column-gap: 70px;
  margin-top: 20px;
}

.form {
  background-color: white;
  border-radius: 20px;
  padding: 30px;
  min-width: 350px;
}

.title {
  min-width: 350px;
}

.information {
  min-width: 350px;
}

.img-neorang {
  position: absolute;
  height: 100px;
  top: 150px;
  right: 420px;
  transition: all 0.2s ease-in-out;
}

.right {
  right: 80px;
  transform: scaleX(-1);
}

@media  (max-width: 1110px) {
  .img-neorang {
    right: 0;
    left: 590px;
  }

  .right {
    left: 930px;
  }
}
</style>
