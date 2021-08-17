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
      gameTimer: 0
    })

    const setTimer = () => {
      state.gameTimer = props.timer
    }

    const Clock = () => {
      if (state.gameTimer > 0) {
        state.gameTimer -= 1
      } else {
        clearInterval(interval)
      }

    }

    const interval = setInterval(() => {
      Clock()
      window.requestAnimationFrame(interval)
    }, 1000)

    watch(props, () => {
      setTimer()
    })

    return { state }
  }
}
</script>

<style>
  @import url('./timer.css');
</style>
