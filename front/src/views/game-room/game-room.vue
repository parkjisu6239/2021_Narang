<template>
  <!-- 테스트용 지워도 무방 -->
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
      @leaveRoom="informGameRoomInfoChange"
      @changeGame="informGameRoomInfoChange"
      @openDialog="openDialog"
      :roomId="route.params.roomId"
      :room="state.room"/>
  </article>
  <GameRoomInfoChangeDialog
    @closeDialog="closeDialog"
    @changeGameRoomInfo="informGameRoomInfoChange"
    :open="state.open"
    :roomId="route.params.roomId"
    :room="state.room"/>

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
import { computed, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
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
    const state = reactive({
      open: false,
      userList: [],
      chatList: [],
      room: computed(() => store.getters['root/myRoom']),
      stompClient: null,
    })

    const openDialog = () => {
      state.open = true
    }

    const closeDialog = () => {
      state.open = false
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
        state.stompClient.send('/server', JSON.stringify(message), {})
      }
    }

    const sendMessage = (msg) => {
      if (state.stompClient && state.stompClient.connected && msg) {
        let profileImageURL = ''
        state.userList.forEach(user => {
          if (user.thumbnailURL && user.username === state.myUserName) {
            profileImageURL = 'https://localhost:8080/' + thumbnailURL
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
        state.stompClient.send('/server', JSON.stringify(message), {})
      }
    }

    const connectSocket = () => {
      let socket = new SockJS("https://localhost:8080/chat")
      state.stompClient = Stomp.over(socket)
      state.stompClient.connect({},
        frame => {
          state.stompClient.subscribe(`/client/${route.params.roomId}`, res => {
            console.log(res.body)
            const message = JSON.parse(res.body)
            console.log(message, '게임 인포 변경 중..')
            if (message.content) {
              state.chatList.push(message)
            } else if (message.roomInfoChange === true) {
              requestRoomInfo()
            } else if (message.gameStart === true) {
              if ( room.game ) {
                setTimeout(() => {
                  route.push({
                    name: room.game,
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
        })
        .catch(err => {
          ElMessage(err)
        })
    }

    requestRoomInfo()
    requestMyInfo()
    requestUserList()
    connectSocket()

    return { state, route, openDialog, closeDialog, requestRoomInfo, sendMessage, informGameRoomInfoChange }
  }
}
</script>


