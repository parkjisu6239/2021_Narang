<template>
  <div class="main-navbar">
    <span @click="goHome"><i class="el-icon-s-home"></i></span>
    <span @click="goMyPage"><i class="el-icon-s-custom"></i></span>
    <span @click="goBack"><i class="el-icon-back"></i></span>
  </div>
</template>
<style>
  @import url('./navbar.css');
</style>
<script>
import { useRouter } from 'vue-router'
export default {
  name: 'navbar',
  setup(props, { emit }) {
    const route = useRouter()

    const goHome = () => {
      route.push({
        name: 'home',
      })
    }

    const goMyPage = () => {
      console.log('되라고')
      route.push({
        name: 'mypage',
      })
    }

    const goBack = () => {
      route.go(-1)
    }

    return { goHome, goMyPage, goBack }
  }
}
</script>

