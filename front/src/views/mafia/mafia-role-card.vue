<template>
  <div class="role-card">
    <div class="role-card-front">
      <img v-if="myRole === 'Citizen'" class="role-img role-citizen" :src="require('@/assets/images/mafia/role-citizen.png')" alt="시민">
      <img v-else class="role-img role-mafia" :src="require('@/assets/images/mafia/role-mafia.png')" alt="마피아">
      <div class="role">{{ myRole }}</div>
    </div>
    <div class="role-card-back">
      <div class="card-inner-line">
        <img class="neurang" :src="require('@/assets/images/Neurang.png')" alt="너랑">
        <p>당신의 역할은?</p>
        <p>마우스를 올려보세요</p>
        <img class="neurang-mirror" :src="require('@/assets/images/Neurang.png')" alt="너랑">
      </div>
    </div>
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
