<template>
  <article class="game-room-container">
    <section class="game-cam-chat-container">
      <GameRoomWebcam/>
      <GameRoomChat :roomId="route.params.roomId" :userList="state.userList"/>
    </section>
    <GameRoomSetting :roomId="route.params.roomId" @openDialog="openDialog"/>
  </article>
  <GameRoomInfoChangeDialog @closeDialog="closeDialog" :open="state.open" :roomId="route.params.roomId"/>

  <div>
    <img class="city" :src="require('@/assets/images/mafia/city.png')" alt="">
    <img class="mafia-neorang" :src="require('@/assets/images/mafia/mafia.png')" alt="">
    <div class="circle moon"></div>
    <div class="night-background"></div>
  </div>
  <!-- <div>
    <img class="land" :src="require('@/assets/images/mafia/land.png')" alt="">
    <img class="cloud1" :src="require('@/assets/images/mafia/cloud1.png')" alt="">
    <img class="cloud2" :src="require('@/assets/images/mafia/cloud2.png')" alt="">
    <img class="cloud3" :src="require('@/assets/images/mafia/cloud3.png')" alt="">
    <div class="circle sun"></div>
    <div class="day-background"></div>
  </div> -->

</template>
<style scoped>
  @import url('./game-room.css');
</style>
<script>
import GameRoomInfoChangeDialog from './game-room-setting/game-room-info-change-dialog.vue'
import GameRoomChat from './game-room-chat/game-room-chat.vue'
import GameRoomSetting from './game-room-setting/game-room-setting.vue'
import GameRoomWebcam from './game-room-webcam/game-room-webcam.vue'
import { ElMessage } from 'element-plus'
import { reactive } from '@vue/reactivity'
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
      room: {},
    })

    const openDialog = () => {
      state.open = true
    }

    const closeDialog = () => {
      state.open = false
    }

    const requestRoomInfo = () => {
      store.dispatch('root/requestReadSingleGameRoom', route.params.roomId)
        .then(res => {
          store.commit('root/setRoomInfo', res.data.room)
          state.room = res.data.room
        })
        .catch(err => {
          console.log(err)
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
          console.log(res.data.userList)
          state.userList = res.data.userList
        })
        .catch(err => {
          ElMessage(err)
        })
    }

    requestRoomInfo()
    requestMyInfo()
    requestUserList()

    return { state, openDialog, closeDialog, requestRoomInfo, route }
  }
}
</script>


