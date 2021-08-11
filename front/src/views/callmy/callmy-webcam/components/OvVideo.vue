<template>
  <video ref="myWebCam" class="webcam" autoplay playsinline controls="false"/>
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

    return { myWebCam }
  }
}
</script>
<style scoped>
  @import url('OvVideo.css');
</style>

