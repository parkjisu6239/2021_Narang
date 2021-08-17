<template>
  <div class="callmy-notice-container">
    <div class="callmy-notice-container-msg"><span v-if="msgType === 'win'">🎉</span>{{ msg }}<span v-if="msgType === 'win'">🎉</span></div>
  </div>
</template>

<script>
export default {
  name: 'callmyNotice',

  props: {
    msg: String,
    msgType: String,
  }
}
</script>

<style>
.callmy-notice-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px;
  min-width: 30vw;
  min-height: 20vh;
}

.callmy-notice-container-msg {
  font-weight: 700;
  font-size: 30px;
}
</style>
