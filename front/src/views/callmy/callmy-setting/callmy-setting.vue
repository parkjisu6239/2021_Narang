<template>
  <div class="callmy-setting-container">
    <div v-if="state.onAudio" class="callmy-setting-btn" @click="muteAudio"><i class="el-icon-microphone"></i></div>
    <div v-if="!state.onAudio" class="callmy-setting-btn" @click="muteAudio"><i class="el-icon-turn-off-microphone"></i></div>

    <div v-if="state.onVideo" class="callmy-setting-btn" @click="muteVideo"><i class="el-icon-video-camera"></i></div>
    <div v-if="!state.onVideo" class="callmy-setting-btn" @click="muteVideo"><i class="el-icon-video-pause"></i></div>

    <div class="callmy-setting-btn" @click="leaveRoom"><i class="el-icon-close"></i></div>
  </div>
</template>

<script>
import { reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'callmySetting',

  setup(props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()

    const state =  reactive({
      onVideo : true,
      onAudio : true,
    })

    const leaveRoom = () => {
      router.push({
        name: 'waitingRoom'
      })
    }

    const muteAudio = () => {
        state.onAudio = !state.onAudio;
        console.log(state.onAudio)
        store.state.root.publisher.publishAudio(state.onAudio);
    }

    const muteVideo = () => {
        state.onVideo = !state.onVideo;
        console.log(state.onVideo)
        store.state.root.publisher.publishVideo(state.onVideo);
    }

    return {state, leaveRoom, muteAudio, muteVideo}
  }
}
</script>

<style>
  @import url('./callmy-setting.css');
</style>


