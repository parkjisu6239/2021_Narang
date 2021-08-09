<template>
  <div>
    {{ msg }}
  </div>
</template>

<script>
export default {
  name: 'notice',

  props: {
    msg: {
      type: String,
    }
  },
}
</script>

<style>

</style>
