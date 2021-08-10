<template>
  <div class="mafia-main-container">
    <LeftSide
      class="mafia-left-side"
      :roomId="state.roomId" :stage="state.stage"/>
    <RightSide
      class="mafia-right-side"
      @sendGetRole="sendGetRole"
      @clickStartMission="clickStartMission"
      :msg="state.msg"
      />
  </div>

  <MafiaRoleCard
  v-if="state.roleCardVisible"
  :myRole="state.myRole"
  @click="state.roleCardVisible = false"/>

  <div v-if="store.state.root.mafiaManager.stage === 'night'">
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
    import { onBeforeMount, onMounted, onBeforeUpdate, onUpdated, onBeforeUnmount, onUnmounted, onActivated, onDeactivated, onErrorCaptured } from 'vue'

import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { computed, reactive, ref } from 'vue'
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
      mafiaManager: computed(() => store.getters['root/mafiaManager']),
      roomId: route.params.roomId,
      stompClient: null,
      username: localStorage.getItem('username'),
      myRole: null,
      myMission : null,
      myMissionKeepCnt : 0,
      myMissionSuccess : false,
      destinationUrl: 'https://localhost:8080/narang',
      roleCardVisible: false,
      msg: '',
      userRole: {},
      surviver: {},
      time: [10000, 10000, 10000, 5000],
    })

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

        // window.requestAnimationFrame(loop);
        loopPredict = window.requestAnimationFrame(loop);

        myWebcam = document.getElementById("myWebcam");
        console.log("에ㅜㅂㅁ둡캠가져옴",myWebcam);
        labelContainer = document.getElementById("mission-container");
        for (let i = 0; i < maxPredictions; i++) { // and class labels
            labelContainer.appendChild(document.createElement("div"));
        }
    }

    async function loop(timestamp) {
      console.log("루프 메서드")
      console.log("루프프레딕트:",loopPredict)
      await predict();
      if(loopPredict){
        console.log("루프")
        loopPredict = window.requestAnimationFrame(loop);
      }

    }

    async function predict() {
        // Prediction #1: run input through posenet
        // estimatePose can take in an image, video or canvas html element
        const { pose, posenetOutput } = await model.estimatePose(myWebcam);
        // Prediction 2: run input through teachable machine classification model
        const prediction = await model.predict(posenetOutput);

        for (let i = 0; i < maxPredictions; i++) {
            const classPrediction = prediction[i].className + ": " + prediction[i].probability.toFixed(2);
            labelContainer.childNodes[i].innerHTML = classPrediction;
            if(prediction[state.myMission].probability.toFixed(2) >= 0.90 && !state.myMissionSuccess) state.myMissionKeepCnt++;
        }
        if(state.myMissionKeepCnt >= 300) {
          console.log("미션 성공!");
          ElMessage.success(prediction[state.myMission].className + '하기 미션에 성공하였습니다!');
          cancelAnimationFrame(loop);
          state.myMissionSuccess = true;
          if(loopPredict){ // 동작 인식 loop 멈춤
            window.cancelAnimationFrame(loopPredict);
            loopPredict = undefined;
          }
        }
    }

    // 동작 인식 시작 (mission 종류 없으면 작동 안 함)
    const clickStartMission = () => {
      if (state.myMission == null) ElMessage.error('역할 받기 전에는 안 됨.');
      else if(state.myMission == -1) ElMessage.success('시민이라서 미션 시작 안 되지롱');
      else{
        console.log("마피아니까 미션 가능")
        poseEstimationInit()
      }
    }




    // [Func|socket] 전체 소켓 연결 컨트롤
    const connectSocket = async () => {
      console.log("1. 전체 소켓 연결 컨트롤")
      const socket = await new SockJS(state.destinationUrl)

      // 클라이언트 객체 생성
      state.stompClient = Stomp.over(socket)

      await state.stompClient.connect({}, async () => {
        console.log("2. 롤 배분 소켓 연결 전")
        await connectGetRoleSocket() // 롤 배분 소켓 연결
        console.log("3. 롤 배분 소켓 연결 후")
        console.log("4. 투표 소켓 연결 전")
        await connectVoteSocket() // 투표 소켓 연결
        console.log("5. 투표 소켓 연결 후")
        console.log("6. player 소켓 연결 전")
        await connectGetPlayerList()
        console.log("7. plater 소켓 연결 후")
        console.log("8. setGane sendPlayers 전")
        await sendPlayers();
        console.log("9. setGame sendPlayers 후")
        console.log("setGame gameInit 전")
        await gameInit();
        console.log("setGame gameInit 후")
        }
      )
    }

    // [Func|socket] 롤 배분 소켓 연결
    const connectGetRoleSocket = async () => {
      console.log("롤 배분 소켓 연결")
      const fromRoleUrl = `/from/mafia/role/${route.params.roomId}/${state.username}`
        await state.stompClient.subscribe(fromRoleUrl,async res => {
        const result = JSON.parse(res.body)
        state.myRole = result.roleName
        store.state.root.mafiaManager.myRole =  result.roleName;
        state.myMission = result.missionNumber;
        console.log('미션을 받았다!', result.missionNumber)
      })
    }

    // [Func|socket] 롤카드 배분 소켓 send
    const sendGetRole = () => {
      const toRoleUrl = `/to/mafia/role/${route.params.roomId}/${state.username}`
      state.stompClient.send(toRoleUrl)
      PopUpRoleCard()
    }

    // [Func|socket] 마피아 투표 소켓 연결
    const connectVoteSocket = async () => {
      console.log("마피아 투표 소켓 연결")
      const fromVoteUrl = `/from/mafia/vote/${route.params.roomId}`
      await state.stompClient.subscribe(fromVoteUrl, async res => {
        const result = JSON.parse(res.body)
        await setUserRole(result.roleString) // 유저 롤 세팅
        await getVoteResult(result) // 결과 해석
      })
    }

    // [Func|socket] 마피아 투표 소켓 send
    const sendVoteSocket = () => {
      const toVoteUrl = `/to/mafia/vote/${route.params.roomId}`
      const message = {
          username: store.state.root.mafiaManager.username, // 내 이름
          theVoted: store.state.root.mafiaManager.theVoted, // 내가 투표한 사람의 유저 네임
          stage: store.state.root.mafiaManager.stage, // day1, day2, night
          isAgree: store.state.root.mafiaManager.isAgree, // false : 살린다, true: 죽인다
          secondVoteusername: store.state.root.mafiaManager.secondVoteusername // 2차 투표 진행시 해당 유저의 이름
        }
      state.stompClient.send(toVoteUrl, JSON.stringify(message), {})
    }

    // [Func|dialog] 롤카드 팝업 애니메이션
    const PopUpRoleCard = () => {
      state.roleCardVisible = true
    }

     // [Func|socket] 생존하는 players 소켓 연결
    const connectGetPlayerList = async () => {
      const fromPlayersUrl = `/from/mafia/players/${route.params.roomId}`
      state.stompClient.subscribe(fromPlayersUrl, res => {
        const result = JSON.parse(res.body)
        store.state.root.mafiaManager.players = result;
      })
    }

    // [Func|socket] 마피아 투표 소켓 send
    const sendPlayers = async () => {
      const toPlayersUrl = `/to/mafia/players/${route.params.roomId}`
      state.stompClient.send(toPlayersUrl)
    }

    // [Func|sys] 유저 클릭 -> 투표
    const clickPlayer = (voted) => {
      // if (voted === state.username) { // 내가 나를 투표
      //   return
      // }
      // sendVoteSocket(voted, 'day1', null, null)
    }

    // [Func|sys] 유저의 역할 정보 저장 -> 최종 결과에서 직업 밝힐때 사용
    const setUserRole = async (roleString) => {
      if (roleString) {
          const userRole = []
          const userRoleList = roleString.split('&')
          userRoleList.forEach(ele => {
            userRole.push(ele.split(':'))
          })
          state.userRole = userRole
        }
    }

    // [Func|sys] 투표 결과 해석
    const getVoteResult = async (result) => {
      if (result.finished) { // 게임 종료
        console.log('게임 종료! 결과:', result.msg)
        state.msg = `게임 종료! 결과: ${result.msg}, ${result.roleString}`
        store.state.root.mafiaManager.stage = 'default'

        // 각자 직업 나오는것도 해야함
      } else if (!result.completeVote && result.msg != ""){
        if(result.msg == "투표가 진행 중입니다") {
          console.log('투표 진행중! 좀만 기달')
          state.msg = `투표 진행중! 좀만 기달`
        } else {
          goDay2(result.msg) // msg = secondVoteUsername, completeVote = false
        }
      } else if (result.completeVote){
        console.log('투표 종료! 결과:', result.msg) // 사람 이름 죽은 사람 이름 ""
        state.msg = `투표 종료! 결과: ${result.msg}`
        nextStage(result);
      }
    }


    const nextStage = async (result) => {
      await sendPlayers(); // 죽은 사람이 존재할 수 있으니 players 정보 다시 가져오기
      if(store.state.root.mafiaManager.stage == "day1") {
        console.log("투표 XXXXX 밤으로 가즈아")
        state.msg = `투표 XXXXX 밤으로 가즈아`
        setTimeout(() => {
          goNight()
        }, state.time[2])
      } else if(store.state.root.mafiaManager.stage == "day2") {
        // day2 결과 말해주기 , 역할 뭐였는지도 말해줘야하나?
        console.log(result.msg, " 가 투표에 의해 죽었습니다.")
        console.log("직업은 000 였습니다.")
        state.msg = `${result.msg} 가 투표에 의해 죽었습니다.`
        setTimeout(() => {
          goNight();
        }, state.time[2])
      } else if (store.state.root.mafiaManager.stage == "night") {
        store.state.root.mafiaManager.stage = "default";
        console.log("낮이되었다 100초간 토의 진행해주세요")
        state.msg = `낮이되었다 100초간 토의 진행해주세요`
        setTimeout(() => {
          // 투표하러 갈끄니까
          goDay1()
        }, state.time[0]);
      }
    }
    const goDay1 = async () => {
      store.state.root.mafiaManager.stage = "day1";
      store.state.root.mafiaStage = "day1";
      state.msg = `투표 시간입니다`
      setTimeout(() => {
        sendVoteSocket();
      }, state.time[0])
      store.state.root.mafiaManager.theVoted = null;
    }

    // [Func|game] Day2 로직 ; 20초간 낮 2차 투표 진행 한 후, 투표결과 일괄 전송
    const goDay2 = (secondVoteUsername) => {
      console.log(secondVoteUsername, " 이 단두대에 올랐습니다. 최후 변론 30초간 해주세요");
      state.msg = `${secondVoteUsername} 이 단두대에 올랐습니다. 최후 변론 30초간 해주세요`
      // 단두대 오른 대상자 설정
      store.state.root.mafiaManager.secondVoteUsername = secondVoteUsername;
      // 투표 상태 day2로 변경
      store.state.root.mafiaManager.stage = "day2";
      setTimeout(() => {
        sendVoteSocket();
      }, state.time[1]);
      // 초기값 설정
      store.state.root.mafiaManager.isAgree = false;
      // 초기값 설정
      store.state.root.mafiaManager.secondVoteUsername = '';
    }

    const goNight = async () => {
      console.log("밤이됩니다")
      state.msg = `밤이됩니다`
      store.state.root.mafiaManager.stage = "night";
      // 마피아끼리 말할 수 있고 투표 할 수 있게 된다.
      setTimeout(() => {
        sendVoteSocket();
      }, state.time[2])
      store.state.root.mafiaManager.theVoted = null;
    }

    const gameInit = async () => {
      console.log(store.state.root.mafiaManager);
      store.state.root.mafiaManager.username = state.username
      store.state.root.mafiaManager.stage = "default";
      console.log("회의 시작!!")
      state.msg = `회의 시작!!`
      setTimeout(() => {
        goDay1();
      }, state.time[0])
      store.state.root.mafiaManager.theVoted = null;
    }
    //* created *//
    const setGame = async () => {
        console.log("setGame 소켓 연결 전")
        await connectSocket()
        console.log("setGame 소켓 연결 후")
    }
    setGame();
    return { state, store, connectSocket, connectGetRoleSocket, sendGetRole, clickPlayer, clickStartMission, sendPlayers}
  },
}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
