<template>
  <div class="mypage-container">
    {{ state.selectedMenu }}
    <Sidebar @openSelectedMenu="setSelectedMenu"/>
    <MyPageSection :seletedMenu="seletedMenu"/>
  </div>
</template>

<style>
  @import url('./mypage.css');
</style>
<script>
import Sidebar from './sidebar/sidebar.vue'
import MyPageSection from './section/my-page-section.vue'
import { reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'Mypage',
  components: {
    Sidebar,
    MyPageSection,
  },
  setup (props, { emit }) {
    const store = useStore()
    const router = useRouter()
    const state = reactive({
      seletedMenu: { menu : '' }

    })

    const setSelectedMenu = (menu) => {
      state.seletedMenu = { menu }

    }

    return { state, setSelectedMenu }
  }
}
</script>
