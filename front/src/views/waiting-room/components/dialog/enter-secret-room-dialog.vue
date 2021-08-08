<template>
  <el-dialog title="비밀방 입장" v-model="state.dialogVisible" @close="handleClose">
    <el-form ref="enterSecretRoomForm" :model="state.form" :rules="state.rules" label-width="120px">
      <el-form-item prop="password" label="비밀번호">
        <el-input v-model="state.form.password"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="clickEnterRoom">Enter</el-button>
        <el-button @click="handleClose">Cancel</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
import { reactive, computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'enterSecretRoomDialog',

  props: {
    open: {
      type: Boolean,
      default: false
    },
    room: {
      type: Object
    }
  },

  setup (props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const enterSecretRoomForm = ref(null)

    const state = reactive({
      form: {
        password: null,
      },
      rules: {
        password: [
          { message: '4자리 숫자입니다.', trigger: 'change', pattern: /^[0-9]{4,4}$/ }
        ],
      },
      dialogVisible: computed(() => props.open),
    })

    const clickEnterRoom = function() {
        console.log(props.room)
        enterSecretRoomForm.value.validate((valid) => {
          if (valid) {
            store.dispatch('root/requestEnterGameRoom', { roomId: props.room.roomId, password: state.form.password })
            .then(function (result) {
              console.log(result, '성공함')
              store.commit('root/setRoomInfo', props.room)
              handleClose()
              router.push({
                name: 'gameRoom',
                params: {
                  roomId: props.room.roomId
                }
              })
            })
            .catch(function (err) {
              ElMessage.error(err.response.data.message)
            })

          } else {
            ElMessage.error('Validate error!');
          }
        })
      }

    const handleClose = function () {
      emit('closeEnterSecretRoomDialog')
    }

    return { enterSecretRoomForm, state, handleClose, clickEnterRoom }
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
