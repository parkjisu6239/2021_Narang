<template>
  <div class="webcam-wrap" style="border-radius: 10px 0px 0px 10px">
    <div
      :class="{
        'webcam-container': true,
        'under-two': state.subscribers.length >= 1,
        'under-four': state.subscribers.length >= 2,
        'under-nine': state.subscribers.length >= 4,
      }">
      <user-video :stream-manager="state.publisher" @click="updateMainVideoStreamManager(state.publisher) "/>
      <user-video
        v-for="sub in state.subscribers"
        :key="sub.stream.connection.connectionId"
        :stream-manager="sub"
        @click="updateMainVideoStreamManager(sub)"/>
    </div>
    <button @click="test">적용</button>
    <button @click="test2">삭제</button>
  </div>

</template>
<style scoped>
  @import url('game-room-webcam.css');
</style>

<script>
import $axios from 'axios'
import { computed, reactive, onBeforeUnmount } from 'vue'
import { OpenVidu, Subscriber } from 'openvidu-browser'
import { useStore } from 'vuex'
import UserVideo from './components/UserVideo'

$axios.defaults.headers.post['Content-Type'] = 'application/json; charset=utf-8'
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
    const OPENVIDU_SERVER_URL = "https://" + location.hostname + ":443"
    const OPENVIDU_SERVER_SECRET = "NARANG_VIDU"
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
							resolution: '600x320',  // The resolution of your video
							frameRate: 30,			// The frame rate of your video
							insertMode: 'APPEND',	// How the video is inserted in the target element 'video-container'
							mirror: false,       	// Whether to mirror your local video or not

						})
						state.mainStreamManager = publisher
						state.publisher = publisher
            store.state.root.publisher = publisher
						state.session.publish(store.state.root.publisher)
					})
					.catch(error => {
						console.log('There was an error connecting to the session:', error.code, error.message);
					})

			})

      window.addEventListener('beforeunload', leaveSession)
		}

    const leaveSession = () => {
			// --- Leave the session by calling 'disconnect' method over the Session object ---
			if (state.session) {
        state.session.disconnect();
      }

			state.session = undefined;
			state.mainStreamManager = undefined;
			state.publisher = undefined;
			state.subscribers = [];
			state.OV = undefined;

      window.removeEventListener('beforeunload', leaveSession)
		}

		const updateMainVideoStreamManager = (stream) => {
      console.log("이게뭐야1")
			if (state.mainStreamManager === stream) return
      console.log("이게뭐야2")
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
							console.warn(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL} `);
							if (window.confirm(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL} \n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`)) {
								location.assign(`https://i5b205.p.ssafy.io:4443/accept-certificate`);
							}
							reject(error.response);
						}
					});
			});
		}

    const createToken = (sessionId) => {
			return new Promise((resolve, reject) => {
				$axios
					.post(`${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`, {
              "type": "WEBRTC",
              "role": "PUBLISHER",
              "kurentoOptions": {
                "videoMaxRecvBandwidth": 1000,
                "videoMinRecvBandwidth": 300,
                "videoMaxSendBandwidth": 1000,
                "videoMinSendBandwidth": 300,
                "allowedFilters": ["GStreamerFilter", "FaceOverlayFilter"]
              }
          },
          {
						auth: {
							username: 'OPENVIDUAPP',
							password: OPENVIDU_SERVER_SECRET,
						},
					})
					.then(response => {
            console.log("tqtqtqtqtq")
            console.log(response.data)
            return response.data}
            )
					.then(data => resolve(data.token))
					.catch(error => reject(error.response))
			});
		}

    //* Life Cycle *//
    // created
    joinSession()

    // beforeunmount
    onBeforeUnmount(() => {
      leaveSession()
    })
    const test = () => {
    console.log("여기좀봐!!")
    store.state.root.publisher.stream.applyFilter("GStreamerFilter", { command: "videobox fill=yellow top=-120 bottom=-120 left=-240 right=-240" })
            .then(() => {
                console.log("단두대 오른사람 필터 적용 완료")
            })
            .catch(error => {
                console.error(error);
            });


    }

    const test2 = () => {
      store.state.root.publisher.stream.removeFilter()
        .then(() => console.log("Filter removed"))
        .catch(error => console.error(error));
    }
    return { state, store,updateMainVideoStreamManager , test, test2}

  },
}
</script>
