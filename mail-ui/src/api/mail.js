import request from '@/router/axios'

const base = `/mgr/mail/`

// 获取邮件分组信息
export function getQueryGroup(query) {
  return request({
    url: `${base}/mail/business/queryGroup`,
    method: 'get',
    params: query
  })
}

// 创建 邮件分组
export function addGroup(query) {
  return request({
    url: `${base}/mail/business/addGroup`,
    method: 'get',
    params: query
  })
}

// 创建 邮件分组
export function editGroup(data) {
  return request({
    url: `${base}/mail/business/group/rename`,
    method: 'put',
    data: data
  })
}

// 删除分组
export function delGroup(query) {
  return request({
    url: `${base}/mail/business/delGroup`,
    method: 'get',
    params: query
  })
}

// 发送邮件
export function sendMail(data) {
  return request({
    url: `${base}/mail/business/sendMail`,
    method: 'post',
    data
  })
}

// 保存邮件
export function saveDraft(data) {
  return request({
    url: `${base}/mail/business/saveDraft`,
    method: 'post',
    data
  })
}

// 获取当前用户邮箱
export function getSenderMail(query) {
  return request({
    url: `${base}/mail/business/getSenderMail`,
    method: 'get',
    params: query
  })
}

// 获取个性签名
export function getSignature() {
  return request({
    url: `${base}/mail/signature/getSignature`,
    method: 'get'
  })
}

// 添加个性签名
export function saveSignature(data) {
  return request({
    url: `${base}/mail/signature/save`,
    method: 'post',
    data
  })
}

// 编辑签名个性签名
export function editSignature(data) {
  return request({
    url: `${base}/mail/signature/editSignature`,
    method: 'post',
    data
  })
}

// 删除签名
export function delSignature(data) {
  return request({
    url: `${base}/mail/signature/del`,
    method: 'post',
    data
  })
}

// 保存签名
export function setDefault(query) {
  return request({
    url: `${base}/mail/signature/setDefault`,
    method: 'get',
    params: query
  })
}

// 获取邮件分组信息
export function queryMailGroup(query) {
  return request({
    url: `${base}/mail/business/queryGroup`,
    method: 'get',
    params: query
  })
}

// 获取邮件
export function getMailList(query) {
  return request({
    url: `${base}/mail/business/getMail`,
    method: 'get',
    params: query
  })
}

// 获取邮件详情
export function getMailDetails(query) {
  return request({
    url: `${base}/mail/business/mailDetails`,
    method: 'get',
    params: query
  })
}

// 标记邮件
// 标记 0 已读  1 未读  2 红旗 3 取消红旗
export function postTagMail(data) {
  return request({
    url: `${base}/mail/business/tagMail`,
    method: 'post',
    data
  })
}

// 删除邮件
export function delMail(data) {
  return request({
    url: `${base}/mail/business/delMail`,
    method: 'post',
    data
  })
}

// 彻底删除邮件
export function deleteMail(data) {
  return request({
    url: `${base}/mail/business/deleteMail`,
    method: 'delete',
    data
  })
}

// 移动邮件
export function moveMail(data) {
  return request({
    url: `${base}/mail/business/moveMail`,
    method: 'post',
    data
  })
}

// 全部标记已读
export function tagAllRead() {
  return request({
    url: `${base}/mail/business/tagAllRead`,
    method: 'get'
  })
}

// 获取邮件分组信息
export function queryGroup() {
  return request({
    url: `${base}/mail/group/queryGroup`,
    method: 'get'
  })
}

// 新增分组或修改分组名称
export function saveOrUpdateGroup(data) {
  return request({
    url: `${base}/mail/group/saveOrUpdateGroup`,
    method: 'post',
    data
  })
}

// 删除分组
export function delGroupItem(id) {
  return request({
    url: `${base}/mail/group/delGroup?id=${id}`,
    method: 'get'
  })
}

// 分组 查邮箱列表
export function getEmailByGroupId(id) {
  return request({
    url: `${base}/mail/group/getEmailByGroupId?id=${id}`,
    method: 'get'
  })
}

// 未分组 查邮箱列表
export function getEmailByUnGroup() {
  return request({
    url: `${base}/mail/group/getEmailByGroupId`,
    method: 'get'
  })
}

// 添加到某个组
export function unGroupAdd(data) {
  return request({
    url: `${base}/mail/group/unGroupAdd`,
    method: 'post',
    data
  })
}

// 拉取邮箱信息
export function acceptMail() {
  return request({
    url: `${base}/mail/business/acceptMail`,
    method: 'get'
  })
}

// 信任邮件
export function trustMaill(id) {
  return request({
    url: `${base}/mail/business/trustMail?id=${id}`,
    method: 'get'
  })
}

// 保存当前用户邮件配置
export function saveMailConfig(data) {
  return request({
    url: `${base}/mail/business/saveMailConfig`,
    method: 'post',
    data
  })
}

// 修改当前用户邮件配置
export function updateConfig(data) {
  return request({
    url: `${base}/mail/business/updateConfig`,
    method: 'post',
    data
  })
}

// 保存当前用户邮件配置
export function updateConfigState(query) {
  return request({
    url: `${base}/mail/business/updateConfigState`,
    method: 'get',
    params: query
  })
}


// 判断是否为第一次登录
export function isFirstLogin() {
  return request({
    url: `${base}/mail/business/isFirstLogin`,
    method: 'get'
  })
}

// 用户列表
export function getUserMailList() {
  return request({
    url: `${base}/mail/business/getUserMailList`,
    method: 'get'
  })
}

// 用户配置列表
export function getMailConfigList() {
  return request({
    url: `${base}/mail/business/getMailConfigList`,
    method: 'get'
  })
}

// 删除用户配置邮箱
export function delConfigById(id) {
  return request({
    url: `${base}/mail/business/delConfigById?id=${id}`,
    method: 'get'
  })
}

// 查询某个邮箱未读数量
export function queryUnreadCount(query) {
  return request({
    url: `${base}/mail/business/queryUnreadCount`,
    method: 'get',
    params: query
  })
}

// 确认 已读回执 状态
export function updateIsReceipt(query) {
  return request({
    url: `${base}/mail/business/updateIsReceipt`,
    method: 'get',
    params: query
  })
}

// 拉取附件
export function getMailAccessory(query) {
  return request({
    url: `${base}/mail/business/getMailAccessory`,
    method: 'get',
    params: query
  })
}

// 开启-关闭消息提醒
export const businessRemind = (id,remindStatus) => {
  return request({
      url: `${base}/mail/business/remind/${id}/${remindStatus}`,
      method: 'put',
  })
}

// 绑定标签
export const bindTag = (mailId,tagId) => {
  return request({
      url: `${base}/mail/tag/binding/${mailId}/${tagId}`,
      method: 'put',
  })
}


// 标签解绑
export const unbindTag = (mailId) => {
  return request({
      url: `${base}/mail/tag/unbind/${mailId}`,
      method: 'put',
  })
}
