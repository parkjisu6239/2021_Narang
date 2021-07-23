<template>
  <el-form class="user-info-change-form" ref="form" status-icon :model="state.form" label-width="120px">
    <el-form-item label="email">
      <el-input v-model="state.form.email"></el-input>
    </el-form-item>
    <el-form-item label="username">
      <el-input v-model="state.form.username"></el-input>
    </el-form-item>
    <el-form-item label="image">
      <el-input v-model="state.form.image"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="changeInfo">Submit</el-button>
      <el-button @click="changeInfo">편집하기</el-button>
    </el-form-item>
  </el-form>
</template>
<style>
  @import url('./user-info-change.css');
</style>
<script>
import { reactive, ref, onBeforeMount } from 'vue'
import { useStore } from 'vuex'
export default {
  name: 'UserInfoChange',
  props: {

  },
  setup() {
    const store = useStore()

    const state = reactive({
      form: {
        email: ref(''),
        username: ref(''),
        image: ref(''),
      }
    })

    const changeInfo = () => {
      const payload = {
        ...state.form
      }
      console.log(payload)
      store.dispatch('root/requestUpdateMyInfo', payload)
        .then(res => {
          console.log(res)
        })
        .catch(err => {
          alert('회원정보 수정에 실패했습니다.')
        })
    }

    onBeforeMount(() => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          state.form.email = res.data.email
          state.form.username = res.data.username
        })
        .catch(err => {
          console.log(err)
        })
    })

    return { state, changeInfo }
  }
}
</script>


