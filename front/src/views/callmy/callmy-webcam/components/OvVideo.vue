<template>
  <div class="container">
    <canvas class="canvas" width="720" height="400" ref="myCanvas" @click="startFaceDetection"></canvas>
    <video ref="myWebCam" class="webcam" autoplay/>
  </div>
</template>
<style>
  @import url('./OvVideo.css');
</style>
<script>
import { onMounted, computed, reactive, ref, watch } from 'vue'
import { useStore } from 'vuex'
import * as faceapi from 'face-api.js'

export default {
  name: 'OvVideo',
  props: {
    streamManager: Object,
    startRecognition: Boolean,
    username: String,
  },
  setup(props, {emit}) {
    const myWebCam = ref(null)
    const myCanvas = ref(null)
    const store = useStore()

    let ctx = ''

    const state = reactive({
      detections: '',
      height: '',
      width: '',
      myUserName: computed(() => store.state.root.username),
      timeId: '',
    })

    const startFaceDetection = async () => {
      if (state.timeId) {
        clearInterval(state.timeId)
        ctx.clearRect(0, 0, 720, 400)
        state.timeId = ''
        return
      }

      state.timeId = setInterval(async () => {
        ctx.clearRect(0, 0, 720, 400)
        state.detections = await faceapi.detectSingleFace(myWebCam.value, new faceapi.TinyFaceDetectorOptions())

        if (state.detections.box) {
          const text = [
            '아이유',
          ]

          console.log(state.myUserName, props.username)
          const anchor = {
            x: state.myUserName === props.username ? 660 - state.detections.box.topLeft.x : state.detections.box.topLeft.x,
            y: state.detections.box.topLeft.y - 30,
          }

          const drawOptions = {
            anchorPosition: 'TOP_RIGHT',
            backgroundColor: 'rgba(255, 255, 255, 1)',
            fontSize: 40,
            fontColor: 'black',
            padding: 15,
          }

          const drawText = new faceapi.draw.DrawTextField(text, anchor, drawOptions)
          drawText.draw(myCanvas.value)
        }

      }, 800)
    }

    onMounted(() => {
      ctx = myCanvas.value.getContext('2d');
      faceapi.nets.tinyFaceDetector.load('/static/models')
      props.streamManager.addVideoElement(myWebCam.value)
    })

    watch(() => props.startRecognition, () => {
      if(startRecognition && props.username !== myUserName) startFaceDetection()
    })

    return { state, myWebCam, startFaceDetection, myCanvas }
  }
}
</script>


