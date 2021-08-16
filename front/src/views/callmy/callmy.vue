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
        :order="state.order"
        :isVoteTime="state.isVoteTime"
        @sendVote="sendVote"/>
      <CallMyChat
        :chatList="state.chatList"
        :roomId="route.params.roomId"
        @sendChat="sendChat"/>
      <CallmySetting/>
    </div>
  </div>
  <CallmyStt @sendGuessName="sendGuessName" v-if="state.callmyManager.nowPlayUsers.length && (state.userId === state.callmyManager.nowPlayUsers[0].userId || state.userId === state.callmyManager.nowPlayUsers[1].userId)"/>
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
      order: 0,
      isVoteTime: false,
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
          subscribeDefaultNickname()
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
        state.isVoteTime = true
        sendDefaultNickname() // 1번 사람 디폴트 닉네임 받기
      })
    }


    const subscribeGuessName = () => {
      state.stompClient.subscribe(`/from/call/guess-name/${route.params.roomId}`, res => {
        const guessNameRes = JSON.parse(res.body)
        console.log("guessNameRes")
        console.log(guessNameRes)
        if(guessNameRes.isGameEnd) {
          gameOver();
          return;
        }
        if(guessNameRes.isCorrect) {
          const winner = state.userIdToUserName[result.userId];
          console.log("내가 바로 개다")
          console.log(winner)
          sendPlay("next")
          init();
          return;
        }
        console.log("틀렸습니다.")
      })
    }


    const subscribePlay = () => { // 게임 진행 중인 유저들의 정보, 현재 라운드, status => 0: 제시어 정함, 1: 하는 중,
      state.stompClient.subscribe(`/from/call/play/${route.params.roomId}`, res => {
        const result = JSON.parse(res.body)
        console.log(result, '다음 대결자들')
        store.state.root.callmyManager.round = result.round;

        if(result.status === 0){ // 제시어 정하는 시간
          store.state.root.callmyManager.nowPlayUsers = [
            {
              userId: result.user1.userId,
              username: state.userIdToUserName[result.user1.userId],
              nickname: '',
            },
            {
              userId: result.user2.userId,
              username: state.userIdToUserName[result.user2.userId],
              nickname: '',
            }
          ];
        } else { // 플레이 하는 시간
          store.state.root.callmyManager.nowPlayUsers[0].nickname = result.user1.nickname
          store.state.root.callmyManager.nowPlayUsers[1].nickname = result.user2.nickname
        }
      })
    }


    const subscribeSetName = () => {
      state.stompClient.subscribe(`/from/call/set-name/${route.params.roomId}`, res => {
        const setNamRes = JSON.parse(res.body)
        if (setNamRes.isFinished) {
          if (state.callmyManager.nowPlayUsers[0].nickname){ // user1의 닉네임이 있으면 user2 닉네임 저장
            state.callmyManager.nowPlayUsers[1].nickname = setNamRes.result
            console.log(`${state.callmyManager.nowPlayUsers[1].username}의 제시어는 ${setNamRes.result}입니다`)
            state.nicknameList = {}
            state.order = 0
            sendPlay('now')
            state.isVoteTime = false
          } else { // user1의 닉네임이 없으면 user1 닉네임 저장
            state.callmyManager.nowPlayUsers[0].nickname = setNamRes.result
            console.log(`${state.callmyManager.nowPlayUsers[0].username}의 제시어는 ${setNamRes.result}입니다`)
            state.nicknameList = {}
            state.order = 1
            sendDefaultNickname() // 두번째 사람 디폴트 이름 받아오기
          }
        } else {
          state.nicknameList = setNamRes.voteStatus
        }
        console.log("setNamRes")
        console.log(setNamRes)
      })
    }


    const subscribeDefaultNickname = () => {
      state.stompClient.subscribe(`/from/call/default-name/${route.params.roomId}/${state.userId}`, res => {
        const DefaultNickname = JSON.parse(res.body)
        store.state.root.callmyManager.defaultNickname = DefaultNickname
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


    const sendGuessName = (answer) => {
      state.stompClient.send(`/from/call/guess-name/${route.params.roomId}`, JSON.stringify({answer}), {})
    }


    const sendDefaultNickname = () => {
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.send(`/to/call/default-name/${route.params.roomId}/${state.userId}`)
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

    const init = () => {
      store.state.root.callmyManager.status = 0;
      store.state.root.callmyManager.isFinished = false;
      store.state.root.callmyManager.nowPlayUsers = [];
      store.state.root.callmyManager.draw =  [];
    }


    const gameOver = () => {
      // 상태 초기화
      init();
      setTimeout(() => {
        // 게임 정보 변경
        const roomInfo = {
          ...state.room,
          isActivate: true
        }

        store.dispatch('root/requestUpdateGameRoom', roomInfo)
        .then(res => {
          console.log('방정보가 정상적으로 변경되었습니다. 입장 가능')
        })
        .catch(err => {
          console.log(err)
        })

        router.push({
          name: 'gameRoom',
          params: {
            roomId: route.params.roomId,
          }
        })
      }, 5000);
    }


    requestMyInfo()
    requestUserList()
    connectSocket()


    return { state, store, route, sendChat, joinCallMyRoom, sendVote, sendPlay, sendGuessName }
  }
}
</script>

