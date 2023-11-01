import Socket from "@/api/socket"
import { systemSocket } from "@/api/system"

const socket = {
  state: {
    $socket: null,
    socketMsg: {
      // 唯一时间戳
      timeStamp: 0,
      type: '',
      data: {}
    },
    mailConfigId: Number(localStorage.getItem('mailConfigId') || 0),
  },
  mutations: {
    INIT: (state) => {
        // 已经有连接了，就不用再次创建
      if(state.$socket) {
        // 已关闭状态，销毁，然后重新创建
        if(!state.$socket._docker || state.$socket._docker.readyState === 3) {
          state.$socket = null
        }
      }
      if (state.$socket !== null) return
      // 创建
      let token = localStorage.getItem('socketToken')
      if(!token){
      systemSocket().then(res => {
        if(res.data.code === 0) {
          token = res.data.data.token
          localStorage.setItem('socketToken', token)
          resolve(true)
        } else {
          reject(false)
        }
      }).catch(e => {
        reject(e)
      })
      }
      state.$socket = new Socket(token)
      state.$socket.init()

      state.$socket.addMethods([(e) => {
        // console.error(e.data,'!!!!!!!!!!!!!!')
        if (e.data === 'pong') return
        if (e.type === 'close') {
          state.socketMsg = {
            timeStamp: e.timeStamp,
            type: e.type,
            data: 'socket已断开连接'
          }
          state.$socket = null
        }
        try {
          const parseData = JSON.parse(e.data)
          console.log(parseData.data,"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<5发送消息S")
          state.socketMsg = {
            timeStamp: e.timeStamp,
            type: parseData.type || e.type,
            data: parseData.data
          }
        } catch (error) {
          // console.error(error,"???????")
        }
      }])

      // console.info(state.socketMsg, 'debug socketMsg')
    },

    SET_MAILCONFIGID: (state, mailConfigId) => {
      state.mailConfigId = mailConfigId
    },
  },
  actions: {
    // 初始化
    WSINIT({ commit }) {
      commit('INIT')
    },
    // 断开
    DISCONNECT({ commit }) {
    },
    /**
     * 发送
     * @param data    {Object}  数据： 结构为{"type":"xxx","data":"xxx"}
     * */
    handleSend({ commit, state }, data) {
      if(!state.$socket) {
        commit('INIT')
      }
      if (state.$socket) {
        if(!state.$socket._docker || state.$socket._docker.readyState === 3) {
          state.$socket = null
          commit('INIT')
        }
        // if(state.$socket._docker.readyState)
        state.$socket.send(data)
      }
    },

    SetMailConfigId({commit}, mailConfigId) {
      commit('SET_MAILCONFIGID', mailConfigId)
    }
  }
}

export default socket
