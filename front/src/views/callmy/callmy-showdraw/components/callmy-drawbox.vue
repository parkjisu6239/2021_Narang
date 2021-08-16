<template>
  <div class="drawbox">

  </div>
</template>
<style>

</style>
<script>
import { reactive } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'CallmyDrawBox',
  props: {
    playerId: Number,
  },
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({

    })
  }

}
</script>

