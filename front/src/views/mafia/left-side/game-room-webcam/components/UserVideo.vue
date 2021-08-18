<template>
  <div v-if="streamManager" style="position: relative">
    <div class="citizen-video-none" v-if="state.mafiaManager.stage === 'night' && state.mafiaManager.myRole === 'Citizen'"></div>
    <ov-video v-else
      :stream-manager="streamManager"
      :isSelected="gameStart && state.clientData === state.mafiaManager.secondVoteUsername"
      :isDead="gameStart && !state.mafiaManager.players.includes(state.clientData)"
      />
  </div>
</template>
<script>
import OvVideo from './OvVideo';
import { computed, reactive } from 'vue'
import { useStore } from 'vuex'

export default {
	name: 'UserVideo',
	components: {
		OvVideo,
	},

	props: {
		streamManager: Object,
    gameStart: Boolean,
	},

  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
			clientData: computed(() => {
        const { connection } = props.streamManager.stream
        const { clientData } = JSON.parse(connection.data)
        return clientData;
      }),
      mafiaManager: computed(() => store.getters['root/mafiaManager']),
    })


    return { state }
  },

};
</script>
<style scoped>
  @import url('./uservideo.css');
</style>
