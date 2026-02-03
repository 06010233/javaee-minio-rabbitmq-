// 完整文件： store/index.js

import Vue from 'vue';
import Vuex from 'vuex';
import api from '@/api/api';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    // 从 localStorage 初始化用户信息，防止刷新页面丢失登录状态
    user: JSON.parse(localStorage.getItem('user')) || null,
    selectedFlight: null,
    globalLoading: false
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user;
      // 同步保存到 localStorage
      localStorage.setItem('user', JSON.stringify(user));
    },
    CLEAR_USER(state) {
      state.user = null;
      localStorage.removeItem('user');
    },
    // LOGOUT_USER 功能与 CLEAR_USER 类似，保留以兼容旧代码调用
    LOGOUT_USER(state) {
      state.user = null;
      localStorage.removeItem('user');
    },
    SET_SELECTED_FLIGHT(state, flight) {
      state.selectedFlight = flight;
    },
    SET_GLOBAL_LOADING(state, isLoading) {
      state.globalLoading = isLoading;
    }
  },
  actions: {
    // 登录 Action
    async login({ commit }, credentials) {
      commit('SET_GLOBAL_LOADING', true);
      try {
        const user = await api.login(credentials);
        commit('SET_USER', user);
        return user;
      } catch (error) {
        throw error;
      } finally {
        commit('SET_GLOBAL_LOADING', false);
      }
    },
    
    // 登出 Action
    async logout({ commit }) {
      commit('SET_GLOBAL_LOADING', true);
      try {
        await api.logout();
      } catch (error) {
        console.error('调用后端登出接口失败:', error);
      } finally {
        // 无论后端是否成功，前端都清除状态
        commit('CLEAR_USER'); 
        commit('SET_GLOBAL_LOADING', false);
      }
    },
    
    // ★★★ 核心修复：刷新用户信息 ★★★
    // 用于在用户修改资料或支付后，同步最新的状态（如消费金额、余额等）
    async refreshUser({ commit }) {
      try {
        const freshUser = await api.getCurrentUserProfile();
        commit('SET_USER', freshUser);
        return freshUser;
      } catch (error) {
        console.error("刷新用户信息失败:", error);
        // 关键改动：不再清除本地用户信息，而是将错误向上抛出。
        // 让调用者（如路由守卫或组件）根据具体的错误场景决定下一步操作。
        // commit('CLEAR_USER'); // <--- 已移除此行，防止网络抖动导致下线
        throw error; 
      }
    },
    
    // 选择航班（用于从搜索页跳转到预订页传递数据）
    selectFlight({ commit }, flight) {
      commit('SET_SELECTED_FLIGHT', flight);
    },
    
    // 创建订单
    async createOrder({ commit }, orderData) {
      commit('SET_GLOBAL_LOADING', true);
      try {
        const order = await api.createOrder(orderData);
        return order;
      } catch (error) {
        throw error;
      } finally {
        commit('SET_GLOBAL_LOADING', false);
      }
    },
    
    // 获取航空公司列表
    async fetchAirlines({ commit }) {
      commit('SET_GLOBAL_LOADING', true);
      try {
        const airlines = await api.getAirlines();
        return airlines;
      } catch (error) {
        throw error;
      } finally {
        commit('SET_GLOBAL_LOADING', false);
      }
    }
  },
  getters: {
    isAuthenticated: state => state.user !== null,
    // 判断是否为管理员
    isAdmin: state => state.user && state.user.role === 'ADMIN',
    // 判断是否为乘客
    isPassenger: state => state.user && state.user.role === 'PASSENGER',
    // 获取当前用户信息
    currentUser: state => state.user
  }
});