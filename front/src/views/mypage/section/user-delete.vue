<template>
  <el-button @click="DeleteUser">회원탈퇴</el-button>
</template>

<script>
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
export default {
  name: 'UserDelete',
  props: {

  },
  setup(props, { emit }){
    const store = useStore

    const DeleteUser = () => {
      store.dispatch('requestDeleteMyInfo')
        .then(res => {
          store.dispatch('requestLogout')
          ElMessage({
            type: 'success',
            message: '회원탈퇴에 성공했습니다.'
          })
        })
    }

    return { DeleteUser }
  }
}
</script>

<style>

</style>
