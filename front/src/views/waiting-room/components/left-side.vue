<template>
  <div>
    <div class="my-info">
      <img src="https://myanimals.co.kr/wp-content/uploads/2018/12/domestic-duck-species.jpg" alt="" style="width: 100px">
      <h3>내 닉네임 넣을 예정</h3>
      <h3>내 이메일 넣을 예정</h3>
    </div>
    <div class="friend-list" v-for="i in 3" :key="i">
      <Friend />
    </div>
    <el-row class="btn-group">
      <el-button type="primary" icon="el-icon-edit" circle></el-button>
      <el-button type="success" icon="el-icon-check" circle></el-button>
      <el-button type="info" icon="el-icon-message" circle></el-button>
      <el-button type="warning" icon="el-icon-star-off" circle></el-button>
      <el-button type="danger" icon="el-icon-delete" circle></el-button>
    </el-row>
  </div>
</template>

<script>
import Friend from './friend.vue'

export default {
  name: "leftSide",

  components: {
    Friend
  }
}
</script>

<style>

</style>
