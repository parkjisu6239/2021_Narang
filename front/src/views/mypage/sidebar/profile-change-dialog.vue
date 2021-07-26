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
      <input
        class="image-uploader"
        type="file"
        ref="imageTag"
        @change="uploadImage"
        accept="image/*">
    </ul>
  </el-dialog>
</template>
<style>
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
      formData.append('profile-image',imageTag.value.files[0])
      store.dispatch('root/requestUpdateMyInfo', formData)
        .then(res => {
          Elmessage({
            message: '프로필 이미지 변경이 완료되었습니다.',
            type: 'success',
          })
        })
        .catch(err => {
          Elmessage({
            message: '수정에 실패했습니다.',
            type: 'error',
          })

        })
    }

    return { state, handleClose, uploadImage, openUploader, imageTag }
  }
}
</script>
