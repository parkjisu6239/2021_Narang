<template>
  <div class="mafia-main-container">
    <LeftSide
      class="mafia-left-side"
      :roomId="state.roomId"/>
    <RightSide
      class="mafia-right-side"
      @sendGetRole="sendGetRole"
      @clickStartMission="clickStartMission"/>
  </div>

  <MafiaRoleCard
  v-if="state.roleCardVisible"
  :myRole="state.myRole"
  @click="state.roleCardVisible = false"/>

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
import LeftSide from './left-side/left-side.vue'
import RightSide from './right-side/right-side.vue'
import MafiaRoleCard from './role-card/mafia-role-card.vue'

import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import '@tensorflow/tfjs';
import * as tmPose from '@teachablemachine/pose';


export default {
  name: 'mafia',

  components: {
    LeftSide,
    RightSide,
    MafiaRoleCard,
  },

  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()

    const state = reactive({
      radio: ref('day'),
      roomId: route.params.roomId,
      stompClient: null,
      username: localStorage.getItem('username'),
      myRole: null,
      myMission : null,
      myMissionKeepCnt : 0,
      isMissionComplete : false,
      destinationUrl: 'https://localhost:8080/narang',
      roleCardVisible: false,
      msg: '메시지',
      userList: null,
      userRole: {},
      surviver: {},
    })

    //
    const URL = "https://teachablemachine.withgoogle.com/models/J7odkV8ms/";
    let model, myWebcam, labelContainer, maxPredictions, loopPredict;
    async function poseEstimationInit() {
        const modelURL = URL + "model.json";
        const metadataURL = URL + "metadata.json";

        // load the model and metadata
        // Refer to tmImage.loadFromFiles() in the API to support files from a file picker
        // Note: the pose library adds a tmPose object to your window (window.tmPose)
        // model = await tmPose.load(posemeta, posemodel);
        model = await tmPose.load(modelURL, metadataURL);
        maxPredictions = model.getTotalClasses();

        loopPredict = window.requestAnimationFrame(loop); // 반복적으로 프레임 가져오기 위해

        myWebcam = document.getElementById("myWebcam");

        labelContainer = document.getElementById("mission-container");
        for (let i = 0; i < maxPredictions; i++) {
            labelContainer.appendChild(document.createElement("div"));
        }
    }

    // 반복적으로 프레임 가져오고 동작 인식 호출
    async function loop(timestamp) {
      console.log("루프프레딕트:",loopPredict)
      await predict();
      if(loopPredict){
        console.log("루프")
        loopPredict = window.requestAnimationFrame(loop);
      }

    }

    // 학습된 동작 비교
    async function predict() {
        // Prediction #1: run input through posenet
        // estimatePose can take in an image, video or canvas html element
        const { pose, posenetOutput } = await model.estimatePose(myWebcam);
        // Prediction 2: run input through teachable machine classification model
        const prediction = await model.predict(posenetOutput);

        for (let i = 0; i < maxPredictions; i++) {
            const classPrediction = prediction[i].className + ": " + prediction[i].probability.toFixed(2);
            labelContainer.childNodes[i].innerHTML = classPrediction;
            if(prediction[state.myMission].probability.toFixed(2) >= 0.90 && !state.isMissionComplete) state.myMissionKeepCnt++;
        }
        if(state.myMissionKeepCnt >= 300) {
          console.log("미션 성공!");
          ElMessage.success(prediction[state.myMission].className + '하기 미션에 성공하였습니다!');
          cancelAnimationFrame(loop);
          state.isMissionComplete = true;
          if(loopPredict){ // 동작 인식 loop 멈춤
            window.cancelAnimationFrame(loopPredict);
            loopPredict = undefined;
          }
        }
    }

    // 동작 인식 시작 (myMission 종류 없으면 작동 안 함)
    const clickStartMission = () => {
      if (state.myMission == null) ElMessage.error('역할 받기 전에는 안 됨.');
      else if(state.myMission == -1) ElMessage.success('시민이라서 미션 시작 안 되지롱');
      else{
        console.log("마피아니까 미션 가능")
        poseEstimationInit()
      }
    }

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
        const result = JSON.parse(res.body)
        state.myRole = result.roleName
        state.myMission = result.missionNumber;
        console.log('미션을 받았다!', result.missionNumber)
        // setUserRole(result.roleString) // 유저 롤 세팅
        // getVoteResult(result) // 결과 해석
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
          secondVoteusername: secondVoteusername, // 2차 투표 진행시 해당 유저의 이름
          isMissionComplete: state.isMissionComplete, // 마피아 미션 성공 시 true, 실패 시 false
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

    return { state, connectSocket, connectGetRoleSocket, sendGetRole, requestUserList, clickPlayer, clickStartMission}
  }

}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
