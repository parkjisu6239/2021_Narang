<template>
  <div class="game-room-item">
    <div :class="{header: true, canEnter: room.isActivate}">
      <div>
        {{ room.title }}
        <i v-if="room.password !== 0" class="el-icon-lock"></i>
      </div>
      <div>{{ user ? user.length : 0 }} / {{ room.maxPlayer }}</div>
    </div>
    <div class="content">
      <div class="thumbnail">
        <div v-if="room.isActivate">
          <img v-if="room.game === null" :src="require('@/assets/images/game-thumbnail-default.png')" alt="">
          <img v-if="room.game === 'mafia'" :src="require('@/assets/images/game-thumbnail-mafia.png')" alt="">
          <img v-if="room.game === 'callmy'" :src="require('@/assets/images/game-thumbnail-callmy.png')" alt="">
        </div>
        <div v-else>
          <img v-if="room.game === null" :src="require('@/assets/images/game-thumbnail-default.png')" alt="">
          <img v-if="room.game === 'mafia'" :src="require('@/assets/images/game-thumbnail-mafia-disable.png')" alt="">
          <img v-if="room.game === 'callmy'" :src="require('@/assets/images/game-thumbnail-callmy-disable.png')" alt="">
        </div>
      </div>
      <div class="status">
        <div class="selected-game">Now selected {{ room.game ? room.game : 'None' }}</div>
        <div class="play-or-wait">
          <div class="wait" v-if="room.isActivate">Waiting</div>
          <div class="play" v-else>Playing</div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.game-room-item {
  background: rgba(255, 255, 255, 0.5);
  box-sizing: border-box;
  backdrop-filter: blur(50px);
  border-radius: 20px;
  padding: 10px;
  margin: 0px 10px;
  margin-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  padding: 5px 15px;
  background: linear-gradient(180deg, #616161 0%, #808080 100%);
  border-radius: 12px;
  color: white;
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 10px;
}

.canEnter {
  background: linear-gradient(180deg, #FF9F00 0%, #FAC04F 100%);
}

.status {
  padding-right: 15px;
  padding-top: 10px;
}

.content {
  display: flex;
  justify-content: space-between;
}

.thumbnail img {
  height: 100px;
  border-radius: 15px;
}

.selected-game {
  font-weight: 600;
}

.play-or-wait {
  font-size: 40px;
  font-weight: 700;
}

.play-or-wait .wait {
  color: #FF9F00;
}

.play-or-wait .play {
  color: #727272;
}

</style>
<script>
export default {
  name: 'Home',

  props: {
    room: {
      type: Object,
    },
    user: {
      type: Object,
    }
  },

  setup () {}
}
</script>
