<template>
  <div class="webcam-wrap" style="border-radius: 10px 0px 0px 10px">
    <div class="webcam-container">
      <button type="button" @click="poseEstimationInit()">Start</button>
      <div id="label-container"></div>
      <div
        :class="{
          'webcam-container': true,
          'under-four': state.subscribers.length <= 4}">
        <user-video :stream-manager="state.publisher" @click="updateMainVideoStreamManager(state.publisher) "/>
        <user-video
          v-for="sub in state.subscribers"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          @click="updateMainVideoStreamManager(sub)"/>
      </div>
    </div>
  </div>

</template>
<style scoped>
  @import url('game-room-webcam.css');
</style>

<script>
import $axios from 'axios'
import { computed, reactive } from 'vue'
import { OpenVidu, Subscriber } from 'openvidu-browser'
import { useStore } from 'vuex'
import UserVideo from './components/UserVideo'
import { ElMessage } from 'element-plus'
import '@tensorflow/tfjs';
import * as tmPose from '@teachablemachine/pose';
// import posemeta from './pose-model/metadata.json';
// import posemodel from './pose-model/model.json';

$axios.defaults.headers.post['Content-Type'] = 'application/json'
export default {
  components: {
		UserVideo,
	},
  props: {
    roomId: {
      type: Number
    }
  },
  setup(props, { emit }) {
    const OPENVIDU_SERVER_URL = "https://" + location.hostname + ":4443";
    const OPENVIDU_SERVER_SECRET = "MY_SECRET";
    const store = useStore();
    const state = reactive({
			OV: undefined,
			session: undefined,
			mainStreamManager: undefined,
			publisher: undefined,
			subscribers: [],
			mySessionId: computed(() => props.roomId),
			myUserName: computed(() => store.getters['root/username']),
      mode : computed(() => {
        return findMode();
      }),
    })
    const findMode = () => {
      let len = state.subscribers.length + 1;
      console.log("Asdasdasdasdasd")
      console.log(len)
      if(len == 1) return 1;
      else if(len <= 4) return 2;
      else return 3;
    }

    // const URL = "./pose-model/";
    const URL = "https://teachablemachine.withgoogle.com/models/J7odkV8ms/";
    let model, myWebcam, ctx, labelContainer, maxPredictions;

    async function poseEstimationInit() {
        const modelURL = URL + "model.json";
        const metadataURL = URL + "metadata.json";

        // load the model and metadata
        // Refer to tmImage.loadFromFiles() in the API to support files from a file picker
        // Note: the pose library adds a tmPose object to your window (window.tmPose)
        // model = await tmPose.load(posemeta, posemodel);
        model = await tmPose.load(modelURL, metadataURL);
        maxPredictions = model.getTotalClasses();

        window.requestAnimationFrame(loop);

        myWebcam = document.getElementById("myWebcam")
        labelContainer = document.getElementById("label-container");
        for (let i = 0; i < maxPredictions; i++) { // and class labels
            labelContainer.appendChild(document.createElement("div"));
        }
    }

    async function loop(timestamp) {
        await predict();
        window.requestAnimationFrame(loop);
    }

    let goal = 0, cnt = 0, finish = 0;
    async function predict() {
        // Prediction #1: run input through posenet
        // estimatePose can take in an image, video or canvas html element
        const { pose, posenetOutput } = await model.estimatePose(myWebcam);
        // Prediction 2: run input through teachable machine classification model
        const prediction = await model.predict(posenetOutput);

        for (let i = 0; i < maxPredictions; i++) {
            const classPrediction = prediction[i].className + ": " + prediction[i].probability.toFixed(2);
            labelContainer.childNodes[i].innerHTML = classPrediction;
            if(prediction[goal].probability.toFixed(2) >= 0.90 && !finish) cnt++;
        }
        // if(!finish) console.log(cnt);
        if(cnt >= 500 && finish == 0) {
          console.log("미션 성공!");
          ElMessage.success(prediction[goal].className + '하기 미션에 성공하였습니다!');
          finish = 1;
        }

    }

    const joinSession = () => {
			// --- Get an OpenVidu object ---
			state.OV = new OpenVidu();

			// --- Init a session ---
			state.session = state.OV.initSession();

			// On every new Stream received...
			state.session.on('streamCreated', ({ stream }) => {
				const subscriber = state.session.subscribe(stream);
				state.subscribers.push(subscriber);
			});

			// On every Stream destroyed...
			state.session.on('streamDestroyed', ({ stream }) => {
				const index = state.subscribers.indexOf(stream.streamManager, 0);
				if (index >= 0) {
					state.subscribers.splice(index, 1);
				}
			});

			// On every asynchronous exception...
			state.session.on('exception', ({ exception }) => {
				console.warn(exception);
			});


			// 'getToken' method is simulating what your server-side should do.
			// 'token' parameter should be retrieved and returned by your own backend
			getToken(state.mySessionId).then(token => {
        console.log('토큰 받음아아아아',token)
				state.session.connect(token, { clientData: state.myUserName })
					.then(() => {

						// --- Get your own camera stream with the desired properties ---
						let publisher = state.OV.initPublisher(undefined, {
							audioSource: undefined, // The source of audio. If undefined default microphone
							videoSource: undefined, // The source of video. If undefined default webcam
							publishAudio: true,  	// Whether you want to start publishing with your audio unmuted or not
							publishVideo: true,  	// Whether you want to start publishing with your video enabled or not
							resolution: '600x300',  // The resolution of your video
							frameRate: 30,			// The frame rate of your video
							insertMode: 'APPEND',	// How the video is inserted in the target element 'video-container'
							mirror: false       	// Whether to mirror your local video or not
						});

						state.mainStreamManager = publisher;
						state.publisher = publisher;
            store.publisher = publisher;
						state.session.publish(state.publisher);
					})
					.catch(error => {
						console.log('There was an error connecting to the session:', error.code, error.message);
					});
			});
		}

    const leaveSession = () => {
			// --- Leave the session by calling 'disconnect' method over the Session object ---
			if (state.session) state.session.disconnect();

			state.session = undefined;
			state.mainStreamManager = undefined;
			state.publisher = undefined;
			state.subscribers = [];
			state.OV = undefined;

			window.removeEventListener('beforeunload', leaveSession);
		}

		const updateMainVideoStreamManager = (stream) => {
			if (state.mainStreamManager === stream) return
			state.mainStreamManager = stream
		}

    const getToken = (mySessionId) => {
			return createSession(mySessionId).then(sessionId => createToken(sessionId));
		}

		const createSession = (sessionId) => {
			return new Promise((resolve, reject) => {
				$axios
					.post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions`, JSON.stringify({
						customSessionId: sessionId,
					}), {
						auth: {
							username: 'OPENVIDUAPP',
							password: OPENVIDU_SERVER_SECRET,
						},
					})
					.then(response => response.data)
					.then(data => resolve(data.id))
					.catch(error => {
						if (error.response.status === 409) {
							resolve(sessionId);
						} else {
							console.warn(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`);
							if (window.confirm(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`)) {
								location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`);
							}
							reject(error.response);
						}
					});
			});
		}

    const createToken = (sessionId) => {
			return new Promise((resolve, reject) => {
				$axios
					.post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`, {}, {
						auth: {
							username: 'OPENVIDUAPP',
							password: OPENVIDU_SERVER_SECRET,
						},
					})
					.then(response => response.data)
					.then(data => resolve(data.token))
					.catch(error => reject(error.response))
			});
		}

    joinSession()

    window.addEventListener('beforeunload', leaveSession)

    return { state, updateMainVideoStreamManager, findMode, poseEstimationInit }

  },
}
</script>
