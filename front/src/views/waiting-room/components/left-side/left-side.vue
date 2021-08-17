<template>
  <div class="left-screen">
    <div class="my-info">
      <img v-if="state.profileImage !== 'null'" :src="state.profileImageURL" alt="">
      <img class="no-profile" v-else :src="require('@/assets/images/Neurang.png')" alt="">
      <div class="nickname">{{ state.username }}</div>
      <div class="email">{{ state.email }}</div>
    </div>
    <ul class="infinite-list friend-list" v-infinite-scroll="load">
      <li v-for="i in state.count" class="infinite-list-item friend-list-item" :key="i" >
        <friend />
      </li>
    </ul>
    <div class="btn-group">
      <el-button icon="el-icon-info" circle @click="clickInfoBtn"></el-button>
      <el-button icon="el-icon-s-tools" circle @click="clickComingSoonBtn"></el-button>
      <el-button icon="el-icon-message" circle @click="clickComingSoonBtn"></el-button>
      <el-button icon="el-icon-star-off" circle @click="clickComingSoonBtn"></el-button>
    </div>
  </div>
</template>

<script>
import Friend from './friend.vue'

import { reactive, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: "leftSide",

  components: {
    Friend
  },

  setup (props, { emit }) {
    const store = useStore()

    const state = reactive({
      count: 12,
      email: '',
      username: '',
      profileImageURL: '',
      profileImage: localStorage.getItem('profileImageURL'),
    })

    const load = function () {
      state.count += 4
    }

    const setMyInfo = function () {
      state.email = localStorage.getItem('email')
      state.username = localStorage.getItem('username')
      state.profileImageURL = `https://0.0.0.0:8080${localStorage.getItem('profileImageURL')}`
    }

    const clickInfoBtn = () => {
      // 가이드 다이얼로그 팝업
    }

    const clickComingSoonBtn = () => {
      ElMessage({
        type: 'primary',
        message: 'Coming Soon'
      })
    }

    setMyInfo()

    return { state, load, clickInfoBtn, clickComingSoonBtn }
  }
}
</script>
<style scoped>
  @import url('./left-side.css');
</style>
