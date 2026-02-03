// main.js
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'

// --- 关键修改：移除 Ant Design Vue，引入 Element UI ---
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI)
// --- Element UI 引入结束 ---

// --- 保留：引入 ECharts ---
import * as echarts from 'echarts';
import VChart from 'vue-echarts';

// 全局注册 ECharts 组件
Vue.component('v-chart', VChart);
Vue.prototype.$echarts = echarts;
// --- ECharts 引入结束 ---

// 设置 Axios 基础配置
// 注意：您的组件中请求的是 'http://localhost:8080/api'，
// 如果您的所有后端接口都在8080端口，建议将这里也统一，或者在组件中直接使用相对路径/api
axios.defaults.baseURL = 'http://localhost:8080/api'

// 全局样式
import './assets/styles/main.css'

// --- 已移除 Ant Design Vue ---
// import Antd from 'ant-design-vue'
// import 'ant-design-vue/dist/antd.css'
// Vue.use(Antd)

// 引入 Moment.js 用于日期处理
import moment from 'moment'
Vue.prototype.$moment = moment

// 全局配置
Vue.config.productionTip = false

// 全局错误处理
Vue.config.errorHandler = (err, vm, info) => {
  console.error(`Error: ${err.toString()}\nInfo: ${info}`)
}

// 创建 Vue 实例
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')