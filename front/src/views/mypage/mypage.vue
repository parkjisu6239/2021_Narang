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
import { useStore } from 'vuex'

export default {
  name: 'Mypage',
  components: {
    Sidebar,
    MyPageSection,
    ProfileChangeDialog,
  },
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({
      open: false
    })

    const openProfileChangeDialog = () => {
      state.open = true
    }

    const closeProfileChangeDialog = () => {
      state.open = false
    }

    return { state, openProfileChangeDialog, closeProfileChangeDialog }
  }
}
</script>
