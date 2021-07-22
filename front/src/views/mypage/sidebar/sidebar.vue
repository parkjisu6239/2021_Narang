<template>
  <div class="side-bar-container">
    <div class="img-wrapper">
      <div class="profile-img"></div>
    </div>
    <div>

    </div>
  </div>
</template>
<style>
 @import url('./sidebar.css');
</style>
<script>
import { reactive } from '@vue/reactivity'
export default {
  name: 'SideBar',
  props: {

  },
  setup(props, { emit }) {
    const state = reactive({
      imgUrl: "./ssafy-logo.png"
    })

    return { state }
  }




}
</script>


