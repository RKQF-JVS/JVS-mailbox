export default [
  {
    path: '/',
    name: '主页',
    redirect: '/mail'
  },
  {
    path: "/mail",
    name: "邮箱",
    component: () =>
      import(/* webpackChunkName: "page" */ "@/views/mail/index"),
    meta: {
      keepAlive: true,
      isTab: false,
      isAuth: false
    }
  },
  {
    path: "/demo",
    name: "案例",
    component: () =>
      import(/* webpackChunkName: "page" */ "@/views/demo/index"),
    meta: {
      keepAlive: true,
      isTab: false,
      isAuth: false
    }
  },
  {
    path: '/user',
      name: "个人中心",
      component: () =>import(/* webpackChunkName: "page" */ "@/views/user/index"),
      meta: {
        keepAlive: true,
        isTab: false,
        isAuth: false
      }
    // }]
  },
];
