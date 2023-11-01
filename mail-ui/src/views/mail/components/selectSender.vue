<!--选择发送人-->
<!--<template>-->
<!--  <div class="select-container">-->
<!--    <div class="select-view select-left">-->
<!--      <div class="select-search" style="margin-top: 15px;">-->
<!--        <div class="select-title">所有联系人</div>-->
<!--      </div>-->
<!--      <div style="background: #F5F5F5;margin: 15px 0;">-->
<!--        <el-scrollbar class="select-left-list mail-select-list-collapse noScroll" style="overflow:hidden;">-->
<!--          &lt;!&ndash; 一级  &ndash;&gt;-->
<!--          <el-collapse v-model="activeCollapse" accordion>-->
<!--            <el-collapse-item :name="group.id"-->
<!--                              @click.native="changeCollage(group)"-->
<!--                              class="mail-collapse-head1"-->
<!--                              v-for="(group, index) in groups"-->
<!--                              :key="'groups' + index">-->
<!--              <template slot="title">-->
<!--                <div class="mail-collapse1 ellipsis">{{ group.name }}</div>-->
<!--              </template>-->
<!--              &lt;!&ndash; 公司组 二级 &ndash;&gt;-->
<!--              <el-collapse v-model="childActiveCollapse" accordion v-if="group.type === 'gs'">-->
<!--                <el-collapse-item :name="children.id"-->
<!--                                  class="mail-collapse-head2"-->
<!--                                  v-for="(children, index) in group.childList"-->
<!--                                  :key="'children' + index">-->
<!--                  <template slot="title">-->
<!--                    <div class="mail-collapse2 flex-center">-->
<!--                      <span class="tip" style="margin-right: 8px;"></span>-->
<!--                      <div class="mail-collapse2-title ellipsis">-->
<!--                        {{ children.name }}-->
<!--                      </div>-->
<!--                    </div>-->
<!--                  </template>-->
<!--                  &lt;!&ndash; 邮箱 &ndash;&gt;-->
<!--                  <div class="mail-list-container">-->
<!--                    <div @click="handleChangeUser(`${mailUser.username}<${mailUser.email}>`)"-->
<!--                         v-for="(mailUser, index) in mailUserList"-->
<!--                         v-show="showEmail(mailUser.email)"-->
<!--                         :key="'mailUser' + index"-->
<!--                         class="mail-item">-->
<!--                      <div class="user-mail ellipsis">{{ mailUser.username || '' }} <{{ mailUser.email }}></div>-->
<!--                      <div class="user-active">-->
<!--                        <i class="el-icon-success user-checked" v-if="isUserSelected(`${mailUser.username}<${mailUser.email}>`)"></i>-->
<!--                        <i class="el-icon-success user-unchecked" v-else></i>-->
<!--                      </div>-->
<!--                    </div>-->
<!--                  </div>-->
<!--                </el-collapse-item>-->
<!--              </el-collapse>-->
<!--              &lt;!&ndash; 分组的 &ndash;&gt;-->
<!--              <el-collapse v-model="groupActiveCollapse" accordion v-else-if="group.type === 'grouped'">-->
<!--                <div class="mail-list-container">-->
<!--                  <div @click="handleChangeUser(`${mailUser.username}<${mailUser.email}>`)"-->
<!--                       v-for="(mailUser, index) in groupList"-->
<!--                       v-show="showEmail(mailUser.email)"-->
<!--                       :key="'mailUser' + index"-->
<!--                       class="mail-item">-->
<!--                    <div class="user-mail ellipsis">{{ mailUser.email }}</div>-->
<!--                    <div class="user-active">-->
<!--                      <i class="el-icon-success user-checked" v-if="isUserSelected(`${mailUser.username}<${mailUser.email}>`)"></i>-->
<!--                      <i class="el-icon-success user-unchecked" v-else></i>-->
<!--                    </div>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </el-collapse>-->
<!--              &lt;!&ndash; 未分组的 &ndash;&gt;-->
<!--              <el-collapse v-model="unGroupActiveCollapse" accordion v-else-if="group.type === 'unGrouped'">-->
<!--                <div class="mail-list-container">-->
<!--                  <div @click="handleChangeUser(mailUser.email)"-->
<!--                       v-for="(mailUser, index) in unGroupList"-->
<!--                       v-show="showEmail(mailUser.email)"-->
<!--                       :key="'mailUser' + index"-->
<!--                       class="mail-item">-->
<!--                    <div class="user-mail ellipsis">{{ mailUser.email }}</div>-->
<!--                    <div class="user-active">-->
<!--                      <i class="el-icon-success user-checked" v-if="isUserSelected(mailUser.email)"></i>-->
<!--                      <i class="el-icon-success user-unchecked" v-else></i>-->
<!--                    </div>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </el-collapse>-->
<!--            </el-collapse-item>-->
<!--          </el-collapse>-->
<!--        </el-scrollbar>-->
<!--      </div>-->
<!--      <div @click="handleShowAddGroupDialog"-->
<!--           class="flex-space-between cursor"-->
<!--           style="justify-content: flex-start;margin-top: 20px;">-->
<!--        <img src="@/assets/img/icon/mail/addGroup.png" class="addGroup-btn" alt="">-->
<!--        <span class="addGroup-text">添加分组</span>-->
<!--      </div>-->
<!--    </div>-->
<!--    <div class="select-view select-right" style="padding-top: 15px;">-->
<!--      <div class="select-title">收件人（{{ userMailSelected.length }}）</div>-->
<!--      <el-scrollbar class="selected-list-outer noScroll" style="background: #F5F5F5;margin-top: 15px;overflow: hidden">-->
<!--        <div class="mail-selected-list">-->
<!--          <div class="selected-item" v-for="(mail, index) of userMailSelected" :key="'userListsSelected' + index">-->
<!--            <div class="selected-inner flex-space-between" @click="handleChangeUser(mail)">-->
<!--              <span class="ellipsis">{{ mail }}</span>-->
<!--              <i class="el-icon-circle-close"></i>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </el-scrollbar>-->
<!--      <div class="flex-space-between" style="margin-top: 30px;">-->
<!--        <el-button type="primary" size="small" @click="handleSelect" style="width: 120px;">确认</el-button>-->
<!--        <el-button @click="selectCancel" size="small" style="width: 120px;">取消</el-button>-->
<!--      </div>-->
<!--    </div>-->
<!--    &lt;!&ndash; 选择分组   &ndash;&gt;-->
<!--    <el-dialog append-to-body-->
<!--               width="50%"-->
<!--               :before-close="handleCloseAddGroupDialog"-->
<!--               class="dialog-message-box"-->
<!--               :visible.sync='addGroupDialog'-->
<!--               :close-on-click-modal="false">-->
<!--      <div slot="title" class="dialog-header-row">-->
<!--        <div class="dialog-tip"></div>-->
<!--        <span class="el-dialog__title">选择分组</span>-->
<!--      </div>-->
<!--      <addGroup v-if="addGroupDialog" :selectedList="userMailSelected"></addGroup>-->
<!--    </el-dialog>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--  import { getDeptTree, getUserByDeptId } from "@/api/dept"-->
<!--  import { queryEmail } from "@/api/system";-->
<!--  import { getMailNameValue, isEmail, isLikeEmail } from "@/utils/validate"-->
<!--  import addGroup from "@/views/mail/components/addGroup"-->
<!--  import { getEmailByGroupId, queryGroup, getEmailByUnGroup } from "@/api/mail"-->

<!--  export default {-->
<!--    name: "selectSender",-->
<!--    props: {-->
<!--      selectedList: {-->
<!--        type: Array-->
<!--      },-->
<!--      // 选择人类型 ['to': 收件人, 'ccList': 抄送, 'bccList': 密送]-->
<!--      addType: {-->
<!--        type: String,-->
<!--        required: true-->
<!--      }-->
<!--    },-->
<!--    components: {-->
<!--      addGroup-->
<!--    },-->
<!--    mounted() {-->
<!--      this.handleGetDeptTree()-->
<!--      this.initSerListsSelected()-->
<!--    },-->
<!--    data() {-->
<!--      return {-->
<!--        isIndeterminate: true,-->
<!--        // 所有的-->
<!--        allUserList: [],-->
<!--        userMailSelected: [],-->
<!--        userListsSelected: [],-->
<!--        count: 0, // 防止多加-->
<!--        activeCollapse: '',-->
<!--        childActiveCollapse: '',-->
<!--        groupActiveCollapse: '',-->
<!--        unGroupActiveCollapse: '',-->
<!--        userList: [],-->
<!--        defaultProps: {-->
<!--          children: 'children',-->
<!--          label: 'name'-->
<!--        },-->
<!--        mailUserList: [],-->
<!--        // 公司的-->
<!--        groups: [],-->
<!--        // 分组的-->
<!--        groupList: [],-->
<!--        // 未分组的-->
<!--        unGroupList: [],-->
<!--        addGroupDialog: false-->
<!--      }-->
<!--    },-->
<!--    computed: {-->
<!--      selectedUser() {-->
<!--        return this.allUserList.filter(user => user.checked)-->
<!--      }-->
<!--    },-->
<!--    methods: {-->
<!--      // 回显选中人-->
<!--      initSerListsSelected() {-->
<!--        let userMailSelected = [...this.selectedList]-->
<!--        let userListsSelected = []-->

<!--        getUserByDeptId({ deptId: 0 }).then(res => {-->
<!--          if (res.data.code === 0) {-->
<!--            let lists = res.data.data-->
<!--            userMailSelected.forEach(email => {-->
<!--              // 统一 未分组-->
<!--              userListsSelected.push({ email, username: '' })-->
<!--            })-->

<!--            lists.forEach(user => {-->
<!--              user.username = user.realName-->
<!--              userListsSelected.forEach(suser => {-->
<!--                suser.username = user.realName-->
<!--                // 有公司组-->
<!--                if (suser.email === user.email) {-->
<!--                  suser.username = user.username-->
<!--                }-->
<!--              })-->
<!--            })-->

<!--            this.userMailSelected = userMailSelected-->
<!--            this.userListsSelected = userListsSelected-->
<!--          }-->
<!--        })-->
<!--      },-->
<!--      // 获取公司分组-->
<!--      handleGetDeptTree() {-->
<!--        const groups = []-->
<!--        // 公司分组-->
<!--        getDeptTree().then(res => {-->
<!--          if (res.data.code === 0) {-->
<!--            const gsLists = res.data.data-->
<!--            gsLists.forEach(item => {-->
<!--              item.type = 'gs'-->
<!--            })-->
<!--            groups.push(...gsLists)-->
<!--            const deptId = res.data.data[0].id-->
<!--            this.handleGetDeptPage(deptId)-->
<!--            // 获取已经分组的-->
<!--            queryGroup().then(gres => {-->
<!--              if (gres.data.code === 0) {-->
<!--                const addedList = gres.data.data-->
<!--                // 分组标识-->
<!--                addedList.forEach(item => {-->
<!--                  item.type = 'grouped'-->
<!--                })-->
<!--                groups.push(...addedList)-->
<!--                // 获取未分组的-->
<!--                getEmailByUnGroup().then(unres => {-->
<!--                  if (unres.data.code === 0) {-->
<!--                    const unAddedList = [{ name: '未分组', id: '-1', type: 'unGrouped' }]-->
<!--                    this.unGroupList = unres.data.data-->
<!--                    groups.push(...unAddedList)-->
<!--                    this.groups = groups-->
<!--                  }-->
<!--                })-->
<!--              }-->
<!--            })-->
<!--          }-->
<!--        })-->
<!--      },-->
<!--      handleGetDeptPage(deptId) {-->
<!--        getUserByDeptId({ deptId }).then(res => {-->
<!--          if (res.data.code === 0) {-->
<!--            const lists = res.data.data-->
<!--            lists.forEach(item => {-->
<!--              item.username = item.realName-->
<!--            })-->
<!--            this.mailUserList = lists-->
<!--          }-->
<!--        })-->
<!--      },-->
<!--      //  分组 查邮箱列表-->
<!--      handleGetEmailByGroupId(groupId) {-->
<!--        getEmailByGroupId(groupId).then(res => {-->
<!--          if (res.data.code === 0) {-->
<!--            this.groupList = res.data.data-->
<!--          }-->
<!--        })-->
<!--      },-->
<!--      // 邮箱选中-->
<!--      handleChangeUser(email) {-->
<!--        if (!isLikeEmail(email)) {-->
<!--          this.$message.info('邮箱格式错误')-->
<!--          return-->
<!--        }-->
<!--        // const mailData = getMailNameValue(email)-->
<!--        // email = mailData.email-->
<!--        if (this.userMailSelected.includes(email)) {-->
<!--          const index = this.userMailSelected.indexOf(email)-->
<!--          if (index === -1) return-->
<!--          this.userMailSelected.splice(index, 1)-->
<!--          // this.userListsSelected = this.userListsSelected.filter(item => item.email !== email)-->
<!--        } else {-->
<!--          this.userMailSelected.push(email)-->
<!--          // this.userListsSelected.push({email, username})-->
<!--        }-->
<!--      },-->
<!--      // 是否为email-->
<!--      showEmail(email) {-->
<!--        return isEmail(email)-->
<!--      },-->
<!--      handleSelect() {-->
<!--        // const allUserList = this.allUserList.filter(item => item.checked)-->
<!--        // const lists = []-->
<!--        // allUserList.forEach(user => {-->
<!--        //   lists.push({-->
<!--        //     id: user.userId,-->
<!--        //     username: user.username-->
<!--        //   })-->
<!--        // })-->
<!--        // const data = {-->
<!--        //   lists-->
<!--        // }-->
<!--        const list = this.userMailSelected-->
<!--        this.$emit('selectUserSuccess', list, this.addType)-->
<!--      },-->
<!--      selectCancel() {-->
<!--        this.$emit('selectCancel')-->
<!--      },-->
<!--      isUserSelected(mailStr) {-->
<!--        return this.userMailSelected.includes(mailStr)-->
<!--      },-->
<!--      changeCollage(group) {-->
<!--        // 分组的-->
<!--        if (group.type === 'grouped') {-->
<!--          this.handleGetEmailByGroupId(group.id)-->
<!--        } else if (group.type === 'unGrouped') {-->
<!--          // 获取未分组的-->
<!--          getEmailByUnGroup().then(unres => {-->
<!--            if (unres.data.code === 0) {-->
<!--              this.unGroupList = unres.data.data-->
<!--            }-->
<!--          })-->
<!--        }-->
<!--      },-->
<!--      // 添加分组 dialog-->
<!--      handleShowAddGroupDialog() {-->
<!--        this.addGroupDialog = true-->
<!--      },-->
<!--      handleCloseAddGroupDialog() {-->
<!--        this.handleGetDeptTree()-->
<!--        this.addGroupDialog = false-->
<!--      }-->
<!--    },-->
<!--    watch: {-->
<!--      // 公司的-->
<!--      childActiveCollapse(newval, oldval) {-->
<!--        if (newval !== oldval && typeof newval === 'number') {-->
<!--          this.handleGetDeptPage(newval)-->
<!--        }-->
<!--      }-->
<!--    }-->
<!--  }-->
<!--</script>-->

<!--<style scoped>-->

<!--</style>-->


<!--选择发送人-->
<template>
  <div class="select-container">
    <div class="select-view select-left">
      <div class="select-search">
        <div class="select-title">所有联系人</div>
      </div>
      <div style="background: #F5F5F5;margin: 15px 0;" v-loading="groupLoading">
        <el-scrollbar class="select-left-list mail-select-list-collapse noScroll" style="overflow:hidden;">
          <!-- 一级  -->
          <el-collapse v-model="activeCollapse" accordion>
            <el-collapse-item :name="group.id"
                              @click.native="changeCollage(group)"
                              class="mail-collapse-head1"
                              v-for="(group, index) in groups"
                              :key="'groups' + index">
              <template slot="title">
                <div class="mail-collapse1 ellipsis">{{ group.name }}</div>
              </template>
              <Tree :groups="group.childList" :mailUserList="mailUserList" @handleChangeUsers="handleChangeUser"></Tree>
              <!-- 公司组 二级 -->
               <!-- <el-collapse v-model="childActiveCollapse" accordion v-if="group.cjsl !== 0">
                <el-collapse-item :name="children.id"
                                  class="mail-collapse-head2"
                                  v-for="(children, index) in group.childList"
                                  :key="'children' + index">
                  <template slot="title">
                    <div class="mail-collapse2 flex-center">
                      <span class="tip" style="margin-right: 8px;"></span>
                      <div class="mail-collapse2-title ellipsis">
                        {{ children.name }}
                      </div>
                    </div>
                  </template> -->
                  <!-- 邮箱 -->
                  <!-- <div class="mail-list-container">
                    <div @click="handleChangeUser(mailUser.email, mailUser.username)"
                          v-for="(mailUser, index) in mailUserList"
                          v-show="showEmail(mailUser.email)"
                         :key="'mailUser' + index"
                         class="mail-item">
                      <div class="user-mail ellipsis">{{ mailUser.username || '' }} <{{ mailUser.email }}></div>
                      <div class="user-active">
                        <i class="el-icon-success user-checked" v-if="isUserSelected(mailUser)"></i>
                        <i class="el-icon-success user-unchecked" v-else></i>
                      </div>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse> -->
              <!-- 分组的 -->
              <el-collapse v-model="groupActiveCollapse" accordion v-if="group.type === 'grouped'">
                <div class="mail-list-container">
                  <div @click="handleChangeUser(mailUser.email, mailUser.username)"
                       v-for="(mailUser, index) in groupList"
                       v-show="showEmail(mailUser.email)"
                       :key="'mailUser' + index"
                       class="mail-item">
                    <div class="user-mail ellipsis">{{ mailUser.email }}</div>
                    <div class="user-active">
                      <i class="el-icon-success user-checked" v-if="isUserSelected(mailUser)"></i>
                      <i class="el-icon-success user-unchecked" v-else></i>
                    </div>
                  </div>
                </div>
              </el-collapse>
              <!-- 未分组的 -->
              <el-collapse v-model="unGroupActiveCollapse" accordion v-else-if="group.type === 'unGrouped'">
                <div class="mail-list-container">
                  <div @click="handleChangeUser(mailUser.email)"
                       v-for="(mailUser, index) in unGroupList"
                       v-show="showEmail(mailUser.email)"
                       :key="'mailUser' + index"
                       class="mail-item">
                    <div class="user-mail ellipsis">{{ mailUser.email }}</div>
                    <div class="user-active">
                      <i class="el-icon-success user-checked" v-if="isUserSelected(mailUser)"></i>
                      <i class="el-icon-success user-unchecked" v-else></i>
                    </div>
                  </div>
                </div>
              </el-collapse>
            </el-collapse-item>
          </el-collapse>
        </el-scrollbar>
      </div>
      <div @click="handleShowAddGroupDialog"
           class="flex-space-between cursor"
           style="justify-content: flex-start;margin-top: 20px;">
        <img src="@/assets/img/icon/mail/addGroup.png" class="addGroup-btn" alt="">
        <span class="addGroup-text">添加分组</span>
      </div>
    </div>
    <div class="select-view select-right">
      <div class="select-title">收件人（{{ userListsSelected.length }}）</div>
      <el-scrollbar class="selected-list-outer noScroll" :style="{height:(groups.length>0?'82%':'')}">
        <div class="mail-selected-list" v-loading="groupLoading">
          <div class="selected-item" v-for="(user, index) of userListsSelected" :key="'userListsSelected' + index">
            <div class="selected-inner flex-space-between" @click="handleChangeUser(user.email, user.username)">
              <span class="ellipsis">{{ user.username || user.email }}</span>
              <i class="el-icon-circle-close"></i>
            </div>
          </div>
        </div>
      </el-scrollbar>
      <div class="flex-space-between" style="margin: 20px 0;">
        <el-button type="primary" size="small" @click="handleSelect" style="width: 120px;">确认</el-button>
        <el-button @click="selectCancel" size="small" style="width: 120px;">取消</el-button>
      </div>
    </div>
    <!-- 选择分组   -->
    <el-dialog append-to-body
               width="50%"
               :before-close="handleCloseAddGroupDialog"
               class="dialog-message-box"
               :visible.sync='addGroupDialog'
               :close-on-click-modal="false">
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">选择分组</span>
      </div>
      <addGroup v-if="addGroupDialog" :selectedList="userMailSelected"></addGroup>
    </el-dialog>
  </div>
</template>

<script>
import { getDeptTree, getUserByDeptId } from "@/api/dept"
// import { queryEmail } from "@/api/system";
import { isEmail } from "@/util/validate"
import addGroup from "@/views/mail/components/addGroup"
import { getEmailByGroupId, queryGroup, getEmailByUnGroup } from "@/api/mail"
import Tree from './Tree.vue'

export default {
  name: "selectSender",
  props: {
    selectedList: {
      type: Array
    },
    // 选择人类型 ['to': 收件人, 'ccList': 抄送, 'bccList': 密送]
    addType: {
      type: String,
      required: true
    }
  },
  components: {
    addGroup,
    Tree
  },
  mounted() {
    this.handleGetDeptTree()
    this.initSerListsSelected()
  },
  data() {
    return {
      isIndeterminate: true,
      // 所有的
      allUserList: [],
      userMailSelected: [],
      userListsSelected: [],
      count: 0, // 防止多加
      activeCollapse: '',
      childActiveCollapse: '',
      groupActiveCollapse: '',
      unGroupActiveCollapse: '',
      userList: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      mailUserList: [],
      // 公司的
      groups: [],
      // 分组的
      groupList: [],
      // 未分组的
      unGroupList: [],
      addGroupDialog: false,
      groupLoading:false //loading
    }
  },
  computed: {
    selectedUser() {
      return this.allUserList.filter(user => user.checked)
    }
  },
  methods: {
    // 回显选中人
    initSerListsSelected() {
      let userMailSelected = [...this.selectedList]
      let userListsSelected = []
      getUserByDeptId({ deptId: 0 }).then(res => {
        if (res.data.code === 0) {
          let lists = res.data.data
          userMailSelected.forEach(email => {
            // 统一 未分组
            userListsSelected.push({ email, username: '' })
          })

          lists.forEach(user => {
            user.username = user.realName
            userListsSelected.forEach(suser => {
              suser.username = user.realName
              // 有公司组
              if (suser.email === user.email) {
                suser.username = user.username
              }
            })
          })

          this.userMailSelected = userMailSelected
          this.userListsSelected = userListsSelected
        }
      })
    },
    //递归函数
    fndg(gsLists){
      let b=0
      // console.log(b,gsLists,"9999999999999")
      for(let i=0;i<gsLists.length;i++){
        if(gsLists[i].childList){
          b++
          gsLists[i].cjsl=b;
          // console.log(gsLists[i].childList,"iiiiiii")
          // console.log(b,i,"9999999999999")
          this.fndg(gsLists[i].childList)
        }else{
          // console.log(gsLists[i],"1111111111iiiiiii")
          // console.log(b,i,"9999999999999")
          b=0
        }
      }
    },
    // 获取公司分组
    handleGetDeptTree() {
      this.groupLoading = true
      const groups = []
      // 公司分组
      getDeptTree().then(res => {
        if (res.data.code === 0) {
          const gsLists = res.data.data
          gsLists.forEach(item => {
            item.type = 'gs'
          })
          this.fndg(gsLists)
          groups.push(...gsLists)
          const deptId = res.data.data[0].id
          this.handleGetDeptPage(deptId)
          // 获取已经分组的
          queryGroup().then(gres => {
            if (gres.data.code === 0) {
              const addedList = gres.data.data
              // 分组标识
              addedList.forEach(item => {
                item.type = 'grouped'
              })
              groups.push(...addedList)
              // 获取未分组的
              getEmailByUnGroup().then(unres => {
                if (unres.data.code === 0) {
                  const unAddedList = [{ name: '未分组', id: '-1', type: 'unGrouped' }]
                  this.unGroupList = unres.data.data
                  groups.push(...unAddedList)
                  this.groups = groups
                  this.groupLoading = false
                }
              })
            }
          })
        }
      })
    },
    handleGetDeptPage(deptId) {
      getUserByDeptId({ deptId }).then(res => {
        if (res.data.code === 0) {
          const lists = res.data.data
          lists.forEach(item => {
            item.username = item.realName
          })
          this.mailUserList = lists
        }
      })
    },
    //  分组 查邮箱列表
    handleGetEmailByGroupId(groupId) {
      getEmailByGroupId(groupId).then(res => {
        if (res.data.code === 0) {
          this.groupList = res.data.data
        }
      })
    },
    // 邮箱选中
    handleChangeUser(email, username) {
      if (!isEmail(email)) {
        this.$message.info('邮箱格式错误')
        return
      }
      if (this.userMailSelected.includes(email)) {
        const index = this.userMailSelected.indexOf(email)
        if (index === -1) return
        this.userMailSelected.splice(index, 1)
        this.userListsSelected = this.userListsSelected.filter(item => item.email !== email)
      } else {
        this.userMailSelected.push(email)
        this.userListsSelected.push({email, username})
      }
    },
    // 是否为email
    showEmail(email) {
      return isEmail(email)
    },
    handleSelect() {
      // const allUserList = this.allUserList.filter(item => item.checked)
      // const lists = []
      // allUserList.forEach(user => {
      //   lists.push({
      //     id: user.userId,
      //     username: user.username
      //   })
      // })
      // const data = {
      //   lists
      // }
      console.log(this.userMailSelected,"选中大是大非四代机沃尔夫")
      const list = this.userMailSelected
      this.$emit('selectUserSuccess', list, this.addType)
    },
    selectCancel() {
      this.$emit('selectCancel')
    },
    isUserSelected(mailUser) {
      return this.userMailSelected.includes(mailUser.email)
    },
    changeCollage(group) {
      // console.log(!group.childList)
      // 分组的
      if (group.type === 'grouped') {
        this.handleGetEmailByGroupId(group.id)
      } else if (group.type === 'unGrouped') {
        // 获取未分组的
        getEmailByUnGroup().then(unres => {
          if (unres.data.code === 0) {
            this.unGroupList = unres.data.data
          }
        })
      }
      if(!group.childList){
      this.handleGetDeptPage(group.id)
      }
    },
    // 添加分组 dialog
    handleShowAddGroupDialog() {
      this.addGroupDialog = true
    },
    handleCloseAddGroupDialog() {
      this.handleGetDeptTree()
      this.addGroupDialog = false
    }
  },
  watch: {
    // 公司的
    childActiveCollapse(newval, oldval) {
      if (newval !== oldval && typeof newval === 'number') {
        this.handleGetDeptPage(newval)
      }
    }
  }
}
</script>

<style scoped lang='scss'>
.select-container{
  display: flex;
  justify-content: space-between;
  .select-right,.select-left{
    width: 50%;
    margin: 0 20px 40px;
    padding-top: 10px;
  }
  .select-left{
    border-right: 1px solid #e4e7ed;
    margin-right: 0px;
    padding-right: 20px;
  }
  .selected-list-outer{
    background: #e9e9e959;
    margin-top: 15px;
    overflow: hidden
  }
}
</style>
