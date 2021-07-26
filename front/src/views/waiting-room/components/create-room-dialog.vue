<template>
  <el-dialog title="방만들기" v-model="state.dialogVisible" @close="handleClose">
    <el-form ref="createRoomForm" :model="state.form" :rules="state.rules" label-width="120px">
    <el-form-item prop="name" label="방 이름">
      <el-input v-model="state.form.name"></el-input>
    </el-form-item>
    <el-row>
      <el-col :span="9">
        <el-form-item prop="secret" label="비밀방 여부">
          <el-switch v-model="state.form.secret"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="15">
        <el-form-item prop="password" label="비밀번호">
          <el-input v-model="state.form.password" :disabled="!state.form.secret"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
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

export default {
  name: 'createRoomDialog',

  props: {
    open: {
      type: Boolean,
      default: false
    }
  },

  setup (props, { emit }) {
    const createRoomForm = ref(null)

    const state = reactive({
      form: {
        name: '',
        secret: false,
        password: '',
        num: 6
      },
      rules: {
        name: [
          { required: true, message: 'Please Input the nickname', trigger: 'blur' },
          { message: 'You can enter up to 20 characters', trigger: 'change', max: 20 }
        ],
        password: [
          { message: '최대 6자.', trigger: 'change', max: 6 },
          { message: '최소 4자', trigger: 'change', min: 4 },
          { message: '숫자만 가능합니다', trigger: 'change', pattern: /(?=.*\d{4,6})$/ }
        ],
      },
      dialogVisible: computed(() => props.open),
    })

    const clickCreateRoom = function() {
        createRoomForm.value.validate((valid) => {
          if (valid) {
            console.log('submit')
            // action 보내기
            //.then -> 방 생성 성공, 모달 닫고, push
            // catch -> 방 생성 실패, ElMessage 에러, 모달 닫기
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

</style>
