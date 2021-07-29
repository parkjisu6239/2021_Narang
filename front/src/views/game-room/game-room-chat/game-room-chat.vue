<template>
  <div class="chat-container" style="border-radius: 0px 10px 10px 0px">
    <span class="chat-header" style="border-radius: 20px">Live Chat</span>
    <span class="close-chat"><i class="el-icon-close"></i></span>
    <div class="chat-area" style="border-radius: 0px">
      <div v-for="(chat, idx) in state.chatList" :key="idx">
        <img :src="state.myProfile" alt="">
        <span>{{ chat.username }}</span>
        {{ chat.content }}
      </div>
    </div>
    <div class="chat-input-button" style="border-radius: 0px">
      <input @keyup.enter="sendChat" class="chat-input" placeholder="내용을 입력해주세요." v-model="state.myChat">
      <div @click="sendChat" class="chat-send-button" style="border-radius: 50%;"><i class="el-icon-thumb"></i></div>
    </div>
  </div>
</template>
<style scoped>
  @import url('./game-room-chat.css');
</style>

<script>
import { reactive } from '@vue/reactivity'
import { computed } from '@vue/runtime-core'
import { useStore } from 'vuex'
export default {
  name: 'GameRoomChat',
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({
      myChat: '',
      chatList: [],
      myUsername: store.getters['root/username'],
      myProfile: store.getters['root/profileImageURL']
    })

    const sendChat = () => {
      const newChatList = state.chatList.map((chat) => {
        return {
          ...chat
        }
      })

      newChatList.push({
        username: state.myUsername,
        content: state.myChat,
      })

      state.chatList = newChatList
      state.myChat = ''
      console.log(state.chatList)
    }

    return { sendChat, state }
  }


}
</script>

