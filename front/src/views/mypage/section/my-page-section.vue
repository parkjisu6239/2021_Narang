<template>
  <section class="user-info-change-container">
    <transition name="fade">
      <UserDelete v-if="state.selected === 'userDelete'"/>
    </transition>
    <transition name="fade">
      <UserPasswordChange v-if="state.selected === 'userPasswordChange' "/>
    </transition>
    <transition name="fade">
      <UserInfoChange v-if="state.selected === 'userInfoChange'"/>
    </transition>
  </section>
</template>
<style>
 @import url('./my-page-section.css');
</style>
<script>
import UserInfoChange from './user-info-change.vue'
import UserDelete from './user-delete.vue'
import UserPasswordChange from './user-password-change.vue'
import { reactive } from '@vue/reactivity'
import { computed } from '@vue/runtime-core'
import { useStore } from 'vuex'

export default {
  name: "MyPageSection",
  components: {
    UserInfoChange,
    UserDelete,
    UserPasswordChange,
  },
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({
      selected: computed(() => {
        return store.getters['root/mypageMenu']
      })
    })

    return { state }
  }
}
</script>

