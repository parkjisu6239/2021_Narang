<template>
  <div class="friend">
    <img src="https://www.ilbe.com/files/attach/new/20160525/28622079/9769697/8117463623/a3fae9a81bb12d364d5adeb3c1202ccf.jpeg" alt="">
    <div>
      <div class="nickname">칭긔 이름</div>
      <div class="email">friend@exp.com</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'friend'
}
</script>

<style>
.friend {
  display: grid;
  grid-template-columns: 1fr 3fr;

}

.friend img {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}

.friend .nickname {
  font-weight: 500;
  font-size: 14px;
}

.friend .email{
  font-weight: 500;
  font-size: 10px;
}

</style>
