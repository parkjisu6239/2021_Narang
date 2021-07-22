<template>
  <el-container class="main-wrapper">
    <el-container class="main-container">
      <el-aside class="hide-on-small" width="240px">
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<style>
  @import "https://unpkg.com/element-plus/lib/theme-chalk/index.css";
  @import './main.css';
  @import '../../common/css/common.css';
  @import '../../common/css/element-plus.css';

</style>
<script>

export default {
  name: 'Main'
}
</script>
