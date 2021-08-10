<template>
  <div>
    <Notice :msg="msg" v-if="state.mafiaManager.stage == 'default'"/>
    <Vote :msg="msg" v-else/>
  </div>
</template>

<script>
import Notice from './notice.vue'
import Vote from './vote.vue'

import { computed, reactive } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'board',

  props: {
    msg: {
      type: String,
    }
  },

  components: {
    Notice,
    Vote,
  },

  setup(props, { emit }) {
    const store = useStore()

    const state = reactive({
      mafiaManager: computed(() => store.getters['root/mafiaManager']),
    })

    return { state }
  }
}
</script>

<style>

</style>
