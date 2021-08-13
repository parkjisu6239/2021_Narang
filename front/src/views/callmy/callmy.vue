<template>
  <div class="callmy-container">
    <CallMyWebCam
      :roomId="route.params.roomId"/>
    <div class="callmy-right-side">
      <CallMyGameBoard/>
      <CallMyChat
        :chatList="state.chatList"
        :roomId="route.params.roomId"
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
          SubcribeChatSocket() // 채팅 소켓
        }
      )
    }

    const SubcribeChatSocket = () => {
      const chatEndPoint = `/from/call/chat/${route.params.roomId}`
      state.stompClient.subscribe(chatEndPoint, res => {
        const chat = JSON.parse(res.body)
        state.chatList.push(chat)
      })
    }

    const SubcribeStartSocket = () => {

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

    connectSocket()
    return { state, route, sendChat }
  }
}
</script>

