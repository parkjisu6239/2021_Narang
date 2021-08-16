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
import { reactive, computed } from 'vue'

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
      state.recognition.continuous = true;
      state.recognition.interimResults = true;
      state.recognition.interimResults = 'ko-KR';
      state.recognition.start();


      state.recognition.onresult=function(event){
        state.finalTranscript = '' // 기존에 작성된 내용 초기화
        let nowSay = '' // 사용자가 지금하는 말

        for (let i = event.resultIndex; i < event.results.length; ++i) {
          const transcript = event.results[i][0].transcript;

          if (event.results[i].isFinal) {
            nowSay += transcript;
          }
        }

        console.log(nowSay)

        if (state.ans) { // 정답 타임인 경우
          state.finalTranscript = nowSay // 이번에 말한 내용으로 보드 변경
          sendGuessName();
        }

        if (nowSay.trim() === '정답') { // 정답이라고 말한 경우
          state.ans = true // 정답 타임!
          setTimeout(() => {
              state.ans = false // 5초 후 정답 타임 취소
            }, 5000)
        }
      };

      state.recognition.onerror = function(event){
        console.log(event)
      };
    }


    const sendGuessName = () => {
      const message = {
        userId: state.userId,
        name: state.finalTranscript.replace(/(\s*)/g, "")
      }

      emit('sendGuessName', message)
    }


    startRecognition()


    return { state }
  }
}
</script>

<style>
  @import url('./callmy-stt.css');
</style>
