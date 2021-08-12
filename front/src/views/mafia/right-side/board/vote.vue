<template>
  <div class="vote-container">
    <div v-if="state.mafiaManager.stage == 'default'"></div>
    <div v-else-if="state.mafiaManager.stage == 'day1'" >
      <div v-for="name in state.mafiaManager.players"
        :key="name"
        :class="{'default-username': true, 'is-selected': name === state.victim }"
        @click="clickVictim(name)">
        {{ name }}
      </div>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'day2' && state.mafiaManager.secondVoteUsername !== state.mafiaManager.username">
      <div :class="{'default-username': true, 'is-selected': state.tOrF === true }" @click="clickTrueOfFalse(true)">찬성</div>
      <div :class="{'default-username': true, 'is-selected': state.tOrF === false }" @click="clickTrueOfFalse(false)">반대</div>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'night' && state.mafiaManager.myRole == 'Mafia' && state.mafiaManager.canMafiaVote">
      <div v-for="name in state.mafiaManager.players"
        :key="name"
        :class="{'default-username': true, 'is-selected': name === state.victim }"
        @click="clickVictim(name)">
        {{ name }}
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
      victim: store.state.root.mafiaManager.username,
      tOrF: false,
    })

    const clickVictim = (name) => {
      state.victim = name
      store.state.root.mafiaManager.theVoted = state.victim
    }

    const clickTrueOfFalse = (value) => {
      state.tOrF = value
      if (value === true) {
        store.state.root.mafiaManager.theVoted = state.mafiaManager.secondVoteUsername
      } else {
        store.state.root.mafiaManager.theVoted = state.mafiaManager.username
      }
    }

    return { state, clickVictim, clickTrueOfFalse }
  }
}
</script>

<style scoped>
  @import url('./vote.css');
</style>
