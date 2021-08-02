<template>
  <el-dialog title="방 수정하기" v-model="state.dialogVisible" @close="handleClose">
    <el-form ref="updateRoomForm" :model="state.form" :rules="state.rules" label-width="120px">

      <el-form-item prop="roomTitle" label="방 이름">
        <el-input v-model="state.form.title"></el-input>
      </el-form-item>

      <el-form-item prop="secret" label="비밀방 여부">
        <el-switch v-model="state.form.secret"></el-switch>
      </el-form-item>

      <el-form-item prop="password" label="비밀번호">
        <el-input v-model="state.form.password" type="password" :disabled="!state.form.secret"></el-input>
      </el-form-item>

      <el-form-item prop="maxNum" label="최대인원">
        <el-input-number v-model="state.form.maxPlayer" :min="1" :max="9"></el-input-number>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="updateRoomSetting">설정 완료</el-button>
        <el-button @click="handleClose">취소</el-button>
      </el-form-item>

    </el-form>
  </el-dialog>
</template>

<script>
import { ElMessage } from 'element-plus'
import { reactive } from '@vue/reactivity'
import { computed } from '@vue/runtime-core'
import { useStore } from 'vuex'
import { ref, watch } from 'vue'
export default {
  name: 'GameRoomInfoChangeDialog',
  props: {
    open: {
      type: Boolean,
      default: false
    },
    roomId: {
      type: Number,
    },
  },
  setup(props, { emit }) {
    const store = useStore()
    const updateRoomForm = ref(null)
    const state = reactive({
      dialogVisible: computed(() => props.open),
      form: {
        roomTitle: null,
        secret: null,
        password: null,
        maxPlayer: null,
      },
      rules: {
        roomTitle: [
          { required: true, message: 'Please Input the room title', trigger: 'blur' },
          { message: 'You can enter up to 20 characters', trigger: 'change', max: 20 }
        ],
        password: [
          { message: '4자리 숫자만 가능합니다', trigger: 'change', pattern: /^[0-9]{4,4}$/ }
        ],
      },
    })

    const handleClose = () => {
      emit('closeDialog')
    }

    const setInitialValue = () => {
      const newForm = {
        title: store.state.root.myRoom.title,
        secret: store.state.root.myRoom.password ? true : false,
        password: store.state.root.myRoom.password,
        maxPlayer: store.state.root.myRoom.maxPlayer,
      }

      state.form = newForm
    }

    const updateRoomSetting = () => {
      store.dispatch('root/requestUpdateGameRoom', {
        ...state.form,
        roomId: props.roomId,
      })
      .then(res => {
        ElMessage({
          type: 'success',
          message: '방 정보가 수정되었습니다.'
        })
        handleClose()
        // 방에 있는 사람들에게 푸쉬하기
        // 방에 있는 사람들에게 수정 요청 보내기.
      })
      .catch(err => {
        ElMessage({
          type: 'error',
          message: '실패했습니다.'
        })
      })
    }

    watch(props, () => {
      setInitialValue()
    })

    return { state, handleClose, updateRoomSetting, updateRoomForm, store }
  }
}
</script>

<style>

</style>
