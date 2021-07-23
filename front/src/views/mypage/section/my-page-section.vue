<template>
  <section class="user-info-change-container">
    {{ state.seleted }}
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

export default {
  name: "MyPageSection",
  props: {
    seletedMenu: {
      type: Object,
      default: { menu : 'userInfoChange' }
    }
  },
  components: {
    UserInfoChange,
    UserDelete,
  },
  setup(props, { emit }) {
    const state = reactive({
      seleted: computed(() => {
        console.log(props.seletedMenu)
        return props.seletedMenu.menu
      })
    })

    return { state }
  }
}
</script>

