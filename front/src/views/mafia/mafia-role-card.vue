<template>
  <div class="role-card">
    <div class="role-img">그림</div>
    <div class="role">{{ myRole }}</div>
    <div class="description">{{ state.description[myRole] }}</div>
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
      description: {
        'Mafia': '당신의 마피아입니다. 시민을 속여 마피아의 승리를 이끌어보세요.',
        'Citizen': '당신의 선량한 시민입니다. 마피아를 찾아내어 마을의 평화를 유지하세요.'
      },
    })

    return { state, }
  }

}
</script>

<style scoped>
  @import url('./mafia-role-card.css');
</style>
