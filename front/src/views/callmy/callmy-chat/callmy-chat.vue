<template>
  <div class="callmy-chat-container">
    <div class="callmy-chat-header" style="border-radius: 20px">
      <span class="callmy-chat-live">Live Chat</span>
    </div>
    <div class="callmy-chat-area" ref="chatArea" style="border-radius: 0px">
      <div v-for="(chat, idx) in state.chatList" :key="idx">
        <div v-if="chat.userName === state.myUserName">
          <div class="callmy-chat-me"><div class="callmy-chat-content-me">{{ chat.content }}</div></div>
        </div>
        <div v-else>
          <div v-if="idx === 0 || state.chatList[idx].userName !== state.chatList[idx - 1].userName" class="callmy-chat-profile-username-you">
            <img v-if="chat.profileImageURL" class="callmy-chat-profile" :src="chat.profileImageURL">
            <img v-else class="callmy-chat-profile" :src="require('@/assets/images/Neurang.png')">
            <span class="callmy-chat-username">{{ chat.userName }}</span>
          </div>
          <div><div class="callmy-chat-content-you">{{ chat.content }}</div></div>
        </div>
      </div>
    </div>
    <div class="callmy-chat-input-button">
      <div class="callmy-chat-input-container">
        <input @keyup.enter="sendChat" class="callmy-chat-input" placeholder="내용을 입력해주세요." v-model="state.myChat">
      </div>
      <div @click="sendChat" class="callmy-chat-send-button"><div class="callmy-chat-send-text">전송</div></div>
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
      profileImageURL: computed(() => store.state.root.profileImageURL)
    })

    const sendChat = () => {
      if (state.myChat) {
        const message = {
          userName: store.state.root.username,
          content: state.myChat,
          roomId: props.roomId,
          profileImageURL: state.profileImageURL,
          roomInfoChange: false,
          gameStart: false,
        }
        emit('sendChat', message)
      }

      state.myChat = ''
    }

    onUpdated(() => {
      chatArea.value.scrollTop = chatArea.value.scrollHeight;
    })

    return { state, sendChat, chatArea }
  }
}
</script>


