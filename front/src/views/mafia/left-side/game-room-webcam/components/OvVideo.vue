<template>
  <div
    @mouseleave="hideVideoMenu"
    class="video-overlay"
    :style="{'display': state.hover}">
    <button @click="startExpressDetection" class="menu-button">거짓말 탐지기</button>
  </div>
  <video id="myWebcam" ref="myWebCam" @mouseover="showVideoMenu" class="webcam" autoplay playsinline controls="false"/>
</template>

<script>
import { onMounted, reactive, ref, computed } from 'vue'
import * as faceapi from 'face-api.js'

export default {
  name: 'OvVideo',
  props: {
    streamManager: Object,
  },
  setup(props, {emit}) {
    const myWebCam = ref(null)

    const state = reactive({
      detections: null,
      myEmotion: '',
      hover: 'none',
      overlayWidth: 0,
      overlayHeight: 0,
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

    const showVideoMenu = () => {
      state.hover = 'block'
    }

    const hideVideoMenu = () => {
      state.hover = 'none'
    }

    onMounted(() => {
      props.streamManager.addVideoElement(myWebCam.value)
    })

    return { state, myWebCam, startExpressDetection, showVideoMenu, hideVideoMenu }
  }
}
</script>
<style scoped>
  @import url('OvVideo.css');
</style>

