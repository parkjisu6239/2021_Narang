<template>
  <div class="side-bar-container">
    <div class="img-wrapper">
      <div class="profile-img"></div>
    </div>
    <SidebarMenu @openSelectedMenu="openSelectedMenu"/>
  </div>
</template>
<style>
 @import url('./sidebar.css');
</style>
<script>
import { reactive } from '@vue/reactivity'
import SidebarMenu from './sidebar-menu.vue'
export default {
  name: 'SideBar',
  components: {
    SidebarMenu,
  },
  props: {

  },
  setup(props, { emit }) {
    const state = reactive({
      imgUrl: "./ssafy-logo.png"
    })

    const openSelectedMenu = (menu) => {
      emit('openSelectedMenu', menu)
    }

    return { state, openSelectedMenu }
  }




}
</script>


