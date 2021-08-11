<template>
  <div class="chat-container" style="border-radius: 0px 10px 10px 0px">

    <span class="chat-header" style="border-radius: 20px">Live Chat</span>
    <span class="close-chat"><i class="el-icon-close"></i></span>

    <div class="chat-area" ref="chatArea" style="border-radius: 0px">
      <div v-for="(chat, idx) in state.chatList" :key="idx">

        <div v-if="chat.userName === state.myUserName">
          <div v-if="idx === 0 || state.chatList[idx].userName !== state.chatList[idx - 1].userName" class="chat-profile-username-me">
            <img v-if="chat.profileImageURL" class="chat-profile" :src="chat.profileImageURL">
            <img v-else class="chat-profile" :src="require('@/assets/images/Neurang.png')">
            <span class="chat-username">{{ chat.userName }}</span>
          </div>
          <div class="chat-me"><div class="chat-content-me">{{ chat.content }}</div></div>
        </div>

        <div v-else>
          <div v-if="idx === 0 || state.chatList[idx].userName !== state.chatList[idx - 1].userName" class="chat-profile-username-you">
            <img v-if="chat.profileImageURL" class="chat-profile" :src="chat.profileImageURL">
            <img v-else class="chat-profile" :src="require('@/assets/images/Neurang.png')">
            <span class="chat-username">{{ chat.userName }}</span>
          </div>
          <div><div class="chat-content-you">{{ chat.content }}</div></div>
        </div>

      </div>
    </div>

    <div class="chat-input-button" style="border-radius: 0px">
      <input @keyup.enter="sendMessage" class="chat-input" style="border-radius: 30px 0px 0px 30px;" placeholder="내용을 입력해주세요." v-model="state.myChat">
      <div @click="sendMessage" class="chat-send-button" style="border-radius: 0px 30px 30px 0px;"><i class="el-icon-edit send-icon"></i></div>
    </div>

  </div>
</template>
<style scoped>
  @import url('./callmy-chat.css');
</style>

<script>
import { reactive } from '@vue/reactivity'
import { useStore } from 'vuex'
import { computed, ref, onUpdated } from 'vue'

export default {
  name: 'CallMyChat',
  props: {
    roomId: {
      type: Number,
    },
    userList: {
      type: Array,
    },
    room: {
      type: Object,
    },
    chatList: {
      type: Array,
    },
  },
  setup(props, { emit }) {
    const store = useStore()
    const chatArea = ref(null);

    const state = reactive({
      myChat: '',
      chatList: computed(() => props.chatList),
      myUserName: computed(() => store.state.root.username),
    })

    const sendMessage = () => {
      emit('sendMessage', state.myChat)
      state.myChat = ''
    }

    onUpdated(() => {
      chatArea.value.scrollTop = chatArea.value.scrollHeight;
    })



    return { state, sendMessage, chatArea }
  }
}
</script>


