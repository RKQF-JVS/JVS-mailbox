<template>
  <div class="send-curd-header-outer">
    <div class="send-curd-header">
      <div class="send-btn send-btn2 send-action-tag send-action-tag-active flex-center" @click="handleGoBack" v-show="config.back">
        <i class="el-icon-d-arrow-left back-action-icon"></i> 返回 </div>
      <el-checkbox v-model="isCheckedAll" @change="selectAll($event)" style="margin-right: 30px;" v-show="config.qx">全选</el-checkbox>
      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleDelMail" v-show="config.sc"> 删除 </div>
      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleShowDeleteReDialog" v-show="config.cdsc"> 彻底删除 </div>
      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleTrustMail" v-show="config.xr"> 信任 </div>
      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleZF" v-show="config.zf"> 转发 </div>
      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleHF" v-show="config.hf"> 回复 </div>
      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleHFAll" v-show="config.qbhf"> 回复全部 </div>
      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleReEdit" v-show="config.zcbj"> 再次编辑 </div>
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" v-show="config.ch" @click="handleBackDialog('open')"> 撤回 </div>-->
      <div class="send-btn send-btn3 send-action-tag flex-center" v-show="config.bjw">
        <DropDownSelect :prefix="'标记为'" :option="bj_dropDownOptionList" @selectChange="bj_dropDownSelectChange" />
      </div>
      <div class="send-btn send-btn3 send-action-tag flex-center" v-show="config.ydd">
        <DropDownSelect :prefix="'移动到'" :option="ydd_dropDownOption" @selectChange="ydd_dropDownSelectChange" @click.native="getFolderList" />
      </div>
    </div>
    <div class="send-curd-header" v-show="config.flag">
      <div class="header-flag cursor" @click="handleFilterStress">
        <div :class="stress === 1 ? 'header-flag-icon-active' : 'header-flag-icon'">
          <svg-icon :icon-class="stress === 1 ? 'flag_fill' : 'flag'"></svg-icon>
        </div>
        <span>邮件</span>
      </div>
    </div>
    <!--撤回dialog  -->
    <el-dialog append-to-body
               width="40%"
               :visible.sync='backDialog'
               :close-on-click-modal="false">
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">撤回确认</span>
      </div>
      <div class="flex-column-center">
        <img src="@/assets/img/mail/drafts.png" class="drafts-img" alt="">
        <span class="drafts-info" :style="`color: ${canChehui ? '#222222' : '#FF770F'}`">{{ canChehui ? '请您确认邮箱是否撤回？' : '此邮件不支持撤回，请返回！' }}</span>
      </div>
      <div class="flex-center" style="margin-top: 70px;" v-if="canChehui">
        <div @click="handleBackSubmit" class="mail-btn flex-center mail-btn-primary" style="margin-right: 30px;">确定撤回</div>
        <div @click="backDialog = false" class="mail-btn flex-center mail-btn-disable">取消</div>
      </div>
      <div class="flex-center" style="margin-top: 70px;" v-else>
        <div @click="backDialog = false" class="mail-btn flex-center mail-btn-primary" style="margin-right: 30px;">关闭</div>
      </div>
    </el-dialog>
    <!--彻底删除dialog  -->
    <el-dialog append-to-body
               width="40%"
               :visible.sync='deleteReDialog'
               :close-on-click-modal="false">
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">彻底删除确认</span>
      </div>
      <div class="flex-column-center">
        <img src="@/assets/img/mail/mail_delete.png" class="drafts-img" alt="">
        <span class="drafts-info" style="color: #222222">彻底删除后，无法恢复，确认要删除邮件？</span>
      </div>
      <div class="flex-center" style="margin-top: 70px;">
        <div @click="handleDelMailRe" class="mail-btn flex-center mail-btn-primary" style="margin-right: 30px;">确定删除</div>
        <div @click="deleteReDialog = false" class="mail-btn flex-center mail-btn-disable">取消</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { bj_dropDownOption, ydd_dropDownOption } from "@/const/const"
import DropDownSelect from "@/components/DropDown/DropDownSelect"
import { delMail, postTagMail, moveMail, getQueryGroup, trustMaill, deleteMail } from "@/api/mail"

export default {
  name: "MailTopAction",
  props: {
    // 入口
    entry: {
      type: String,
      default: ''
    },
    // 全选控制
    propIsCheckedAll: {
      type: Boolean,
      required: true
    },
    // flag 标记
    stress: {
      type: Number
    },
    // 配置头部 action
    config: {
      type: Object,
      required: true
    },
    // id 列表
    mailIds: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  components: {
    DropDownSelect,
  },
  data() {
    return {
      isCheckedAll: false,
      ydd_dropDownOption: [],
      dropDownActive: '',
      // config: {
      //   back: true,
      //   qx: true,
      //   fh: true,
      //   sc: true,
      //   cdsc: true,
      //   xr: true,
      //   qbhf: true,
      //   ch: true,
      //   zf: true,
      //   qbhf: false,
      //   zcbj: false,
      //   bjw: true,
      //   ydd: true,
      //   flag: true,
      // },
      backDialog: false,
      canChehui: false,
      // 彻底删除 dialog
      deleteReDialog: false
    }
  },
  mounted() {
    this.isCheckedAll = this.propIsCheckedAll
    this.getFolderList()
  },
  computed: {
    // 标记为 下拉框
    bj_dropDownOptionList() {
      // 草稿箱、已发送
      if(this.entry === 'inbox') {
        return bj_dropDownOption
      }
      return [
        { label: '红旗邮件', value: 2 },
        { label: '取消红旗', value: 3 }
      ]
    }
  },
  methods: {
    // 返回
    handleGoBack() {
      this.$emit('handleGoBack')
    },
    // 全选
    selectAll(isAll) {
      this.$emit('selectAll', isAll)
    },
    // 是否有勾选的
    isSelected() {
      const isChecked = this.mailIds.length > 0
      if (!isChecked) {
        this.$message.info('请先勾选')
      }
      return isChecked
    },
    // 获取文件夹列表
    getFolderList() {
      const dirArr = []
      getQueryGroup().then(res => {
        if (res.data.code === 0) {
          res.data.data.forEach(item => {
            dirArr.push({
              label: item.groupName,
              value: item.id
            })
          })
          const options = this.yddDrownOptionFiler()
          let dropDownOption = [...dirArr, ...options]
          // 没有文件夹，去掉分割线
          if(dirArr.length === 0) {
            dropDownOption.forEach(item => {
              item.divided = false
            })
          }
          // 合并
          this.ydd_dropDownOption = dropDownOption
        }
      })
    },
    // 移动到 过滤
    yddDrownOptionFiler() {
      const entry = this.entry
      let option = []
      console.log('entry', entry)
      switch (entry) {
          // { label: '收件箱', value: 1, divided: true },
          // { label: '草稿箱', value: 2 },
          // { label: '已发送', value: 3 },
          // { label: '已删除', value: 4 },
          // { label: '垃圾邮件', value: 5 }
        //  收件箱
        case 'inbox':
          option = [
            { label: '已发送', value: 3, divided: true },
            { label: '已删除', value: 4 },
            { label: '垃圾邮件', value: 5 }
          ]
          break
        //草稿箱
        case 'drafts':
          option = [
            { label: '收件箱', value: 1, divided: true },
            { label: '已发送', value: 3 },
            { label: '已删除', value: 4 },
            { label: '垃圾邮件', value: 5 }
          ]
          break
        // 已发送
        case 'sentMail':
          option = [
            { label: '收件箱', value: 1, divided: true },
            { label: '已删除', value: 4 },
            { label: '垃圾邮件', value: 5 }
          ]
          break
        // 已删除
        case 'deletedMail':
          option = [
            { label: '收件箱', value: 1, divided: true },
            { label: '已发送', value: 3 },
            { label: '垃圾邮件', value: 5 }
          ]
          break
        // 垃圾箱
        case 'spamMail':
          option = [
            { label: '收件箱', value: 1, divided: true },
            { label: '已发送', value: 3 },
            { label: '已删除', value: 4 },
          ]
          break
        // 文件夹
        case 'directory':
          option = [
            { label: '收件箱', value: 1, divided: true },
            { label: '已发送', value: 3 },
            { label: '已删除', value: 4 },
            { label: '垃圾邮件', value: 5 }
          ]
          break
        default:
          option = [...ydd_dropDownOption]
      }
      return option
    },
    handleFilterStress() {
      this.$emit('changeStress')
    },
    // 标记
    bj_dropDownSelectChange(item) {
      const isChecked = this.isSelected()
      if (!isChecked) return

      // this.dropDownActive = item.value
      const data = {
        mailIds: this.mailIds.join(','),
        tag: item.value
      }
      postTagMail(data).then(res => {
        if (res.data.code === 0) {
          this.$message.success('操作成功')
          this.$emit('refresh')
        }
      })
    },
    // 删除 String 1是删除
    handleDelMail() {
      const isChecked = this.isSelected()
      if (!isChecked) return

      const data = {
        mailIds: this.mailIds.join(','),
        delFlag: '1'
      }
      delMail(data).then(res => {
        if (res.data.code === 0) {
          this.$message.success('删除成功')
          this.handleGoBack()
          this.refresh()
        }
      })
    },
    handleShowDeleteReDialog() {
      const isChecked = this.isSelected()
      if (!isChecked) return

      this.deleteReDialog = true
    },
    // 信任邮件
    handleTrustMail() {
      const isChecked = this.isSelected()
      if (!isChecked) return

      const id = this.mailIds[0]
      trustMaill(id).then(res => {
        if (res.data.code === 0) {
          this.$message.success('操作成功')
          this.handleGoBack()
          this.refresh()
        }
      })
    },
    // 彻底删除  2是彻底删除
    handleDelMailRe() {
      const isChecked = this.isSelected()
      if (!isChecked) return

      const data = {
        mailIds: this.mailIds.join(',')
      }
      deleteMail(data).then(res => {
        if (res.data.code === 0) {
          this.$message.success('删除成功')
          this.deleteReDialog = false
          this.handleGoBack()
          this.refresh()
        }
      })
    },
    // 转发
    handleZF() {
      const isChecked = this.isSelected()
      if (!isChecked) return

      if (this.mailIds.length > 1) {
        this.$message.info('只能转发单个邮件')
        return
      }
      this.$emit('actionCallBack', { routerName: 'writeMail', id: this.mailIds[0], action: 'zhuanFa' })
    },
    // 回复
    handleHF() {
      const isChecked = this.isSelected()
      if (!isChecked) return

      if (this.mailIds.length > 1) {
        this.$message.info('只能回复单个邮件')
        return
      }
      this.$emit('actionCallBack', { routerName: 'writeMail', id: this.mailIds[0], action: 'huiFu' })
    },
    // 回复全部
    handleHFAll() {
      const isChecked = this.isSelected()
      if (!isChecked) return

      if (this.mailIds.length > 1) {
        this.$message.info('只能回复单个邮件')
        return
      }
      this.$emit('actionCallBack', { routerName: 'writeMail', id: this.mailIds[0], action: 'huiFuQb' })
    },
    // 再次编辑
    handleReEdit() {
      this.$emit('actionCallBack', { routerName: 'writeMail', id: this.mailIds[0], action: 'reEdit' })
    },
    // 移动到
    ydd_dropDownSelectChange(item) {
      const isChecked = this.isSelected()
      if (!isChecked) return

      const data = {
        groupId: item.value,
        mailIds: this.mailIds.join(',')
      }
      moveMail(data).then(res => {
        if (res.data.code === 0) {
          this.$message.success('操作成功')
          this.refresh()
        }
      })
    },
    refresh() {
      this.$emit('refresh')
    },
    // 撤回 dialog 隐现
    handleBackDialog(type) {
      const isChecked = this.isSelected()
      if (!isChecked) return

      console.log('type', type)
      if (type === 'open') {
        this.backDialog = true
      } else {
        this.backDialog = false
      }
    },
    // 撤回 提交
    handleBackSubmit() {
      this.backDialog = false
    }
  },
  watch: {
    'propIsCheckedAll': {
      handler(value) {
        this.isCheckedAll = value
      }
    }
  }
}
</script>

<style scoped>

</style>
