<template>
  <div class="draw">
    <h1 v-for="(player, index) in players" :key="index">
      {{ player.username }}
    </h1>
  </div>
</template>
<style>
  @import url('./callmy-showresult.css');
</style>
<script>
import CallmyDrawBox from './components/callmy-drawbox.vue'
import { useStore } from 'vuex'
import { reactive, computed } from 'vue'
export default {
  name: 'CallmyShowResult',
  components: {
    // CallmyDrawBox,
  },
  props: {
    players: Array,
    show: Boolean,
  },
  setup(props, { emit }) {
    const store = useStore()


    const state = reactive({
      callmyManager: computed(() => store.state.root.callmyManager),
    })

    return { state }
  }
}
</script>
