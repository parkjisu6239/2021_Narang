<template>
  <h1>{{ streamManager }}</h1>
  <div v-if="streamManager" style="position: relative">
    <div class="citizen-video-none" v-if="state.mafiaManager.stage === 'night' && state.mafiaManager.myRole === 'Citizen'"></div>
    <ov-video v-else :stream-manager="streamManager"/>
  </div>
</template>
<style scoped>
  @import url('./uservideo.css');
</style>
<script>
import OvVideo from './OvVideo.css';
import { computed, reactive } from 'vue'
import { useStore } from 'vuex'

export default {
	name: 'UserVideo',
	components: {
		OvVideo,
	},
	props: {
		streamManager: Object,
	},
  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
			clientData: computed(() => {
        const { clientData } = getConnectionData();
        return clientData;
      }),
      mafiaManager: computed(() => store.getters['root/mafiaManager']),
    })

    const getConnectionData = () => {
			const { connection } = props.streamManager.stream;
      console.log(connection.data)
			return JSON.parse(connection.data);
		}


    return { state, getConnectionData }
  },

};
</script>

