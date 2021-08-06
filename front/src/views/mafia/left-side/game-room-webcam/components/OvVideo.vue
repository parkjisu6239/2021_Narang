<template >
  <video class="webcam" autoplay playsinline controls="false"/>
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
<style scoped>
  @import url('OvVideo.css');
</style>

