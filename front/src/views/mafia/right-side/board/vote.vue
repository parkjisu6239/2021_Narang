<template>
  <div>
    <div>투표</div>
    <div v-if="mafiaManager.stage == 'default'"></div>
    <div v-else-if="mafiaManager.stage == 'day1'" >
      <div v-for="player in mafiaManager.players" :key="player" @click="voteUser(player.user.username)">{{player.user.username}}</div>
    </div>
    <div v-else-if="mafiaManager.stage == 'day2'">
      <button @click="voteAgree(true)">찬성</button>
      <button @click="voteAgree(false)">반대</button>
    </div>
    <div v-else-if="mafiaManager.stage == 'night' && mafiaManager.myRole == 'Mafia'">
      <div v-for="player in mafiaManager.players" :key="player" @click="voteUser(player.user.username)">{{player.user.username}}</div>
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
      store.mafiaManager.theVoted = theVoted
    }

    // 최종 반론 30초동안 찬반 투표 진행해야함 30초 뒤에 socket send함
    const voteAgree = (isAgree) => {
      store.mafiaManager.isAgree = isAgree
    }
    return { state }
  }
}
</script>

<style>

</style>
