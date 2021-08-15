<template>
  <div class="game-board-container">
    <div class="callmy-board-header">
      <div class="callmy-board-title" @click="sendVoteFinish">pick my name!</div>
    </div>
    <div class="callmy-board-vote-container">
      <div class="callmy-board-vote">
        <div
          v-for="nickname in Object.keys(nicknameList)" :key="nickname"
          :class="{'callmy-vote-item': true, 'callmy-vote-selected': state.selectedNickname === nickname}"
          @click="clickNicknameSelect(nickname)">
          <div class="callmy-nickname">{{ nickname }}</div>
          <div class="callmy-nickname-vote-count"><span>{{nicknameList[nickname]}}</span></div>
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
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { reactive, computed } from 'vue'

export default {
  name: 'CallMyGameBoard',

  props: {
    nicknameList: Object,
  },

  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()

    const state = reactive({
      isVoteTime: true,
      nicknameSendchance: true,
      inputNickname: '',
      selectedNickname: '',
      userId: computed(() => store.state.root.userId),
    })

    const clickNicknameBtn = () => {
      if (state.inputNickname) {

        const message = {
          userId: state.userId,
          targetId: 45, // 테스트 용
          content: state.inputNickname,
          vote: 0,
          isFinished: false,
        }

        emit('sendVote', message)

        console.log('썻으니까 이제 기회 끝^^', state.inputNickname)
        state.inputNickname = ''
        state.nicknameSendchance = false
      }
    }

    const clickNicknameSelect = (nickname) => {
      if (!state.selectedNickname) {
        console.log(`${nickname} 처음 투표 함`)
        const message = {
          userId: state.userId,
          targetId: 45, // 테스트 용
          content: nickname,
          vote: 1,
          isFinished: false,
        }
        emit('sendVote', message)
      } else {
        console.log(`${state.selectedNickname} 에서 ${nickname}로 투표 바꿈`)

        const messageRemoveVote = {
          userId: state.userId,
          targetId: 45, // 테스트 용
          content: state.selectedNickname, // 기존에 선택했던 것
          vote: -1, // 취소
          isFinished: false,
        }
        emit('sendVote', messageRemoveVote)

        const messageAddVote = {
          userId: state.userId,
          targetId: 45, // 테스트 용
          content: nickname, // 새로 선택한 것
          vote: 1, // 추가
          isFinished: false,
        }
        emit('sendVote', messageAddVote)
      }
      state.selectedNickname = nickname
    }

    const sendVoteFinish = () => {
      const message = {
          userId: state.userId,
          targetId: state.userId,
          content: '',
          vote: 0,
          isFinished: true,
        }

        emit('sendVote', message)
    }

    return { state, clickNicknameBtn, clickNicknameSelect, sendVoteFinish }
  }
}
</script>


