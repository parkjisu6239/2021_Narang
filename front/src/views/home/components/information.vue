<template>
  <div class="guide">
    <section v-for="i in 9" :key="i">
      <img :src="require(`@/assets/images/info/info(${i}).png`)" alt="">
    </section>
  </div>
</template>

<script>
export default {
  name: "information"
}
</script>

<style>
.guide {
  display: grid;
  grid-auto-rows: repeat(auto-fill, 1fr);
  height: 350px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(5px);
  border-radius: 15px;
  box-shadow: inset 0px -3px 2px rgba(0, 0, 0, 0.025),
              inset 0px 2px 5px rgba(0,0,0,0.15);
  overflow: auto;
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}

.guide::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera*/
}

.guide section {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 350px;
  background-color: white;
  border-bottom: 1px solid rgb(202, 202, 202);
}

.guide section img{
  height: 100%;
  width: auto;
}
</style>
