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
      isAllConnected: false,
    })


    const connectSocket = () => {
      let socket = new SockJS("/narang")
      state.stompClient = Stomp.over(socket)
      state.stompClient.connect({}, () => {
          subscribeChat() // 채팅 소켓
          subscribeCheckConnect() // 모든 유저가 접속했는지에 따라 true or false 값을 준다
          subscribeGuessName() // 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
        }
      )
    }


    const subscribeChat = () => {
      state.stompClient.subscribe(`/from/call/chat/${route.params.roomId}`, res => {
        const chat = JSON.parse(res.body)
        state.chatList.push(chat)
      })
    }


    const subscribeCheckConnect = () => {
      state.stompClient.subscribe(`/from/call/checkConnect/${route.params.roomId}`, res => {
        console.log(res)
        const data = JSON.parse(res.body)
        console.log(res.body)
        console.log(data)
        state.isAllConnected = true
      })
    }


    const subscribeGuessName = () => {
      state.stompClient.subscribe(`/from/call/guess-name/${route.params.roomId}`, res => {
        const guessNameRes = JSON.parse(res.body)
        console.log("guessNameRes")
        console.log(guessNameRes)
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


    connectSocket()

    return { state, route, sendChat }
  }
}
</script>

