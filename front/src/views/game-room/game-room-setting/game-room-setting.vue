<template>
  <div class="setting-container">

    <div class="game-btns">
      <div class="game-start" style="border-top-right-radius: 0px; border-bottom-right-radius: 0px;">Game Start!</div>
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
      <div class="setting-btn"><i class="el-icon-close"></i></div>
    </div>

  </div>
</template>
<style scoped>
  @import url('./game-room-setting.css');
</style>
<script>
import { useStore } from 'vuex'
import { reactive } from 'vue'

export default {
  name: 'GameRoomSetting',
  setup(props, { emit }) {
    const store = useStore()

    const openDialog = () => {
      emit('openDialog')
    }

    const updateGameInfo = (event) => {
      const game = event.target.dataset.game
      const roomInfo = {
        game,
        roomId: 1,
      }

      store.dispatch('root/requestUpdateGameRoom')
        .then(res => {
          console.log(res)
        })
        .catch(err => {
          console.log(err)
        })
    }

    return { openDialog, updateGameInfo }
  }
}
</script>


