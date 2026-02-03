<template>
  <el-container class="admin-dashboard-layout">
    <!-- 左侧侧边栏 -->
    <el-aside :width="collapsed ? '64px' : '200px'" class="sidebar-container">
      <div class="logo">
        <i class="el-icon-s-platform"></i>
        <h1 v-if="!collapsed">管理后台</h1>
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
        <el-menu-item index="/admin/dashboard/airlines">
          <i class="el-icon-s-promotion"></i>
          <span slot="title">航空公司管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/dashboard/airports">
          <i class="el-icon-office-building"></i>
          <span slot="title">机场管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/dashboard/routes">
          <i class="el-icon-s-cooperation"></i>
          <span slot="title">航线与排班</span>
        </el-menu-item>
        <el-menu-item index="/admin/dashboard/files">
          <i class="el-icon-document-add"></i>
          <span slot="title">附件/公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/dashboard/order-stats">
          <i class="el-icon-s-order"></i>
          <span slot="title">订单统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/dashboard/revenue-stats">
          <i class="el-icon-s-data"></i>
          <span slot="title">收益统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/dashboard/calendar">
          <i class="el-icon-date"></i>
          <span slot="title">飞行日历</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区域 -->
    <el-container>
      <el-header class="header-container">
        <div class="header-left">
          <i :class="collapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'" class="trigger" @click="collapsed = !collapsed"></i>
        </div>
        <div class="header-right">
          <div class="notification-wrapper" @click="goToNotifications" title="查看全部通知">
            <i class="el-icon-bell"></i>
          </div>
          
          <span>欢迎, {{ currentUser }}</span>
          <el-button type="danger" size="mini" icon="el-icon-switch-button" @click="logout">
            退出
          </el-button>
        </div>
      </el-header>
      
      <el-main class="content-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      collapsed: false,
    };
  },
  computed: {
    ...mapState(['user']),
    currentUser() {
      return this.user ? this.user.username : '管理员';
    },
  },
  methods: {
    goToNotifications() {
      if (this.$route.path !== '/admin/dashboard/notifications') {
        this.$router.push('/admin/dashboard/notifications');
      }
    },
    logout() {
      this.$confirm('您确定要退出管理后台吗？', '确认退出', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('logout').then(() => {
          this.$router.push('/login');
        });
      }).catch(() => {
        // 用户点击了取消
      });
    }
  }
};
</script>

<style scoped>
.admin-dashboard-layout {
  height: 100vh;
}
.content-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
.sidebar-container {
  background-color: #304156;
  transition: width 0.28s;
}
.logo {
  height: 60px;
  background-color: #2b2f3a;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  gap: 10px;
  font-size: 16px;
  overflow: hidden;
  white-space: nowrap;
}
.logo i { font-size: 24px; }
.logo h1 { color: white; margin: 0; font-size: 18px; font-weight: 600; }
.el-menu { border-right: none; }
.el-menu-vertical:not(.el-menu--collapse) { width: 200px; }
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f2f5;
  background-color: #fff;
  padding: 0 20px;
}
.trigger { font-size: 22px; cursor: pointer; transition: color 0.3s; }
.trigger:hover { color: #409EFF; }
.header-right { display: flex; align-items: center; gap: 16px; }

.notification-wrapper {
  width: 32px;
  height: 32px;
  background-color: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: opacity 0.3s;
}
.notification-wrapper:hover {
  opacity: 0.8;
}
.notification-wrapper .el-icon-bell {
  color: white;
  font-size: 18px;
}
</style>