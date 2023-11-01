import request from '@/router/axios'
import config from '../../package.json'
import { client_id } from "@/const/const"

const base = '/mgr/mail'
const baseTask = `/mgr/task-engine`
let version = config.version.split('.').join('')
// 发送本地路由
export const sendRouter= (data) => {
    return request({
        url: `${base}/router/list`,
        method: 'post',
        data: data,
        headers: {
            client: 'mail-ui', // fixme 请修改为当前的项目名称
            name: encodeURI('邮件'), // fixme 请修改为当前的项目中文名称
            version: version,
        }
    })
}

// 根据jti 获取token
export const getToken = (str) => {
    return request({
        url: `/auth/token/convert/` + str,
        method: 'get',
    })
}

// 获取下拉列表
export const getSelectData = (str) => {
    return request({
        url: str,
        method: 'get',
    })
  }

// task 转换文件下载地址
export function getFileUrl(query) {
  return request({
    url: `${baseTask}/base/getFileUrl`,
    method: 'get',
    params: query
  })
}