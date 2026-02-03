<template>
  <div class="flight-search-container">
    <div class="search-section">
      <h1>航班搜索</h1>
      <div class="search-form">
        <div class="form-group">
          <label for="departure">出发地</label>
          <select v-model="searchParams.departureCode" id="departure">
            <option value="">请选择出发机场</option>
            <option v-for="airport in airports" :key="airport.airportCode" :value="airport.airportCode">
              {{ airport.city }} - {{ airport.airportName }} ({{ airport.airportCode }})
            </option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="arrival">目的地</label>
          <select v-model="searchParams.arrivalCode" id="arrival">
            <option value="">请选择到达机场</option>
            <option v-for="airport in airports" :key="airport.airportCode" :value="airport.airportCode">
              {{ airport.city }} - {{ airport.airportName }} ({{ airport.airportCode }})
            </option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="date">出发日期</label>
          <!-- 移除 :min="today" 属性，允许选择过去的日期 -->
          <input type="date" v-model="searchParams.date" id="date">
        </div>
        
        <div class="form-group">
          <label for="seatClass">舱位等级</label>
          <select v-model="searchParams.seatClass" id="seatClass">
            <option value="ECONOMY">经济舱</option>
            <option value="BUSINESS">商务舱</option>
          </select>
        </div>
        
        <button @click="searchFlights" class="search-btn" :disabled="loading">
          {{ loading ? '搜索中...' : '搜索航班' }}
        </button>
      </div>
    </div>
    
    <!-- 全局错误提示，用于显示机场列表加载失败等信息 -->
    <div v-if="errorMessage" class="error-section">
      <p>{{ errorMessage }}</p>
    </div>
    
    <!-- 搜索时的加载动画 -->
    <div v-if="loading" class="loading-section">
      <div class="spinner"></div>
      <p>正在搜索航班...</p>
    </div>
    
    <div v-if="!loading && flights.length > 0" class="results-section">
      <h2>搜索结果</h2>
      <p class="result-count">共找到 {{ flights.length }} 个航班</p>
      
      <div class="flight-list">
        <div v-for="flight in flights" :key="flight.flightId" class="flight-card">
          <div class="flight-header">
            <span class="flight-number">{{ flight.flightNumber }}</span>
            <span class="airline">{{ flight.airline.airlineName }}</span>
            <span class="aircraft">{{ flight.aircraftModel }}</span>
            <!-- 航班状态显示现在依赖 isPastFlight() 方法 -->
            <span class="status" :class="getStatusClass(flight)">{{ getStatusText(flight) }}</span>
          </div>
          
          <div class="flight-details">
            <div class="departure">
              <div class="time">{{ formatTime(flight.departureTime) }}</div>
              <div class="airport">{{ flight.departureAirport.airportCode }} - {{ flight.departureAirport.airportName }}</div>
              <div class="city">{{ flight.departureAirport.city }}</div>
            </div>
            
            <div class="duration">
              <div class="line"></div>
              <div class="time">{{ calculateDuration(flight.departureTime, flight.arrivalTime) }}</div>
            </div>
            
            <div class="arrival">
              <div class="time">{{ formatTime(flight.arrivalTime) }}</div>
              <div class="airport">{{ flight.arrivalAirport.airportCode }} - {{ flight.arrivalAirport.airportName }}</div>
              <div class="city">{{ flight.arrivalAirport.city }}</div>
            </div>
          </div>
          
          <div class="flight-pricing">
            <div class="price-info">
              <div class="price-label">价格</div>
              <div class="price">¥{{ getPriceByClass(flight) }}</div>
            </div>
            
            <!-- 按钮的可用性和文本现在依赖 isPastFlight() 方法 -->
            <button 
              @click="selectFlight(flight)" 
              class="select-btn"
              :disabled="isPastFlight(flight)"
              :class="{ 'disabled': isPastFlight(flight) }"
            >
              {{ isPastFlight(flight) ? '无法预订' : '选择航班' }}
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else-if="searched && !loading && !errorMessage" class="no-results">
      <p>没有找到符合条件的航班，请修改搜索条件后重试</p>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';
import moment from 'moment';

export default {
  name: 'FlightSearch',
  data() {
    return {
      airports: [],
      flights: [],
      loading: false,
      searched: false,
      errorMessage: '',
      searchParams: {
        departureCode: '',
        arrivalCode: '',
        date: this.getFormattedDate(new Date()),
        seatClass: 'ECONOMY'
      }
    };
  },
  methods: {
    async fetchAirports() {
      this.errorMessage = '';
      try {
        const data = await api.getAirports();
        if (Array.isArray(data)) {
          this.airports = data;
          if (data.length === 0) {
            this.errorMessage = '机场列表为空，请检查后端数据库是否有数据。';
          }
        } else {
          this.errorMessage = '获取机场数据失败，响应格式不正确。';
          this.airports = [];
        }
      } catch (error) {
        this.errorMessage = '获取机场数据失败，请确认后端服务已启动并检查CORS配置。';
        console.error('获取机场数据失败:', error);
        this.airports = [];
      }
    },
    
    async searchFlights() {
      if (!this.searchParams.departureCode || !this.searchParams.arrivalCode) {
        this.errorMessage = '请选择出发地和目的地';
        return;
      }
      if (this.searchParams.departureCode === this.searchParams.arrivalCode) {
        this.errorMessage = '出发地和目的地不能相同';
        return;
      }

      this.loading = true;
      this.errorMessage = '';
      this.searched = true;
      this.flights = [];

      try {
        const params = {
          departureCode: this.searchParams.departureCode,
          arrivalCode: this.searchParams.arrivalCode,
          date: this.searchParams.date
        };
        this.flights = await api.searchFlights(params);
      } catch (error) {
        this.errorMessage = '搜索航班失败: ' + (error.response?.data?.message || '请检查后端服务日志。');
        console.error('搜索航班失败:', error);
      } finally {
        this.loading = false;
      }
    },
    
    isPastFlight(flight) {
      return moment(flight.departureTime).isBefore(moment());
    },

    getStatusText(flight) {
        if (this.isPastFlight(flight) && flight.status !== 'CANCELLED') {
            return '已完成';
        }
        const statusMap = {
            'PLANNED': '计划中', 'BOARDING': '登机中', 'IN_FLIGHT': '飞行中',
            'ARRIVED': '已到达', 'CANCELLED': '已取消'
        };
        return statusMap[flight.status] || flight.status;
    },
    
    getStatusClass(flight) {
      let currentStatus = flight.status;
      if (this.isPastFlight(flight) && flight.status !== 'CANCELLED') {
        currentStatus = 'ARRIVED';
      }
      const classMap = {
        'PLANNED': 'planned', 'BOARDING': 'boarding', 'IN_FLIGHT': 'in-flight',
        'ARRIVED': 'arrived', 'CANCELLED': 'cancelled'
      };
      return classMap[currentStatus] || '';
    },
    
    selectFlight(flight) {
      if (this.isPastFlight(flight)) {
        this.$message.warning('历史航班无法预订。');
        return;
      }
      this.$router.push({
        name: 'BookingForm',
        params: { flightId: flight.flightId }
      });
    },

    formatTime(dateTime) {
      return moment(dateTime).format('HH:mm');
    },
    
    calculateDuration(departure, arrival) {
      const dep = moment(departure);
      const arr = moment(arrival);
      const duration = moment.duration(arr.diff(dep));
      return `${duration.hours()}小时${duration.minutes()}分`;
    },
    
    getPriceByClass(flight) {
      switch (this.searchParams.seatClass) {
        case 'ECONOMY':
          return flight.economySeatPrice;
        case 'BUSINESS':
          return flight.businessSeatPrice;
        default:
          return flight.economySeatPrice;
      }
    },
    
    getFormattedDate(date) {
      return moment(date).format('YYYY-MM-DD');
    }
  },
  
  mounted() {
    this.fetchAirports();
  }
};
</script>


<style scoped>
.flight-search-container { max-width: 1200px; margin: 0 auto; padding: 20px; font-family: 'Arial', sans-serif; background-color: #f9f9f9; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }
.search-section { background-color: #fff; padding: 25px; border-radius: 8px; margin-bottom: 30px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05); }
.search-section h1 { margin-top: 0; color: #2c3e50; text-align: center; font-weight: 600; margin-bottom: 25px; }
.search-form { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; align-items: end; }
.form-group { display: flex; flex-direction: column; }
.form-group label { font-weight: 500; margin-bottom: 8px; color: #555; }
.form-group select, .form-group input { padding: 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 16px; background-color: #fff; transition: border-color 0.3s; }
.form-group select:focus, .form-group input:focus { border-color: #3498db; outline: none; box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2); }
.search-btn { grid-column: span 1; background-color: #3498db; color: white; border: none; padding: 14px; border-radius: 4px; font-size: 16px; font-weight: 500; cursor: pointer; transition: background-color 0.3s; }
.search-btn:hover:not(:disabled) { background-color: #2980b9; }
.search-btn:disabled { background-color: #95a5a6; cursor: not-allowed; }
.loading-section { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 40px; }
.spinner { width: 50px; height: 50px; border: 5px solid rgba(52, 152, 219, 0.2); border-top: 5px solid #3498db; border-radius: 50%; animation: spin 1s linear infinite; margin-bottom: 20px; }
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
.error-section { background-color: #ffecec; color: #e74c3c; padding: 15px; border-radius: 4px; margin-bottom: 20px; text-align: center; }
.results-section { background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05); }
.results-section h2 { margin-top: 0; color: #2c3e50; }
.result-count { color: #7f8c8d; margin-bottom: 20px; }
.flight-list { display: flex; flex-direction: column; gap: 20px; }
.flight-card { border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; transition: transform 0.3s, box-shadow 0.3s; }
.flight-card:hover { transform: translateY(-3px); box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08); }
.flight-header { display: flex; align-items: center; padding: 15px 20px; background-color: #f8f9fa; border-bottom: 1px solid #e0e0e0; }
.flight-number { font-weight: bold; font-size: 18px; margin-right: 15px; color: #2c3e50; }
.airline { flex: 1; color: #7f8c8d; }
.aircraft { background-color: #e3f2fd; color: #1976d2; padding: 4px 8px; border-radius: 4px; font-size: 14px; margin-right: 15px; }
.status { padding: 4px 10px; border-radius: 4px; font-size: 14px; font-weight: 500; }
.status.planned { background-color: #e3f2fd; color: #1976d2; }
.status.boarding { background-color: #fff8e1; color: #ff8f00; }
.status.in-flight { background-color: #e8f5e9; color: #388e3c; }
.status.arrived { background-color: #e8f5e9; color: #388e3c; }
.status.cancelled { background-color: #ffebee; color: #d32f2f; }
.flight-details { display: flex; padding: 20px; align-items: center; }
.departure, .arrival { flex: 2; text-align: center; }
.departure .time, .arrival .time { font-size: 24px; font-weight: 500; margin-bottom: 5px; color: #2c3e50; }
.airport { font-size: 16px; font-weight: 500; color: #2c3e50; }
.city { font-size: 14px; color: #7f8c8d; }
.duration { flex: 3; display: flex; flex-direction: column; align-items: center; }
.line { width: 100%; height: 2px; background: linear-gradient(to right, #3498db, #2ecc71); position: relative; margin: 10px 0; }
.line::before { content: ''; position: absolute; width: 10px; height: 10px; background-color: #3498db; border-radius: 50%; left: 0; top: -4px; }
.line::after { content: ''; position: absolute; width: 10px; height: 10px; background-color: #2ecc71; border-radius: 50%; right: 0; top: -4px; }
.duration .time { font-size: 14px; color: #7f8c8d; }
.flight-pricing { display: flex; justify-content: space-between; align-items: center; padding: 15px 20px; background-color: #f8f9fa; border-top: 1px solid #e0e0e0; }
.price-info { display: flex; flex-direction: column; }
.price-label { font-size: 14px; color: #7f8c8d; }
.price { font-size: 24px; font-weight: bold; color: #e74c3c; }
.select-btn { background-color: #2ecc71; color: white; border: none; padding: 10px 20px; border-radius: 4px; font-size: 16px; font-weight: 500; cursor: pointer; transition: background-color 0.3s; }
.select-btn:hover { background-color: #27ae60; }
.select-btn.disabled {
  background-color: #95a5a6;
  cursor: not-allowed;
  color: #fff;
}
.select-btn.disabled:hover {
  background-color: #7f8c8d;
}
.no-results { text-align: center; padding: 40px; background-color: #fff; border-radius: 8px; color: #7f8c8d; font-size: 18px; }
@media (max-width: 768px) { .search-form { grid-template-columns: 1fr; } .flight-details { flex-direction: column; align-items: stretch; } .departure, .arrival, .duration { margin-bottom: 15px; } .flight-pricing { flex-direction: column; gap: 15px; } .price-info, .seat-availability { text-align: center; } }
</style>