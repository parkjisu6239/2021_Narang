<template>
  <el-dialog
    title="프로필 사진 변경"
    v-model="state.dialogVisible"
    width="25%"
    center="true"
    @close="handleClose">
    <ul class="profile-change-dialog-menu">
      <li @click="openUploader">
        사진 업로드
      </li>
      <li @click="openUploader" style="color: red">
        프로필 사진 삭제
      </li>
      <input
        class="image-uploader"
        type="file"
        ref="imageTag"
        @change="uploadImage"
        accept="image/*">
    </ul>
  </el-dialog>
</template>
<style scoped>
  @import url('./profile-change-dialog.css');
</style>
<script>
import { ref } from 'vue'
import { useStore } from 'vuex'
import { reactive } from '@vue/reactivity'
import { computed } from '@vue/runtime-core'
import { ElMessage } from 'element-plus'
export default {
  name: 'ProfileChangeDialog',
  props: {
    open: {
      type: Boolean,
      default: false,
    }
  },
  setup(props, { emit }) {
    const store = useStore()
    const imageTag = ref(null)
    const state = reactive({
      dialogVisible: computed(() => props.open )
    })

    const handleClose = () => {
      emit('closeProfileChangeDialog')
    }

    const openUploader = () => {
      imageTag.value.click()
    }

    const uploadImage = () => {
      const formData = new FormData()
      console.log(imageTag.value.files[0], '이미지 폼 데이타')
      formData.append('file', imageTag.value.files[0])
      store.dispatch('root/requestUpdateMyInfo', formData)
        .then(res => {
            store.dispatch('root/requestReadMyInfo')
              .then(res => {
                const userInfo = {
                  email: res.data.user.email,
                  username: res.data.user.username,
                  profileImageURL: res.data.user.thumbnailUrl, // null
                }
                console.log(res.data.user.thumbnailUrl)
                store.commit('root/setUserInfo', userInfo)
              })
              .catch(err => {
                console.log(err)
              })

          ElMessage({
            message: '프로필 이미지 변경이 완료되었습니다.',
            type: 'success'
          })

          handleClose()
        })
        .catch(err => {
          console.log(err)
          ElMessage({
            message: '수정에 실패했습니다.',
            type: 'error',
          })

        })
    }

    return { state, handleClose, uploadImage, openUploader, imageTag }
  }
}
</script>
