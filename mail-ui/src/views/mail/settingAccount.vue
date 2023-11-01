<template>
  <div>
    <jvs-table
        ref="crud"
        class="pager_curd"
        :data="tableData"
        :option="tableOption"
        @refresh-change='getList'
        @on-load='getList'
        style="margin-top: 30px;">
      <template slot="menuLeft" slot-scope="scope">
        <div class="settingAccountAddBtn cursor" @click="handleAdd">添加其他账户</div>
      </template>
      <template slot="useWay" slot-scope="scope">
        <el-switch v-model="scope.row.useWay"
                   @change="handleChange($event, scope.row)"
                   :inactive-value="0"
                   :active-value="1">
        </el-switch>
      </template>
      <template slot="isRemind" slot-scope="scope">
        <el-switch
          v-model="scope.row.isRemind"
          @change="handleremindStatus(scope.row)"
          active-color="#5DADF7"
          inactive-color="#ccc"
          :active-value="true"
          :inactive-value="false">
          <!-- :active-text="scope.row.isRemind?'关闭消息提醒':'开启消息提醒'" -->
          <!-- <span :style="{'color':scope.row.isRemind?'#5DADF7':'#ccc','margin':'5px 0px 0px 10px'}">{{scope.row.isRemind?'关闭消息提醒':'开启消息提醒'}}</span> -->
        </el-switch>
      </template>
      <!--操作 -->
      <template slot="menu" slot-scope="scope">
        <span class="cursor" @click="handleDel(scope.row)" style="margin-right: 15px;color: #3471FF;font-size: 12px;">删除</span>
        <span class="cursor" @click="handleEdit(scope.row)" style="color: #3471FF;font-size: 12px;">修改配置</span>
        <!-- <span class="cursor" @click="handleSignature(scope.row)" style="color: #3471FF;font-size: 12px;">个性签名</span> -->
        <!-- <div>
        <el-switch
          v-model="scope.row.isRemind"
          @change="handleremindStatus(scope.row)"
          active-color="#5DADF7"
          inactive-color="#ccc"
          :active-value="true"
          :inactive-value="false"
          active-text="消息提醒"> -->
          <!-- :active-text="scope.row.isRemind?'关闭消息提醒':'开启消息提醒'" -->
          <!-- <span :style="{'color':scope.row.isRemind?'#5DADF7':'#ccc','margin':'5px 0px 0px 10px'}">{{scope.row.isRemind?'关闭消息提醒':'开启消息提醒'}}</span> -->
        <!-- </el-switch>
        </div> -->
      </template>
    </jvs-table>
    <!-- 选择类型  -->
    <el-dialog append-to-body
               width="440px"
               :show-close="false"
               :visible.sync='selectTypeDialog'>
      <div class="mail-create-container flex-center">
        <h3 class="mail-header-text">添加账户</h3>
        <div class="createForm">
          <div class="mail-item flex-center cursor"
               @click="handleSelectItem(item)"
               v-for="(item, index) in mailList"
               :key="'mailList' + index">
            <div class="mailList-item-row">
              <img :src="item.img" :style="`width: ${item.w};height: ${item.h};`" alt="">
              <span class="mail-item-text" v-if="item.text">{{ item.text }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
    <!--  配置  -->
    <el-dialog append-to-body
               width="630px"
               :visible.sync='mailConfigDialog'
               :before-close="handleMailConfigDialogCancel"
               :show-close="false"
               :close-on-click-modal="true">
      <div slot="title" class="dialog-header-row" style="position: relative">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">账户设置</span>
<!--        <span class="dialog-header-right-text cursor" @click="handleGoToHelp">配置引导说明</span>-->
      </div>
      <mailLoginForm :actionMode="actionMode"
                     :initData="rowData"
                     showUseWay
                     :initMailAccountType="mailAccountType"
                     @success="successCb"
                     v-if="mailConfigDialog" />
    </el-dialog>
    <el-dialog
      title="设置签名"
      :before-close="handleClose"
      :visible.sync='showInfo'
      v-if="showInfo">
      <!-- 个性签名组件 -->
      <signature></signature>
    </el-dialog>
  </div>
</template>

<script>
import { dicConfig, settingAccountOption } from "@/const/const"
import { getMailConfigList, delConfigById, updateConfigState ,businessRemind } from "@/api/mail"
import mailLoginForm from "@/views/mail/mailLoginForm"
import EventBus from '@/eventBus'
import { openLink } from "@/util/util"
import signature from "@/views/mail/signature"
export default {
  name: "settingAccount",
  components: {
    mailLoginForm,
    signature
  },
  data() {
    return {
      tableData: [],
      tableOption: settingAccountOption,
      tableLoading: true,
      dialog: false,
      rowData: {}, // 行数据
      knowTaskId: '', // 处理id
      mailConfigDialog: false,
      selectTypeDialog: false,
      actionMode: 'add',  // [add, edit]
      mailAccountType: 'other',
      mailList: [
        // {img: require('@/assets/img/mail/jck.png'),  w: '26px', h: '30px', type: 'jck', text: '进出口邮箱'},
        {img: require('@/assets/img/mail/tx.png'), w: '29px', h: '20px', type: 'tengXunQiYe', text: '腾讯企业邮'},
        {img: require('@/assets/img/mail/qq.png'),  w: '29px', h: '29px', type: 'tengXunQQ', text: 'QQ邮箱'},
        {img: require('@/assets/img/mail/163.png'),  w: '29px', h: '15px', type: 'wangYi', text: '163邮箱'},
        {img: require('@/assets/img/mail/126.png'),  w: '29px', h: '16px', type: '126', text: '126邮箱'},
        // {img: require('@/assets/img/mail/gmail.png'),  w: '29px', h: '22px', type: 'gmail', text: 'Gmail'},
        // {img: require('@/assets/img/mail/outlook.png'),  w: '29px', h: '25px', type: 'outlook', text: 'Outlook'},
        // {img: require('@/assets/img/mail/exchange.png'),  w: '29px', h: '27px', type: 'exchange', text: 'Exchange'},
        {img: require('@/assets/img/mail/mail.png'),  w: '29px', h: '26px', type: 'other', text: '其他邮箱'},
      ],
      showInfo:false,
      remindStatus:false
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    //个性签名弹框关闭
    handleClose(){
      this.showInfo = false
    },
    // 用户列表
    // TODO:
    getList() {
      this.tableLoading = true
      // const res = {
      //   data: {"code":200,"msg":"成功","data":[{"id":71,"sysUserId":419,"mailType":"wangYi","account":"Beats0","sendHost":"smtp.163.com","acceptHost":"imap.163.com","sendPort":465,"acceptPort":993,"acceptSsl":1,"sendSsl":1,"username":"jyl2047155291@163.com","password":"xxx","sendProtocol":"smtp","acceptProtocol":"imap","defaultEncodingS":"UTF-8","useWay":1,"createTime":"2021-04-03T22:21:00","updateTime":"2021-04-03T22:21:00","delFlag":0,"tenantId":39},{"id":110,"sysUserId":419,"mailType":"tengXunQQ","account":"","sendHost":"smtp.qq.com","acceptHost":"imap.qq.com","sendPort":465,"acceptPort":993,"acceptSsl":1,"sendSsl":1,"username":"1357097581@qq.com","password":"xxppeerzfmrebagg","sendProtocol":"smtp","acceptProtocol":"imap","defaultEncodingS":"UTF-8","useWay":1,"createTime":"2021-06-09T09:26:32","updateTime":"2021-06-09T09:26:32","delFlag":0,"tenantId":39}],"timestamp":"2021-06-09 14:28:06 870"}
      // }
      getMailConfigList().then(res => {
        if (res.data.code === 0) {
          this.tableData = res.data.data
          // console.log(this.tableData)
        }
      }).finally(() => {
        this.tableLoading = false
      })
    },
    successCb() {
      this.mailConfigDialog = false
      this.getList()
      EventBus.$emit('handleCheckIsFirstLogin')
    },
    // 选中item
    handleSelectItem(item) {
      this.mailAccountType = item.type
      this.actionMode = 'add'
      this.rowData = {}
      this.selectTypeDialog = false
      this.mailConfigDialog = true
    },
    // 帮助
    handleGoToHelp() {
      // const url = process.env.NODE_ENV === 'development'?location.protocol+'//'+location.host+':8081':''
      // console.log(location,"99999999999")
      openLink(location.origin+'/html/mailhelp.html')
    },
    handleMailConfigDialogCancel() {
      this.mailConfigDialog = false
    },
    // 开启、关闭
    handleChange(status, row) {
      const query = {
        id: row.id,
        useWay: row.useWay
      }
      updateConfigState(query).then(res => {
        if (res.data.code === 0) {
          this.getList()
        }
      })
    },
    // 删除
    handleDel({id}) {
      this.$msgbox({
        title: '提示',
        message: `
          <div class="msgBoxInner">
            <div class="innerHeader">
              <i class="innerIcon el-icon-info warning-icon"></i>
              <span class="innerTitle">确定将邮箱账号删除吗？</span>
            </div>
          </div>
          `,
        dangerouslyUseHTMLString: true,
        customClass: 'customMsgBox',
        showCancelButton: true,
        confirmButtonText: '确定',
        confirmButtonClass: 'confirmBtn confirmButton',
        cancelButtonText: '取消',
        cancelButtonClass: 'confirmBtn cancelButton',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '执行中...'
            delConfigById(id).then(res => {
              if (res.data.code === 0) {
                this.$message.success('删除成功')
                EventBus.$emit('handleCheckIsFirstLogin')
                this.getList()
              }
            }).finally(() => {
              done()
              instance.confirmButtonLoading = false
            })
          } else {
            done()
          }
        }
      }).then(action => {
      }).catch(e => {})
    },
    // 编辑
    handleEdit(row) {
      this.actionMode = 'edit'
      this.rowData = row
      this.mailConfigDialog = true
    },
    // 新增
    handleAdd() {
      this.mailAccountType = 'tengXunQiYe'
      // this.selectTypeDialog = true
      this.actionMode = 'add'
      this.rowData = {}
      // this.selectTypeDialog = false
      this.mailConfigDialog = true
    },
    //个性签名弹框打开
    handleSignature(){
      this.showInfo = true
    },
    //开启关闭消息提醒
    handleremindStatus(row){
      console.log(row)
      businessRemind(row.id,row.isRemind).then(res => {
        if (res.data.code === 0) {
          this.getList()
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
