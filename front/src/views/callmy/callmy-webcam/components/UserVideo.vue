<template>
  <div v-if="streamManager" style="position:relative">
    <ov-video :username="state.clientData" :startRecognition="startRecognition" :stream-manager="streamManager"/>
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
    startRecognition: Boolean,
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
			return JSON.parse(connection.data)
		}


    return { state }
  },

}
</script>

