<template>
  <div class="setting-container">
    <div class="game-btns" v-show="state.disableGameBtns">
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
      <div class="setting-btn" @click="muteAudio"><i class="el-icon-microphone"></i></div>
      <div class="setting-btn" @click="muteVideo"><i class="el-icon-video-camera"></i></div>
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
    },
  },
  setup(props, { emit }) {
    const store = useStore()
    const route = useRouter()

    const state = reactive({
      userList: [],
      disableGameBtns:undefined,
    })

    const setBtnDisabled = () => {
      console.log("방 주인 : " + props.room.ownerId);
      console.log("접속 유저 : " + store.state.root.userId);
      if (props.room.ownerId === store.state.root.userId) {
        console.log("방장한테만 보이게");
        state.disableGameBtns=true;
      }
      else state.disableGameBtns=true;
    }

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
            message: '방장만 변경할 수 있습니다.'
          })
        })
    }

    const gameStart = () => {
      console.log("유저목록");
      state.userList.forEach(user => {
        console.log(user.userId)
        })
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
    const ovSetting = {
      onVideo : true,
      onAudio : true
    }
    const muteAudio = () => {
        ovSetting.onAudio = !ovSetting.onAudio;
        store.publisher.publishAudio(ovSetting.onAudio);
    }
    const muteVideo = () => {
        ovSetting.onVideo = !ovSetting.onVideo;
        store.publisher.publishVideo(ovSetting.onVideo);
    }
    const requestUserList = () => {
      store.dispatch('root/requestReadUserList', props.roomId)
        .then(res => {
          state.userList = res.data.userList
        })
        .catch(err => {
          ElMessage(err)
        })
    }
    requestUserList()
    setBtnDisabled()
    return { state, openDialog, updateGameInfo, leaveRoom, muteAudio, muteVideo, gameStart}
  }
}
</script>


