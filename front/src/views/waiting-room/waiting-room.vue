<template>
  <div class="screen">
    <LeftSide />
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


import { reactive } from 'vue'

export default {
  name: 'waitingRoom',

  components: {
    LeftSide,
    RightSide,
    createRoomDialog,
    enterSecretRoomDialog,
    WaitingRoomBackground,
  },

  setup() {

    const state = reactive({
      createRoomDialogOpen: false,
      enterSecretRoomDialogOpen: false,
      room: null
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

    return { state, onOpenCreateRoomDialog, onCloseCreateRoomDialog, onOpenEnterSecretRoomDialog, onCloseEnterSecretRoomDialog }
  }
}
</script>
