<template>
  <h1>마피아 게임 방</h1>
  <el-radio-group v-model="state.radio">
    <el-radio-button label="day"></el-radio-button>
    <el-radio-button label="night"></el-radio-button>
  </el-radio-group>
  <el-button @click="sendGetRole">역할 받기</el-button>
  <div>사람들 역할, 사실 비밀임 {{ state.userRole }}</div>
  <div>죽었나 살았나 {{ state.surviver }} </div>
  <div>내이름 {{ state.username }}</div>
  <div>니역할 {{ state.myRole }}</div>
  <div>참여자 정보</div>
  <div class="playerList" v-for="player in state.userList" :key="player.userId">
    <div class="player" @click="clickPlayer(player.username)">
      <div>ID : {{ player.userId }}</div>
      <div>name : {{ player.username }}</div>
    </div>
  </div>

  <MafiaNotice
  :msg="state.msg"
  />

  <MafiaRoleCard
  v-if="state.roleCardVisible"
  :myRole="state.myRole"
  @click="state.roleCardVisible = false"
  />

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
import MafiaRoleCard from './mafia-role-card'
import MafiaNotice from './mafia-notice'

import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'mafia',

  components: {
    MafiaRoleCard,
    MafiaNotice,
  },

  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()

    const state = reactive({
      radio: ref('day'),
      stompClient: null,
      username: localStorage.getItem('username'),
      myRole: null,
      destinationUrl: 'https://localhost:8080/narang',
      roleCardVisible: false,
      msg: '메시지',
      userList: null,
      userRole: {},
      surviver: {},
    })

    // [Func|socket] 전체 소켓 연결 컨트롤
    const connectSocket = () => {
      const socket = new SockJS(state.destinationUrl)

      // 클라이언트 객체 생성
      state.stompClient = Stomp.over(socket)

      state.stompClient.connect({}, () => {
          connectGetRoleSocket() // 롤 배분 소켓 연결
          connectVoteSocket() // 투표 소켓 연결
        }
      )
    }

    // [Func|socket] 롤 배분 소켓 연결
    const connectGetRoleSocket = () => {
      const fromRoleUrl = `/from/mafia/role/${route.params.roomId}/${state.username}`
      state.stompClient.subscribe(fromRoleUrl, res => {
        console.log(res)
        state.myRole = res.body
        console.log('역할을 받았다!', res.body)
      })
    }

    // [Func|socket] 롤카드 배분 소켓 send
    const sendGetRole = () => {
      const toRoleUrl = `/to/mafia/role/${route.params.roomId}/${state.username}`
      state.stompClient.send(toRoleUrl)
      PopUpRoleCard()
    }

    // [Func|socket] 마피아 투표 소켓 연결
    const connectVoteSocket = () => {
      const fromVoteUrl = `/from/mafia/vote/${route.params.roomId}`
      state.stompClient.subscribe(fromVoteUrl, res => {
        const result = JSON.parse(res.body)
        setUserRole(result.roleString) // 유저 롤 세팅
        getVoteResult(result) // 결과 해석
      })
    }

    // [Func|socket] 마피아 투표 소켓 send
    const sendVoteSocket = (theVoted, stage, isAgree, secondVoteusername) => {
      const toVoteUrl = `/to/mafia/vote/${route.params.roomId}`
      const message = {
          username: state.username, // 내 이름
          theVoted: theVoted, // 내가 투표한 사람의 유저 네임
          stage: stage, // day1, day2, night
          isAgree: isAgree, // false : 살린다, true: 죽인다
          secondVoteusername: secondVoteusername // 2차 투표 진행시 해당 유저의 이름
        }
      state.stompClient.send(toVoteUrl, JSON.stringify(message), {})
    }

    // [Func|dialog] 롤카드 팝업 애니메이션
    const PopUpRoleCard = () => {
      state.roleCardVisible = true
    }

    // [Func|req] 유저 리스트 가져오기
    const requestUserList = () => {
      store.dispatch('root/requestReadUserList', route.params.roomId)
        .then(res => {
          state.userList = res.data.userList
          state.userList.forEach(user => {
            state.surviver[user.username] = 1
          })
        })
        .catch(err => {
          ElMessage(err)
        })
    }

    // [Func|sys] 유저 클릭 -> 투표
    const clickPlayer = (voted) => {
      // if (voted === state.username) { // 내가 나를 투표
      //   return
      // }
      sendVoteSocket(voted, 'day1', null, null)
    }

    // [Func|sys] 유저의 역할 정보 저장 -> 최종 결과에서 직업 밝힐때 사용
    const setUserRole = (roleString) => {
      if (roleString) {
          const userRole = []
          const userRoleList = roleString.split('&')
          userRoleList.forEach(ele => {
            userRolePair = ele.roleString.split(':')
            userRole.push(userRolePair)
          })
          state.userRole = userRole
        }
    }

    // [Func|sys] 투표 결과 해석
    const getVoteResult = (result) => {
      if (result.finished) { // 게임 종료
        console.log('게임 종료! 결과:', result.msg)
      } else if (result.completeVote){
        console.log('투표 종료! 결과:', result.msg)
      } else {
        console.log('투표 진행중! 좀만 기달', result.msg)
      }
    }

    //* created *//
    connectSocket()
    requestUserList()

    return { state, connectSocket, connectGetRoleSocket, sendGetRole, requestUserList, clickPlayer}
  }

}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
