<template>
  <el-container>
    <el-form :model="state.form" :rules="state.rules" ref="editForm" :label-position="state.form.align" :disabled="!state.editMode">
      <el-form-item prop="id" label="아이디" :label-width="state.formLabelWidth">
        <el-input v-model="state.form.id" autocomplete="off" disabled="true"></el-input>
      </el-form-item>
      <el-form-item prop="name" label="이름" :label-width="state.formLabelWidth" >
        <el-input v-model="state.form.name" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item prop="department" label="소속" :label-width="state.formLabelWidth" >
        <el-input v-model="state.form.department" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item prop="position" label="직책" :label-width="state.formLabelWidth" >
        <el-input v-model="state.form.position" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
  </el-container>
  <div v-if="!state.editMode">
    <el-button type="primary" @click="state.editMode = !state.editMode">회원정보 수정</el-button>
    <el-button class="delete-btn" type="danger" @click="clickDelete">회원 탈퇴</el-button>
  </div>
  <div v-else>
    <el-button type="primary" @click="clickUpdate">수정</el-button>
    <el-button @click="state.editMode = !state.editMode">취소</el-button>
  </div>
</template>

<style>
section.el-container {
  justify-content: center;
}
</style>

<script>
import { reactive, ref, onBeforeMount } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Mypage',
  setup () {
    const store = useStore()
    const router = useRouter()
    const editForm = ref(null)

    const state = reactive({
      form: {
        name: '',
        department: '',
        position: '',
        id: '',
        align: 'left'
      },
      rules: {
        name: [
          { required: true, validator: validateName, trigger: 'blur' }
        ],
        department: [
          { validator: validateDepartment, trigger: 'blur' }
        ],
        position: [
          { validator: validatePosition, trigger: 'blur' }
        ]
      },
      editMode: false,
      formLabelWidth: '120px'
    })

    const validateName = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please input the Name'));
      } else if (value.length > 30) {
        callback(new Error('You can enter up to 30 characters'))
      } else {
        callback();
      }
    };

    const validateDepartment = (rule, value, callback) => {
      if (value.length > 30) {
        callback(new Error('You can enter up to 30 characters'))
      } else {
        callback();
      }
    };

    const validatePosition = (rule, value, callback) => {
      if (value.length > 30) {
        callback(new Error('You can enter up to 30 characters'))
      } else {
        callback();
      }
    };


    const getUserInfo = () => {
      store.dispatch('root/requestReadMyInfo')
      .then(function (result) {
        console.log(result)
        state.form.id = result.data.userId
        state.form.name = result.data.name
        state.form.department = result.data.department
        state.form.position = result.data.position
      })
      .catch(function (err) {
        ElMessage.error(err)
      })
    }

    const clickUpdate = function() {
      console.log(editForm)
      editForm.value.validate((valid) => {
        if (valid) {
          console.log('submit')
          store.dispatch('root/requestUpdateMyInfo', {
            name: state.form.name,
            department: state.form.department,
            position: state.form.position
          })
          .then(function (result) {
            ElMessage({
              message: '수정이 완료되었습니다.',
              type: 'success',
            })
            state.editMode = !state.editMode
          })
          .catch(function (err) {
            console.log(err)
          })
        } else {
          ElMessage.error('Validate error!');
        }
      });
    }

    const clickDelete = function() {
      store.dispatch('root/requestDeleteMyInfo')
      .then(function (result) {
        store.dispatch('root/requestLogout') // 로그아웃
        store.commit('root/deleteToken')
        router.push({
          name: 'home'
        })

        ElMessage({
          message: '회원탈퇴가 정상적으로 완료되었습니다.',
          type: 'success',
        })
      })
      .catch(function (err) {
        console.log(err)
      })
    }

    // 페이지 진입시 불리는 훅
    onBeforeMount (() => {
      getUserInfo()
    })

    return { editForm, state, clickUpdate, clickDelete }
  }
}
</script>
