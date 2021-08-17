<template>
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
  <div class="signup-btn" @click="clickSignup">회원가입</div>
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
        callback(new Error('비밀번호가 일치하지 않습니다.'));
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
          { required: true, message: '닉네임을 입력해주세요.', trigger: 'blur' },
          { message: '닉네임은 최대 30자만 가능합니다.', trigger: 'change', max: 30 }
        ],
        email: [
          { required: true, message: '이메일을 입력해주세요.', trigger: 'blur' },
          { type: 'email', message: '이메일 형식이 아닙니다.', trigger: ['blur', 'change'] }
        ],
        password: [
          { required: true, message: '비밀번호를 입력해주세요.', trigger: 'blur' },
          { message: '최대 16자까지 입력 가능합니다.', trigger: 'change', max: 16 },
          { message: '최소 9글자를 입력해야 합니다.', trigger: 'change', min: 9 },
          { message: '영문, 숫자, 특수문자가 조합되어야 합니다.', trigger: 'change', pattern: /(?=.*\d{1,14})(?=.*[~`!@#$%\^&*()_]{1,14})(?=.*[a-zA-Z]{1,14}).{9,16}$/ }
        ],
        checkPass: [
          {required: true, message: '비밀번호를 확인해주세요.', trigger: 'blur'},
          {validator: checkPass, trigger: 'change'},
        ],
      },

      formLabelWidth: '120px'
    })

    const checkEmail = function() {
      if (state.form.email.replace(/ /g, "") === "") {
        ElMessage.error('이메일을 입력해주세요.');
        return
      }

      // axios 보냄
      store.dispatch('root/requestEmailCheck', { email: state.form.email })
      .then(function (result) {
        state.emailCheck = 1 // 성공하면 초록색
        ElMessage({
          message: '사용 가능한 이메일입니다.',
          type: 'success',
        })
      })
      .catch(function (err) {
        state.emailCheck = 2 // 실패하면 빨간색
        ElMessage.error('이미 존재하는 이메일입니다.');
      })
    }

    const checkUserName = function() {
      if (state.form.username.replace(/ /g, "") === "") {
        ElMessage.error('닉네임을 입력해주세요.');
        return
      }

      // axios 보냄
      store.dispatch('root/requestNameCheck', { username: state.form.username })
      .then(function (result) {
        state.userNameCheck = 1 // 성공하면 초록색
        ElMessage({
          message: '사용 가능한 닉네임입니다.',
          type: 'success',
        })
      })
      .catch(function (err) {
        state.userNameCheck = 2 // 실패하면 빨간색
        ElMessage.error('이미 존재하는 닉네임입니다.');
      })
    }

    const clickSignup = function() {
      if (state.emailCheck != 1) {
        ElMessage.error('이메일 중복확인을 해주세요.');
        return
      }

      if (state.userNameCheck != 1 ) {
        ElMessage.error('닉네임 중복확인을 해주세요.');
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
              message: '축하합니다! 회원가입에 성공했습니다. 로그인 후 서비스 이용이 가능합니다.',
              type: 'success',
            })

            emit('signupSuccess')

          })
          .catch(function (err) {
            ElMessage.error('회원가입이 실패했습니다. 잠시후 다시 시도해주세요.');
          })
        } else {
          ElMessage.error('다시 입력해주세요.');
        }
      });
    }

    return { signupForm, state, clickSignup, checkEmail, checkUserName }
  }
}
</script>


<style scoped>
.signup-btn {
  background: linear-gradient(90deg, rgba(114, 65, 254, 0.8) 0%, #FF5BF8 100%);
  text-align: center;
  border-radius: 40px;
  width: 90%;
  padding: 10px;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.signup-btn:hover {
  background: linear-gradient(90deg, rgba(99, 57, 216, 0.8) 0%, #d44bce 100%);
  box-shadow: inset 0px -3px 2px rgba(0, 0, 0, 0.025),
              inset 0px 2px 5px rgba(0,0,0,0.15);
}
</style>
