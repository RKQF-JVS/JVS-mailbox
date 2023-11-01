<template>
  <div>
    <el-dialog
      :title="bulletin.title + '公告'"
      :visible.sync="dialogVisible"
      width="800px"
      :before-close="handleClose">
      <div style="max-height: 800px;overflow-y: auto">
        <section v-html="bulletin.content"></section>
      </div>
    </el-dialog>
    <div class="dialog-box" v-if="imgVisible" @click="imgVisible = false">
      <img :src="bulletin.content" alt=""/>
    </div>
  </div>
</template>

<script>
import {getBulletinList} from '@/api/login'
export default {
  data() {
    return {
      dialogVisible:false,
      bulletin:{},
      imgVisible:false
    };
  },
  created() {

  },
  mounted() {

  },
  methods: {
    getBulletinList() {
      getBulletinList().then(res => {
        this.bulletin = res.data.data || {}
        console.log(this.bulletin, res.data.data)
        if (this.bulletin.content && this.bulletin.content.length > 0) {
          this.bulletin.content = this.bulletin.content.replace('<img ', `<img style="max-width: 710px;"`)
          if (this.bulletin.contentType === 'TEXT') {
            this.dialogVisible = true
          } else {
            this.imgVisible = true
          }
        }
      })
    },
    handleClose(){
      this.dialogVisible = false
    }
  }
};
</script>

<style scoped lang="scss">
.dialog-box/deep/{
  position: absolute;
  top: 0;
  left: 0;
  height: 100vh;
  width: 100vw;
  z-index: 9999;
  text-align: center;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 6px;
  img{
    max-width: 800px;
    min-width: 400px;
    margin-top: 10vh;
    z-index: 10000;
  }
}
</style>
