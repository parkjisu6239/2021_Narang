<template>
  <div class="mafia-main-container">
    <LeftSide
      class="mafia-left-side"
      :roomId="state.roomId"
      :gameStart="state.gameStart"
      :playerNumber="state.userList.length"
      @sendAddPlayer="sendAddPlayer"/>
    <RightSide
      class="mafia-right-side"
      @sendGetRole="sendGetRole"
      @clickShowMission="clickShowMission"
      @clickLie="clickLie"
      @clickGuide="state.mafiaGuideVisible = true"
      :msg="state.msg"
      :isVoteTime="state.isVoteTime"
      :timer="state.timer/1000"
      :voteStatus="state.voteStatus"
      />
  </div>

  <MafiaRoleCard
    v-if="state.roleCardVisible && state.gameStart"
    :myRole="state.myRole"
    @click="state.roleCardVisible = false"/>

  <GameDialog v-if="state.gameOver">
    <GameOverContent
      :msg="state.msg"
      :result="state.gameOverResult"/>
  </GameDialog>

  <Dialog v-if="state.mafiaGuideVisible" @click="state.mafiaGuideVisible = false">
    <NafiaGuide/>
  </Dialog>

  <GameDialog
    v-if="state.clickMissionDialog"
    @click="state.clickMissionDialog = false">
    <MissionContent
      :missionName="store.state.root.mafiaManager.missionName"
      :missionNumber="store.state.root.mafiaManager.missionNumber"/>
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
import MissionContent from './game-dialog/mission-content.vue'
import Dialog from '@/views/common/dialog/dialog.vue'
import NafiaGuide from './nafia-guide/nafia-guide.vue'

import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

import { computed, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as tmPose from '@teachablemachine/pose'
import '@tensorflow/tfjs'


export default {
  name: 'mafia',

  components: {
    LeftSide,
    RightSide,
    MafiaRoleCard,
    GameDialog,
    GameOverContent,
    MissionContent,
    Dialog,
    NafiaGuide,
  },

  setup(props, { emit }) {
    const store = useStore()
    const route = useRoute()
    const router = useRouter()


    const state = reactive({
      room: computed(() => store.state.root.myRoom),
      mafiaManager: computed(() => store.getters['root/mafiaManager']),
      userId: computed(() => store.state.root.userId),
      roomId: route.params.roomId,
      stompClient: null,
      username: localStorage.getItem('username'),
      myRole: null,
      isMissionComplete : false,
      destinationUrl: '/narang',
      poseUrl: 'https://teachablemachine.withgoogle.com/models/Rafr6YSIZ/',
      roleCardVisible: false,
      msg: '',
      userRole: {},
      surviver: {},
      time: [60000, 10000, 10000, 20000, 5000], // 0: 낮 자유 토론, 1: 낮 1차 투표, 2: 낮 2차 투표, 3: 밤, 4: 중간 결과 확인
      gameOver: false,
      gameStart: false,
      isVoteTime: false,
      timer: 0,
      isDialogVisible: false,
      gameOverResult: '',
      model: null,
      myWebcam: null,
      missionProgress: undefined,
      missionMessage: undefined,
      maxPredictions: null,
      loopPredict: undefined,
      missionMessageCnt: 0,
      clickMissionDialog: false,
      voteStatus : {},
      userList: {},
      mafiaGuideVisible: false,
    })


    const poseEstimationInit = async() => {
        const modelURL = state.poseUrl + "model.json";
        const metadataURL = state.poseUrl + "metadata.json";

        // load the model and metadata
        state.model = Object.freeze(await tmPose.load(modelURL, metadataURL));
        state.maxPredictions = state.model.getTotalClasses();

        state.loopPredict = window.requestAnimationFrame(loop); // 동작 인식 반복 시작

        state.myWebcam = document.getElementById(state.username)
        const { pose, posenetOutput } = await state.model.estimatePose(state.myWebcam);
        const prediction = await state.model.predict(posenetOutput);
        store.state.root.mafiaManager.missionName = prediction[store.state.root.mafiaManager.missionNumber].className
        // console.log("미션 번호 : ", store.state.root.mafiaManager.missionNumber);
        // console.log("너의 미션은?", store.state.root.mafiaManager.missionName);
        state.missionProgress = document.getElementById("mission-progress");
        state.missionMessage = document.getElementById("mission-message");
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
            state.missionProgress.innerHTML = "미션 " + (store.state.root.mafiaManager.missionKeepCnt / 6).toFixed(0) + "% 진행 중...";
            // 미션 동작인 경우 missionKeepCnt 카운트
            if(prediction[store.state.root.mafiaManager.missionNumber].probability.toFixed(2) >= 0.83 && !store.state.root.mafiaManager.missionSuccess) {
              if(store.state.root.mafiaManager.missionKeepCnt == 0) state.missionMessage.innerHTML = "동작 인식 중입니다. 성공 전까지 해당 자세를 유지하세요.";
              store.state.root.mafiaManager.missionKeepCnt++;
            }
            else{ // 미션 동작이 아닌 경우 missionKeepCnt 초기화
              state.missionMessage.innerHTML = "그 동작이 아닙니다. 다른 자세로 다시 시도해주세요.";
              store.state.root.mafiaManager.missionKeepCnt = 0;
            }
        }
        // console.log(store.state.root.mafiaManager.missionKeepCnt);
        if(store.state.root.mafiaManager.missionKeepCnt >= 600) {
          store.state.root.mafiaManager.missionKeepCnt = 0; // 동작 유지 cnt 0으로 초기화
          console.log("미션 성공!");
          state.missionProgress.innerHTML = "미션 성공!";
          state.missionMessage.innerHTML = "[" + prediction[store.state.root.mafiaManager.missionNumber].className + "] 미션에 성공하였습니다!";
          store.state.root.mafiaManager.missionSuccess = true;
          sendMafias();
          stopMission();
          // console.log("미션 성공 결과 : ", store.state.root.mafiaManager.missionSuccess);
        }
        // console.log("여기서 프레딕션은??"+prediction);
    }


    // [Func|mafia] 마피아 미션 확인
    const clickShowMission = () => {
      if (store.state.root.mafiaManager.missionNumber == null) ElMessage.error('역할을 받은 후에 시도해주세요.');
      else if(store.state.root.mafiaManager.missionNumber == -1) ElMessage.success('시민은 미션이 없습니다.');
      else if(store.state.root.mafiaManager.missionSuccess == true) ElMessage.success('미션을 이미 완료했습니다.');
      else{
        if(store.state.root.mafiaManager.missionName == null) ElMessage.error('미션을 불러오고 있는 중입니다. 잠시 후 다시 시도하세요.')
        else state.clickMissionDialog = true;
      }
    }


    // [Func|mafia] 동작 인식 시작 (마피아일 때만 작동)
    const startMission = () => {
      if(store.state.root.mafiaManager.myRole === 'Mafia') {
        store.state.root.mafiaManager.missionSuccess = false; // 미션 성공 여부 false로 초기화
        poseEstimationInit();
      }
    }


    // [Func|mafia] 동작 인식 종료
    const stopMission = () => {
      store.state.root.mafiaManager.missionName = null;
        if(state.loopPredict){
          window.cancelAnimationFrame(state.loopPredict);
          state.loopPredict = undefined;
        }
    }


    // [Func|Item] 거짓말 탐지 아이템 활성화
    const clickLie = () => {
      if (state.mafiaManager.lierItem) { // 아이템이 남아 있는 경우
        store.state.root.mafiaManager.isLierItemActivate = true // 캠 누를 수 있게
        ElMessage({
          type: 'success',
          message: '거짓말 탐지 아이템이 활성화되었습니다. 10초 안에 정체가 궁금한 사람의 비디오를 눌러주세요.'
        })

        setTimeout(() => {
          store.state.root.mafiaManager.isLierItemActivate = false
          if (state.mafiaManager.lierItem) {
            ElMessage({
              type: 'success',
              message: '비디오를 클릭하지 않아서, 아이템이 비활성화 되었습니다. 다시 사용이 가능합니다.'
            })
          }
        }, 10000)
      } else {
        ElMessage({
          type: 'success',
          message: '거짓말 아이템을 이미 사용하셨습니다. 한번만 사용 가능합니다.'
        })
      }
    }

    // [Func|socket] 전체 소켓 연결 컨트롤
    const connectSocket = () => {
      const socket =  new SockJS(state.destinationUrl)

      // 클라이언트 객체 생성
      state.stompClient = Stomp.over(socket)

      state.stompClient.connect({}, () => {
        connectGetRoleSocket() // 롤 배분 소켓 연결
        connectVoteSocket() // 투표 소켓 연결
        connectGetPlayerList()
        connectCheckConnect()
        sendGetRole()
        sendPlayers()
        }
      )
    }

    // [Func|socket] 롤 배분 소켓 연결
    const connectGetRoleSocket =  () => {
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
        const toRoleUrl = `/to/mafia/role/${route.params.roomId}/${state.username}`
        state.stompClient.send(toRoleUrl)
        PopUpRoleCard()
      }

     // [Func|socket] 마피아끼리 소켓 연결
    const connectMafiasSocket = () => {
      const fromMafiasUrl = `/from/mafia/mafias/${route.params.roomId}`
      state.stompClient.subscribe(fromMafiasUrl, res => {
        if(res.body == 1) {
          store.state.root.mafiaManager.canMafiaVote = true;
        }
        else if(res.body == 0) {
          store.state.root.mafiaManager.canMafiaVote = false;
        }
        else {
          store.state.root.mafiaManager.canMafiaVote = false;
        }
      })
    }

     // [Func|socket] 마피아끼리 소켓 send
    const sendMafias = () => {
      const toMafiasUrl = `/to/mafia/mafias/${route.params.roomId}`
      const message = {
          username: store.state.root.mafiaManager.username, // 내 이름
          isMissionComplete: store.state.root.mafiaManager.missionSuccess, // 미션 성공 여부
        }
      state.stompClient.send(toMafiasUrl, JSON.stringify(message), {})
    }

    // [Func|socket] 마피아 투표 소켓 연결
    const connectVoteSocket =  () => {
      const fromVoteUrl = `/from/mafia/vote/${route.params.roomId}`
      state.stompClient.subscribe(fromVoteUrl,  res => {
        const result = JSON.parse(res.body)
        if (!state.gameOver) { // 게임이 끝나지 않은 경우에만 수신
          state.voteStatus = result.voteStatus;
          getVoteResult(result) // 결과 해석
        }
      })
    }


    const connectCheckConnect = () => {
      state.stompClient.subscribe(`/from/mafia/checkConnect/${route.params.roomId}`, res => {
        sendGetRole()
        sendPlayers()
        state.gameStart = true
        gameInit()
        setTimeout(() => {
          goDay();
        }, state.time[4])
      })
    }


    const sendAddPlayer = () => {
      const username = localStorage.getItem('username')
      setTimeout(() => {
        state.stompClient.send(`/to/mafia/addPlayer/${route.params.roomId}`, JSON.stringify(username), {})
      }, 2000)
    }


    // [Func|socket] 마피아 투표 소켓 send
    const sendVoteSocket = () => {
      const toVoteUrl = `/to/mafia/vote/${route.params.roomId}`
      const message = {
          username: store.state.root.mafiaManager.stage === 'night' && store.state.root.mafiaManager.myRole === 'Citizen' ? null : store.state.root.mafiaManager.username, // 내 이름
          theVoted: store.state.root.mafiaManager.theVoted, // 내가 투표한 사람의 유저 네임
          stage: store.state.root.mafiaManager.stage, // day1, day2, night
          secondVoteUsername: store.state.root.mafiaManager.secondVoteUsername, // 2차 투표 진행시 해당 유저의 이름
        }

      // store에 내용 바꾸는거나중에 commit으로 바꾸기
      store.state.root.mafiaManager.theVoted = null;
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
        if(store.state.root.mafiaManager.myRole === 'Mafia'){
          sendMafias();
          state.missionProgress.innerHTML = "";
          state.missionMessage.innerHTML = "";
        }
        state.msg = `${result.msg}`
        state.gameOverResult = result.roleString
        store.state.root.mafiaManager.stage = 'default'
        state.gameOver = true
        gameOver()
      } else if (!result.completeVote && result.msg != ""){ // 1차 -> 2차
        if(result.msg != "투표가 진행 중입니다") {
          state.msg = `${result.msg}님이 선택되었습니다. \n잠시후 최후반론과 최종투표가 진행됩니다.`
          state.isVoteTime = false
          state.timer = state.time[4] // 5초 쉬고 낮 2차로 이동
          setTimeout(() => {
            state.voteStatus = {} // 다음으로 넘어갈 때 비우기
            goDay2(result.msg) // msg = secondVoteUsername, completeVote = false
          }, state.time[4]);
        }
      } else if (result.completeVote){ // 1차 -> 밤 or 2차 -> 밤 or 밤 -> 낮
        stopMission(); // 마피아 동작 인식 중지
        if(store.state.root.mafiaManager.myRole === 'Mafia'){
          state.missionProgress.innerHTML = "";
          state.missionMessage.innerHTML = "";
          sendMafias();
          if(state.mafiaManager.stage === 'night') // 밤 -> 낮 될 때만 미션 번호 갱신
            store.state.root.mafiaManager.missionNumber = result.missionNumber;
        }

        if (result.msg === ""){ // 죽은 사람 안나오는 경우
          if (state.mafiaManager.stage === 'day1') { // 1차 -> 밤
            state.msg = '최다 득표자가 결정되지 않았습니다. \n잠시후 밤이 됩니다.'
          } else if (store.state.root.mafiaManager.stage === 'day2'){ // 2차 -> 밤
            state.msg = '휴,, 살리자는 의견이 더 많았습니다 다행이네요. \n잠시후 밤이 됩니다.'
          } else if (store.state.root.mafiaManager.stage === 'night') { // 밤 -> 낮
            state.msg = '아무도 죽지 않았습니다. \n잠시후 아침이 됩니다.'
          }
        } else { // 죽은 사람이 나오는 경우 2차 -> 밤 or 밤 -> 낮
          if (store.state.root.mafiaManager.username ===  result.msg) { // 죽은 사람이 나인 경우
            store.state.root.mafiaManager.isAlive = false
            store.state.root.mafiaManager.onAudio = false
            store.state.root.publisher.publishAudio(store.state.root.mafiaManager.onAudio)
          }
          if (store.state.root.mafiaManager.stage === 'day2') {
            state.msg = `${result.msg}님이 투표에 의해 죽었습니다. \n잠시후 밤이 됩니다.`
          } else if (state.mafiaManager.stage === 'night') {
            state.msg = `지난밤에 ${result.msg}님이 마피아에 의해 죽었습니다. \n잠시후 아침이 됩니다.`
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
          state.voteStatus = {} // 다음으로 넘어갈 때 비우기
          goNight()
        }, state.time[4]);
      } else { // 밤 -> 낮
        setTimeout(() => {
          store.state.root.mafiaManager.canMafiaVote = false;
          goDay()
        }, state.time[4]);
      }
    }

    // [Func|game] 낮 자유 토론
    const goDay = () => {
      startMission(); // 낮이 되면 마피아들 미션 새로 부여 받고 동작 인식 시작.

      // 상태 변경
      state.isVoteTime = false
      state.timer = state.time[0]
      store.state.root.mafiaManager.stage = "default";

      // 메시지 변경
      state.msg = `낮이되었습니다. \n${state.time[0]/1000}초간 토의 진행해주세요`

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
      state.msg = `낮 1차 투표 시간(${state.time[1]/1000}초)입니다. \n마피아로 의심되는 사람을 투표해주세요.`

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
      state.msg = `${secondVoteUsername}님이 단두대에 올랐습니다. \n최후 변론(${state.time[2]/1000}초)을 듣고 죽여야 한다면 찬성, 그렇지 않으면 반대를 눌러주세요`

      setTimeout(() => { // 투표하기
        sendVoteSocket();
      }, state.time[2]);
    }

    const goNight =  () => {
      // 상태 변경
      state.isVoteTime = true
      store.state.root.mafiaManager.secondVoteUsername = '';
      state.timer = state.time[3]
      store.state.root.mafiaManager.stage = "night";


      // 메시지 변경
      if ( store.state.root.mafiaManager.myRole === 'Mafia') {
        state.msg = `밤(${state.time[3]/1000}초)이 되었습니다. \n마피아는 고개를 들어주세요`
      } else {
        state.msg = `밤이 되었습니다. 안심하지 마십시오. \n마피아는 당신을 지켜보고 있습니다`
      }

      setTimeout(() => { // 투표하기
        sendVoteSocket();
      }, state.time[3])
    }

    const gameInit =  () => {

      // 상태 초기화
      store.state.root.mafiaManager.username = state.username
      store.state.root.mafiaManager.stage = "default";
      store.state.root.mafiaManager.theVoted = null;
      store.state.root.mafiaManager.secondVoteUsername = ''
      store.state.root.mafiaManager.isAlive = true
      state.timer = state.time[4]

      // 메시지 변경
      state.msg = `잠시후 게임이 시작됩니다. \n롤카드를 확인해주세요.`

    }

    const gameOver = () => {
      // 상태 초기화
            setTimeout(() => {
              router.push({
                name: 'gameRoom',
                params: {
                  roomId: route.params.roomId,
                }
              })
            }, 5000);
      store.state.root.mafiaManager.theVoted = null
      store.state.root.mafiaManager.stage = 'default'
      store.state.root.mafiaManager.players = []
      store.state.root.mafiaManager.secondVoteUsername = ''
      store.state.root.mafiaManager.myRole = ''
      store.state.root.mafiaManager.isAlive = true
      store.state.root.mafiaManager.lierItem = true
      store.state.root.mafiaManager.missionNumber = null
      store.state.root.mafiaManager.missionName = null
      store.state.root.mafiaManager.missionSuccess = false
      state.gameStart = false
      if(state.missionProgress !== undefined){
        state.missionProgress.innerHTML = "";
        state.missionMessage.innerHTML = "";
      }
      if (state.stompClient !== null) {
          state.stompClient.disconnect()
      }

      if (state.room.ownerId === state.userId) requestUpdateRoomInfo()

    }


    const requestUserList = () => {
      store.dispatch('root/requestReadUserList', route.params.roomId)
        .then(res => {
          state.userList = res.data.userList
          connectSocket()
        })
        .catch(err => {
          ElMessage(err)
        })
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
         const userInfo = {
            email: res.data.user.email,
            username: res.data.user.username,
            profileImageURL: res.data.user.thumbnailUrl,
            userId: res.data.user.userId,
          }
          store.commit('root/setUserInfo', userInfo)
        })
        .catch(err => {
          ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
        })
    }


    const requestUpdateRoomInfo = () => {
      const roomInfo = {
        ...state.room,
        game: null,
        isActivate: true,
      }

      store.dispatch('root/requestUpdateGameRoom', roomInfo)
        .then(res => {
          console.log('방정보가 정상적으로 변경되었습니다. 입장 가능')
        })
        .catch(err => {
          console.log(err)
        })
    }

    requestRoomInfo()
    requestUserList()
    requestMyInfo()


    return { state, store, connectSocket, connectMafiasSocket, connectGetRoleSocket, sendGetRole, clickShowMission, sendPlayers, clickLie, sendAddPlayer }
  },
}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
