<template>
  <article :class="{'game-room-container': true}">
    <section class="game-cam-chat-container">
      <GameRoomWebcam :roomId="route.params.roomId"/>
      <GameRoomChat
        @sendMessage="sendMessage"
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
</template>
<style scoped>
  @import url('./game-room.css');
</style>
<script>
import GameRoomInfoChangeDialog from './game-room-setting/game-room-info-change-dialog.vue'
import GameRoomChat from './game-room-chat/game-room-chat.vue'
import GameRoomSetting from './game-room-setting/game-room-setting.vue'
import GameRoomWebcam from './game-room-webcam/game-room-webcam.vue'
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
      stompClient: null,
      gameStart: false,
      count: 5,
    })

    const openDialog = () => {
      state.open = true
    }

    const closeDialog = () => {
      state.open = false
    }

    const gameStart = () => {
      if(state.stompClient && state.stompClient.connected) {
        const message = {
          userName: store.state.root.username,
          content: '',
          roomId: route.params.roomId,
          profileImageURL: '',
          gameStart: true,
          roomInfoChange: false,
        }
        state.stompClient.send('/to/chat', JSON.stringify(message), {})
      }
    }

    const informGameRoomInfoChange = () => {
      if(state.stompClient && state.stompClient.connected) {
        const message = {
          userName: store.state.root.username,
          content: '',
          roomId: route.params.roomId,
          profileImageURL: '',
          gameStart: false,
          roomInfoChange: true,
        }
        state.stompClient.send('/to/chat', JSON.stringify(message), {})
      }
    }

    const sendMessage = (msg) => {
      if (state.stompClient && state.stompClient.connected && msg) {
        let profileImageURL = ''
        state.userList.forEach(user => {
          if (user.thumbnailURL && user.username === state.myUserName) {
            profileImageURL = 'https://0.0.0.0:8080/' + thumbnailURL
          }
        })

        const message = {
          userName: store.state.root.username,
          content: msg,
          roomId: route.params.roomId,
          profileImageURL: '',
          roomInfoChange: false,
          gameStart: false,
        }
        state.stompClient.send('/to/chat', JSON.stringify(message), {})
      }
    }

    const connectSocket = () => {
      let socket = new SockJS("https://localhost:8080/narang")
      state.stompClient = Stomp.over(socket)
      state.stompClient.connect({},
        frame => {
          state.stompClient.subscribe(`/from/chat/${route.params.roomId}`, res => {
            console.log(res.body)
            const message = JSON.parse(res.body)
            if (message.content) {
              state.chatList.push(message)
            } else if (message.roomInfoChange === true) {
              requestRoomInfo()
            } else if (message.gameStart === true) {
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
      )
    }

    const requestRoomInfo = () => {
      store.dispatch('root/requestReadSingleGameRoom', route.params.roomId)
        .then(res => {
          console.log(res, '방 정보')
          store.commit('root/setRoomInfo', res.data.room)
        })
        .catch(err => {
          ElMessage({
            type: 'error',
            message: '문제가 발생했습니다.'
          })
        })
    }

    const requestMyInfo = () => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          console.log(res, '내 정보')
          store.commit('root/setUserInfo', res.data.user)
          if (!res.data.user.room) { // 방에 속해있지 않으면 퇴장
            router.push({
              name: 'waitingRoom'
            })
          }
        })
        .catch(err => {
          ElMessage(err)
        })
    }

    const requestUserList = () => {
      store.dispatch('root/requestReadUserList', route.params.roomId)
        .then(res => {
          state.userList = res.data.userList
        })
        .catch(err => {
          ElMessage(err)
        })
    }

    const leaveRoom = () => {
      store.dispatch('root/requestLeaveGameRoom', { roomId: state.room.roomId })
        .then(res => {
          informGameRoomInfoChange()
          ElMessage({
            type: 'success',
            message: '방에서 퇴장하셨습니다.'
          })
        })
        .catch(err => {
          console.log(err)
        })
    }

    const countDown = () => {
      setTimeout(() => { state.count = 4 }, 1000)
      setTimeout(() => { state.count = 3 }, 2000)
      setTimeout(() => { state.count = 2 }, 3000)
      setTimeout(() => { state.count = 1 }, 4000)
      setTimeout(() => { state.count = 'Go!' }, 5000)
      console.log('끝')
    }


    onBeforeUnmount(() => { // vue 컴포넌트가 파괴되기 전에 시행 = 페이지 이동 감지
      if (!state.gameStart) {
        leaveRoom()
      }
    })

    window.addEventListener('beforeunload', function(e){ // 윈도우창 닫기 or 새로고침 전에 시행
      e.preventDefault()
      e.returnValue = ''
      window.alert('test')
      leaveRoom()
    })

    //* created *//
    requestRoomInfo()
    requestMyInfo()
    requestUserList()
    connectSocket()

    return { state, route, openDialog, closeDialog, requestRoomInfo, sendMessage, informGameRoomInfoChange, gameStart, leaveRoom }
  }
}
</script>


