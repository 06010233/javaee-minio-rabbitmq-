<!-- 文件路径: src/views/UserOverview.vue -->
<template>
  <div class="user-overview">
    <div v-loading="loading" element-loading-text="正在加载用户数据..." class="dashboard-content">
      <div v-if="!loading" class="dashboard-layout">
        <!-- 左侧：个人信息卡片 -->
        <div class="left-column">
          <div class="dashboard-card user-info-card">
            <div class="card-header">
              <h2><i class="el-icon-user-solid"></i> 个人信息</h2>
              <el-button type="text" @click="showEditModal"><i class="el-icon-edit"></i> 编辑资料</el-button>
            </div>
            <div class="card-body">
              <div class="user-profile">
                <div class="profile-avatar"><i class="el-icon-user"></i></div>
                <div class="profile-info">
                  <h3>{{ user ? user.username : '加载中...' }} ({{ passenger.name }})</h3>
                  <p>ID: {{ passenger.idCard }}</p>
                  <span class="membership-badge" :class="membershipClass"><i :class="membershipIcon"></i> {{ membershipText }}</span>
                </div>
              </div>
              <div class="profile-details">
                <div class="detail-item"><div class="label">累计消费</div><div class="value">¥{{ passenger.totalSpent ? passenger.totalSpent.toFixed(2) : '0.00' }}</div></div>
                <div class="detail-item"><div class="label">联系电话</div><div class="value">{{ passenger.phone }}</div></div>
                <div class="detail-item"><div class="label">电子邮箱</div><div class="value">{{ passenger.email || '未设置' }}</div></div>
                <div class="detail-item"><div class="label">教育程度</div><div class="value">{{ educationText(passenger.education) }}</div></div>
                <div class="detail-item"><div class="label">职业</div><div class="value">{{ passenger.occupation || '未设置' }}</div></div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧：行程列表 -->
        <div class="right-column">
          <!-- 待出行行程 -->
          <div class="dashboard-card trip-list-card">
            <div class="card-header">
              <h2><i class="el-icon-suitcase"></i> 我的行程 (待出行)</h2>
            </div>
            <div class="card-body">
              <el-empty v-if="trips.length === 0" description="您还没有任何待出行的行程"></el-empty>
              <div v-else class="order-list">
                <div class="order-item" v-for="trip in trips" :key="'trip-' + trip.orderId">
                  <div class="order-header">
                    <span class="order-id">订单号: {{ trip.orderId }}</span>
                    <span class="order-status" :class="getStatusClass(trip)">{{ getStatusText(trip) }}</span>
                  </div>
                  <div class="order-body trip-body">
                    <!-- 增加 v-if 判断，防止航班被删导致报错 -->
                    <div class="flight-info" v-if="trip.flight">
                      <div class="flight-number"><i class="el-icon-position"></i> {{ trip.flight.flightNumber }}</div>
                      <div class="route">{{ trip.flight.departureAirport.city }} → {{ trip.flight.arrivalAirport.city }}</div>
                      <div class="flight-time-detailed"><i class="el-icon-time"></i> {{ formatDateTime(trip.flight.departureTime) }}</div>
                    </div>
                    <!-- 如果航班数据丢失，显示提示 -->
                    <div class="flight-info" v-else>
                      <div class="flight-number" style="color: #909399;"><i class="el-icon-warning-outline"></i> 航班信息已失效</div>
                      <div class="route" style="font-size: 12px; color: #C0C4CC;">该航班可能已被调整或取消</div>
                    </div>

                    <div class="passenger-details">
                      <div><span class="label">乘机人:</span> {{ trip.ticketPassenger ? trip.ticketPassenger.name : '未知' }}</div>
                      <div><span class="label">证件号:</span> {{ trip.ticketPassenger ? trip.ticketPassenger.idCard : '未知' }}</div>
                    </div>
                    <div class="seat-price-info">
                      <div><span class="label">舱位:</span> {{ formatSeatClass(trip.seatClass) }}</div>
                      <div class="price">¥{{ trip.actualPrice.toFixed(2) }}</div>
                    </div>
                  </div>
                  <div class="order-actions">
                    <template v-if="isRefundable(trip)">
                      <el-button type="danger" plain @click="handleCancelOrRefund(trip, 'refund')" size="mini">申请退票</el-button>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 全部订单记录 -->
          <div class="dashboard-card booking-list-card">
            <div class="card-header">
              <h2><i class="el-icon-tickets"></i> 全部预订记录</h2>
            </div>
            <div class="card-body">
              <el-empty v-if="bookings.length === 0" description="您还没有任何预订记录"></el-empty>
              <div v-else class="order-list">
                <div class="order-item" v-for="booking in bookings" :key="'booking-' + booking.orderId">
                  <div class="order-header">
                    <div class="order-id-time">
                      <span class="order-id">订单号: {{ booking.orderId }}</span>
                      <span class="order-date">下单时间: {{ formatDate(booking.orderTime) }}</span>
                    </div>
                    <span class="order-status" :class="getStatusClass(booking)">{{ getStatusText(booking) }}</span>
                  </div>
                  <div class="order-body">
                     <!-- 增加 v-if 判断 -->
                     <div class="flight-info" v-if="booking.flight">
                      <div class="flight-number"><i class="el-icon-position"></i> {{ booking.flight.flightNumber }}</div>
                      <div class="route">{{ booking.flight.departureAirport.city }} → {{ booking.flight.arrivalAirport.city }}</div>
                    </div>
                    <div class="flight-info" v-else>
                      <div class="flight-number" style="color: #909399;">航班信息已失效</div>
                    </div>

                    <div class="passenger-info">
                      <div><span class="label">乘机人:</span> {{ booking.ticketPassenger ? booking.ticketPassenger.name : '未知' }}</div>
                      <div class="price-container">
                        <span class="label">金额:</span> 
                        <span class="price">¥{{ booking.actualPrice.toFixed(2) }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="order-actions">
                     <template v-if="booking.status === 'PENDING_PAYMENT'">
                      <el-button type="danger" plain @click="handleCancelOrRefund(booking, 'cancel')" size="mini">取消订单</el-button>
                      <el-button type="primary" @click="handlePay(booking)" size="mini">去支付</el-button>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 支付弹窗 -->
    <el-dialog title="扫码支付" :visible.sync="paymentModalVisible" width="380px" center @close="closePaymentModal">
      <div class="payment-modal-content">
        <p>请使用微信或支付宝扫描下方二维码完成支付。</p>
        <div class="qr-code-container">
          <qrcode-vue v-if="qrCodeUrl" :value="qrCodeUrl" :size="220"></qrcode-vue>
          <div v-else class="qr-loading">二维码生成中...</div>
        </div>
        <p class="payment-amount">支付金额: <span class="amount">¥{{ paymentAmount.toFixed(2) }}</span></p>
        <p class="order-info">订单号: {{ currentPayingOrderId }}</p>
      </div>
    </el-dialog>
    
    <!-- 编辑资料弹窗 -->
    <el-dialog title="编辑个人资料" :visible.sync="editModalVisible" width="500px" @close="handleEditCancel" :destroy-on-close="true">
        <el-form :model="editFormModel" :rules="editFormRules" ref="editFormRef" label-width="120px" label-position="top">
            <el-divider>账户信息</el-divider>
            <el-form-item label="用户名" prop="username"><el-input v-model="editFormModel.username" placeholder="登录时使用的用户名"></el-input></el-form-item>
            <el-form-item label="新密码 (不修改请留空)" prop="password"><el-input v-model="editFormModel.password" type="password" show-password placeholder="输入新密码，至少6位"></el-input></el-form-item>
            <el-divider>乘客资料</el-divider>
            <el-form-item label="联系电话" prop="phone"><el-input v-model="editFormModel.phone" placeholder="请输入11位手机号"></el-input></el-form-item>
            <el-form-item label="电子邮箱" prop="email"><el-input v-model="editFormModel.email" placeholder="可选，用于接收行程信息"></el-input></el-form-item>
            <el-form-item label="教育程度" prop="education">
                <el-select v-model="editFormModel.education" placeholder="请选择您的教育程度" style="width: 100%;"><el-option label="不设置" :value="null"></el-option><el-option label="小学" value="PRIMARY"></el-option><el-option label="初中" value="JUNIOR"></el-option><el-option label="高中" value="HIGH"></el-option><el-option label="大专" value="COLLEGE"></el-option><el-option label="本科" value="BACHELOR"></el-option><el-option label="硕士" value="MASTER"></el-option><el-option label="博士" value="PHD"></el-option><el-option label="其他" value="OTHER"></el-option></el-select>
            </el-form-item>
            <el-form-item label="职业" prop="occupation"><el-input v-model="editFormModel.occupation" placeholder="请输入您的职业"></el-input></el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer"><el-button @click="handleEditCancel">取 消</el-button><el-button type="primary" @click="handleEditSave" :loading="editSaving">保 存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api/api';
import { mapState, mapActions } from 'vuex';
import moment from 'moment';
import QrcodeVue from 'qrcode.vue';
import SockJS from 'sockjs-client/dist/sockjs.min.js';
import Stomp from 'stompjs';

export default {
  name: 'UserOverview',
  components: { QrcodeVue },
  data() {
    return {
      trips: [], bookings: [], loading: true, editModalVisible: false, editSaving: false,
      editFormModel: { username: '', password: '', phone: '', email: '', education: null, occupation: '' },
      editFormRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ min: 6, message: '密码至少为6位', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的11位手机号码', trigger: 'blur' }],
        email: [{ type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }]
      },
      paymentModalVisible: false, qrCodeUrl: '', paymentAmount: 0, currentPayingOrderId: null,
      stompClient: null, subscription: null,
    };
  },
  computed: {
    ...mapState(['user']),
    passenger() { return this.user ? (this.user.passenger || {}) : {}; },
    membershipClass() { return this.passenger.membershipLevel ? 'badge-' + this.passenger.membershipLevel.toLowerCase() : 'badge-regular'; },
    membershipIcon() { const l = this.passenger.membershipLevel; if (l === 'GOLD') return 'el-icon-medal-1'; if (l === 'SILVER') return 'el-icon-medal'; return 'el-icon-user'; },
    membershipText() { const l = this.passenger.membershipLevel; if (l === 'GOLD') return '金卡会员'; if (l === 'SILVER') return '银卡会员'; return '普通会员'; },
  },
  async created() {
    await this.fetchDashboardData();
  },
  methods: {
    ...mapActions(['refreshUser']),
    async fetchDashboardData() {
      if (!this.user) { return; }
      this.loading = true;
      try {
        const passengerId = this.user.passenger.passengerId;
        const [tripsData, bookingsData] = await Promise.all([api.getTripsByPassengerId(passengerId), api.getOrdersByPassengerId(passengerId)]);
        this.trips = tripsData.sort((a, b) => new Date(b.flight?.departureTime || 0) - new Date(a.flight?.departureTime || 0));
        this.bookings = bookingsData.sort((a, b) => new Date(b.orderTime) - new Date(a.orderTime));
      } catch (error) {
        if (error.response?.status === 401) {
          this.$store.dispatch('logout');
          this.$router.push('/login');
        } else {
          console.error("加载数据失败", error);
        }
      } finally {
        this.loading = false;
      }
    },
    formatDate(d) { return d ? moment(d).format('YYYY/MM/DD HH:mm') : '-'; },
    formatDateTime(d) { return d ? moment(d).format('YYYY/MM/DD HH:mm') : '-'; },
    formatSeatClass(s) { return { 'ECONOMY': '经济舱', 'BUSINESS': '商务舱' }[s] || s; },
    getStatusText(o) {
      const m = { 'PENDING_PAYMENT': '待支付', 'PAID': '已支付', 'TICKETED': '已出票', 'COMPLETED': '已完成', 'CANCELLED': '已取消', 'REFUNDED': '已退票' };
      if (['PAID', 'TICKETED', 'COMPLETED'].includes(o.status)) {
        if (!o.flight) return '已完成'; // 如果航班被删了，算作完成
        return moment(o.flight.departureTime).isBefore(moment()) ? '已完成' : '待出行';
      }
      return m[o.status] || o.status;
    },
    getStatusClass(o) {
      if (['PAID', 'TICKETED', 'COMPLETED'].includes(o.status)) {
        if (!o.flight) return 'status-completed';
        return moment(o.flight.departureTime).isBefore(moment()) ? 'status-completed' : 'status-paid';
      }
      return 'status-' + o.status.toLowerCase();
    },
    isRefundable(o) { 
        // 增加对 flight 是否存在的检查
        return o.flight && ['PAID', 'TICKETED'].includes(o.status) && moment(o.flight.departureTime).isAfter(moment()); 
    },
    
    // 核心：更健壮的支付逻辑
    async handlePay(order) {
      try {
        this.currentPayingOrderId = order.orderId;
        this.paymentAmount = order.actualPrice;
        
        // 1. 获取二维码
        const result = await api.getPaymentQrCode(this.currentPayingOrderId);
        this.qrCodeUrl = result.qrCodeUrl;
        
        // 2. 无论 Socket 是否成功，先显示二维码弹窗
        this.paymentModalVisible = true;
        
        // 3. 尝试连接 WebSocket (即使失败也不影响扫码)
        try {
            this.initWebSocket();
        } catch (wsError) {
            console.error("WebSocket 初始化异常:", wsError);
        }
      } catch (error) {
        console.error("获取支付信息失败:", error);
        const message = error.response?.data?.message || "获取支付信息失败，请重试！";
        this.$message.error(message);
      }
    },

    // 核心：WebSocket 连接
    initWebSocket() {
      if (this.stompClient && this.stompClient.connected) { return; }
      
      // 使用代理路径 /ws，由 vite.config.js 转发到后端
      const serverUrl = `/ws`; 
      const socket = new SockJS(serverUrl);
      
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = null; // 关闭控制台调试日志
      
      this.stompClient.connect({}, () => {
          console.log('支付状态监听已连接');
          const topic = `/topic/payment-status/${this.currentPayingOrderId}`;
          this.subscription = this.stompClient.subscribe(topic, async message => {
            const paymentResult = JSON.parse(message.body);
            if (paymentResult.status === 'SUCCESS') {
              this.closePaymentModal();
              this.$message.success('支付成功！正在刷新订单...');
              
              // ★★★ 核心修复：防止401错误中断流程 ★★★
              setTimeout(async () => {
                // 1. 尝试刷新用户信息，如果失败（如401），捕获但不中断
                try {
                  await this.refreshUser(); 
                } catch (e) {
                  console.warn('刷新用户信息失败(可能是会话过期)，尝试继续刷新列表');
                }
                
                // 2. 刷新订单列表（让状态变更为待出行）
                try {
                  await this.fetchDashboardData();
                } catch (e) {
                  console.error('刷新订单列表失败', e);
                }
              }, 500);
            }
          });
        },
        (error) => {
           console.warn('WebSocket 连接失败 (自动刷新可能不可用):', error);
        }
      );
    },
    
    closePaymentModal() { this.paymentModalVisible = false; this.disconnectWebSocket(); },
    disconnectWebSocket() { if (this.subscription) { this.subscription.unsubscribe(); this.subscription = null; } if (this.stompClient && this.stompClient.connected) { this.stompClient.disconnect(); } this.stompClient = null; this.qrCodeUrl = ''; this.currentPayingOrderId = null; },

    async handleCancelOrRefund(order, actionType) { 
        if (actionType === 'refund') { 
            try { 
                const preview = await api.getRefundPreview(order.orderId); 
                const message = `<p>${preview.policy}</p><p>退款金额: ¥${preview.refundAmount.toFixed(2)}</p>`; 
                this.$confirm(message, '退票申请', { dangerouslyUseHTMLString: true, confirmButtonText: preview.refundable ? '确认退票' : '关闭', showCancelButton: preview.refundable, type: 'warning' })
                .then(() => { if (preview.refundable) this.executeRefund(order.orderId); }); 
            } catch (error) { this.$message.error('获取退票信息失败'); } 
        } else { 
            this.$confirm('确定取消订单?', '提示', { type: 'warning' }).then(async () => { 
                try { await api.updateOrderStatus(order.orderId, 'CANCELLED'); this.$message.success('取消成功'); await this.fetchDashboardData(); } 
                catch (error) { this.$message.error('操作失败'); } 
            }); 
        } 
    },
    async executeRefund(orderId) { try { await api.updateOrderStatus(orderId, 'REFUNDED'); this.$message.success('退票申请成功'); await this.fetchDashboardData(); } catch (error) { this.$message.error('退票失败'); } },
    
    educationText(e) { const m = { PRIMARY: '小学', JUNIOR: '初中', HIGH: '高中', COLLEGE: '大专', BACHELOR: '本科', MASTER: '硕士', PHD: '博士', OTHER: '其他' }; return m[e] || e || '未设置'; },
    showEditModal() { this.editFormModel = { username: this.user.username, password: '', phone: this.passenger.phone, email: this.passenger.email, education: this.passenger.education, occupation: this.passenger.occupation }; this.editModalVisible = true; },
    handleEditCancel() { this.editModalVisible = false; this.$refs.editFormRef.resetFields(); },
    handleEditSave() { this.$refs.editFormRef.validate(async (valid) => { if (valid) { this.editSaving = true; const pData = { phone: this.editFormModel.phone, email: this.editFormModel.email, education: this.editFormModel.education, occupation: this.editFormModel.occupation }; const aData = { username: this.editFormModel.username, ...(this.editFormModel.password && { password: this.editFormModel.password }) }; try { await api.updatePassenger(this.passenger.passengerId, pData); await api.updateUserAccount(this.user.userId, aData); this.editModalVisible = false; this.$message.success('更新成功'); await this.refreshUser(); await this.fetchDashboardData(); } catch (error) { this.$message.error('更新失败'); } finally { this.editSaving = false; } } }); },
  },
  beforeDestroy() {
    this.disconnectWebSocket();
  }
}
</script>

<style scoped>
/* 这里保留原 UserDashboard 的内部样式，去除最外层的 layout 样式 */
.dashboard-layout { display: grid; grid-template-columns: 300px 1fr; gap: 20px; align-items: start; }
.left-column, .right-column { display: flex; flex-direction: column; gap: 20px; }
.dashboard-card { background: white; border-radius: 4px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); border: 1px solid #ebeef5; }
.card-header { padding: 15px 20px; border-bottom: 1px solid #ebeef5; display: flex; justify-content: space-between; align-items: center; background-color: #fafafa; }
.card-header h2 { font-size: 16px; font-weight: 600; margin: 0; color: #303133; }
.card-body { padding: 20px; }
.user-profile { display: flex; gap: 15px; align-items: center; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px dashed #ebeef5; }
.profile-avatar { width: 60px; height: 60px; border-radius: 50%; background: #ecf5ff; display: flex; align-items: center; justify-content: center; font-size: 30px; color: #409EFF; }
.profile-info h3 { font-size: 18px; margin: 0 0 5px 0; color: #303133; }
.profile-info p { margin: 0 0 5px 0; color: #909399; font-size: 13px; }
.membership-badge { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 12px; }
.badge-gold { background: #fdf6ec; color: #e6a23c; border: 1px solid #f3d19e; }
.badge-silver { background: #f4f4f5; color: #909399; border: 1px solid #d3d4d6; }
.badge-regular { background: #ecf5ff; color: #409eff; border: 1px solid #d9ecff; }
.detail-item { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 14px; }
.detail-item .label { color: #909399; }
.detail-item .value { color: #606266; }

.order-item { border: 1px solid #ebeef5; border-radius: 4px; margin-bottom: 15px; transition: all 0.3s; }
.order-item:hover { box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); }
.order-header { padding: 10px 15px; background: #fafafa; border-bottom: 1px solid #ebeef5; display: flex; justify-content: space-between; align-items: center; }
.order-id { font-weight: bold; font-size: 14px; color: #606266; }
.order-status { font-size: 12px; padding: 2px 6px; border-radius: 2px; color: white; }
.status-pending_payment { background: #E6A23C; }
.status-paid, .status-ticketed { background: #67C23A; }
.status-cancelled, .status-refunded { background: #909399; }
.status-completed { background: #409EFF; }
.order-body { padding: 15px; display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.trip-body { grid-template-columns: 2fr 1.5fr 1fr; }
.flight-info { font-size: 14px; line-height: 1.8; }
.flight-number { font-weight: bold; color: #303133; font-size: 16px; }
.route { color: #409EFF; }
.seat-price-info .price { font-size: 18px; color: #F56C6C; font-weight: bold; }
.order-actions { padding: 10px 15px; border-top: 1px solid #ebeef5; text-align: right; }

.payment-modal-content { text-align: center; }
.qr-code-container { margin: 20px auto; display: flex; justify-content: center; }
.payment-amount .amount { font-size: 24px; color: #F56C6C; font-weight: bold; }

@media (max-width: 900px) {
  .dashboard-layout { grid-template-columns: 1fr; }
  .trip-body { grid-template-columns: 1fr; }
}
</style>