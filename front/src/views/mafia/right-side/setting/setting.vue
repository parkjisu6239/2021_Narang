<template>
  <div class="setting-btns">
      <div v-if="state.onAudio" class="setting-btn" @click="muteAudio"> <i class="el-icon-microphone"></i></div>
      <div v-if="!state.onAudio" class="setting-btn" @click="muteAudio"><i class="el-icon-turn-off-microphone"></i></div>

      <div v-if="state.onVideo" class="setting-btn" @click="muteVideo"><i class="el-icon-video-camera"></i></div>
      <div v-if="!state.onVideo" class="setting-btn" @click="muteVideo"><i class="el-icon-video-pause"></i></div>

      <div class="setting-btn" @click="leaveRoom"><i class="el-icon-close"></i></div>
    </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
export default {
  name: 'setting',
  setup(props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const state =  reactive({
      onVideo : true,
      onAudio : true
    })
    const leaveRoom = () => {
      router.push({
        name: 'waitingRoom'
      })
    }

    const muteAudio = () => {
        state.onAudio = !state.onAudio;
        console.log(state.onAudio)
        store.onAudio = state.onAudio
        store.publisher.publishAudio(state.onAudio);
    }
    const muteVideo = () => {
        state.onVideo = !state.onVideo;
        console.log(state.onVideo)
        store.onVideo = state.onVideo
        store.publisher.publishVideo(state.onVideo);
    }

    return {state, leaveRoom, muteAudio, muteVideo}
  }
}
</script>

<style>
 @import url('./setting.css');
</style>
