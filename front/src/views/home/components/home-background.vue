<template>
  <div class="home-background">
    <div class="text1">Welcome to Narnag</div>
    <div class="text2">Hello Narnag</div>
    <div class="text2">Let's play our nicccce game</div>
    <div class="text2">Narang video game</div>
    <div class="text1">Welcome to Narnag</div>
  </div>
</template>

<script>
export default {
  name: 'homeBackground'
}
</script>

<style scoped>
.home-background {
  position: absolute;
  top: 0px;
  z-index: -10;
  width: 100%;
  height: 100%;
}

.home-background div {
  position: relative;
  left: -20px;
  color: rgba(255, 255, 255, 0.05);
  font-weight: 900;
  font-size: 300px;
  width: 200vw;
  margin: 0px;
  letter-spacing: -10px;
  word-spacing: -10px;
  line-height: 240px;
  transform: translate(0px, 30px) rotate(5deg);
  animation: move 5s ease-in-out infinite;
}

@keyframes move {
  0% {
    transform: translate(0px, 30px) rotate(5deg);
    color: rgba(255, 255, 255, 0.05);
  }
  50% {
    transform: translate(0px, 50px) rotate(5deg);
    color: rgba(255, 255, 255, 0.1);
  }
  100% {
    transform: translate(0px, 30px) rotate(5deg);
    color: rgba(255, 255, 255, 0.05);
  }
}
</style>
