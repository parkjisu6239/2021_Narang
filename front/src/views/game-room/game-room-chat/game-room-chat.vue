<template>
  <div class="chat-container" style="border-radius: 0px 10px 10px 0px">

    <span class="chat-header" style="border-radius: 20px">Live Chat</span>
    <span class="close-chat"><i class="el-icon-close"></i></span>

    <div class="chat-area" style="border-radius: 0px">
      <div v-for="(chat, idx) in state.chatList" :key="idx">
        <img :src="state.myProfile" alt="">
        <span>{{ chat.userName }}: {{ chat.content }}</span>
      </div>
    </div>

    <div class="chat-input-button" style="border-radius: 0px">
      <input @keyup.enter="sendMessage" class="chat-input" placeholder="내용을 입력해주세요." v-model="state.myChat">
      <div @click="sendMessage" class="chat-send-button" style="border-radius: 50%;"><i class="el-icon-thumb"></i></div>
    </div>

  </div>
</template>
<style scoped>
  @import url('./game-room-chat.css');
</style>

<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { reactive } from '@vue/reactivity'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'

export default {
  name: 'GameRoomChat',
  props: {
    roomId: {
      type: Number,
    }
  },
  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()

    let stompClient
    const state = reactive({
      stompClient: '',
      myChat: '',
      chatList: [],
    })

    const sendMessage = () => {
      if (stompClient && stompClient.connected) {
        const message = {
          userName: store.state.root.username,
          content: state.myChat,
          roomId: props.roomId,
        }
        stompClient.send('/server', JSON.stringify(message), {})
        state.myChat = ''
      }
    }

    const connectSocket = () => {
      let socket = new SockJS("https://localhost:8080/chat")
      stompClient = Stomp.over(socket)
      console.log(props.roomId)
      stompClient.connect({},
        frame => {
          stompClient.subscribe(`/client/${props.roomId}`, res => {
            console.log(res.body)
            state.chatList.push(JSON.parse(res.body))
          })
        }
      )
    }

    connectSocket()

    return { state, sendMessage }
  }
}
</script>

