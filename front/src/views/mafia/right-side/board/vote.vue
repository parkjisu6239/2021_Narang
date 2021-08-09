<template>
  <div>
    <div>투표</div>
    <div v-if="state.mafiaManager.stage == 'default'"></div>
    <div v-else-if="state.mafiaManager.stage == 'day1'" >
      <div v-for="name in state.mafiaManager.players" :key="name" @click="voteUser(name)">{{name}}</div>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'day2'">
      <button @click="voteAgree(true)">찬성</button>
      <button @click="voteAgree(false)">반대</button>
    </div>
    <div v-else-if="state.mafiaManager.stage == 'night' && state.mafiaManager.myRole == 'Mafia'">
      <div v-for="name in state.mafiaManager.players" :key="name" @click="voteUser(name)">{{name}}</div>
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
    })

    const voteUser = (theVoted) => {
      console.log(theVoted)
      store.state.root.mafiaManager.theVoted = theVoted
    }

    // 최종 반론 30초동안 찬반 투표 진행해야함 30초 뒤에 socket send함
    const voteAgree = (isAgree) => {
      store.state.root.mafiaManager.isAgree = isAgree
    }
    return { state, voteUser}
  }
}
</script>

<style>

</style>
