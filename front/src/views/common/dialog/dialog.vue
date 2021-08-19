<template>
  <div class="dialong-body">
    <div class="dialog-container">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  name: 'dialog',

  props: {

  }
}
</script>

<style scoped>
  @import url('./dialog.css');
</style>
