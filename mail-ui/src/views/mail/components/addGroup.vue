<template>
  <div class="group-list-container">
    <div class="group-list-row cursor" @click="handleShowAddGroupDialog">
      <i class="el-icon-circle-plus-outline add-circle-btn"></i>
      <span class="group-list-row-title">添加分组</span>
    </div>
    <div class="group-list-row2 cursor"
         v-for="(item, index) in groups"
         :key="'group-list-row' + index">
      <span @click="handleAddToGroup(item)" class="group-list-row-title">{{ item.name }}</span>
      <i class="el-icon-edit group-action-edit-btn" @click="handleEditGroup(item)"></i>
      <i class="el-icon-delete group-action-btn" @click="handleDelGroup(item)"></i>
    </div>
    <!-- 添加分组 -->
    <el-dialog append-to-body
               width="30%"
               :before-close="handleCloseAddGroupDialog"
               class="dialog-message-box add-group-dialog"
               :visible.sync='addGroupDialog'
               :close-on-click-modal="false">
      <div class="add-group-container">
        <div class="add-group-title">{{ actionMode === 'add' ? '添加分组' : '编辑分组' }}</div>
        <div class="add-group-sub">请输入新的分组名称</div>
        <el-input
          placeholder="请输入分组名"
          v-model="form.name"
          maxlength="15"
          show-word-limit
          style="width: 320px;margin-top: 25px;"
          clearable>
        </el-input>
      </div>
      <div class="flex-center" style="margin-top: 35px;margin-bottom: 10px;">
        <div class="add-group-footer-btn cursor"
             @click="handleAddGroupSubmit"
             style="margin-right: 35px;">确定</div>
        <div class="add-group-footer-btn cursor"
             @click="handleCloseAddGroupDialog"
             style="background: #EEEEEE;color: #222222">取消</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { queryGroup, saveOrUpdateGroup, delGroupItem, unGroupAdd } from "@/api/mail"
import { hasBlank } from "@/util/validate"

export default {
  name: "addGroup",
  props: {
    // 要添加列表
    selectedList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      groups: [
        // {id: '', name: 'xxx'}
      ],
      form: {
        name: ''
      },
      addGroupDialog: false,
      actionMode: '' // ['edit': 编辑, 'add': 添加]
    }
  },
  mounted() {
    this.handleQueryGroup()
  },
  methods: {
    // 获取邮件分组信息
    // TODO:
    handleQueryGroup() {
      const res = {data: {"code":0,"msg":"成功","data":[{"id":67,"groupName":"文件夹111","userId":419,"configId":71,"type":{"value":"yourself"},"createTime":"2021-04-04T10:49:41","updateTime":"2021-04-04T10:49:41","delFlag":0,"tenantId":39}],"timestamp":"2021-06-09 15:07:36 779"}}
      // queryGroup().then(res => {
        if (res.data.code === 0) {
          this.groups = res.data.data
        }
      // })
    },
    // 删除分组
    handleDelGroup(group) {
      delGroupItem(group.id).then(res => {
        if (res.data.code === 0) {
          this.$message.success('添加成功')
          this.handleQueryGroup()
          this.handleCloseAddGroupDialog()
        }
      })
    },
    // 编辑组
    handleEditGroup(group) {
      this.form = {
        id: group.id,
        name: group.name
      }
      this.actionMode = 'edit'
      this.addGroupDialog = true
    },
    // 添加到组
    handleAddToGroup(item) {
      if (this.selectedList.length === 0) {
        this.$message.error('请先选择要将分组的邮箱')
        return
      }

      this.$msgbox({
        title: '选择分组',
        message: `
          <div class="msgBoxInner">
            <div class="innerHeader">
              <i class="innerIcon el-icon-info warning-icon"></i>
              <span class="innerTitle">确定将邮箱账号移动到${item.name}吗？</span>
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
            const data = {
              id: item.id,
              list: this.selectedList
            }
            unGroupAdd(data).then(res => {
              if (res.data.code === 0) {
                this.$message.success('添加成功')
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
    handleAddGroupSubmit() {
      const actionMode = this.actionMode
      console.log(this.form)
      if (hasBlank(this.form.name)) {
        this.$message.error('请输入正确的分组名称')
        return
      }
      const data = this.form
      saveOrUpdateGroup(data).then(res => {
        if (res.data.code === 0) {
          this.$message.success(actionMode === 'add' ? '添加成功' : '修改成功')
          this.handleQueryGroup()
          this.handleCloseAddGroupDialog()
        }
      })
    },
    handleShowAddGroupDialog() {
      this.addGroupDialog = true
    },
    handleCloseAddGroupDialog() {
      this.addGroupDialog = false
      this.$emit('refresh')
      this.form = {}
    }
  }
}
</script>

<style scoped>

</style>
