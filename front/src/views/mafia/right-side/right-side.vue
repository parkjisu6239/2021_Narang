<template>
  <div class="right-side-container">
    <div class="btn-group">
      <button @click="clickGetRole">롤카드 보기</button>
      <button>거짓말탐지</button>
      <button>도움말</button>
    </div>
    <Board/>
    <Chat/>
  </div>
</template>

<script>
import Board from './board/board.vue'
import Chat from './chat/chat.vue'

import { reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'rightSide',

  components: {
    Board,
    Chat,
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

    return { state, clickGetRole }
  }
}
</script>

<style scoped>
  @import url('./right-side.css');
</style>
