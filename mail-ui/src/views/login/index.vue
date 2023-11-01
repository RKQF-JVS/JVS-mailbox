<template>
  <div class="login-container"
    :style="background"> 
  </div>
</template>

<script>
import "@/styles/login.scss";
import {getDomain} from '@/api/login'
export default {
  name: "index",
  data () {
    return {
      background: "background",
      title: "无忧 - 邮件",
    }
  },
  created () {
    // 获取域名相关设置
    this.getDomainHandle()
  },
  mounted () { },
  methods: {
    // 获取域名对应设置信息
    async getDomainHandle () {
      this.background = "background:none;"
      this.logoV2Pic = ""
      document.title = ""
      await getDomain().then(res => {
        if(res.data && res.data.code == 0) {
          if(res.data.data){
            if(res.data.data.bgImg) {
              this.background = `background-image:url("${res.data.data.bgImg}");`
            }
            if(res.data.data.logo) {
              this.logoV2Pic = res.data.data.logo
            }
            if(res.data.data.name) {
              document.title = res.data.data.name
            }
            if(res.data.data.icon) {
              var link = document.createElement('link')
              link.type = 'image/x-icon'
              link.rel = 'shortcut icon'
              link.href = res.data.data.icon
              document.getElementsByTagName('head')[0].appendChild(link);
            }
            this.openLogin()
          } else {
            // this.openLogin()
            this.$router.push({ path: '/404' });
          }
        } else {
          this.$router.push({ path: '/404' });
        }
      }).catch(err => {
        this.$router.push({ path: '/404' });
      })
    },
    /**
     * 打开登录
     */
    openLogin() {
      if(this.$route.path != '/mail') {
        this.$openLogin({
          right: '150px',
          successClose: false,
          afterLogin: (dialog, res) => {
            console.log('登录提交。。。。。')
            dialog.handleClose()
            this.loginToPath()
            // // 登录IM
            // try {
            //   this.connect(res.code, dialog)
            // } catch (error) {
            //   dialog.handleClose()
            //   this.loginToPath()
            // }
          },
          afterRegister: () => {
            console.log('注册提交。。。。。')
          }
        })
      }
    },
    // 登陆后跳转
    loginToPath () {
      let path = sessionStorage.getItem('lastUrl')
      if(path) {
        this.$openUrl(path, '_self')
      }else{
        this.$router.push({
          path: (this.tagWel && this.tagWel.value) ? this.tagWel.value : '/mail',
        });
      }
    },
  }
}
</script>

<style lang="scss" scoped>
</style>
