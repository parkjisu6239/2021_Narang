<template>
  <div class="callmy-guide-container">
    <img :src="require('@/assets/images/callmy-guide/callmy-guide1.png')" alt="">
    <img :src="require('@/assets/images/callmy-guide/callmy-guide2.png')" alt="">
    <img :src="require('@/assets/images/callmy-guide/callmy-guide3.png')" alt="">
    <img :src="require('@/assets/images/callmy-guide/callmy-guide4.png')" alt="">
  </div>
</template>

<script>
export default {
  name: 'callmyGuide'
}
</script>

<style scoped>
  @import url('./callmy-guide.css');
</style>
