import request from '@/router/axios'

// 列表
export const getPageList = (params) => {
  return request({
      url: `/mgr/page/design/page`,
      method: 'get',
      params: params
  })
}

// 新建页面
export const createPage = (params) => {
  return request({
      url: `/mgr/page/design/create`,
      method: 'get',
      params: params
  })
}

//获取所有系统列表
export const getAllSystemList = (params) => {
  return request({
    url: `/mgr/upms/system/all`,
    method: "get",
    params: params
  });
};

// 部署
export const deployPage = (data) => {
  return request({
      url: `/mgr/page/design/deploy/${data.id}`,
      method: 'post',
      data: data
  })
}

// 删除
export const deletePage = (id) => {
  return request({
      url: `/mgr/page/design/del/${id}`,
      method: 'delete'
  })
}

// 卸载菜单
export const unloadPage = (id) => {
  return request({
      url: `/mgr/page/design/unload/${id}`,
      method: 'post'
  })
}

// 修改页面
export const editPage = (data) => {
  return request({
      url: `/mgr/page/design/rename`,
      method: 'put',
      data: data
  })
}

// 分类字典列表
export const getClassifyDict = (params) => {
  return request({
    url: `/mgr/upms/api/tree/list`,
    method: "get",
    params: params
  });
};

// 根据唯一名称查询字典,返回字段树
export const getClassifyDictTree = (uniqueName) => {
  return request({
    url: `/mgr/upms/api/tree/${uniqueName}`,
    method: "get"
  });
};

// 所有字典
export const getSystemDict = (params) => {
  return request({
    url: `/mgr/upms/api/dict/api/dict/list/dicts`,
    method: "get",
    params: params
  });
};

// 所有字典项
export const getSystemDictItems = (uniqId) => {
  return request({
    url: `/mgr/upms/api/dict/api/dict/list/items`,
    method: "get",
    params: {uniqId: uniqId}
  });
};