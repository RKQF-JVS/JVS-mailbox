<!-- 上传组件 支持分片上传-->
<template>
  <div class="uploadFile upload-container">
    <el-upload
        class="upload-block"
        ref="uploadFilePiecess"
        action="'"
        multiple
        :before-upload="beforeExcelUpload"
        :http-request="handleUploadRequest"
        :show-file-list='false'
        :accept="accept">
      <el-button size="small" type="primary" class="upload-btn flex-center" v-if="!lineMode">
        <i class="add-btn el-icon-circle-plus-outline" style="font-size: 18px;margin-right: 8px;"></i>
        {{uploadText}}</el-button>
      <div v-else>{{uploadText}}</div>
    </el-upload>
    <!-- 进度条  -->
    <el-dialog append-to-body
               width="30%"
               class="dialog-message-box"
               :close-on-click-modal="false"
               :close-on-press-escape="false"
               :show-close="false"
               :visible.sync='uploadDialog'>
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">提示</span>
      </div>
      <div>
        <div class="warning-content">
          <span class="warning-title"><i class="el-icon-info warning-icon"></i>上传中</span>
        </div>
        <div style="padding: 0 20px;margin-top: 20px;margin-bottom: 30px;" v-if="uploadPercent > 0">
          <el-progress :percentage="uploadPercent"></el-progress>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {uploadByPieces} from '@/util/util'
import { getFileUrlLarger } from "@/api/system"
export default {
  name: 'UploadFilePiecess',
  props: {
    // format="rar,zip,doc,docx,pdf,jpg,bmp,png"
    format: {
      type: String,
      default: 'pdf,ppt,pptx,doc,docx,rar,zip,jpg,bmp,png,xlsx,xls'
    },
    uploadText: {
      type: String,
      default: '点击上传'
    },
    errorText: {
      type: String,
      default: '请上传正确的文件'
    },
    accept: {
      type: String,
      default: '.pdf, .ppt, .pptx, .doc, .docx, .rar, .zip, .jpg, .bmp, .png, .xlsx, .xls, application/pdf'
    },
    // 文件大小限制
    maxSize: {
      type: Number
    },
    // 使用 line 模式
    lineMode: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      loadingText: '上传进度',
      fileList: [],
      uploadDialog: false,
      uploadPercent: 0,
    }
  },
  computed: {
  },
  watch: {
    fileList (fileList, oldFileList) {
      if(fileList.length) {
        this.$nextTick(() => {
          this.dealUpload()
        })
      }
    }
  },
  methods: {
    // 上传前
    beforeExcelUpload (file) {
      return new Promise((resolve, reject) => {
        const format = file.name.split('.')
        const suffix = format[format.length - 1]
        const formatArr = this.format.split(',')
        if (format.length < 2) {
          this.$message.error('您上传文件的格式不正确！')
          reject(false)
          return false
        }
        if (this.format !== 'all') {
          if (formatArr.indexOf(suffix) === -1) {
            this.$message.error(this.errorText)
            reject(false)
            return false
          }
        }

        // 弹窗确认
        this.$msgbox({
          title: '附件提示',
          message: `
          <div class="msgBoxInner">
            <div class="innerHeader">
              <i class="innerIcon el-icon-info info-icon"></i>
              <span class="innerTitle">超大附件30天后下载地址将会失效</span>
            </div>
           <div class="innerTip">是否继续上传？</div>
          </div>
          `,
          dangerouslyUseHTMLString: true,
          customClass: 'customMsgBox',
          showCancelButton: true,
          confirmButtonText: '是，继续上传',
          confirmButtonClass: 'confirmBtn confirmButton',
          cancelButtonText: '否，取消上传',
          cancelButtonClass: 'confirmBtn cancelButton',
          beforeClose: (action, instance, done) => {
            if (action === 'confirm') {
              resolve(true)
              done()
            } else {
              reject(false)
              done()
            }
          }
        }).then(action => {
        }).catch(e => {
          reject(false)
        })
      })
    },
    // 上传请求
    handleUploadRequest (back) {
      this.fileList.push(back.file)
    },
    // 处理上传文件
    dealUpload () {
      uploadByPieces({
        files: this.fileList,
        // 5M,单位M
        pieceSize: 5,
        progress: (num, file) => {
          // console.log(`上传进度${num}%`)
          // this.uploadDialog = true
          this.uploadPercent = num
          this.$emit('progress', num, file)
        },
        success: (data) => {
          this.uploadDialog = false
          this.handleGetFileUrlLarger(data)
          this.fileList = []
          this.$emit('success', data.fileName)
        },
        error: (e) => {
          this.uploadDialog = false
          this.$message.error('文件上传失败')
          this.fileList = []
          this.$emit('error')
        }
      })
    },
    // 获取文件链接
    handleGetFileUrlLarger(fileData) {
      // const fileData = {
      //   bucketName: 'manage',
      //   fileName: 'bigfile/2021-02-26/15247c1cf50fc4c04d25095d83e8f43a.zip',
      //   name: 'xxx.zip',
      //   size: 1080,
      // }
      getFileUrlLarger(fileData.bucketName, fileData.fileName).then(res => {
        if(res.data.code === 0) {
          const resData = {
            name: fileData.name,
            size: fileData.size,
            url: res.data.data.data
          }
          this.$emit('uploadSuccess', resData)
        }
      })
    }
  },
}
</script>
<style lang="scss">
.upload-block {
  .el-upload {
    text-align: unset;
  }
}
</style>
