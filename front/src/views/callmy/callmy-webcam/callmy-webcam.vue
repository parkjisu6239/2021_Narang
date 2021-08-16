<template>
  <div class="callmy-left-top-container">
    <div
      class="callmy-all-video-list">
      <UserVideo
        :isPlayer="false"
        :stream-manager="state.publisher"
        :startDetection="false"
        @click="updateMainVideoStreamManager(state.publisher) "/>
      <UserVideo
        v-for="sub in state.subscribers"
        :isPlayer="false"
        :key="sub.stream.connection.connectionId"
        :startDetection="false"
        :stream-manager="sub"
        @click="updateMainVideoStreamManager(sub)"/>
    </div>
  </div>
  <div class="callmy-left-bottom-container">
    <div v-if="gameStart && roundStart" class="callmy-round-info-container">
      <div>
        <span class="callmy-title-round">{{ state.callmyManager.round }} Round!</span> {{ state.callmyManager.nowPlayUsers[0].username }}<span class="callmy-title-vs">VS</span>{{ state.callmyManager.nowPlayUsers[1].username }}
      </div>
    </div>
    <div class="callmy-now-play-video-container">
      <div v-if="gameStart && roundStart" class="callmy-now-play-video-list">
        <UserVideo
          :isPlayer="true"
          :startDetection="startDetection"
          :stream-manager="state.publisher"/>
        <UserVideo
          v-for="sub in state.subscribers"
          :key="sub.stream.connection.connectionId"
          :isPlayer="true"
          :startDetection="startDetection"
          :stream-manager="sub"/>
      </div>
      <div v-else-if="!gameStart">
        <h1>아직 게임 시작 전입니다. {{ state.joinedPlayerNumbers }} / {{ playerNumbers }}</h1>
      </div>
      <div v-else>
        <h1>잠시 후, 다음 라운드가 시작됩니다.</h1>
      </div>
    </div>

  </div>
</template>
<style>
  @import url('./callmy-webcam.css');
</style>
<script>
import $axios from 'axios'
import { computed, reactive, onBeforeUnmount, watch } from 'vue'
import { OpenVidu } from 'openvidu-browser'
import { useStore } from 'vuex'
import UserVideo from './components/UserVideo.vue'

$axios.defaults.headers.post['Content-Type'] = 'application/json'

export default {
  components: {
		UserVideo,
	},

  props: {
    roomId: {
      type: String,
    },
    socketConnected: {
      type: Boolean,
    },
    gameStart: {
      type: Boolean,
    },
    roundStart: {
      type: Boolean,
    },
    playerNumbers: {
      type: Number,
    },
    startDetection: {
      type: Boolean,
    },
  },

  setup(props, { emit }) {
    const OPENVIDU_SERVER_URL = "https://" + location.hostname + ":443"
    const OPENVIDU_SERVER_SECRET = "NARANG_VIDU"
    const store = useStore()

    const state = reactive({
			OV: undefined,
			session: undefined,
			mainStreamManager: undefined,
			publisher: undefined,
			subscribers: [],
			mySessionId: computed(() => props.roomId),
			myUserName: computed(() => store.getters['root/username']),
      callmyManager: computed(() => store.state.root.callmyManager),
      joinedPlayerNumbers: 0,
    })

    const joinSession = () => {
			// --- Get an OpenVidu object ---
			state.OV = new OpenVidu()

			// --- Init a session ---
			state.session = state.OV.initSession()

			// On every new Stream received...
			state.session.on('streamCreated', ({ stream }) => {
				const subscriber = state.session.subscribe(stream)
				state.subscribers.push(subscriber)
        state.joinedPlayerNumbers++
			})

			// On every Stream destroyed...
			state.session.on('streamDestroyed', ({ stream }) => {
				const index = state.subscribers.indexOf(stream.streamManager, 0)
        state.joinedPlayerNumbers--
        if (index >= 0) {
					state.subscribers.splice(index, 1)
				}
			})

			// On every asynchronous exception...
			state.session.on('exception', ({ exception }) => {
				console.warn(exception)
			})

			// 'getToken' method is simulating what your server-side should do.
			// 'token' parameter should be retrieved and returned by your own backend
			getToken(state.mySessionId)
        .then(token => {
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
                mirror: true       	// Whether to mirror your local video or not
              })
              state.mainStreamManager = publisher
              state.publisher = publisher
              store.state.root.publisher = publisher
              state.joinedPlayerNumbers++
              state.session.publish(publisher)
            })
            .catch(error => {
              console.log('There was an error connecting to the session:', error.code, error.message)
            })
			})

			window.addEventListener('beforeunload', leaveSession)
		}


    const leaveSession = () => {
			// --- Leave the session by calling 'disconnect' method over the Session object ---
			if (state.session) state.session.disconnect()

			state.session = undefined
			state.mainStreamManager = undefined
			state.publisher = undefined
			state.subscribers = []
			state.OV = undefined

			window.removeEventListener('beforeunload', leaveSession)
		}


		const updateMainVideoStreamManager = (stream) => {
			if (state.mainStreamManager === stream) return
			state.mainStreamManager = stream
		}


    const getToken = (mySessionId) => {
			return createSession(mySessionId).then(sessionId => createToken(sessionId))
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
							resolve(sessionId)
						} else {
							console.warn(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`)
							if (window.confirm(`No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`)) {
								location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`)
							}
							reject(error.response)
						}
					})
			})
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
			})
		}

    watch(() => state.joinedPlayerNumbers, () => {
      if(state.joinedPlayerNumbers === props.playerNumbers) emit('joinCallMyRoom')
    })

    watch(() => props.socketConnected, () => {
      joinSession()
    })


    // beforeunmount
    onBeforeUnmount(() => {
      leaveSession()
    })

    return { state, store, updateMainVideoStreamManager }
  },
}
</script>
