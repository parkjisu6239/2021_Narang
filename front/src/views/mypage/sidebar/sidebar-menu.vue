<template>
  <ul class="sidebar-menus">
    <li @click="clickMenu('userInfoChange')">회원정보수정</li>
    <li @click="clickMenu('userDelete')">회원탈퇴</li>
    <li>친구관리 - comming soon</li>
  </ul>
</template>
<style>
  @import url('./sidebar-menu.css');
</style>
<script>
import { reactive } from '@vue/reactivity'
import { useStore } from 'vuex'
export default {
  name:"SidebarMenu",
  props: {

  },
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({

    })

    const clickMenu = (menu) => {
      store.commit('root/setSeletedMenu', menu)
    }


    return { state, clickMenu }
  }
}
</script>

