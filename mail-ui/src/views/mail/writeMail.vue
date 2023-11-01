<!--写邮件-->
<template>
  <MainBasicContainer :useBorder="false"
                      class="send-view-container"
                      v-loading="loading">
   <!-- <span v-if="showDev">{{ JSON.stringify(params) }}</span> -->
    <div class="send-header" style="position: relative;">
      <!-- <div class="writeMailHeaderText">写邮件</div> -->
      <div class="writeMailHeaderRight">
        <div class="writeMailHeaderItem flex-center cursor" style="margin-right: 5px;background-color: #3471FF;" @click="handleSendMail('send')">
          <!-- <img class="writeMailHeaderActionIcon" src="@/assets/img/mail/write-send.png" alt=""/> -->
          <svg t="1670492004246" class="icon writeMailHeaderActionIcon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1624" width="200" height="200"><path d="M915.82 142.44L97.84 458.18c-46.13 17.76-44.89 43.66 2.47 57.72l197.59 58.71 80.42 239.03c10.61 31.33 37.99 37.99 61.42 14.55l103.6-102.62 204.74 149.98c26.64 19.49 54.02 9.13 61.18-23.19L958.5 179.43c7.15-32.31-11.85-48.83-42.68-36.99zM791.49 294.63l-348.8 310.81c-6.17 5.67-11.84 16.78-12.83 24.91l-15.29 144.8c-1.73 16.28-7.4 17.02-12.58 1.23L334.4 572.89c-2.71-7.89 0.99-17.76 8.14-21.95l442.54-265.42c27.87-16.78 30.83-12.84 6.41 9.11z" p-id="1625" fill="#ffffff"></path></svg>
          <div class="writeMailHeaderActionText" style="color: #fff;">发送</div>
        </div>
        <div class="writeMailHeaderItem flex-center cursor" key="" @click="handleSendMail('save')">
          <!-- <img class="writeMailHeaderActionIcon" src="@/assets/img/mail/write-save.png" alt=""> -->
          <div class="writeMailHeaderActionText">存草稿</div>
        </div>
      </div>
      <div class="send-tip" style="display:flex;">
        <span class="cursor" @click="addRow('cs', showCs)" v-show="!showFbfs">{{ showCs ? '删除' : '添加' }}抄送</span>
        <span class="cursor" @click="addRow('ms', showMs)" v-show="!showFbfs">{{ showMs ? '删除' : '添加' }}密送</span>
        <span class="cursor" @click="addRow('fbfs', true)" v-show="!showFbfs" title="什么是分送：会对多个人一对一发送。每个人将收到单独发给他/她的邮件。">分别发送</span>
        <span v-show="showFbfs" style="color: #999;">
          以上收件人将单独收到邮件
          <span @click="addRow('fbfs', false)"
                class="send-tip cursor"
                style="margin-left: 10px;" title="什么是分送：会对多个人一对一发送。每个人将收到单独发给他/她的邮件。">取消分别发送</span>
        </span>
        <div style="display:flex;">
        <UploadFile uploadText='添加附件'
          showMaxSizeErrorText
          :initFileList="form.multipartFiles"
          :maxSize="1024*1024*20"
          @changeUpload='changeUpload'
          lineMode
          @onRemove="onRemove"
          errorText='请上传正确的附件' style="padding:0px 10px;" class="file"/>
        <UploadFilePiecess uploadText='添加超大附件'
          :initFileList="form.multipartFiles"
          @uploadSuccess="uploadFilePiecessSuccess"
          @progress="onProgress"
          @success="onSuccess"
          @error="onError"
          lineMode
          errorText='请上传正确的附件' style="padding-left:10px;" class="file"/></div>
      </div>
    </div>
    <div class="send-main-container">
      <!--收件人 -->
      <div class="send-row" v-show="!showFbfs">
        <div class="send-label" style="color:#3471FF;">收件人：</div>
        <i @click="handleShowAddDialog('to')" class="el-input__icon el-icon-circle-plus-outline"></i>
        <div class="send-row-item-container">
<!--          <Mailselect-->
<!--            v-model="form.toArr"-->
<!--            style="width: 100%;"-->
<!--            multiple-->
<!--            filterable-->
<!--            allow-create-->
<!--            default-first-option-->
<!--            placeholder="请输入收件人邮箱"-->
<!--            @iconClick="handleShowAddDialog('to')"-->
<!--          >-->
<!--          </Mailselect>-->
<!--          <MailselectList v-model="value"-->
<!--                          multiple-->
<!--                          remote-->
<!--                          style="width: 100%;"-->
<!--                          :remote-method="data => handleSearchUserMail(data)"-->
<!--                          filterable-->
<!--                          allow-create-->
<!--                          default-first-option-->
<!--                          @iconClick="handleShowAddDialog('to')"-->
<!--                          placeholder="请输入收件人邮箱">-->
<!--            <el-option v-for="item in searchOptions"-->
<!--                       :key="item.value"-->
<!--                       :label="item.label"-->
<!--                       :value="item.value">-->
<!--            </el-option>-->
<!--          </MailselectList>-->
          <el-select v-model="form.toArr"
                     class="mailselectEl"
                     multiple
                     remote
                     :remote-method="data => handleSearchUserMail(data)"
                     @change="handleChange($event, 'to')"
                     filterable
                     allow-create
                     default-first-option
                     placeholder="请输入收件人邮箱">
            <el-option v-for="item in searchOptions"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
        </div>
      </div>
      <!--抄送 -->
      <div class="send-row" v-show="showCs && !showFbfs">
        <div class="send-label" style="color:#3471FF;">抄送：</div>
        <i @click="handleShowAddDialog('cc')" class="el-input__icon el-icon-circle-plus-outline"></i>
        <div class="send-row-item-container">
<!--          <Mailselect-->
<!--              v-model="form.ccListArr"-->
<!--              style="width: 100%;"-->
<!--              multiple-->
<!--              filterable-->
<!--              allow-create-->
<!--              default-first-option-->
<!--              placeholder="请输入收件人邮箱"-->
<!--              @iconClick="handleShowAddDialog('cc')"-->
<!--          >-->
<!--          </Mailselect>-->
          <el-select v-model="form.ccListArr"
                     class="mailselectEl"
                     multiple
                     remote
                     :remote-method="data => handleSearchUserMail(data)"
                     @change="handleChange($event, 'cc')"
                     filterable
                     allow-create
                     default-first-option
                     placeholder="请输入收件人邮箱">
            <el-option v-for="item in searchOptions"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
        </div>
      </div>
      <!--密送 -->
      <div class="send-row" v-show="showMs && !showFbfs">
        <div class="send-label" style="color:#3471FF;">密送：</div>
        <i @click="handleShowAddDialog('bcc')" class="el-input__icon el-icon-circle-plus-outline"></i>
        <div class="send-row-item-container">
<!--          <Mailselect-->
<!--              v-model="form.bccListArr"-->
<!--              style="width: 100%;"-->
<!--              multiple-->
<!--              filterable-->
<!--              allow-create-->
<!--              default-first-option-->
<!--              placeholder="请输入收件人邮箱"-->
<!--              @iconClick="handleShowAddDialog('bcc')">-->
<!--          </Mailselect>-->
          <el-select v-model="form.bccListArr"
                     class="mailselectEl"
                     multiple
                     remote
                     :remote-method="data => handleSearchUserMail(data)"
                     @change="handleChange($event, 'bcc')"
                     filterable
                     allow-create
                     default-first-option
                     placeholder="请输入收件人邮箱">
            <el-option v-for="item in searchOptions"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
        </div>
      </div>
      <!--分别发送 -->
      <div class="send-row" v-show="showFbfs">
        <div class="send-label">分别发送：</div>
        <div class="send-row-item-container">
          <Mailselect
              v-model="form.toFbfsArr"
              style="width: 100%;"
              :showIcon="false"
              multiple
              filterable
              placeholder="分别发送"
              allow-create
              default-first-option
          >
          </Mailselect>
        </div>
      </div>
      <!-- <div class="send-tip">
        <span class="cursor" @click="addRow('cs', showCs)" v-show="!showFbfs">{{ showCs ? '删除' : '添加' }}抄送 ｜</span>
        <span class="cursor" @click="addRow('ms', showMs)" v-show="!showFbfs">{{ showMs ? '删除' : '添加' }}密送 ｜</span>
        <span class="cursor" @click="addRow('fbfs', true)" v-show="!showFbfs" title="什么是分送：会对多个人一对一发送。每个人将收到单独发给他/她的邮件。">分别发送</span>
        <span v-show="showFbfs" style="color: #999;">
          以上收件人将单独收到邮件
          <span @click="addRow('fbfs', false)"
                class="send-tip cursor"
                style="margin-left: 10px;" title="什么是分送：会对多个人一对一发送。每个人将收到单独发给他/她的邮件。">取消分别发送</span>
        </span>
      </div> -->
      <!--主题 -->
      <div class="send-row">
        <div class="send-label">主题：</div>
        <div class="send-row-item-container">
          <el-input
            placeholder="请输入邮件主题"
            maxlength="50"
            show-word-limit
            style="width: 100%;"
            v-model="form.subject">
          </el-input>
        </div>
      </div>
      <!-- <div class="send-tip" style="position: relative;">
        <UploadFile uploadText='添加附件'
                    showMaxSizeErrorText
                    :initFileList="form.multipartFiles"
                    :maxSize="1024*1024*20"
                    @changeUpload='changeUpload'
                    lineMode
                    @onRemove="onRemove"
                    errorText='请上传正确的附件'/>
        <UploadFilePiecess uploadText=' ｜添加超大附件'
                           :initFileList="form.multipartFiles"
                           @uploadSuccess="uploadFilePiecessSuccess"
                           @progress="onProgress"
                           @success="onSuccess"
                           @error="onError"
                           lineMode
                           errorText='请上传正确的附件'
                           class="uploadFilePiecessItem" />
      </div> -->
      <div class="send-tip">
        <div class="bigFileListItem" v-for="(item, index) in bigFileList" :key="'bigFileList' + index">
          {{ item.name }}
          <el-progress :stroke-width="2" :percentage="item.uploadProgressNum"></el-progress>
        </div>
      </div>
      <!--正文 -->
      <div class="send-rows">
        <div class="send-label">正文：</div>
        <div class="send-row-item-container">
          <div id="editor"/>
          <div class="send-bottom">
            <div class="sender-info ellipsis">
              发件人：<span class="sender-name">{{ senderInfo.account }}</span> &lt;{{ senderInfo.username }}&gt;｜
              签名：
              <div class="send-dropdown sign-text-row ellipsis">
                <DropDown :initActiveName="signId" :option="signList" prefix="" @selectChange="handleSignChange" />
              </div>
              <div class="send-bottom-box">
                <el-checkbox v-model="form.urgent" :true-label="1" :false-label="0">紧急</el-checkbox>
                <el-checkbox v-model="form.receipt" :true-label="1" :false-label="0">已读回执</el-checkbox>
                <el-checkbox v-model="form.isTiming" :true-label="1" :false-label="0" @change="isTimeChange">定时发送</el-checkbox>
                <span class="cursor" @click="handleChangeTime">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{{ form.timing }}</span>
<!--                <el-date-picker-->
<!--                  v-model="form.timing"-->
<!--                  type="datetime"-->
<!--                  size="mini"-->
<!--                  style="width: 100%;"-->
<!--                  format="yyyy-MM-dd HH:mm"-->
<!--                  value-format="yyyy-MM-dd HH:mm"-->
<!--                  placeholder="请选择定时发送时间">-->
<!--                </el-date-picker>-->
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="send-footer">
      <div class="send-btn send-footer-btn flex-center" @click="handleSendMail('send')" style="background-color: #3471FF;">
        <!-- <img class="writeMailHeaderActionIcon" src="@/assets/img/mail/write-send.png" alt=""/> -->
        <svg t="1670492004246" class="icon writeMailHeaderActionIcon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1624" width="200" height="200"><path d="M915.82 142.44L97.84 458.18c-46.13 17.76-44.89 43.66 2.47 57.72l197.59 58.71 80.42 239.03c10.61 31.33 37.99 37.99 61.42 14.55l103.6-102.62 204.74 149.98c26.64 19.49 54.02 9.13 61.18-23.19L958.5 179.43c7.15-32.31-11.85-48.83-42.68-36.99zM791.49 294.63l-348.8 310.81c-6.17 5.67-11.84 16.78-12.83 24.91l-15.29 144.8c-1.73 16.28-7.4 17.02-12.58 1.23L334.4 572.89c-2.71-7.89 0.99-17.76 8.14-21.95l442.54-265.42c27.87-16.78 30.83-12.84 6.41 9.11z" p-id="1625" fill="#ffffff"></path></svg>
        <span style="color:#fff;">发送</span>
      </div>
      <div class="send-btn send-footer-btn flex-center" @click="handleSendMail('save')">存草稿</div>
    </div>
    <!--  快速添加收件人 dialog    -->
    <el-dialog  append-to-body
                width="80%"
                :visible.sync='addDialog'
                class="select-dialog"
                :close-on-click-modal="false">
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">快速添加收件人</span>
      </div>
      <selectSender v-if="addDialog"
                    :addType="addDialogType"
                    :selectedList="selectedList"
                    @selectUserSuccess="selectUserSuccess"
                    @selectCancel="selectCancel"></selectSender>
    </el-dialog>
    <!-- 定时发送   -->
    <el-dialog append-to-body
               width="30%"
               :before-close="handleClearRangeTime"
               class="dialog-message-box"
               :visible.sync='showTimeRangeDialog'
               :close-on-click-modal="false">
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">定时发送时间</span>
      </div>
      <div style="display: flex;flex-direction: column">
        <el-date-picker
          v-model="form.timing"
          type="datetime"
          style="width: 100%;"
          format="yyyy-MM-dd HH:mm"
          value-format="yyyy-MM-dd HH:mm"
          placeholder="请选择定时发送时间">
        </el-date-picker>
        <span class="warning-sub2">请选择定时发送时间</span>
        <div class="warning-footer">
          <el-button @click="handleClearRangeTime">取消</el-button>
          <el-button type="primary" @click="handleRangeTimeSubmit">确定</el-button>
        </div>
      </div>
    </el-dialog>
  </MainBasicContainer>
</template>

<script>
import Editor from 'wangeditor'
import selectSender from "@/views/mail/components/selectSender"
import MainBasicContainer from "@/components/BasicContainer/MainBasicContainer"
import DropDown from "@/components/DropDown/DropDown"
import UploadFile from "@/components/UploadFile/UploadFile"
import UploadFilePiecess from "@/components/UploadFile/UploadFilePiecess"
import { getMailConfigList, getMailDetails, getSenderMail, getMailAccessory, getSignature, saveDraft, sendMail } from "@/api/mail"
import {
  encryption,
  getFileSize,
  getFileSuffix,
  getMailPrefix,
  getMailStr,
  handleRedirect,
  replaceCIDImg,
  replaceImgCID,
  setIframeInnerHtml,
  transEnterTextBr,
  xssFilter,
  formatToCNDateDay2,
  openLink
} from "@/util/util"
import dayjs from "dayjs"
import Mailselect from "@/components/Mailselect/Mailselect"
import MailselectList from "@/components/Mailselect/MailselectList";
import { queryEmail, uploadFile } from "@/api/system"
import { hasBlank, isLikeEmail } from "@/util/validate"
import DropDownSelect from "@/components/DropDown/DropDownSelect"
import { mapState } from "vuex"
import EventBus from "@/eventBus"
import { decodeRes } from "@/views/mail/t";

export default {
  name: "WriteMail",
  props: {
    params: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    MainBasicContainer,
    DropDown,
    selectSender,
    UploadFile,
    UploadFilePiecess,
    Mailselect,
    DropDownSelect,
    MailselectList
  },
  data() {
    return {
      signId: -1,
      signList: [
        { value: -1, label: '不使用签名' }
      ],
      // 插入图片
      cidImgs: [],
      value: [],
      form: {
        // 密送列表
        bcc: [],
        bccListArr: [],
        // 抄送列表
        cc: [],
        ccListArr: [],
        // 0 正常，1 分别发送
        isRespectively: 0,
        // 0 未定时, 1 定时发送
        isTiming: 0,
        // 附件
        multipartFiles: [
          // {
          //   name: '',
          //   url: ''
          // }
        ],
        // 0 不回执, 1 回执
        receipt: 0,
        // 回复地址
        replyTo: '',
        // 发送时间
        sentDate: '',
        // 邮件主题
        subject: '',
        // 内容
        text: '',
        // 邮件类型
        textType: 'TEXT/HTML; charset=utf-8',
        // 定时发送时间
        timing: '',
        // 邮件接收人
        to: [
          // {
          //   name: '',
          //   url: ''
          // }
        ],
        toArr: [],
        toFbfsArr: [],
        // 0 不紧急，1紧急
        urgent: 0,
        userId: 0
      },
      senderInfo: {
        account: '',
        username: ''
      },
      signStr: '',
      currentMailId: '',
      userMail_dropDownOptionList: [],
      editor: null,
      checkList: [],
      addDialog: false,
      showCs: false,
      searchOptions: [],
      showMs: false,
      showFbfs: false,
      showTimeRangeDialog: false,
      isChangeTime: false,
      // 是否已经保存
      isSaved: true,
      addDialogType: '', // 选择人类型 ['to': 收件人, 'cc': 抄送, 'bcc': 密送]
      bigFileList: [],
      uploadProgressNum: 0,
      loading: false
    }
  },
  mounted() {
    this.initEditor()
    this.handleGetUserMail(this.mailConfigId)
    this.handleGetSignature()
    this.initUserMailList()
    if (this.params.hasOwnProperty('id')) {
      this.handleGetMailDetails()
    }
    if (this.params.action === 'initWriteMail') {
      this.initWriteCCList()
    }
  },
  computed: {
    ...mapState({
      mailConfigId: state => state.socket.mailConfigId
    }),
    // 当前选中的
    selectedList() {
      let list = []
      switch (this.addDialogType) {
        case 'to':
          list = this.form.toArr
          break
        case 'cc':
          list = this.form.ccListArr
          break
        case 'bcc':
          list = this.form.bccListArr
          break
      }
      return list
    },
  },
  methods: {
    // 获取用户邮箱列表
    initUserMailList() {
      this.currentMailId = this.mailConfigId
      getMailConfigList().then(res => {
        if (res.data.code === 0) {
          // const configId = res.data.data[0].id
          const arr = []
          res.data.data.forEach(item => {
            arr.push({label: item.username, value: item.id})
          })
          this.userMail_dropDownOptionList = arr
        }
      })
    },
    // 拉取附件
    /**
     * @return {PromiseRejectionEvent<Boolean> | PromiseFulfilledResult<Array>}
     * */
    handleGetMailFile(query) {
      // TODO:
      return new Promise((resolve, reject) => {
        resolve([])
        getMailAccessory(query).then(res => {
          if (res.data.code === 0) {
            resolve(res.data.data)
          }
        }).catch(e => {
          reject(false)
        })
      })
    },
    // TODO: async 初始化邮件信息
    async handleGetMailDetails() {
      // 操作方式 String ['reEdit': 再次编辑, 'zhuanFa'：转发, 'huiFu': 回复, 'huiFuQb': 回复全部 ]
      const action = this.params.action
      this.loading = true
      const query = {
        id: this.params.id
      }
      TODO:
      // const res = {
      //   data: decodeRes()
      // }
      getMailDetails(query).then(async res => {
        if (res.data.code === 0) {
          const resData = res.data.data

          const MailExtendListRes = await this.handleGetMailFile(query)
          if(MailExtendListRes === false) {
            this.$message.error('附件加载失败')
            resData.MailExtendList = []
          } else {
            resData.MailExtendList = MailExtendListRes
          }
          this.loading = false
          const { mailBody, MailExtendList, MailRecipientList, MailContent } = resData

          const formated = this.formatRecipientList(action, resData)
          const resource = this.formatResource(action, resData)
          this.form = {
            ...this.form,
            // toStr: mailBody.from,
            ...formated,
            subject: this.formatReplySubject(action, resData),
            ...resource,
          }
          // TODO: 是否显示抄送
          this.showCs = formated.cc.length > 0
          this.showMs = formated.bcc.length > 0
          // 内容
          let text = MailContent.text
          // 如果为文本类型，转换
          if(MailContent.textType.toLowerCase().indexOf('text/plain') !== -1) {
            text = transEnterTextBr(MailContent.text)
            // 添加图片
            MailExtendList.forEach(item => {
              if(item.cid) {
                text += `<img src="${item.imgUrl}" /><br />`
              }
            })
          }
          // const signStr = `<br><br><br><div class="sign-item-container">签名</div><br><br>`
          // 原始邮件头部信息
          const csArrStr = this.formatReplyRecipient(action, resData, 'cs')
          const originalHeaderStr = xssFilter(
            `
             <br>
             <br>
            <div>
              <div style="text-align: center;color: #222222;padding: 5px;">------------------ 原始邮件信息 ------------------</div>
              <div style="background: #f4f4f4;padding: 15px 5px;margin-bottom: 15px;font-size: 19px;">
                <div style="font-weight: 500; color: #222222; line-height: 28px;margin: 0;">${mailBody.subject}</div>
                <div>
                  <div style="font-size: 14px; font-weight: 400; color: #222222; line-height: 22px;">发件人：<span style="color: #BFBFBF;font-size: 14px;">${mailBody.from};</span></div>
                </div>
                <div>
                  <div style="font-size: 14px; font-weight: 400; color: #222222; line-height: 22px;">收件人：<span style="color: #BFBFBF;font-size: 14px;">${this.formatReplyRecipient(action, resData, 'sjr')}</span></div>
                </div>
                ${csArrStr ? `<div><div style="font-size: 14px; font-weight: 400; color: #222222; line-height: 22px;">抄&nbsp;&nbsp;&nbsp;&nbsp;送：<span style="color: #BFBFBF;font-size: 14px;">${csArrStr}</span></div></div>` : ''}
                ${mailBody.sentDate ? `<div> <div style="font-size: 14px; font-weight: 400; color: #222222; line-height: 22px;">时&nbsp;&nbsp;&nbsp;&nbsp;间：<span style="color: #BFBFBF;font-size: 14px;">${this.timerFormat(mailBody.sentDate)}</span></div></div>` : ``}
              </div>
            </div>
          `
          )

          // 插入 iframe
          const iframeEl = `<iframe id="mailContent" scrolling="no" height="0" frameborder="0"></iframe>`
          // 内容
          let content = ``
          switch (action) {
            case 'huiFu':
            case 'huiFuQb':
              content = originalHeaderStr + text
              break
            case 'zhuanFa':
              // 带上文件
              this.initExtendList(MailExtendList)
              // content = signStr + originalHeaderStr + text
              content = originalHeaderStr + text
              break
            case 'reEdit':
              // 带上文件
              this.initExtendList(MailExtendList)
              content = text
              break
          }
          // 签名
          content = this.signStr + content

          const replacedData = replaceCIDImg(MailExtendList, content, 'text/html', false, false, action)
          content = replacedData.content
          this.editor.txt.html(content)
          this.form.text = content
          // 替换过滤后的附件
          this.cidImgs = replacedData.multipartFiles
          this.form.multipartFiles = replacedData.multipartFiles
        }
      }).finally(() => {
        this.loading = false
      })
    },
    // 初始化写邮件人信息
    initWriteCCList() {
      const initMail = this.params.initMail
      this.form.toArr.push(initMail)
    },
    // 搜索人
    handleSearchUserMail(search) {
      const query = {
        search
      }
      queryEmail(query).then(res => {
        if (res.data.code === 0) {
          let searchOptions = []
          res.data.data.forEach(item => {
            const str = `${ item.name || '' }<${ item.email }>`
            searchOptions.push({
              label: str,
              value: item.email,
            })
          })
          this.searchOptions = searchOptions
        }
      })
    },
    // 下拉选中
    handleChange(arr, type) {
      this.$nextTick(() => {
        // 过滤, 防止输入非邮箱类型的
        arr = arr.filter(str => isLikeEmail(str))
        // console.log('arr',type)
        switch (type) {
          case 'to':
            this.form.toArr = arr
            break;
          case 'cc':
            this.form.ccListArr = arr
            break;
          case 'bcc':
            this.form.bccListArr = arr
            break;
        }
      })
    },
    // 格式化 接收人，发送人，抄送
    formatRecipientList(action, resData) {
      const { mailBody, MailExtendList, MailRecipientList, MailContent } = resData

      let to = []
      let cc = []
      let bcc = []
      // 回复 拿 from的
      if (action === 'huiFu') {
        const mailFormat = getMailStr(mailBody.from)
        if (mailFormat) {
          to = [{ mailName: mailBody.from, mail: mailFormat, name: mailBody.from }]
        }
      } else if (action === 'huiFuQb') {
        // 包括发送人
        to = MailRecipientList.filter(item => item.mailType.value === 'to')
        // 不要密送
        // bcc = MailRecipientList.filter(item => item.mailType.value === 'bcc')

        // 包括发送人
        const mailFormat = getMailStr(mailBody.from)
        cc = MailRecipientList.filter(item => item.mailType.value === 'cc' && item.mail !== mailFormat)

        let isExistFlag = false
        // 检验是否已经存在自己，防止重复
        to.forEach(item => {
          if(item.mail === mailFormat) {
            isExistFlag = true
          }
        })
        if(!isExistFlag) {
          to.unshift({ mailName: mailBody.from, mail: mailFormat, name: mailBody.from })
        }

      } else if (action === 'reEdit' || action === 'original') {
        to = MailRecipientList.filter(item => item.mailType.value === 'to')
        cc = MailRecipientList.filter(item => item.mailType.value === 'cc')
        bcc = MailRecipientList.filter(item => item.mailType.value === 'bcc')
      } else if (action === 'zhuanFa') {
        to = []
        cc = []
        bcc = []
      }

      const toArr = to.map(item => item.mail) || []
      const ccListArr = cc.map(item => item.mail) || []
      const bccListArr = bcc.map(item => item.mail) || []

      const formated = {
        to,
        toArr,
        cc,
        ccListArr,
        bcc,
        bccListArr
      }
      return formated
    },
    // 再次编辑格式化 回显
    formatResource(action, resData) {
      if (action !== 'reEdit') return {}
      const { mailBody, MailExtendList, MailRecipientList, MailContent } = resData
      const {id, isTiming, timing, stress, receipt, replyTo, subject, urgent} = mailBody
      return {
        id,
        urgent,
        isTiming,
        timing,
        stress,
        receipt,
        replyTo
      }
    },
    // 回复 主题 格式化
    formatReplySubject(action, resData) {
      const { mailBody, MailExtendList, MailRecipientList, MailContent } = resData

      let str = ``
      if (action === 'huiFu' || action === 'huiFuQb') {
        str = `回复：${mailBody.subject}`
      } else if (action === 'zhuanFa') {
        str = `转发：${mailBody.subject}`
      } else if (action === 'reEdit') {
        str = `${mailBody.subject}`
      }else {
        str = `${mailBody.from}`
      }
      return str
    },
    // 原始邮件信息 格式化 收件人、抄送
    formatReplyRecipient(action, resData, type) {
      const { mailBody, MailExtendList, MailRecipientList, MailContent } = resData
      let str = ``
      if (action === 'huiFu') {
        str = `${mailBody.from}`
      } else if (action === 'huiFuQb') {
        let listStr = ''
        MailRecipientList.forEach(item => {
          listStr += `${item.mailName};`
        })
        str = listStr
      } else {
        str = `${mailBody.from}`
      }
      const formated = this.formatRecipientList('original', resData)
      let listStr = ''

      // 收件人

      if (type === 'sjr') {
        formated.to.forEach(item => {
          const mailAddr = item.mailName ? item.mailName : item.mail
          listStr += `${mailAddr};`
        })
      } else if (type === 'cs') {
        // 抄送 的 接收人 和 抄送人
        const lists = [...formated.cc]
        lists.forEach(item => {
          // 去重添加
          const mailAddr = item.mailName ? item.mailName : item.mail
          if (listStr.indexOf(mailAddr) === -1) {
            listStr += `${mailAddr};`
          }
        })
      }
      return listStr
    },
    // 初始化文件 列表
    initExtendList(MailExtendList) {
      const fileList = [...MailExtendList]
      fileList.forEach(item => {
        item.fileName = item.name
      })
      this.form.multipartFiles = fileList
    },
    // 抄送，密送
    addRow(type, action) {
      switch (type) {
        // 抄送
        case 'cs':
          this.showCs = !action
          break
        // 密送
        case 'ms':
          this.showMs = !action
          break
        // 分别发送
        case 'fbfs':
          this.showFbfs = action
          // 分别发送
          if (action === true) {
            // 先 添加到分别发送
            const toFbfsArr = []
            toFbfsArr.push(...this.form.toArr)
            if (this.showCs) {
              toFbfsArr.push(...this.form.ccListArr)
            }
            if (this.showMs) {
              toFbfsArr.push(...this.form.bccListArr)
            }
            // 去重显示
            this.form.toFbfsArr = Array.from(new Set(toFbfsArr))
            // 隐藏
            this.showCs = false
            this.showMs = false
          } else {
            // 取消分别发送
            if (this.form.ccListArr.length) {
              this.showCs = true
            }
            if (this.form.bccListArr.length) {
              this.showMs = true
            }
          }
          break
      }
    },
    // 显示选择人dialog
    handleShowAddDialog(type) {
      this.addDialog = true
      this.addDialogType = type
      // switch (type) {
      //   // 收件人
      //   case 'to':
      //     break
      //   // 抄送
      //   case 'cc':
      //     break
      //   // 密送
      //   case 'bcc':
      //     break
      // }
    },
    // 初始化富文本
    initEditor() {
      const _this = this
      const editor = new Editor(`#editor`)
      // 配置菜单栏，删减菜单，调整顺序
      editor.config.menus = [
        'head',
        'bold',
        'fontSize',
        'fontName',
        'italic',
        'underline',
        'strikeThrough',
        'indent',
        'lineHeight',
        'foreColor',
        'backColor',
        'link',
        'list',
        'todo',
        'justify',
        'quote',
        'emoticon',
        'image',
        'code',
        'splitLine',
        'undo',
        'redo',
      ]
      // 配置 onchange 回调函数，将数据同步到 vue 中
      editor.config.onchange = (newHtml) => {
        this.isSaved = false
        this.form.text = newHtml
      }
      editor.config.pasteFilterStyle = false
      editor.config.focus = false
      editor.config.pasteIgnoreImg = false
      editor.config.customUploadImg = function (resultFiles, insertImgFn) {
        // resultFiles 是 input 中选中的文件列表
        // insertImgFn 是获取图片 url 后，插入到编辑器的方法
        console.log('resultFiles', resultFiles)
        _this.handleUploadImgFile(resultFiles).then(res => {
          // const imgUrl = 'https://pic1.zhimg.com/50/v2-31d8dab795c556dd52eddee160bf8760_hd.jpg'
          // 上传图片，返回结果，将图片插入到编辑器中
          _this.cidImgs.push(res)
          insertImgFn(res.imgUrl)
        })
      }
      // Base64 图片上传
      editor.config.uploadImgShowBase64 = false
      editor.config.uploadImgMaxSize = 2 * 1024 * 1024 // 2M
      // 创建编辑器
      editor.create()
      this.editor = editor
    },
    // wangeditor config 网络图片上传方式
    handleUploadImgFile(files) {
      return new Promise((resolve, reject) => {
        const formData = new FormData()
        const file = files[0]
        formData.append('file', file)

        uploadFile(formData).then(res => {
          if (res.data.code === 0) {
            const { fileName, url } = res.data.data
            const fileSuffix = getFileSuffix(fileName)
            const cidName = fileName.replace(fileSuffix, '')
            const saveData = {
              fileName,
              name: cidName,
              url,
              imgUrl: url,
              cidImg: `cid:${fileName}`,
              fileType: 1
            }
            resolve(saveData)
          }
        })
      })
    },
    // 获取正文
    getEditContent() {
      if (!this.editor.txt.text()) {
        this.$message.info('请输入正文')
        return
      }
      console.log(this.editor.txt.html())
    },
    // 更换签名
    handleSignChange(item) {
      this.signId = item.value
      // 删除签名
      if (item.value === -1) {
        const signElArr = document.querySelectorAll('.sign-item-container')
        signElArr.forEach(el => {
          el.remove()
        })
      } else {
        // 添加签名
        // 内容
        const text = this.form.text
        // 签名
        const signStr = `<br><br><div class="sign-item-container">------------------<br />${item.content}</div><br><br>`
        this.signStr = signStr
        let content = ''
        const action = this.params.action || ''
        switch (action) {
          case 'huiFu':
          case 'huiFuQb':
          case 'zhuanFa':
            content = signStr + text
            break
          case 'reEdit':
            content = text
            break
          // 写邮件
          case '':
            // // 先清空全部签名，再填充
            // document.querySelectorAll('.sign-item-container').forEach(el => {
            //   el.remove()
            // })
            content = text + signStr
            break
        }
        // 添加位置判断
        this.editor.txt.html(content)
        this.form.text = content
      }
    },
    // 获取个性签名
    handleGetSignature() {
      getSignature().then(res => {
        if (res.data.code === 0) {
          const list = res.data.data

          const currentItem = list.filter(item => item.isDefault === 1)
          // 未设置签名的
          if(!currentItem || currentItem.length === 0) {
            this.signId = -1
          } else {
            this.signId = currentItem[0].id
            const itemData = {
              ...currentItem[0],
              value: currentItem[0].id
            }
            this.handleSignChange(itemData)
          }

          list.forEach(item => {
            item.label = item.name
            item.value = item.id
          })
          this.signList = [{ value: -1, label: '不使用签名' }, ...list]
        }
      })
    },
    // 选择人回调
    selectUserSuccess(list, addType) {
      // 选择人类型 ['to': 收件人, 'cc': 抄送, 'bcc': 密送]
      switch (addType) {
        case 'to':
          this.form.toArr = list
          break
        case 'cc':
          this.form.ccListArr = list
          break
        case 'bcc':
          this.form.bccListArr = list
          break
      }
      this.addDialog = false
    },
    selectCancel() {
      this.addDialog = false
    },
    // 获取用户邮箱
    handleGetUserMail(mailConfigId = '') {
      let query = {}
      if(mailConfigId) query.configId = mailConfigId
      getSenderMail(query).then(res => {
        if (res.data.code === 0) {
          const { username, account } = res.data.data
          this.senderInfo = {
            account,
            username
          }
        }
      })
    },
    /** 转换
     * @param type  {String}  ['send': 发送, 'save': 保存]
     * @return  Boolean
     */
    translateMailList(type) {
      // 收件人
      const toArr = this.form.toArr
      // 检验收件人
      if (type === 'send' && toArr.length === 0) {
        this.$message.error('请填写收件人')
        return false
      }
      const to = []
        console.log("报错？？？？？？？？？？？？")
      toArr.forEach(item => {
        to.push({
          name: item,
          mail: item
        })
      })
      this.form.to = to
      // 抄送
      const ccListArr = this.form.ccListArr
      const cc = []
      ccListArr.forEach(item => {
        cc.push({
          name: item,
          mail: item
        })
      })
      this.form.cc = cc
      // 密送
      const bccListArr = this.form.bccListArr
      const bcc = []
      bccListArr.forEach(item => {
        bcc.push({
          name: item,
          mail: item
        })
      })
      this.form.bcc = bcc
      return true
    },
    /**
     * 发送、保存邮件
     * @param type  {String}  ['send': 发送, 'save': 保存]
     * @param cb    {Function || null}  回调函数
     */
    async handleSendMail(type, cb = null) {
      if(type === 'send') {
        const passFlag = this.translateMailList(type)
        if (!passFlag) {
          cb && cb('failed')
          return
        }
        // 未填写主题的 提示, 不是切换保存草稿类型
        if(hasBlank(this.form.subject) && !cb) {
          const result = await this.handleShowSaveMsgBox()
          if(result === 'checkSubject-success') {
            cb && cb('success')
          } else {
            cb && cb('cancel')
            return
          }
        }
      } else if(type === 'save') {
        // 保存检查
        const passFlag = this.translateMailList(type)
      }

      const text = xssFilter(this.editor.txt.html())
      if (text.length >= 10000) {
        this.$message.error('内容不得超过10000字符')
        cb && cb('failed')
        return
      }
      // 邮件内容
      const data = {
        ...this.form,
        from: this.senderInfo.username,
        configId:this.mailConfigId,
        // multipartFiles: [...this.form.multipartFiles, ...this.cidImgs],
        // text: resultData.content
        text
      }
      // 有定时的
      if (data.isTiming === 1 && !data.timing) {
        this.showTimeRangeDialog = true
        cb && cb('failed')
        return
      }
      // 没有定时的 清空 定时
      if (data.isTiming === 0) {
        data.timing = ''
      }
      // 没有抄送的 清空
      if (!this.showCs) {
        data.cc = []
      }
      // 没有密送的 清空
      if (!this.showMs) {
        data.bcc = []
      }
      // 分别发送 转义
      data.isRespectively = this.showFbfs ? 1 : 0
      // 如果是分别发送，只传to
      if (data.isRespectively === 1) {
        const toFbfsArr = []
        data.toFbfsArr.forEach(item => {
          toFbfsArr.push({ name: item, mail: item })
        })
        data.to = toFbfsArr
        data.cc = []
        data.bcc = []
      }

      // return
      const requestApi = type === 'send' ? sendMail : saveDraft
      const infoText = type === 'send' ? '发送成功' : '保存成功'

      // const afterData = encryption({
      //   data: data,
      //   key: "ZnJhbWVmcmFtZQ==",
      //   param: ["text"]
      // });

      if(this.loading) return
      this.loading = true
      requestApi(data).then(res => {
          console.log(res.data,"是否成功？")
        if (res.data.code === 0) {
          this.isSaved = true
          this.$message.success(infoText)
          if (cb) {
            cb('success')
          } else {
            if (type === 'send') {
              // 发送成功 跳转
              this.$emit('changeRoute', {})
            }
          }
          EventBus.$emit('refresh')
        }
      }).catch(e => {
        cb && cb('failed')
      }).finally(() => {
        this.loading = false
      })
    },

    // 主题提醒 cb =>  ['checkSubject-success' , 'checkSubject-failed']
    handleShowSaveMsgBox() {
      return new Promise((resolve, reject) => {
        this.$msgbox({
          title: '提示',
          message: `
          <div class="msgBoxInner">
            <div class="innerHeader">
              <i class="innerIcon el-icon-info info-icon"></i>
              <span class="innerTitle">您的邮件没有填写主题</span>
            </div>
           <div class="innerTip">您确定继续发送？</div>
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
              done()
              resolve('checkSubject-success')
            } else {
              done()
              resolve('checkSubject-failed')
            }
          }
        }).then(action => {
        }).catch(e => {})
      })
    },
    // 定时确定
    handleRangeTimeSubmit() {
      if (!this.form.timing) {
        this.$message.error('请选择正确的时间')
        return
      }
      const currentTimeSS = dayjs().unix()
      const timeStartSS = dayjs(this.form.timing).unix()
      // 小于120秒
      if (timeStartSS < (currentTimeSS - 120)) {
        this.$message.error('不能小于当前时间')
        return
      }
      this.showTimeRangeDialog = false
    },
    // 关闭定时
    handleClearRangeTime() {
      if(!this.isChangeTime) {
        this.form.timing = ''
        this.form.isTiming = 0
      }
      this.showTimeRangeDialog = false
    },
    // 切换是否定时
    isTimeChange(value) {
      this.isChangeTime = false
      // 没有 清空定时
      if (value === 0) {
        this.form.timing = ''
      } else {
        // 有 显示时间dialog
        this.showTimeRangeDialog = true
      }
    },
    // 更改时间
    handleChangeTime() {
      this.isChangeTime = true
      this.showTimeRangeDialog = true
    },
    // 文件
    // 文件上传成功回调
    changeUpload(data) {
      console.log('data.res', data.res)
      this.form.multipartFiles.push(data.res)
    },
    // 文件删除回调
    onRemove(file, fileList) {
      // 有 response 的
      let url = file.url
      if (file.response) {
        url = file.response.data.url
      }
      // 过滤 url
      this.form.multipartFiles = this.form.multipartFiles.filter(item => item.url !== url)
    },
    onProgress(num, file) {
      let bigFileList = this.bigFileList
      let filesArr = bigFileList.filter(item => item.fileName === file.fileName)
      if(filesArr.length) {
        bigFileList.forEach(item => {
          if(item.fileName === file.fileName) {
            item.uploadProgressNum = num
          }
        })
      } else {
        file.uploadProgressNum = num
        bigFileList.push(file)
      }
      this.bigFileList = bigFileList
      // this.uploadProgressNum = num
    },
    onSuccess(fileName) {
      this.bigFileList = this.bigFileList.filter(item => item.fileName !== fileName)
    },
    onError() {
      this.bigFileList = []
    },
    // 大文件上传成功回调
    uploadFilePiecessSuccess(data) {
      this.$message.success('文件上传成功')
     // data =  {
     //    name: fileData.name,
     //    size: fileData.size,
     //    url: res.data.data
     //  }

      data.expTime = dayjs().add(30, 'day').format('YYYY-MM-DD HH:mm')
      data.size = getFileSize(data.size)

      let content = this.editor.txt.html()
      const bigFileBockEl = document.querySelector('#bigFileBockEl')
      const bigFileBockHeadrHtml = `<br><br><br><div id="bigFileBockEl" style="background: #f4f4f4; padding: 15px 5px; width: 100%;">超大附件</div><br>`
      const bigFileBockItemHtml = `
      <div>
        <a href="${data.url}" style="color: #1890ff;">${data.name}</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <span>附件大小: ${data.size}</span>&nbsp;&nbsp;&nbsp;&nbsp;
        <span>到期时间: ${data.expTime}</span>&nbsp;&nbsp;&nbsp;&nbsp;
        <a href="${data.url}" style="color: #1890ff;">下载</a>&nbsp;&nbsp;&nbsp;&nbsp;
      </div>
      <br>
      `

      // 已经存在 超大附件 header 直接添加子附件
      if(bigFileBockEl) {
        content = content + bigFileBockItemHtml
      } else {
        content = content + bigFileBockHeadrHtml + bigFileBockItemHtml
      }
      this.editor.txt.html(content)
      this.form.text = content
    },
    // 设置 iframe 高度
    changeFrameHeight() {
      this.$nextTick(() => {
        const ele = document.querySelector('#mailContent')
        const iframeBody = ele.contentWindow.document.body

        // 监听点击链接 打开 操作
        const links = iframeBody.querySelectorAll('a')
        links.forEach(link => {
          link.addEventListener('click', (e) => {
            e.preventDefault()
            openLink(link.href)
          })
        })
        ele.setAttribute('height', String(iframeBody.scrollHeight + 50))
      })
    },
    // 时间转换
    timerFormat(dateStr) {
      let str = dayjs(dateStr).format('YYYY年MM月DD日 hh:mm')
      str += ` （${formatToCNDateDay2(dayjs(dateStr).day())}）`
      return str
    }
  }
}
</script>

<style scoped lang="scss">
/deep/.el-select .el-input.is-focus .el-input__inner{
  border:0;
}
/deep/.el-input--medium .el-input__inner{
  border: 0;
}
/deep/.el-tag.el-tag--info {
  background-color: #9fbcfd47;
  border-color: #cbddf3db;
  padding: 0px 15px 0px 10px;
}
</style>
