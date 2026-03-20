import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import {request} from "./utils/request.js"
Vue.prototype.request=request
import './uni.promisify.adaptor'
import './static/css/global.css'//引入自定义的样式
Vue.config.productionTip = false
App.mpType = 'app'
// 定义全局变量
uni.baseUrl = 'http://localhost:9090/';
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif