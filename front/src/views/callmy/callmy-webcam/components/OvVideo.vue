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
import { onMounted, reactive, ref, watch } from 'vue'
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
    let ctx = ''

    const state = reactive({
      detections: '',
      height: '',
      width: '',
    })

    const startFaceDetection = async () => {
      setInterval(async () => {
        ctx.clearRect(0, 0, 720, 400)
        state.detections = await faceapi.detectSingleFace(myWebCam.value, new faceapi.TinyFaceDetectorOptions())
        const text = [
          props.username
        ]

        const anchor = {
          x: 650 - state.detections.box.topLeft.x,
          y: state.detections.box.topLeft.y,
        }

        const drawOptions = {
          anchorPosition: 'TOP_RIGHT',
          backgroundColor: 'rgba(255, 255, 255, 0.5)',
          fontSize: 30,
          fontColor: 'black',
        }

        const drawText = new faceapi.draw.DrawTextField(text, anchor, drawOptions)
        drawText.draw(myCanvas.value)
      }, 1000)
    }

    onMounted(() => {
      ctx = myCanvas.value.getContext('2d');
      faceapi.nets.tinyFaceDetector.load('/static/models')
      props.streamManager.addVideoElement(myWebCam.value)
    })

    watch(() => props.startRecognition, () => {
      if(startRecognition) startFaceDetection()
    })

    return { state, myWebCam, startFaceDetection, myCanvas }
  }
}
</script>


