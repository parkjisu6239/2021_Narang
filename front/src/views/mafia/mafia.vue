<template>
  <h1>마피아 게임 방</h1>
  <el-radio-group v-model="state.radio">
    <el-radio-button label="day"></el-radio-button>
    <el-radio-button label="night"></el-radio-button>
  </el-radio-group>
  <el-button @click="sendGetRole">역할 받기</el-button>

  <div>내이름 {{ state.username }}</div>
  <div>내역할 {{ state.myRole }}</div>


  <div v-if="state.radio === 'night'">
    <img class="city" :src="require('@/assets/images/mafia/city.png')" alt="">
    <img class="mafia-neorang" :src="require('@/assets/images/mafia/mafia.png')" alt="">
    <div class="circle moon"></div>
    <div class="night-background"></div>
  </div>
  <div v-else>
    <img class="land" :src="require('@/assets/images/mafia/land.png')" alt="">
    <img class="cloud1" :src="require('@/assets/images/mafia/cloud1.png')" alt="">
    <img class="cloud2" :src="require('@/assets/images/mafia/cloud2.png')" alt="">
    <img class="cloud3" :src="require('@/assets/images/mafia/cloud3.png')" alt="">
    <div class="circle sun"></div>
    <div class="day-background"></div>
  </div>
</template>

<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'mafia',

  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()

    const state = reactive({
      radio: ref('day'),
      stompClient: null,
      mafiaMessageList: [],
      username: localStorage.getItem('username'),
      myRole: null,
      destinationUrl: 'https://localhost:8080/narang'
    })

    // 전체 소켓 연결; 최상위 연결
    const connectSocket = () => {
      const socket = new SockJS(state.destinationUrl)

      // 클라이언트 객체 생성
      state.stompClient = Stomp.over(socket)

      // 전체 연결
      state.stompClient.connect({}, () => {
          connectGetRoleSocket()
        }
      )
    }

    // 롤 배분 소켓 연결
    const connectGetRoleSocket = () => {
    const getFromRoleUrl = `/from/mafia/role/${route.params.roomId}/${state.username}`
      state.stompClient.subscribe(getFromRoleUrl, res => {
        console.log(res)
        state.myRole = res.body
        console.log('역할을 받았다!', res.body)
      })
    }

    // 롤카드 배분 소켓 send
    const sendGetRole = () => {
      const getToRoleUrl = `/to/mafia/role/${route.params.roomId}/${state.username}`
      state.stompClient.send(getToRoleUrl)
    }

    //* created *//
    connectSocket()

    return { state, connectSocket, connectGetRoleSocket, sendGetRole}
  }

}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
