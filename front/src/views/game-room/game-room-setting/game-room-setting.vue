<template>
  <div class="setting-container">
    <div class="game-btn">
      <div>Game Start!</div>
      <el-button>마피아 게임</el-button>
      <el-button>콜마이네임</el-button>
    </div>
    <div class="setting-btn">
      <el-button>소리끄기</el-button>
      <el-button>화면끄기</el-button>
      <el-button @click="openDialog">방 정보 수정</el-button>
    </div>
  </div>
</template>
<style>
  @import url('./game-room-setting.css');
</style>
<script>
export default {
  name: 'GameRoomSetting',
  setup(props, { emit }) {

    const openDialog = () => {
      emit('openDialog')
    }

    return { openDialog }
  }
}
</script>


