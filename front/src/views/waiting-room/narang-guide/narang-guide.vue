<template>
  <div class="narang-guide-container">
    <header class="narang-guide-header">
      <div class="narang-guide-header-title-exit">
        <div class="narang-guide-title">How to play Narang!</div>
        <div class="narang-guide-exit" @click="clickExit"><span>x</span></div>
      </div>
      <hr>
    </header>
    <section>
      <div class="narang-guide-neurang"><img :src="require('@/assets/images/Neurang.png')" alt=""></div>
      <div class="narang-guide-neurang-text">
        <div class="bold">안녕! 나는 <span class="narang">나랑</span>의 마스코트이자, 그 자체인 '너랑이'야</div>
        <div><span class="narang">나랑</span> 을 즐기는 방법은 아주 간단해!</div>
        <div>여기 <span class="waiting">대기실</span>에서는 게임방을 생성하거나, 입장 할 수 있어</div>
        <div>오른쪽에 <button>방만들기</button>를 클릭하면, 방을 만들 수 있고 내가 방장이 되지!</div>
        <div>다른 친구가 만든 방에 들어가고 싶으면, 방을 클릭하면 돼!</div>
        <div>내가 원하는 방만 보고 싶으면 오른쪽 위에 검색버튼으로 원하는 조건의 방을 찾아봐!</div>
      </div>
    </section>
  </div>
</template>

<script>
import { reactive } from 'vue'

export default {
  name: 'naranGuide',

  setup(props, { emit }) {

    const state = reactive({
    })

    const clickExit = () => {
      emit('clickExit')
    }


    return { state, clickExit }
  }
}
</script>

<style>
  @import url('./narang-guide.css');
</style>
