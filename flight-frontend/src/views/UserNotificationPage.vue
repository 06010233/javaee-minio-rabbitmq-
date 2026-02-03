<!-- 文件路径: src/views/UserNotificationPage.vue -->
<template>
  <el-card class="notification-layout-card">
    <el-container class="notification-container">
      <!-- 左侧区域 -->
      <el-aside width="200px" class="menu-aside">
        <el-menu default-active="1" class="notification-menu">
          <el-menu-item index="1">
            <i class="el-icon-bell-solid"></i>
            <span slot="title">通知</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <!-- 右侧内容区 -->
      <el-main class="content-main">
        <div class="content-header">
          <span class="content-title">全部通知 ({{ notifications.length }})</span>
          <el-button type="text" @click="goBack">
            <i class="el-icon-arrow-left"></i>
            返回上一页
          </el-button>
        </div>
        <el-table :data="notifications" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="标题" min-width="300"></el-table-column>
          <el-table-column label="发布时间" width="200">
            <template slot-scope="scope">
              {{ formatDateTime(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="center">
            <template slot-scope="scope">
              <el-button @click="showDetails(scope.row)" type="text" size="small">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
    <!-- 详情弹窗 Dialog -->
    <el-dialog :title="currentNotificationDetails.title" :visible.sync="dialogVisible" width="60%">
      <div class="detail-content" v-if="currentNotificationDetails.id">
        <h4>公告内容：</h4>
        <p class="content-text" v-html="currentNotificationDetails.content"></p>
        <el-divider v-if="currentNotificationDetails.attachments && currentNotificationDetails.attachments.length > 0"></el-divider>
        <h4 v-if="currentNotificationDetails.attachments && currentNotificationDetails.attachments.length > 0">附件列表：</h4>
        <div v-if="currentNotificationDetails.attachments && currentNotificationDetails.attachments.length > 0">
          <ul class="attachment-list">
            <li v-for="file in currentNotificationDetails.attachments" :key="file.id">
              <el-link :href="file.filePath" type="primary" target="_blank" icon="el-icon-document">{{ file.fileName }}</el-link>
            </li>
          </ul>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>
// ★★★ 核心修改 1: 导入我们封装的 api.js ★★★
import api from '@/api/api';
import moment from 'moment';

export default {
  name: 'UserNotificationPage',
  data() {
    return {
      notifications: [],
      loading: true,
      dialogVisible: false,
      currentNotificationDetails: {},
    };
  },
  created() {
    this.fetchNotifications();
  },
  methods: {
    async fetchNotifications() {
      this.loading = true;
      try {
        // ★★★ 核心修改 2: 使用 api.js 中的函数来获取数据 ★★★
        const responseData = await api.getAnnouncements();
        this.notifications = responseData.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      } catch (error) {
        this.$message.error('通知列表加载失败，请稍后再试。');
        console.error("Failed to fetch notifications:", error);
      } finally {
        this.loading = false;
      }
    },
    showDetails(row) {
      this.currentNotificationDetails = row;
      this.dialogVisible = true;
    },
    goBack() {
      // 优先返回上一页，如果历史记录太少则直接去用户中心
      if (window.history.length > 2) {
        this.$router.go(-1);
      } else {
        this.$router.push({ name: 'UserDashboard' });
      }
    },
    formatDateTime(isoString) {
      if (!isoString) return '';
      // 使用 moment.js 库来格式化，和您项目其他地方保持一致
      return moment(isoString).format('YYYY-MM-DD HH:mm:ss');
    }
  }
};
</script>

<style scoped>
/* (所有样式保持不变) */
.notification-layout-card >>> .el-card__body {
  padding: 0;
}
.notification-container {
  min-height: calc(100vh - 180px); 
}
.menu-aside {
  border-right: 1px solid #e6e6e6;
  background-color: #fafcfe;
  padding: 10px 0;
}
.notification-menu {
  border-right: none;
}
.notification-menu .el-menu-item {
  font-size: 15px;
  font-weight: 500;
}
.notification-menu .el-menu-item.is-active {
  background-color: #409EFF !important;
  color: #fff !important;
}
.notification-menu .el-menu-item.is-active i {
  color: #fff;
}
.notification-menu .el-menu-item:not(.is-active):hover {
    background-color: #ecf5ff !important;
}
.content-main {
  padding: 20px;
}
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e6e6e6;
}
.content-title {
  font-size: 18px;
  font-weight: 500;
}
.detail-content .content-text {
  white-space: pre-wrap;
  line-height: 1.6;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  color: #333;
}
.attachment-list {
  list-style: none;
  padding-left: 0;
}
.attachment-list li {
  margin-bottom: 8px;
}
</style>