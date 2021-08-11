<template>
  <div
    @mouseleave="hideVideoMenu"
    class="video-overlay"
    :style="{'display': state.hover}">
    <button @click="startExpressDetection" class="menu-button">거짓말 탐지기</button>
  </div>
  <video
    ref="myWebCam"
    @mouseover="showVideoMenu"
    class="webcam"
    :class="{ 'died-user' : state.mafiaManager.isAlive }"
    autoplay
    playsinline
    controls="false"/>
</template>

<script>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
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
      webCamWidth: 0,
      webCamHeight: 0,
      emotions: {
        angry: 0,
        disgusted: 0,
        fearful: 0,
        happy: 0,
        neutral: 0,
        sad: 0,
        surprised: 0,
      },
      mafiaManager: computed(() => store.getters['root/mafiaManager']),
    })

    const startExpressDetection = async () => {
      await faceapi.nets.faceRecognitionNet.load('/static/models')
      await faceapi.nets.tinyFaceDetector.load('/static/models')
      await faceapi.nets.faceExpressionNet.load('/static/models')
      ElMessage({
        type: 'success',
        message: '거짓말 탐지기가 작동 중입니다.'
      })
      let timerId = setInterval(async () => {
        state.detections = await faceapi.detectAllFaces(myWebCam.value, new faceapi.TinyFaceDetectorOptions())
          .withFaceExpressions()

        let maxVal = 0
        let maxEmotion = ''
        for (let emotion in state.detections[0].expressions) {
          if (state.detections[0].expressions[emotion] > maxVal) {
            maxVal = state.detections[0].expressions[emotion]
            maxEmotion = emotion
          }
        }

        state.emotions[maxEmotion]++
      }, 500)

      setTimeout(() => {
        let maxVal = 0
        let maxEmotion = ''
        for (let emotion in state.emotions) {
          if (state.emotions[emotion] > maxVal) {
            maxVal = state.emotions[emotion]
            maxEmotion = emotion
          }
        }

        state.emotions = {
          angry: 0,
          disgusted: 0,
          fearful: 0,
          happy: 0,
          neutral: 0,
          sad: 0,
          surprised: 0,
        }

        ElMessage({
          type: 'success',
          message: `${maxEmotion} 현재 감정 상태입니다.`
        })

        clearTimeout(timerId)
      }, 5000)
    }

    const showVideoMenu = () => {
      console.log(myWebCam.value.videoWidth, myWebCam.value.videoHeight)
      state.webCamWidth= myWebCam.value.videoWidth
      state.webCamHeight = myWebCam.value.videoHeight
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

