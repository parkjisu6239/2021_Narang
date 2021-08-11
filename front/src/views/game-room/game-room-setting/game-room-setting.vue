<template>
  <div class="setting-container">
    <div class="game-btns">
      <div class="game-start" @click="gameStart" style="border-top-right-radius: 0px; border-bottom-right-radius: 0px;">Game Start!</div>
      <div class="game-select" style="border-top-left-radius: 0px; border-bottom-left-radius: 0px;">
        <img
          :src="require('@/assets/images/game-thumbnail-mafia.png')"
          @click="updateGameInfo"
          data-game="mafia"
          class="game-img">
        <img
          :src="require('@/assets/images/game-thumbnail-callmy.png')"
          @click="updateGameInfo"
          data-game="callmy"
          class="game-img">
      </div>
    </div>

    <div class="setting-btns">
      <div v-if="state.onAudio" class="setting-btn" @click="muteAudio"> <i class="el-icon-microphone"></i></div>
      <div v-if="!state.onAudio" class="setting-btn" @click="muteAudio"><i class="el-icon-turn-off-microphone"></i></div>

      <div v-if="state.onVideo" class="setting-btn" @click="muteVideo"><i class="el-icon-video-camera"></i></div>
      <div v-if="!state.onVideo" class="setting-btn" @click="muteVideo"><i class="el-icon-video-pause"></i></div>

      <div class="setting-btn" @click="openDialog"><i class="el-icon-setting"></i></div>
      <div class="setting-btn" @click="leaveRoom"><i class="el-icon-close"></i></div>
    </div>

  </div>
</template>
<style scoped>
  @import url('./game-room-setting.css');
</style>
<script>
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'GameRoomSetting',
  props: {
    roomId: {
      type: Number
    },
    room: {
      type: Object
    }
  },

  setup(props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const state =  reactive({
      onVideo : true,
      onAudio : true
    })
    const openDialog = () => {
      emit('openDialog')
    }

    const updateGameInfo = (event) => {
      // if (props.room.ownerId !== store.state.root.userId) return
      const game = event.target.dataset.game
      const roomInfo = {
        ...props.room,
        game,
      }
      console.log(roomInfo)
      store.dispatch('root/requestUpdateGameRoom', roomInfo)
        .then(res => {
          emit('changeGame')
        })
        .catch(err => {
          ElMessage({
            type: 'error',
            message: '방장만 시작할 수 있습니다.'
          })
        })
    }

    const gameStart = () => {
      emit('gameStart')
    }

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

    return {state ,openDialog, updateGameInfo, leaveRoom, muteAudio, muteVideo, gameStart}
  }
}
</script>


