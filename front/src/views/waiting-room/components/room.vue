<template>
  <div class="game-room-item">
    <div class="header">
      <div>제목</div>
      <div>3 / 6</div>
    </div>
    <div class="content">
      <div class="thumbnail">
        <img :src="require('@/assets/images/game-thumbnail-default.png')" alt="">
      </div>
      <div class="status">
        <div class="selected-game">Now selected None</div>
        <div class="play-or-wait">Waiting</div>
      </div>
    </div>
  </div>
</template>
<style>
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
  background: linear-gradient(180deg, #FF9F00 0%, #FAC04F 100%);
  border-radius: 12px;
  color: white;
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 10px;
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
  color: #FF9F00;
}

</style>
<script>
export default {
  name: 'Home',

  props: {
    title: {
      type: String,
      default: '제목'
    },
    desc: {
      type: String,
      default: "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
    }
  },

  setup () {}
}
</script>
