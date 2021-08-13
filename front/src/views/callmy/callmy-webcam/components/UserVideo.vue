<template>
  <div v-if="streamManager" style="position:relative">
    <ov-video :stream-manager="streamManager"/>
    <div class="nameTag"><p>{{ state.clientData }}</p></div>
  </div>
</template>
<style scoped>
  @import url('./uservideo.css');
</style>
<script>
import OvVideo from './OvVideo.vue'
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
        const { clientData } = getConnectionData()
        return clientData
      }),
    })

    const getConnectionData = () => {
			const { connection } = props.streamManager.stream;
      console.log(props.streamManager, '여기가 스트림매니저')
      console.log(connection.data, '여기가 connection data')
			return JSON.parse(connection.data)
		}


    return { state }
  },

}
</script>

