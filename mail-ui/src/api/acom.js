import request from "@/router/axios";
const BASE_PATH = '/mgr/message-push'

//标记全部已读
export const noticeAll = (data)=>{
    return request({
        url:`${BASE_PATH}/station/inside/notice/all/read`,
        method:'get',
        params:data
    })
}
//查询所有
export const noticeList = (data)=>{
    return request({
        url:`${BASE_PATH}/station/inside/notice/list`,
        method:'get',
        params:data
    })
}
//查看详情
export const noticeDetail = (id)=>{
    return request({
        url:`${BASE_PATH}/station/inside/notice/${id}/detail`,
        method:'get'
    })
}
//分页查询
export const noticePage = (data)=>{
    return request({
      url:`${BASE_PATH}/station/inside/notice/page`,
      method:'get',
      params:data
    })
}
//删除消息
export const noticeDel = (all)=>{
    return request({
        url:`${BASE_PATH}/station/inside/notice/del/${all}`,
        method:'delete',
    })
}
//删除单条消息
export const noticeDelone = (id)=>{
    return request({
        url:`${BASE_PATH}/station/inside/notice/del/single/${id}`,
        method:'delete',
    })
}