<template>
  <div :class="{'stt-container':true, 'ans': state.ans}">
    <div class="stt-constent">
      <div>정답을 듣고 있습니다. <span>5초</span> 안에 <span>정확히</span> 말해주세요</div>
      <h1>{{ state.finalTranscript }}</h1>
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex'
import { reactive, computed, onBeforeUnmount } from 'vue'

export default {
  name: 'callmyStt',

  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
      recognition: null,
      isRecognizing: false,
      ignoreEndProcess: false,
      finalTranscript: '',
      ans: false,
      userId: computed(() => store.state.root.userId),
    })

    const startRecognition = () => {
      state.recognition = new webkitSpeechRecognition()
      state.recognition.continuous = false
      state.recognition.interimResults = true
      state.recognition.interimResults = 'ko-KR'
      state.recognition.start()


      state.recognition.onresult = function(event){
        console.log(event, '이거는 event')
        console.log(event.results, '이거는 event results')
        let nowSay = Array.from(event.results).map(results => results[0].transcript).join("")

        console.log(nowSay)

        if (state.ans) { // 정답 타임인 경우
          state.finalTranscript = '';
          state.finalTranscript = nowSay // 이번에 말한 내용으로 보드 변경
          sendGuessName();
        }

        if (nowSay.trim() === '정답') { // 정답이라고 말한 경우
          state.finalTranscript = '';
          state.ans = true // 정답 타임!
          setTimeout(() => {
              state.ans = false // 5초 후 정답 타임 취소
            }, 5000)
        }
      }

      state.recognition.onerror = function(event){
        console.log(event)
      }

      state.recognition.onend = function(event) {
        console.log(event, 'STT 끝났어요')
        state.recognition.start()
      }
    }


    const sendGuessName = () => {
      const message = {
        userId: state.userId,
        name: state.finalTranscript.replace(/(\s*)/g, ""),
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
