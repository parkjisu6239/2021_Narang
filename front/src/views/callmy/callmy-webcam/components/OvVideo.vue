<template>
  <div class="container">
    <canvas class="canvas" width="720" height="400" ref="myCanvas"></canvas>
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
    startDetection: Boolean,
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
      nickName: computed(() => {
        let target
        store.state.root.callmyManager.nowPlayUsers.forEach(user => {
          if (user.username === username) target = username
        })
        return target
      }),
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

        if (state.detections) {
          const nickname = props.username === state.myUserName ? '???' : state.nickName
          const text = [
            nickname,
          ]

          console.log(state.myUserName, props.username)
          const anchor = {
            x: state.myUserName === props.username ? 600 + text[0].length * 20 - state.detections.box.topLeft.x : state.detections.box.topRight.x - text[0].length * 20,
            y: state.detections.box.topLeft.y - 30,
          }

          const drawOptions = {
            anchorPosition: state.myUserName === props.username ? 'TOP_RIGHT' : 'TOP_LEFT',
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
      ctx = myCanvas.value.getContext('2d')
      ctx.clearRect(0, 0, 720, 400)
      faceapi.nets.tinyFaceDetector.load('/static/models')
      props.streamManager.addVideoElement(myWebCam.value)
    })


    watch(() => props.startDetection, () => {
      if (props.startDetection && props.username !== state.myUserName) startFaceDetection()
    })


    return { state, myWebCam, startFaceDetection, myCanvas }
  }
}
</script>


