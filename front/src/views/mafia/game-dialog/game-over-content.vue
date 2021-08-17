<template>
  <div class="game-over-content">
    <h2>게임 종료!</h2>
    <div>{{ msg }}</div>
    <img class="mafia-win" v-if="msg === '마피아가 승리하였습니다.'" :src="require('@/assets/images/mafia/role-mafia.png')" alt="">
    <img class="citizen-win" v-else :src="require('@/assets/images/mafia/role-citizen.png')" alt="">
    <div>
      <span>마피아는</span>
      <span class="mafia-ele" v-for="mafia in state.mafia" :key="mafia">{{ mafia }}</span>
      <span>였습니다.</span>
    </div>
    <div>잠시 후 게임룸으로 돌아갑니다.</div>
  </div>
</template>

<script>
import { reactive } from 'vue'

export default {
  name: 'gameOverContent',

  props: {
    msg: {
      type: String,
    },
    result: {
      type: String,
    }
  },

  setup(props, { emit }) {

    const state = reactive({
      mafia: [],
      citizen: [],
    })

    const setGameResult = () => {
      if (props.result === '') {
        return
      }

      props.result.split('&').forEach(fair => {
        let result = fair.split(':')
        if (result[1] === 'Mafia') {
          state.mafia.push(result[0])
        } else {
          state.citizen.push(result[0])
        }
      })
    }

    setGameResult()

    return { state }
  }
}
</script>

<style>
  @import url('./game-over-content.css');
</style>
