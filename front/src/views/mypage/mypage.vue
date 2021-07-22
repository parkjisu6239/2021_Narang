<template>
  <div class="mypage-container">
    <Sidebar/>
    <MyPageSection/>
  </div>
</template>

<style>
  @import url('./mypage.css');
</style>
<script>
import Sidebar from './sidebar/sidebar.vue'
import MyPageSection from './section/my-page-section.vue'
import { reactive, ref, onBeforeMount } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Mypage',
  components: {
    Sidebar,
    MyPageSection,
  },
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
    // onBeforeMount (() => {
    //   getUserInfo()
    // })

    return { editForm, state, clickUpdate, clickDelete }
  }
}
</script>
