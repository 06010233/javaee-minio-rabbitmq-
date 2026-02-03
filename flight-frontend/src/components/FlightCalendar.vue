<template>
  <div class="flight-calendar-container">
    <div class="header">
      <h2><i class="el-icon-date" /> 航班排期日历</h2>
      <div class="legend">
        <span class="badge planned"></span> 计划中
        <span class="badge flying"></span> 飞行中/到达
        <span class="badge cancelled"></span> 已取消
      </div>
    </div>
    
    <el-calendar v-model="selectedDate">
      <template #dateCell="{ date, data }">
        <div class="calendar-day">
          <!-- 显示日期数字 -->
          <span :class="{ 'is-today': isToday(date) }">{{ date.getDate() }}</span>
          
          <!-- 显示当天的航班 -->
          <ul class="events">
            <li v-for="item in getListData(data.day)" :key="item.flightId">
              <el-popover
                placement="top"
                width="250"
                trigger="hover">
                <div>
                  <h4 style="margin:0 0 10px 0;">{{ item.airline.airlineName }} {{ item.flightNumber }}</h4>
                  <p><strong>航线:</strong> {{ item.departureAirport.city }} -> {{ item.arrivalAirport.city }}</p>
                  <p><strong>起飞:</strong> {{ formatTime(item.departureTime) }}</p>
                  <p><strong>到达:</strong> {{ formatTime(item.arrivalTime) }}</p>
                  <p><strong>机型:</strong> {{ item.aircraftModel }}</p>
                  <p><strong>价格:</strong> ￥{{ item.economySeatPrice }}起</p>
                </div>
                <div slot="reference" class="flight-badge" :class="getStatusClass(item)">
                  {{ formatTime(item.departureTime) }} {{ item.flightNumber }}
                </div>
              </el-popover>
            </li>
          </ul>
        </div>
      </template>
    </el-calendar>
  </div>
</template>

<script>
import api from '@/api/api';
import moment from 'moment';

export default {
  name: 'FlightCalendar',
  data() {
    return {
      flights: [],
      selectedDate: new Date(), // 当前选中的日期（决定日历显示哪个月）
    };
  },
  // ★★★ 核心修复：监听日期变化 ★★★
  watch: {
    selectedDate(newDate, oldDate) {
      // 如果月份变了，就重新加载数据
      if (!oldDate || newDate.getMonth() !== oldDate.getMonth() || newDate.getFullYear() !== oldDate.getFullYear()) {
        this.fetchFlights();
      }
    }
  },
  mounted() {
    this.fetchFlights();
  },
  methods: {
    async fetchFlights() {
      // 获取当前日历视图的 月初 和 月末
      // 注意：为了防止漏掉首尾跨月的日期，我们可以多取前后几天，或者简单点直接取整月
      const startDate = moment(this.selectedDate).startOf('month').format('YYYY-MM-DD');
      const endDate = moment(this.selectedDate).endOf('month').format('YYYY-MM-DD');
      
      try {
        const res = await api.getVisibleFlightsForCalendar(startDate, endDate);
        this.flights = res || [];
      } catch (error) {
        this.$message.error('加载航班日历数据失败');
      }
    },
    
    getListData(dateStr) {
      // 筛选出属于这一天的航班
      // dateStr 格式通常是 'yyyy-MM-dd'
      if (!this.flights.length) return [];
      return this.flights.filter(flight => {
        return moment(flight.departureTime).format('YYYY-MM-DD') === dateStr;
      });
    },
    
    getStatusClass(flight) {
      if (flight.status === 'CANCELLED') return 'cancelled';
      if (['IN_FLIGHT', 'ARRIVED'].includes(flight.status)) return 'flying';
      return 'planned';
    },
    
    formatTime(timeStr) {
      return moment(timeStr).format('HH:mm');
    },

    isToday(date) {
      const today = new Date();
      return date.getDate() === today.getDate() &&
             date.getMonth() === today.getMonth() &&
             date.getFullYear() === today.getFullYear();
    }
  }
};
</script>

<style scoped>
.flight-calendar-container {
  padding: 20px;
  background: white;
  min-height: 600px;
  border-radius: 8px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.header h2 { font-size: 20px; color: #303133; }

/* 日历单元格样式微调 */
::v-deep .el-calendar-day {
  height: 120px; /* 固定高度，防止忽高忽低 */
  padding: 5px;
  display: flex;
  flex-direction: column;
}

.calendar-day span {
  font-weight: bold;
  margin-bottom: 5px;
  display: block;
}
.calendar-day span.is-today {
  color: #409EFF;
}

/* 滚动条区域 */
.events {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  overflow-y: auto; /* 内容多了可以滚动 */
}
.events::-webkit-scrollbar {
  width: 4px;
}
.events::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.events li {
  margin-bottom: 2px;
}

/* 航班小条样式 */
.flight-badge { 
  font-size: 12px; 
  padding: 2px 4px; 
  border-radius: 3px; 
  white-space: nowrap; 
  overflow: hidden; 
  text-overflow: ellipsis; 
  color: white; 
  cursor: pointer;
  transition: opacity 0.2s;
}
.flight-badge:hover { opacity: 0.8; }

.flight-badge.planned { background-color: #409EFF; } 
.flight-badge.flying { background-color: #67C23A; } 
.flight-badge.cancelled { background-color: #909399; text-decoration: line-through; } 

.legend {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #606266;
}
.badge {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
  margin-right: 5px;
}
.badge.planned { background-color: #409EFF; }
.badge.flying { background-color: #67C23A; }
.badge.cancelled { background-color: #909399; }
</style>