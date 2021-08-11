<template>
  <div v-if="streamManager" style="position:relative">
    <ov-video :stream-manager="streamManager"/>
    <div class="nameTag"><p>{{ state.clientData }}</p></div>
  </div>
</template>
<style scoped>
div > p {
  margin: 0;
}

.nameTag {
  position: absolute;
  top: 10%;
  left: 5%;
}
</style>
<script>

import OvVideo from './OvVideo';
import { computed, reactive } from 'vue'
export default {
	name: 'UserVideo',
	components: {
		OvVideo,
	},
	props: {
		streamManager: Object,
	},
  setup(props, { emit }) {
    const state = reactive({
			clientData: computed(() => {
        const { clientData } = getConnectionData();
			  return clientData;
      }),
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
