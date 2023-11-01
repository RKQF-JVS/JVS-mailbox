<!--邮件详情-->
<template>
  <MainBasicContainer :useBorder="false"
                      v-loading="loading">
    <MailTopHeader :headerTitle="form.mailBody.subject"
                   :propIsCheckedAll="false"
                   :config="headerConfig"
                   :mailConfigId="mailConfigId"
                   :mailDetailId="mailDetailId"
                   :mailIds="[params.id]"
                   :entry="detailEntry"
                   @handleGoBack="handleGoBack"
                   @actionCallBack="actionCallBack"
                   @refresh="handleGetListCb"
                   @getList="getList"
                   @selectAll="() => {}"
                   v-if="!loading"></MailTopHeader>
<!--    <MailTopAction :propIsCheckedAll="false"-->
<!--                   :config="headerConfig"-->
<!--                   :mailIds="[params.id]"-->
<!--                   :entry="detailEntry"-->
<!--                   @handleGoBack="handleGoBack"-->
<!--                   @actionCallBack="actionCallBack"-->
<!--                   @refresh="handleGetListCb"-->
<!--                   @selectAll="() => {}"></MailTopAction>-->
    <div class="mail-header-container">
      <div class="mail-info">
        <div class="mail-info-row">
          <div class="mail-info-label">发件人：</div>
          <!--          <div class="mail-info-detail">{{ form.mailBody.from | getMailPrefix }}<{{ form.mailBody.from }}>;</div>-->
          <div class="mail-info-detail">
            <span class="mail-info-text">{{ form.mailBody.from }}</span>
          </div>
        </div>
        <div class="mail-info-row" v-show="formatedRecipientList.to.length > 0">
          <div class="mail-info-label">收件人：</div>
          <div class="mail-info-detail" v-show="formatedRecipientList.to.length > 0">
            <span v-for="(item, index) in formatedRecipientList.to"
              :key="'MailRecipientList' + index"
              v-show="item.mailType.value === 'to'"
              style="display: inline-block;" class="mail-info-text" v-if="index < 3">
              {{ item.name }}&lt;{{ item.mail }}&gt;{{ formatedRecipientList.to.length === 1 || index === formatedRecipientList.to.length - 1? '' : ';' }}
            </span>
            <span v-if="formatedRecipientList.to.length>3" class="mail-info-textcolor">
                还有{{formatedRecipientList.to.length-3}}个联系人
            </span>
          </div>
        </div>
        <div class="mail-info-row" v-show="formatedRecipientList.cc.length > 0">
          <div class="mail-info-label">抄送：</div>
          <div class="mail-info-detail">
            <span v-for="(item, index) in formatedRecipientList.cc"
                  :key="'MailRecipientList' + index"
                  v-show="item.mailType.value === 'cc'"
                  style="display: inline-block;">{{ item.name }}&lt;{{ item.mail }}&gt;{{ formatedRecipientList.cc.length === 1 || index === formatedRecipientList.cc.length - 1 ? '' : ';' }}</span>
          </div>
        </div>
<!--        <div class="mail-info-row" v-show="formatedRecipientList.bcc.length > 0">-->
<!--          <div class="mail-info-label">密送：</div>-->
<!--          <div class="mail-info-detail">-->
<!--            <span v-for="(item, index) in formatedRecipientList.bcc"-->
<!--                  :key="'MailRecipientList' + index"-->
<!--                  v-show="item.mailType.value === 'bcc'"-->
<!--                  style="display: inline-block;">{{ item.name }}<{{ item.mail }}>;</span>-->
<!--          </div>-->
<!--        </div>-->
        <div class="mail-info-rows" v-if="form.mailBody.sentDate">
          <div class="mail-info-label">时间：</div>
          <div class="mail-info-detail">{{ timerFormat(form.mailBody.sentDate) }}</div>
        </div>
        <div class="mail-info-rows" v-if="form.MailExtendList.length">
          <div class="mail-info-label">附件：</div>
<!--          <div class="mail-info-detail">{{ form.MailExtendList.length }}个（<span class="fj-tag"-->
<!--                                                                                @click="handleDownload(item)"-->
<!--                                                                                v-for="(item, index) in form.MailExtendList"-->
<!--                                                                                :key="'MailExtendList' + index">{{ item.name }}{{ index >= 0 && index !== form.MailExtendList.length -1 ? '、' : '' }}</span>）</div>-->
          <div class="mail-info-detail">
            <div class="fj-tag"
                 @click="handleDownload(item)"
                 v-for="(item, index) in form.MailExtendList"
                 :key="'MailExtendList' + index">{{ item.name }}</div></div>
        </div>
      </div>
    </div>
    <div class="mail-content-container">
      <!--      <p class="mail-title">Hi，郑志勇，</p>-->
      <!--      <p class="mail-content" v-html="xssFilter(form.MailContent.text)">-->
      <!-- 邮箱内容 -->
      <iframe id="mailContent" scrolling="no" :height="iframeHeight" frameborder="0"></iframe>
      <div>
      </div>
    </div>
    <div class="send-footer" style="justify-content: flex-end;">
      <div :class="['send-btn prev-mail-btn flex-center', form.upId === '0' ? 'disabled-btn' : '']" @click="handleGotoNext(form.upId)">上一封</div>
      <div :class="['send-btn next-mail-btn flex-center', form.downId === 0+'' ? 'disabled-btn' : '']" @click="handleGotoNext(form.downId)">下一封</div>
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
  </MainBasicContainer>
</template>

<script>
import MailTopHeader from "@/views/mail/components/MailTopHeader"
import { getMailDetails, getSenderMail, sendMail, updateIsReceipt, getMailAccessory } from "@/api/mail"
import dayjs from 'dayjs'
import { formatToCNDateDay2 } from "@/util/util"
import { handleTransUrlAndDownLoadFile } from "@/util/util"
import { openLink } from "@/util/util"
import MailTopAction from "@/views/mail/components/MailTopAction"
import xss from 'xss'
import { getMailStr, replaceCIDImg, setIframeInnerHtml, transEnterTextBr } from "@/util/util"
import { mapState } from "vuex"
import MainBasicContainer from "@/components/BasicContainer/MainBasicContainer"
import { decodeRes } from "@/views/mail/t";

export default {
  name: "mailDetail",
  props: {
    detailEntry: {
      type: String
    },
    // 邮件id
    mailDetailId: {
      type: String
    },
    params: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    MainBasicContainer,
    MailTopHeader,
    MailTopAction
  },
  data() {
    return {
      loading: false,
      headerConfig: {
        back: true,
        fh: true,
        sc: true,
        cdsc: true,
        zf: true,
        qbhf: true,
        zcbj: true,
        ch: true,
        bjw: true,
        ydd: true
      },
      canChehui: false,
      backDialog: false,
      form: {
        "mailBody": {
          "id": 0,
          "groupId": 0,
          "userId": 0,
          "from": "",
          "replyTo": "",
          "sentDate": "",
          "subject": "",
          "isSend": 0,
          "urgent": 0,
          "receipt": 0,
          "isTiming": 0,
          "rend": 0,
          "answered": 0,
          "stress": 0,
          "delFlag": "0",
          "createTime": "",
          "updateTime": "",
          "tenantId": 0
        },
        "MailExtendList": [
        //   {
        //   "id": 0,
        //   "mailId": 0,
        //   "name": "xxx.pdf",
        //   "url": "",
        //   "createTime": "",
        //   "updateTime": "",
        //   "delFlag": "0",
        //   "tenantId": 0
        // }
        ],
        "MailRecipientList": [
        //   {
        //   "id": 0,
        //   "mailId": 0,
        //   "mail": "",
        //   "name": "",
        //   "mailType": { "value": "to" },
        //   "createTime": "",
        //   "updateTime": "",
        //   "delFlag": "0",
        //   "tenantId": 0
        // }
        ],
        "MailContent": {
          "id": 0,
          "mailId": 0,
          "text": "",
          "textType": "TEXT/HTML; charset=utf-8",
          "createTime": "",
          "updateTime": "",
          "delFlag": "0",
          "tenantId": 0
        },
        upId: 0,
        downId: 0
      },
      senderInfo: {
        account: '',
        username: ''
      },
      iframeHeight: 500 // iframe 高度
    }
  },
  mounted() {
    // console.log('this.detailEntry', this.detailEntry)
    switch (this.detailEntry) {
      // 收件箱 或 文件夹
      case 'inbox':
      case 'directory':
        this.headerConfig = {
          back: true,
          sc: true,
          cdsc: true,
          zf: true,
          hf: true,
          qbhf: true,
          bjw: true,
          ydd: true
        }
        break
      // 垃圾箱
      case 'spamMail':
        this.headerConfig = {
          back: true,
          cdsc: true,
          xr: true,
          zf: true,
          bjw: true,
          ydd: true
        }
        break
      // 草稿箱
      case 'drafts':
        this.headerConfig = {
          back: true,
          zcbj: true,
          sc: true,
          cdsc: true
        }
        break
      // 已发送
      case 'sentMail':
        this.headerConfig = {
          back: true,
          sc: true,
          cdsc: true,
          zf: true,
          qbhf: true,
          zcbj: true,
          ch: true,
          bjw: true,
          ydd: true
        }
        break
      // 已删除
      case 'deletedMail':
        this.headerConfig = {
          back: true,
          cdsc: true,
          bjw: true,
          ydd: true
        }
        break
    }

    this.init()
  },
  computed: {
    ...mapState({
      mailConfigId: state => state.socket.mailConfigId
    }),
    // 格式化 接收人
    formatedRecipientList() {
      const { MailRecipientList } = this.form
      const to = MailRecipientList.filter(item => item.mailType.value === 'to')
      const cc = MailRecipientList.filter(item => item.mailType.value === 'cc')
      const bcc = MailRecipientList.filter(item => item.mailType.value === 'bcc')

      const toArr = to.map(item => item.mail)
      const ccListArr = cc.map(item => item.mail)
      const bccListArr = bcc.map(item => item.mail)

      const formated = {
        to,
        toArr,
        cc,
        ccListArr,
        bcc,
        bccListArr
      }
      return formated
    }
  },
  methods: {
    getList() {
      this.$emit('getList', '1', this.mailConfigId)
    },
    init() {
      this.handleGetUserMail()
    },
    handleGetListCb() {
      this.$emit('changeMailTotal')
      this.$emit('refresh')
    },
    // 获取邮件详情
    async handleGetMailDetails() {
      // 清空
      this.iframeHeight = 0
      const el = document.querySelector('#mailContent')
      setIframeInnerHtml(el, '')

      this.loading = true
      const query = {
        id: this.params.id,
        configId:this.mailConfigId
      }
      // TODO: async
      getMailDetails(query).then(async res => {
      // const res = {
      //   data: decodeRes()}
      if (res.data.code === 0) {
        const resData = res.data.data
        // console.log(resData)

          // const htmlStr = transEnterTextBr(MailContent.text)

          const MailExtendListRes = await this.handleGetMailFile(query)
          if(MailExtendListRes === false) {
            this.$message.error('附件加载失败')
            resData.MailExtendList = []
          } else {
            resData.MailExtendList = MailExtendListRes
          }
          this.loading = false

          const { mailBody, MailExtendList, MailRecipientList, MailContent } = resData
          // console.log(MailContent)

          const replacedData = replaceCIDImg(MailExtendList, MailContent.text, MailContent.textType, true, true, 'preview')
          this.form = {
            ...resData,
            // 替换附件
            MailExtendList: replacedData.multipartFiles
          }

          // 更新未读数量
          this.$emit('changeMailTotal')
          // 设置 iframe 内容
          setIframeInnerHtml(el, replacedData.content)
          setTimeout(() => {
            this.changeFrameHeight()
          }, 100)
          // 已读回执
          if (mailBody.receipt === 1 && mailBody.isReceipt === 0 && mailBody.rend === 0 && mailBody.isSend === 1 && mailBody.groupId === 1) {
            const from = getMailStr(mailBody.from)
            const myEmail = getMailStr(this.senderInfo.username)
            const subject = mailBody.subject
            this.handleShowYdhzMsgbox(from, myEmail, subject)
          }
        }
      }).finally(() => {
        this.loading = false
      })
    },
    // 拉取附件
    // TODO:
    handleGetMailFile(query) {
      return new Promise((resolve, reject) => {
        resolve([])
        getMailAccessory(query).then(res => {
          if (res.data.code === 0) {
            resolve(res.data.data)
          }
        }).catch(e => {
          reject(e)
        })
      })
    },
    // 获取用户邮箱
    handleGetUserMail() {
      let query = {
        configId: this.mailConfigId
      }
      TODO:
      getSenderMail(query).then(res => {
        // console.log("33333333111",this.mailConfigId,query.mailConfigId)
        if (res.data.code === 0) {
          // const res = {data: {"code":200,"msg":"成功","data":{"id":71,"sysUserId":419,"mailType":"wangYi","account":"Beats0","sendHost":"smtp.163.com","acceptHost":"imap.163.com","sendPort":465,"acceptPort":993,"username":"jyl2047155291@163.com","sendProtocol":"smtp","acceptProtocol":"imap","defaultEncodingS":"UTF-8","useWay":1,"createTime":"2021-04-03T22:21:00","updateTime":"2021-04-03T22:21:00","delFlag":0,"tenantId":39,"name":"蒋远林"},"timestamp":"2021-06-09 15:07:32 863"}}
          const { username, account } = res.data.data
          this.senderInfo = {
            account,
            username
          }
          this.handleGetMailDetails()
        }
      })
    },
    /**
     * 显示已读回执Msgbox
     * @param   to      {String}  接受人地址
     * @param   from    {String}  发送人地址(自己)
     * @param   subject {String}  主题
     * */
    handleShowYdhzMsgbox(to,from, subject) {

      let _this = this
      this.$msgbox({
        title: '提示',
        message: `
          <div class="msgBoxInner">
            <div class="innerHeader">
              <i class="innerIcon el-icon-info warning-icon"></i>
              <span class="innerTitle">对方要求发送已读回执，是否发送？</span>
            </div>
           <div class="innerTip"></div>
          </div>
          `,
        dangerouslyUseHTMLString: true,
        customClass: 'customMsgBox',
        showCancelButton: true,
        confirmButtonText: '发送',
        confirmButtonClass: 'confirmBtn confirmButton',
        cancelButtonText: '不发送',
        cancelButtonClass: 'confirmBtn cancelButton',
        beforeClose: async (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '执行中...'
            _this.handleSendYdhzMail(to,from, subject).then(result => {
              if(result) {
                done()
                instance.confirmButtonLoading = false
              }
            })
          } else {
            done()
          }

          // 确认 已读回执 状态
          const query = {
            id: this.params.id
          }
          updateIsReceipt(query).then(res => {
            //
          })
        }
      }).then(action => {
        // this.$message({
        //   type: 'info',
        //   message: 'action: ' + action
        // });
      }).catch(e => {
      })
    },
    // 发送已读回执mail
    handleSendYdhzMail(toStr, from, subject) {
      return new Promise((resolve, reject) => {
        const time = dayjs().format('YYYY-MM-DD HH:mm')
        const text = `<p>这是邮件收条, ${time} 发给 ${toStr}<${toStr}>,  主题为 ${subject} 的信件已被接收</p>
                      <br>
                      <br>
                      <p>此收条只表明收件人的计算机上曾显示过此邮件</p>`
        const to = [{ name: toStr, mail: toStr }]

        const mailData = {
          bcc: [],
          cc: [],
          configId: 4,
          from,
          isRespectively: 0,
          isTiming: 0,
          multipartFiles: [],
          receipt: 0,
          replyTo: "",
          sentDate: "",
          subject,
          text,
          textType: "TEXT/HTML; charset=utf-8",
          timing: "",
          to,
          toFbfsArr: [],
          urgent: 0,
          userId: 0
        }

        sendMail(mailData).then(res => {
          if (res.data.code === 0) {
            this.$message.success('已发送已读回执')
            resolve('success')
          } else {
            reject('error')
          }
        }).catch(e => {
          reject(e)
        })
      })
    },
    // 设置 iframe 高度
    changeFrameHeight() {
      this.$nextTick(() => {
        const ele = document.querySelector('#mailContent')
        const iframeBody = ele.contentWindow.document.body
        // console.log(iframeBody.scrollHeight)

        // 监听点击链接 打开 操作
        const links = iframeBody.querySelectorAll('a')
        links.forEach(link => {
          link.addEventListener('click', (e) => {
            e.preventDefault()
            openLink(link.href)
          })
        })
        this.iframeHeight = iframeBody.scrollHeight + 100
      })
    },
    // 返回
    handleGoBack() {
      this.$emit('changeRoute', { routerName: this.detailEntry })
    },
    // action回调
    actionCallBack(args) {
      this.$emit('changeRoute', args)
    },
    /**
     * @param id   {Number} 上一封 upId， 下 一封 downId
     * */
    handleGotoNext(id) {
      if (id === 0) return
      this.$emit('changeRoute', { routerName: 'mailDetail', action: 'next', id })
    },
    // 回复
    handleFeedBack() {
      this.$emit('changeRoute', { routerName: 'writeMail', action: 'feedBack', id: '' })
    },
    // 撤回 dialog 隐现
    handleBackDialog(type) {
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
    },
    // 时间转换
    timerFormat(dateStr) {
      let str = dayjs(dateStr).format('YYYY年MM月DD日 HH:mm')
      str += ` （${formatToCNDateDay2(dayjs(dateStr).day())}）`
      return str
    },
    // 文件下载
    handleDownload(file) {
      handleTransUrlAndDownLoadFile(file.url)
    },
    // xss 过滤
    xssFilter(str) {
      return xss(str)
    }
  },
  watch: {
    'params': {
      handler(value) {
        this.handleGetMailDetails()
      }
    }
  }
}
</script>

<style scoped lang='scss'>
  .disabled-btn{
    display: none;
  }
</style>
