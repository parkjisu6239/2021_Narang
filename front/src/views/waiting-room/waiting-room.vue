<template>
  <div class="screen">
    <LeftSide
      @narangGuidePopUp="state.isNarangGuideVisible = true"/>
    <RightSide
      @openCreateRoomDialog="onOpenCreateRoomDialog"
      @openEnterSecretRoomDialog="onOpenEnterSecretRoomDialog"/>
  </div>
  <create-room-dialog
    :open="state.createRoomDialogOpen"
    @closeCreateRoomDialog="onCloseCreateRoomDialog"/>
  <enter-secret-room-dialog
    :open="state.enterSecretRoomDialogOpen"
    :room="state.room"
    @closeEnterSecretRoomDialog="onCloseEnterSecretRoomDialog"/>
  <WaitingRoomBackground/>
  <Dialog
    v-if="state.isNarangGuideVisible">
    <NarangGuide @clickExit="state.isNarangGuideVisible = false"/>
  </Dialog>
</template>
<style>
.screen {
  display: flex;
  margin: 40px 80px;
  height: 90vh;
}
</style>
<script>
import LeftSide from './components/left-side/left-side'
import RightSide from './components/right-side/right-side'
import createRoomDialog from './components/dialog/create-room-dialog'
import enterSecretRoomDialog from './components/dialog/enter-secret-room-dialog'
import WaitingRoomBackground from './waiting-room-background/waiting-room-background'
import Dialog from '@/views/common/dialog/dialog.vue'
import NarangGuide from './narang-guide/narang-guide.vue'

import { reactive } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'waitingRoom',

  components: {
    LeftSide,
    RightSide,
    createRoomDialog,
    enterSecretRoomDialog,
    WaitingRoomBackground,
    Dialog,
    NarangGuide,
  },

  setup() {
    const store = useStore()

    const state = reactive({
      createRoomDialogOpen: false,
      enterSecretRoomDialogOpen: false,
      room: null,
      isNarangGuideVisible: false,
    })

    const onOpenCreateRoomDialog = function() {
      state.createRoomDialogOpen = true
    }

    const onCloseCreateRoomDialog = function() {
      state.createRoomDialogOpen = false
    }

    const onOpenEnterSecretRoomDialog = function(room) {
      state.enterSecretRoomDialogOpen = true
      state.room = room
    }

    const onCloseEnterSecretRoomDialog = function() {
      state.enterSecretRoomDialogOpen = false
    }

    const requestReadMyInfo = () => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          const userInfo = {
            email: res.data.user.email,
            username: res.data.user.username,
            profileImageURL: res.data.user.thumbnailUrl,
          }
          console.log(res.data.user.thumbnailUrl)
          store.commit('root/setUserInfo', userInfo)
        })
        .catch(err => {
          console.log(err)
        })
    }

    requestReadMyInfo()

    return { state, onOpenCreateRoomDialog, onCloseCreateRoomDialog, onOpenEnterSecretRoomDialog, onCloseEnterSecretRoomDialog }
  }
}
</script>
