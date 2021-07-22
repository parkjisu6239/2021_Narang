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
      store.dispatch('root/requestUpdateMyInfo', payload)
        .then(res => {
          console.log(res)
        })
        .catch(err => {
          console.log(err)
        })
    }

    onBeforeMount(() => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          console.log(res)
        })
        .catch(err => {
          console.log(err)
        })
    })

    return { state, changeInfo }
  }
}
</script>


