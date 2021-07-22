<template>
  <h1>이것은 회원가입폼</h1>
  <el-form :model="state.form" :rules="state.rules" ref="signupForm" :label-position="state.form.align">
    <el-row :gutter="20">
      <el-col :span="21">
        <el-form-item prop="email" label="이메일" :label-width="state.formLabelWidth">
          <el-input v-model="state.form.email" autocomplete="off"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="3">
        <el-button :type="state.duplColor[state.duplicationCheck]" icon="el-icon-check" circle @click="checkId"></el-button>
      </el-col>
    </el-row>
    <el-form-item prop="name" label="이름" :label-width="state.formLabelWidth" >
      <el-input v-model="state.form.name" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item prop="password" label="비밀번호" :label-width="state.formLabelWidth">
      <el-input v-model="state.form.password" autocomplete="off" show-password></el-input>
    </el-form-item>
    <el-form-item prop="checkPass" label="비밀번호 확인" :label-width="state.formLabelWidth">
      <el-input v-model="state.form.checkPass" autocomplete="off" show-password></el-input>
    </el-form-item>
  </el-form>
  <el-button type="primary" @click="clickSignup">회원가입</el-button>
</template>

<script>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'

export default {
  name: "signUp",

  setup(props, { emit }) {
    const store = useStore()
    const signupForm = ref(null)

    const checkPass = (rule, value, callback) => {
      if (value !== state.form.password) {
        callback(new Error('Two inputs don\'t match!'));
      } else {
        callback();
      }
    };

    const state = reactive({
      duplicationCheck: 0,
      duplColor: ['secondary', 'success', 'danger'],
      form: {
        name: '',
        email: '',
        password: '',
        checkPass: '',
        align: 'left'
      },
      rules: {
        name: [
          { required: true, message: 'Please Input the name', trigger: 'blur' },
          { message: 'You can enter up to 30 characters', trigger: 'change', max: 30 }
        ],
        email: [
          { required: true, message: 'Please input email address', trigger: 'blur' },
          { type: 'email', message: 'Please input correct email address', trigger: ['blur', 'change'] }
        ],
        password: [
          { required: true, message: 'Please Input the password', trigger: 'blur' },
          { message: '최대 16자까지 입력 가능합니다.', trigger: 'change', max: 16 },
          { message: '최소 9글자를 입력해야 합니다.', trigger: 'change', min: 9 },
          { message: '영문, 숫자, 특수문자가 조합되어야 합니다.', trigger: 'change', pattern: /(?=.*\d{1,14})(?=.*[~`!@#$%\^&*()_]{1,14})(?=.*[a-zA-Z]{1,14}).{9,16}$/ }
        ],
        checkPass: [
          {required: true, message: 'Please Input the password confirm', trigger: 'blur'},
          {validator: checkPass, trigger: 'change'},
        ],
      },

      formLabelWidth: '120px'
    })

    const checkId = function() {
      // axios 보냄
      store.dispatch('root/requestCheckDuplicate', { id: state.form.id })
      .then(function (result) {
        state.duplicationCheck = 1 // 성공하면 초록색
      })
      .catch(function (err) {
        state.duplicationCheck = 2 // 실패하면 빨간색
        ElMessage.error('This email already exists');
      })
    }

    const clickSignup = function() {
      if (state.duplicationCheck != 1) {
        ElMessage.error('Please double check');
        return
      }

      signupForm.value.validate((valid) => {
        if (valid) {
          console.log('submit')
          store.dispatch('root/requestSignup', {
            email: state.form.email,
            password: state.form.password,
            name: state.form.name,
          })
          .then(function (result) {
            ElMessage({
              message: 'Congrats, signup is success',
              type: 'success',
            })
          })
          .catch(function (err) {
            ElMessage.error('회원가입 실패. 다시 시도해');
          })
        } else {
          ElMessage.error('Validate error!');
        }
      });
    }

    return { signupForm, state, clickSignup, checkId }
  }
}
</script>


<style>

</style>
