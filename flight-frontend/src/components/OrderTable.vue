<template>
  <!-- 
    将 <a-table> 替换为 <el-table>。
    - :data="orders" 绑定数据源。
    - v-loading="loading" 控制加载状态。
    - row-key="orderId" 指定行的唯一标识。
    - border 添加边框，empty-text 设置无数据时的提示。
   -->
  <el-table
    :data="orders"
    v-loading="loading"
    style="width: 100%"
    row-key="orderId"
    border
    empty-text="当前分类下暂无订单"
  >
    <!-- 
      在 Element UI 中，每一列都用一个 <el-table-column> 组件来定义。
      这替代了 Ant Design Vue 在 <script> 中定义 columns 数组的方式。
     -->
    <el-table-column
      prop="orderId"
      label="订单号"
      width="180">
    </el-table-column>

    <el-table-column
      label="用户/乘客"
      width="180">
      <!-- 使用 slot-scope="scope" 来自定义单元格内容，scope.row 代表当前行的数据 -->
      <template slot-scope="scope">
        <!-- 增加 v-if 判断，防止因数据不存在而报错 -->
        <div v-if="scope.row.passenger">下单: {{ scope.row.passenger.name }}</div>
        <div v-if="scope.row.ticketPassenger">乘机: {{ scope.row.ticketPassenger.name }}</div>
      </template>
    </el-table-column>
    
    <el-table-column
      label="航班信息"
      min-width="220">
      <template slot-scope="scope">
        <div v-if="scope.row.flight">
          <div>{{ scope.row.flight.flightNumber }}</div>
          <div v-if="scope.row.flight.departureAirport && scope.row.flight.arrivalAirport">
            {{ scope.row.flight.departureAirport.city }} -> {{ scope.row.flight.arrivalAirport.city }}
          </div>
        </div>
      </template>
    </el-table-column>

    <el-table-column
      label="下单时间"
      width="160">
      <template slot-scope="scope">
        <!-- 调用 methods 中的方法来格式化日期 -->
        <span>{{ formatDate(scope.row.orderTime) }}</span>
      </template>
    </el-table-column>
    
    <el-table-column
      label="金额"
      width="100"
      align="center">
      <template slot-scope="scope">
        <!-- 检查 actualPrice 是否存在 -->
        <span v-if="scope.row.actualPrice !== null && scope.row.actualPrice !== undefined">
          ¥{{ scope.row.actualPrice.toFixed(2) }}
        </span>
      </template>
    </el-table-column>
    
    <el-table-column
      label="状态"
      width="120"
      align="center">
      <template slot-scope="scope">
        <!-- 将 <a-tag> 替换为 <el-tag>，并使用 :type 属性控制颜色 -->
        <el-tag :type="getStatusTagType(scope.row.status)" size="medium">
          {{ getStatusText(scope.row.status) }}
        </el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import moment from 'moment';

export default {
  name: 'OrderTable',
  // props 保持不变，这是标准的 Vue 用法
  props: {
    orders: {
      type: Array,
      required: true,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  // data 中的 columns 数组不再需要，因为列已在 template 中定义
  data() {
    return {
      // 这个 columns 数组在 Element UI 的用法中是不需要的，可以安全移除。
    };
  },
  methods: {
    formatDate(date) {
      return date ? moment(date).format('YYYY-MM-DD HH:mm') : '-';
    },
    // getStatusText 方法保持不变
    getStatusText(status) {
      const map = {
        PENDING_PAYMENT: '待支付',
        PAID: '已支付',
        TICKETED: '已出票',
        CANCELLED: '已取消',
        COMPLETED: '已完成',
        REFUNDED: '已退票'
      };
      return map[status] || status;
    },
    // ★ 关键修改 ★
    // 将 getStatusColor 方法重命名并修改，以返回 Element UI 的 el-tag 所支持的 type 类型
    getStatusTagType(status) {
      const types = {
        PENDING_PAYMENT: 'warning', // 橙色 -> 警告
        PAID: 'primary',      // 蓝色 -> 主要
        TICKETED: 'success',    // 绿色 -> 成功
        CANCELLED: 'danger',      // 红色 -> 危险
        COMPLETED: '',            // Element UI 没有紫色，使用默认
        REFUNDED: 'info'        // 青色 -> 信息
      };
      return types[status] || 'info'; // 默认为灰色
    }
  }
};
</script>

<style scoped>
/* 可选：为表格添加一些样式 */
</style>