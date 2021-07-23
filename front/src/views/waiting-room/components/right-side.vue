<template>
  <div class="right-screen">
    <div class="navbar">
      <h1>Narang Lobby</h1>
      <div class="top-btn-group">
        <el-button type="primary" round @click="clickCreateRoom">방만들기</el-button>
        <el-select v-model="value" placeholder="Select">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <el-input placeholder="검색 꽥꽥" style="width: 300px"></el-input>
      </div>
    </div>
    <ul class="infinite-list" v-infinite-scroll="load" style="overflow:auto; height: 70vh">
      <li v-for="i in state.count" @click="clickConference(i)" class="infinite-list-item" :key="i" >
        <room />
      </li>
    </ul>
  </div>
</template>
<style>
.right-screen {
  display: flex;
  flex-direction: column;
}

.infinite-list {
  padding-left: 0;
  max-height: calc(100% - 35px);
}

@media (min-width: 701px) and (max-width: 1269px) {
  .infinite-list {
    min-width: 700px;
  }
}

@media (min-width: 1270px) {
  .infinite-list {
    min-width: 1021px;
  }
}

.infinite-list .infinite-list-item {
  min-width: 335px;
  max-width: 25%;
  display: inline-block;
  cursor: pointer;
}
</style>
<script>
import Room from './room'

import { reactive } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: "rightSide",

  components: {
    Room,
  },

  setup (props, { emit }) {
    const router = useRouter()

    const state = reactive({
      count: 12,
    })

    const load = function () {
      state.count += 4
    }

    const clickConference = function (id) {
      router.push({
        name: 'gameRoom',
        params: {
          roomId: id
        }
      })
    }

    const clickCreateRoom = function() {
      emit('openCreateRoomDialog')
    }

    return { state, load, clickConference, clickCreateRoom }
  }
}
</script>
