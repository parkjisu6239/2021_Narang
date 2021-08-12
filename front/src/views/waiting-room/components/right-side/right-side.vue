<template>
  <div class="right-screen">
    <div class="navbar">
      <h1>Narang Lobby</h1>
      <div class="nav-bottom">
        <div class="search-group">
          <div class="select-type">
            <div :class="{'select-all': true, 'selected-btn': state.isActivate === 'All'}" @click="clickActivateTypeBtn('All')">All</div>
            <div :class="{'select-wait': true, 'selected-btn': state.isActivate === 'Waiting'}" @click="clickActivateTypeBtn('Waiting')">Waiting</div>
            <div :class="{'select-play': true, 'selected-btn': state.isActivate === 'Playing'}" @click="clickActivateTypeBtn('Playing')">Playing</div>
          </div>
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
            size="small"
            @keyup.enter="clickSearch">
          </el-input>
          <el-button
            icon="el-icon-search"
            type="primary"
            circle
            @click="clickSearch">
          </el-button>
        </div>
        <el-button type="primary" round @click="clickCreateRoom">방만들기</el-button>
      </div>
    </div>
    <ul class="infinite-list room-list" v-infinite-scroll="load" style="overflow:auto; height: 70vh">
      <li v-for="room in state.gameRoomList" @click="clickConference(room.room)" class="infinite-list-item room-list-item" :key="room.roomId" >
        <room :room='room.room' :user="room.joinUsers"/>
      </li>
    </ul>
  </div>
</template>
<script>
import Room from './room'

import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

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
      activateList: {All: null, Waiting: true, Playing: false},
      isActivate: 'All',
      options:
        [{
            value: 'All',
            label: 'All'
          }, {
            value: 'mafia',
            label: 'Nafia'
          }, {
            value: 'callmy',
            label: 'Call My Name'
        }],
      value: 'All',
      input: '',
      page: 1,
      end: false,
      roomId: null,
    })

    const load = function () {
      console.log(state.end)
      if (!state.end) {
        readGameRoomList()
      }
    }

    const clickSearch = function () {
      state.page = 1
      state.end = false
      state.gameRoomList = []
      readGameRoomList()
    }

    const clickActivateTypeBtn = function(value) {
      state.isActivate = value
      clickSearch()
    }

    const clickConference = function (room) {
      const player = room.userList ? room.userList.length : 0
      if (player >= room.maxPlayer) {
        ElMessage({
          message: '방이 이미 가득차서 들어갈 수 없습니다.',
        })
        return
      }

      if (room.password == 0) {
        store.dispatch('root/requestEnterGameRoom', {roomId: room.roomId, password: 0})
        .then(res => {
          store.commit('root/setRoomInfo', room)
          router.push({
            name: 'gameRoom',
            params: {
              roomId: room.roomId
            }
          })
        })
        .catch(err => {
          console.log(err)
        })
      } else {
        emit('openEnterSecretRoomDialog', room)
      }
    }

    const clickCreateRoom = function() {
      emit('openCreateRoomDialog')
    }

    const readGameRoomList = function() {
      const payload = {
        game: state.value === 'All' ? null : state.value,
        isActivate: state.activateList[state.isActivate],
        title: state.input ? state.input : null,
        page: state.page,
        size: 10,
      }
      store.dispatch('root/requestReadGameRoomList', payload)
      .then(function (result) {
        console.log(result.data.roomList)
        state.gameRoomList = state.gameRoomList.concat(result.data.roomList.content)
        state.page += 1
        state.end = result.data.roomList.last
      })
      .catch(function (err) {
        console.log(err)
      })
    }

    readGameRoomList()

    return { state, load, clickConference, clickCreateRoom, readGameRoomList, clickSearch, clickActivateTypeBtn }
  }
}
</script>
<style scoped>
  @import url('./right-side.css');
</style>
