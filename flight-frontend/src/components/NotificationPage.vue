<template>
  <el-card class="notification-layout-card">
    <el-container class="notification-container">

      <!-- 左侧区域 -->
      <el-aside width="200px" class="menu-aside">
        <!-- ★★★ 核心修改 1：重新使用 el-menu 来管理状态 ★★★ -->
        <!-- 我们用一个只含单项的菜单来模拟一个可激活的标题 -->
        <el-menu
          default-active="1"
          class="notification-menu">
          <el-menu-item index="1">
            <i class="el-icon-bell-solid"></i>
            <span slot="title">通知</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧内容区 (保持不变) -->
      <el-main class="content-main">
        <!-- 内容区的头部 -->
        <div class="content-header">
          <span class="content-title">全部通知 ({{ notifications.length }})</span>
          <el-button type="text" @click="goBack">
            <i class="el-icon-arrow-left"></i>
            返回上一页
          </el-button>
        </div>

        <!-- 通知表格 -->
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

    <!-- 详情弹窗 Dialog (保持不变) -->
    <el-dialog :title="currentNotificationDetails.title" :visible.sync="dialogVisible" width="60%">
      <div class="detail-content" v-if="currentNotificationDetails.id">
        <h4>公告内容：</h4>
        <p class="content-text">{{ currentNotificationDetails.content }}</p>
        <el-divider></el-divider>
        <h4>附件列表：</h4>
        <div v-if="currentNotificationDetails.attachments && currentNotificationDetails.attachments.length > 0">
          <ul class="attachment-list">
            <li v-for="file in currentNotificationDetails.attachments" :key="file.id">
              <el-link :href="file.filePath" type="primary" target="_blank" icon="el-icon-document">{{ file.fileName }}</el-link>
            </li>
          </ul>
        </div>
        <div v-else>
          <p>无附件</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>
import axios from 'axios';

// 脚本部分无需任何改动
export default {
  name: 'NotificationPage',
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
        const response = await axios.get('/announcements');
        this.notifications = response.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
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
      this.$router.go(-1);
    },
    formatDateTime(isoString) {
      if (!isoString) return '';
      const date = new Date(isoString);
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      const seconds = date.getSeconds().toString().padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }
  }
};
</script>

<style scoped>
/* 移除外层卡片的默认内边距，让布局容器占满它 */
.notification-layout-card >>> .el-card__body {
  padding: 0;
}
.notification-container {
  min-height: calc(100vh - 180px); 
}

/* 左侧菜单栏样式 */
.menu-aside {
  border-right: 1px solid #e6e6e6;
  background-color: #fafcfe;
  /* 为菜单项留出一点上下空间 */
  padding: 10px 0;
}

/* ★★★ 核心修改 2：定制化菜单和激活项的样式 ★★★ */
.notification-menu {
  /* 移除菜单默认的边框，因为 aside 已经有了 */
  border-right: none;
}
/* 设置菜单项的默认样式 */
.notification-menu .el-menu-item {
  font-size: 15px;
  font-weight: 500;
}
/* 关键！设置激活菜单项的样式 */
.notification-menu .el-menu-item.is-active {
  background-color: #409EFF !important; /* Element UI 的主蓝色 */
  color: #fff !important; /* 文字颜色变为白色 */
}
/* 同时确保激活时，内部的图标也变为白色 */
.notification-menu .el-menu-item.is-active i {
  color: #fff;
}
/* 提供一个柔和的悬停效果 (非激活状态下) */
.notification-menu .el-menu-item:not(.is-active):hover {
    background-color: #ecf5ff !important;
}


/* 右侧内容区样式 */
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