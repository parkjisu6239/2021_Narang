<template>
  <div style="position:relative">
    <ov-video :stream-manager="streamManager"/>
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
		streamManager: {
      type: Object,
    },
	},
  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
			clientData: computed(() => {
        const { clientData } = getConnectionData();
        return clientData;
      }),
    })

    const getConnectionData = () => {
			const { connection } = props.streamManager.stream;
      console.log(connection.data, '여기가 connection data')
			return JSON.parse(connection.data);
		}


    return { state }
  },

};
</script>

