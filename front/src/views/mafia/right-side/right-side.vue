<template>
  <div class="right-side-container">
    <div class="btn-group">
      <button @click="clickGetRole">롤카드 보기</button>
      <button @click="clickLie">거짓말탐지</button>
      <button>도움말</button>
      <button @click="clickShowMission">미션 보기</button>
    </div>
    <Timer :timer="timer"/>
    <Board :msg="msg" :isVoteTime="isVoteTime"/>
    <Chat/>
    <Setting/>
  </div>
</template>

<script>
import Board from './board/board.vue'
import Timer from './timer/timer.vue'
import Chat from './chat/chat.vue'
import Setting from './setting/setting.vue'

import { reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'rightSide',

  props: {
    msg: {
      type: String,
    },
    isVoteTime: {
      type: Boolean,
    },
    timer: {
      tpye: Number,
    }
  },

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

    const clickShowMission = () => {
      emit('clickShowMission')
    }

const clickLie = () => {
      emit('clickLie')
    }

    return { state, clickGetRole, clickShowMission, clickLie }
  }
}
</script>

<style scoped>
  @import url('./right-side.css');
</style>
