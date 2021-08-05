<template>
  알림창
  {{ msg }}
</template>

<script>
import { reactive } from 'vue'

export default {
  name: 'mafiaNotice',

  props: {
    msg: {
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

<style>

</style>
