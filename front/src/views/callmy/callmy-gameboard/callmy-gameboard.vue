<template>
  <div class="game-board-container">
    <div class="callmy-board-header">
      <div v-if="isVoteTime" class="callmy-board-title" @click="sendVoteFinish">pick {{ state.targetName }}'s name!</div>
      <div v-else class="callmy-board-title" @click="sendVoteFinish">Now playing!</div>
    </div>
    <div class="callmy-board-vote-container">
      <div class="callmy-board-vote" v-if="state.userId !== state.targetId">
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
          :disabled="!isVoteTime || !state.nicknameSendchance || state.userId === state.targetId"
          @keyup.enter="clickNicknameBtn"
          v-model="state.inputNickname">
      </div>
      <button
        :disabled="!isVoteTime || !state.nicknameSendchance || state.userId === state.targetId"
        @click="clickNicknameBtn">전송</button>
    </div>
  </div>
</template>
<style>
  @import url('./callmy-gameboard.css');
</style>
<script>
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { reactive, computed, watch } from 'vue'

export default {
  name: 'CallMyGameBoard',

  props: {
    nicknameList: Object,
    order: Number,
    isVoteTime: Boolean,
  },

  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()


    const state = reactive({
      nicknameSendchance: true,
      inputNickname: computed(() => store.state.root.callmyManager.defaultNickname),
      selectedNickname: '',
      userId: computed(() => store.state.root.userId),
      callmyManager: computed(() => store.getters['root/callmyManager']),
      targetId: computed(() => store.state.root.callmyManager.nowPlayUsers.length ? store.state.root.callmyManager.nowPlayUsers[props.order].userId : 0),
      targetName: computed(() => store.state.root.callmyManager.nowPlayUsers.length ? store.state.root.callmyManager.nowPlayUsers[props.order].username : ''),
    })


    const clickNicknameBtn = () => {
      if (state.inputNickname) {
        const message = {
          userId: state.userId,
          targetId: state.targetId,
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
      if (state.selectedNickname === nickname) {
        return
      }
      if (!state.selectedNickname) {
        console.log(`${nickname} 처음 투표 함`)
        const message = {
          userId: state.userId,
          targetId: state.targetId,
          content: nickname,
          vote: 1,
          isFinished: false,
        }
        emit('sendVote', message)
      } else {
        console.log(`${state.selectedNickname} 에서 ${nickname}로 투표 바꿈`)
        const messageRemoveVote = {
          userId: state.userId,
          targetId: state.targetId,
          content: state.selectedNickname, // 기존에 선택했던 것
          vote: -1, // 취소
          isFinished: false,
        }
        emit('sendVote', messageRemoveVote)
        const messageAddVote = {
          userId: state.userId,
          targetId: state.targetId,
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


    watch(() => props.order, () => {
      state.nicknameSendchance = true
      state.inputNickname = ''
      state.selectedNickname = ''
    })


    watch(() => store.state.root.callmyManager, () => {
      state.targetId = store.state.root.callmyManager.nowPlayUsers.length ? store.state.root.callmyManager.nowPlayUsers[props.order].userId : 0
      state.targetName = store.state.root.callmyManager.nowPlayUsers.length ? store.state.root.callmyManager.nowPlayUsers[props.order].username : ''
    })


    return { state, clickNicknameBtn, clickNicknameSelect, sendVoteFinish }
  }
}
</script>


