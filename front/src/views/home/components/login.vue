<template>
  <el-form :model="state.form" :rules="state.rules" ref="loginForm" :label-position="state.form.align">
    <el-form-item prop="email" label="이메일" :label-width="state.formLabelWidth" >
      <el-input v-model="state.form.email" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item prop="password" label="비밀번호" :label-width="state.formLabelWidth">
      <el-input v-model="state.form.password" autocomplete="off" show-password></el-input>
    </el-form-item>
  </el-form>
  <el-button type="primary" @click="clickLogin">로그인</el-button>
</template>

<script>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'

export default {
  name: "login",

  setup(props, { emit }) {
    const store = useStore()
    const loginForm = ref(null)

    const state = reactive({
      form: {
        email: '',
        password: '',
        align: 'left'
      },
      rules: {
        email: [
          { required: true, message: 'Please input email', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please input password', trigger: 'blur' }
        ]
      },
      formLabelWidth: '100px'
    })

    const clickLogin = function () {
      loginForm.value.validate((valid) => {
        if (valid) {
          console.log('submit')
          store.dispatch('root/requestLogin', { email: state.form.email, password: state.form.password })
          .then(function (result) {
            localStorage.setItem('access_token', result.data.accessToken) // 로컬스토리지에 토큰 저장
            localStorage.setItem('email', state.form.email) // 로컬스토리지에 아이디 저장
            store.commit('root/setAccessToken')
          })
          .catch(function (err) {
            ElMessage.error(err)
          })
        } else {
          ElMessage.error('Validate error!')
        }
      });
    }

    return { loginForm, state, clickLogin }
  }
}
</script>

<style>

</style>
