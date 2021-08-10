<template>
  <div class="mafia-main-container">
    <LeftSide
      class="mafia-left-side"
      :roomId="state.roomId" :stage="state.stage"/>
    <RightSide
      class="mafia-right-side"
      @sendGetRole="sendGetRole"
      @clickStartMission="clickStartMission"
      @clickLie="clickLie"
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
import { onMounted } from 'vue'

import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { computed, reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import '@tensorflow/tfjs'
import * as tmPose from '@teachablemachine/pose'


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
      myMissionNumber : null,
      myMissionName : null,
      myMissionKeepCnt : 0,
      myMissionSuccess : false,
      canMafiaVote : false,
      destinationUrl: 'https://localhost:8080/narang',
      // destinationUrl: '/narang',
      roleCardVisible: false,
      msg: '',
      userRole: {},
      surviver: {},
      time: [20000, 10000, 10000, 10000, 5000], // 0: 낮 자유 토론, 1: 낮 1차 투표, 2: 낮 2차 투표, 3: 밤, 4: 중간 결과 확인
      gameOver: false,
    })

    const URL = "https://teachablemachine.withgoogle.com/models/J7odkV8ms/";
    let model, myWebcam, labelContainer, maxPredictions, loopPredict;
    const poseEstimationInit = async() => {
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

        myWebcam = document.getElementById("myWebcam").childNodes[2];
        console.log("웹캠가져옴", myWebcam);
        console.log("미션 번호 : "+state.myMissionNumber);
        const { pose, posenetOutput } = await model.estimatePose(myWebcam);
        const prediction = await model.predict(posenetOutput);
        state.myMissionName = prediction[state.myMissionNumber].className
        console.log("너의 미션은?"+state.myMissionName);
        labelContainer = document.getElementById("mission-container");
        for (let i = 0; i < maxPredictions; i++) { // and class labels
            labelContainer.appendChild(document.createElement("div"));
        }
    }

    const loop = async(timestamp) => {
      // console.log("루프프레딕트:",loopPredict)
      await predict();
      if(loopPredict){
        // console.log("루프")
        loopPredict = window.requestAnimationFrame(loop);
      }

    }

    const predict = async() => {
        // Prediction #1: run input through posenet
        // estimatePose can take in an image, video or canvas html element
        const { pose, posenetOutput } = await model.estimatePose(myWebcam);
        // Prediction 2: run input through teachable machine classification model
        // console.log("프레딕트왔음")
        const prediction = await model.predict(posenetOutput);

        for (let i = 0; i < maxPredictions; i++) {
            const classPrediction = prediction[i].className + ": " + prediction[i].probability.toFixed(2);
            labelContainer.childNodes[i].innerHTML = classPrediction;
            if(prediction[state.myMissionNumber].probability.toFixed(2) >= 0.90 && !state.myMissionSuccess) state.myMissionKeepCnt++;
        }
        console.log(state.myMissionKeepCnt);
        if(state.myMissionKeepCnt >= 300) {
          state.myMissionKeepCnt = 0;
          console.log("미션 성공!");
          ElMessage.success(prediction[state.myMissionNumber].className + '하기 미션에 성공하였습니다!');
          state.myMissionSuccess = true;
          sendMafias();
          cancelAnimationFrame(loop);
          console.log("미션 성공 결과 : ", state.myMissionSuccess);
          if(loopPredict){ // 동작 인식 loop 멈춤
            window.cancelAnimationFrame(loopPredict);
            loopPredict = undefined;
          }
        }
        // console.log("여기서 프레딕션은??"+prediction);
    }

    // 동작 인식 시작 (mission 종류 없으면 작동 안 함)
    const clickStartMission = () => {
      if (state.myMissionNumber == null) ElMessage.error('역할 받기 전에는 안 됨.');
      else if(state.myMissionNumber == -1) ElMessage.success('시민이라서 미션 시작 안 되지롱');
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
          state.stompClient.subscribe(fromRoleUrl, (res) => {
            const result = JSON.parse(res.body)
            state.myRole = result.roleName
            store.state.root.mafiaManager.myRole = result.roleName;
            state.myMissionNumber = result.missionNumber;
            if(state.myRole === 'Mafia'){
              connectMafiasSocket() // 마피아끼리 소켓 연결하러 가기
            }
        })
    }

      // [Func|socket] 롤카드 배분 소켓 send
      const sendGetRole = () => {
        console.log("롤카드 배분 버튼 누름!")
        const toRoleUrl = `/to/mafia/role/${route.params.roomId}/${state.username}`
        state.stompClient.send(toRoleUrl)
        PopUpRoleCard()
      }

     // [Func|socket] 마피아끼리 소켓 연결
    const connectMafiasSocket = () => {
      const fromMafiasUrl = `/from/mafia/mafias/${route.params.roomId}`
      state.stompClient.subscribe(fromMafiasUrl, res => {
        if(res.body == 1) {
          state.canMafiaVote = true;
          console.log("모든 마피아들 미션 성공! 투표 가능!!");
        }
        else if(res.body == 0) {
          state.canMafiaVote = false;
          console.log("모든 마피아들이 미션 성공 실패! 투표 불가!!!")
        }
        else {
          state.canMafiaVote = false;
          console.log("아직 마피아 미션 집계 중입니다!");
        }
      })
    }

     // [Func|socket] 마피아끼리 소켓 send
    const sendMafias = () => {
      console.log("마피아 미션 성공 여부 소켓 send")
      const toMafiasUrl = `/to/mafia/mafias/${route.params.roomId}`
      const message = {
          username: store.state.root.mafiaManager.username, // 내 이름
          isMissionComplete: state.myMissionSuccess, // 미션 성공 여부
        }
      state.stompClient.send(toMafiasUrl, JSON.stringify(message), {})
    }

    // [Func|socket] 마피아 투표 소켓 연결
    const connectVoteSocket = async () => {
      console.log("마피아 투표 소켓 연결")
      const fromVoteUrl = `/from/mafia/vote/${route.params.roomId}`
      await state.stompClient.subscribe(fromVoteUrl, async res => {
        const result = JSON.parse(res.body)
        if (!state.gameOver) { // 게임이 끝나지 않은 경우에만 수신
          await getVoteResult(result) // 결과 해석
        }
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

  const clickLie=()=>{
    console.log("거짓말탐지기클릭")
    sendMafias();
  }

    // [Func|socket] players 소켓 send
    const sendPlayers = async () => {
      const toPlayersUrl = `/to/mafia/players/${route.params.roomId}`
      state.stompClient.send(toPlayersUrl)
    }

    // [Func|game] 투표 결과 해석 ; 1차 투표 이후, 2차 투표 이후, 밤 투표 이후 실행
    const getVoteResult = async (result) => {
      // 이전에 내가 한 투표 초기화
      store.state.root.mafiaManager.theVoted = null
      store.state.root.mafiaManager.isAgree = false;
      store.state.root.mafiaManager.secondVoteUsername = '';

      if (result.finished) { // 2차 or 밤 -> 게임 종료
        console.log('게임 종료! 결과:', result.msg)
        state.msg = `게임 종료! ${result.msg}! 정체 공개 -> ${result.roleString}`
        store.state.root.mafiaManager.stage = 'default'
        state.gameOver = true
      } else if (!result.completeVote && result.msg != ""){ // 1차 -> 2차
        if(result.msg == "투표가 진행 중입니다") {
          console.log('투표 진행중! 좀만 기달')
        } else {
          state.msg = `${result.msg}님이 선택되었습니다. 잠시후 최후반론과 최종투표가 진행됩니다.`

          // 5초 쉬고 낮 2차로 이동
          setTimeout(() => {
            goDay2(result.msg) // msg = secondVoteUsername, completeVote = false
          }, state.time[4]);
        }
      } else if (result.completeVote){ // 1차 -> 밤 or 2차 -> 밤
        if (result.msg === ""){ // 죽은 사람 안나오는 경우
          if (state.mafiaManager.stage === 'day1') { // 1차 -> 밤
            console.log('최다 득표자가 결정되지 않았습니다. 잠시후 밤이 됩니다.')
            state.msg = '최다 득표자가 결정되지 않았습니다. 잠시후 밤이 됩니다.'
          } else if (state.mafiaManager.stage === 'day2'){ // 2차 -> 밤
            console.log('휴,, 살리자는 의견이 더 많았습니다 다행이네요. 잠시후 밤이 됩니다.')
            state.msg = '휴,, 살리자는 의견이 더 많았습니다 다행이네요. 잠시후 밤이 됩니다.'
          }
        } else { // 죽은 사람이 나오는 경우 2차 -> 밤
          console.log(`${result.msg}님이 투표에 의해 죽었습니다`)
          state.msg = `${result.msg}님이 투표에 의해 죽었습니다`
          if (state.mafiaManager.username ===  result.msg) { // 죽은 사람이 나인 경우
            store.state.root.mafiaManager.isAlive = false
          }
        }

        // 5초 쉬고 다음으로 이동
        setTimeout(() => {
          nextStage(result)
        }, state.time[4]);
      }
    }

    // [Func|game] 다음 스테이지 이동
    const nextStage = async (result) => {
      await sendPlayers(); // 죽은 사람이 존재할 수 있으니 players 정보 다시 가져오기
      if(store.state.root.mafiaManager.stage !== "night") { // 낮 1차 or 낮 2차 -> 밤
        goNight()
      } else { // 밤 -> 낮 1차
        goDay()
      }
    }

    // [Func|game] 낮 자유 토론
    const goDay = async () => {
      // 상태 초기화, 메시지 변경
      store.state.root.mafiaManager.stage = "default";
      console.log(`낮이되었습니다. ${state.time[0]/1000}초간 토의 진행해주세요`)
      state.msg = `낮이되었습니다. ${state.time[0]/1000}초간 토의 진행해주세요`

      // 토론 후 이동
      setTimeout(() => {
        goDay1()
      }, state.time[0]);
    }

    // [Func|game] 낮 1차 투표
    const goDay1 = async () => {
      store.state.root.mafiaManager.stage = "day1";
      console.log(`낮 1차 투표 시간(${state.time[1]/1000}초)입니다. 미피아로 의심되는 사람을 투표해주세요.`)
      state.msg = `낮 1차 투표 시간(${state.time[1]/1000}초)입니다. 미피아로 의심되는 사람을 투표해주세요.`

      setTimeout(() => { // 투표하기
        sendVoteSocket();
      }, state.time[1])
    }

    // [Func|game] 낮 2차 투표
    const goDay2 = (secondVoteUsername) => {
      // 상태 변경
      store.state.root.mafiaManager.secondVoteUsername = secondVoteUsername;
      store.state.root.mafiaManager.stage = "day2";

      // 메시지 변경
      console.log(`${secondVoteUsername}님이 단두대에 올랐습니다.
        최후 변론(${state.time[2]/1000}초)을 듣고,  ${secondVoteUsername}님을 죽여야 한다면 찬성, 그렇지 않으면 반대를 눌러주세요`);
      state.msg = `${secondVoteUsername}님이 단두대에 올랐습니다.
        최후 변론(${state.time[2]/1000}초)을 듣고,  ${secondVoteUsername}님을 죽여야 한다면 찬성, 그렇지 않으면 반대를 눌러주세요`

      setTimeout(() => { // 투표하기
        sendVoteSocket();
      }, state.time[2]);
    }

    const goNight = async () => {
      // 상태 변경
      store.state.root.mafiaManager.stage = "night";
      if(state.myRole === 'Mafia') sendMafias();
      // 메시지 변경
      console.log(`밤(${state.time[3]/1000}초)이 되었습니다. 마피아는 고개를 들어주세요`)
      state.msg = `밤(${state.time[3]/1000}초)이 되었습니다. 마피아는 고개를 들어주세요`

      setTimeout(() => { // 투표하기
        sendVoteSocket();
      }, state.time[3])
    }

    const gameInit = async () => {
      console.log('gameInit', store.state.root.mafiaManager);

      // 상태 초기화
      store.state.root.mafiaManager.username = state.username
      store.state.root.mafiaManager.stage = "default";

      // 메시지 변경
      console.log(`잠시후 게임이 시작됩니다. 롤카드를 확인해주세요.`)
      state.msg = `잠시후 게임이 시작됩니다. 롤카드를 확인해주세요.`

      setTimeout(() => {
        goDay();
      }, state.time[4])
    }

    //* created *//
    const setGame = async () => {
        console.log("setGame 소켓 연결 전")
        await connectSocket()
        console.log("setGame 소켓 연결 후")
    }

    setGame();

    return { state, store, connectSocket, connectMafiasSocket, connectGetRoleSocket, sendGetRole, clickStartMission, sendPlayers}
  },
}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
