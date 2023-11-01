<template>
  <div id="app">
    <div class="basic-cont-box">
      <router-view />
    </div>
  </div>
</template>

<script>
  import {getDomain} from '@/api/login'
  import store from '@/store'
  import {sendRouter, getToken} from '@/api/index'
  // constants
  import '../public/jvs-ui-public/fonts/font.scss';
  import * as globalTypes from '@/store/types/global'
  import { getStore } from "@/util/store";
  export default {
    name: 'app',
    data() {
      return {
        myInfo:{},
      }
    },
    watch: {},
    created() {
      this.myInfo = getStore({ name: "userInfo" }) || {}
      let routerList = JSON.parse(sessionStorage.getItem('routerList'))
      // sendRouter(routerList) // 先暂时屏蔽发送本地路由
      this.getDomainHandle()
    },
    methods: {
      // 获取域名对应设置信息
      async getDomainHandle () {
        await getDomain().then(res => {
          if(res.data && res.data.code == 0) {
            if(res.data.data){
              this.$store.commit("SET_TENANTINFO", res.data.data)
            } else {
            }
          } else {
          }
        }).catch(err => {
        })
      }
    },
    computed: {}
  }
</script>
<style lang="scss">
  #app {
    width: 100%;
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
    font-family: MiSans-Demibold;
    scrollbar-width: none; /* firefox */
    -ms-overflow-style: none; /* IE 10+ */
  }
  #app::-webkit-scrollbar {
    display: none; /* Chrome Safari */
  }
  ul, li {
    list-style: none;
  }
  .basic-cont-box{
    //padding: 8px 10px;
    position: relative;
    height: 100%;
    box-sizing: border-box;
  }
  .select-dialog .el-dialog__body{
    padding: 0px !important;
  }
  .select-dialog .el-dialog__header{
    padding: 20px 20px 0px !important;
  }
</style>
<style lang="scss" scoped>
  .theme-text{
    position: fixed;
    right: 0;
    top: 50%;
    z-index: 9999999999999;
  }
  .theme-box {
    width: 100%;
    height: 100%;
  }
</style>
