<template >
  <video id="myWebcam" autoplay style="width: 98%; max-width: 540px"/>
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
