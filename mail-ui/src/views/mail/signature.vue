<template>
    <!-- 常规 个性签名 -->
    <div class="flex-column sign-form">
      <div class="sign-form-item" style="padding: 15px;">
        <div class="item-label" style="margin-top: 10px;">个性签名：</div>
        <div>
          <div>
            <el-select v-model="signId" @change="handleSelectSign" placeholder="请选择个性签名">
              <el-option :label="item.name" :value="item.id" v-for="item in signList" :key="item.id"></el-option>
            </el-select>
            <span class="primary cursor sign-text" @click="handleAddSignDialog('open')">添加个性签名</span>

          </div>
          <div v-show="signId !== '-1'">
            <div class="sign-preview">
              <div v-html="activeSignContent"></div>
            </div>
            <div class="flex-align-center">
              <div @click="handleEdit" class="mail-btn flex-center mail-btn-primary" style="margin-right: 30px;">编辑</div>
              <div @click="handleDelete" class="mail-btn flex-center mail-btn-disable">删除</div>
            </div>
          </div>
        </div>
      </div>
      <div class="flex-center">
        <div @click="handleSave" class="save-btn flex-center cursor">
          <span>保存设置</span>
        </div>
      </div>
      <!--添加签名 dialog  -->
        <el-dialog append-to-body
                width="40%"
                :visible.sync='signDialog'
                :close-on-click-modal="false">
        <div slot="title" class="dialog-header-row">
            <div class="dialog-tip"></div>
            <span class="el-dialog__title">操作个性签名</span>
        </div>
        <SignDialog :actionType="actionType"
                    :rowData="rowData"
                    @handleSignSubmit="handleSignSubmit"
                    @handleSignClose="handleSignClose"
                    v-if="signDialog"></SignDialog>
        </el-dialog>
    </div>
  </template>
  
  <script>
  import { mailPageOption } from "@/const/const"
  import SignDialog from "@/views/mail/signDialog"
  import { getSignature, saveSignature, editSignature, delSignature, tagAllRead, setDefault } from "@/api/mail"
  export default {
    props:{
    },
    data() {
      return {
        signId: '-1',
        signList: [
            { id: '-1', name: '不使用签名' }
        ],
        signDialog: false,
        formData: {
            name: '',
            content: ''
        },
        rowData: {}, // 编辑数据
        actionType: '',// 操作方式 [add, edit, preview]
        mailPageSize: '',
      };
    },
    components:{
        SignDialog
    },
    created() {
      
    },
    mounted() {
        this.handleGetSignature()
        const mailPageSize = localStorage.getItem('mailPageSize') || '25'
        this.mailPageSize = String(mailPageSize)
    },
    computed: {
        // 选中签名内容
        activeSignContent() {
            const signId = this.signId
            let content = ''
            this.signList.forEach(item => {
                if (item.id === signId) {
                content = item.content
                }
            })
            return content
        }
    },
    methods: {
        // 签名提交
        handleSignSubmit(data) {
            const requestApi = this.actionType === 'add' ? saveSignature : editSignature
            requestApi(data).then(res => {
                if (res.data.code === 0) {
                this.handleSignClose()
                this.handleGetSignature()
                }
            })
        },
        handleSignClose() {
            this.signDialog = false
            this.handleResetForm()
        },
        // form 清空
        handleResetForm() {
            this.formData = {
                name: '',
                content: ''
            }
        },
        // 获取个性签名
        handleGetSignature() {
          getSignature().then(res => {
            if (res.data.code === 0) {
              const currentItem = res.data.data.filter(item => item.isDefault === 1)
              // 未设置签名的
              if(!currentItem || currentItem.length === 0) {
                this.signId = '-1'
              } else {
                this.signId = currentItem[0].id
              }
              // console.log('currentItem', currentItem)
              this.signList = [{ id: '-1', name: '不使用签名' }, ...res.data.data]
            }
          })
        },
        handleSelectSign(id) {
            this.signList.forEach(item => {
                if (item.id === id) {
                this.rowData = item
                }
            })
        },
        handleEdit() {
            this.handleSelectSign(this.signId)
            this.actionType = 'edit'
            this.signDialog = true
        },
        // 删除签名
        handleDelete() {
            this.$msgbox({
                title: '删除确认',
                message: `
                <div class="msgBoxInner">
                    <div class="innerHeader">
                    <i class="innerIcon el-icon-delete warning-icon"></i>
                    <span class="innerTitle">您确定要删除该条个性签名？</span>
                    </div>
                <div class="innerTip">删除后，将不会在显示该个性签名</div>
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

                    const id = this.signId
                    delSignature({ id }).then(res => {
                    if (res.data.code === 0) {
                        this.handleGetSignature()
                        // 改为无签名
                        this.signId = '-1'
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
        // 保存设置
        handleSave() {
            localStorage.setItem('mailPageSize', String(this.mailPageSize))
            mailPageOption.pageSize = Number(this.mailPageSize)
            // 保存签名
            const query = {
                id: this.signId
            }
            setDefault(query).then(res => {
                if (res.data.code === 0) {
                this.$message.success('保存成功')
                }
            })
        },
        handleAddSignDialog() {
            this.signDialog = true
            this.actionType = 'add'
        },
    }
  };
  </script>
  
  <style scoped lang="scss">
  
  </style>
  <style lang="scss">
  
  </style>