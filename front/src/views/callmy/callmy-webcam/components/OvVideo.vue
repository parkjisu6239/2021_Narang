<template>
  <video class="webcam" autoplay playsinline controls="false"/>
</template>

<script>
import { onMounted, ref } from 'vue'

export default {
  name: 'OvVideo',
  props: {
    streamManager: Object,
  },
  setup(props, {emit}) {
    const myWebCam = ref(null)

    onMounted(() => {
      props.streamManager.addVideoElement(myWebCam.value)
    })
  }
}
</script>
<style scoped>
  @import url('OvVideo.css');
</style>

