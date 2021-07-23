<template>
  <div class="mypage-container">
    <Sidebar/>
    <MyPageSection/>
  </div>
</template>

<style>
  @import url('./mypage.css');
</style>
<script>
import Sidebar from './sidebar/sidebar.vue'
import MyPageSection from './section/my-page-section.vue'

export default {
  name: 'Mypage',
  components: {
    Sidebar,
    MyPageSection,
  },
}
</script>
