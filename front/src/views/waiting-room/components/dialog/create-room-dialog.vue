<template>
  <el-dialog title="방만들기" v-model="state.dialogVisible" @close="handleClose">
    <el-form ref="createRoomForm" :model="state.form" :rules="state.rules" label-width="120px">
    <el-form-item prop="name" label="방 이름">
      <el-input v-model="state.form.name"></el-input>
    </el-form-item>
    <el-form-item prop="secret" label="비밀방 여부">
      <el-switch v-model="state.form.secret"></el-switch>
    </el-form-item>
    <el-form-item prop="password" label="비밀번호">
      <el-input v-model="state.form.password" :disabled="!state.form.secret"></el-input>
    </el-form-item>
    <el-form-item prop="num" label="최대인원">
      <el-input-number v-model="state.form.num" :min="1" :max="9"></el-input-number>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="clickCreateRoom">Create</el-button>
      <el-button @click="handleClose">Cancel</el-button>
    </el-form-item>
  </el-form>
  </el-dialog>
</template>

<script>
import { reactive, computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

export default {
  name: 'createRoomDialog',

  props: {
    open: {
      type: Boolean,
      default: false
    },
  },

  setup (props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const createRoomForm = ref(null)

    const state = reactive({
      form: {
        name: '',
        secret: false,
        password: null,
        num: 6
      },
      rules: {
        name: [
          { required: true, message: 'Please Input the nickname', trigger: 'blur' },
          { message: 'You can enter up to 20 characters', trigger: 'change', max: 20 }
        ],
        password: [
          { message: '4자리 숫자만 가능합니다', trigger: 'change', pattern: /^[0-9]{4,4}$/ }
        ],
      },
      dialogVisible: computed(() => props.open),
    })

    const clickCreateRoom = function() {
        createRoomForm.value.validate((valid) => {
          if (valid) {
            const password = state.form.secret ? state.form.password : 0
            const payload = {
              title: state.form.name,
              password: password,
              maxPlayer: state.form.num
            }

            store.dispatch('root/requestCreateGameRoom', payload)
            .then(function (result) {
              const roomId = result.data.roomId
              store.commit('root/setRoomInfo', result.data.roomId)
              ElMessage({
                message: '방생성 완료!',
                type: 'success',
              })
              handleClose()

              store.dispatch('root/requestEnterGameRoom', { roomId, password })
              .then(function (result) {
                router.push({
                  name: 'gameRoom',
                  params: {
                    roomId,
                  }
                })
              })
              .catch(function (err) {
                ElMessage.error(err.response.data.message)
              })
            })
            .catch(function (err) {
              ElMessage.error('방생성 실패')
            })
            } else {
              ElMessage.error('Validate error!');
            }
        })
      }

    const handleClose = function () {
      emit('closeCreateRoomDialog')
    }

    return { createRoomForm, state, handleClose, clickCreateRoom }
  }
}
</script>

<style>
.el-dialog {
  min-width: 400px;
  max-width: 400px;
}

.el-dialog__body {
  padding: 50px 70px 20px 20px;
}

.setting-secret .el-input__inner{
  max-width: 200px;
}
</style>
