<template>
  <div class="container">
    <canvas class="canvas" width="190" height="100" ref="myCanvas" @click="startFaceDetection"></canvas>
    <video ref="myWebCam" class="webcam" @click="startFaceDetection" autoplay/>
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
  },
  setup(props, {emit}) {
    const myWebCam = ref(null)
    const myCanvas = ref(null)
    let ctx = ''

    const state = reactive({
      detections: '',
      height: 0,
      width: 0,
    })

    const startFaceDetection = async () => {
      setInterval(async () => {
        ctx.clearRect(0, 0, 720, 320)
        state.detections = await faceapi.detectSingleFace(myWebCam.value, new faceapi.TinyFaceDetectorOptions())
        console.log(state.detections.box.topLeft)
        const box = { x: 720 - state.detections.box.topRight.x, y: state.detections.box.topRight.y, width: 100, height: 100 }
        const drawOptions = {
          label: 'Dongyun',
          lineWidth: 2
        }
        const drawBox = new faceapi.draw.DrawBox(box, drawOptions)
        drawBox.draw(myCanvas.value)
      }, 1000)
    }

    onMounted(() => {
      ctx = myCanvas.value.getContext('2d');
      faceapi.nets.tinyFaceDetector.load('/static/models')
      props.streamManager.addVideoElement(myWebCam.value)
      console.log(myWebCam.value.videoWidth)

    })

    watch(myWebCam, () => {
      state.width = String(myWebCam.value.videoWidth)
      state.height = String(myWebCam.value.videoHeight)
    })

    return { state, myWebCam, startFaceDetection, myCanvas }
  }
}
</script>


