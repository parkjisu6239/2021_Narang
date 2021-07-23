<template>
  <div class="screen">
    <LeftSide />
    <RightSide @openCreateRoomDialog="onOpenCreateRoomDialog"/>
  </div>
  <create-room-dialog
    :open="state.createRoomDialogOpen"
    @closeCreateRoomDialog="onCloseCreateRoomDialog"/>
</template>
<style>
.screen {
  display: flex;
}
</style>
<script>
import LeftSide from './components/left-side'
import RightSide from './components/right-side'
import createRoomDialog from './components/create-room-dialog'

import { reactive } from 'vue'

export default {
  name: 'waitingRoom',

  components: {
    LeftSide,
    RightSide,
    createRoomDialog
  },

  setup() {

    const state = reactive({
      createRoomDialogOpen: false,
    })

    const onOpenCreateRoomDialog = function() {
      state.createRoomDialogOpen = true
    }

    const onCloseCreateRoomDialog = function() {
      state.createRoomDialogOpen = false
    }

    return { state, onOpenCreateRoomDialog, onCloseCreateRoomDialog}
  }
}
</script>
