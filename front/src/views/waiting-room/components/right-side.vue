<template>
  <div class="right-screen">
    <div class="navbar">
      <h1>Narang Lobby</h1>
      <div class="nav-bottom">
        <div class="style-bar"></div>
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
          <el-input placeholder="검색 꽥꽥"></el-input>
        </div>
      </div>
    </div>
    <ul class="infinite-list" v-infinite-scroll="load" style="overflow:auto; height: 70vh">
      <li v-for="room in state.gameRoomList" @click="clickConference(i)" class="infinite-list-item" :key="room.roomId" >
        <room :room='room'/>
      </li>
    </ul>
  </div>
</template>
<style scoped>
.right-screen {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(30px);
  border-radius: 0px 40px 40px 0px;
  padding: 20px 50px;
}

.nav-bottom {
  display: flex;
  justify-content: space-between;
}

.style-bar {
  width: 300px;
  height: 30px;
  background: rgba(255, 255, 255, 0.5);
  box-shadow: inset 0px 4px 4px rgba(255, 255, 255, 0.8);
  border-radius: 50px;
}

.top-btn-group {
  display: grid;
  max-width: 40%;
  grid-template-columns: 1fr 2fr 5fr;
  column-gap: 20px;
}

.infinite-list {
  padding-left: 0;
  max-height: calc(100% - 35px);
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}

.infinite-list::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera*/
}

@media (max-width: 1000px) {
  .infinite-list {
    min-width: 350px;
  }

  .nav-bottom {
    flex-direction: column;
  }

  .top-btn-group {
    max-width: 100%;
  }
}

@media (min-width: 1001px) and (max-width: 1269px) {
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
import { useStore } from 'vuex'

export default {
  name: "rightSide",

  components: {
    Room,
  },

  setup (props, { emit }) {
    const store = useStore()
    const router = useRouter()

    const state = reactive({
      gameRoomList: [],
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

    const readGameRoomList = function() {
      store.dispatch('root/requestReadGameRoomList')
      .then(function (result) {
        console.log(result.data.roomList.content)
        state.gameRoomList = result.data.roomList.content
      })
      .catch(function (err) {
        console.log(err)
      })
    }

    readGameRoomList()

    return { state, load, clickConference, clickCreateRoom }
  }
}
</script>
