<template>
  <el-form
    :model="state.form"
    :rules="state.rules"
    ref="userInfoForm"
    class="user-info-change-form"
    label-width="120px"
    status-icon
    label-position="left">

    <el-form-item prop="email" label="email">
      <el-input
        v-model="state.form.email"
        autocomplete="off"
        type="email"
        :disabled="true">
      </el-input>
    </el-form-item>

    <el-form-item prop="username" label="username">
      <el-input
        v-model="state.form.username"
        autocomplete="off"
        :disabled="!state.editMode">
      </el-input>
    </el-form-item>

    <el-form-item>
      <el-button v-if="state.editMode" type="danger" @click="initChangeInfo">back</el-button>
      <el-button v-if="state.editMode" type="primary" @click="changeInfo">submit</el-button>
      <el-button @click="editModeToggle" v-if="!state.editMode">edit</el-button>
    </el-form-item>

  </el-form>
</template>
<style scoped>
  @import url('./user-info-change.css');
</style>
<script>
import { ElMessage } from 'element-plus'
import { reactive, ref, onMounted } from 'vue'
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
      state.form = {
        email: store.getters['root/email'],
        username: store.getters['root/username']
      }
      editModeToggle()
    }

    const changeInfo = () => {
      const formData = new FormData()
      formData.append('username', state.form.username)

      store.dispatch('root/requestUpdateMyInfo', formData)
        .then(res => {
          const userInfo = {
            username: state.form.username,
            email: state.form.email,
            profileImageURL: state.form.thumbnailUrl,
          }
          store.commit('root/setUserInfo', userInfo)
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

    onMounted(() => {
      store.dispatch('root/requestReadMyInfo')
        .then(res => {
          const userInfo = {
            username: res.data.username,
            profileImageURL: res.data.thumbnailUrl,
            email: res.data.email,
          }
          state.form = {
            username: res.data.username,
            email: res.data.email
          }
          store.commit('root/setUserInfo', userInfo)
        })
        .catch(err => {
          console.log(err)
        })
    })


    return { state, changeInfo, editModeToggle, initChangeInfo, userInfoForm }
  }
}
</script>


