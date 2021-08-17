<template>
  <div class="title">
    <div class="narang">나랑</div>
  </div>
</template>

<script>
export default {
  name: "Title"
}
</script>

<style scoped>
.title {
  color: white;
}

.title .narang {
  font-size: 100px;
  font-weight: 700;
  display: inline-block;
}

.narang::after {
  font-size: 16px;
  font-weight: 200;
  content: '';
  animation: typing 5s 5s infinite;
}

@keyframes typing {
  0% {
    content: '';
  }
  4% {
    content: '|';
  }
  8% {
    content: '';
  }
  12% {
    content: '아|';
  }
  16% {
    content: '아이|';
  }
  20% {
    content: '아이스|';
  }
  24% {
    content: '아이스브|';
  }
  28% {
    content: '아이스브레|';
  }
  32% {
    content: '아이스브레|';
  }
  36% {
    content: '아이스브레이|';
  }
  40% {
    content: '아이스브레이킹|';
  }
  44% {
    content: '아이스브레이킹 |';
  }
  48% {
    content: '아이스브레이킹 웹|';
  }
  52% {
    content: '아이스브레이킹 웹 |';
  }
  56% {
    content: '아이스브레이킹 웹 화|';
  }
  60% {
    content: '아이스브레이킹 웹 화상|';
  }
  64% {
    content: '아이스브레이킹 웹 화상 |';
  }
  68% {
    content: '아이스브레이킹 웹 화상 게|';
  }
  72% {
    content: '아이스브레이킹 웹 화상 게임|';
  }
  76% {
    content: '아이스브레이킹 웹 화상 게임 |';
  }
  80% {
    content: '아이스브레이킹 웹 화상 게임 서|';
  }
  84% {
    content: '아이스브레이킹 웹 화상 게임 서비|';
  }
  88% {
    content: '아이스브레이킹 웹 화상 게임 서비스|';
  }
  92% {
    content: '아이스브레이킹 웹 화상 게임 서비스';
  }
  96% {
    content: '아이스브레이킹 웹 화상 게임 서비스|';
  }
  100% {
    content: '아이스브레이킹 웹 화상 게임 서비스';
  }
}

</style>
