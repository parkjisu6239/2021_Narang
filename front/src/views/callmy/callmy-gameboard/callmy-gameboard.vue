<template>
  <div class="game-board-container">
    <div class="callmy-board-header">
      <div v-if="isVoteTime" class="callmy-board-title">{{ state.targetName }}'s name!</div>
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
      <div class="callmy-board-input-container" v-if="state.nicknameSendchance">
        <input
          type="text"
          :placeholder="defaultNickname"
          :disabled="!isVoteTime || !state.nicknameSendchance || state.userId === state.targetId"
          @keyup.enter="clickNicknameBtn"
          v-model="state.inputNickname">
      </div>
      <button
        v-if="state.nicknameSendchance"
        :disabled="!isVoteTime || !state.nicknameSendchance || state.userId === state.targetId"
        @click="clickNicknameBtn">전송</button>
      <div
        :class="{'callmy-nickname-ok': true, 'callmy-nickname-ok-clicked': state.isclickNicknameOk}"
        v-if="!state.nicknameSendchance"
        @click="sendVoteFinish">
        <div>투표 완료</div>
      </div>
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
    defaultNickname: String,
  },

  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()


    const state = reactive({
      nicknameSendchance: true,
      isclickNicknameOk: false,
      inputNickname: '',
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
        console.log('썻으니까 이제 기회 끝^^', state.inputNickname)
        emit('sendVote', message) // 전송
      } else {
        const message = {
          userId: state.userId,
          targetId: state.targetId,
          content: props.defaultNickname,
          vote: 0,
          isFinished: false,
        }
        console.log('썻으니까 이제 기회 끝^^', props.defaultNickname)
        emit('sendVote', message) // 전송
      }
      state.inputNickname = '' // 비우기
      store.state.root.callmyManager.defaultNickname = '' // 비우기
      state.nicknameSendchance = false // 기회 다 씀
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
      if (state.isclickNicknameOk) {
        return
      }

      const message = {
          userId: state.userId,
          targetId: state.targetId,
          content: '',
          vote: 0,
          isFinished: true,
        }
      emit('sendVote', message)
      state.isclickNicknameOk = true
    }


    watch(() => props.order, () => {
      state.nicknameSendchance = true
      state.selectedNickname = ''
      state.inputNickname = ''
      state.isclickNicknameOk = false
    })


    watch(() => store.state.root.callmyManager, () => {
      state.targetId = store.state.root.callmyManager.nowPlayUsers.length ? store.state.root.callmyManager.nowPlayUsers[props.order].userId : 0
      state.targetName = store.state.root.callmyManager.nowPlayUsers.length ? store.state.root.callmyManager.nowPlayUsers[props.order].username : ''
    })

    return { state, clickNicknameBtn, clickNicknameSelect, sendVoteFinish }
  }
}
</script>


