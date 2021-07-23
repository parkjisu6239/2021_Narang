<template>
  <h1>이것은 회원가입폼</h1>
  <el-form :model="state.form" :rules="state.rules" ref="signupForm" :label-position="state.form.align">
    <el-row :gutter="20">
      <el-col :span="21">
        <el-form-item prop="email" label="이메일" :label-width="state.formLabelWidth">
          <el-input v-model="state.form.email" autocomplete="off" @change="state.emailCheck = 0"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="3">
        <el-button :type="state.duplColor[state.emailCheck]" icon="el-icon-check" circle @click="checkEmail"></el-button>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="21">
        <el-form-item prop="username" label="닉네임" :label-width="state.formLabelWidth" >
          <el-input v-model="state.form.username" autocomplete="off" @change="state.userNameCheck = 0"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="3">
        <el-button :type="state.duplColor[state.userNameCheck]" icon="el-icon-check" circle @click="checkUserName"></el-button>
      </el-col>
    </el-row>
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
import { useRouter } from 'vue-router'

export default {
  name: "signup",

  setup( props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const signupForm = ref(null)

    const checkPass = (rule, value, callback) => {
      if (value !== state.form.password) {
        callback(new Error('Two inputs don\'t match!'));
      } else {
        callback();
      }
    };

    const state = reactive({
      emailCheck: 0,
      userNameCheck: 0,
      duplColor: ['secondary', 'success', 'danger'],
      form: {
        username: '',
        email: '',
        password: '',
        checkPass: '',
        align: 'left'
      },
      rules: {
        username: [
          { required: true, message: 'Please Input the nickname', trigger: 'blur' },
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

    const checkEmail = function() {
      if (state.form.email.replace(/ /g, "") === "") {
        ElMessage.error('Please input email address');
        return
      }

      // axios 보냄
      store.dispatch('root/requestEmailCheck', { email: state.form.email })
      .then(function (result) {
        state.emailCheck = 1 // 성공하면 초록색
        ElMessage({
          message: '사용 가능한 이메일입니다',
          type: 'success',
        })
      })
      .catch(function (err) {
        state.emailCheck = 2 // 실패하면 빨간색
        ElMessage.error('This email already exists');
      })
    }

    const checkUserName = function() {
      if (state.form.username.replace(/ /g, "") === "") {
        ElMessage.error('Please input nickname');
        return
      }

      // axios 보냄
      store.dispatch('root/requestNameCheck', { username: state.form.username })
      .then(function (result) {
        state.userNameCheck = 1 // 성공하면 초록색
        ElMessage({
          message: '사용 가능한 닉네임입니다',
          type: 'success',
        })
      })
      .catch(function (err) {
        state.userNameCheck = 2 // 실패하면 빨간색
        ElMessage.error('This nickname already exists');
      })
    }

    const clickSignup = function() {
      if (state.emailCheck != 1) {
        ElMessage.error('Please double check email');
        return
      }

      if (state.userNameCheck != 1 ) {
        ElMessage.error('Please double check nickname');
        return
      }

      signupForm.value.validate((valid) => {
        if (valid) {
          console.log('submit')
          store.dispatch('root/requestSignup', {
            email: state.form.email,
            password: state.form.password,
            username: state.form.username,
          })
          .then(function (result) {
            ElMessage({
              message: 'Congrats, signup is success',
              type: 'success',
            })

            emit('signupSuccess')

          })
          .catch(function (err) {
            ElMessage.error('회원가입 실패. 다시 시도해');
          })
        } else {
          ElMessage.error('Validate error!');
        }
      });
    }

    return { signupForm, state, clickSignup, checkEmail, checkUserName }
  }
}
</script>


<style>

</style>
