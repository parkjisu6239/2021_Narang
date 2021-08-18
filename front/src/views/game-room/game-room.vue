<template>
  <article class="game-room-container">
    <section class="game-cam-chat-container">
      <GameRoomWebcam :roomId="route.params.roomId"/>
      <GameRoomChat
        @sendMessage="sendChatMessage"
        :roomId="route.params.roomId"
        :room="state.room"
        :userList="state.userList"
        :chatList="state.chatList"/>
    </section>
    <GameRoomSetting
      @leaveRoom="leaveRoom"
      @changeGame="informGameRoomInfoChange"
      @openDialog="openDialog"
      @gameStart="gameStart"
      :roomId="route.params.roomId"
      :room="state.room"/>
  </article>
  <GameRoomInfoChangeDialog
    @closeDialog="closeDialog"
    @changeGameRoomInfo="informGameRoomInfoChange"
    :open="state.open"
    :roomId="route.params.roomId"
    :room="state.room"/>
  <div v-if="state.gameStart" class="countdown">
    <div class="counter">{{ state.count }}</div>
  </div>
  <GameRoomBackground/>
</template>
<style scoped>
  @import url('./game-room.css');
</style>
<script>
import GameRoomInfoChangeDialog from './game-room-setting/game-room-info-change-dialog.vue'
import GameRoomChat from './game-room-chat/game-room-chat.vue'
import GameRoomSetting from './game-room-setting/game-room-setting.vue'
import GameRoomWebcam from './game-room-webcam/game-room-webcam.vue'
import GameRoomBackground from './game-room-background/game-room-background.vue'

import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { ElMessage } from 'element-plus'
import { computed, reactive, onBeforeUnmount } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: "gameRoom",

  components: {
    GameRoomChat,
    GameRoomSetting,
    GameRoomWebcam,
    GameRoomInfoChangeDialog,
    GameRoomBackground,
  },

  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()

    const state = reactive({
      open: false,
      userList: [],
      chatList: [],
      room: computed(() => store.getters['root/myRoom']),
      profileImageURL: computed(() => store.state.root.profileImageURL),
      myUserName: computed(() => store.state.root.username),
      stompClient: null,
      gameStart: false,
      count: 5,
      gameSelected: null,
    })

    // [Func|dialog] 방 정보 수정 open
    const openDialog = () => {
      state.open = true
    }

    // [Func|dialog] 방 정보 수정 close
    const closeDialog = () => {
      state.open = false
    }

    // [Func|btn] Game Start 버튼 클릭 이벤트
    const gameStart = () => {
      if(state.stompClient && state.stompClient.connected) {
        const message = {
          userName: store.state.root.username,
          content: '',
          roomId: route.params.roomId,
          profileImageURL: state.profileImageURL,
          gameStart: true,
          roomInfoChange: false,
        }

        // 참여자에게 소켓으로 게임 시작 알림
        state.stompClient.send('/to/chat', JSON.stringify(message), {})

        // 게임 시작 소켓에 메시지 전송 -> 백엔드에서 게임 매니저 생성
        sendGameStart()

        if (state.room.game) {
          const roomInfo = {
              ...state.room,
              isActivate: false
            }
          store.dispatch('root/requestUpdateGameRoom', roomInfo)
          .then(res => {
            console.log(res)
            console.log('방정보가 정상적으로 변경되었습니다. 게임중: 입장 불가')

          })
          .catch(err => {
            ElMessage({
              type: 'error',
              message: '게임 시작에 문제가 발생했습니다.'
            })
          })
        }
      }
    }

    // [Func|sys] 방 정보가 수정된 어떠한 경우라도 시행
    const informGameRoomInfoChange = () => {
      if(state.stompClient && state.stompClient.connected) {
        const message = {
          userName: store.state.root.username,
          content: '',
          roomId: route.params.roomId,
          profileImageURL: state.profileImageURL,
          gameStart: false,
          roomInfoChange: true,
        }

        // 방정보 수정을 참여자에게 알림 -> 수신한 참여자는 서버에 방정보 업데이트 요청
        state.stompClient.send('/to/chat', JSON.stringify(message), {})
      }
    }

    // [Func|socket] 전체 소켓 연결 컨트롤
    const connectSocket = async () => {
      let socket = await new SockJS("/narang")
      state.stompClient = Stomp.over(socket)
      console.log('채팅 소켓')
      await state.stompClient.connect({}, () => {
          connectChatSocket() // 채팅 소켓
          connectMafiaStartSocket() // 게임 시작 소켓
        }
      )
    }

    // [Func|socket] 채킹 소켓 연결
    const connectChatSocket = () => {
      state.stompClient.subscribe(`/from/chat/${route.params.roomId}`, res => {
        console.log("채팅 연결됨, 메시지 받음. 내용: ", res.body)
        const message = JSON.parse(res.body)
        if (message.content) { // 일반 메시지 수신 -> 채팅창 추가
          state.chatList.push(message)
        } else if (message.roomInfoChange === true) { // 방정보 변경 수신 -> 업데이트 요청
          requestRoomInfo()
        } else if (message.gameStart === true) { // 게임 시작 수신 -> 카운트 다운 & 페이지 이동
          if (state.room.game) {
            state.gameStart = true
            countDown()
            setTimeout(() => {
              router.push({
                name: state.room.game,
                params: route.params.roomId
              })
            }, 5000)
          } else {
            ElMessage({
              type: 'error',
              message: '게임이 선택되지 않았습니다.'
            })
          }
        }
      })
    }


    // [Func|socket] 마피에 게임 시작 소켓 연결
    const connectMafiaStartSocket = () => {
      state.stompClient.subscribe(`/from/mafia/start/${route.params.roomId}`, res => {
        console.log('마피아 게임 시작 소켓 연결', res.body)
      })
    }


    const startCallmy = () => {
      state.stompClient.send(`/to/call/start/${route.params.roomId}`)
    }


    // [Func|socket] 채팅 send
    const sendChatMessage = (msg) => {
      if (state.stompClient && state.stompClient.connected && msg) {
        const message = {
          userName: store.state.root.username,
          content: msg,
          roomId: route.params.roomId,
          profileImageURL: state.profileImageURL,
          roomInfoChange: false,
          gameStart: false,
        }
        state.stompClient.send('/to/chat', JSON.stringify(message), {})
      }
    }


    // [Func|socket] 게임 시작 send
    const sendGameStart = () => {
      if (state.room.game === 'mafia') {
        state.stompClient.send(`/to/mafia/start/${route.params.roomId}`)
        console.log('마피아 게임 시작 send')
      } else if (state.room.game === 'callmy') {
        startCallmy()
        console.log('콜마이 게임 시작')
      } else {
        console.log('게임 미선택')
      }
    }


    // [Func|req] 방 정보 가져오기
    const requestRoomInfo = () => {
      store.dispatch('root/requestReadSingleGameRoom', route.params.roomId)
        .then(res => {
          if (res.data.room.game !== state.gameSelected) {
            state.gameSelected = res.data.room.game
            ElMessage({
              type: 'success',
              message: `${res.data.room.game}으로 게임이 변경되었습니다.`
            })
          }
          store.commit('root/setRoomInfo', res.data.room)
        })
        .catch(err => {
          ElMessage({
            type: 'error',
            message: '문제가 발생했습니다.'
          })
        })
    }


    // [Func|req] 내 정보 가져오기
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
          if (!res.data.user.room) { // 방에 속해있지 않으면 퇴장
            router.push({
              name: 'waitingRoom'
            })
          }
        })
        .catch(err => {
          ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
        })
    }


    // [Func|req] 유저 리스트 가져오기
    const requestUserList = () => {
      store.dispatch('root/requestReadUserList', route.params.roomId)
        .then(res => {
          state.userList = res.data.userList
        })
        .catch(err => {
          ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
        })
    }


    // [Func|req] 방 나가기 가져오기
    const leaveRoom = () => {
      informGameRoomInfoChange()
      store.dispatch('root/requestLeaveGameRoom', { roomId: state.room.roomId })
        .then(res => {
          ElMessage({
            type: 'success',
            message: '방에서 퇴장하셨습니다.'
          })
        })
        .catch(err => {
          ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
        })
    }


    // [Func|sys] 게임 시작 카운트 다운
    const countDown = () => {
      setTimeout(() => { state.count = 4 }, 1000)
      setTimeout(() => { state.count = 3 }, 2000)
      setTimeout(() => { state.count = 2 }, 3000)
      setTimeout(() => { state.count = 1 }, 4000)
    }


    //* Life Cycle *//
    onBeforeUnmount(() => { // vue 컴포넌트가 파괴되기 전에 시행 = 페이지 이동 감지
      if (!state.gameStart) {
        leaveRoom()
      }
    })


    //* created *//
    requestRoomInfo()
    requestMyInfo()
    requestUserList()
    connectSocket()

    return { state, route, openDialog, closeDialog, requestRoomInfo, sendChatMessage, informGameRoomInfoChange, gameStart, leaveRoom }
  }
}
</script>


