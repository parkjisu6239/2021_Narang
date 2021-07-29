<template>
  <article class="game-room-container">
    <section class="game-cam-chat-container">
      <GameRoomWebcam/>
      <GameRoomChat :roomId="route.params.roomId"/>
    </section>
    <GameRoomSetting :roomId="route.params.roomId" @openDialog="openDialog"/>
  </article>
  <GameRoomInfoChangeDialog @closeDialog="closeDialog" :open="state.open"/>

</template>
<style scoped>
  @import url('./game-room.css');
</style>
<script>
import GameRoomInfoChangeDialog from './game-room-setting/game-room-info-change-dialog.vue'
import GameRoomChat from './game-room-chat/game-room-chat.vue'
import GameRoomSetting from './game-room-setting/game-room-setting.vue'
import GameRoomWebcam from './game-room-webcam/game-room-webcam.vue'
import { reactive } from '@vue/reactivity'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
export default {
  name: "gameRoom",
  components: {
    GameRoomChat,
    GameRoomSetting,
    GameRoomWebcam,
    GameRoomInfoChangeDialog,
  },
  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()
    const state = reactive({
      open: false
    })

    const openDialog = () => {
      state.open = true
    }

    const closeDialog = () => {
      state.open = false
    }

    const requestRoomInfo = () => {
      store.dispatch('root/requestReadSingleGameRoom', route.params.roomId)
        .then(res => {
          store.commit('root/setRoomInfo', res.data.room)
          console.log('방 입장 완료')
        })
        .catch(err => {
          ElMessage(err)
        })
    }

    const requestUserInfo = () => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          store.commit('root/setUserInfo', res.data.user)
        })
    }

    requestRoomInfo()
    requestUserInfo()

    return { state, openDialog, closeDialog, requestRoomInfo, route }
  }
}
</script>


