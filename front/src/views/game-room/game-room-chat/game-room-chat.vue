<template>
  <div class="chat-container" style="border-radius: 0px 10px 10px 0px">

    <span class="chat-header" style="border-radius: 20px">Live Chat</span>
    <span class="close-chat"><i class="el-icon-close"></i></span>

    <div class="chat-area" style="border-radius: 0px">
      <div v-for="(chat, idx) in state.chatList" :key="idx">

        <div v-if="chat.userName === state.myUserName">
          <div v-if="idx === 0 || state.chatList[idx].userName !== state.chatList[idx - 1].userName" class="chat-profile-username">
            <img v-if="chat.profileImageURL" class="chat-profile" :src="chat.profileImageURL">
            <img v-else class="chat-profile" :src="require('@/assets/images/Neurang.png')">
            <span class="chat-username">{{ chat.userName }}</span>
          </div>
          <div><div class="chat-content">{{ chat.content }}</div></div>
        </div>

        <div v-else>
          <div v-if="idx === 0 || state.chatList[idx].userName !== state.chatList[idx - 1].userName" class="chat-profile-username-you">
            <img v-if="chat.profileImageURL" class="chat-profile" :src="chat.profileImageURL">
            <img v-else class="chat-profile" :src="require('@/assets/images/Neurang.png')">
            <span class="chat-username">{{ chat.userName }}</span>
          </div>
          <div class="chat-you"><div class="chat-content-you">{{ chat.content }}</div></div>
        </div>

      </div>
    </div>

    <div class="chat-input-button" style="border-radius: 0px">
      <input @keyup.enter="sendMessage" class="chat-input" style="border-radius: 30px 0px 0px 30px;" placeholder="내용을 입력해주세요." v-model="state.myChat">
      <div @click="sendMessage" class="chat-send-button" style="border-radius: 0px 30px 30px 0px;"><i class="el-icon-edit send-icon"></i></div>
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
import { computed } from 'vue'

export default {
  name: 'GameRoomChat',
  props: {
    roomId: {
      type: Number,
    },
    userList: {
      type: Object,
    },
  },
  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()

    let stompClient
    const state = reactive({
      stompClient: '',
      myChat: '',
      chatList: [],
      myUserName: computed(() => store.state.root.username),
    })

    const sendMessage = () => {
      if (stompClient && stompClient.connected && state.myChat) {
        let profileImageURL = null
        props.userList.forEach(user => {
          if (user.thumbnailURL && user.username === state.myUserName) {
            profileImageURL = 'https://localhost:8080/' + thumbnailURL
          }
        })

        const message = {
          userName: store.state.root.username,
          content: state.myChat,
          roomId: props.roomId,
          profileImageURL,
        }
        stompClient.send('/server', JSON.stringify(message), {})
        state.myChat = ''
      }
    }

    const connectSocket = () => {
      let socket = new SockJS("https://localhost:8080/chat")
      stompClient = Stomp.over(socket)
      stompClient.connect({},
        frame => {
          stompClient.subscribe(`/client/${props.roomId}`, res => {
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

