<template>
  <div class="callmy-container">
    <div class="callmy-left-side">
      <CallMyWebCam
        @joinSomeOne="joinSomeOne"
        @joinCallMyRoom="joinCallMyRoom"
        :socketConnected="state.socketConnected"
        :roomId="route.params.roomId"
        :gameStart="state.isAllConnected"
        :playerNumbers="state.userList.length"/>
    </div>
    <div class="callmy-right-side">
      <CallMyGameBoard
        :nicknameList="state.nicknameList"
        @sendVote="sendVote"/>
      <CallMyChat
        :chatList="state.chatList"
        :roomId="route.params.roomId"
        @sendChat="sendChat"/>
      <CallmySetting/>
    </div>
  </div>
  <CallmyStt/>
  <CallmyBackground/>
</template>
<style scoped>
  @import url('./callmy.css');
</style>
<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import CallMyWebCam from './callmy-webcam/callmy-webcam.vue'
import CallMyChat from './callmy-chat/callmy-chat.vue'
import CallMyGameBoard from './callmy-gameboard/callmy-gameboard.vue'
import CallmyBackground from './callmy-background/callmy-background.vue'
import CallmySetting from './callmy-setting/callmy-setting.vue'
import CallmyStt from './callmy-stt/callmy-stt.vue'
import { ElMessage } from 'element-plus'

import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { reactive, computed } from 'vue'

export default {
  name: 'callMy',
  components: {
    CallMyWebCam,
    CallMyChat,
    CallMyGameBoard,
    CallmyBackground,
    CallmySetting,
    CallmyStt,
  },

  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()


    const state = reactive({
      stompClient: null,
      chatList: [],
      isAllConnected: false,
      userList: {},
      callmyManager: computed(() => store.state.root.callmyManager),
      userId: computed(() => store.state.root.userId),
      socketConnected: false,
      nicknameList: {},
      userIdToUserName: {},
    })


    const connectSocket = () => {
      let socket = new SockJS("/narang")
      state.stompClient = Stomp.over(socket)
      state.stompClient.connect({}, () => {
          state.socketConnected = true // webcam으로 connected 됐다는 props를 내림.
          subscribeChat() // 채팅 소켓
          subscribeCheckConnect() // 모든 유저가 접속했는지에 따라 true or false 값을 준다
          subscribeGuessName() // 사용자가 자신의 이름을 맞힐 때 호출되는 메서드
          subscribeSetName() // 플레이어의 제시어를 결정할 때 호출되는 메서드
          subscribePlay()
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
        state.isAllConnected = true
        state.draw = JSON.parse(res.body)
        sendPlay('next')
      })
    }


    const subscribeGuessName = () => {
      state.stompClient.subscribe(`/from/call/guess-name/${route.params.roomId}`, res => {
        const guessNameRes = JSON.parse(res.body)
        console.log("guessNameRes")
        console.log(guessNameRes)
      })
    }


    const subscribePlay = () => { // 게임 진행 중인 유저들의 정보, 현재 라운드, status => 0: 제시어 정함, 1: 하는 중,
      state.stompClient.subscribe(`/from/call/play/${route.params.roomId}`, res => {
        const result = JSON.parse(res.body)
        console.log(result, '다음 대결자들')
        store.state.root.callmyManager.nowPlayUsers = [
          {
            userId1: result.user1.userId,
            username1: state.userIdToUserName[result.user1.userId],
            nickname1: '',
          },
          {
            userId2: result.user2.userId,
            username2: state.userIdToUserName[result.user2.userId],
            nickname2: '',
          }
        ]
      })
    }


    const subscribeSetName = () => {
      state.stompClient.subscribe(`/from/call/set-name/${route.params.roomId}`, res => {
        const setNamRes = JSON.parse(res.body)
        if (setNamRes.isFinished) {
          if (state.callmyManager.nowPlayUsers[0].nickname1){ // user1의 닉네임이 있으면 user2 닉네임 저장
            state.callmyManager.nowPlayUsers[1].nickname2 = setNamRes.result
            console.log(`${state.callmyManager.nowPlayUsers[1].username2}의 제시어는 ${setNamRes.result}입니다`)
          } else { // user1의 닉네임이 없으면 user1 닉네임 저장
            state.callmyManager.nowPlayUsers[0].nickname = setNamRes.result
            console.log(`${state.callmyManager.nowPlayUsers[0].username1}의 제시어는 ${setNamRes.result}입니다`)
          }
        }
        state.nicknameList = setNamRes.voteStatus
        console.log("setNamRes")
        console.log(setNamRes)
      })
    }


    const sendChat = (message) => {
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.send(`/to/call/chat/${route.params.roomId}`, JSON.stringify(message), {})
      }
    }


    const sendVote = (message) => {
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.send(`/to/call/set-name/${route.params.roomId}`, JSON.stringify(message), {})
      }
    }


    const sendPlay = (stage) => {
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.send(`/to/call/play/${route.params.roomId}`, JSON.stringify(stage), {})
      }
    }


    const joinCallMyRoom = () => {
      console.log('조인하는 중')
      state.stompClient.send(`/to/call/addPlayer/${route.params.roomId}`, JSON.stringify(state.userId), {})
    }


    const requestMyInfo = () => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          console.log(res, '내 정보')
          store.commit('root/setUserInfo', res.data.user)
        })
        .catch(err => {
          ElMessage(err)
        })
    }


    const requestUserList = () => {
      store.dispatch('root/requestReadUserList', route.params.roomId)
        .then(res => {
          state.userList = res.data.userList
          for (let i = 0; i < state.userList.length; i++) {
            state.userIdToUserName[state.userList[i].userId] = state.userList[i].username
          }
        })
        .catch(err => {
          ElMessage(err)
        })
    }


    requestMyInfo()
    requestUserList()
    connectSocket()

    return { state, route, sendChat, joinCallMyRoom, sendVote, sendPlay }
  }
}
</script>

