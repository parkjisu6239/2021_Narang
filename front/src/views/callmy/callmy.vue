<template>
  <div class="callmy-container">
    <callMyWebCam/>
  </div>
</template>
<style>
  @import url('./callmy.css');
</style>
<script>
import callMyWebCam from './callmy-webcam/callmy-webcam.vue'
export default {
  name: 'callMy',
  components: {
    callMyWebCam,
  },
}
</script>

