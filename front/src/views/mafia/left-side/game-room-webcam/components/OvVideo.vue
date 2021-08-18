<template>
  <video
    ref="myWebCam"
    @mouseover="showVideoMenu"
    :class="{'webcam': true, 'selected-border': isSelected, 'died-user': isDead}"
    autoplay
    playsinline
    controls="false"/>
  <div v-if="state.mafiaManager.isLierItemActivate" class="video-overlay">
    <i @click="startExpressDetection" class="menu-button el-icon-search"></i>
  </div>
  <div v-if="state.show" class="lie-detection">
    <span v-if="state.lie" class="lie">거짓말!</span>
    <span v-else class="true">진실.</span>
  </div>
</template>

<script>
import { onMounted, computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import * as faceapi from 'face-api.js'

export default {
  name: 'OvVideo',
  props: {
    streamManager: Object,
    isSelected: Boolean,
    isDead: Boolean,
  },
  setup(props, {emit}) {
    const store = useStore()
    const myWebCam = ref(null)

    const state = reactive({
      detections: null,
      myEmotion: '',
      webCamWidth: 0,
      webCamHeight: 0,
      hide: false,
      lie: false,
      timerId: 0,
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
      store.state.root.mafiaManager.lierItem = false
      store.state.root.mafiaManager.isLierItemActivate = false

      ElMessage({
        type: 'success',
        message: '거짓말 탐지기가 작동 중입니다.'
      })

      state.timerId = setInterval(async () => {
        state.detections = await faceapi.detectSingleFace(myWebCam.value, new faceapi.TinyFaceDetectorOptions())
          .withFaceExpressions()

        console.log(state.detections.expressions)
        if (state.detections) {
          let maxVal = 0
          let maxEmotion = ''
          for (let emotion in state.detections.expressions) {
            if (state.detections.expressions[emotion] > maxVal) {
              maxVal = state.detections.expressions[emotion]
              maxEmotion = emotion
            }
          }
          state.emotions[maxEmotion]++
        }


      }, 500)

      setTimeout(() => {
        let emotionNum = 0
        state.lie = false
        console.log(state.emotions)
        for (let emotion in state.emotions) {
          console.log(emotion)
          if (state.emotions[emotion]) emotionNum++
        }

        console.log(state.emotions)
        state.emotions = {
          angry: 0,
          disgusted: 0,
          fearful: 0,
          happy: 0,
          neutral: 0,
          sad: 0,
          surprised: 0,
        }

        if (emotionNum >= 3) state.lie = true

        state.show = true
        console.log('끝내자')
        clearInterval(state.timerId)
      }, 5000)
    }

    onMounted(async () => {
      props.streamManager.addVideoElement(myWebCam.value)
      await faceapi.nets.tinyFaceDetector.load('/static/models')
      await faceapi.nets.faceExpressionNet.load('/static/models')
    })

    watch(() => state.show, () => {
      if (state.show) {
        setTimeout(() => {
          state.show = false
        }, 3000)
      }
    })

    return { state, myWebCam, startExpressDetection }
  }
}
</script>
<style scoped>
  @import url('OvVideo.css');
</style>

