<template>
  <div class="user-password-container">
    <el-form
      :model="state.form"
      :rules="state.rules"
      class="user-password-change-form"
      ref="userPasswordForm"
      label-width="120px"
      status-icon
      label-position="left">
      <el-form-item prop="currentPassword" label="현재 비밀번호">
        <el-input
          v-model="state.form.currentPassword"
          autocomplete="off"
          type="password">
        </el-input>
      </el-form-item>
      <el-form-item prop="newPassword" label="새로운 비밀번호">
        <el-input
          v-model="state.form.newPassword"
          autocomplete="off"
          type="password">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="userPasswordChange">비밀번호 변경</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<style scoped>
  @import url('./user-password-change.css');
</style>
<script>
import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
export default {
  name: 'UserPasswordChange',
  setup() {
    const store = useStore()
    const route = useRoute()
    const userPasswordForm = ref(null)

    const state = reactive({
      form: {
        currentPassword: '',
        newPassword: '',
      },
      rules: {
        currentPassword: [
          { required: true, message: '현재 비밀번호를 적어주세요', trigger: ['blur'] }
        ],
        newPassword: [
          { required: true, message: 'Please Input the password', trigger: 'blur' },
          { message: '최대 16자까지 입력 가능합니다.', trigger: ['change', 'blur'], max: 16 },
          { message: '최소 9글자를 입력해야 합니다.', trigger: ['change', 'blur'], min: 9 },
          { message: '영문, 숫자, 특수문자가 조합되어야 합니다.', trigger: ['change', 'blur'], pattern: /(?=.*\d{1,14})(?=.*[~`!@#$%\^&*()_]{1,14})(?=.*[a-zA-Z]{1,14}).{9,16}$/ }
        ]
      }
    })

    const userPasswordChange = () => {
      const formData = new FormData()
      formData.append('currentPassword', state.form.currentPassword)
      formData.append('newPassword', state.form.newPassword)

      userPasswordForm.value.validate((valid) => {
        if (valid) {
          console.log('submit')
          store.dispatch('root/requestChangeMyPassword', formData)
          .then(res => {
            store.dispatch('root/requestLogout')
            router.push({name: 'home'})
            ElMessage({
              message: '비밀번호 변경이 완료되었습니다. 다시 로그인 해주세요',
              type: 'success',
            })
          })
          .catch(err => {
            ElMessage.error('서버 에러가 발생하였습니다. 잠시 후, 다시 시도해주세요');
          })
        } else {
          ElMessage.error('새로운 비밀번호의 형식이 잘못되었습니다.');
        }
      });
    }

    return { state, userPasswordChange, userPasswordForm }
  }
}
</script>

