<template>
  <div class="mafia-main-container">
    <LeftSide
      class="mafia-left-side"
      :roomId="state.roomId" :stage="state.stage"/>
    <RightSide
      class="mafia-right-side"
      @sendGetRole="sendGetRole"
      @clickShowMission="clickShowMission"
      @clickLie="clickLie"
      :msg="state.msg"
      :isVoteTime="state.isVoteTime"
      :timer="state.timer/1000"
      />
  </div>

  <MafiaRoleCard
  v-if="state.roleCardVisible"
  :myRole="state.myRole"
  @click="state.roleCardVisible = false"/>

  <GameDialog v-if="state.gameOver">
    <GameOverContent
      :msg="state.msg"
      :result="state.gameOverResult"/>
  </GameDialog>

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
import GameDialog from './game-dialog/game-dialog.vue'
import GameOverContent from './game-dialog/game-over-content.vue'

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
    GameDialog,
    GameOverContent,
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
      destinationUrl: '/narang',
      poseUrl: 'https://teachablemachine.withgoogle.com/models/J7odkV8ms/',
      canMafiaVote : false,
      roleCardVisible: false,
      msg: '',
      userRole: {},
      surviver: {},
      time: [20000, 10000, 10000, 10000, 5000], // 0: 낮 자유 토론, 1: 낮 1차 투표, 2: 낮 2차 투표, 3: 밤, 4: 중간 결과 확인
      gameOver: false,
      isVoteTime: false,
      timer: 0,
      isDialogVisible: false,
      gameOverResult: '',
      model: null,
      myWebcam: null,
      labelContainer: null,
      maxPredictions: null,
      loopPredict: undefined,
    })

    const poseEstimationInit = async() => {
        console.log("동작 인식 시작!!!")
        const modelURL = state.poseUrl + "model.json";
        const metadataURL = state.poseUrl + "metadata.json";

        // load the model and metadata
        state.model = Object.freeze(await tmPose.load(modelURL, metadataURL));
        state.maxPredictions = state.model.getTotalClasses();

        state.loopPredict = window.requestAnimationFrame(loop); // 동작 인식 반복 시작

        state.myWebcam = document.getElementById("myWebcam").childNodes[2];
        const { pose, posenetOutput } = await state.model.estimatePose(state.myWebcam);
        const prediction = await state.model.predict(posenetOutput);
        store.state.root.mafiaManager.missonName = prediction[store.state.root.mafiaManager.missionNumber].className
        console.log("미션 번호 : ", store.state.root.mafiaManager.missionNumber);
        console.log("너의 미션은?", store.state.root.mafiaManager.missonName);
        // state.labelContainer = document.getElementById("mission-container");
        // for (let i = 0; i < state.maxPredictions; i++) {
        //     state.labelContainer.appendChild(document.createElement("div"));
        // }
    }

    const loop = async(timestamp) => {
      await predict();
      if(state.loopPredict){
        state.loopPredict = window.requestAnimationFrame(loop);
      }
    }

    const predict = async() => {
        // Prediction #1: run input through posenet
        // estimatePose can take in an image, video or canvas html element
        const { pose, posenetOutput } = await state.model.estimatePose(state.myWebcam);
        // Prediction 2: run input through teachable machine classification model
        const prediction = await state.model.predict(posenetOutput);

        for (let i = 0; i < state.maxPredictions; i++) {
            // const classPrediction = prediction[i].className + ": " + prediction[i].probability.toFixed(2);
            // state.labelContainer.childNodes[i].innerHTML = classPrediction;
            // 미션 동작인 경우 missionKeepCnt 카운트
            if(prediction[store.state.root.mafiaManager.missionNumber].probability.toFixed(2) >= 0.87 && !store.state.root.mafiaManager.missionSuccess) {
              if(store.state.root.mafiaManager.missionKeepCnt == 0) ElMessage.success("동작 인식 중입니다. 성공 전까지 해당 자세를 유지하세요.");
              else if(store.state.root.mafiaManager.missionKeepCnt == 150) ElMessage.success("동작 인식 25% 진행 중...");
              else if(store.state.root.mafiaManager.missionKeepCnt == 300) ElMessage.success("동작 인식 50% 진행 중...");
              else if(store.state.root.mafiaManager.missionKeepCnt == 450) ElMessage.success("동작 인식 75% 진행 중...");
              store.state.root.mafiaManager.missionKeepCnt++;
            }
            // 미션 동작이 아닌 경우 missionKeepCnt 초기화
            else{
              if(store.state.root.mafiaManager.missionKeepCnt > 0) ElMessage.error("그 동작이 아닙니다. 다른 자세로 다시 시도해주세요.");
              store.state.root.mafiaManager.missionKeepCnt = 0;
            }
        }
        console.log(store.state.root.mafiaManager.missionKeepCnt);
        if(store.state.root.mafiaManager.missionKeepCnt >= 600) {
          store.state.root.mafiaManager.missionKeepCnt = 0; // 동작 유지 cnt 0으로 초기화
          console.log("미션 성공!");
          ElMessage.success('[' + prediction[store.state.root.mafiaManager.missionNumber].className + '] 미션에 성공하였습니다!');
          store.state.root.mafiaManager.missionSuccess = true;
          sendMafias();
          stopMission();
          console.log("미션 성공 결과 : ", store.state.root.mafiaManager.missionSuccess);
        }
        // console.log("여기서 프레딕션은??"+prediction);
    }

    // 마피아 소켓 send 되는지 확인하려고 넣어놓음. 나중에 지울 예정
    const clickLie = () => {
      console.log("거짓말탐지기클릭")
      sendMafias();
    }

    // [Func|mafia] 마피아 미션 확인
    const clickShowMission = () => {
      console.log("미션 보기 클릭함 : ",store.state.root.mafiaManager.missonName);
      if (store.state.root.mafiaManager.missionNumber == null) ElMessage.error('역할 받기 전에는 못 보지롱');
      else if(store.state.root.mafiaManager.missionNumber == -1) ElMessage.success('시민이라서 미션 없지롱');
      else{
        if(store.state.root.mafiaManager.missonName == null) ElMessage.error('미션을 불러오고 있는 중입니다. 잠시 후 다시 시도하세요.')
        else ElMessage.success('당신의 미션은 ['+store.state.root.mafiaManager.missonName+'] 입니다. 밤이 되기 전에 해당 동작을 완료하세요.')
      }
    }

    // [Func|mafia] 동작 인식 시작 (마피아일 때만 작동)
    const startMission = () => {
      console.log("미션시작한다이자식아!")
      if(store.state.root.mafiaManager.myRole === 'Mafia') {
        store.state.root.mafiaManager.missionSuccess = false; // 미션 성공 여부 false로 초기화
        poseEstimationInit();
      }
    }

    // [Func|mafia] 동작 인식 종료
    const stopMission = () => {
        if(state.loopPredict){
          console.log("동작 인식 끝!!!")
          window.cancelAnimationFrame(state.loopPredict);
          state.loopPredict = undefined;
        }
    }

    // [Func|socket] 전체 소켓 연결 컨트롤
    const connectSocket = () => {
      console.log("1. 전체 소켓 연결 컨트롤")
      const socket =  new SockJS(state.destinationUrl)

      // 클라이언트 객체 생성
      state.stompClient = Stomp.over(socket)

      state.stompClient.connect({}, () => {
        console.log("2. 롤 배분 소켓 연결 전")
        connectGetRoleSocket() // 롤 배분 소켓 연결
        console.log("3-1. 롤 배분 소켓 연결 후")
        console.log("3-2. 롤 배분 전송 전")
        sendGetRole()
        console.log("3-3. 롤 배분 전송 후")
        console.log("4. 투표 소켓 연결 전")
        connectVoteSocket() // 투표 소켓 연결
        console.log("5. 투표 소켓 연결 후")
        console.log("6. player 소켓 연결 전")
        connectGetPlayerList()
        console.log("7. plater 소켓 연결 후")
        console.log("8. setGane sendPlayers 전")
        sendPlayers();
        console.log("9. setGame sendPlayers 후")
        console.log("setGame gameInit 전")
        gameInit();
        console.log("setGame gameInit 후")
        }
      )
    }

    // [Func|socket] 롤 배분 소켓 연결
    const connectGetRoleSocket =  () => {
      console.log("롤 배분 소켓 연결")
      const fromRoleUrl = `/from/mafia/role/${route.params.roomId}/${state.username}`
          state.stompClient.subscribe(fromRoleUrl, (res) => {
            const result = JSON.parse(res.body)
            state.myRole = result.roleName
            store.state.root.mafiaManager.myRole = result.roleName;
            store.state.root.mafiaManager.missionNumber = result.missionNumber;
            if(store.state.root.mafiaManager.myRole === 'Mafia'){
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
          isMissionComplete: store.state.root.mafiaManager.missionSuccess, // 미션 성공 여부
        }
      state.stompClient.send(toMafiasUrl, JSON.stringify(message), {})
    }

    // [Func|socket] 마피아 투표 소켓 연결
    const connectVoteSocket =  () => {
      console.log("마피아 투표 소켓 연결")
      const fromVoteUrl = `/from/mafia/vote/${route.params.roomId}`
      state.stompClient.subscribe(fromVoteUrl,  res => {
        const result = JSON.parse(res.body)
        if (!state.gameOver) { // 게임이 끝나지 않은 경우에만 수신
           getVoteResult(result) // 결과 해석
        }
      })
    }

    // [Func|socket] 마피아 투표 소켓 send
    const sendVoteSocket = () => {
      const toVoteUrl = `/to/mafia/vote/${route.params.roomId}`
      const message = {
          username: state.mafiaManager.stage === 'night' && state.mafiaManager.myRole === 'Citizen' ? null : state.mafiaManager.username, // 내 이름
          theVoted: state.mafiaManager.theVoted, // 내가 투표한 사람의 유저 네임
          stage: state.mafiaManager.stage, // day1, day2, night
          secondVoteUsername: state.mafiaManager.secondVoteUsername, // 2차 투표 진행시 해당 유저의 이름
          missionNumber: store.state.root.mafiaManager.missionNumber, // 현재 미션 넘버 (시민 : -1, 마피아 : 0 ~ 10)
        }

      // store에 내용 바꾸는거나중에 commit으로 바꾸기
      store.state.root.mafiaManager.theVoted = null
      state.isVoteTime = false

      state.stompClient.send(toVoteUrl, JSON.stringify(message), {})
    }

    // [Func|dialog] 롤카드 팝업 애니메이션
    const PopUpRoleCard = () => {
      state.roleCardVisible = true
    }

     // [Func|socket] 생존하는 players 소켓 연결
    const connectGetPlayerList =  () => {
      const fromPlayersUrl = `/from/mafia/players/${route.params.roomId}`
      state.stompClient.subscribe(fromPlayersUrl, res => {
        const result = JSON.parse(res.body)
        store.state.root.mafiaManager.players = result;
      })
    }

    // [Func|socket] players 소켓 send
    const sendPlayers = () => {
      const toPlayersUrl = `/to/mafia/players/${route.params.roomId}`
      state.stompClient.send(toPlayersUrl)
    }

    // [Func|game] 투표 결과 해석 ; 1차 투표 이후, 2차 투표 이후, 밤 투표 이후 실행
    const getVoteResult = (result) => {
      if (result.finished) { // 2차 or 밤 -> 게임 종료
        stopMission(); // 마피아 동작 인식 중지
        console.log('게임 종료! 결과:', result.msg)
        state.msg = `${result.msg}`
        state.gameOverResult = result.roleString
        store.state.root.mafiaManager.stage = 'default'
        state.gameOver = true
        gameOver()
      } else if (!result.completeVote && result.msg != ""){ // 1차 -> 2차
        if(result.msg == "투표가 진행 중입니다") {
          console.log('투표 진행중! 좀만 기달')
        } else {
          state.msg = `${result.msg}님이 선택되었습니다. 잠시후 최후반론과 최종투표가 진행됩니다.`
          state.isVoteTime = false

          // 5초 쉬고 낮 2차로 이동
          state.timer = state.time[4]
          setTimeout(() => {
            goDay2(result.msg) // msg = secondVoteUsername, completeVote = false
          }, state.time[4]);
        }
      } else if (result.completeVote){ // 1차 -> 밤 or 2차 -> 밤 or 밤 -> 낮
        stopMission(); // 마피아 동작 인식 중지
        if(store.state.root.mafiaManager.myRole === 'Mafia' && state.mafiaManager.stage === 'night'){ // 밤 -> 낮 될 때
          store.state.root.mafiaManager.missionNumber = result.missionNumber; // 마피아인 경우만 미션 번호 갱신
        }
        if (result.msg === ""){ // 죽은 사람 안나오는 경우
          if (state.mafiaManager.stage === 'day1') { // 1차 -> 밤
            console.log('최다 득표자가 결정되지 않았습니다. 잠시후 밤이 됩니다.')
            state.msg = '최다 득표자가 결정되지 않았습니다. 잠시후 밤이 됩니다.'
          } else if (state.mafiaManager.stage === 'day2'){ // 2차 -> 밤
            console.log('휴,, 살리자는 의견이 더 많았습니다 다행이네요. 잠시후 밤이 됩니다.')
            state.msg = '휴,, 살리자는 의견이 더 많았습니다 다행이네요. 잠시후 밤이 됩니다.'
          } else if (state.mafiaManager.stage === 'night') { // 밤 -> 낮
            console.log('아무도 죽지 않았습니다. 잠시후 아침이 됩니다.')
            state.msg = '아무도 죽지 않았습니다. 잠시후 아침이 됩니다.'
          }
        } else { // 죽은 사람이 나오는 경우 2차 -> 밤 or 밤 -> 낮
          if (state.mafiaManager.username ===  result.msg) { // 죽은 사람이 나인 경우
            store.state.root.mafiaManager.isAlive = false
            store.state.root.mafiaManager.onAudio = false
            store.publisher.publishAudio(store.state.root.mafiaManager.onAudio);
          }

          if (state.mafiaManager.stage === 'day2') {
            console.log(`${result.msg}님이 투표에 의해 죽었습니다. 잠시후 밤이 됩니다.`)
            state.msg = `${result.msg}님이 투표에 의해 죽었습니다. 잠시후 밤이 됩니다.`
          } else if (state.mafiaManager.stage === 'night') {
            console.log(`지난밤에 ${result.msg}님이 마피아에 의해 죽었습니다. 잠시후 아침이 됩니다.`)
            state.msg = `지난밤에 ${result.msg}님이 마피아에 의해 죽었습니다. 잠시후 아침이 됩니다.`
          }
        }

        nextStage(result)
      }
    }

    // [Func|game] 다음 스테이지 이동
    const nextStage =  (result) => {
       sendPlayers(); // 죽은 사람이 존재할 수 있으니 players 정보 다시 가져오기
      if(store.state.root.mafiaManager.stage !== "night") { // 낮 1차 or 낮 2차 -> 밤
        setTimeout(() => {
          goNight()
        }, state.time[4]);
      } else { // 밤 -> 낮
        setTimeout(() => {
          goDay()
        }, state.time[4]);
      }
    }

    // [Func|game] 낮 자유 토론
    const goDay = () => {
      console.log("토론시간이다이자식이ㅏ!!!!!!!!")
      startMission(); // 낮이 되면 마피아들 미션 새로 부여 받고 동작 인식 시작.

      // 상태 변경
      state.isVoteTime = false
      state.timer = state.time[0]
      store.state.root.mafiaManager.stage = "default";

      // 메시지 변경
      console.log(`낮이되었습니다. ${state.time[0]/1000}초간 토의 진행해주세요`)
      state.msg = `낮이되었습니다. ${state.time[0]/1000}초간 토의 진행해주세요`

      // 토론 후 이동
      setTimeout(() => {
        goDay1()
      }, state.time[0]);
    }

    // [Func|game] 낮 1차 투표
    const goDay1 = () => {
      // 상태 변경
      store.state.root.mafiaManager.stage = "day1";
      state.timer = state.time[1]
      state.isVoteTime = true

      // 메시지 변경
      console.log(`낮 1차 투표 시간(${state.time[1]/1000}초)입니다. 마피아로 의심되는 사람을 투표해주세요.`)
      state.msg = `낮 1차 투표 시간(${state.time[1]/1000}초)입니다. 마피아로 의심되는 사람을 투표해주세요.`

      setTimeout(() => { // 투표하기
        sendVoteSocket();
      }, state.time[1])
    }

    // [Func|game] 낮 2차 투표
    const goDay2 = (secondVoteUsername) => {
      // 상태 변경
      state.isVoteTime = true
      state.timer = state.time[2]
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

    const goNight =  () => {
      // 상태 변경
      state.isVoteTime = true
      state.timer = state.time[3]
      store.state.root.mafiaManager.stage = "night";
      if(store.state.root.mafiaManager.myRole === 'Mafia') sendMafias();

      // 메시지 변경
      console.log(`밤(${state.time[3]/1000}초)이 되었습니다. 마피아는 고개를 들어주세요`)
      if ( state.mafiaManager.myRole === 'Mafia') {
        state.msg = `밤(${state.time[3]/1000}초)이 되었습니다. 마피아는 고개를 들어주세요`
      } else {
        state.msg = `밤이 되었습니다. 안심하지 마십시오. 마피아는 당신을 지켜보고 있습니다`
      }

      setTimeout(() => { // 투표하기
        sendVoteSocket();
      }, state.time[3])
    }

    const gameInit =  () => {
      console.log('gameInit', store.state.root.mafiaManager);

      // 상태 초기화
      store.state.root.mafiaManager.username = state.username
      store.state.root.mafiaManager.stage = "default";
      state.timer = state.time[4]

      // 메시지 변경
      console.log(`잠시후 게임이 시작됩니다. 롤카드를 확인해주세요.`)
      state.msg = `잠시후 게임이 시작됩니다. 롤카드를 확인해주세요.`

      setTimeout(() => {
        goDay();
      }, state.time[4])
    }

    const gameOver = () => {
      // 상태 초기화
      store.state.root.mafiaManager.theVoted = null
      store.state.root.mafiaManager.stage = 'default'
      store.state.root.mafiaManager.players = null
      store.state.root.mafiaManager.secondVoteUsername = ''
      store.state.root.mafiaManager.myRole = ''
      store.state.root.mafiaManager.isAlive = true


      setTimeout(() => {
        router.push({
          name: 'gameRoom',
          params: {
            roomId: route.params.roomId,
          }
        })
      }, 5000);
    }

    const setGame =  () => {
      console.log("setGame 소켓 연결 전")
      connectSocket()
      console.log("setGame 소켓 연결 후")
    }

    //* created *//
    setGame();

    return { state, store, connectSocket, connectMafiasSocket, connectGetRoleSocket, sendGetRole, clickShowMission, sendPlayers}
  },
}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
