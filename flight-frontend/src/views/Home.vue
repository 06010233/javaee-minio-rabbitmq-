<!-- 文件路径: src/views/Home.vue -->
<template>
  <div class="home-container">
    <!-- 1. 巨幕区域 (Hero Section) -->
    <div class="hero-section">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="animate-up">探索世界，从这里起飞</h1>
        <p class="animate-up delay-1">为您提供安全、舒适、便捷的航空出行体验</p>
        <div class="hero-actions animate-up delay-2">
          <el-button type="primary" icon="el-icon-search" class="cta-btn" @click="$router.push('/flight-search')">
            立即搜索航班
          </el-button>
          <el-button v-if="!isAuthenticated" plain class="cta-btn-outline" @click="$router.push('/login')">
            登录 / 注册
          </el-button>
          <el-button v-else plain class="cta-btn-outline" @click="goToDashboard">
            进入个人中心
          </el-button>
        </div>
      </div>
    </div>

    <!-- 2. 服务特性 (Features) -->
    <div class="features-section">
      <el-row :gutter="40" type="flex" justify="center">
        <el-col :xs="24" :sm="8" :md="6">
          <div class="feature-card">
            <div class="icon-box"><i class="el-icon-s-promotion"></i></div>
            <h3>全球航线</h3>
            <p>覆盖全球热门目的地，想去哪里就去哪里。</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8" :md="6">
          <div class="feature-card">
            <div class="icon-box"><i class="el-icon-date"></i></div>
            <h3>极速预订</h3>
            <p>实时航班动态，一键下单，出行无忧。</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8" :md="6">
          <div class="feature-card">
            <div class="icon-box"><i class="el-icon-service"></i></div>
            <h3>贴心服务</h3>
            <p>24小时客服支持，退改签流程透明便捷。</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 3. 热门推荐 (示例展示) -->
    <div class="promotion-section">
      <h2 class="section-title">热门目的地</h2>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="promo-card large" style="background-image: url('https://images.unsplash.com/photo-1504609773096-104ff2c73ba4?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80')">
            <div class="promo-content">
              <h3>探索北京</h3>
              <p>感受千年古都的魅力</p>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="promo-card large" style="background-image: url('https://images.unsplash.com/photo-1477959858617-67f85cf4f1df?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80')">
            <div class="promo-content">
              <h3>漫游上海</h3>
              <p>体验现代都市的繁华</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'Home',
  computed: {
    ...mapGetters(['isAuthenticated', 'isAdmin', 'isPassenger'])
  },
  methods: {
    goToDashboard() {
      if (this.isAdmin) {
        this.$router.push('/admin/dashboard');
      } else {
        this.$router.push('/user/dashboard');
      }
    }
  }
}
</script>

<style scoped>
.home-container {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  background-color: #fff;
}

/* --- Hero Section --- */
.hero-section {
  position: relative;
  height: 600px;
  background-image: url('https://images.unsplash.com/photo-1436491865332-7a61a109cc05?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80'); /* 飞机起飞的高清图 */
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #fff;
  overflow: hidden;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4); /* 黑色遮罩，让文字更清晰 */
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  padding: 0 20px;
}

.hero-content h1 {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: 2px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
}

.hero-content p {
  font-size: 1.5rem;
  margin-bottom: 40px;
  font-weight: 300;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.cta-btn {
  padding: 15px 40px;
  font-size: 18px;
  border-radius: 50px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4);
  transition: transform 0.3s;
}

.cta-btn:hover {
  transform: translateY(-3px);
}

.cta-btn-outline {
  padding: 15px 40px;
  font-size: 18px;
  border-radius: 50px;
  background: transparent;
  color: #fff;
  border: 2px solid #fff;
  font-weight: 600;
  transition: all 0.3s;
}

.cta-btn-outline:hover {
  background: #fff;
  color: #333;
}

/* --- Features Section --- */
.features-section {
  padding: 80px 20px;
  background-color: #f9f9f9;
}

.feature-card {
  background: #fff;
  padding: 40px 20px;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid #eee;
  height: 100%;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.1);
  border-color: transparent;
}

.icon-box {
  width: 70px;
  height: 70px;
  background: #ecf5ff;
  color: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 32px;
}

.feature-card h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

/* --- Promotion Section --- */
.promotion-section {
  padding: 80px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  text-align: center;
  font-size: 32px;
  color: #333;
  margin-bottom: 50px;
  font-weight: 600;
  position: relative;
}

.section-title::after {
  content: '';
  display: block;
  width: 60px;
  height: 4px;
  background: #409EFF;
  margin: 15px auto 0;
  border-radius: 2px;
}

.promo-card {
  height: 300px;
  border-radius: 12px;
  background-size: cover;
  background-position: center;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.promo-card:hover {
  transform: scale(1.02);
}

.promo-content {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 20px;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  color: #fff;
}

.promo-content h3 {
  font-size: 24px;
  margin: 0 0 5px;
}

.promo-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

/* --- Animations --- */
.animate-up {
  opacity: 0;
  transform: translateY(30px);
  animation: fadeUp 0.8s forwards ease-out;
}

.delay-1 { animation-delay: 0.2s; }
.delay-2 { animation-delay: 0.4s; }

@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .hero-content h1 { font-size: 2.5rem; }
  .hero-actions { flex-direction: column; }
  .promo-card { margin-bottom: 20px; }
}
</style>