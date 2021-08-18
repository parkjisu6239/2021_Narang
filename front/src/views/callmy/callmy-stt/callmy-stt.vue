<template>
  <div :class="{'stt-container':true, 'ans': state.ans}">
    <div class="stt-constent">
      <div>{{ speaker }} 정답 타임! <span>5초</span> 안에 <span>정확히</span> 말해주세요</div>
      <h1>{{ state.finalTranscript }}</h1>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex'
import { reactive, computed, onBeforeUnmount } from 'vue'

export default {
  name: 'callmyStt',

  props: {
    speaker: String
  },

  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
      recognition: null,
      isRecognizing: false,
      ignoreEndProcess: false,
      finalTranscript: '',
      ans: false,
      callmyManager: computed(() => store.state.root.callmyManager),
      userId: computed(() => store.state.root.userId),
    })

    const startRecognition = () => {
      state.recognition = new webkitSpeechRecognition()
      state.recognition.continuous = false
      state.recognition.interimResults = true
      state.recognition.interimResults = 'ko-KR'
      state.recognition.start()


      state.recognition.onresult = function(event){
        state.finalTranscript = '';
        let finalTranscript = '';
        for (let i = event.resultIndex; i < event.results.length; ++i) {
          let transcript = event.results[i][0].transcript;
          if (event.results[i].isFinal) {
            finalTranscript += transcript;
          }
        }
        if (state.ans) { // 정답 타임인 경우
          state.finalTranscript = finalTranscript // 이번에 말한 내용으로 보드 변경
          sendGuessName(state.finalTranscript);
          return;
        }
        if (finalTranscript.trim() === '정답') { // 정답이라고 말한 경우
        if(state.callmyManager.isAnswer) {
            return; // 정답을 한 번만 외칠 수 있게함. 또는 누가 정답을 외쳤다.
        }
          sendGuessName('정답');
          state.ans = true // 정답 타임!
          setTimeout(() => {
              state.ans = false // 5초 후 정답 타임 취소
              sendGuessName('정답타임끝');
            }, 5000)
        }
      }

      state.recognition.onerror = function(event){
        console.log(event)
      }

      state.recognition.onend = function(event) {
        state.recognition.start()
      }
    }


    const sendGuessName = (finalTranscript) => {
      const message = {
        userId: state.userId,
        name: finalTranscript,
      }
      emit('sendGuessName', message)
    }


    onBeforeUnmount(() => {
      state.recognition.onend = null
      state.recognition.stop()
    })


    startRecognition()


    return { state }
  }
}
</script>

<style>
  @import url('./callmy-stt.css');
</style>
