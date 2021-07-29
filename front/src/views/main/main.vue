<template>
  <div class="main-wrapper">
    <Navbar/>
    <router-view></router-view>
  </div>
</template>
<style>
  @import "https://unpkg.com/element-plus/lib/theme-chalk/index.css";
  @import './main.css';
  @import '../../common/css/common.css';
  @import '../../common/css/element-plus.css';
</style>
<script>
import Navbar from './navbar.vue'
export default {
  name: 'Main',
  components: {
    Navbar,
  }
}
</script>
