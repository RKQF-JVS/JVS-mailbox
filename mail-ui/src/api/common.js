import request from '@/router/axios'

// 公共的字典转换接口
export function byKeyDicData(url){
    return request({
      url:url,
      method:'get'
    })
  }