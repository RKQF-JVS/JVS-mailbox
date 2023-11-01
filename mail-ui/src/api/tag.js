import request from '@/router/axios'
const base = `/mgr/mail`


//查询当前邮箱下的所有标签
export const getTagmailAll = (configId)=>{
  return request({
    url:`${base}/mail/tag/all/${configId}`,
    method:'get',
  })
}
//新增标签
export const tagmailSave = (data)=>{
  return request({
    url:`${base}/mail/tag/save`,
    method:'post',
    data
  })
}
//删除标签
export const tagmailDel = (id)=>{
  return request({
    url:`${base}/mail/tag/del/${id}`,
    method:"delete"
  })
}
//编辑标签
export const tagmailEdit = (data)=>{
  return request({
    url:`${base}/mail/tag/edit`,
    method:"put",
    data
  })
}
//邮件标签绑定
export const tagbindingMail = (data)=>{
  return request({
    url:`${base}/mail/tag/binding/${data.mailId}/${data.tagId}`,
    method:"put",
    data
  })
}

