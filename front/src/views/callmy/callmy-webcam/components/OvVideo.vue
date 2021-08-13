<template>
  <video ref="myWebCam" autoplay/>
</template>
<style>
  @import url('./OvVideo.css');
</style>
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


