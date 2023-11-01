<template>
  <div class="mail-header">
    <!-- <div style="max-width: calc(100vw - 960px);">
      <div class="header-title ellipsis">{{ headerTitle }}</div>
    </div> -->
    <div class="mailHeaderRowContainer">
      <div class="mailHeaderItem cursor flex-center" @click="handleHF" v-if="config.hf">
        <!-- <div class="cursor action-icon">
          <svg-icon icon-class="mail_hf"></svg-icon>
        </div> -->
        <span class="action-text">回复</span>
      </div>
      <div class="mailHeaderItem cursor flex-center" @click="handleHFAll" v-if="config.qbhf">
        <!-- <div class="cursor action-icon">
          <svg-icon icon-class="mail_hfqb"></svg-icon>
        </div> -->
        <span class="action-text">回复全部</span>
      </div>
      <div class="mailHeaderItem cursor flex-center" @click="handleZF" v-if="config.zf">
        <!-- <div class="cursor action-icon">
          <svg-icon icon-class="mail_zf"></svg-icon>
        </div> -->
        <span class="action-text">转发</span>
      </div>
      <DropDownMenu
        :config="config"
        :tagList="tagList"
        :fileList="fileList"
        :mailConfigId="mailConfigId"
        :mailDetailId="mailDetailId"
        :entry="entry"
        @getList="getList"
        @changeMenu="changeMenu"
        @handleRead="bj_dropDownSelectChange"
        @handleMove="ydd_dropDownSelectChange"
      ></DropDownMenu>
    </div>
    <div style="max-width: calc(100vw - 960px);">
      <div class="header-title ellipsis">{{ headerTitle }}</div>
    </div>
<!--    <div>-->
<!--      <div class="send-btn send-btn2 send-action-tag send-action-tag-active flex-center" @click="handleGoBack" v-show="config.back">-->
<!--        <i class="el-icon-d-arrow-left back-action-icon"></i> 返回 </div>-->
<!--      <el-checkbox v-model="isCheckedAll" @change="selectAll($event)" style="margin-right: 30px;" v-show="config.qx">全选</el-checkbox>-->
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleDelMail" v-show="config.sc"> 删除 </div>-->
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleShowDeleteReDialog" v-show="config.cdsc"> 彻底删除 </div>-->
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleTrustMail" v-show="config.xr"> 信任 </div>-->
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleZF" v-show="config.zf"> 转发 </div>-->
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleHF" v-show="config.hf"> 回复 </div>-->
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleHFAll" v-show="config.qbhf"> 回复全部 </div>-->
<!--      <div class="send-btn send-btn2 send-action-tag flex-center" @click="handleReEdit" v-show="config.zcbj"> 再次编辑 </div>-->
<!--&lt;!&ndash;      <MailTopHeaderAction></MailTopHeaderAction>&ndash;&gt;-->
<!--    </div>-->
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
import MailTopHeaderAction from "@/views/mail/components/MailTopHeaderAction"
import { deleteMail, delMail, getQueryGroup, moveMail, postTagMail, trustMaill } from "@/api/mail"
import { ydd_dropDownOption } from "@/const/const"
import DropDownMenu from "@/components/DropDownMenu/DropDownMenu"
import EventBus from "@/eventBus";
import {getTagmailAll} from "@/api/tag";

export default {
  name: "MailTopHeader",
  props: {
    headerTitle: {
      type: String,
      required: true
    },
    // 邮箱ID
    mailConfigId: {
      type: String,
      default: ''
    },
    // 邮件ID
    mailDetailId: {
      type: String,
      default: ''
    },
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
    MailTopHeaderAction,
    DropDownMenu
  },
  data() {
    return {
      isCheckedAll: false,
      ydd_dropDownOption: [],
      dropDownActive: '',
      backDialog: false,
      canChehui: false,
      // 彻底删除 dialog
      deleteReDialog: false,
      tagList: [],  //标签列表
      fileList: [], // 文件夹列表
    }
  },
  created() {
    this.getTagList()
    this.getFileList()
  },
  methods: {
    getList() {
      // this.$emit('getList', '1', this.mailConfigId)
      this.$emit('refresh')
    },
    getTagList() {
      getTagmailAll(this.mailConfigId).then(res => {
        console.log(res.data.data)
        if (res.data && res.data.code == 0) {
          this.tagList = res.data.data || []
        }
      })
    },
    getFileList() {
      getQueryGroup({configId:this.mailConfigId}).then(res => {
        if (res.data && res.data.code == 0) {
          this.fileList = res.data.data || []
        }
      })
    },
    // 返回
    handleGoBack() {
      this.$emit('handleGoBack')
    },
    // 全选
    selectAll(isAll) {
      this.$emit('selectAll', isAll)
    },
    // 更爱菜单回调
    changeMenu(menu) {
      console.log(menu)
      const { action, value = '' } = menu
      switch (action) {
        case 'sc':
          this.handleDelMail()
          break;
        case 'cdsc':
          this.handleShowDeleteReDialog()
          break;
        case 'xr':
          this.handleTrustMail()
          break;
        case 'zf':
          this.handleZF()
          break;
        case 'hf':
          this.handleHF()
          break;
        case 'qbhf':
          this.handleHFAll()
          break;
        case 'zcbj':
          this.handleReEdit()
          break;
        case 'bjw':
          this.bj_dropDownSelectChange(value)
          break;
        case 'ydd':
          this.ydd_dropDownSelectChange(value)
          break;
      }
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
      // TODO:
      // getQueryGroup().then(res => {
        const res = {data: {"code":0,"msg":"成功","data":[{"id":67,"groupName":"文件夹111","userId":419,"configId":71,"type":{"value":"yourself"},"createTime":"2021-04-04T10:49:41","updateTime":"2021-04-04T10:49:41","delFlag":0,"tenantId":39}],"timestamp":"2021-06-09 15:07:36 779"}}
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
      // })
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
            { label: '垃圾邮件', value: 5 },
            // { label: '文件夹', value: 'id' }
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
    bj_dropDownSelectChange(tag) {
      const isChecked = this.isSelected()
      if (!isChecked) return

      // this.dropDownActive = item.value
      const data = {
        mailIds: this.mailIds.join(','),
        tag
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
    ydd_dropDownSelectChange(groupId) {
      const isChecked = this.isSelected()
      if (!isChecked) return

      const data = {
        groupId,
        mailIds: this.mailIds.join(',')
      }
      moveMail(data).then(res => {
        if (res.data.code === 0) {
          this.$message.success('操作成功')
          this.refresh()
          EventBus.$emit('moveMailRefresh')
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
}
</script>

<style lang="scss">
.mail-header {
  // display: flex;
  // align-items: center;
  // justify-content: space-between;
  width: 100%;
  padding: 0px 10px;
  // background: #EFF2F7;
  // height: 40px;
  // line-height: 40px;
  height: 65px;
  line-height: 60px;
  .header-title {
    font-size: 18px;
    font-weight: 600;
    color: #222222;
    line-height: 20px;
  }
  .el-input-group__prepend {
    background: #768099;
    color: #fff;
    padding: 0 15px;
  }
  .el-input__inner {
    height: 30px;
    line-height: 30px;
    font-size: 12px;
  }
  .el-input__suffix {
    .el-input__icon.el-input__clear {
      line-height: 30px;
    }
  }
}
</style>
