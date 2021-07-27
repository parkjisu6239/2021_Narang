<template>
  <div class="right-screen">
    <div class="navbar">
      <h1>Narang Lobby</h1>
      <div class="nav-bottom">
        <div class="search-group">
          <el-radio-group v-model="state.isActivate" size="small">
            <el-radio-button label="All"></el-radio-button>
            <el-radio-button label="Waiting"></el-radio-button>
            <el-radio-button label="Playing"></el-radio-button>
          </el-radio-group>
          <el-select v-model="state.value" placeholder="Select" size="small">
            <el-option
              v-for="item in state.options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
          <el-input
            v-model="state.input"
            placeholder="검색 꽥꽥"
            prefix-icon="el-icon-search"
            clearable
            size="small">
          </el-input>
          <el-button
            icon="el-icon-search"
            type="primary"
            circle
            @click="ClickReadGameRoomList">
          </el-button>
        </div>
        <el-button type="primary" round @click="clickCreateRoom">방만들기</el-button>
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
  padding: 10px 30px;
}

.nav-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-group {
  display: flex;
  align-items: center;
}

.search-group > *{
  margin-right: 10px;
}

.search-group .el-select {
  max-width: 100px;
}

.search-group .el-input {
  max-width: 200px;
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

import { reactive, ref } from 'vue'
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
      activateList: {All: null, Waiting: true, Playing: false},
      isActivate: ref('All'),
      options:
        [{
            value: 'All',
            label: 'All'
          }, {
            value: 'Nafia',
            label: 'Nafia'
          }, {
            value: 'callmy',
            label: 'Call My Name'
        }],
      value: 'All',
      input: '',
      page: 1,
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

    const ClickReadGameRoomList = function() {
      const payload = {
        game: state.value === 'All' ? null : state.value,
        isActivate: state.activateList[state.isActivate],
        title: state.input ? state.input : null
      }
      store.dispatch('root/requestReadGameRoomList', payload)
      .then(function (result) {
        console.log(result.data.roomList)
        state.gameRoomList = result.data.roomList.content
      })
      .catch(function (err) {
        console.log(err)
      })
    }

    ClickReadGameRoomList()

    return { state, load, clickConference, clickCreateRoom, ClickReadGameRoomList }
  }
}
</script>
