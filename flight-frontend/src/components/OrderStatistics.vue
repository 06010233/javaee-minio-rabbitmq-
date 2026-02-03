<template>
  <div class="order-statistics">
    <h2>订单统计</h2>
    <!-- 使用 Element UI 的 el-tabs 组件 -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="未支付订单" name="PENDING_PAYMENT">
        <!-- OrderTable 子组件保持不变 -->
        <OrderTable :orders="pendingOrders" :loading="loading" />
      </el-tab-pane>
      <el-tab-pane label="已支付/已出票订单" name="PAID">
        <OrderTable :orders="paidOrders" :loading="loading" />
      </el-tab-pane>
      <el-tab-pane label="已取消订单" name="CANCELLED">
        <OrderTable :orders="cancelledOrders" :loading="loading" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import api from '@/api/api';
import OrderTable from './OrderTable.vue';

export default {
  name: 'OrderStatistics',
  components: { 
    OrderTable 
  },
  data() {
    return {
      allOrders: [],
      loading: false,
      // 为 Element UI 的 el-tabs 添加 activeTab 数据
      activeTab: 'PENDING_PAYMENT',
    };
  },
  computed: {
    pendingOrders() {
      return this.allOrders.filter(o => o.status === 'PENDING_PAYMENT');
    },
    paidOrders() {
      return this.allOrders.filter(o => ['PAID', 'TICKETED', 'COMPLETED'].includes(o.status));
    },
    cancelledOrders() {
      return this.allOrders.filter(o => ['CANCELLED', 'REFUNDED'].includes(o.status));
    },
  },
  mounted() {
    this.fetchOrders();
  },
  methods: {
    async fetchOrders() {
      this.loading = true;
      try {
        this.allOrders = await api.getOrders();
      } catch (error) {
        if (error.request && !error.response) {
            // this.$message 是由 Element UI 提供的，所以这里可以正常工作
            this.$message.error('网络错误或无法连接到服务器，请检查后端服务是否正在运行。');
        } else {
            this.$message.error('订单数据加载失败');
        }
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.order-statistics { 
  padding: 24px; 
}
</style>