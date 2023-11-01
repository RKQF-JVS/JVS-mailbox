import request from "@/router/axios";
// 修改用户
export function editUser(data) {
    return request({
      url: `/mgr/jvs-auth/user/update`,
      method: "put",
      data: data
    });
  }