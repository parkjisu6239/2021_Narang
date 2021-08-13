<template>
  <div>
    <div v-html="state.message"></div>
  </div>
</template>

<script>
import { reactive, computed } from 'vue'

export default {
  name: 'notice',

  props: {
    msg: {
      type: String,
    }
  },

  setup (props, { emit }) {

    const state = reactive({
      message: computed(() => props.msg.replace("\n", "<br />")),
    })

    return { state }
  }

}
</script>

<style>

</style>
