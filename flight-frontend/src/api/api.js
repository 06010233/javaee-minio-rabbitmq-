// 文件路径: src/api/api.js

import axios from 'axios';
import store from '@/store';

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
});

// 请求拦截器
instance.interceptors.request.use(
  config => {
    store.commit('SET_GLOBAL_LOADING', true);
    return config;
  },
  error => {
    store.commit('SET_GLOBAL_LOADING', false);
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  response => {
    store.commit('SET_GLOBAL_LOADING', false);
    return response.data;
  },
  error => {
    store.commit('SET_GLOBAL_LOADING', false);
    if (error.response) {
      console.error('请求错误', error.response.status, error.response.data);
    } else if (error.request) {
      console.error('请求已发出，但未收到响应', error.request);
    } else {
      console.error('请求设置出错', error.message);
    }
    return Promise.reject(error);
  }
);

export default {
  // --- 用户认证 ---
  login: (credentials) => instance.post('/auth/login', credentials),
  register: (userData) => instance.post('/auth/register', userData),
  logout: () => instance.post('/auth/logout'),
  getCurrentUserProfile: () => instance.get('/auth/profile'),
  validateResetRequest: (data) => instance.post('/auth/validate-reset-request', data),
  resetPassword: (data) => instance.post('/auth/reset-password', data),

  // --- 航班相关 ---
  getFlights: () => instance.get('/flights'),
  searchFlights: (params) => instance.get('/flights/search', { params }),
  getFlightDetails: (id) => instance.get(`/flights/${id}`),
  addFlight: (flightData) => instance.post('/flights', flightData),
  updateFlight: (id, flightData) => instance.put(`/flights/${id}`, flightData),
  deleteFlight: (id) => instance.delete(`/flights/${id}`),
  
  // --- 订单相关 ---
  createOrder: (orderData) => instance.post('/orders', orderData),
  getOrders: () => instance.get('/orders'),
  getOrdersByPassengerId: (passengerId) => instance.get(`/orders/passenger/${passengerId}`),
  getTripsByPassengerId: (passengerId) => instance.get(`/orders/trips/passenger/${passengerId}`),
  updateOrderStatus: (id, status) => instance.put(`/orders/${id}/status`, null, { params: { status } }),
  getRefundPreview: (id) => instance.get(`/orders/${id}/refund-preview`),
  getPaymentQrCode: (orderId) => instance.get(`/orders/${orderId}/payment-qrcode`),
  
  // --- 公告相关 ---
  getAnnouncements: () => instance.get('/announcements'), 

  // ★★★ 新增：获取用户日历行程的函数 ★★★
  getCalendarEventsForPassenger: (passengerId) => instance.get(`/orders/calendar/passenger/${passengerId}`),

  // --- 常用乘机人管理 ---
  getContacts: (userId) => instance.get(`/system-users/${userId}/contacts`),
  addContact: (userId, contactData) => instance.post(`/system-users/${userId}/contacts`, contactData),
  removeContact: (userId, passengerId) => instance.delete(`/system-users/${userId}/contacts/${passengerId}`),

  // --- 乘客相关 ---
  getPassengerById: (passengerId) => instance.get(`/passengers/${passengerId}`),
  addPassenger: (passengerData) => instance.post('/passengers', passengerData),
  updatePassenger: (id, passengerData) => instance.put(`/passengers/${id}`, passengerData),
  
  // --- 航空公司管理 ---
  getAirlines: () => instance.get('/airlines'),
  addAirline: (airline) => instance.post('/airlines', airline),
  updateAirline: (id, airline) => instance.put(`/airlines/${id}`, airline),
  deleteAirline: (id) => instance.delete(`/airlines/${id}`),
  
  // --- 机场管理 ---
  getAirports: () => instance.get('/airports'),
  addAirport: (airport) => instance.post('/airports', airport),
  updateAirport: (code, airport) => instance.put(`/airports/${code}`, airport),
  deleteAirport: (code) => instance.delete(`/airports/${code}`),

  // --- 航线模板管理 ---
  getFlightRoutes: () => instance.get('/flight-routes'),
  createFlightRoute: (routeData) => instance.post('/flight-routes', routeData),
  updateFlightRoute: (id, routeData) => instance.put(`/flight-routes/${id}`, routeData),
  deleteFlightRoute: (id) => instance.delete(`/flight-routes/${id}`),
  getFutureFlightsForRoute: (id) => instance.get(`/flight-routes/${id}/future-flights`),
  scheduleFlightsForRoute: (routeId, days) => instance.post(`/flight-routes/${routeId}/schedule`, { days }),

  // --- 用户管理 ---
  updateUserAccount: (id, userData) => instance.put(`/system-users/${id}`, userData),

  // --- 统计相关 ---
  getMonthlyRevenue: () => instance.get('/statistics/monthly-revenue'),
  getAirlineRevenue: () => instance.get('/statistics/airline-revenue'),

  // --- 附件/文件相关 (MinIO) ---
  uploadFile: (formData) => instance.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  getFilePreview: (fileName) => instance.get(`/files/preview/${fileName}`),

  // --- 日历可见航班的 API 函数 ---
  getVisibleFlightsForCalendar: (start, end) => instance.get('/flights/calendar', { 
      params: { start, end } 
  })
};