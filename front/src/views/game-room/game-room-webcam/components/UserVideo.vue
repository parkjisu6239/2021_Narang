<template>
<div v-if="streamManager" style="position:relative">
	<ov-video :stream-manager="streamManager"/>
	<div class="nameTag"><p>{{ clientData }}</p></div>
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

export default {
	name: 'UserVideo',
	components: {
		OvVideo,
	},

	props: {
		streamManager: Object,
	},

	computed: {
		clientData () {
			const { clientData } = this.getConnectionData();
			return clientData;
		},
	},

	methods: {
		getConnectionData () {
			const { connection } = this.streamManager.stream;
			return JSON.parse(connection.data);
		},
	},
};
</script>
