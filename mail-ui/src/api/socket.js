import { dicConfig } from "@/const/const"
import {getSysSocketToken} from "@/store/modules/user";
let wsbaseurlHear='ws://'

// const wsUrl = 'ws://10.0.0.98:8088/send'
// const wsUrl = wsbaseurlHear+'10.0.0.125:10000/mgr/mail/websocket'
// const wsUrl = wsbaseurlHear+'10.0.1.141:10000/mgr/mail/websocket'
// const wsUrl = wsbaseurlHear+'10.0.0.249:10059/mgr/mail/websocket'
// const wsUrl = wsbaseurlHear+'10.0.0.249:10059/websocket'
const wsUrl = wsbaseurlHear+'10.0.0.38:10000/mgr/mail/websocket'

// const wsUrl = dicConfig.socketURL

export default class Socket {
  constructor(token) {
    this.token = token
    if(!token) {
      return
    }
    this._docker = new WebSocket(`${wsUrl}?token=${token}`)
    this._methods = []

    this.isConntedPromise = new Promise(r => {
      this.resolveIsConntedPromise = r
    })
  }

  async init() {
    // console.log(this._docker,"11111111111???????????????????")
    this._docker.binaryType = 'arraybuffer'
    this._docker.onopen = event => {
      if(this._docker.readyState!==1) return
      this.resolveIsConntedPromise()
      // console.log('debug socket 连接成功',this._docker.readyState)
      this._docker.send('ping')
      this._docker.send(`{"id":1,"msg":"你好！"}`);
      this._sendBeat()
    }
    this._docker.onmessage = event => {
      // console.log('debug socket 收到消息', event, event.data)
      this._dockeronMessage(event)
    }
    this._docker.onerror = event => {
      // console.log('debug socket 连接失败')
      this.reconnect()
    }
    this._docker.onclose = event => {
      // const msg = {
      //   type: 'disconnect',
      //   data: event
      // }
      this._call(event)
      console.log(`socket已经关闭...`)
      this.reconnect()
    }
  }
  async _dockeronMessage(event) {
    this._call(event)
  }

  addMethods(fns) {
    this._methods = fns
  }

  // 重连
  reconnect() {
    if (this._timer) {
      clearInterval(this._timer)
      this._timer = null
    }
    getSysSocketToken().then(result => {
      if(result) {
        const newSocketToken = localStorage.getItem('socketToken')
        this._docker = new WebSocket(`${wsUrl}?token=${newSocketToken}`)
        this.init()
      }
    })
  }

  /**
   * 发送信息
   * @param data    {String}  数据： 结构为 json字符串，"{"type":"xxx","data":"xxx"}"
   */
  async send(data) {
    await this.isConntedPromise;
    // console.log(this._docker)
    this._docker.send(JSON.stringify(data))
  }

  // 关闭 socket
  close() {
    // 清除定时脚本
    if (this._timer) {
      clearInterval(this._timer)
      this._timer = null
    }
  }

  _call(...args) {
    for (let i = 0, l = this._methods.length; i < l; i++) {
      const fn = this._methods[i]
      if (typeof fn !== 'function') continue
      fn.apply(null, args)
    }
  }

  // 发送心跳包，表明连接激活
  _sendBeat() {
    this._timer = setInterval(() => {
      // this._docker.send('send Beat')
      this._docker.send(`{"id":1,"msg":"send Beat"}`);
      this._docker.send(`ping`)
    }, 30 * 1000)
  }
}
