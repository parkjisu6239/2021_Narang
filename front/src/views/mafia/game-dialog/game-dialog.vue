<template>
  <div class="dialong-body">
    <div class="dialog-container">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  name: 'gameDialog',

  props: {

  }
}
</script>

<style scoped>
  @import url('./game-dialog.css');
</style>
