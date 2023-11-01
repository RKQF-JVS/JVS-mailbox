<!--文件夹-->
<template>
  <BasicContainer :useBorder="false">
    <div class="top-label">
      <MailTopHeader :headerTitle="directoryRouterParams.title" @refresh="getList" @search="handleSearch"></MailTopHeader>
    </div>
    <MailTopAction :propIsCheckedAll="isCheckedAll"
                   :config="headerConfig"
                   :mailIds="mailIds"
                   entry="directory"
                   :stress="stress"
                   @refresh="getList"
                   @changeStress="changeStress"
                   @actionCallBack="actionCallBack"
                   @selectAll="selectAll"></MailTopAction>
    <avue-crud
      ref="crud"
      class="pager_curd mail_pager_curd"
      :page.sync="page"
      :data="tableData"
      :option="tableOption"
      :table-loading="tableLoading"
      :row-class-name="tableRowClassName"
      @refresh-change='getList'
      @cell-click="cellClick"
      @on-load='getList'
      v-if="tableData.length > 0">
      <template slot="from" slot-scope="scope">
        <div class="flex-align-center">
          <el-checkbox v-model="tableData[scope.row.$index].checked"
                       @change="handleCheckItem(scope.row.$index, $event)"
                       style="margin-right: 15px;"></el-checkbox>
<!--          <img src="@/assets/img/icon/mail.png" class="mail-type-icon" alt="" v-show="scope.row.receipt === 1">-->
          <i class="el-icon-info warningColor" style="margin-right: 5px;" v-show="scope.row.urgent === 1"></i>
          <span class="cursor ellipsis" @click="changeRoute(scope.row)">{{ scope.row.from || '未填写'}}</span>
        </div>
      </template>
      <template slot="stress" slot-scope="scope">
        <span class="mail-flag cursor" @click="handleFlag(scope.row.id, scope.row.stress === 0 ? 2 : 3)">
          <!-- 未标记 -->
          <span class="mail-flag-icon" v-if="scope.row.stress === 0"> <svg-icon :icon-class="'flag'"></svg-icon></span>
          <!-- 已标记 -->
          <span class="mail-flag-icon-active" v-else> <svg-icon :icon-class="'flag_fill'"></svg-icon></span>
        </span>
      </template>
    </avue-crud>
    <div class="flex-center nodataImgContainer" v-loading="tableLoading" v-else-if="tableData.length === 0 && isLoaded">
      <img src="@/assets/img/mail/nodata.png" class="nodataImg"  alt="">
    </div>
    <div class="flex-center nodataImgContainer" v-loading="tableLoading" v-else></div>
  </BasicContainer>
</template>

<script>
import BasicContainer from "@/components/BasicContainer/BasicContainer"
import { mailPageOption, inboxOption } from "@/const/const"
import MailTopHeader from "@/views/mail/components/MailTopHeader"
import { getMailList, postTagMail } from "@/api/mail"
import MailTopAction from "@/views/mail/components/MailTopAction"

export default {
  name: "directory",
  props: {
    // 文件夹参数
    directoryRouterParams: {
      type: Object,
      required: true
    }
  },
  components: {
    BasicContainer,
    MailTopHeader,
    MailTopAction
  },
  data() {
    return {
      headerConfig: {
        qx: true,
        sc: true,
        cdsc: true,
        zf: true,
        bjw: true,
        ydd: true,
        flag: true
      },
      page: JSON.parse(JSON.stringify(mailPageOption)),
      tableData: [],
      tableOption: inboxOption,
      tableLoading: true,
      isLoaded: false,
      subject: '',
      isCheckedAll: false,
      stress: 0, // 是否强调 [0, 不强调， 1： 强调]
    }
  },
  computed: {
    // 选中id
    mailIds() {
      const arr = []
      this.tableData.forEach(item => {
        if (item.checked) {
          arr.push(item.id)
        }
      })
      return arr
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    getList(page) {
      page = page || this.page
      this.tableLoading = true
      const query = {
        subject: this.subject,
        groupId: this.directoryRouterParams.id,
        current: page.currentPage,
        size: page.pageSize
      }
      // 是否强调
      if (this.stress === 1) {
        query.stress = 1
      }
      getMailList(query)
        .then(res => {
          if (res.data.code === 0) {
            const { page } = res.data.data
            const { records, pages, size, total } = page
            records.forEach(item => {
              item.checked = false
            })
            this.tableData = records
            this.page.total = total
            this.isCheckedAll = false
          }
        }).finally(() => {
          this.tableLoading = false
          this.isLoaded = true
        })
    },
    // 搜索
    handleSearch(k) {
      this.subject = k
      this.page.currentPage = 1
      this.getList()
    },
    // 全选
    selectAll(isAll) {
      this.isCheckedAll = isAll
      this.tableData.forEach(item => {
        item.checked = isAll
      })
    },
    // 单元格点击
    cellClick(row, column, cell, event) {
      if (column.property === 'subject') {
        this.changeRoute(row)
      }
    },
    /**
     * 标记单个
     * @param tag {Number} 2 红旗邮件, 3 取消红旗
     * */
    handleFlag(mailId, tag) {
      const data = {
        mailIds: String(mailId),
        tag
      }
      postTagMail(data).then(res => {
        if (res.data.code === 0) {
          this.getList()
        }
      })
    },
    // 单选
    handleCheckItem(index, checked) {
      // 点box的 change
      if (typeof checked === 'boolean') {
        this.tableData[index].checked = checked
      } else {
        this.tableData[index].checked = !this.tableData[index].checked
      }
      this.isCheckedAll = this.tableData.every(item => item.checked)
    },
    // 路由切换
    changeRoute({ id = '' }) {
      this.$emit('changeRoute', { routerName: 'mailDetail', id })
    },
    // action回调
    actionCallBack(args) {
      this.$emit('changeRoute', args)
    },
    changeStress() {
      this.stress = this.stress === 0 ? 1 : 0
      this.getList()
    },
    // 选中高亮
    tableRowClassName({ row, rowIndex }) {
      let classStr = ''
      if (row.checked) {
        classStr += ' highLight-row'
      }
      if (row.rend === 0) {
        classStr += ' unRead-highLight-row'
      }
      return classStr
    },
  },
  watch: {
    directoryRouterParams: {
      handler(routerParams) {
        this.getList()
      },
      deep: true
    }
  }
}
</script>

<style scoped>

</style>
