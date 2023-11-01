<template>
  <basic-container>
    <div class="outer-container">
      <iframe v-if="$route.query.src" :src="src" class="iframe" ref="iframe" style="height: 100%;"></iframe>
      <iframe v-else :src="urlPath" class="iframe" ref="iframe" style="height: 100%;"></iframe>
    </div>
  </basic-container>
</template>

<script>
import { mapGetters } from "vuex";
import { getStore } from "@/util/store.js";
import NProgress from "nprogress"; // progress bar
import "nprogress/nprogress.css"; // progress bar style
export default {
  name: "home",
  data() {
    return {
      urlPath: this.getUrlPath(), //iframe src 路径
      src: ""
    };
  },
  created() {
    NProgress.configure({ showSpinner: false });
  },
  mounted() {
    this.load();
    this.resize();
  },
  props: ["routerPath"],
  watch: {
    $route: function() {
      this.load();
    },
    routerPath: function() {
      // 监听routerPath变化，改变src路径
      this.urlPath = this.getUrlPath();
    }
  },
  components: {
    ...mapGetters(["screen"])
  },
  methods: {
    created() {},
    // 显示等待框
    show() {
      NProgress.start();
    },
    // 隐藏等待狂
    hide() {
      NProgress.done();
    },
    // 加载浏览器窗口变化自适应
    resize() {
      window.onresize = () => {
        this.iframeInit();
      };
    },
    // 加载组件
    load() {
      this.show();
      var flag = true; //URL是否包含问号
      if (this.$route.query.src.indexOf("?") == -1) {
        flag = false;
      }
      var list = [];
      for (var key in this.$route.query) {
        if (key != "src" && key != "name") {
          list.push(`${key}= this.$route.query[key]`);
        }
      }
      list = list.join("&").toString();
      if (flag) {
        this.$route.query.src = `${this.$route.query.src}${
          list.length > 0 ? `&list` : ""
        }`;
      } else {
        this.$route.query.src = `${this.$route.query.src}${
          list.length > 0 ? `?list` : ""
        }`;
      }
      const token = getStore({
        name: "access_token",
        debug: true
      });
      if(this.$route.query.src.indexOf('?') != -1){
         this.src = this.$route.query.src;
      }else{
         this.src = this.$route.query.src;
      }
      //超时5s自动隐藏等待框，加强用户体验
      let time = 5;
      const timeFunc = setInterval(() => {
        time--;
        if (time == 0) {
          this.hide();
          clearInterval(timeFunc);
        }
      }, 1000);
      this.iframeInit();
    },
    //iframe窗口初始化
    iframeInit() {
      const iframe = this.$refs.iframe;
      const clientHeight =
        document.documentElement.clientHeight - (screen > 1 ? 200 : 130);
      // iframe.style.height = `${clientHeight}px`
      iframe.style.height = `100%`;
      if (iframe.attachEvent) {
        iframe.attachEvent("onload", () => {
          this.hide();
        });
      } else {
        iframe.onload = () => {
          this.hide();
        };
      }
    },
    getUrlPath: function() {
      //获取 iframe src 路径
      const token = getStore({
        name: "access_token",
        debug: true
      });
      //请求携带token
      let url = window.location.href;
      //  + "%3Ftoken%3D"+token.content;
      url = url.replace("/myiframe", "");
      return url;
    }
  }
};
</script>

<style lang="scss">
.iframe {
  height: 100%;
  width: 100%;
  border: none;
  position: absolute;
  right: -20px;
  top: 0;
  bottom: 0;
  overflow-x: hidden;
  overflow-y: scroll;
}
.outer-container {
  position: relative;
  // overflow: hidden;
  width: 100%;
  height: 900px;
}
</style>
