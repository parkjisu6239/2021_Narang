<template>
  <div class="callmy-container">
    <CallMyWebCam/>
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
          subscribeChatSocket() // 채팅 소켓
          subscribeAllConnected() // 모든 유저가 접속했는지에 따라 true or false 값을 준다
          subscribeGuessName() // 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
        }
      )
    }
    /**
     *
     * subscribe
     *
     */
    const subscribeChatSocket = () => {
      /**
      * 게임 방 안에서 채팅할 때 호출되는 메서드
      * @param chatModel
      * @return 채팅에 필요한 내용을 가진 객체
      */
      const chatEndPoint = `/from/call/chat/${route.params.roomId}`
      state.stompClient.subscribe(chatEndPoint, res => {
        const chat = JSON.parse(res.body)
        state.chatList.push(chat)
      })
    }


    const subscribeAllConnected = () => {
      const chatEndPoint = `/from/call/checkConnect/${route.params.roomId}`
      state.stompClient.subscribe(chatEndPoint, res => {
        console.log(res)
        const data = JSON.parse(res.body)
        console.log(res.body)
        console.log(data)
        state.isAllConnected = true;
      })
    }

    const subscribeGuessName = () => {
      /**
      * 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
      * TODO : 반환값 필요한거 추가하기
      * @param roomId : path로 받는 roomId (PK)
      * @param req : userId와 정해진 이름이 있는 객체
      * @return 답이 맞았는지, 게임이 끝났는지 여부를 멤버변수로 가진 객체
      */
      const chatEndPoint = `/from/call/guess-name/${route.params.roomId}`
      state.stompClient.subscribe(chatEndPoint, res => {
        const guessNameRes = JSON.parse(res.body)
        console.log("guessNameRes")
        console.log(guessNameRes);
      })
    }

    connectSocket()



    /**
     *
     *  send
     *
     */
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

    return { state, route, sendChat }
  }
}
</script>

