<template>
  <div class="side-bar-container">
    <div class="img-wrapper">
      <img v-if="state.profileImageURL === undefined" class="profile-img" @click="clickProfile" :src="state.profileImageURL">
      <img
        v-else
        class="profile-img"
        @click="clickProfile"
        :src="require('@/assets/images/Neurang.png')"
        :style="{'background': state.backgroundColor[state.backgroundNum]}">
      <div class="side-bar-username">{{ state.username }}</div>
      <div class="side-bar-email">{{ state.email }}</div>
    </div>
    <SidebarMenu/>
  </div>
</template>
<style>
 @import url('./sidebar.css');
</style>
<script>
import SidebarMenu from './sidebar-menu.vue'
import { useStore } from 'vuex'
import { reactive } from '@vue/reactivity'
import { computed } from '@vue/runtime-core'
export default {
  name: 'SideBar',
  components: {
    SidebarMenu,
  },
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({
      username: computed(() => store.getters['root/username']),
      email: computed(() => store.getters['root/email']),
      profileImageURL: computed(() => store.getters['root/profileImageURL']),
      backgroundColor: ['orange', 'red', 'green'],
      backgroundNum: store.getters['root/email'].length % 3,
    })

    const clickProfile = () => {
      emit('openProfileChangeDialog')
    }
    return { state, clickProfile }
  }
}
</script>


