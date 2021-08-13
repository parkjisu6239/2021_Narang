<template>
  <div class="setting-btns">
    <div v-if="state.mafiaManager.onAudio" class="mafia-setting-btn" @click="muteAudio"> <i class="el-icon-microphone"></i></div>
    <div v-if="!state.mafiaManager.onAudio" class="mafia-setting-btn" @click="muteAudio"><i class="el-icon-turn-off-microphone"></i></div>

    <div v-if="state.onVideo" class="mafia-setting-btn" @click="muteVideo"><i class="el-icon-video-camera"></i></div>
    <div v-if="!state.onVideo" class="mafia-setting-btn" @click="muteVideo"><i class="el-icon-video-pause"></i></div>

    <div class="mafia-setting-btn" @click="leaveRoom"><i class="el-icon-close"></i></div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
export default {
  name: 'setting',
  setup(props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const state =  reactive({
      onVideo : true,
      mafiaManager: computed(() => store.getters['root/mafiaManager']),

    })
    const leaveRoom = () => {
      router.push({
        name: 'waitingRoom'
      })
    }

    const muteAudio = () => {
      if(!state.mafiaManager.isAlive) {
        return;
      }
        store.state.root.mafiaManager.onAudio = !store.state.root.mafiaManager.onAudio
        console.log(store.state.root.mafiaManager.onAudio)
        store.state.root.publisher.publishAudio(store.state.root.mafiaManager.onAudio);
    }
    const muteVideo = () => {
        state.onVideo = !state.onVideo;
        console.log(state.onVideo)
        store.state.root.onVideo = state.onVideo
        store.state.root.publisher.publishVideo(state.onVideo);
    }

    return {state, leaveRoom, muteAudio, muteVideo}
  }
}
</script>

<style scoped>
  @import url('./setting.css');
</style>
