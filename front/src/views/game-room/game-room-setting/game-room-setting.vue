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
      <div class="setting-btn"><i class="el-icon-microphone"></i></div>
      <div class="setting-btn"><i class="el-icon-video-camera"></i></div>
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
    const route = useRouter()

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
            message: '에러'
          })
        })
    }

    const gameStart = () => {
      emit('gameStart')
    }

    const leaveRoom = () => {
      store.dispatch('root/requestLeaveGameRoom', { roomId: Number(props.roomId) })
        .then(res => {
          ElMessage({
            type: 'success',
            message: '방에서 퇴장하셨습니다.'
          })
          emit('leaveRoom')
          route.push({
            name: 'waitingRoom'
          })
        })
        .catch(err => {
          console.log(err)
        })
    }

    return { openDialog, updateGameInfo, leaveRoom, gameStart }
  }
}
</script>


