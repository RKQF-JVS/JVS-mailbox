import 'babel-polyfill'
import 'classlist-polyfill'
import Vue from 'vue'
import axios from './router/axios'
import VueAxios from 'vue-axios'
import App from './App'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/icons'// svg icon
import './permission' // 权限
import './error' // 日志
import router from './router/router'
import store from './store'
import { loadStyle } from './util/util'
import permissionMatch from './util/permision'
import openUrl from './util/url'
import * as urls from '@/config/env'
import {
  iconfontUrl,
  iconfontVersion
} from '@/config/env'
import * as filters from './filters' // 全局filter
import loginForm from './util/login'
import './styles/common.scss'
import './styles/resetAll.scss' // fixme 统一表单表格样式，自定义需要注释此代码
import JsonViewer from 'vue-json-viewer'
import vuescroll from 'vuescroll';
Vue.use(vuescroll);

/**
 * 全局注册容器、组件
 * 不可删除，添加全局组件引用请修改index.js
*/
import './components/index'

// socket
// import VueSocketio from 'vue-socket.io';
// import socketio from 'socket.io-client';
// Vue.use(VueSocketio, socketio('ws://localhost:7777'));//与websocket服务端链接

import basicContainer from "@/components/basic-container/main";

// 注册全局容器
Vue.component("basicContainer", basicContainer);

Vue.use(VueAxios, axios)

Vue.use(ElementUI, {
  size: 'medium',
  menuType: 'text'
})
Vue.use(JsonViewer)

Vue.use(router)

Vue.use(permissionMatch) // 权限
Vue.use(openUrl) // 打开链接 用于 预览、下载、打开地址



// 加载相关url地址
Object.keys(urls).forEach(key => {
  Vue.prototype[key] = urls[key]
})

//加载过滤器
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

// 动态加载阿里云字体库
iconfontVersion.forEach(ele => {
  loadStyle(iconfontUrl.replace('$key', ele))
})

Vue.prototype.$openLogin = loginForm.install
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
