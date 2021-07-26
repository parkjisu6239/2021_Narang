<template>
  <div class="chat-container">
    <div class="">
      <h1>하이</h1>
      <h1>하이투</h1>
    </div>
    <el-input v-model="state.myChat"></el-input>
    <el-button>보내기</el-button>
  </div>
</template>
<style>
  @import url('./game-room-chat.css');
</style>

<script>
import { reactive } from '@vue/reactivity'
export default {
  name: 'GameRoomChat',
  setup(props, { emit }) {
    const state = reactive({
      myChat: '',
    })

    const sendChat = () => {
      // chatsocket
    }

    return { sendChat, state }
  }


}
</script>

