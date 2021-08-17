<template>
  <div class="friend">
    <img :src="imgUrl" alt="">
    <div>
      <div class="nickname">{{ username }}</div>
      <div class="email">{{ email }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'friend',
  props: {
    username: {
      type: String
    },
    email: {
      type: String
    },
    imgUrl: {
      type: String
    },
  },
}

</script>
<style scoped>
  @import url('./friend.css');
</style>
