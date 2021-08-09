<template>
  <div class="right-side-container">
    <div class="btn-group">
      <button @click="clickGetRole">롤카드 보기</button>
      <button>거짓말탐지</button>
      <button>도움말</button>
      <button @click="clickStartMission">동작 인식</button>
    </div>
    <Timer/>
    <Board/>s
    <Chat/>
    <Setting/>
  </div>
</template>

<script>
import Board from './board/board.vue'
import Voard from './board/vote.vue'
import Timer from './timer/timer.vue'
import Chat from './chat/chat.vue'
import Setting from './setting/setting.vue'

import { reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'rightSide',

  components: {
    Board,
    Timer,
    Chat,
    Setting,
  },

  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()

    const state = reactive({
    })

    const clickGetRole = () => {
      emit('sendGetRole')
    }

    const clickStartMission = () => {
      emit('clickStartMission')
    }

    return { state, clickGetRole, clickStartMission }
  }
}
</script>

<style scoped>
  @import url('./right-side.css');
</style>
