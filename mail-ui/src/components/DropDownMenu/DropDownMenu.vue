<template>
  <div class="nav">
    <!-- 一级 -->

    <div v-if="true" class="mailnavItem" v-for="(menuItem, index) in menu" :key="'menuItem' + index" @click="init">
      <span @click="changeMenu(menuItem)" tabindex="1">
        {{ menuItem.label }}
        <i class="el-icon-arrow-down" v-if="menuItem.menu && menuItem.menu.length"></i>
      </span>
      <!-- 二级 -->
      <div class="mailnavmenuItem" v-if="menuItem.menu && menuItem.menu.length">
        <div class="mailnavmenuItem-top" v-for="(item, index) in menuItem.menu" :key="'menuItemMenu' + index">
          <span :class="item.divided ? 'divided' : ''" @click="changeMenu(item, menuItem.value)">
            {{ item.label }}
          </span>
        </div>
      </div>
    </div>
    <div class="dropdown-menu">
      <el-menu default-active="1" active-text-color="#778099" text-color="#778099" menu-trigger="hover" mode="horizontal">
        <el-submenu index="1">
          <template slot="title">标记为</template>
          <el-menu-item index="2-1" @click="handleRead('0')">已读</el-menu-item>
          <el-menu-item index="2-2" @click="handleRead('1')">未读</el-menu-item>
          <el-submenu index="2-4" popper-class="submenu-box" :popper-append-to-body="false">
            <template slot="title">其他标签</template>
            <el-menu-item v-for="(item, key) in tagList" :key="key" :index="item.id" @click="handleBindTag(item)">
              <i class="iconfont mail-24gf-tags2" :style="{'color':item.tagColor}"></i>
              <span style="margin-left: 12px;">{{ item.tagName }}</span>
            </el-menu-item>
          </el-submenu>
          <el-menu-item index="2-2" @click="handleUnbindTag">取消标签</el-menu-item>
        </el-submenu>
        <el-submenu index="2">
          <template slot="title">移动到</template>
          <el-menu-item index="2-1" @click="handleMove(3)">已发送</el-menu-item>
          <el-menu-item index="2-2" @click="handleMove(4)">已删除</el-menu-item>
          <el-menu-item index="2-3" @click="handleMove(5)">垃圾邮件</el-menu-item>
          <el-submenu index="2-4" popper-class="submenu-box" :popper-append-to-body="false">
            <template slot="title">其他文件夹</template>
            <el-menu-item v-for="(item, key) in fileList" :key="key" :index="item.id" @click="handleMove(item.id)">
             <!-- <i class="iconfont mail-24gf-tags2" :style="{'color':item.tagColor}"></i> -->
              <svg t="1670481636269" class="icon" viewBox="0 0 1228 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8759" width="15" height="15"><path d="M1196.987733 212.5824v540.0576c0 39.594667-34.474667 71.3728-76.765866 71.3728H323.242667c-51.780267 0-88.746667-46.762667-73.250134-92.808533l126.737067-375.808H70.417067C31.675733 355.362133 0 326.4512 0 291.089067V98.372267C0 63.044267 31.675733 34.0992 70.417067 34.0992h378.811733c26.7264 0 51.029333 13.9264 63.010133 35.703467l39.048534 71.406933H1120.256c42.257067 0 76.8 32.119467 76.8 71.3728" fill="#5398DF" p-id="8760"></path><path d="M1128.721067 997.853867H68.266667a68.266667 68.266667 0 0 1-68.266667-68.266667V280.3712a68.266667 68.266667 0 0 1 68.266667-68.266667h1060.4544a68.266667 68.266667 0 0 1 68.266666 68.266667V929.5872a68.266667 68.266667 0 0 1-68.266666 68.266667" fill="#85BCFF" p-id="8761"></path></svg>
              <span style="margin-left: 12px;">{{ item.groupName }}</span>
            </el-menu-item>
          </el-submenu>
        </el-submenu>
      </el-menu>
    </div>
    <!-- <ul id="nav" style="margin-top: -4px;">
    <li>
      <a class="fly" @click="init" tabindex="1">
        <div class="cursor action-icon" slot="reference" style="width: 21px">
          <svg-icon icon-class="mail_more" style="width: 2em;height: 2em;margin-top: 12px;"></svg-icon>
        </div> -->
       <!-- <img class="action-icon"
            style="width: 21px"
            slot="reference"
            src="@/assets/img/mail/headerAction/menu_active.png" alt=""> -->
      <!-- </a>
      <ul class="dd"> -->
        <!-- 一级  -->
        <!-- <li v-for="(menuItem, index) in menu" :key="'menuItem' + index">
          <a class="flyRow" @click="changeMenu(menuItem)" tabindex="1">
            <span class="flex-center">
                <div class="cursor action-icon" slot="reference" style="width: 21px">
                  <svg-icon :icon-class="menuItem.icon" style="width: 17px;"></svg-icon>
                </div>
              {{ menuItem.label }}
            </span>
            <i class="el-icon-arrow-right" v-if="menuItem.menu && menuItem.menu.length"></i></a> -->
          <!-- 二级   -->
          <!-- <ul v-if="menuItem.menu && menuItem.menu.length">
            <li v-for="(item, index) in menuItem.menu" :key="'menuItemMenu' + index">
              <a :class="item.divided ? 'divided' : ''" @click="changeMenu(item, menuItem.value)" >{{ item.label }}</a>
            </li>
          </ul>
        </li>
      </ul>
    </li>
  </ul> -->
  </div>
</template>

<script>
  import { bj_dropDownOption, ydd_dropDownOption } from "@/const/const"
  import {bindTag, getQueryGroup, unbindTag} from "@/api/mail"

  export default {
    name: "DropDownMenu",
    props: {
      config: {
        type: Object,
        required: true
      },
      tagList: {
        type: Array,
        required: true
      },
      fileList: {
        type: Array,
        required: true
      },
      // 邮箱id
      mailConfigId: {
        type: String,
        required: true
      },
      // 邮件id
      mailDetailId: {
        type: String,
        required: true
      },
      entry: {
        type: String,
        required: true
      }
    },
    data() {
      return {
        defaultMenu: [
          { label: '删除', value: 'sc', icon: 'mail_sc' },
          { label: '彻底删除', value: 'cdsc', icon: 'mail_cdsc'},
          { label: '信任', value: 'xr', icon: 'mail_xr'},
          { label: '再次编辑', value: 'zcbj', icon: 'mail_zcbj'},
          // { label: '标记为', value: 'bjw', icon: 'mail_bjw',
          //   menu: []
          // },
          // { label: '移动到', value: 'ydd', icon: 'mail_ydd',
          //   menu: []
          // },
        ],
        menu: []
      }
    },
    async created(){
      await this.getFolderList()
    },
    async mounted() {
      await this.init()
    },
    methods: {
      // 绑定标签
      handleBindTag(obj) {
        bindTag(this.mailDetailId, obj.id).then(res => {
          if (res.data && res.data.code == 0) {
            this.$message.success('标记成功')
            this.$emit('getList')
          }
        })
      },
      // 标签解绑
      handleUnbindTag() {
        unbindTag(this.mailDetailId).then(res => {
          if (res.data && res.data.code == 0) {
            this.$message.success('取消标记成功')
            this.$emit('getList', '1', this.mailConfigId)
          }
        })
      },
      async init() {
        let menu = []
        let configKeys = Object.keys(this.config)
        const bjwMenu = this.get_bjwOptions()
        const yddMenu = await this.getFolderList()

        configKeys.forEach(key => {
          const menuChecked = this.defaultMenu.filter(m => m.value === key)
          if(!menuChecked || menuChecked.length === 0) {
          } else {
            if(key === 'ydd') {
              menuChecked[0].menu = yddMenu
            }
            if(key === 'bjw') {
              menuChecked[0].menu = bjwMenu
            }
            menu.push(menuChecked[0])
          }
        })
        this.menu = menu
      },
      // 已读/未读
      handleRead(tag) {
        this.$emit('handleRead', tag)
      },
      // 移动邮件
      handleMove(groupId) {
        this.$emit('handleMove', groupId)
      },
      // 菜单选中
      changeMenu(menu, parentAction = '') {
        let action = {
          action: '',
          value: '',
        }
        // 是子菜单，展开
        if(menu.menu && menu.menu.length) {
        } else {
          // 菜单选中
          // 二级菜单
          if(parentAction) {
            action = {
              action: parentAction,
              value: menu.value
            }
          } else {
            action = {
              action: menu.value,
            }
          }
          this.$emit('changeMenu', action)
        }
      },
      // 标记为
      get_bjwOptions() {
        // 草稿箱、已发送
        if(this.entry === 'inbox') {
          return bj_dropDownOption
        }
        return [
          { label: '红旗邮件', value: 2 },
          { label: '取消红旗', value: 3 }
        ]
      },
      // 获取文件夹列表
      getFolderList() {
        return new Promise((resolve, reject) => {
          const dirArr = []
          getQueryGroup().then(async res => {
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
              resolve(dropDownOption)
            }
          }).catch(e => {
            resolve([])
          })
        })
      },
      // 移动到 过滤
      yddDrownOptionFiler() {
        const entry = this.entry
        let option = []
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
              { label: '垃圾邮件', value: 5 }
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
    }
  }
</script>
<style lang="scss" scoped>
.el-submenu /deep/.el-menu{
  max-height: 300px;
  overflow-y: auto;
  .el-menu-item{
    font-size: 12px;
    &:hover{
      background-color: #EEEEEE;
    }
  }
}
.el-menu--horizontal .el-menu .el-menu-item, .el-menu--horizontal .el-submenu /deep/.el-submenu__title{
  font-family: MiSans-Demibold;
  font-size: 12px!important;
  &:hover{
    background-color: #EEEEEE;
  }
}
.nav{
  display:flex;
  align-items: center;
  margin-left: 10px;
  cursor: pointer;
  z-index: 1;
  padding: 15px 0px;
  .dropdown-menu{
    height: 30px;
    border: 1px solid #eee;
    overflow:hidden;
    /deep/.el-menu--horizontal>.el-submenu .el-submenu__title{
      height: 34px;
      line-height: 30px;
      color: #778099;
      font-size: 12px;
      border-right: 1px solid #eee;
      &:hover{
        border-bottom: none!important;
      }
    }
  }
  .mailnavItem{
    transition: color .3s;
    color: #778099;
    //width: 60px;
    height: 30px;
    padding: 0 15px;
    line-height: 30px;
    text-align: center;
    border: 1px solid #eee;
    overflow:hidden;
    &:hover {
      color: #3471FF;
      //overflow:visible;
    }
    .mailnavmenuItem{
      z-index: -1;
      margin:0;
      line-height: 30px;
      background-color: #fff;
      box-shadow: 0px 3px 10px 0px rgba(34, 34, 34, 0.1);
      .mailnavmenuItem-top{
        color: #778099;
        &:hover {
          background-color: #ecf5ff;
          color: #66b1ff;
        }
        .divided {
          border-top: 1px solid #eee;
          margin-top: 3px;
          padding-top: 3px;
        }
      }
    }
  }
}
  // /* main menu styles */
  // #nav,#nav ul {
  //   list-style:none;
  //   margin:0;
  //   padding:0;
  // }

  // #nav {
  //   position:relative;
  //   z-index:2;
  //   ul {
  //     background: #fff;
  //     left:-9999px;
  //     position:absolute;
  //     top:47px;
  //     width:auto;
  //     padding: 10px 0;
  //     border-radius: 4px;
  //     box-shadow: 0px 3px 10px 0px rgba(34, 34, 34, 0.1);
  //     ul {
  //       left:-9999px;
  //       position:absolute;
  //       top:0;
  //       width:auto;
  //     }
  //   }
  // }
  // #nav li {
  //   float:left;
  //   margin-right:5px;
  //   position:relative;
  //   a {
  //     color:#000;
  //     float:left;
  //     font-size:14px;
  //     padding: 0 14px;
  //     text-decoration:none;
  //   }
  // }

  // #nav > li > a {
  //   border-radius:6px;
  //   overflow:hidden;
  // }
  // #nav li a.fly {
  //   //background:#c1c1bf;
  //   padding: 0;
  // }
  // #nav ul li {
  //   margin:0;
  //   line-height: 30px;
  //   a {
  //     width: 125px;
  //   }
  //   a.divided {
  //     border-top: 1px solid #eee;
  //     margin-top: 3px;
  //     padding-top: 3px;
  //   }
  //   a.fly {
  //     padding-right:10px;
  //   }
  // }

  // /*hover styles*/
  // #nav li:hover > a {
  //   background-color: #ecf5ff;
  //   color: #66b1ff;
  // }

  // /*focus styles*/
  // #nav li a:focus {
  //   outline-width:0;
  // }

  // /*popups*/
  // #nav li a:active + ul.dd,#nav li a:focus + ul.dd,#nav li ul.dd:hover {
  //   left: -90px;
  // }
  // #nav ul.dd li a:active + ul,#nav ul.dd li a:focus + ul,#nav ul.dd li ul:hover {
  //   width: 125px;
  //   left: -125px;
  // }
  // .flyRow {
  //   display: flex;
  //   align-items: center;
  //   justify-content: space-between;
  //   img {
  //     width: 13px;
  //     margin-right: 7px;
  //   }
  // }
  // .action-icon {
  //   width: 14px;
  //   color: #778099;
  //   margin-right: 8px;
  //   &:hover {
  //    color: #3471FF;
  //   }
  // }
  // .flyRow {
  //   &:hover {
  //     .action-icon {
  //       color: #3471FF;
  //     }
  //   }
  // }
</style>
