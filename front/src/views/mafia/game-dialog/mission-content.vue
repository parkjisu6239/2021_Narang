<template>
  <div class="mission-content">
    <h2>{{ missionName }}</h2>
    <img :src="require(`@/assets/images/mafia/mission/m${missionNumber}.png`)" alt="">
  </div>
</template>

<script>
import { reactive } from 'vue'

export default {
  name: 'missionContent',

  props: {
    missionName: {
      type: String,
    },
    missionNumber: {
      type: Number,
    }
  },

}
</script>

<style scoped>
  @import url('./mission-content.css');
</style>
