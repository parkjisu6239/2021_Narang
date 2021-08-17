<template>
  <div class="timer-content">
    <div class="time">타이머 째깍째깍 <span class="time-number">{{ state.gameTimer }}</span>초</div>
  </div>
</template>

<script>
import { reactive, watch } from 'vue'

export default {
  name: 'timer',

  props: {
    timer: {
      tpye: Number,
    }
  },

  setup(props, { emit }) {

    const state = reactive({
      gameTimer: 0,
      timeId: 0,
    })

    const Clock = () => {
      if (state.gameTimer > 0) {
        state.gameTimer -= 1
      } else {
        clearInterval(state.timeId)
        state.timeId = 0
      }
    }

    const startTimer = () => {
      state.timeId = setInterval(() => {
        Clock()
      }, 1000)
    }


    watch(() => props.timer, () => {
      if (state.timeId) {
        clearInterval(state.timeId)
        state.timeId = 0
      }
      state.gameTimer = props.timer
      startTimer()
    })

    return { state }
  }
}
</script>

<style>
  @import url('./timer.css');
</style>
