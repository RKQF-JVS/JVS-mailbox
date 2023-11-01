/*
 * @Author: Xiuer
 * @Date: 2021-07-20
 * @Description:
 */
import request from '@/router/axios'

const baseRoot = `/mgr`
const base = `/mgr/mail/`

// 修改密码
export function checkPassword(data) {
  return request({
    url: `${baseRoot}/upms/user/password`,
    method: "post",
    data
  });
}

// 获取用户信息
export function getUser() {
  return request({
    url: `${base}/system/getUser`,
    method: 'get'
  })
}


// 退出系统
export function quit() {
  return request({
    url: `/system/quit`,
    method: 'get'
  })
}

// 邮件上传图片
export function uploadFile(data) {
  return request({
    url: `${base}/base/imgUpload`,
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 邮件上传图片
export function fileUpload(data) {
  return request({
    url: `${base}/base/fileUpload`,
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}


// 获取 Certificate
export function getCertificate(params) {
  return request({
    url: `${base}/base/getCertificate`,
    method: 'get',
    params
  })
}

// 转换文件下载地址
export function getFileUrlLarger(bucketName, fileName) {
  return request({
    url: `${base}/base/getFileUrl?url=${bucketName}_${fileName}`,
    method: 'get',
  })
}

// 获取sockettoken
export function systemSocket() {
  return request({
    url: `/mgr/mail//system/socket/1`,
    method: 'get',
  })
}

// 查询邮箱
export function queryEmail(query) {
  return request({
    url: `${base}/system/queryEmail`,
    method: 'get',
    params: query
  })
}

