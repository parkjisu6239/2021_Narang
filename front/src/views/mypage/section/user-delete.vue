<template>
    <div class="user-delete-header">
      <img src="" alt="">
      <span>정말 탈퇴하시겠어요?</span>
    </div>

    <el-form
      :model="state.form"
      :rules="state.rules"
      ref="userDeleteForm"
      label-width="120px"
      label-position="left"
      class="user-delete-form"
      status-icon>

      <el-form-item prop="email" label="이메일" :label-width="state.formLabelWidth" >
        <el-input v-model="state.form.email" autocomplete="off"></el-input>
      </el-form-item>

      <el-form-item prop="password" label="비밀번호" :label-width="state.formLabelWidth">
        <el-input v-model="state.form.password" autocomplete="off" show-password></el-input>
      </el-form-item>

      <el-form-item>
        <el-button @click="deleteUser" type="danger">회원탈퇴</el-button>
      </el-form-item>

    </el-form>
</template>
<style scoped>
  @import url('./user-delete.css');
</style>
<script>
import { ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { reactive } from '@vue/reactivity'
export default {
  name: 'UserDelete',
  props: {

  },
  setup(props, { emit }){
    const store = useStore()
    const router = useRouter()
    const userDeleteForm = ref(null)

    const state = reactive({
      form: {
        email: '',
        password: '',
      },
      rules: {
        email: [
          { required: true, message: 'Please input email', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please input password', trigger: 'blur' }
        ]
      }
    })

    const deleteUser = () => {
      const loginInfo = {
        email: state.form.email,
        password: state.form.password,
      }
      if (loginInfo.email !=='' && loginInfo.password !=='') {
        store.dispatch('root/requestLogin', loginInfo)
          .then(res => {
            store.dispatch('root/requestDeleteMyInfo', loginInfo)
              .then(res => {
                store.dispatch('root/requestLogout')
                store.commit('root/setAccessToken')
                router.push({
                  name: 'home'
                })
                ElMessage({
                  type: 'success',
                  message: '회원탈퇴에 성공했습니다.'
                })
              })
              .catch(err => {
                ElMessage.error(err)
              })
          })
          .catch(err => {
            ElMessage({
              message: '로그인 정보가 잘못되었습니다.',
              type: 'error'
            })
          })
      }
    }

    return { deleteUser, userDeleteForm, state }
  }
}
</script>


