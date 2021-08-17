<template>
  <div>
    <div v-html="state.message"></div>
    <div v-if="voteStatus && voteStatus.length" class="mafia-vote-status-list">
      <div class="mafia-vote-status-item" v-for="player in state.players" :key="player">
        <div class="mafia-vote-status-name">{{ player }}</div>
        <div class="mafia-vote-status-count"><span>{{ voteStatus[player] }}</span></div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, computed } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'notice',

  props: {
    msg: {
      type: String,
    },
    voteStatus: {
      type: Object
    }
  },

  setup (props, { emit }) {
    const store = useStore()

    const state = reactive({
      message: computed(() => props.msg.replace("\n", "<br />")),
      players: computed(() => store.state.root.mafiaManager.players)
    })

    return { state }
  }

}
</script>

<style>
.mafia-vote-status-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.mafia-vote-status-item {
  display: flex;
  align-items: center;
  border-radius: 15px;
  padding: 3px 10px;
  width: 80%;
  gap: 20px
}

.mafia-vote-status-count {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgb(202, 46, 77);
  color: white;
  border: 50%;
  width: 18px;
  height: 18px;
}

</style>
