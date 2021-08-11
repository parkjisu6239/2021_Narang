<template>
  <div class="vote-container">
    <div v-if="state.mafiaManager.stage == 'default'"></div>
    <div v-else-if="state.mafiaManager.stage == 'day1'" >
      <div class="vote-element" v-for="name in state.mafiaManager.players" :key="name" @click="setVote">
        <input type="radio" :id="name" name="vote-day1" v-model="state.voteDay1" :value="name">
        <label :for="name">{{ name }}</label>
      </div>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'day2' && state.mafiaManager.secondVoteUsername !== state.mafiaManager.username">
      <div class="vote-element" @click="setVote">
        <input type="radio" id="찬성" name="vote-day2" v-model="state.voteDay2" :value="true">
        <label for="찬성">찬성</label>
      </div>
      <div class="vote-element" @click="setVote">
        <input type="radio" id="반대" name="vote-day2" v-model="state.voteDay2" :value="false">
        <label for="반대">반대</label>
      </div>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'night' && state.mafiaManager.myRole == 'Mafia'">
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
      } else if (state.mafiaManager.stage == 'day2') {
        if (state.voteDay2 === true) {
          store.state.root.mafiaManager.theVoted = state.mafiaManager.secondVoteUsername
        } else {
          store.state.root.mafiaManager.theVoted = null
        }
      }
    }

    return { state, setVote }
  }
}
</script>

<style scoped>
  @import url('./vote.css');
</style>
