import request from '@/router/axios'

// 用户列表
export function getUserList(query) {
  return request({
    url: "/mgr/upms/usermanager/list",
    method: "get",
    params: query
  });
}

// 获取用户的部门菜单
export function getDeptList() {
  return request({
    url: "/mgr/upms/dept/list",
    method: "get",
  });
}

// 获取全部角色信息
export function getRoleList() {
  return request({
    url: "/mgr/upms/role/list",
    method: "GET"
  });
}

// 获取职位列表
export function getPostList() {
  return request({
    url: "/mgr/upms/job/list",
    method: "GET"
  });
}

// 数据源下拉框
export function getDataSourceInfoList(data) {
  return request({
    url: "/mgr/page/datasource/dataSelect",
    method: "post",
    data: data
  });
}