<template>
  <section class="user-info-change-container">
    <UserPasswordChange v-if="seleted === 'user-password-change'"/>
    <UserInfoChange v-if="seleted === 'user-info-change'"/>
  </section>

</template>
<style>
 @import url('./my-page-section.css');
</style>
<script>
import UserInfoChange from './user-info-change.vue'
import UserPasswordChange from './user-password-change.vue'

export default {
  name: "MyPageSection",
  props: {
    seleted: {
      type: String,
      default: 'user-info-change'
    }
  },
  components: {
    UserInfoChange,
    UserPasswordChange,
  }
}
</script>

