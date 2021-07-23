<template>
  <el-dialog
    title="방 설정"
    v-model="state.dialogVisible"
    @close="handleClose">
    <span style="margin-right: 10px;">제목</span>
    <el-input placeholder="변경할 제목" v-model="state.roomTitle"></el-input>

    <span style="margin-right: 10px;">최대인원</span>
    <el-select v-model="state.value" filterable placeholder="Select">
      <el-option
        v-for="item in state.options"
        :key="item.value"
        :label="item.label"
        :value="item.value">
      </el-option>
    </el-select>

  </el-dialog>
</template>

<script>
import { reactive } from '@vue/reactivity'
import { computed } from '@vue/runtime-core'
export default {
  name: 'GameRoomInfoChangeDialog',
  props: {
    open: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    const state = reactive({
      dialogVisible: computed(() => props.open),
      value: '',
      roomTitle: '',
      options: [
        {value: 4, label: '4명'},
        {value: 5, label: '5명'},
        {value: 6, label: '6명'},
        {value: 7, label: '7명'},
        {value: 8, label: '8명'},
        {value: 9, label: '9명'},
      ]

    })

    const handleClose = () => {
      emit('closeDialog')
    }

    return { state, handleClose }
  }
}
</script>

<style>

</style>
