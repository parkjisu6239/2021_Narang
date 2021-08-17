<template>
  <div class="callmy-container">
    <div class="callmy-left-side">
      <div :class="{'stt-container':true, 'ans': state.ans}" v-if="state.callmyManager.isAnswer && state.speaker !== store.state.root.username">
        <div class="stt-constent">
          <div> {{ state.speaker }}의 이름은?!?!<span></span> 빠크 <span>빠크</span> 빠크</div>
          <h1>{{ state.answer }}</h1>
        </div>
      </div>
      <CallMyWebCam
        @joinSomeOne="joinSomeOne"
        @joinCallMyRoom="joinCallMyRoom"
        :socketConnected="state.socketConnected"
        :roomId="route.params.roomId"
        :gameStart="state.isAllConnected"
        :roundStart="state.roundStart"
        :playerNumbers="state.userList.length"
        :startDetection="state.startDetection"/>
    </div>
    <div class="callmy-right-side">
      <CallMyGameBoard
        :nicknameList="state.nicknameList"
        :order="state.order"
        :isVoteTime="state.isVoteTime"
        :defaultNickname="store.state.root.callmyManager.defaultNickname"
        @sendVote="sendVote"/>
      <CallMyChat
        :chatList="state.chatList"
        :roomId="route.params.roomId"
        @sendChat="sendChat"/>
      <CallmySetting/>
    </div>
  </div>
  <CallmyShowDraw
    v-show="state.showDraw"
    :players="state.callmyManager.nowPlayUsers"/>
  <CallmyStt @sendGuessName="sendGuessName" v-if="!state.isVoteTime && state.callmyManager.nowPlayUsers.length && (state.userId === state.callmyManager.nowPlayUsers[0].userId || state.userId === state.callmyManager.nowPlayUsers[1].userId)"/>
  <CallmyBackground/>
</template>
<style scoped>
  @import url('./callmy.css');
  @import url('./callmy-stt/callmy-stt.css');
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
import CallmyShowResult from './callmy-result/callmy-showresult.vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { reactive, computed, onBeforeUnmount } from 'vue'

export default {
  name: 'callMy',
  components: {
    CallMyWebCam,
    CallMyChat,
    CallMyGameBoard,
    CallmyBackground,
    CallmySetting,
    CallmyStt,
    CallmyShowResult,
  },

  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()


    const state = reactive({
      stompClient: null,
      chatList: [],
      userList: {},
      callmyManager: computed(() => store.state.root.callmyManager),
      userId: computed(() => store.state.root.userId),
      socketConnected: false,
      nicknameList: {},
      userIdToUserName: {},
      showResult: false,
      order: 0,
      isVoteTime: false,
      startDetection: false,
      isAllConnected: false,
      roundStart: false,
      round: 1,
      answer: '',
      speaker: '',
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
        state.isVoteTime = true
        sendPlay('next')
      })
    }


    const subscribeGuessName = () => {
      state.stompClient.subscribe(`/from/call/guess-name/${route.params.roomId}`, res => {
        const guessNameRes = JSON.parse(res.body)
        console.log("guessNameRes")
        console.log(guessNameRes)
        if(guessNameRes.answer === '정답타임끝'){
          endAnswerTime();
          return;
        }

        if(guessNameRes.answer === '정답'){
          console.log("현재 정답을 말하고 있습니까?")
          console.log(store.state.root.callmyManager.isAnswer)
          if(store.state.root.callmyManager.isAnswer) return;
          store.state.root.callmyManager.isAnswer = true;
          console.log("아니요 아무도 말 안하고 있습니다~")
        }
        state.speaker = state.userIdToUserName[guessNameRes.userId];
        state.answer = guessNameRes.answer;
        if(guessNameRes.gameEnd) {
            gameOver();
            return;
        }
        if(guessNameRes.correct) {
          console.log(`${state.speaker}가 승리했습니다`)
          state.isVoteTime = true
          state.roundStart = false
          state.startDetection = false
          endAnswerTime();
          sendPlay("next")
          return;
        }
        state.answer = '틀렸습니당';
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
          state.isVoteTime = true
          sendDefaultNickname() // 1번 사람 디폴트 닉네임 받기
          showResult()
        } else { // 플레이 하는 시간
          store.state.root.callmyManager.nowPlayUsers[0].nickname = result.user1.nickname
          store.state.root.callmyManager.nowPlayUsers[1].nickname = result.user2.nickname
          state.startDetection = true
        }
      })
      console.log(state.callmyManager)
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
        console.log(res)
        console.log(res.body)
        const DefaultNickname = res.body
        if (state.userId !== state.callmyManager.nowPlayUsers[state.order].userId) {
          store.state.root.callmyManager.defaultNickname = DefaultNickname
        } else {
          store.state.root.callmyManager.defaultNickname = ''
        }
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
      if (stage === 'next') {
        console.log('여기서 roundStart, startDetection 초기화')
        state.roundStart = true
        state.startDetection = false
      }
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.send(`/to/call/play/${route.params.roomId}`, JSON.stringify(stage), {})
      }
    }


    const sendGuessName = (message) => {
      state.stompClient.send(`/to/call/guess-name/${route.params.roomId}`, JSON.stringify(message), {})
    }


    const sendDefaultNickname = () => {
      if (state.stompClient && state.stompClient.connected) {
        state.stompClient.send(`/to/call/default-name/${route.params.roomId}/${state.userId}`)
      }
    }


    const joinCallMyRoom = () => {
      console.log('조인하는 중')
      setTimeout(() => {
        state.stompClient.send(`/to/call/addPlayer/${route.params.roomId}`, JSON.stringify(state.userId), {})
      }, 1000)
    }


    const requestMyInfo = () => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
         const userInfo = {
            email: res.data.user.email,
            username: res.data.user.username,
            profileImageURL: res.data.user.thumbnailUrl,
            userId: res.data.user.userId,
          }
          store.commit('root/setUserInfo', userInfo)
        })
        .catch(err => {
          ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
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
          ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
        })
    }


    const requestRoomInfo = () => {
      store.dispatch('root/requestReadSingleGameRoom', route.params.roomId)
        .then(res => {
          store.commit('root/setRoomInfo', res.data.room)
        })
        .catch(err => {
          ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
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
      endAnswerTime();
      setTimeout(() => {
        // 게임 정보 변경
        const roomInfo = {
          ...store.state.root.room,
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


    const socketDisconnect = () => {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
    }


    const showResult = () => {
      setTimeout(() => {
        state.showResult = true
      }, 1000)
      setTimeout(() => {
        state.showResult = false
      }, 4000)
    }


    const endAnswerTime = () => {
          state.speaker = '';
          state.answer = '';
          store.state.root.callmyManager.isAnswer = false;
    }

    onBeforeUnmount(() => {
      socketDisconnect()
    })


    requestRoomInfo()
    requestMyInfo()
    requestUserList()
    connectSocket()

    return { state, store, route, sendChat, joinCallMyRoom, sendVote, sendPlay, sendGuessName }
  }
}
</script>

