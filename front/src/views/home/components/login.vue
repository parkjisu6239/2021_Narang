<template>
  <el-form :model="state.form" :rules="state.rules" ref="loginForm" :label-position="state.form.align">
    <el-form-item prop="email" label="이메일" :label-width="state.formLabelWidth" >
      <el-input v-model="state.form.email" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item prop="password" label="비밀번호" :label-width="state.formLabelWidth">
      <el-input v-model="state.form.password" autocomplete="off" show-password></el-input>
    </el-form-item>
  </el-form>
  <div class="login-btn" @click="clickLogin">로그인</div>
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
          { required: true, message: '이메일을 입력해주세요.', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '비밀번호를 입력해주세요.', trigger: 'blur' }
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

            store.dispatch('root/requestReadMyInfo')
            .then(res => {
              const userInfo = {
                email: res.data.user.email,
                username: res.data.user.username,
                profileImageURL: res.data.user.thumbnailUrl,
              }
              store.commit('root/setUserInfo', userInfo)
              localStorage.setItem('username', res.data.user.username)
              localStorage.setItem('profileImageURL', res.data.user.thumbnailUrl)
            })
            .catch(err => {
              ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
            })
          })
          .catch(function (err) {
            ElMessage.err('오류가 발생했습니다. 잠시후 다시 시도해주세요.')
          })
        } else {
          ElMessage.error('다시 입력주세요.')
        }
      });
    }

    return { loginForm, state, clickLogin }
  }
}
</script>

<style scoped>
.login-btn {
  background: linear-gradient(90deg, #00D1FF 0%, #007BFF 100%);
  text-align: center;
  border-radius: 40px;
  width: 90%;
  padding: 10px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.login-btn:hover {
  background: linear-gradient(90deg, #00b2da 0%, #006cdf 100%);
  box-shadow: inset 0px -3px 2px rgba(0, 0, 0, 0.025),
              inset 0px 2px 5px rgba(0,0,0,0.15);
}
</style>
