<template>
  <div class="callmy-container">
    <callMyWebCam/>
    <callMyChat
      :chatList="state.chatList"
      :roomId="route.params.roomId"
      @sendChat="sendChat"
      />
  </div>
</template>
<style>
  @import url('./callmy.css');
</style>
<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import callMyWebCam from './callmy-webcam/callmy-webcam.vue'
import callMyChat from './callmy-chat/callmy-chat.vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { reactive } from '@vue/reactivity'

export default {
  name: 'callMy',
  components: {
    callMyWebCam,
    callMyChat,
  },
  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()

    const state = reactive({
      stompClient: null,
      chatList: [],
    })

    const connectSocket = () => {
      let socket = new SockJS("/call")
      state.stompClient = Stomp.over(socket)
      state.stompClient.connect({}, () => {
          connectChatSocket() // 채팅 소켓
        }
      )
    }

    const connectChatSocket = () => {
      const chatEndPoint = `/from/call/chat/${route.params.roomId}`
      state.stompClient.subscribe(chatEndPoint, res => {
        const chat = JSON.parse(res.body)
        chatList.push(chat)
      })
    }

    const sandChat = (chat) => {
      if (state.stompClient && state.stompClient.connected && msg) {
        const message = {
          userName: store.state.root.username,
          content: chat,
          roomId: rotue.params.roomId,
          profileImageURL: '',
          roomInfoChange: false,
          gameStart: false,
        }
        state.stompClient.send(`/call/chat/${route.params.roomId}`)
      }
    }

    connectSocket()
    return { state, route }
  }
}
</script>

