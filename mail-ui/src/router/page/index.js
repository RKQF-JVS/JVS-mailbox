export default [{
  path: '/login',
  name: '登录页',
  component: () =>
    import ( /* webpackChunkName: "page" */ '@/views/login/index'),
  meta: {
    keepAlive: true,
    isTab: false,
    isAuth: false
  }
},
	{
        path: '/404',
        component: () =>
            import ( /* webpackChunkName: "page" */ '@/components/error-page/404'),
        name: '404',
        meta: {
            keepAlive: true,
            isTab: false,
            isAuth: false
        }
    }
]
