<template>
  <div class="loading-wrap">
    <div class="duck-container">
      <div class="water-circle"></div>
      <img class="duck" :src="require('@/assets/images/loading/loading-duck.png')" alt="">
    </div>
    <div class="loading"></div>
  </div>
</template>

<script>
export default {
  name: 'loading'
}
</script>

<style scoped>
  @import url('./loading.css');
</style>
