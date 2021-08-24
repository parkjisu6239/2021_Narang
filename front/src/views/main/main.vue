<template>
  <div class="main-wrapper">
    <Navbar/>
    <Loading v-if="this.loading"/>
    <router-view></router-view>
  </div>
</template>
<style>
  @import "https://unpkg.com/element-plus/theme-chalk/index.css";
  @import './main.css';
  @import '../../common/css/common.css';
  @import '../../common/css/element-plus.css';
  @import './components/sound.css';
</style>
<script>
import Navbar from './components/navbar.vue'
import Loading from './components/loading.vue'

export default {
  name: 'Main',

  components: {
    Navbar,
    Loading,
  },

  data () {
    return {
      loading: false,
    }
  },

  methods: {
    onLoading () {
      this.loading = true
      setTimeout(() => {
      this.loading = false
      }, 1000);
    },
  },

  watch: {
    '$route': 'onLoading'
  }

}

const forms = document.querySelectorAll('form')
forms.forEach(form => {
  form.addEventListener('submit', function(event) {
    event.preventDefault()
  })
})

</script>
