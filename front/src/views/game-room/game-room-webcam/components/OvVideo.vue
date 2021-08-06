<template >
  <video autoplay style="width: 98%;"/>
</template>

<script>
export default {
  name: 'OvVideo',
  props: {
    	streamManager: Object,
  },
  mounted() {
    this.streamManager.addVideoElement(this.$el);
  }
}
</script>
