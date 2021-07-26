<template>
  <div class="mypage-container">
    <Sidebar @openProfileChangeDialog="openProfileChangeDialog"/>
    <MyPageSection/>
  </div>
  <ProfileChangeDialog
    :open="state.open"
    @closeProfileChangeDialog="closeProfileChangeDialog"/>
</template>

<style scoped>
  @import url('./mypage.css');
</style>
<script>
import Sidebar from './sidebar/sidebar.vue'
import ProfileChangeDialog from './sidebar/profile-change-dialog.vue'
import MyPageSection from './section/my-page-section.vue'
import { reactive } from '@vue/reactivity'

export default {
  name: 'Mypage',
  components: {
    Sidebar,
    MyPageSection,
    ProfileChangeDialog,
  },
  setup(props, { emit }) {
    const state = reactive({
      open: false
    })

    const openProfileChangeDialog = () => {
      state.open = !state.open
    }

    const closeProfileChangeDialog = () => {
      state.open = !state.open
    }

    return { state, openProfileChangeDialog, closeProfileChangeDialog }
  }
}
</script>
