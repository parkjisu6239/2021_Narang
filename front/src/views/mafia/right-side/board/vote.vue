<template>
  <div>투표시간입니다</div>
  <div class="vote-container">
    <div v-if="state.mafiaManager.stage == 'default'"></div>
    <div v-else-if="state.mafiaManager.stage == 'day1'" >
      <div>선택됨 : {{ state.voteDay1 }}</div>
      <div class="vote-element" v-for="name in state.mafiaManager.players" :key="name" @click="setVote">
        <input type="radio" :id="name" name="vote-day1" v-model="state.voteDay1" :value="name">
        <label :for="name">{{ name }}</label>
      </div>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'day2'">
      <div>선택됨 : {{ state.voteDay2 }}</div>
      <div class="vote-element" @click="setVote">
        <input type="radio" id="찬성" name="vote-day2" v-model="state.voteDay2" value="true">
        <label for="찬성">찬성</label>
      </div>
      <div class="vote-element" @click="setVote">
        <input type="radio" id="반대" name="vote-day2" v-model="state.voteDay2" value="false">
        <label for="반대">반대</label>
      </div>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'night' && state.mafiaManager.myRole == 'Mafia'">
      <div>선택됨 : {{ state.voteNight }}</div>
      <div class="vote-element" v-for="name in state.mafiaManager.players" :key="name" @click="setVote">
        <input type="radio" :id="name" name="vote-night" v-model="state.voteNight" :value="name">
        <label :for="name">{{ name }}</label>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, reactive } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'vote',

  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
      mafiaManager: computed(() => store.getters['root/mafiaManager']),
      voteDay1: '',
      voteDay2: false,
      voteNight: '',
    })

    const setVote = () => {
      if (state.mafiaManager.stage == 'day1') {
        store.state.root.mafiaManager.theVoted = state.voteDay1
      } else if (state.mafiaManager.stage == 'night') {
        store.state.root.mafiaManager.theVoted = state.voteNight
      }
      store.state.root.mafiaManager.isAgree = state.voteDay2
    }

    return { state, setVote }
  }
}
</script>

<style scoped>
  @import url('./vote.css');
</style>
