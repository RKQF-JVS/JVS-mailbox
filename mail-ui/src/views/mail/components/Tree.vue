<!--
 * @Author: Xiuer
 * @Date: 2021-07-30
 * @Description: 折叠面板实现树形控件
-->
<template>
  <el-collapse v-model="childActiveCollapse" accordion>
    <el-collapse-item :name="item.id"
                      class="mail-collapse-head2"
                      v-for="(item, index) in groups"
                      :key="'item' + index">
      <template slot="title">
        <div class="mail-collapse1 ellipsis">{{item.cjsl}}{{ item.name }}</div>
      </template>
      <Tree  v-if="item.childList&&item.childList.length" :groups="item.childList" :mailUserList="mailUserList"></Tree>
    </el-collapse-item>
    <!-- 邮箱 -->
    <div class="mail-list-container" v-if="mailUserList.length">
      <div @click="handleChangeUser(mailUser.email, mailUser.username)"
            v-for="(mailUser, index) in mailUserList"
            :key="'mailUser' + index"
            class="mail-item">
        <div class="user-mail ellipsis">{{ mailUser.username || '' }} <{{ mailUser.email }}></div>
        <div class="user-active">
          <i class="el-icon-success user-checked" v-if="isUserSelected(mailUser)"></i>
          <i class="el-icon-success user-unchecked" v-else></i>
        </div>
      </div>
    </div>
  </el-collapse>
</template>
<script>
import { isEmail } from "@/util/validate"
import Tree from './Tree.vue'
export default {
  name:'Tree',
  components: {
    Tree
  },
  props:{
    groups:{
      type:Array,
      default:()=>{[]}
    },
    mailUserList:{
      type:Array,
      default:()=>{[]}
    }
  },
  data() {
    return {
      childActiveCollapse: '',
      unGroupActiveCollapse: '',
      userMailSelected: [],
      userListsSelected: [],
    }
  },
  methods:{
    handleChangeUser(email, username) {
      if (!isEmail(email)) {
        this.$message.info('邮箱格式错误')
        return
      }
      this.$emit('handleChangeUsers',email, username)
      console.log(this.userMailSelected.includes(email),email,"9999999999999992222222222222")
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
    isUserSelected(mailUser) {
      return this.userMailSelected.includes(mailUser.email)
    },
    // 是否为email
    showEmail(email) {
      return isEmail(email)
    },
  }
}
</script>