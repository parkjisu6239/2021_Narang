<template>
  <div class="mafia-left-container">
    <GameRoomWebcam :roomId="roomId"/>
        <div id="mission-container"></div>
  </div>
</template>

<script>
import GameRoomWebcam from './game-room-webcam/game-room-webcam.vue'

export default {
  name: 'leftSide',

  props: {
    roomId: {
      type: String
    }
  },

  components: {
    GameRoomWebcam,
  }
}
</script>

<style scoped>
.mafia-left-container {
  overflow: auto;
  display: grid;
  place-items: center;
}
</style>
