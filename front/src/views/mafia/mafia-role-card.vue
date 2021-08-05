<template>
  <div class="role-card">
    <img v-if="myRole === 'Citizen'" class="role-img role-citizen" :src="require('@/assets/images/mafia/role-citizen.png')" alt="시민">
    <img v-else class="role-img role-mafia" :src="require('@/assets/images/mafia/role-citizen.png')" alt="마피아">
    <div class="role">{{ myRole }}</div>
  </div>
</template>

<script>
import { reactive } from 'vue'

export default {
  name: 'mafiaRoleCard',

  props: {
    myRole: {
      type: String
    },
  },

  setup(props, { emit }) {

    const state = reactive({
    })

    return { state, }
  }

}
</script>

<style scoped>
  @import url('./mafia-role-card.css');
</style>
