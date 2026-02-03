// 文件路径: src/router/index.js

import Vue from 'vue';
import VueRouter from 'vue-router';
import store from '@/store';

// 页面组件
import Home from '@/views/Home.vue';
import UserLogin from '@/components/UserLogin.vue';

// 用户端组件
import UserDashboard from '@/views/UserDashboard.vue'; // 现在是 Layout
import UserOverview from '@/views/UserOverview.vue';   // 以前的 Dashboard 内容
import ContactManagement from '@/views/ContactManagement.vue';
import UserFlightCalendar from '@/views/UserFlightCalendar.vue';
import UserNotificationPage from '@/views/UserNotificationPage.vue';
import FlightSearch from '@/components/FlightSearch.vue';
import BookingForm from '@/components/BookingForm.vue';

// 管理员端组件 (保持不变)
import AdminDashboard from '@/views/AdminDashboard.vue';
import AirlineManagement from '@/components/AirlineManagement.vue';
import AirportManagement from '@/components/AirportManagement.vue';
import RouteManagement from '@/components/RouteManagement.vue';
import FileManagement from '@/components/FileManagement.vue'; 
import OrderStatistics from '@/components/OrderStatistics.vue'; 
import RevenueDashboard from '@/components/RevenueDashboard.vue'; 
import FlightCalendar from '@/components/FlightCalendar.vue';
import NotificationPage from '@/components/NotificationPage.vue';

Vue.use(VueRouter);

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: UserLogin, meta: { guestOnly: true } },

  // --- ★★★ 核心修改：用户端路由重构 ★★★ ---
  { 
    path: '/user', 
    component: UserDashboard, // 作为 Layout
    meta: { requiresAuth: true, requiresPassenger: true },
    children: [
      // 默认跳转到概览
      { path: '', redirect: 'overview' },
      { path: 'dashboard', redirect: 'overview' }, // 兼容旧链接
      
      { path: 'overview', name: 'UserOverview', component: UserOverview, meta: { title: '用户中心' } },
      { path: 'search', name: 'FlightSearch', component: FlightSearch, meta: { title: '航班搜索' } },
      { path: 'calendar', name: 'UserFlightCalendar', component: UserFlightCalendar, meta: { title: '飞行日历' } },
      { path: 'contacts', name: 'ContactManagement', component: ContactManagement, meta: { title: '常用乘机人' } },
      { path: 'notifications', name: 'UserNotificationPage', component: UserNotificationPage, meta: { title: '消息通知' } },
      // 订票页面也可以放在 Layout 里，或者单独出来，这里放在 Layout 里体验更好
      { path: 'booking/:flightId', name: 'BookingForm', component: BookingForm, props: true, meta: { title: '航班预订' } }
    ]
  },

  // 独立的航班搜索页 (如果没有登录，可以访问这个，或者直接重定向到登录)
  // 如果你想保留一个不需要登录也能看的搜索页，可以保留下面这个，但它不会有侧边栏
  { path: '/flight-search', name: 'PublicFlightSearch', component: FlightSearch },

  // --- 管理员端路由 (保持不变) ---
  { 
    path: '/admin/dashboard', 
    component: AdminDashboard, 
    meta: { requiresAuth: true, requiresAdmin: true },
    redirect: '/admin/dashboard/airlines', 
    children: [
      { path: 'airlines', name: 'AirlineManagement', component: AirlineManagement, meta: { title: '航空公司管理' } },
      { path: 'airports', name: 'AirportManagement', component: AirportManagement, meta: { title: '机场管理' } },
      { path: 'routes', name: 'RouteManagement', component: RouteManagement, meta: { title: '航线与排班' } },
      { path: 'files', name: 'FileManagement', component: FileManagement, meta: { title: '附件公告管理' } }, 
      { path: 'order-stats', name: 'OrderStatistics', component: OrderStatistics, meta: { title: '订单统计' } },
      { path: 'revenue-stats', name: 'RevenueDashboard', component: RevenueDashboard, meta: { title: '收益统计' } },
      { path: 'calendar', name: 'FlightCalendar', component: FlightCalendar, meta: { title: '航班排期日历' } },
      { path: 'notifications', name: 'NotificationPage', component: NotificationPage, meta: { title: '全部通知' } }
    ]
  },
  
  { path: '*', redirect: '/' }
];

const router = new VueRouter({
  mode: 'history',
  routes
});

// 路由守卫保持不变
router.beforeEach((to, from, next) => {
  const isAuthenticated = store.getters.isAuthenticated;
  const isAdmin = store.getters.isAdmin;
  const isPassenger = isAuthenticated && store.state.user.role === 'PASSENGER';

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      return next({ name: 'Login', query: { redirect: to.fullPath } });
    }
    if (to.matched.some(record => record.meta.requiresAdmin)) {
      if (isAdmin) return next();
      else return next({ path: '/user/overview' }); 
    }
    if (to.matched.some(record => record.meta.requiresPassenger)) {
      if (isPassenger) return next();
      else return next({ path: '/admin/dashboard' });
    }
  }
  if (to.matched.some(record => record.meta.guestOnly)) {
    if (isAuthenticated) {
      if (isAdmin) return next({ path: '/admin/dashboard' });
      else return next({ path: '/user/overview' });
    }
  }
  next();
});

export default router;