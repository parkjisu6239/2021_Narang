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
      let socket = new SockJS("/narang")
      state.stompClient = Stomp.over(socket)
      state.stompClient.connect({}, () => {
          console.log('콜마이')
          connectChatSocket() // 채팅 소켓
        }
      )
    }

    const connectChatSocket = () => {
      const chatEndPoint = `/from/call/chat/${route.params.roomId}`
      state.stompClient.subscribe(chatEndPoint, res => {
        const chat = JSON.parse(res.body)
        console.log(chat)
        state.chatList.push(chat)
      })
    }

    const sendChat = (chat) => {
      console.log('챗 보냄', state.stompClient)
      if (state.stompClient && state.stompClient.connected && chat) {
        const message = {
          userName: store.state.root.username,
          content: chat,
          roomId: route.params.roomId,
          profileImageURL: '',
          roomInfoChange: false,
          gameStart: false,
        }
        console.log('챗 보내는 중')
        state.stompClient.send(`/to/call/chat/${route.params.roomId}`, JSON.stringify(message), {})
      }
    }

    connectSocket()
    return { state, route, sendChat }
  }
}
</script>

