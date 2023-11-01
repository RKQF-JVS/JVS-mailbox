<template>
<!--  <div v-if="isMailLogin && isLoaded">-->
  <div v-if="true">
    <div class="topNav">
      <div class="totalName" @click="goHome">
        <img :src="logoImg" />
      </div>
      <div class='topNav-r'>
        <!-- <div class="setting-btn-container cursor flex-center">
          <div class="setting-btn" @click="handleMenuRouterClick(mailConfigId, 'setting')">
            <i class="el-icon-setting setting_icon"></i>
            邮箱设置
          </div>
        </div> -->
        <!-- 消息通知 -->
        <el-tooltip :visible-arrow="false" popper-class="tooltip-class" class="item" effect="dark" content="通知" placement="bottom">
          <el-popover
            placement="bottom"
            width="422"
            popper-class="my-popover"
            trigger="click"
            v-model="visible">
            <div class="right-search" slot="reference" @click="getMessage">
              <el-badge v-if="remainingCount > 0" :value="remainingCount" :max="99" class="item">
                <i class="iconfont mail-tongzhi" style="color:#7DBcF8;font-size: 18px;"></i>
              </el-badge>
              <i v-else class="iconfont mail-tongzhi" style="color:#7DBcF8;font-size: 18px;"></i>
            </div>
            <template>
              <div v-loading="loading">
                <div class="right-tab">
                  <div class="bottom-txt">
                    <div class="bottom-all" @click="allnotice">所有消息</div>
                  </div>
                  <div class="right-tabsize" :v-model="readIs" @click="editReadIs()">
                    <i class="el-icon-success" style="color:#5DCFFF;" :style="readIs ? 'color:#ccc;' : ''"></i>
                    <span style="font-size:14px;" class="right-tabhover"
                    :style="readIs ? 'color:#ccc;' : ''">全部标记为已读</span>
                  </div>
                </div>
                <el-divider></el-divider>
                <vue-scroll :ops="{bar: {background: '#cecece',onlyShowBarOnScroll:false,minSize:0.1}}" style="height:615px;">
                  <div class="top-tab"  v-for="(item,index) in insideList" :key="index"
                    @click="changeDetail(item.id,index,'insideList')" :style="(item.readIs || readIs) ? 'color:#ccc;' : ''">
                    <div style="display:flex;">
                      <img :src="renwu" alt="" v-if="item.clientCode === 'teamwork'" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;" />
                      <img :src="wendang" v-else-if="item.clientCode === 'knowledge'" alt="" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;"  />
                      <span class="top-tab-span">{{item.msgContent.title}}</span>
                      <span class="top-tab-span-span">{{item.createTime | formatLogTime}}</span>
                    </div>
                    <div class="top-txt">{{item.msgContent.content}}</div>
                    <el-divider></el-divider>
                    <div class="container-page" v-if="false">
                      <el-pagination
                        background
                        layout="prev, pager, next"
                        :page-size="10"
                        @current-change="handleCurrentChange"
                        :total="pageObj.total">
                      </el-pagination>
                    </div>
                  </div>
                  <el-empty v-if="insideList && insideList.length===0"></el-empty>
                </vue-scroll>
              </div>
            </template>
          </el-popover>
        </el-tooltip>
        <el-tooltip :visible-arrow="false" popper-class="tooltip-class" class="item" effect="dark" content="设置" placement="bottom">
          <div style="padding:0px 10px;margin-top: 2px;">
            <i class="iconfont mail-shezhi1" style="font-size:18px;cursor: pointer;color:#3da8f5;" @click="handleMenuRouterClick(mailConfigId, 'setting')"></i>
          </div>
        </el-tooltip>
        <el-popover
          placement="bottom"
          width="40"
          trigger="click">
          <div class="top-tool-bar">
            <p @click="goInfo('userManager')">
              <i class="el-icon-user-solid" style="padding:0px 10px;color: rgb(121 132 158);font-size: 16px;margin-left: 10px;" />
              <span>个人中心</span>
            </p>
            <!-- <p @click="goHelp">
              <i class="el-icon-question" style="padding:0px 10px;color: rgb(121 132 158);font-size: 16px;margin-left: 10px;"/>
              <span>帮助中心</span>
            </p> -->
            <!-- <p class="menu-item" v-if="changeTenantsList && changeTenantsList.length > 0" @click="switchTenant">
              <i class="el-icon-s-tools" style="padding:0px 10px;color: rgb(121 132 158);font-size: 16px;margin-left: 10px;"/>
              <span>切换租户</span>
            </p> -->
            <p @click="handleLogout">
              <img :src="outImg" style="padding:0px 10px;margin-left: 10px;"/>
              <span>退出登录</span>
            </p>
          </div>
          <p slot="reference" class="user-info-tool">
            <span style="margin-left:0;">{{userInfo.realName}}</span>
            <img :src="userInfo.headImg ? userInfo.headImg : userImg"/>
            <!-- <img :src="headImg"/> -->
          </p>
        </el-popover>
      </div>
    </div>
    <!-- 所有消息 -->
    <el-dialog
      title="所有消息"
      :visible.sync="connectVisible"
      append-to-body
      width="1090px"
      custom-class="customheight"
      :before-close="handleConnectClose">
      <div class="task-headers">
        <span class="head-title">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="所有" name="1">
              <vue-scroll :ops="{bar: {background: '#cecece',onlyShowBarOnScroll:false,minSize:0.1}}" style="height:600px;" v-loading="noListLoading">
                <div class="top-tab"  v-for="(item,index) in noList" :key="index" :style="(item.readIs || readIs) ? 'color:#ccc;' : ''">
                  <div style="display:flex;cursor: pointer;" @click="changeDetail(item.id,index,'noList')">
                    <img :src="renwu" alt="" v-if="item.clientCode === 'teamwork'" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;" />
                    <img :src="wendang" v-else-if="item.clientCode === 'knowledge'" alt="" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;"  />
                    <div style="display:flex;">
                      <span class="top-tab-span">{{item.msgContent.title}}</span>
                      <span class="top-tab-span-time">{{item.createTime}}</span>
                    </div>
                  </div>
                  <div class="top-txt">
                    {{item.msgContent.content}}
                    <i class="el-icon-delete top-tab-span-span" @click="delnotice(item.id,index,'noList')"></i>
                  </div>
                </div>
                <el-empty v-if="noList && noList.length===0"></el-empty>
              </vue-scroll>
            </el-tab-pane>
            <el-tab-pane label="计划" name="2">
              <vue-scroll :ops="{bar: {background: '#cecece',onlyShowBarOnScroll:false,minSize:0.1}}" style="height:600px;" v-loading="noListLoading">
                <div class="top-tab"  v-for="(item,index) in noList" :key="index"
                :style="(item.readIs || readIs) ? 'color:#ccc;' : ''" v-if="item.clientCode === 'teamwork'">
                  <div style="display:flex;cursor: pointer;" @click="changeDetail(item.id,index,'noList')">
                    <img :src="renwu" alt="" v-if="item.clientCode === 'teamwork'" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;" />
                    <div style="display:flex;">
                      <span class="top-tab-span">{{item.msgContent.title}}</span>
                      <span class="top-tab-span-time">{{item.createTime}}</span>
                    </div>
                  </div>
                  <div class="top-txt">
                    {{item.msgContent.content}}
                    <i class="el-icon-delete top-tab-span-span" @click="delnotice(item.id,index,'noList')"></i>
                  </div>
                </div>
                <el-empty v-if="noList && noList.length===0"></el-empty>
              </vue-scroll>
            </el-tab-pane>
            <el-tab-pane label="文档" name="3">
              <vue-scroll :ops="{bar: {background: '#cecece',onlyShowBarOnScroll:false,minSize:0.1}}" style="height:600px;" v-loading="noListLoading">
                <div class="top-tab"  v-for="(item,index) in noList" :key="index"
                :style="(item.readIs || readIs) ? 'color:#ccc;' : ''" v-if="item.clientCode === 'knowledge'">
                  <div style="display:flex;cursor: pointer;" @click="changeDetail(item.id,index,'noList')">
                    <img :src="wendang" v-if="item.clientCode === 'knowledge'" alt="" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;"  />
                    <div style="display:flex;">
                      <span class="top-tab-span">{{item.msgContent.title}}</span>
                      <span class="top-tab-span-time">{{item.createTime}}</span>
                    </div>
                  </div>
                  <div class="top-txt">
                    {{item.msgContent.content}}
                    <i class="el-icon-delete top-tab-span-span" @click="delnotice(item.id,index,'noList')"></i>
                  </div>
                </div>
                <el-empty v-if="noList && noList.length===0"></el-empty>
              </vue-scroll>
            </el-tab-pane>
            <el-tab-pane label="低代码" name="4">
              <vue-scroll :ops="{bar: {background: '#cecece',onlyShowBarOnScroll:false,minSize:0.1}}" style="height:600px;" v-loading="noListLoading">
                <div class="top-tab"  v-for="(item,index) in noList" :key="index"
                :style="(item.readIs || readIs) ? 'color:#ccc;' : ''" v-if="item.clientCode === 'frame'">
                  <div style="display:flex;cursor: pointer;" @click="changeDetail(item.id,index,'noList')">
                    <img :src="wendang" v-if="item.clientCode === 'frame'" alt="" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;"  />
                    <div style="display:flex;">
                      <span class="top-tab-span">{{item.msgContent.title}}</span>
                      <span class="top-tab-span-time">{{item.createTime}}</span>
                    </div>
                  </div>
                  <div class="top-txt">
                    {{item.msgContent.content}}
                    <i class="el-icon-delete top-tab-span-span" @click="delnotice(item.id,index,'noList')"></i>
                  </div>
                </div>
                <el-empty v-if="noList && noList.length===0"></el-empty>
              </vue-scroll>
            </el-tab-pane>
            <el-tab-pane label="规则" name="5">
              <vue-scroll :ops="{bar: {background: '#cecece',onlyShowBarOnScroll:false,minSize:0.1}}" style="height:600px;" v-loading="noListLoading">
                <div class="top-tab"  v-for="(item,index) in noList" :key="index"
                :style="(item.readIs || readIs) ? 'color:#ccc;' : ''" v-if="item.clientCode === 'risk-policy'">
                  <div style="display:flex;cursor: pointer;" @click="changeDetail(item.id,index,'noList')">
                    <img :src="wendang" v-if="item.clientCode === 'risk-policy'" alt="" style="width: 20px;height: 20px;margin-left: -36px;padding: 0px 16px 0px 0px;"  />
                    <div style="display:flex;">
                      <span class="top-tab-span">{{item.msgContent.title}}</span>
                      <span class="top-tab-span-time">{{item.createTime}}</span>
                    </div>
                  </div>
                  <div class="top-txt">
                    {{item.msgContent.content}}
                    <i class="el-icon-delete top-tab-span-span" @click="delnotice(item.id,index,'noList')"></i>
                  </div>
                </div>
                <el-empty v-if="noList && noList.length===0"></el-empty>
              </vue-scroll>
            </el-tab-pane>
          </el-tabs>
        </span>
        <div class="right-tab">
          <div class="right-tabsize" :v-model="readIs" @click="editReadIs()">
            <i class="el-icon-success" style="color:#5DCFFF;" :style="readIs ? 'color:#ccc;' : ''"></i>
            <span style="font-size:14px;" class="right-tabhover"
            :style="readIs ? 'color:#ccc;' : ''">全部标记为已读</span>
          </div>
          <div style="position: relative;left: 15px;border-left: 1px solid #ccc;">
            <el-select v-model="tabActive" class="select-options" @change="allnotice" size="mini">
              <el-option v-for="(item,index) in mesOption"
                :key="index"
                :label="item.label"
                :value="item.name">
              </el-option>
            </el-select>
          </div>
          <div style="position: relative;left: 15px;border-left: 1px solid #ccc;">
          <!-- @change="delnoticeall" -->
            <el-select v-model="delActive" class="select-options" size="mini">
              <el-option v-for="(item,index) in mesdelOption"
                :key="index"
                :label="item.label"
                :value="item.name"
                @click.native="delnoticeall">
              </el-option>
            </el-select>
          </div>
        </div>
        <span class="header-actions text-right">
          <el-tooltip placement="bottom" :visible-arrow="false" popper-class="tooltip-class">
            <template slot="content">
              <span>关闭面板</span>
            </template>
            <a class="action-item muted" @click="handleConnectClose"><i class="el-icon-close"/></a>
          </el-tooltip>
        </span>
      </div>
    </el-dialog>
    <!-- 消息详情 -->
    <el-dialog
      title="消息详情"
      :visible.sync="connectVisibles"
      append-to-body
      width="1090px"
      custom-class="customheight"
      :before-close="handleConnectCloses">
      <div class="task-headers" style="border-bottom: 1px solid #e5e5e5;">
        <span class="head-title">
          <span style=" display: flex; align-items: center;" @click="backStep">
              <span class="muted head-style iconfont icon-jiantouzuo">返回</span>
          </span>
        </span>
        <span class="head-del">
          <el-tooltip placement="bottom" :visible-arrow="false" popper-class="tooltip-class">
            <template slot="content">
              <span>删除消息</span>
            </template>
            <i class="el-icon-delete top-tab-span-span" @click="delnoDetailList(noDetailList.id,noDetailList.index)"></i>
          </el-tooltip>
        </span>
        <span class="header-actions text-right">
          <el-tooltip placement="bottom" :visible-arrow="false" popper-class="tooltip-class">
            <template slot="content">
              <span>关闭面板</span>
            </template>
            <a class="action-item muted" @click="handleConnectCloses"><i class="el-icon-close"/></a>
          </el-tooltip>
        </span>
      </div>
      <vue-scroll v-loading="noDetailLoading" :ops="{bar: {background: '#cecece',onlyShowBarOnScroll:false,minSize:0.1}}" style="height:600px;">
        <div class="top-tabs">
          <div class="top-tabs-pad">
            <span>消息标题：{{noDetailList.title}}</span>
          </div>
          <div class="top-tabs-pad">
            <span>消息来源：{{noDetailList.clientName}}</span>
          </div>
          <div class="top-tabs-pad">
            <span>消息时间：{{noDetailList.createTime}}</span>
          </div>
          <div class="top-tabs-pad">
            <span>消息内容：{{noDetailList.content}}</span>
          </div>
        </div>
      </vue-scroll>
    </el-dialog>
    <div class="main-view-container">
      <!-- 邮箱列表 -->
      <div class="left-container noScroll">
        <!-- style="height: calc(100vh - 80px);" -->
        <el-scrollbar class="noScroll">
          <div v-for="(item, index) in userMailList" :key="'userMailList' + index" class="user-mail-Collapse">
            <div @click="handleChangeMail(item.id)" class="user-mail-Collapse-head cursor">
              <i :class="isCollapseActive(item.id) ? 'user-mail-Collapse-icon el-icon-caret-bottom' : 'user-mail-Collapse-icon el-icon-caret-right'" style=""></i>
              <img :src="getImg(item.mailType).src" :style="`height: ${getImg(item.mailType).h};margin: 0 6px;`" alt="">
              <span class="user-mail-text ellipsis">{{ item.username }}</span>
            </div>
            <el-collapse-transition>
              <div v-show="isCollapseActive(item.id, item.username)">
                <!--  flex-space-around -->
                <div class="header-action cursor">
                  <div class="header-action-b">
                    <!-- <div @click="handleSyncMail(item.id, item.username)" class="header-action-item1 cursor flex-center">
                      <img src="@/assets/img/icon/mail/getmail.png" class="mail-header-action-icon" alt="" style="width: 16px;">
                      <span>收取</span>
                    </div> -->
                    <div @click="handleToWriteMail(item.id)" class="header-action-item2 cursor flex-center">
                      <img src="@/assets/img/icon/mail/writeMail.png" class="mail-header-action-icon" alt="" style="width: 14px;">
                      <span>写邮件</span>
                    </div>
                  </div>
                </div>
                <div :class="index === userMailList.length - 1 ? 'menu-outer user-mail-Collapse-last' : 'menu-outer'">
                  <!-- 邮箱项目 -->
                  <div style="text-align: left;padding: 10px 0px 10px 20px;color: #A0A1A1;font-size: 16px;">常用功能</div>
                  <li @click="handleMenuRouterClick(item.id, router.path, router.groupId,item.username)"
                      role="menuitem"
                      tabindex="-1"
                      :class="['el-menu-item el-menu-item-li', isCurrentRoute(activeName, router, activeId, item) ? 'el-menu-item-li_active menu_item_border_active' : 'menu_item_border' ]"
                      v-for="(router, index) in routerLists"
                      :key="'routerlist' + index">
                    <div :class="['flex-center', isCurrentRoute(activeName, router, activeId, item) ? 'menu-item-active' : '']">
                    <span :class="['menu_item_icon_container flex-center', isCurrentRoute(activeName, router, activeId, item)]">
                    <!-- <span :class="['menu_item_icon_container flex-center', isCurrentRoute(activeName, router, activeId, item) ? 'menu_item_icon_container_active' : '']"> -->
                      <img :style="{width: `${isCurrentRoute(activeName, router, activeId, item) ? 14 : 15}px`}" :src="isCurrentRoute(activeName, router, activeId, item) ? router.activeIcon : router.icon" alt="">
                    </span>
                      <span class="menu-text">{{ router.title }} {{ router.show && item.unreadCount > 0 ? `（${item.unreadCount}）` : `` }}</span>
                    </div>
                    <div class="flex-center" v-if="isPulling && router.path == 'inbox'">
                      <span style="font-size: 12px;">拉取中</span>
                      <div class="pull-icon">
                        <i class="iconfont mail-gengxin "></i>
                      </div>
                    </div>
                  </li>
                  <!-- 新建文件夹 -->
                  <div role="menuitem"
                      tabindex="-1"
                      :key="'routerlistnew'"
                      style="padding: 10px 20px;color: #A0A1A1;font-size: 16px;display: flex;justify-content: space-between;">
                      邮件文件夹
                      <i class="el-icon-plus" @click="handleOpenDialog(item.id)"></i>
                  </div>
                  <!-- 文件夹列表  -->
                  <li role="menuitem"
                      tabindex="-1"
                      :class="['el-menu-item el-menu-item-li', directoryActiveId === router.id ? 'el-menu-item-li_active menu_item_border_active' : 'menu_item_border' ]"
                      v-for="(router) in item.directoryList"
                      :key="'routerlist' + item.id + router.id"
                      style="padding-right: 8px;">
                    <div :class="['flex-center', directoryActiveId === router.id ? 'menu-item-active' : '']">
                    <span @click="handleDirRouterClick(item.id, router)" :class="['menu_item_icon_container flex-center', directoryActiveId === router.id]">
                      <!-- :class="['menu_item_icon_container flex-center', directoryActiveId === router.id ? 'menu_item_icon_container_active' : '']"> -->
                      <!-- <img :style="{width: `${directoryActiveId === router.id ? 14 : 15}px`}" :src="directoryActiveId === router.id ? dirIcon.activeIcon : dirIcon.icon" alt=""> -->
                      <svg t="1670481636269" class="icon" viewBox="0 0 1228 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8759" width="200" height="200" :style="{width: `${directoryActiveId === router.id ? 14 : 15}px`}">
                        <path d="M1196.987733 212.5824v540.0576c0 39.594667-34.474667 71.3728-76.765866 71.3728H323.242667c-51.780267 0-88.746667-46.762667-73.250134-92.808533l126.737067-375.808H70.417067C31.675733 355.362133 0 326.4512 0 291.089067V98.372267C0 63.044267 31.675733 34.0992 70.417067 34.0992h378.811733c26.7264 0 51.029333 13.9264 63.010133 35.703467l39.048534 71.406933H1120.256c42.257067 0 76.8 32.119467 76.8 71.3728" fill="#5398DF" p-id="8760"></path>
                        <path d="M1128.721067 997.853867H68.266667a68.266667 68.266667 0 0 1-68.266667-68.266667V280.3712a68.266667 68.266667 0 0 1 68.266667-68.266667h1060.4544a68.266667 68.266667 0 0 1 68.266666 68.266667V929.5872a68.266667 68.266667 0 0 1-68.266666 68.266667" fill="#85BCFF" p-id="8761"></path>
                      </svg>
                    </span>
                      <span @click="handleDirRouterClick(item.id, router)"  class="menu-text mail-menu-text ellipsis">{{ router.title }}</span>
                      <!-- <span class="menu-del-action" @click="handleDelFolder(item.id, router.id, router.title)">删除</span> -->
                      <el-popover
                        popper-class="aciton-popover"
                        placement="right-start"
                        width="50"
                        trigger="hover"
                        class="menu-del-action">
                        <div class="actions-box">
                          <div class="actions-item" @click="handleEditFolder(item.id,router.id,router.title)">
                            <i class="el-icon-edit"></i>修改</div>
                          <div class="actions-item" @click="handleDelFolder(item.id, router.id, router.title)">
                            <i class="el-icon-delete"></i>删除
                          </div>
                        </div>
                        <i class="iconfont mail-gengduo" slot="reference"></i>
                      </el-popover>
                    </div>
                  </li>
                  <!-- 新建文件夹 -->
                  <!-- <li role="menuitem"
                      tabindex="-1"
                      :class="['el-menu-item el-menu-item-li', 'menu_item_border' ]"
                      :key="'routerlistnew'"
                      style="padding-right: 8px;"
                      @click="handleOpenDialog(item.id)">
                    <div :class="['flex-center']">
                    <span :class="['menu_item_icon_container flex-center']">
                      <img :src="dirIcon.icon" alt="" style="width: 14px;">
                    </span>
                      <span class="menu-text mail-menu-text ellipsis">新建文件夹</span>
                      <i class="el-icon-plus"></i>
                    </div>
                  </li> -->
                  <!-- <div @click="handleOpenDialog(item.id)" class="add_new_btn flex-center" style="margin-top: 16px;margin-bottom: 10px;">
                    <img src="@/assets/img/icon/mail/mailDir.png" class="file_add_img" alt="">
                    <span>新建</span>
                  </div> -->
                  <!-- 标签 -->
                  <div role="menuitem"
                       tabindex="-1"
                       :key="'routerlistnews'"
                       style="padding: 10px 20px;color: #A0A1A1;font-size: 16px;display: flex;justify-content: space-between;"
                       >
                        标签
                      <i class="el-icon-plus" @click="handlelabelialog(item.id)"></i>
                  </div>
                  <!-- 标签列表 -->
                  <li role="menuitem"
                      tabindex="-1"
                      :class="['el-menu-item el-menu-item-li', directoryActiveId === router.id ? 'el-menu-item-li_active menu_item_border_active' : 'menu_item_border' ]"
                      v-for="(router) in item.tagAllData"
                      :key="'routerlist' + item.id + router.id"
                      style="padding-right: 8px;">
                    <div :class="['flex-center', directoryActiveId === router.id ? 'menu-item-active' : '']">
                    <div style="width: 28px;text-align: center;" @click="handleDirRouterClick(item.id, router)">
                          <!-- :class="['menu_item_icon_container flex-center', directoryActiveId === router.id ? 'menu_item_icon_container_active' : '']"> -->
                      <!-- <img :style="{width: `${directoryActiveId === router.id ? 14 : 15}px`}"
                          :src="directoryActiveId === router.id ? dirIcon.activeIcon : dirIcon.icon" alt=""> -->
                          <i class="iconfont mail-24gf-tags2" :style="{'color':router.tagColor}"></i>
                    </div>
                      <span @click="handleDirRouterClick(item.id, router)"  class="menu-text mail-menu-text ellipsis">{{ router.title }}</span>
                      <!-- <span class="menu-del-action" @click="handleDelFolder(item.id, router.id, router.title)">删除</span> -->
                      <el-popover
                        popper-class="aciton-popover"
                        placement="right-start"
                        width="50"
                        trigger="hover"
                        class="menu-del-action">
                        <div class="actions-box">
                          <div class="actions-item" @click="dotagAction('edit',index,router,item)">
                            <i class="el-icon-edit"></i>修改</div>
                          <div class="actions-item" @click="dotagAction('del',index,router,item)">
                            <i class="el-icon-delete"></i>删除
                          </div>
                        </div>
                        <i class="iconfont mail-gengduo" slot="reference"></i>
                      </el-popover>
                    </div>
                  </li>
                </div>
              </div>
            </el-collapse-transition>
          </div>
        </el-scrollbar>
      </div>
      <!--     时间列表   -->
      <div class="mailTimeListContainer" v-if="activeName !== 'setting' && isMailList && !isWright">
        <!-- <div class="mailSearchContainer">
          <div class="mailSearchInput__out">
            <div class="mailSearchInput__prepend flex-center"><i class="el-icon-search"></i></div>
            <input class="mailSearchInput"
                  @input="handleSearch"
                  v-model="searchSubject" type="text">
          </div>
          <div class="mailFlagIcon flex-center">
            <div :class="['cursor icon-mail-flag', stress === 1 ? 'm-header-flag-icon-active' : 'm-header-flag-icon']" @click="handleFilterStress">
              <svg-icon :icon-class="stress === 1 ? 'youjian' : 'youjian3'" style="width: 1.2em;height: 1.2em;"></svg-icon>
            </div>
          </div>
        </div> -->
        <div class="mailListRow">
          <div class="emptyTimeMail timeMailScroll flex-center" v-if="mailList.length === 0">
            <span class="emptyTimeMailText">无内容</span>
          </div>
          <el-scrollbar class="noScroll timeMailScroll" v-else>
            <el-collapse>
              <el-collapse-item class="mailListRowItem" v-for="(row, index) in mailList" :key="'row' + index">
              <!-- <div class="mailListRowItem" v-for="(row, index) in mailList" :key="'row' + index"> -->
                <!-- 时间段 -->
                <template slot="title">
                <div class="mailRowTitle">
                  <div class="mailRowSubjectText ellipsis">
<!--                    <span>{{ row.title }}（{{row.list.length}}封）</span>-->
                    <span>{{ row.title }}</span>
                  </div>
                </div>
                <div class="mailReadNumber flex-center">{{row.noReadNum}}/{{row.list.length}}</div>
              </template>
                <!-- 列表 -->
                <div v-for="(item, index2) in row.list"
                    :class="['mailRowInner cursor', activeMailDetailId === item.id ? 'active' : '']"
                    @click="changeRoute({routerName: 'mailDetail', id: item.id})"
                    :key="'list' + item + index2">
                  <div class="mailRowInnerOne">
                    <div :class="['mailRowInnerTitle ellipsis', item.rend ? 'hasRead-mail-highLight-row' : '']">
<!--                    <div :class="['mailRowInnerTitle ellipsis', item.rend && showUnReadHightLight ? 'hasRead-mail-highLight-row' : '']">-->
                      <div class="cursor icon-mail-flag"
                          style="margin-right: 5px;"
                          @click.stop="handleFlag(item.id, item.stress === 0 ? 2 : 3)">
                        <div :class="item.stress === 1 ? 'm-header-flag-icon-active' : 'm-header-flag-icon'">
                          <svg-icon :icon-class="item.stress === 1 ? 'youjian' : 'youjian3'"></svg-icon>
                        </div>
                      </div>
                      <span :style="item.sysMailTag && item.sysMailTag.tagColor ? `color: ${item.sysMailTag.tagColor}` : ''" style="font-size: 14px;font-weight: bold;" :class="['ellipsis']">{{ item.from || '未填写' }}</span>
<!--                      <span style="font-size: 14px;font-weight: bold;" :class="['ellipsis', item.rend && showHightLight ? 'unRead-mail-highLight-row' : '']">{{ item.from || '未填写' }}</span>-->
                    </div>
                    <!-- 暂时屏蔽日期！！！ -->
                    <!-- <div v-if="false" class="mainRowDate">{{ item.sentDate | renderCloDate }}</div> -->
                    <div class="mainRowDate">{{ item.createTime | formatLogTime }}</div>
                  </div>
                  <div class="mailRowSubject ellipsis">
                    <span :style="item.sysMailTag && item.sysMailTag.tagColor ? `color: ${item.sysMailTag.tagColor}` : ''" class="mailRowSubjectText ellipsis"><i class="el-icon-info warningColor" style="margin-right: 5px;font-size: 14px;" v-show="showWarningIcon && item.urgent === 1"></i>{{ item.subject }}</span>
                  </div>
                </div>
              <!-- </div> -->
              </el-collapse-item>
            </el-collapse>
          </el-scrollbar>
        </div>
      </div>
      <!-- 路由  -->
      <div class="right-container mail-container">
        <!--邮件详情-->
        <MailDetail v-if="detailMode && activeMailDetailId && routeParams.id"
                    :detailEntry="detailEntry"
                    :mailConfigId="activeId"
                    :mailDetailId="activeMailDetailId"
                    @changeMailTotal="changeMailTotal"
                    @getList="getList"
                    @refresh="refreshCb"
                    :key="detailEntry + activeMailDetailId"
                    :params="routeParams"
                    @changeRoute="changeRoute"></MailDetail>
        <MailEmpty v-else-if="detailMode && activeMailDetailId === ''"></MailEmpty>
        <div v-else>
          <!--收件箱-->
          <!--        <Inbox ref="inbox" @changeRoute="changeRoute" @changeMailTotal="changeMailTotal" v-if="activeName === 'inbox'" :key="'inbox' + activeId"></Inbox>-->
          <!--        &lt;!&ndash;写邮件&ndash;&gt;-->
          <WriteMail ref="writeMail" @changeRoute="changeRoute" :params="routeParams" v-if="showWriteMail" :key="'writeMail' + activeId"></WriteMail>
          <!--        &lt;!&ndash;草稿箱&ndash;&gt;-->
          <!--        <Drafts @changeRoute="changeRoute" v-if="activeName === 'drafts'" :key="'drafts' + activeId"></Drafts>-->
          <!--        &lt;!&ndash;已发送&ndash;&gt;-->
          <!--        <SentMail @changeRoute="changeRoute" v-if="activeName === 'sentMail'" :key="'sentMail' + activeId"></SentMail>-->
          <!--        &lt;!&ndash;已删除&ndash;&gt;-->
          <!--        <DeletedMail @changeRoute="changeRoute" v-if="activeName === 'deletedMail'" :key="'deletedMail' + activeId"></DeletedMail>-->
          <!--        &lt;!&ndash;垃圾箱&ndash;&gt;-->
          <!--        <SpamMail @changeRoute="changeRoute" v-if="activeName === 'spamMail'" :key="'spamMail' + activeId"></SpamMail>-->
          <!--        &lt;!&ndash;文件夹&ndash;&gt;-->
          <!--        <Directory @changeRoute="changeRoute" :directoryRouterParams="directoryRouterParams" v-if="activeName === 'directory'" :key="'directory' + activeId"></Directory>-->
          <!--设置-->
          <Setting @changeRoute="changeRoute" v-if="activeName === 'setting'"></Setting>
        </div>
      </div>
    </div>
    <!-- 新建文件夹弹框  -->
    <el-dialog append-to-body
               :visible.sync='folderDialog'
               :before-close="handleFolderDialogCancel"
               :close-on-click-modal="false">
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">{{ folderTitle }}</span>
      </div>
      <div class="flex-column-center">
        <!-- <img src="@/assets/img/mail/folder.png" class="drafts-img" alt=""> -->
        <div class="flex-align-center" style="margin-top: 10px;">
          <div class="item-label" style="width: 100px;">文件名称：</div>
          <el-input v-model="folderName"
            maxlength="10"
            show-word-limit
            clearable
            placeholder="请输入文件名称"></el-input>
        </div>
      </div>
      <div class="flex-center" style="margin-top: 50px;">
        <div @click="handleFolderSubmit" class="mail-btn flex-center mail-btn-primary">提交</div>
      </div>
    </el-dialog>
    <!-- 拉取邮件进度dialog  -->
    <el-dialog append-to-body
               class="syncDialog"
               width="400px"
               :visible.sync='syncDialog'>
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title ellipsis">{{syncMailName}}</span>
      </div>
      <div class="syncContainer">
        <div class="syncIconOuter flex-center">
          <img src="@/assets/img/mail/syncicon.png" class="syncIcon" alt="">
        </div>
        <div style="padding: 0 15px;width: 100%;">
          <div style="margin-bottom: 10px;">正在拉取邮件({{ syncCurrent }}/{{ syncTotal }})</div>
          <el-progress :percentage="syncPercent" v-if="syncPercent > 0"></el-progress>
        </div>
      </div>
    </el-dialog>
    <!-- 标签弹框  -->
    <el-dialog append-to-body
               :visible.sync='tagDialog'
               :before-close="handletagDialogCancel"
               :close-on-click-modal="false">
      <div slot="title" class="dialog-header-row">
        <div class="dialog-tip"></div>
        <span class="el-dialog__title">标签</span>
      </div>
      <div class="flex-column-center">
        <!-- <img src="@/assets/img/mail/folder.png" class="drafts-img" alt=""> -->
        <div class="flex-align-center" style="margin-top: 10px;">
          <div class="item-label" style="width: 100px;">标签名称：</div>
          <el-input v-model="tagName"
            maxlength="10"
            show-word-limit
            clearable
            placeholder="请输入标签名称"></el-input>
        </div>
        <div class="tag-badge">
          <span class="badge-item" v-for="badge in badgeList" :key="badge">
            <div class="badge-item-dot" :style="{'background-color':badge}" @click="tagColor=badge">
              <i class="el-icon-check" v-show="badge === tagColor"/>
            </div>
          </span>
        </div>
      </div>
      <div class="flex-center" style="margin-top: 40px;">
<!--        <div @click="handletagSubmit" class="mail-btn flex-center mail-btn-primary">立即创建</div>-->
        <div @click="handletagSubmit" class="mail-btn flex-center mail-btn-primary">提交</div>
      </div>
    </el-dialog>
  </div>
  <!-- 登录邮箱 -->
  <MailLogin @success="handleCheckIsFirstLogin" v-else-if="userMailList.length === 0 && isUserMailListLoaded"></MailLogin>
  <div :style="`background-image: url(${bgImg}); height: 100%; background-size: 100% 100%`" v-else>
    <img class="login-logo-picture" :src="logoV2Pic" alt="">
  </div>
</template>

<script>
  import headImg from "@/const/img/headImg.png"
  import wendang from "@/const/img/文档.png"
  import renwu from "@/const/img/任务.png"
  import { relativelyTime } from '@/util/date'
  import {
  noticeAll,
  noticePage,
  noticeList,
  noticeDetail,
  noticeDel,
  noticeDelone
  } from '@/api/acom.js'
  import WriteMail from "@/views/mail/writeMail"
  // import Directory from "@/views/mail/directory"
  import MailDetail from "@/views/mail/mailDetail"
  import Setting from "@/views/mail/setting"
  import MailLogin from "@/views/mail/mailLogin"
  import MailEmpty from "@/views/mail/mailEmpty"
  import store from "@/store";
  import {
    getQueryGroup,
    addGroup,
    delGroup,
    getSenderMail,
    isFirstLogin,
    getMailConfigList,
    queryUnreadCount,
    getMailList,
    postTagMail, editGroup
  } from "@/api/mail"
  import { loginoutHandle } from '@/api/login'
  import { hasBlank } from "@/util/validate"
  import { mapState } from "vuex"
  import EventBus from '@/eventBus'
  import { getStore } from "@/util/store.js";
  import {getDomain} from '@/api/login'
  import out from '@/views/common/img/out.png'
  import logo from '@/views/common/img/logo.png'
  import {
    getTagmailAll,
    tagmailSave,
    tagmailDel,
    tagmailEdit,
    tagbindingMail
  } from "@/api/tag"
  export default {
    name: "mailLayout",
    components: {
      // Inbox,
      // Drafts,
      // SentMail,
      // DeletedMail,
      // SpamMail,
      WriteMail,
      MailDetail,
      Setting,
      MailLogin,
      MailEmpty,
    },
    data() {
      return {
        headImg:headImg,
        // logoImg: logo,
        logo: logo,
        insideList:[],
        loading:false,
        readIs:false,
        tabActive:'1',
        mesOption:[{
          name:"1",
          label:"全部消息"
        },{
          name:"2",
          label:"未读消息"
        }],
        delActive:'all',
        mesdelOption:[{
          name:"all",
          label:"消息删除"
        },{
          name:"readIs",
          label:"删除已读"
        }],
        pageObj:{
          current:1,
          size:10,
          total:0
        },
        connectVisible: false, // 所有消息弹窗标识
        connectVisibles: false, // 消息详情弹窗标识
        noDetailLoading: false, // 消息详情弹窗loading
        noListLoading: false, // 所有消息弹窗loading
        noList:[],
        noDetailList:{},
        noDetail:{},
        value:'',
        visible:false,
        activeName: '1',
        all:false,
        wendang:wendang,
        renwu:renwu,
        outImg: out,
        // 是否登录邮箱
        isMailLogin: false,
        isUserMailListLoaded: false,
        //是否已绑定邮箱
        isLoaded: false,
        activeName: 'inbox',
        folderDialog: false,
        folderTitle: '',
        currentFolderId: '',
        folderType: '',
        routerLists: [
          { path: 'inbox', groupId: '1', title: '收件箱', show: true, icon: require('@/assets/img/icon/mail/收件箱.png'), activeIcon: require('@/assets/img/icon/mail/收件箱.png') },
          { path: 'drafts', groupId: '2', title: '草稿箱', show: false, icon: require('@/assets/img/icon/mail/草稿箱.png'), activeIcon: require('@/assets/img/icon/mail/草稿箱.png') },
          { path: 'sentMail', groupId: '3', title: '已发送', show: false, icon: require('@/assets/img/icon/mail/发送.png'), activeIcon: require('@/assets/img/icon/mail/发送.png') },
          { path: 'deletedMail', groupId: '4', title: '已删除', show: false, icon: require('@/assets/img/icon/mail/删除.png'), activeIcon: require('@/assets/img/icon/mail/删除.png') },
          { path: 'spamMail', groupId: '5', title: '垃圾邮件', show: false, icon: require('@/assets/img/icon/mail/警告.png'), activeIcon: require('@/assets/img/icon/mail/警告.png') },
          // { path: 'mailDetail', title: '邮件详情', count: 0, show: false, icon: require('@/assets/img/icon/myFollow.png'), activeIcon: require('@/assets/img/icon/myFollow_active.png') }
        ],
        directoryActiveId: '',
        directoryList: new Map(),
        activeId: '', // 活动id，一点击就直接赋值
        selectId: '',
        detailMode: true,
        detailEntry: '',
        folderName: '',
        routeParams: {}, // 路由参数
        directoryRouterParams: {}, // 文件夹路由参数
        userMailList: [],
        userMailListDropDownList: [],
        activeUserMail: '',
        collapseActive: [], // 活动collapse
        // dirIcon: {
        //   icon: require('@/assets/img/icon/mail/dir.png'),
        //   activeIcon: require('@/assets/img/icon/mail/dir_active.png'),
        // },
        stress: 0,
        searchSubject: '',
        mailList: [],
        activeMailDetailId: '', // 详情activeId
        showWriteMail: false,
        groupId: '',  // 分组id
        syncDialog: false,
        syncMailName: '',
        syncPercent: 0,
        syncTotal: 0,
        syncCurrent: 0,
        bgImg: '',
        logoV2Pic: '',
        userInfo: {},
        tagDialog:false,
        badgeList: ['#56ABFB', '#5BE2EE', '#A9E072', '#73D897', '#868AF6', '#D287F8', '#FB7894', '#FF9F73'],
        currentBadge:'#56ABFB',
        tagName:'',
        tagColor:'',
        tagId: '',
        tagAllData: new Map(),
        tagType:'add',
        isMailList:true, //写邮件 列表显示隐藏
        logo: logo,
        isPulling: false,
        isWright: false, // 是否写邮件
      }
    },
    created() {
      let headList = document.getElementsByTagName('head')[0].children
      for(let i in headList) {
        if(headList[i].type == 'image/x-icon') {
          headList[i].href = " "
        }
      }
      if(getStore({name: 'userInfo'}) && getStore({name: 'tenantId'})) {
        this.userInfo = getStore({name: 'userInfo'})
      }else{
        this.userInfo = {}
      }
    },
    mounted() {
      let token =  store.getters.access_token
      // console.log(token)
      this.logoV2Pic = ""
      document.title = ""
      // if (token.length === 0) {
      //   getDomain().then(res => {
      //     if(res.data && res.data.code == 0) {
      //       if(res.data.data){
      //         if(res.data.data.bgImg) {
      //           this.background = `background-image:url("${res.data.data.bgImg}");`
      //         }
      //         if(res.data.data.logo) {
      //           this.logoV2Pic = res.data.data.logo
      //         }
      //         if(res.data.data.name) {
      //           document.title = res.data.data.name
      //         }
      //         if(res.data.data.icon) {
      //           var link = document.createElement('link')
      //           link.type = 'image/x-icon'
      //           link.rel = 'shortcut icon'
      //           link.href = res.data.data.icon
      //           document.getElementsByTagName('head')[0].appendChild(link);
      //         }
      //         if(!this.isMobile){
      //           this.openLogin()
      //         }
      //       } else {
      //         this.$router.push({ path: '/404' });
      //       }
      //     // if (res.data && res.data.code == 0) {
      //     //   if(res.data.data.bgImg) {
      //     //     this.bgImg = res.data.data.bgImg
      //     //   }
      //     //   this.bgImg = res.data.data.bgImg
      //     //   if(res.data.data.logo) {
      //     //     this.logoV2Pic = res.data.data.logo
      //     //   }
      //     //   if(res.data.data.name) {
      //     //     document.title = res.data.data.name
      //     //     localStorage.setItem('windowTitle', res.data.data.name)
      //     //   }
      //     //   if(res.data.data.icon) {
      //     //     var link = document.createElement('link')
      //     //     link.type = 'image/x-icon'
      //     //     link.rel = 'shortcut icon'
      //     //     link.href = res.data.data.icon
      //     //     document.getElementsByTagName('head')[0].appendChild(link);
      //     //   }
      //     //   this.$forceUpdate()
      //     }
      //   })
      //   this.$openLogin({
      //     closeable: true,
      //     right: '150px',
      //     successClose: false,
      //     afterLogin: (dialog, res) => {
      //       // this.go('/register')
      //       window.location.reload()
      //       console.log('登录提交。。。。。')
      //       this.userInfo = getStore({name: 'userInfo'})
      //       console.log(this.userInfo)
      //       // 登录IM
      //       try {
      //         this.connect(res.code, dialog)
      //       } catch (error) {
      //         dialog.handleClose()
      //         this.$router.push({ path: this.tagWel.value });
      //       }
      //     },
      //     afterRegister: () => {
      //       console.log('注册提交。。。。。')
      //     }
      //   })
      //   return
      // } else {
      //   document.title = localStorage.getItem('windowTitle')
      // }
      this.$store.dispatch('WSINIT')
      this.handleCheckIsFirstLogin()
      EventBus.$on('handleCheckIsFirstLogin', target => {
        this.handleReset()
      });
      EventBus.$on('refresh', target => {
        this.refreshCb()
      })
      EventBus.$on('moveMailRefresh', target => {
        this.showWriteMail = false
        this.detailMode = true
        this.activeMailDetailId = ''
        this.routeParams = {}
      })
      this.connectWebsocket()
    },
    filters: {
      formatLogTime(value) {
        return relativelyTime(value)
      }
    },
    computed: {
      ...mapState({
        mailConfigId: state => state.socket.mailConfigId,
        systemHelpDict: state => state.common.systemHelpDict,
        remainingCount: state => state.common.remainingCount
      }),
      //logo
      logoImg(){
        // return store?.state?.common?.tenantInfo?.logo || this.logo
        // return this.userInfo.tenant.logo
        return this.$store.state.common.tenantInfo.logo || this.logo
      },
      getWsMsg() {
        return this.$store.state.socket.socketMsg
      },
      // 判断是否显示紧急图标
      showWarningIcon() {
        return this.activeName !== 'drafts'
      },
      // 判断是否显示紧急图标
      showHightLight() {
        return ['inbox', 'directory'].includes(this.activeName)
      },
      // 显示未读
      showUnReadHightLight() {
        return ['inbox', 'directory'].includes(this.activeName)
      },
    },
    methods: {
      // 获取邮箱类型图片
      getImg(mailType) {
        const arr = [
          {src: require('@/assets/img/mail/tx.png'), w: '25px', h: '20px', type: 'tengXunQiYe', text: '腾讯企业邮'},
          {src: require('@/assets/img/mail/qq.png'),  w: '25px', h: '29px', type: 'tengXunQQ', text: 'QQ邮箱'},
          {src: require('@/assets/img/mail/163.png'),  w: '25px', h: '15px', type: 'wangYi', text: '163邮箱'},
          {src: require('@/assets/img/mail/126.png'),  w: '25px', h: '16px', type: '126', text: '126邮箱'},
          {src: require('@/assets/img/mail/ali.png'),  w: '25px', h: '16px', type: 'ali', text: '阿里邮箱'},
          {src: require('@/assets/img/mail/139.png'),  w: '25px', h: '16px', type: '139', text: '139邮箱'},
          {src: require('@/assets/img/mail/xinlang.png'),  w: '25px', h: '16px', type: 'sina', text: '新浪邮箱'},
          {src: require('@/assets/img/mail/souhushandian.png'),  w: '25px', h: '16px', type: 'souhu', text: '搜狐闪电邮箱'},
          {src: require('@/assets/img/mail/189.png'),  w: '25px', h: '16px', type: '189', text: '189邮箱'},
          // {src: require('@/assets/img/mail/gmail.png'),  w: '25px', h: '22px', type: 'gmail', text: 'Gmail'},
          // {src: require('@/assets/img/mail/outlook.png'),  w: '25px', h: '25px', type: 'outlook', text: 'Outlook'},
          // {src: require('@/assets/img/mail/exchange.png'),  w: '25px', h: '27px', type: 'exchange', text: 'Exchange'},
          {src: require('@/assets/img/mail/mail.png'),  w: '25px', h: '26px', type: 'other', text: '其他邮箱'},
        ]
        let obj = {}
        arr.forEach(item => {
          if (item.type === mailType) {
            obj = item
          }
        })
        return obj
      },
      backStep(){
        this.connectVisibles = false//返回所有消息
      },
      //false删除已读，true删除全部
      delnoticeall(){
        if (this.noList.length == 0) {
          this.$message.warning('无删除数据')
        }else{
          this.$confirm('确认删除消息？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            if (this.delActive == 'all') {
              noticeDel(true).then(res=>{
                if(res.data.code == 0 ){
                  this.$openNotify('删除全部消息成功');
                  this.getAllNotice()
                  this.$store.commit("SET_REMAIN_COUNT", 0)
                }
              })
            }else if(this.delActive == 'readIs'){
              noticeDel(false).then(res=>{
                if(res.data.code == 0 ){
                  this.$openNotify('删除已读消息成功');
                  this.getAllNotice()
                }
              })
            }
          })
        }
      },
      //删除单条消息
      delnotice(id,index,params){
        if(!this[params][index].readIs){
          this[params][index].readIs = true
          this.$store.commit("SET_REMAIN_COUNT", this.remainingCount-1)
        }
        this.$confirm('确认删除消息？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          noticeDelone(id).then(res=>{
            if(res.data.code == 0 ){
              this.$openNotify('删除单条消息成功')
              this.noList.splice(index, 1);
            }
          })
        })
      },
      //详情单条删除
      delnoDetailList(id,index){
        this.$confirm('确认删除消息？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          noticeDelone(id).then(res=>{
            if(res.data.code == 0 ){
              this.$openNotify('删除详情单条消息成功')
              this.noList.splice(index, 1);
              this.connectVisibles = false
              this.getAllNotice()
            }
          })
        })
      },
      handleClick(tab, event) {
      },
      //消息详情
      changeDetail(id,index,params){
        this.connectVisibles = true
        this.noDetailLoading = true
        this.visible = false
        //查看详情
        noticeDetail(id).then(res=>{
          if(res.data.code == 0){
            if(!this[params][index].readIs){
              this[params][index].readIs = true
              this.$store.commit("SET_REMAIN_COUNT", this.remainingCount-1)
            }
            this.noDetailList = JSON.parse(JSON.stringify(res.data.data))
            if (res.data.data.msgContent.length > 0) {
              const obj = JSON.parse(res.data.data.msgContent)
              this.noDetailList.title = obj.title
              this.noDetailList.content = obj.content
            }
            this.noDetailLoading = false
          } else {
            this.noDetailLoading = false
          }
        }).catch(err => {
          this.noDetailLoading = false
        })
      },
      // 关闭弹窗
      handleConnectCloses() {
        this.connectVisibles = false
      },
      //打开弹框
      allnotice(){
        this.connectVisible = true
        this.visible = false
        //查询所有
        this.getAllNotice()
      },
      // 获取所有通知
      getAllNotice() {
        this.noListLoading = true
        noticeList({
          readIs:this.tabActive=='1'?null:false
        }).then(res=>{
          if(res.data.code == 0){
            this.noList = JSON.parse(JSON.stringify(res.data.data))
            // console.log(this.noList)
            this.noList.forEach(item => {
              item.msgContent = JSON.parse(item.msgContent)
            })
            this.noListLoading = false
          }
        }).catch(err => {
          this.noListLoading = false
        })
      },
      // 关闭弹窗
      handleConnectClose() {
        this.connectVisible = false
      },
      //标记全部已读
      editReadIs(){
        noticeAll().then(res=>{
          if(res.data.code == 0){
            this.readIs = true
            this.$store.commit("SET_REMAIN_COUNT", 0)
          }
        })
      },
      // 分页查询消息通知
      getMessage() {
        this.readIs = false
        //跳转消息中心
        // this.$router.push({path: '/message'})
        this.loading = true
        noticePage({
          current:this.pageObj.current,
          size:this.pageObj.size,
          tabActive:this.tabActive
        }).then(res=>{
          if(res.data.code == 0){
            this.pageObj.total = res.data.data.total
            this.insideList = JSON.parse(JSON.stringify(res.data.data.records))
            // console.log(this.insideList)
            this.insideList.forEach(item => {
              item.msgContent = JSON.parse(item.msgContent)
            })
          }
        }).finally(res=>{
          this.loading = false
        })
      },
      handleCurrentChange(val){
        this.pageObj.current = val
        this.getMessage()
      },
      // 连接socket
      async connectWebsocket() {
        const env = process.env
        let wstUrl = ''
        if (env.NODE_ENV == 'development') {
          wstUrl = 'ws://10.0.0.38:10000/inside/notice/'
        } else {
          wstUrl = 'ws://' + location.host + '/inside/notice/'
        }
        if (this.userInfo && this.userInfo.id && this.websocket==null) {
          this.websocket = new WebSocket(wstUrl+this.userInfo.id+'/'+this.userInfo.tenantId)
          this.websocket.onopen = event => {
            console.log('-----socket连接成功-----')
            this._timer = setInterval(() => {
              this.websocket.send('PING')
            }, 30 * 1000) // 30 秒发一次心跳包
          }
          this.websocket.onmessage = event => {
            let msg = JSON.parse(event.data);
            // console.log(msg);
            if (getStore({ name: "userInfo" })) {
              this.$store.commit("SET_REMAIN_COUNT", msg.remainingCount)
              if (msg.remainingCount > 0) {
                this.$notify({
                  title: '提示',
                  message: '您有新消息',
                  position: 'bottom-right'
                })
              }
            } else {
              this.websocket.close()
            }
          }
          this.websocket.onerror = event => {
            this.websocket.close()
            this.websocket = null
          }
          this.websocket.onclose = event => {
            console.log('断开连接', event)
            if (this._timer) {
              clearInterval(this._timer) //清除定时器
              this._timer = null
            }
          }
        }
      },

      //个人中心
      goInfo(){
        this.$router.push({path: '/user'})
      },
      //回到首页
      goHome () {
        this.$router.push({path: '/mail'})
      },
      handleLogout() {
        this.$confirm("是否退出系统, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          loginoutHandle().then(res => {
            if(res.data.code == 0) {
              // let path = this.$store.state.common.template || '/mail'
              // sessionStorage.clear()
              // this.$store.dispatch("LogOut")
              // this.$router.push({ path: path })
              // window.location.reload()
              let path = this.$store.state.common.template || '/login'
              sessionStorage.clear()
              this.$store.dispatch("LogOut")
              this.$router.push({ path: path })
            }
          })
        });
      },
      handleChangeLogin() {//切换登录/退出
        // this.userMailList=[]
        // this.isUserMailListLoaded = true
        // this.isLoaded = false
        // this.isMailLogin = false
        this.$openUrl('/wel/index')
        // console.log(this.isLoaded,this.isMailLogin,"9999999999999666666666666")
      },
      // 重置
      handleReset() {
        Object.assign(this.$data, this.$options.data())
        this.handleCheckIsFirstLogin()
      },
      // 用户列表
      handleGetUserMailList() {
        this.userMailListDropDownList = []
        this.isUserMailListLoaded = false
        TODO:
        getMailConfigList().then(res => {
          if (res.data.code === 0) {
        // const res = {
        //   data: {"code":200,"msg":"成功","data":[{"id":71,"sysUserId":419,"mailType":"wangYi","account":"Beats0","sendHost":"smtp.163.com","acceptHost":"imap.163.com","sendPort":465,"acceptPort":993,"acceptSsl":1,"sendSsl":1,"username":"jyl2047155291@163.com","password":"18725948578","sendProtocol":"smtp","acceptProtocol":"imap","defaultEncodingS":"UTF-8","useWay":1,"createTime":"2021-04-03T22:21:00","updateTime":"2021-04-03T22:21:00","delFlag":0,"tenantId":39},{"id":110,"sysUserId":419,"mailType":"tengXunQQ","account":"","sendHost":"smtp.qq.com","acceptHost":"imap.qq.com","sendPort":465,"acceptPort":993,"acceptSsl":1,"sendSsl":1,"username":"1357097581@qq.com","password":"xxppeerzfmrebagg","sendProtocol":"smtp","acceptProtocol":"imap","defaultEncodingS":"UTF-8","useWay":1,"createTime":"2021-06-09T09:26:32","updateTime":"2021-06-09T09:26:32","delFlag":0,"tenantId":39}],"timestamp":"2021-06-09 14:28:06 870"}
        // }
            const configId = res.data.data[0].id
            localStorage.setItem('mailConfigId', configId)
            this.$store.dispatch('SetMailConfigId', configId)
            this.userMailListDropDownList.push({ label: '全部账号', value: -1 })
            res.data.data.forEach(item => {
              item.unreadCount = 0
              item.directoryList = []
              this.userMailListDropDownList.push({ label: item.username, value: item.id })
            })
            this.userMailList = res.data.data
            // console.log(this.userMailList)
            this.handleMenuRouterClick(this.userMailList[0].id, 'inbox', '1',this.userMailList[0].username)
            // 展开第一个
            this.collapseActive = [configId]
            // 获取第一个未读
            this.handleQueryUnreadCount(configId)
            this.activeId = configId
            this.isMailLogin = true
            this.groupId = '1'
            this.getList('1', configId)
            this.getDetailById()
            this.initWriteMail()
            this.setDirectoryListById()
            this.settagAllById()
          }
        }).finally(() => {
          this.isUserMailListLoaded = true
        })
      },
      // 通过router显示邮件详情的
      getDetailById() {
        // 初始化
        const initId = this.$route.query.initId || ''
        if(initId) {
          const initRouter = {
            id: initId,
            routerName: 'mailDetail'
          }
          setTimeout(() => {
            this.changeRoute(initRouter)
          }, 1000)
        }
      },
      // 初始化从外部点击进来写邮件的 ?initMail=xxx&action=initWriteMail
      initWriteMail() {
        const routerQuery = this.$route.query
        if (routerQuery.initMail && routerQuery.action === 'initWriteMail') {
          const id = this.userMailList[0].id
          localStorage.setItem('mailConfigId', id)
          this.$store.dispatch('SetMailConfigId', id)
          this.routeParams = {
            initMail: routerQuery.initMail,
            action: 'initWriteMail'
          }
          this.detailMode = false
          this.showWriteMail = true
          this.refreshCb()
        }
      },
      // 同步
      handleSyncMail(configId, mailName) {
        let data = {}
        // 全部
        if(configId === -1) {
          data = {
            type: 'syncEmail',
            data: {
              event: 'handleSyncMail'
            }
          }
        } else {
          // 单个
          data = {
            type: 'syncEmail',
            data: {
              configId,
            }
          }
        }
        this.syncMailName = mailName
        this.syncDialog = true
        this.$store.dispatch('handleSend', data)
      },
      isCollapseActive(id) {
        return this.collapseActive.includes(id)
      },
      // list
      getList(groupId = '1', mailConfigId) {
        this.tableLoading = true
        let list = []
        const query = {
          subject: this.searchSubject,
          configId: mailConfigId,
          groupId,
        }
        // 是否强调
        if (this.stress === 1) {
          query.stress = 1
        }
        TODO:
        getMailList(query)
          .then(res => {
            if (res.data.code === 0) {
        // const res = {
        //   data: {"code":200,"msg":"成功","data":{"lastMonth":[{"id":10658,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504522","from":"Szjqq","sentDate":"2021-05-07T09:30:21","subject":"57发了1","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-05-07T09:31:02","updateTime":"2021-05-07T09:31:02","tenantId":39},{"id":10640,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504520","from":"qqszj","sentDate":"2021-05-06T19:19:30","subject":"pc1919","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-05-06T19:19:53","updateTime":"2021-05-06T19:19:53","tenantId":39},{"id":10510,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504514","from":"244szj","sentDate":"2021-05-06T17:54:40","subject":"重复测试","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":0,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-05-06T17:54:53","updateTime":"2021-05-06T17:54:53","tenantId":39}],"unreadNum":5,"thisMonth":[],"earlier":[{"id":8659,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504508","from":"Beats0","sentDate":"2021-04-28T09:30:22","subject":"测试3","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-28T09:31:21","updateTime":"2021-04-28T09:31:21","tenantId":39},{"id":8658,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504507","from":"Beats0","sentDate":"2021-04-28T09:29:27","subject":"测试3","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-28T09:31:21","updateTime":"2021-04-28T09:31:21","tenantId":39},{"id":8656,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504505","from":"Beats0","sentDate":"2021-04-28T09:27:20","subject":"测试2","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-28T09:31:20","updateTime":"2021-04-28T09:31:20","tenantId":39},{"id":8651,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504504","from":"Beats0","sentDate":"2021-04-28T09:17:52","subject":"ces2","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":0,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-28T09:21:22","updateTime":"2021-04-28T09:21:22","tenantId":39},{"id":8646,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504502","from":"\" 王棕悦 \"","sentDate":"2021-04-27T22:57:03","subject":"cds","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-28T09:16:06","updateTime":"2021-04-28T09:16:06","tenantId":39},{"id":8016,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504501","from":"网易邮件中心","sentDate":"2021-04-22T03:34:14","subject":"网易邮箱提醒：一次被阻止的收信行为","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":0,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-22T03:34:17","updateTime":"2021-04-28T09:58:52","tenantId":39},{"id":7876,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504499","from":"阿里云","sentDate":"2021-04-16T12:08:10","subject":"【释放预警】阿里云ECS即将释放提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":1,"delFlag":0,"createTime":"2021-04-16T19:11:38","updateTime":"2021-04-22T09:15:06","tenantId":39},{"id":7875,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504498","from":"阿里云","sentDate":"2021-04-16T12:07:17","subject":"【释放预警】阿里云ECS即将释放提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-16T19:11:37","updateTime":"2021-06-09T09:31:36","tenantId":39},{"id":6469,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504497","from":"阿里云","sentDate":"2021-04-13T12:44:33","subject":"【高危应急漏洞情报通知】云安全中心 0Day 漏洞运营实验室，请您重点关注","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-13T12:44:51","updateTime":"2021-06-09T09:31:39","tenantId":39},{"id":6468,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504496","from":"网易邮件中心","sentDate":"2021-04-12T18:18:28","subject":"网易邮箱提醒：一次被阻止的收信行为","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":0,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-12T18:19:19","updateTime":"2021-04-12T18:19:19","tenantId":39},{"id":4862,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504479","from":"阿里云","sentDate":"2021-04-01T12:16:11","subject":"【到期预警】阿里云ECS即将到期提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":0,"answered":0,"stress":1,"delFlag":0,"createTime":"2021-04-03T22:25:37","updateTime":"2021-04-08T19:32:28","tenantId":39},{"id":4861,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504478","from":"阿里云","sentDate":"2021-03-30T12:08:19","subject":"【到期预警】阿里云ECS即将到期提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:37","updateTime":"2021-04-03T22:25:37","tenantId":39},{"id":4860,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504477","from":"阿里云","sentDate":"2021-03-29T11:50:32","subject":"阿里云服务器待处理漏洞周报","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:36","updateTime":"2021-04-03T22:25:36","tenantId":39},{"id":4859,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504476","from":"阿里云","sentDate":"2021-03-26T12:07:58","subject":"【到期预警】阿里云ECS即将到期提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:35","updateTime":"2021-04-03T22:25:35","tenantId":39},{"id":4858,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504475","from":"阿里云","sentDate":"2021-03-22T11:53:30","subject":"阿里云服务器待处理漏洞周报","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:35","updateTime":"2021-04-03T22:25:35","tenantId":39},{"id":4857,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504474","from":"Beats0","sentDate":"2021-03-16T16:28:27","subject":"2047155291@qq.com","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:35","updateTime":"2021-04-03T22:25:35","tenantId":39},{"id":4856,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504473","from":"阿祝","sentDate":"2021-03-16T10:18:17","subject":"测试发邮件","isSend":1,"urgent":1,"receipt":1,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:34","updateTime":"2021-04-03T22:25:34","tenantId":39},{"id":4855,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504472","from":"阿里云","sentDate":"2021-03-15T11:51:10","subject":"阿里云服务器待处理漏洞周报","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:34","updateTime":"2021-04-03T22:25:34","tenantId":39},{"id":4854,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504471","from":"Postmaster@163.com","sentDate":"2021-03-15T11:38:36","subject":"系统退信","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:33","updateTime":"2021-04-03T22:25:33","tenantId":39},{"id":4853,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504470","from":"网易邮箱管理员","sentDate":"2021-03-13T09:30:11","subject":"云附件到期提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:33","updateTime":"2021-04-03T22:25:33","tenantId":39},{"id":4852,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504469","from":"\" Beats0 \"","sentDate":"2021-03-12T17:32:38","subject":"\"jyl2047155291\"<jyl2047155291@163.com>;","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:32","updateTime":"2021-04-03T22:25:32","tenantId":39},{"id":4851,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504468","from":"网易邮箱管理员","sentDate":"2021-03-12T09:30:41","subject":"云附件到期提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:32","updateTime":"2021-04-03T22:25:32","tenantId":39},{"id":4850,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504467","from":"Postmaster@163.com","sentDate":"2021-03-11T20:46:35","subject":"系统退信","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:31","updateTime":"2021-04-03T22:25:31","tenantId":39},{"id":4849,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504466","from":"Postmaster@163.com","sentDate":"2021-03-11T20:28:22","subject":"系统退信","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:30","updateTime":"2021-04-03T22:25:30","tenantId":39},{"id":4848,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504465","from":"Postmaster@163.com","sentDate":"2021-03-11T20:28:13","subject":"系统退信","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:30","updateTime":"2021-04-03T22:25:30","tenantId":39},{"id":4847,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504464","from":"Beats0","sentDate":"2021-03-11T14:10:14","subject":"222jyl2047155291@163.com","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:29","updateTime":"2021-04-03T22:25:29","tenantId":39},{"id":4846,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504463","from":"Beats0","sentDate":"2021-03-11T14:10:11","subject":"222jyl2047155291@163.com","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:28","updateTime":"2021-04-03T22:25:28","tenantId":39},{"id":4845,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504462","from":"Beats0","sentDate":"2021-03-11T14:07:59","subject":"jyl2047155291@163.com","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:28","updateTime":"2021-04-03T22:25:28","tenantId":39},{"id":4844,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504461","from":"Szj","sentDate":"2021-03-09T11:27:12","subject":"回复：111","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:27","updateTime":"2021-04-03T22:25:27","tenantId":39},{"id":4843,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504460","from":"Beats0","sentDate":"2021-03-09T10:50:50","subject":"111","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:27","updateTime":"2021-04-03T22:25:27","tenantId":39},{"id":4842,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504459","from":"阿里云","sentDate":"2021-03-08T11:48:49","subject":"阿里云服务器待处理漏洞周报","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:27","updateTime":"2021-04-03T22:25:27","tenantId":39},{"id":4841,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504458","from":"Beats0","sentDate":"2021-03-06T11:17:01","subject":"111","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:26","updateTime":"2021-04-03T22:25:26","tenantId":39},{"id":4840,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504457","from":"Beats0","sentDate":"2021-03-06T11:06:32","subject":"111","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:25","updateTime":"2021-04-03T22:25:25","tenantId":39},{"id":4839,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504456","from":"\"jyl2047155291@163.com\"","sentDate":"2021-03-05T19:17:13","subject":"转发: 转发: reset_success","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:25","updateTime":"2021-04-03T22:25:25","tenantId":39},{"id":4838,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504455","from":"Beats0","sentDate":"2021-03-05T19:12:20","subject":"回复：图片","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:24","updateTime":"2021-04-03T22:25:24","tenantId":39},{"id":4837,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504454","from":"阿里云","sentDate":"2021-03-05T17:32:31","subject":"阿里云产品动态(2月刊)","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:24","updateTime":"2021-04-03T22:25:24","tenantId":39},{"id":4836,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504453","from":"beats0","sentDate":"2021-03-05T16:37:34","subject":"图片","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:23","updateTime":"2021-04-03T22:25:23","tenantId":39},{"id":4835,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504452","from":"beats0","sentDate":"2021-03-05T16:21:39","subject":"轻舟","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:23","updateTime":"2021-04-03T22:25:23","tenantId":39},{"id":4834,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504451","from":"beats0","sentDate":"2021-03-05T16:06:33","subject":"一个附件","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:22","updateTime":"2021-04-03T22:25:22","tenantId":39},{"id":4833,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504450","from":"beats0","sentDate":"2021-03-05T16:03:57","subject":"111","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:22","updateTime":"2021-04-03T22:25:22","tenantId":39},{"id":4832,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504449","from":"\"jyl2047155291@163.com\"","sentDate":"2021-03-05T15:39:29","subject":"员工转正考评表","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:21","updateTime":"2021-04-03T22:25:21","tenantId":39},{"id":4831,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504448","from":"\"jyl2047155291@163.com\"","sentDate":"2021-03-05T15:36:51","subject":"项目管理","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:20","updateTime":"2021-04-03T22:25:20","tenantId":39},{"id":4830,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504447","from":"\" Beats0 \"","sentDate":"2021-03-05T14:09:48","subject":"项目执行自评估报告 (2)","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:19","updateTime":"2021-04-03T22:25:19","tenantId":39},{"id":4829,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504446","from":"\" Beats0 \"","sentDate":"2021-03-05T14:08:57","subject":"snap","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:19","updateTime":"2021-04-03T22:25:19","tenantId":39},{"id":4828,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504445","from":"beats0","sentDate":"2021-03-05T12:01:46","subject":"项目执行自评估报告 (2)","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:18","updateTime":"2021-04-03T22:25:18","tenantId":39},{"id":4827,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504444","from":"\"jyl2047155291@163.com\"","sentDate":"2021-03-05T11:27:57","subject":"回复: 222","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:18","updateTime":"2021-04-03T22:25:18","tenantId":39},{"id":4826,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504443","from":"beats0","sentDate":"2021-03-05T10:38:45","subject":"222","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:17","updateTime":"2021-04-03T22:25:17","tenantId":39},{"id":4825,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504442","from":"beats0","sentDate":"2021-03-05T10:37:12","subject":"111","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:17","updateTime":"2021-04-03T22:25:17","tenantId":39},{"id":4824,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504441","from":"beats0","sentDate":"2021-03-04T15:38:32","subject":"forty","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:16","updateTime":"2021-04-03T22:25:16","tenantId":39},{"id":4823,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504440","from":"beats0","sentDate":"2021-03-04T15:33:30","subject":"reset_success","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":1,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:16","updateTime":"2021-04-03T22:25:16","tenantId":39},{"id":4822,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504439","from":"beats0","sentDate":"2021-03-04T15:24:17","subject":"轻舟 (2)","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:15","updateTime":"2021-04-03T22:25:15","tenantId":39},{"id":4821,"groupId":1,"mailConfigId":71,"userId":419,"mailId":"jyl2047155291@163.com:1389504438","from":"网易帐号中心 ","sentDate":"2021-03-04T09:15:14","subject":"网易邮箱帐号异常登录提醒","isSend":1,"urgent":0,"receipt":0,"isReceipt":0,"isExtend":0,"isTiming":0,"rend":1,"answered":0,"stress":0,"delFlag":0,"createTime":"2021-04-03T22:25:14","updateTime":"2021-04-03T22:25:14","tenantId":39}]},"timestamp":"2021-06-09 14:49:41 193"}
        // }
              const {thisMonth = [], lastMonth = [], earlier = [], unreadByDate} = res.data.data
              // console.log(res.data.data)
              list.push({title: '本月', list: thisMonth, noReadNum: unreadByDate.thisMonth || 0})
              list.push({title: '上月', list: lastMonth, noReadNum: unreadByDate.lastMonth || 0})
              list.push({title: '更早', list: earlier, noReadNum: unreadByDate.earlier || 0})
              this.mailList = list
              console.log(this.mailList)
              this.$forceUpdate()
            }
          }).finally(() => {
          this.tableLoading = false
          this.isLoaded = true
        })
      },
      // 查询某个邮箱未读
      handleQueryUnreadCount(id) {
        const query = {
          groupId: '1',
          configId: id
        }
        queryUnreadCount(query).then(res => {
          if(res.data.code === 0) {
            const unreadCount = res.data.data
            this.userMailList.forEach(item => {
              if(item.id === this.activeId) {
                item.unreadCount = unreadCount
              }
            })
          }
        })
      },
      // 获取用户邮箱信息
      // handleGetSenderMail() {
      //   getSenderMail().then(res => {
      //     if (res.data.code === 200) {
      //       this.isLoaded = true
      //       // 是否已近绑定邮箱
      //       if (res.data.data) {
      //         this.isMailLogin = true
      // this.setDirectoryListById()
      //       }
      //     }
      //   })
      // },
      // 判断是否为第一次登录
      handleCheckIsFirstLogin() {
        this.isLoaded = true
        this.handleGetUserMailList()
        return
        TODO:
        isFirstLogin().then(res => {
          if (res.data.code === 0) {
            this.isLoaded = res.data.data
            // 是否已绑定邮箱
            if (res.data && res.data.data.length > 0) {
              this.handleGetUserMailList()
            } else {
              this.isUserMailListLoaded = true
            }
          }
        })
      },
      //标签
      handlelabelialog(id){
        this.tagDialog = true
        this.tagType = 'add'
        // console.log(this.tagType)
      },
      //关闭弹框
      handletagDialogCancel(){
        this.tagDialog = false
        this.tagName = ''
        this.tagColor = ''
      },
      //新建标签 确定
      handletagSubmit(){
        if (hasBlank(this.tagName)) {
          this.$message.error('请输入文件夹名称')
          return
        }
        const query = {
          tagName: this.tagName,
          tagColor: this.tagColor,
          id: this.tagId == '' ? undefined : this.tagId,
          // configId:this.mailConfigId
        }
        if(this.tagType === 'add'){
          query.mailConfigId = this.mailConfigId
          // console.log(this.tagType)
          tagmailSave(query).then(res => {
            if (res.data.code === 0) {
              this.$message.success('创建成功')
              this.tagDialog = false
              this.tagName = ''
              this.tagColor = ''
              this.settagAllById()
              this.handleGetUserMailList()
            }
          })
        }else if(this.tagType === 'edit'){
          // console.log(this.tagType)
          tagmailEdit(query).then(res => {
            if (res.data.code === 0) {
              this.$message.success('修改成功')
              this.tagDialog = false
              this.settagAllById()
              this.handleGetUserMailList()
            }
          })
        }
      },
      //查询当前邮箱下的所有标签
      settagAllById(){
        const id = this.activeId
        const dirArr = []
        getTagmailAll(id).then(res => {
          if (res.data.code === 0) {
            res.data.data.forEach(item => {
              dirArr.push({
                id: item.id,
                // path: `inbox${item.id}`,
                title: item.tagName,
                tagColor: item.tagColor,
              })
            })
            this.userMailList.forEach(item => {
              if(item.id === id) {
                item.tagAllData = dirArr
              }
            })
          }
        })
      },
      //标签
      dotagAction(type,index,router,item){
        this.tagId = router.id
        this.tagName = router.title
        this.tagColor = router.tagColor
        switch (type) {
          case 'del':
            this.$confirm(`确认要删除标签？`,'提示',{
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              confirmButtonClass:'el-button--danger',
              type: 'warning'
            }).then(()=> {
              tagmailDel(router.id).then(res=>{
                if(res.data.code == 0){
                  this.$message.success("删除成功")
                  // 如果是删除当前选中的，直接跳转到收件箱
                  if (item.id === this.directoryActiveId) {
                    this.handleMenuRouterClick(item.id, 'inbox')
                  }
                  this.setDirectoryListById(item.id)
                  this.settagAllById()
                }
              })
            }).catch(()=> {})
            break;
          case 'edit':
            this.tagDialog = true
            this.tagType = 'edit'
            // this.tagList = JSON.parse(JSON.stringify(item))
            // console.log(this.tagType)
            // console.log(this.tagList)
            break;
          default:
            break;
        }
      },
      // 新建文件夹
      handleOpenDialog(id) {
        this.folderTitle = '新增文件夹'
        this.folderType = 'add'
        this.selectId = id
        this.folderDialog = true
      },
      // 设置dir, 获取文件夹列表
      setDirectoryListById(id) {
        id = id || this.activeId
        const dirArr = []
        const query = {
          // mailConfigId: id,
          configId:this.mailConfigId
        }
        getQueryGroup(query).then(res => {
          if (res.data.code === 0) {
            res.data.data.forEach(item => {
              dirArr.push({
                id: item.id,
                path: `inbox${item.id}`,
                title: item.groupName,
              })
            })
            this.userMailList.forEach(item => {
              if(item.id === this.activeId) {
                item.directoryList = dirArr
                // console.log(item.directoryList)
              }
            })
          }
        })
      },
      changeMailTotal() {
        const id = this.activeId
        this.handleQueryUnreadCount(id)
      },
      refreshCb() {
        const groupId = this.groupId
        const id = this.activeId
        if (groupId && id) {
          this.getList(groupId, id)
        }
      },
      // 点击展开，更换邮箱
      handleChangeMail(id) {
        if(this.collapseActive.includes(id)) {
          this.collapseActive = this.collapseActive.filter(item => item !== id)
        } else {
          this.handleQueryUnreadCount(id)
          this.collapseActive.push(id)
        }
        // 添加
        if(!this.directoryList[id]) {
          this.setDirectoryListById(id)
        }
        // localStorage.setItem('mailConfigId', id)
        // this.$store.dispatch('SetMailConfigId', id)
        // console.log('change mail id', id)
      },
      handleMenuRouterClick(id, path, groupId,mailName) {
        this.isMailList = true
        this.activeId = id
        localStorage.setItem('mailConfigId', id)
        this.$store.dispatch('SetMailConfigId', id)
        this.searchSubject = ''
        this.stress = 0
        switch (path) {
          case 'inbox':
            let data = {}
            let configId = id
            // 全部
            if(configId === -1) {
              data = {
                type: 'realtimePull',
                unreadNum:0,
                data: {
                  event: 'handleMenuRouterClick'
                }
              }
            } else {
              // 单个
              data = {
                type: 'realtimePull',
                data: {
                  configId,
                }
              }
            }
            this.syncMailName = mailName
            this.$store.dispatch('handleSend', data)
            if (this.$store.state.socket && this.$store.state.socket.$socket && this.$store.state.socket.$socket._docker) {
              this.$store.state.socket.$socket._docker.onmessage = event =>{
                if (event.data.slice(0, 1) === '{') {
                  const obj = JSON.parse(event.data)
                  if (obj.status == 'pulling' && obj.data) {
                    this.isPulling = true
                    Object.keys(obj.data).forEach(item => {
                      switch (item) {
                        case 'thisMonth':
                          this.mailList[0].list.push(obj.data[item])
                          break;
                        case 'lastMonth':
                          this.mailList[1].list.push(obj.data[item])
                          break;
                        case 'earlier':
                          this.mailList[2].list.push(obj.data[item])
                          break;
                        default:break;
                      }
                    })
                  }
                  if (obj.status == 'success') {
                    this.mailList[0].noReadNum += obj.unreadByDate.thisMonth || 0
                    this.mailList[1].noReadNum += obj.unreadByDate.lastMonth || 0
                    this.mailList[2].noReadNum += obj.unreadByDate.earlier || 0
                    setTimeout(() => {
                      this.isPulling = false
                    }, 500)
                  }
                  if (obj.status == 'error') {
                    setTimeout(() => {
                      this.isPulling = false
                      // this.$message.error(obj.msg)
                    }, 500)
                  }
                  this.$forceUpdate()
                }
              }
            }
            break;
          default:
            break;
        }
        if (groupId) {
          this.groupId = groupId
        }
        // 相同路由 不切换
        // if (this.showWriteMail && path === 'writeMail') return
        // 当前为路由为writeMail 且 还没有保存的情况下进行提示
        if (this.showWriteMail && this.$refs.writeMail.isSaved === false) {
          this.handleShowSaveMsgBox((result) => {
            if (result === 'success' || result === 'cancel') {
              this.activeName = path
              // if(this.showWriteMail) {
              //
              // } else {
              //   this.directoryActiveId = ''
              // }
              this.activeMailDetailId = ''
              if (path === 'setting') {
                this.detailMode = false
              } else {
                this.detailMode = true
              }
              this.showWriteMail = false
              this.directoryActiveId = ''
              this.routeParams = {}
              this.refreshCb()
            }
          })
          return
        }
        if (path === 'setting') {
          this.detailMode = false
        } else {
          this.detailMode = true
        }
        this.activeName = path
        this.showWriteMail = false
        this.directoryActiveId = ''
        this.activeMailDetailId = ''
        this.getList(groupId, id)
        this.routeParams = {}
      },
      // 写邮件
      handleToWriteMail(id) {
        this.isMailList = false
        // 跳转提醒
        if (this.showWriteMail && this.$refs.writeMail.isSaved === false) {
          this.handleShowSaveMsgBox((result) => {
            if (result === 'success' || result === 'cancel') {
              this.showWriteMail = false
              this.directoryActiveId = ''
              this.routeParams = {}
              setTimeout(() => {
                localStorage.setItem('mailConfigId', id)
                this.$store.dispatch('SetMailConfigId', id)
                this.showWriteMail = true
              }, 1000)
              this.refreshCb()
            }
          })
          return
        }
        localStorage.setItem('mailConfigId', id)
        this.$store.dispatch('SetMailConfigId', id)
        this.routeParams = {}
        this.detailMode = false
        this.showWriteMail = true
        this.refreshCb()
      },
      handleDirRouterClick(configId, router) {
        this.isMailList = true
        this.activeId = configId
        this.groupId = router.id
        localStorage.setItem('mailConfigId', configId)
        this.$store.dispatch('SetMailConfigId', configId)
        this.searchSubject = ''
        this.stress = 0
        // 跳转提醒
        if (this.showWriteMail && this.$refs.writeMail.isSaved === false) {
          this.handleShowSaveMsgBox((result) => {
            if (result === 'success' || result === 'cancel') {
              this.directoryActiveId = router.id
              this.activeName = 'directory'
              this.detailMode = true
              this.showWriteMail = false
              this.activeMailDetailId = ''
              this.directoryRouterParams = {
                id: router.id,
                title: router.title
              }
              this.refreshCb()
            }
          })
          return
        }
        this.directoryActiveId = router.id
        this.activeMailDetailId = ''
        this.activeName = 'directory'
        this.showWriteMail = false
        this.detailMode = true
        this.directoryRouterParams = {
          id: router.id,
          title: router.title
        }
        this.getList(router.id, configId)
      },
      // 添加成功回调
      successCb() {
        const activeName = this.activeName
        this.folderDialog = false
        // 添加成功后自动刷新
        if (this.$refs[`${activeName}`]) {
          this.$refs[`${activeName}`].getList()
        }
      },

      // 活动router
      isCurrentRoute(activeName, router, activeId, item) {
        return activeName === router.path && activeId === item.id
      },
      // 保存草稿提醒
      handleShowSaveMsgBox(cb) {
        this.$msgbox({
          title: '提示',
          message: `
          <div class="msgBoxInner">
            <div class="innerHeader">
              <i class="innerIcon el-icon-info info-icon"></i>
              <span class="innerTitle">是否要将此邮件存为草稿？</span>
            </div>
           <div class="innerTip"></div>
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

              this.$refs.writeMail.handleSendMail('save', (result) => {
                if (result === 'success') {
                  done()
                  instance.confirmButtonLoading = false
                  cb('success')
                } else if (result === 'failed') {
                  done()
                  instance.confirmButtonLoading = false
                  instance.confirmButtonText = '确定'
                }
              })
            } else if(action === 'cancel') {
              cb('cancel')
              done()
            }
          }
        }).then(action => {
        }).catch(e => {})
      },
      // 更改页面
      changeRoute(router) {
        const groupId = this.groupId
        const configId = this.mailConfigId
        // 如果 routerName为空，默认传当前选中的菜单
        router.routerName = router.routerName || this.activeName
        if (router.routerName === 'mailDetail') {
          // 跳转提醒
          if (this.showWriteMail && this.$refs.writeMail.isSaved === false) {
            this.handleShowSaveMsgBox((result) => {
              if (result === 'success' || result === 'cancel') {
                this.directoryActiveId = router.id
                // this.activeName = 'directory'
                this.detailMode = true
                this.showWriteMail = false
                this.activeMailDetailId = router.id
                this.directoryRouterParams = {
                  id: router.id,
                  title: router.title
                }
                this.refreshCb()
              }
            })
            return
          }
          this.detailMode = true
          this.detailEntry = this.activeName
          this.activeMailDetailId = router.id
          this.showWriteMail = false
        } else {
          if (router.routerName === 'writeMail') {
            this.detailMode = false
            this.showWriteMail = true
          } else {
            this.activeName = router.routerName
            this.activeMailDetailId = ''
          }
        }
        this.routeParams = router
        this.getList(groupId, configId)
        this.handleQueryUnreadCount(router.id)
      },
      // 创建 文件夹
      handleFolderSubmit() {
        if (hasBlank(this.folderName)) {
          this.$message.error('请输入文件夹名称')
          return
        }
        if (this.folderType == 'add') {
          const query = {
            name: this.folderName,
            mailConfigId: this.selectId,
            configId:this.mailConfigId
          }
          addGroup(query).then(res => {
            if (res.data.code === 0) {
              this.$message.success('创建成功')
              this.folderDialog = false
              this.folderName = ''
              this.setDirectoryListById(this.selectId)
            }
          })
        } else {
          const param = {
            groupName: this.folderName,
            id: this.currentFolderId,
            configId: this.mailConfigId
          }
          editGroup(param).then(res => {
            if (res.data.code === 0) {
              this.$message.success('修改成功')
              this.folderDialog = false
              this.folderName = ''
              this.setDirectoryListById(this.selectId)
            }
          })
        }
      },
      handleFolderDialogCancel() {
        this.folderName = ''
        this.folderDialog = false
      },
      // 修改文件夹
      handleEditFolder(configId, id, name) {
        this.currentFolderId = id
        this.folderTitle = '编辑文件夹'
        this.folderType = 'edit'
        this.folderName = name
        this.folderDialog = true
      },
      // 删除文件夹
      handleDelFolder(configId, id, name) {
        this.$msgbox({
          title: '删除文件夹确认',
          message: `
          <div class="msgBoxInner">
            <div class="innerHeader">
              <i class="innerIcon el-icon-delete warning-icon"></i>
              <span class="innerTitle">确定删除命名为${name}的文件夹？</span>
            </div>
           <div class="innerTip"></div>
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
              const query = {
                mailConfigId: configId,
                configId:this.mailConfigId,
                id
              }
              delGroup(query).then(res => {
                if (res.data.code === 0) {
                  this.$message.success('删除成功')
                  // 如果是删除当前选中的，直接跳转到收件箱
                  if (id === this.directoryActiveId) {
                    this.handleMenuRouterClick(this.mailConfigId, 'inbox')
                  }
                  this.setDirectoryListById(configId)
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
          // this.$message({
          //   type: 'info',
          //   message: 'action: ' + action
          // });
        }).catch(e => {})
      },
      // 是否强调
      handleFilterStress() {
        this.stress = this.stress === 1 ? 0 : 1
        const groupId = this.groupId
        const mailConfigId = this.mailConfigId
        this.getList(groupId, mailConfigId)
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
            const groupId = this.groupId
            const mailConfigId = this.mailConfigId
            this.getList(groupId, mailConfigId)
          }
        })
      },
      handleSearch() {
        const groupId = this.groupId
        const mailConfigId = this.mailConfigId
        this.getList(groupId, mailConfigId)
      }
    },
    watch: {
      getWsMsg: {
        handler(msg) {
          // 拉取成功
            // console.log(2,msg)
          if (msg.type === 'syncSuccess' && msg.data) {
            console.log(1)
            this.$message.success('收取邮件成功')
            this.syncDialog = false
            this.syncCurrent = 0
            this.syncTotal = 0
            this.syncPercent = 0
          }
          if(msg.type === 'sySuccess') {
            const { count, total } = msg.data
            this.syncCurrent = count
            this.syncTotal = total
            const progressNum = Math.ceil(count / total * 100)
            this.syncPercent = progressNum
            if (count === total) {
              this.syncDialog = false
              this.syncCurrent = 0
              this.syncTotal = 0
              this.syncPercent = 0
            }
          }
          // this.syncDialog = false
        },
        deep: true
      },
      // 防止点击多个通知时详情数据不刷新
      '$route': {
        handler(router) {
          // 通过路由进入查看详情 ?initId=123
          if (router.query.initId) {
            this.getDetailById()
          }
          // 通过点击邮箱账号进入写邮件 ?initMail=xxx@qq.com&action=initWriteMail
          if(router.query.action === 'initWriteMail') {
            this.initWriteMail()
          }
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
//颜色
.tag-badge {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    .badge-item {
      position: relative;
      margin: 25px 10px;
      .badge-item-dot {
        width: 20px;
        height: 20px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        overflow: hidden;
      }
      i {
        position: absolute;
        color: #fff;
        margin-right: 0px;
      }
      .anticon {
        position: absolute;
        left: 5px;
        top: 5px;
        color: #fff;
      }
    }
  }
// ::v-deep .el-collapse-item__arrow { order: 1; margin:0;}
::v-deep .el-collapse-item__header {
  display: flex!important;
}
::v-deep .el-collapse-item__header {
  font-size: 16px !important;
  background: #fcfcfc;
  justify-content: space-between;
}
::v-deep .el-collapse-item__header:hover{
  color: #5DADF7;
}
/deep/ .el-collapse-item__content{
  padding-bottom: 0px !important;
}
.login-logo-picture{
  position: absolute;
  top: 50px;
  left: 90px;
  height: 100px;
}
  .add_new_btn {
    width: 120px;
    height: 30px;
    margin: 0 auto;
    cursor: pointer;
    margin-top: 10px;
    margin-left: 12px;
    border: 1px solid #BFBFBF;
    color: #222222;
    border-radius: 25px;
  }
  .menu-outer {
    margin-top: 16px;
    // padding-bottom: 20px;
  }
  .main-view-container {
    height: calc(100vh - 60px);
    display: flex;
    .left-container {
      min-width: 215px;
      height: 100%;
      position: relative;
      background: #F8F8F8;
      overflow: hidden;
      overflow-y: scroll;
      border-right: 1px solid #DCDCDC;
    }

    .right-container {
      flex: 1;
      background: #fff;
      overflow-y: auto;
      /*max-width: calc(100vw - 276px);*/
      /*max-width: calc(100% - 535px);*/
    }
    .el-menu-item-li {
      display: flex;
      align-items: center;
      height: 46px!important;
      line-height: 46px!important;
      color: #222222;
      margin-right: 8px;
      padding-left: 20px;
      //&:hover {
      //  background: #EEEEEE;
      //}
      .menu-text {
        margin-left: 20px;
        margin-right: 20px;
      }
      .pull-icon{
        margin-left: 4px;
        animation: rotate 1s linear infinite;
      }
      .menu-item-active {
        display: flex;
        align-items: center;
      }
    }
    .el-menu-item-li_active {
      background: #EEEEEE;
      /*color: #3471FF;*/
    }
    .menu_item_icon_container {
      width: 28px;
      height: 28px;
    }
    .directory-menu-outer {
      height: 325px;
      overflow: hidden;
      overflow-y: scroll;
    }
  }
  @import url('../../styles/mail.scss');
@keyframes rotate {
  0%{-webkit-transform:rotate(0deg);}
  50%{-webkit-transform:rotate(180deg);}
  100%{-webkit-transform:rotate(360deg);}
}
</style>
<style lang="scss">
// 顶部nav
.topNav{
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  position: relative;
  .totalName{
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    img{
      height: 50px;
      display: block;
      cursor: pointer;
    }
  }
  .topNav-r{
    display: flex;
    align-items: center;
    position: absolute;
    right: 0px;
    padding-right: 50px;
    fill: currentColor;
    >div{
      // width: 40px;
      text-align: center;
    }
    .setting-btn-container{
      width: 100px;
    }
    .user-info-tool {
      margin: 0 0 0 10px;
      color: rgb(51, 51, 51);
      cursor: pointer;
      display: flex;
      align-items: center;
      span{
        font-size: 14px;
      }
      img{
        display: block;
        width: 28px;
        height: 28px;
        margin-left: 10px;
        border-radius: 50%;
        overflow: hidden;
      }
    }
    .right-search{
      width: 30px;
      height: 30px;
      border-radius: 50%;
      // background-color: #F7eeF1;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      i{
        font-size: 24px;
        color: rgba(183, 182, 182, 100);
      }
    }
  }
}
.top-tool-bar{
  padding:0px;
p{
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0 12px;
  margin: 0;
  line-height: 36px;
  img{
    display: block;
    width: 14px;
    //height: 18px;
    // margin-right: 20px;
    padding: 0 20px;
  }
  span{
    font-size: 14px;
    color: #222222;
  }
}
}
.top-tool-bar p:hover{
background: #F5F7FA;
}
.el-popper[x-placement^=topNav-r],.el-popper[x-placement^=bottom]{
  padding: 0!important;
}
//消息
.el-popover.my-popover{
  height: 677px;
  .right-tab{
    height: 20px;
    display: flex;
    padding: 25px 29px 0px 29px;
    .select-options{
      width: 100px;
      height: 25px;
    }
    .right-tabsize{
      position: absolute;
      right: 29px;
      margin-top: -7px;
      cursor: pointer;
      .right-tabhover{
        color: #8B96A2;
      }
      .right-tabhover:hover{
        color: #3da8f5;
      }
    }
  }
  .el-divider--horizontal{
    margin: 0 auto;
    background-color: #e5e5e5;
  }
  .top-tab:hover{
    color: #3da8f5;
    background-color: #ecf6fe;
    .top-tab-span{
    }
  }
  .top-tab{
    padding: 14px 36px 0px 53px;
    cursor: pointer;
    .top-tab-span{
      width: 158px;
      overflow:hidden; //超出的文本隐藏
      text-overflow:ellipsis; //溢出用省略号显示
      white-space:nowrap; //溢出不换行
      font-size: 16px;
      font-weight: normal;
      // color: #000;
    }
    .top-tab-span-span{
      position: absolute;
      right: 36px;
      font-size: 14px;
    }
    .top-txt{
      padding: 10px 0 5px 0px;
    }
    .container-page{
      text-align: right;
      padding: 10px 0px 0px;
      position: absolute;
      bottom: 55px;
      right: 36px;
    }
  }
}
</style>
<style scoped lang="scss">
//消息
.bottom-txt{
 position: relative;
 top: -7px;
  .bottom-all{
    color: #8B96A2;
    display: flex;
  }
  .bottom-all:hover{
    color: #3da8f5;
    cursor: pointer;
  }
}
//弹框
.task-headers {
  padding: 10px 0px 10px 0px;
  // border-bottom: 1px solid #e5e5e5;
  display: flex;
  vertical-align: middle;
  margin-top: -30px;
  margin-left: -20px;
  margin-right: -20px;
  // background-color: #F2F6F8;
  .head-title {
    padding: 0 10px 0 40px;
    flex: 1 1;
    font-size: 20px;
    /deep/.el-tabs--top .el-tabs__item.is-top:nth-child(2){
      // font-size: 20px;
    }
    /deep/.el-tabs__item{
      color: #415058;
      margin-top: -10px;
    }
    /deep/.el-tabs__item.is-active{
      color: #409EFF;
      font-size: 20px;
    }
    /deep/.el-tabs__header{
      // margin: 0px;
    }
    /deep/.el-tabs__nav-wrap::after{
      height: 1px;
      background-color: #e5e5e5;
    }
    .head-style{
      color: #8B96A2;
      font-size: 20px;
      cursor: pointer;
    }
    .head-style:hover{
      color: #3da8f5;
    }
  }
  .head-del {
    position: relative;
    right: 40px;
    top: 1px;
    font-size: 14px;
    .top-tab-span-span {
      padding: 4px;
      transition: 218ms;
      transition-property: background, color;
      border-radius: 4px;
      align-items: center;
      display: flex;
      text-align: center;
      justify-content: center;
      min-width: 32px;
      cursor: pointer;
      &.active {
        color: #3da8f5;
      }
      &:hover {
        background: #ecf6fe;
        color: #3da8f5;
        border-radius: 4px;
      }
    }
  }
  .header-actions {
    font-size: 17px;
    padding: 0 20px;
    display: flex;
    max-height: 24px;
    .action-item {
      margin-left: -50px;
      padding: 4px;
      transition: 218ms;
      transition-property: background, color;
      border-radius: 4px;
      align-items: center;
      display: flex;
      text-align: center;
      justify-content: center;
      min-width: 32px;
      cursor: pointer;
      span {
        margin-left: 6px;
        font-size: 14px;
      }
      &.active {
        color: #3da8f5;
      }

      &:hover {
        background: #ecf6fe;
        color: #3da8f5;
        border-radius: 4px;
      }
    }
  }
  &.disabled {
    background: #f5f5f5;
  }
  .right-tab{
    display: flex;
    position: absolute;
    right: 95px;
    .right-tabsize{
      margin-top: 4px;
      cursor: pointer;
      .right-tabhover{
        color: #8B96A2;
      }
      .right-tabhover:hover{
        color: #3da8f5;
      }
    }
    .select-options{
      width: 105px;
      height: 25px;
    }
    /deep/.el-input__inner{
      border: 0;
      color: #8B96A2;
      font-size: 14px;
    }
    /deep/.el-input__inner:hover{
     color: #3da8f5;
    }
    /deep/.el-select__caret{
      font-weight: bold;
    }
  }
}
.top-tabs{
  // padding: 26px 40px;
  padding: 26px 0px 0px 40px;
  height: 100%;
  .top-tabs-pad{
    line-height: 60px;
    width: 990px;
    // overflow:hidden; //超出的文本隐藏
    // text-overflow:ellipsis; //溢出用省略号显示
    // white-space:nowrap; //溢出不换行
    font-size: 16px;
    font-weight: normal;
  }
}
.top-tab:hover{
  color: #3da8f5;
  background-color: #ecf6fe;
  .top-tab-span{
    // color: #3da8f5;
  }
}
.top-tab{
  padding: 14px 15px 10px 53px;
  // cursor: pointer;
  .top-tab-span{
    width: 775px;
    overflow:hidden; //超出的文本隐藏
    text-overflow:ellipsis; //溢出用省略号显示
    white-space:nowrap; //溢出不换行
    font-size: 16px;
    font-weight: normal;
    // color: #000;
  }
  .top-tab-span-time{
    position: absolute;
    right: 15px;
    font-size: 16px;
  }
  .top-txt{
    padding: 10px 0 5px 0px;
    font-size: 14px;
    .top-tab-span-span{
      position: absolute;
      right: 15px;
      font-size: 15px;
      visibility: hidden;
      display: none;
    }
  }
  &:hover{
    .top-txt{
      padding: 10px 0 5px 0px;
      font-size: 14px;
      .top-tab-span-span{
        position: absolute;
        right: 15px;
        font-size: 15px;
        margin-top: -15px;
        cursor: pointer;
        visibility: visible;
        display: block;
      }
      .top-tab-span-span:hover{
        color: #3da8f5;
      }
    }
  }
  .el-divider--horizontal{
    margin: 10px auto 0px;
  }
}
/deep/ .customheight{
  height: 672px;
  .el-dialog__header{
    display: none !important;
  }
}
.el-icon-plus{
  cursor: pointer;
  &:hover{
    color: #3da8f5;
  }
}
.ellipsis{
  overflow:hidden; //超出的文本隐藏
  text-overflow:ellipsis; //溢出用省略号显示
  white-space:nowrap; //溢出不换行
}
</style>
<style lang="scss">
.aciton-popover{
  min-width: 80px !important;
  .actions-box{
    .actions-item{
      height: 30px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      color: #808080;
      &:hover{
        background: #f0f5f7;
        //color: #9a7ef4;
      }
      i{
        font-size: 16px;
        margin-right: 5px;
      }
    }
  }
}
</style>
