<template >
  <video autoplay style="width : 100%; height : 100%"/>
</template>

<script>
export default {
  name: 'OvVideo',
  props: {
    	streamManager: Object,
  },
  mounted() {
    this.streamManager.addVideoElement(this.$el);
  }
}
</script>
