<!-- components/UserOnline.vue -->
<template>
  <div class="user-online-container">
    <div class="header">
      <h1><i class="fas fa-users"></i> 系统用户管理</h1>
      <div class="controls">
        <div class="search-box">
          <input type="text" v-model="searchQuery" placeholder="搜索用户名、姓名或角色..." @input="handleSearch" />
          <i class="fas fa-search"></i>
        </div>
        <button class="refresh-btn" @click="fetchUsers">
          <i class="fas fa-sync-alt"></i> 刷新数据
        </button>
      </div>
    </div>

    <div class="stats-cards">
      <div class="stat-card" v-for="stat in userStats" :key="stat.title">
        <div class="icon" :style="{ backgroundColor: stat.color }">
          <i :class="stat.icon"></i>
        </div>
        <div class="info">
          <h3>{{ stat.title }}</h3>
          <p>{{ stat.value }}</p>
        </div>
      </div>
    </div>

    <div class="table-container">
      <table class="user-table">
        <thead>
          <tr>
            <th @click="sortBy('userId')">用户ID <i :class="sortIcon('userId')"></i></th>
            <th @click="sortBy('username')">用户名 <i :class="sortIcon('username')"></i></th>
            <th @click="sortBy('fullName')">姓名 <i :class="sortIcon('fullName')"></i></th>
            <th @click="sortBy('role')">角色 <i :class="sortIcon('role')"></i></th>
            <th @click="sortBy('lastLogin')">最后登录 <i :class="sortIcon('lastLogin')"></i></th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="7" class="no-data">正在加载...</td>
          </tr>
          <tr v-for="user in paginatedUsers" :key="user.userId">
            <td>{{ user.userId }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.fullName || '-' }}</td>
            <td>
              <span :class="roleClass(user.role)">{{ roleText(user.role) }}</span>
            </td>
            <td>{{ formatDate(user.lastLogin) }}</td>
            <td>
              <span class="status" :class="statusClass(user.lastLogin)">{{ statusText(user.lastLogin) }}</span>
            </td>
            <td>
              <button class="action-btn view" @click="viewUserDetails(user)"><i class="fas fa-eye"></i></button>
              <button class="action-btn edit" @click="editUser(user)"><i class="fas fa-edit"></i></button>
            </td>
          </tr>
          <tr v-if="!loading && filteredUsers.length === 0">
            <td colspan="7" class="no-data">未找到匹配的用户</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button @click="prevPage" :disabled="currentPage === 1"><i class="fas fa-chevron-left"></i></button>
      <span v-for="page in pages" :key="page" @click="goToPage(page)" :class="{ active: page === currentPage }">
        {{ page }}
      </span>
      <button @click="nextPage" :disabled="currentPage === totalPages"><i class="fas fa-chevron-right"></i></button>
    </div>

    <!-- 用户详情模态框 -->
    <div class="modal" v-if="selectedUser">
      <div class="modal-content">
        <div class="modal-header">
          <h2>用户详情</h2>
          <button class="close-btn" @click="selectedUser = null"><i class="fas fa-times"></i></button>
        </div>
        <div class="modal-body">
          <div class="user-details">
            <div class="detail-row"><label>用户ID：</label><span>{{ selectedUser.userId }}</span></div>
            <div class="detail-row"><label>用户名：</label><span>{{ selectedUser.username }}</span></div>
            <div class="detail-row"><label>姓名：</label><span>{{ selectedUser.fullName || '-' }}</span></div>
            <div class="detail-row"><label>角色：</label><span :class="roleClass(selectedUser.role)">{{ roleText(selectedUser.role) }}</span></div>
            <div class="detail-row"><label>最后登录时间：</label><span>{{ formatDate(selectedUser.lastLogin) }}</span></div>
            <div class="detail-row"><label>当前状态：</label><span class="status" :class="statusClass(selectedUser.lastLogin)">{{ statusText(selectedUser.lastLogin) }}</span></div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn close" @click="selectedUser = null">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';

export default {
  name: 'UserOnline',
  data() {
    return {
      users: [],
      searchQuery: '',
      sortField: 'lastLogin',
      sortDirection: 'desc',
      currentPage: 1,
      itemsPerPage: 8,
      selectedUser: null,
      loading: false
    };
  },
  computed: {
    userStats() {
      const total = this.users.length;
      const online = this.users.filter(u => this.isOnline(u.lastLogin)).length;
      const admins = this.users.filter(u => u.role === 'ADMIN').length;
      const passengers = this.users.filter(u => u.role === 'PASSENGER').length;
      return [
        { title: '总用户数', value: total, icon: 'fas fa-users', color: '#3498db' },
        { title: '乘客数', value: passengers, icon: 'fas fa-user', color: '#2ecc71' },
        { title: '管理员数', value: admins, icon: 'fas fa-user-shield', color: '#9b59b6' },
        { title: '在线用户', value: online, icon: 'fas fa-user-check', color: '#e74c3c' },
      ];
    },
    filteredUsers() {
      const query = this.searchQuery.toLowerCase();
      if (!query) return this.users;
      return this.users.filter(user =>
        (user.username && user.username.toLowerCase().includes(query)) ||
        (user.fullName && user.fullName.toLowerCase().includes(query)) ||
        (user.role && this.roleText(user.role).toLowerCase().includes(query))
      );
    },
    sortedUsers() {
      return [...this.filteredUsers].sort((a, b) => {
        let fieldA = a[this.sortField];
        let fieldB = b[this.sortField];
        if (fieldA === null || fieldA === undefined) fieldA = this.sortDirection === 'asc' ? 'zzz' : '';
        if (fieldB === null || fieldB === undefined) fieldB = this.sortDirection === 'asc' ? 'zzz' : '';

        let modifier = this.sortDirection === 'asc' ? 1 : -1;
        if (fieldA < fieldB) return -1 * modifier;
        if (fieldA > fieldB) return 1 * modifier;
        return 0;
      });
    },
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      return this.sortedUsers.slice(start, start + this.itemsPerPage);
    },
    totalPages() {
      return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    },
    pages() {
      const pages = [];
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
      return pages;
    }
  },
  mounted() {
    this.fetchUsers();
  },
  methods: {
    async fetchUsers() {
      this.loading = true;
      try {
        this.users = await api.getUsers();
      } catch (error) {
        this.$message.error('获取用户数据失败');
        console.error('获取用户数据失败:', error);
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
        this.currentPage = 1;
    },
    formatDate(date) {
      if (!date) return '从未登录';
      return new Date(date).toLocaleString('zh-CN', { timeZone: 'Asia/Shanghai' });
    },
    sortBy(field) {
      this.sortDirection = (this.sortField === field && this.sortDirection === 'asc') ? 'desc' : 'asc';
      this.sortField = field;
    },
    sortIcon(field) {
      if (this.sortField !== field) return 'fas fa-sort';
      return this.sortDirection === 'asc' ? 'fas fa-sort-up' : 'fas fa-sort-down';
    },
    roleText(role) {
      const roles = { ADMIN: '管理员', PASSENGER: '乘客' };
      return roles[role] || role;
    },
    roleClass(role) {
      return { 
        'role-admin': role === 'ADMIN', 
        'role-passenger': role === 'PASSENGER' 
      };
    },
    isOnline(lastLogin) {
      if (!lastLogin) return false;
      const diffInMinutes = (new Date() - new Date(lastLogin)) / 60000;
      return diffInMinutes < 30;
    },
    statusText(lastLogin) {
      return this.isOnline(lastLogin) ? '在线' : '离线';
    },
    statusClass(lastLogin) {
      return { online: this.isOnline(lastLogin), offline: !this.isOnline(lastLogin) };
    },
    viewUserDetails(user) {
      this.selectedUser = user;
    },
    editUser(user) {
      this.$message.info(`编辑用户: ${user.username} (功能待开发)`);
    },
    prevPage() { if (this.currentPage > 1) this.currentPage--; },
    nextPage() { if (this.currentPage < this.totalPages) this.currentPage++; },
    goToPage(page) { this.currentPage = page; }
  }
};
</script>

<style scoped>
.user-online-container { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 1200px; margin: 0 auto; padding: 20px; background: #f8f9fa; border-radius: 10px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; padding-bottom: 15px; border-bottom: 1px solid #eaeaea; }
.header h1 { color: #2c3e50; font-weight: 600; display: flex; align-items: center; gap: 10px; margin: 0; }
.controls { display: flex; gap: 15px; align-items: center; }
.search-box { position: relative; display: flex; align-items: center; }
.search-box input { padding: 10px 15px 10px 35px; border: 1px solid #ddd; border-radius: 4px; width: 250px; font-size: 14px; transition: all 0.3s; }
.search-box input:focus { border-color: #3498db; outline: none; box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1); }
.search-box i { position: absolute; left: 12px; color: #95a5a6; }
.refresh-btn { background: #3498db; color: white; border: none; padding: 10px 15px; border-radius: 4px; cursor: pointer; font-weight: 500; display: flex; align-items: center; gap: 8px; transition: background 0.3s; }
.refresh-btn:hover { background: #2980b9; }
.stats-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 20px; margin-bottom: 25px; }
.stat-card { background: white; border-radius: 8px; padding: 20px; display: flex; align-items: center; gap: 20px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05); transition: transform 0.3s; }
.stat-card:hover { transform: translateY(-3px); box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }
.stat-card .icon { width: 60px; height: 60px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-size: 24px; }
.stat-card .info h3 { margin: 0 0 5px 0; font-size: 16px; color: #7f8c8d; font-weight: 500; }
.stat-card .info p { margin: 0; font-size: 24px; font-weight: 700; color: #2c3e50; }
.table-container { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03); margin-bottom: 25px; }
.user-table { width: 100%; border-collapse: collapse; }
.user-table th, .user-table td { padding: 15px 20px; text-align: left; }
.user-table th { background-color: #f1f5f9; color: #34495e; font-weight: 600; cursor: pointer; transition: background 0.2s; position: relative; }
.user-table th:hover { background-color: #e2e8f0; }
.user-table th i { position: absolute; right: 10px; }
.user-table tbody tr { border-bottom: 1px solid #edf2f7; transition: background 0.2s; }
.user-table tbody tr:hover { background-color: #f8fafc; }
.user-table td .status { padding: 5px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.user-table td .online { background-color: #e8f7f0; color: #27ae60; }
.user-table td .offline { background-color: #fceaea; color: #e74c3c; }
.role-admin { background-color: #f3e8fd; color: #8e44ad; padding: 4px 8px; border-radius: 4px; font-weight: 500; font-size: 13px; }
.role-passenger { background-color: #e3f2fd; color: #2980b9; padding: 4px 8px; border-radius: 4px; font-weight: 500; font-size: 13px; }
.action-btn { border: none; border-radius: 4px; padding: 6px 10px; margin-right: 8px; cursor: pointer; transition: all 0.2s; color: white; font-size: 14px; }
.action-btn.view { background: #3498db; }
.action-btn.edit { background: #2ecc71; }
.action-btn:hover { opacity: 0.9; transform: scale(1.05); }
.no-data { text-align: center; padding: 30px; color: #95a5a6; font-style: italic; }
.pagination { display: flex; justify-content: center; align-items: center; gap: 5px; margin-top: 20px; }
.pagination button { background: #3498db; color: white; border: none; width: 36px; height: 36px; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.pagination button:disabled { background: #bdc3c7; cursor: not-allowed; }
.pagination span { width: 36px; height: 36px; display: flex; align-items: center; justify-content: center; border-radius: 50%; cursor: pointer; font-weight: 500; }
.pagination span:hover { background: #e0f7fa; }
.pagination span.active { background: #3498db; color: white; }
.modal { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: white; border-radius: 10px; width: 500px; max-width: 90%; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15); overflow: hidden; }
.modal-header { padding: 20px; background: #3498db; color: white; display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.close-btn { background: none; border: none; color: white; font-size: 20px; cursor: pointer; padding: 5px; }
.modal-body { padding: 25px; }
.user-details .detail-row { display: flex; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #f0f4f8; }
.user-details .detail-row:last-child { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
.user-details label { font-weight: 600; color: #2c3e50; width: 150px; }
.user-details span { flex: 1; color: #34495e; }
.modal-footer { padding: 15px 20px; background: #f8fafc; display: flex; justify-content: flex-end; }
.btn { padding: 8px 20px; border-radius: 4px; border: none; cursor: pointer; font-weight: 500; transition: all 0.2s; }
.btn.close { background: #95a5a6; color: white; }
.btn.close:hover { background: #7f8c8d; }
</style>