<template>
  <h1>마피아 게임 방</h1>
  <el-radio-group v-model="radio1">
    <el-radio-button label="New York"></el-radio-button>
    <el-radio-button label="Washington"></el-radio-button>
    <el-radio-button label="Los Angeles"></el-radio-button>
    <el-radio-button label="Chicago"></el-radio-button>
  </el-radio-group>

  <div>
    <img class="city" :src="require('@/assets/images/mafia/city.png')" alt="">
    <img class="mafia-neorang" :src="require('@/assets/images/mafia/mafia.png')" alt="">
    <div class="circle moon"></div>
    <div class="night-background"></div>
  </div>
  <!-- <div>
    <img class="land" :src="require('@/assets/images/mafia/land.png')" alt="">
    <img class="cloud1" :src="require('@/assets/images/mafia/cloud1.png')" alt="">
    <img class="cloud2" :src="require('@/assets/images/mafia/cloud2.png')" alt="">
    <img class="cloud3" :src="require('@/assets/images/mafia/cloud3.png')" alt="">
    <div class="circle sun"></div>
    <div class="day-background"></div>
  </div> -->
</template>

<script>
export default {
  name: 'mafia',

}
</script>

<style scoped>
  @import url('./mafia.css');
</style>
