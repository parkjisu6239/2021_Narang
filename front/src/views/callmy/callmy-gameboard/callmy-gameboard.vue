<template>
  <div class="game-board-container">
    <div class="callmy-board-header">
      <div class="callmy-board-title">pick my name!</div>
    </div>
    <div class="callmy-board-vote-container">
      <div class="callmy-board-vote">
        <div
          v-for="i in 10" :key="i"
          :class="{'callmy-vote-item': true, 'callmy-vote-selected': state.selectedNickname === i}"
          @click="clickNicknameSelect(i)">
          <div class="callmy-nickname">제시어asdasdasdasadas</div>
          <div class="callmy-nickname-vote-count"><span>5</span></div>
        </div>
      </div>
    </div>
    <div class="callmy-board-input">
      <div class="callmy-board-input-container">
        <input
          type="text"
          placeholder="제시어를 추가하세요"
          :disabled="!state.isVoteTime || !state.nicknameSendchance"
          @keyup.enter="clickNicknameBtn"
          v-model="state.inputNickname">
      </div>
      <button :disabled="!state.isVoteTime || !state.nicknameSendchance" @click="clickNicknameBtn">전송</button>
    </div>
  </div>
</template>
<style>
  @import url('./callmy-gameboard.css');
</style>
<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { reactive, computed } from 'vue'

export default {
  name: 'CallMyGameBoard',

  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()

    const state = reactive({
      stompClient: null,
      isVoteTime: true,
      nicknameSendchance: true,
      inputNickname: '',
      selectedNickname: '',
    })

    const clickNicknameBtn = () => {
      if (state.inputNickname) {
        console.log('썻으니까 이제 기회 끝^^', state.inputNickname)
        state.inputNickname = ''
        state.nicknameSendchance = false
      }
    }

    const clickNicknameSelect = (i) => {
      if (!state.selectedNickname) {
        console.log(`${i} 처음 투표 함`)
      } else {
        console.log(`${state.selectedNickname} 에서 ${i}로 투표 바꿈`)
        // 소켓 보낼 때 state.selectedNickname 는 -1로 보내고
        // i 는 1로 보내고
        // 두번 보내야함(지금은 i인데 실제로는 닉네임일 예정)
      }
      state.selectedNickname = i
    }

    return { state, clickNicknameBtn, clickNicknameSelect }
  }
}
</script>


