<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <!-- v-if 确保只有在非登录页、非全屏页才显示 -->
    <header v-if="showGlobalHeader">
      <nav>
        <div class="logo">
          <!-- 点击 Logo 回首页 -->
          <router-link to="/">航空订票系统</router-link>
        </div>
        
        <div class="nav-links">
          <!-- 首页链接 -->
          <router-link to="/" class="nav-item">首页</router-link>
          
          <!-- 航班搜索 -->
          <router-link to="/flight-search" class="nav-item">航班搜索</router-link>
          
          <!-- ★★★ 这里的逻辑就是你想要的 ★★★ -->
          <!-- 如果没登录：显示“登录”按钮 -->
          <template v-if="!isAuthenticated">
            <router-link to="/login" class="nav-item">登录</router-link>
          </template>
          
          <!-- 如果已登录：显示“进入控制台” -->
          <template v-else>
             <a @click="enterDashboard" class="nav-item" style="cursor: pointer">
               {{ isAdmin ? '管理后台' : '个人中心' }}
             </a>
          </template>
        </div>
      </nav>
    </header>

    <!-- 主体内容 -->
    <main :class="{ 'no-padding': !showGlobalHeader }">
      <router-view />
    </main>

    <!-- 底部信息 -->
    <footer v-if="showGlobalHeader">
      <p>© 2025 航空订票系统 | 客服热线: 400-123-4567</p>
    </footer>
    
    <!-- 全局加载状态 -->
    <div v-if="globalLoading" class="global-loader">
      <div class="spinner"></div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';

export default {
  name: 'App',
  computed: {
    ...mapGetters(['isAuthenticated', 'isAdmin']),
    ...mapState(['globalLoading']),
    
    // ★★★ 确保首页显示导航栏的核心逻辑 ★★★
    showGlobalHeader() {
      const path = this.$route.path;
      
      // 1. 如果是登录页，绝对不显示导航栏（因为是全屏视频）
      if (path === '/login') return false; 
      
      // 2. 如果是后台管理 (/admin) 或 用户中心 (/user)，通常有侧边栏，也不显示顶部栏
      // (如果你希望用户中心也显示顶部栏，可以把 && !path.startsWith('/user') 去掉)
      if (path.startsWith('/admin') || path.startsWith('/user')) return false;

      // 3. 其他情况（包括首页 '/'），显示导航栏
      return true;
    }
  },
  methods: {
    enterDashboard() {
      if (this.isAdmin) this.$router.push('/admin/dashboard');
      else this.$router.push('/user/overview'); // 或者是 /user/home
    }
  }
};
</script>

<style>
/* 样式保持不变 */
#app { font-family: 'Helvetica Neue', Arial, sans-serif; color: #2c3e50; min-height: 100vh; display: flex; flex-direction: column; }
header { background: linear-gradient(135deg, #1a73e8, #0d47a1); color: white; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); position: sticky; top: 0; z-index: 100; }
nav { display: flex; justify-content: space-between; align-items: center; max-width: 1200px; margin: 0 auto; padding: 15px 20px; }
.logo a { font-size: 1.5rem; font-weight: bold; color: white; text-decoration: none; }
.nav-links { display: flex; gap: 25px; }
.nav-item { color: rgba(255, 255, 255, 0.85); text-decoration: none; font-weight: 500; padding: 8px 0; cursor: pointer; }
.nav-item:hover { color: white; }
main { flex: 1; width: 100%; }
main.no-padding { max-width: none; margin: 0; padding: 0; }
main:not(.no-padding) { max-width: 1200px; margin: 0 auto; }
footer { background: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #eaeaea; }
.global-loader { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(255, 255, 255, 0.8); display: flex; justify-content: center; align-items: center; z-index: 9999; }
.spinner { width: 50px; height: 50px; border: 5px solid rgba(26, 115, 232, 0.2); border-top: 5px solid #1a73e8; border-radius: 50%; animation: spin 1s linear infinite; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>