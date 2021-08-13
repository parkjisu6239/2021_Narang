<template>
  <div class="callmy-container">
    <CallMyWebCam
      :roomId="Number(route.params.roomId)"/>
    <div class="callmy-right-side">
      <CallMyGameBoard/>
      <CallMyChat
        :chatList="state.chatList"
        :roomId="Number(route.params.roomId)"
        @sendChat="sendChat"/>
    </div>
  </div>
</template>
<style>
  @import url('./callmy.css');
</style>
<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'
import CallMyWebCam from './callmy-webcam/callmy-webcam.vue'
import CallMyChat from './callmy-chat/callmy-chat.vue'
import CallMyGameBoard from './callmy-gameboard/callmy-gameboard.vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { reactive } from '@vue/reactivity'

export default {
  name: 'callMy',
  components: {
    CallMyWebCam,
    CallMyChat,
    CallMyGameBoard
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
      let socket = new SockJS("/narang")
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
        state.chatList.push(chat)
      })
    }

    const sendChat = (chat) => {
      if (state.stompClient && state.stompClient.connected && chat) {
        const message = {
          userName: store.state.root.username,
          content: chat,
          roomId: route.params.roomId,
          profileImageURL: '',
          roomInfoChange: false,
          gameStart: false,
        }
        state.stompClient.send(`/to/call/chat/${route.params.roomId}`, JSON.stringify(message), {})
      }
    }

    const isAllConnectedSocket = () => {
      const chatEndPoint = `/from/call/checkConnect/${route.params.roomId}`
      state.stompClient.subscribe(chatEndPoint, res => {
        console.log(res)
        const chat = JSON.parse(res.body)
        console.log(chat)
        chatList.push(chat)
      })
    }

    connectSocket()
    return { state, route, sendChat }
  }
}
</script>

