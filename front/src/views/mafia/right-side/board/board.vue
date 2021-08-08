<template>
  <div>
    보드
    <Notice/>
    <Vote/>
  </div>
</template>

<script>
import Notice from './notice.vue'
import Vote from './vote.vue'

export default {
  name: 'board',

  components: {
    Notice,
    Vote,
  }
}
</script>

<style>

</style>
