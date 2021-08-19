<template>
  <div class="main-navbar">
    <div class="menu-thumb"><i class="el-icon-menu"></i></div>
    <div class="menu-item" @click="goHome"><i class="el-icon-s-home"></i></div>
    <div class="menu-item" @click="goMyPage"><i class="el-icon-s-custom"></i></div>
    <div class="menu-item" @click="goBack"><i class="el-icon-back"></i></div>
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
    const router = useRouter()

    const goHome = () => {
      router.push({
        name: 'home',
      })
    }

    const goMyPage = () => {
      router.push({
        name: 'mypage',
      })
    }

    const goBack = () => {
      router.go(-1)
    }

    return { goHome, goMyPage, goBack }
  }
}
</script>

