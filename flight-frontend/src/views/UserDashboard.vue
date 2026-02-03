<!-- 文件路径: src/views/UserDashboard.vue -->
<template>
  <el-container class="user-dashboard-layout">
    <!-- 侧边栏 -->
    <el-aside :width="collapsed ? '64px' : '220px'" class="sidebar-container">
      <div class="logo">
        <i class="el-icon-s-promotion"></i>
        <h1 v-if="!collapsed">航空订票系统</h1>
      </div>
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        :collapse="collapsed"
        :collapse-transition="false"
        router
      >
        <el-menu-item index="/user/overview">
          <i class="el-icon-s-home"></i>
          <span slot="title">用户中心</span>
        </el-menu-item>
        
        <el-menu-item index="/user/search">
          <i class="el-icon-search"></i>
          <span slot="title">航班搜索</span>
        </el-menu-item>
        
        <el-menu-item index="/user/calendar">
          <i class="el-icon-date"></i>
          <span slot="title">飞行日历</span>
        </el-menu-item>
        
        <el-menu-item index="/user/contacts">
          <i class="el-icon-user"></i>
          <span slot="title">常用乘机人</span>
        </el-menu-item>

        <el-menu-item index="/user/notifications">
          <i class="el-icon-bell"></i>
          <span slot="title">消息通知</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧主体 -->
    <el-container>
      <!-- 顶部 Header -->
      <el-header class="header-container">
        <div class="header-left">
          <i :class="collapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'" class="trigger" @click="collapsed = !collapsed"></i>
          <span class="breadcrumb-text">{{ currentRouteName }}</span>
        </div>
        <div class="header-right">
          <span class="welcome-text">欢迎您, {{ user ? user.username : '乘客' }}</span>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link avatar-wrapper">
              <el-avatar icon="el-icon-user-solid" size="small"></el-avatar>
              <i class="el-icon-caret-bottom"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="home">返回首页</el-dropdown-item>
              <el-dropdown-item divided command="logout" style="color: #F56C6C;">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 主内容区：子路由渲染在这里 -->
      <el-main class="content-main">
        <transition name="fade-transform" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'UserDashboard',
  data() {
    return {
      collapsed: false
    };
  },
  computed: {
    ...mapState(['user']),
    currentRouteName() {
      return this.$route.meta.title || '用户中心';
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.logout();
      } else if (command === 'home') {
        this.$router.push('/');
      }
    },
    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('logout').then(() => {
          this.$router.push('/login');
        });
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.user-dashboard-layout { height: 100vh; }
.sidebar-container { background-color: #304156; transition: width 0.28s; overflow-x: hidden; }
.logo { height: 60px; line-height: 60px; background-color: #2b2f3a; color: #fff; text-align: center; font-size: 20px; font-weight: 600; display: flex; align-items: center; justify-content: center; gap: 10px; overflow: hidden;}
.logo i { font-size: 24px; color: #409EFF; }
.logo h1 { font-size: 18px; margin: 0; white-space: nowrap; color: white;}
.el-menu { border-right: none; }
.header-container { background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; justify-content: space-between; align-items: center; padding: 0 20px; height: 60px; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.trigger { font-size: 20px; cursor: pointer; transition: all .3s; }
.trigger:hover { color: #409EFF; }
.breadcrumb-text { margin-left: 15px; font-size: 16px; color: #303133; font-weight: 500; }
.header-right { display: flex; align-items: center; gap: 15px; }
.welcome-text { font-size: 14px; color: #606266; }
.avatar-wrapper { cursor: pointer; display: flex; align-items: center; gap: 5px; }
.content-main { background-color: #f0f2f5; padding: 20px; }

/* 简单的淡入淡出动画 */
.fade-transform-enter-active, .fade-transform-leave-active { transition: all .3s; }
.fade-transform-enter { opacity: 0; transform: translateX(-10px); }
.fade-transform-leave-to { opacity: 0; transform: translateX(10px); }
</style>