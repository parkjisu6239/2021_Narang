<template>
  <ul class="sidebar-menus">
    <li @click="clickMenu('userInfoChange')" :class="{'selected': state.selected[0]}">
      <div class="menu-name">회원정보수정</div>
    </li>
    <li @click="clickMenu('userPasswordChange')" :class="{'selected': state.selected[1]}">
      <div class="menu-name">비밀번호 변경</div>
    </li>
    <li @click="clickMenu('userDelete')" :class="{'selected': state.selected[2]}">
      <div class="menu-name">회원탈퇴</div>
    </li>
  </ul>
</template>
<style scoped>
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
      selected: [true, false, false]
    })

    const clickMenu = (menu) => {
      store.commit('root/setSeletedMenu', menu)
      if (menu === 'userInfoChange') {
        state.selected = [true, false, false]
      } else if (menu === 'userPasswordChange') {
        state.selected = [false, true, false]
      }else if (menu === 'userDelete') {
        state.selected = [false, false, true]
      }
    }

    return { state, clickMenu }
  }
}
</script>

