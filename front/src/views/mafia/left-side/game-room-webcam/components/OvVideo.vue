<template>
  <h1 class="expression" v-if="state.detections" >{{ state.detections[0].expressions }}</h1>
  <video ref="myWebCam" @click="startExpressDetection" class="webcam" autoplay playsinline controls="false"/>
</template>

<script>
import { onMounted, reactive, ref } from 'vue'
import * as faceapi from 'face-api.js'

export default {
  name: 'OvVideo',
  props: {
    streamManager: Object,
  },
  setup(props, {emit}) {
    const myWebCam = ref(null)

    const state = reactive({
      detections: null
    })

    const startExpressDetection = async () => {
      await faceapi.nets.faceRecognitionNet.load('https://localhost:8080/static/models')
      await faceapi.nets.faceLandmark68Net.load('https://localhost:8080/static/models')
      await faceapi.nets.tinyFaceDetector.load('https://localhost:8080/static/models')
      await faceapi.nets.faceExpressionNet.load('https://localhost:8080/static/models')

      let timerId = setInterval(async () => {
        state.detections = await faceapi.detectAllFaces(myWebCam.value, new faceapi.TinyFaceDetectorOptions())
          .withFaceLandmarks()
          .withFaceExpressions()
        console.log(state.detections)
      }, 500)

      setTimeout(() => {
        clearTimeout(timerId)
      }, 5000)
    }

    onMounted(() => {
      props.streamManager.addVideoElement(myWebCam.value)
    })

    return { myWebCam, startExpressDetection, state }
  }
}
</script>
<style scoped>
  @import url('OvVideo.css');
</style>

