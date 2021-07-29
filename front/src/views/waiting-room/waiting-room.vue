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
    :roomId="state.roomId"
    @closeEnterSecretRoomDialog="onCloseEnterSecretRoomDialog"/>
</template>
<style>
.screen {
  display: flex;
  margin: 40px 80px;
  height: 90vh;
}
</style>
<script>
import LeftSide from './components/left-side'
import RightSide from './components/right-side'
import createRoomDialog from './components/create-room-dialog'
import enterSecretRoomDialog from './components/enter-secret-room-dialog'

import { reactive } from 'vue'

export default {
  name: 'waitingRoom',

  components: {
    LeftSide,
    RightSide,
    createRoomDialog,
    enterSecretRoomDialog,
  },

  setup() {

    const state = reactive({
      createRoomDialogOpen: false,
      enterSecretRoomDialogOpen: false,
      roomId: null
    })

    const onOpenCreateRoomDialog = function() {
      state.createRoomDialogOpen = true
    }

    const onCloseCreateRoomDialog = function() {
      state.createRoomDialogOpen = false
    }

    const onOpenEnterSecretRoomDialog = function(roomId) {
      state.enterSecretRoomDialogOpen = true
      state.roomId = roomId
    }

    const onCloseEnterSecretRoomDialog = function() {
      state.enterSecretRoomDialogOpen = false
    }

    return { state, onOpenCreateRoomDialog, onCloseCreateRoomDialog, onOpenEnterSecretRoomDialog, onCloseEnterSecretRoomDialog }
  }
}
</script>
