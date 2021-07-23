<template>
  <el-form
    :model="state.form"
    :rules="state.rules"
    ref="userInfoForm"
    class="user-info-change-form"
    label-width="90px"
    status-icon
    label-position="left">
    <el-form-item prop="email" label="email">
      <el-input
        v-model="state.form.email"
        autocomplete="off"
        type="email"
        :disabled="true"></el-input>
    </el-form-item>
    <el-form-item prop="username" label="username">
      <el-input
        v-model="state.form.username"
        autocomplete="off"
        :disabled="!state.editMode"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button v-if="state.editMode" type="primary" @click="changeInfo">submit</el-button>
      <el-button v-if="state.editMode" type="danger" @click="initChangeInfo">back</el-button>
      <el-button @click="editModeToggle" v-if="!state.editMode">edit</el-button>
    </el-form-item>
  </el-form>
</template>
<style>
  @import url('./user-info-change.css');
</style>
<script>
import { ElMessage } from 'element-plus'
import { reactive, ref, onBeforeMount } from 'vue'
import { useStore } from 'vuex'
export default {
  name: 'UserInfoChange',
  props: {

  },
  setup(props, { emit }) {
    const store = useStore()
    const userInfoForm = ref(null)

    const state = reactive({
      form: {
        email: '',
        username: '',
      },
      image: '',
      rules: {
        email: [
          { message: '이메일 형식으로 적어주세요', trigger: ['blur', 'change'], required: true, pattern: /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i }
        ],
        username: [
          { message: '유저네임을 입력해주세요', trigger: ['blur', 'change'], required: true},
          { message: '최대 16자까지 입력이 가능합니다', trigger: ['blur', 'change'], max: 16 },
          { message: '최소 3자 이상 입력해야 합니다', trigger: ['blur', 'change'], min: 3},
        ]
      },
      editMode: false,
    })

    const initChangeInfo = () => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          state.form.email = res.data.email
          state.form.username = res.data.username
          editModeToggle()
        })
    }

    const changeInfo = () => {
      const payload = {
        ...state.form
      }
      store.dispatch('root/requestUpdateMyInfo', payload)
        .then(res => {
          editModeToggle()
          ElMessage({
            message: '수정이 완료되었습니다.',
            type: 'success',
          })
        })
        .catch(err => {
          ElMessage({
            message: '수정에 실패했습니다.',
            type: 'error',
          })
        })
    }

    const editModeToggle = () => {
        state.editMode = !state.editMode
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

    return { state, changeInfo, editModeToggle, initChangeInfo, userInfoForm }
  }
}
</script>


