<template>
  <div class="left-screen">
    <div class="my-info">
      <img :src="state.profileImageURL" alt="">
      <div class="nickname">{{ state.username }}</div>
      <div class="email">{{ state.email }}</div>
    </div>
    <ul class="infinite-list friend-list" v-infinite-scroll="load">
      <li v-for="i in state.count" class="infinite-list-item friend-list-item" :key="i" >
        <friend />
      </li>
    </ul>
    <el-row class="btn-group">
      <el-button type="primary" icon="el-icon-edit" circle></el-button>
      <el-button type="success" icon="el-icon-check" circle></el-button>
      <el-button type="info" icon="el-icon-message" circle></el-button>
      <el-button type="warning" icon="el-icon-star-off" circle></el-button>
    </el-row>
  </div>
</template>

<script>
import Friend from './friend.vue'

import { reactive, computed } from 'vue'
import { useStore } from 'vuex'

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
    })

    const load = function () {
      state.count += 4
    }

    const setMyInfo = function () {
      state.email = localStorage.getItem('email')
      state.username = localStorage.getItem('username')
      state.profileImageURL = `https://0.0.0.0:8080${localStorage.getItem('profileImageURL')}`
    }

    setMyInfo()

    return { state, load }
  }
}
</script>

<style scoped>
.left-screen {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  border-radius: 30px 0px 0px 30px;
  padding: 60px 40px;

  max-width: 300px;
  display: grid;
  grid-template-rows: 2fr 15fr 20px;
}

.left-screen img {
  border-radius: 50%;
}

.my-info {
  text-align: center;
  max-width: 220px;
}

.my-info img {
  width: 100px;
  height: 100px;
}

.my-info .nickname {
  font-size: 20px;
  font-weight: 500;
}

.my-info .email {
  font-size: 14px;
}

.friend-list {
  padding: 20px;
  width: 180px;
  overflow: auto;
  background: #FFFFFF;
  box-shadow: inset 1px 2px 4px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(4px);
  border-radius: 20px;
  display: grid;
  row-gap: 15px;
}

.infinite-list {
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}

.infinite-list::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera*/
}

.infinite-list .infinite-list-item {
  display: inline-block;
  cursor: pointer;
}



</style>
