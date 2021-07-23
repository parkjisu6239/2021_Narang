<template>
  <el-button @click="DeleteUser">회원탈퇴</el-button>
</template>

<script>
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
export default {
  name: 'UserDelete',
  props: {

  },
  setup(props, { emit }){
    const store = useStore()
    const router = useRouter()

    const DeleteUser = () => {
      store.dispatch('root/requestDeleteMyInfo')
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
    }

    return { DeleteUser }
  }
}
</script>

<style>

</style>
