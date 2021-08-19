<template>
  <div class="mafia-left-container">
    <GameRoomWebcam
      :roomId="roomId"
      :playerNumber="playerNumber"
      :gameStart="gameStart"
      @sendAddPlayer="sendAddPlayer"/>
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
    },
    gameStart: {
      type: Boolean
    },
    playerNumber: {
      type: Number,
      default: 0,
    }
  },

  components: {
    GameRoomWebcam,
  },

  setup(props, { emit }) {

    const sendAddPlayer = () => {
      emit('sendAddPlayer')
    }

    return { sendAddPlayer }
  }
}
</script>

<style scoped>
.mafia-left-container {
  overflow: auto;
  display: grid;
  place-items: center;
  position: relative;
}
</style>
