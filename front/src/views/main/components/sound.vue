<template>
  <i v-if="state.sound" @click="soundToggle" class="el-icon-headset sound"></i>
  <i v-else @click="soundToggle" class="el-icon-video-pause sound"></i>
</template>

<script>
import { useStore } from 'vuex'
import { reactive, computed } from 'vue'
export default {
  name: 'sound',
  setup(props, { emit }) {
    const store = useStore()
    const state = reactive({
      sound: computed(() => store.state.root.sound)
    })

    const soundToggle = () => {
      store.state.root.sound = !store.state.root.sound
    }

    return { state, soundToggle }
  }
}
</script>

<style>

</style>
