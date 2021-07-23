<template>
  <section class="user-info-change-container">
    <UserDelete v-if="state.seleted === 'userDelete'"/>
    <UserInfoChange v-if="state.seleted === 'userInfoChange'"/>
  </section>

</template>
<style>
 @import url('./my-page-section.css');
</style>
<script>
import UserInfoChange from './user-info-change.vue'
import UserDelete from './user-delete.vue'
import { reactive } from '@vue/reactivity'
import { computed } from '@vue/runtime-core'
import { useStore } from 'vuex'

export default {
  name: "MyPageSection",
  components: {
    UserInfoChange,
    UserDelete,
  },
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({
      seleted: computed(() => {
        console.log('하이')
        return store.getters['root/mypageMenu']
      })
    })

    return { state }
  }
}
</script>

