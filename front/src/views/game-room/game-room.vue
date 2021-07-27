<template>
  <article class="game-room-container">
    <section class="game-cam-chat-container">
      <GameRoomWebcam/>
      <GameRoomChat/>
    </section>
    <GameRoomSetting @openDialog="openDialog"/>
  </article>
  <GameRoomInfoChangeDialog @closeDialog="closeDialog" :open="state.open"/>

</template>
<style>
  @import url('./game-room.css');
</style>
<script>
import GameRoomInfoChangeDialog from './game-room-setting/game-room-info-change-dialog.vue'
import GameRoomChat from './game-room-chat/game-room-chat.vue'
import GameRoomSetting from './game-room-setting/game-room-setting.vue'
import GameRoomWebcam from './game-room-webcam/game-room-webcam.vue'
import { reactive } from '@vue/reactivity'
export default {
  name: "gameRoom",
  components: {
    GameRoomChat,
    GameRoomSetting,
    GameRoomWebcam,
    GameRoomInfoChangeDialog,
  },
  setup(props, { emit }) {
    const state = reactive({
      open: false
    })

    const openDialog = () => {
      state.open = true
    }

    const closeDialog = () => {
      state.open = false
    }

    return { state, openDialog, closeDialog }
  }
}
</script>


