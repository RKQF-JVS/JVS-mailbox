/**
 * 配置该文件可以参考:
 * https://cli.vuejs.org/zh/config/#%E7%9B%AE%E6%A0%87%E6%B5%8F%E8%A7%88%E5%99%A8
 *
 */
const url = "http://gateway:10000";
const domainUrl = 'http://gateway:10000'
// 基础路径，发布前修改这里,当前配置打包出来的资源都是相对路径
const pathName ="mail-ui" // fixme 请修改为自己的前端项目名称
const path = require("path");

function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = {
  //项目路径地址
  publicPath: process.env.NODE_ENV !== 'development' ? '/' : ('/' +pathName),
  //静态资源文件地址
  assetsDir: pathName + '/static',
  //静太资源文件
  indexPath: pathName + '/index.html',

  lintOnSave: true,
  productionSourceMap: false,
  chainWebpack: config => {
    // 忽略的打包文件
    config.externals({
      'axios': 'axios'
    })
    config.module
      .rule('images')
      .use('url-loader')
      .loader('url-loader')
      .tap(options => Object.assign(options, { limit: 2000, esModule: false })); // 配置线上图片转base64。 app 已有做浏览器缓存。转换过大，会加大首屏加载时长
    // svg
    config.module.rules.delete("svg");
    config.module.rule("svg").exclude.add(resolve("src/icons")).end();
    config.module
      .rule("icons")
      .test(/\.svg$/)
      .include.add(resolve("src/icons"))
      .end()
      .use("svg-sprite-loader")
      .loader("svg-sprite-loader")
      .options({
        symbolId: "icon-[name]",
      })
      .end();
    const entry = config.entry('app')
    entry
      .add('babel-polyfill')
      .end()
    entry
      .add('classlist-polyfill')
      .end()
  },
  // 配置转发代理
  devServer: {
    proxy: {
      '/mgr': {
        target: url,
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/mgr':  '/mgr'
        }
      },
      '/api': {
        target: domainUrl,
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/api':  '/api'
        }
      },
      '/auth': {
        target: url,
        ws: true,
        pathRewrite: {
          '^/auth': '/auth'
        }
      },
      "^/agreement": {
        target: url,
        ws: true,
        pathRewrite: {
          "^/agreement": "/agreement"
        }
      },
    }
  },
  css: {
    loaderOptions: {
      // 给 sass-loader 传递选项
      sass: {
        // @/ 是 src/ 的别名
        // 注意：在 sass-loader v8 中，这个选项名是 "prependData"  v8以下使用 additionalData
        prependData: `@import "~@/styles/themes/index.scss"`
      },
      // 默认情况下 `sass` 选项会同时对 `sass` 和 `scss` 语法同时生效
      // 因为 `scss` 语法在内部也是由 sass-loader 处理的
      // 但是在配置 `prependData` 选项的时候
      // `scss` 语法会要求语句结尾必须有分号，`sass` 则要求必须没有分号
      // 在这种情况下，我们可以使用 `scss` 选项，对 `scss` 语法进行单独配置
      scss: {
        prependData: `@import "~@/styles/themes/index.scss";`
      },
    }
  }
}
