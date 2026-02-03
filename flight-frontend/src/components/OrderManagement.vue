<template>
  <div class="order-management">
    <div style="margin-bottom: 16px">
      <a-button type="primary" @click="showModal()" style="margin-right: 8px">
        创建新订单
      </a-button>
      <a-button @click="fetchOrders">刷新</a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="orders"
      rowKey="orderId"
      :loading="loading"
      :pagination="{ pageSize: 10 }"
    >
      <template #flightNumber="text, record">
        {{ record.flight ? record.flight.flightNumber : '-' }}
      </template>
      <template #passengerName="text, record">
        {{ record.ticketPassenger ? record.ticketPassenger.name : '-' }}
      </template>
      <template #seatClass="text">
        {{ formatSeatClass(text) }}
      </template>
      <template #orderTime="text">
        {{ formatDate(text) }}
      </template>
      <template #actualPrice="text">
        ¥{{ text }}
      </template>
      <template #status="text">
        <a-tag :color="getStatusColor(text)">
          {{ getStatusText(text) }}
        </a-tag>
      </template>
      <template #action="text, record">
        <!-- 编辑功能已移除，因为后端不支持通用更新 -->
        <!-- <a-button type="link" size="small" @click="showModal(record)">编辑</a-button> -->
        
        <a-button
          v-if="record.status === 'PENDING_PAYMENT'"
          type="link"
          size="small"
          @click="handleStatusUpdate(record.orderId, 'PAID')"
        >
          设为已支付
        </a-button>
        
        <a-button
          v-if="record.status === 'PAID'"
          type="link"
          size="small"
          @click="handleStatusUpdate(record.orderId, 'CANCELLED')"
        >
          取消订单
        </a-button>
        
        <!-- 删除功能已移除，因为后端不支持 -->
        <!-- 
        <a-popconfirm
          title="确定删除此订单吗?"
          @confirm="handleDelete(record.orderId)"
          okText="确定"
          cancelText="取消"
        >
          <a-button type="link" size="small" danger>删除</a-button>
        </a-popconfirm>
        -->
      </template>
    </a-table>

    <!-- 创建订单模态框 -->
    <a-modal
      title="创建新订单"
      :visible="isModalVisible"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirm-loading="submitLoading"
      :destroyOnClose="true"
    >
      <a-form :form="form" layout="vertical">
        <a-form-item label="乘客姓名">
          <a-input v-decorator="['passengerName', { rules: [{ required: true, message: '请输入乘客姓名' }] }]" placeholder="请输入乘客姓名" />
        </a-form-item>
        <a-form-item label="身份证号">
          <a-input v-decorator="['idCard', { rules: [{ required: true, message: '请输入身份证号' }] }]" placeholder="请输入身份证号" />
        </a-form-item>
        <a-form-item label="航班">
          <a-select v-decorator="['flightId', { rules: [{ required: true, message: '请选择航班' }] }]" placeholder="请选择航班">
            <a-select-option v-for="flight in flights" :key="flight.flightId" :value="flight.flightId">
              {{ flight.flightNumber }} ({{ flight.departureAirport.city }} - {{ flight.arrivalAirport.city }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="舱位等级">
          <a-select v-decorator="['seatClass', { rules: [{ required: true, message: '请选择舱位等级' }] }]" placeholder="请选择舱位等级">
            <a-select-option value="ECONOMY">经济舱</a-select-option>
            <a-select-option value="BUSINESS">商务舱</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="联系电话">
          <a-input v-decorator="['contactPhone', { rules: [{ required: true, message: '请输入联系电话' }] }]" placeholder="请输入联系电话" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import moment from 'moment';
import api from '@/api/api';

export default {
  name: 'OrderManagement',
  data() {
    return {
      orders: [],
      flights: [],
      isModalVisible: false,
      loading: false,
      submitLoading: false,
      form: this.$form.createForm(this),
      columns: [
        { title: '订单号', dataIndex: 'orderId', key: 'orderId' },
        { title: '航班号', key: 'flightNumber', scopedSlots: { customRender: 'flightNumber' } },
        { title: '乘客姓名', key: 'passengerName', scopedSlots: { customRender: 'passengerName' } },
        { title: '舱位', dataIndex: 'seatClass', key: 'seatClass', scopedSlots: { customRender: 'seatClass' } },
        { title: '订单时间', dataIndex: 'orderTime', key: 'orderTime', scopedSlots: { customRender: 'orderTime' } },
        { title: '实际价格', dataIndex: 'actualPrice', key: 'actualPrice', scopedSlots: { customRender: 'actualPrice' } },
        { title: '状态', dataIndex: 'status', key: 'status', scopedSlots: { customRender: 'status' } },
        { title: '操作', key: 'action', scopedSlots: { customRender: 'action' } }
      ],
      orderStatusMap: {
        PENDING_PAYMENT: '待支付',
        PAID: '已支付',
        TICKETED: '已出票',
        CANCELLED: '已取消',
        COMPLETED: '已完成',
        REFUNDED: '已退票'
      }
    };
  },
  mounted() {
    this.fetchOrders();
    this.fetchFlights();
  },
  methods: {
    formatDate(date) {
      return date ? moment(date).format('YYYY-MM-DD HH:mm') : '-';
    },
    formatSeatClass(seatClass) {
      const map = { ECONOMY: '经济舱', BUSINESS: '商务舱' };
      return map[seatClass] || seatClass;
    },
    getStatusColor(status) {
      const colors = {
        PENDING_PAYMENT: 'orange',
        PAID: 'blue',
        TICKETED: 'green',
        CANCELLED: 'red',
        COMPLETED: 'purple',
        REFUNDED: 'cyan'
      };
      return colors[status] || 'default';
    },
    getStatusText(status) {
        return this.orderStatusMap[status] || status;
    },
    async fetchOrders() {
      this.loading = true;
      try {
        this.orders = await api.getOrders();
      } catch (error) {
        this.$message.error('获取订单数据失败');
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    async fetchFlights() {
      try {
        // 获取所有航班用于下拉选择
        this.flights = await api.searchFlights({ departureCode: 'PEK', arrivalCode: 'PVG', date: '2025-01-01' }); // 这是一个示例，实际可能需要一个findAllFlights的接口
      } catch (error) {
        this.$message.error('获取航班数据失败');
      }
    },
    showModal() {
      this.isModalVisible = true;
    },
    handleCancel() {
      this.isModalVisible = false;
      this.form.resetFields();
    },
    handleSubmit() {
      this.form.validateFields(async (err, values) => {
        if (!err) {
          this.submitLoading = true;
          try {
            const selectedFlight = this.flights.find(f => f.flightId === values.flightId);
            const orderData = {
              passenger: { passengerId: 1 }, // 警告: 硬编码下单用户ID
              flight: { flightId: selectedFlight.flightId },
              seatClass: values.seatClass,
              ticketPassenger: {
                name: values.passengerName,
                idCard: values.idCard,
                phone: values.contactPhone,
                birthDate: '1990-01-01',
                gender: 'OTHER'
              },
              contactPhone: values.contactPhone,
              status: 'PENDING_PAYMENT',
            };
            await api.createOrder(orderData);
            this.$message.success('订单创建成功');
            this.handleCancel();
            this.fetchOrders();
          } catch (error) {
            this.$message.error('操作失败: ' + (error.response?.data?.message || error.message));
          } finally {
            this.submitLoading = false;
          }
        }
      });
    },
    async handleStatusUpdate(orderId, status) {
      try {
        await api.updateOrderStatus(orderId, status);
        this.$message.success('订单状态更新成功');
        this.fetchOrders();
      } catch (error) {
        this.$message.error('状态更新失败: ' + (error.response?.data?.message || error.message));
      }
    }
  }
};
</script>

<style scoped>
.order-management {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>