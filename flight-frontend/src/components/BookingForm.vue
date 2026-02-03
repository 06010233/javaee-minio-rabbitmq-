<template>
  <div class="booking-form">
    <div class="container">
      <h2>航班预订</h2>
      
      <!-- 航班信息显示 (保持不变) -->
      <div v-if="selectedFlight" class="flight-info-section">
        <h3>航班信息</h3>
        <div class="flight-details">
          <div class="flight-header">
            <span class="flight-number">{{ selectedFlight.flightNumber }}</span>
            <span class="aircraft-type">{{ selectedFlight.aircraftModel }}</span>
          </div>
          <div class="route-info">
            <div class="departure">
              <div class="airport">{{ selectedFlight.departureAirport.airportName }}</div>
              <div class="city">{{ selectedFlight.departureAirport.city }}</div>
              <div class="time">{{ formatDateTime(selectedFlight.departureTime) }}</div>
            </div>
            <div class="route-line">
              <div class="line"></div>
              <div class="duration">{{ calculateDuration(selectedFlight.departureTime, selectedFlight.arrivalTime) }}</div>
            </div>
            <div class="arrival">
              <div class="airport">{{ selectedFlight.arrivalAirport.airportName }}</div>
              <div class="city">{{ selectedFlight.arrivalAirport.city }}</div>
              <div class="time">{{ formatDateTime(selectedFlight.arrivalTime) }}</div>
            </div>
          </div>
          <div class="price-info">
            <span class="price">
              经济舱 ¥{{ selectedFlight.economySeatPrice }}
              <span class="availability-text" v-if="selectedFlight.availableEconomySeats != null">
                (余{{ selectedFlight.availableEconomySeats }}座)
              </span>
            </span>
            <span class="price">
              商务舱 ¥{{ selectedFlight.businessSeatPrice }}
              <span class="availability-text" v-if="selectedFlight.availableBusinessSeats != null">
                (余{{ selectedFlight.availableBusinessSeats }}座)
              </span>
            </span>
          </div>
        </div>
      </div>
      <div v-else class="loading-placeholder">正在加载航班信息...</div>

      <!-- 预订表单 -->
      <div class="booking-form-section">
        <h3>预订信息</h3>
        <form @submit.prevent="submitBooking">
          
          <div class="passenger-section-header">
            <h4>乘客信息 (共 {{ passengerList.length }} 人)</h4>
            <div>
              <el-button type="success" size="mini" icon="el-icon-plus" @click="addEmptyPassenger">添加乘客</el-button>
              <el-button type="primary" size="mini" icon="el-icon-user" @click="openContactDialog">从常用乘机人选择</el-button>
            </div>
          </div>

          <!-- ★★★ 核心修改：循环渲染乘客列表 ★★★ -->
          <div v-for="(passenger, index) in passengerList" :key="index" class="passenger-card-item">
            <div class="passenger-index-label">
              <span>第 {{ index + 1 }} 位乘客</span>
              <el-button v-if="passengerList.length > 1" type="text" icon="el-icon-delete" class="delete-btn" @click="removePassenger(index)">删除</el-button>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>乘客姓名 <span class="required">*</span></label>
                <input v-model="passenger.name" type="text" placeholder="请输入真实姓名" required />
              </div>
              <div class="form-group">
                <label>身份证号 <span class="required">*</span></label>
                <input v-model="passenger.idCard" type="text" placeholder="请输入18位身份证号" maxlength="18" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>联系电话 <span class="required">*</span></label>
                <input v-model="passenger.phone" type="tel" placeholder="请输入11位手机号" maxlength="11" required />
              </div>
              <div class="form-group">
                <label>邮箱地址</label>
                <input v-model="passenger.email" type="email" placeholder="可选" />
              </div>
            </div>
          </div>

          <!-- 座位选择 -->
          <div class="seat-section">
            <h4>座位选择 (所有乘客同等舱位)</h4>
            <div class="seat-types">
              <div v-for="seatType in seatTypes" :key="seatType.value" class="seat-type-option" :class="{ active: selectedSeatClass === seatType.value }" @click="selectSeatType(seatType.value)">
                <div class="seat-type-header">
                  <span class="seat-name">{{ seatType.name }}</span>
                  <span class="seat-price">¥{{ calculateSeatPrice(seatType.value) }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="price-summary">
            <h4>价格总计</h4>
            <div class="price-total">
              <div class="calc-detail">
                <span>¥{{ singlePrice.toFixed(2) }}</span>
                <span> x </span>
                <span>{{ passengerList.length }}人</span>
              </div>
              <span class="total-amount">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <small>请核对总人数和总金额，提交后将生成 {{ passengerList.length }} 个订单。</small>
          </div>

          <div class="agreement-section">
            <label class="checkbox-item">
              <input type="checkbox" v-model="agreedToTerms" required>
              <span>我已阅读并同意《用户服务协议》</span>
            </label>
          </div>
          <div class="form-actions">
            <button type="button" @click="goBack" class="back-btn">返回</button>
            <button type="submit" class="submit-btn" :disabled="submitting || !selectedFlight">
              {{ submitting ? '正在提交...' : `确认支付 ¥${totalPrice.toFixed(2)}` }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- ★★★ 修改：支持多选的弹窗 ★★★ -->
    <el-dialog title="选择常用乘机人 (可多选)" :visible.sync="contactDialogVisible" width="600px">
      <!-- 添加 type="selection" 开启多选，添加 row-key 用于标识 -->
      <el-table 
        ref="contactTable"
        :data="myContacts" 
        style="width: 100%"
        empty-text="暂无常用乘机人，请在用户中心添加"
        row-key="idCard"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" :reserve-selection="true"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="contactDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmContactSelect" :disabled="selectedContacts.length === 0">
          确定添加 ({{ selectedContacts.length }})
        </el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import api from '@/api/api';
import moment from 'moment';
import { mapState } from 'vuex';

export default {
  name: 'BookingForm',
  props: {
    flightId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      seatTypes: [
        { value: 'ECONOMY', name: '经济舱' },
        { value: 'BUSINESS', name: '商务舱' }
      ],
      // ★ 改动：由对象变为数组
      passengerList: [
        { name: '', idCard: '', phone: '', email: '' }
      ],
      selectedSeatClass: 'ECONOMY', // 单独提取舱位选择
      
      errors: {},
      agreedToTerms: false,
      submitting: false,
      selectedFlight: null,
      
      contactDialogVisible: false,
      myContacts: [],
      selectedContacts: [] // 存储弹窗中选中的数组
    };
  },
  computed: {
    ...mapState(['user']),
    singlePrice() {
      if (!this.selectedFlight) return 0;
      return this.selectedSeatClass === 'BUSINESS'
        ? this.selectedFlight.businessSeatPrice
        : this.selectedFlight.economySeatPrice;
    },
    // ★ 改动：总价计算逻辑
    totalPrice() {
      return this.singlePrice * this.passengerList.length;
    }
  },
  async created() {
    await this.fetchFlightDetails();
    // 默认填入当前登录用户
    if (this.user && this.user.passenger) {
        this.passengerList[0] = {
            name: this.user.passenger.name,
            idCard: this.user.passenger.idCard,
            phone: this.user.passenger.phone,
            email: this.user.passenger.email || ''
        };
    }
  },
  methods: {
    async fetchFlightDetails() {
      try {
        this.selectedFlight = await api.getFlightDetails(this.flightId);
      } catch (error) {
        this.$message.error('获取航班信息失败');
        this.$router.push('/flight-search');
      }
    },
    
    // 打开弹窗
    async openContactDialog() {
      if (!this.user) {
        this.$message.warning("请先登录");
        return;
      }
      this.contactDialogVisible = true;
      try {
        this.myContacts = await api.getContacts(this.user.userId);
        // 添加自己
        const self = this.user.passenger;
        if (!this.myContacts.some(c => c.idCard === self.idCard)) {
            const selfClone = JSON.parse(JSON.stringify(self));
            selfClone.name += " (本人)";
            this.myContacts.unshift(selfClone); 
        }
      } catch (error) {
        this.$message.error('获取常用联系人失败');
      }
    },

    // ★ 改动：处理多选
    handleSelectionChange(val) {
      this.selectedContacts = val;
    },

    // ★ 改动：确认多选
    confirmContactSelect() {
      if (this.selectedContacts.length > 0) {
        // 1. 如果当前列表只有1个且是空的，直接覆盖
        const first = this.passengerList[0];
        if (this.passengerList.length === 1 && !first.name && !first.idCard) {
            this.passengerList = [];
        }

        // 2. 遍历选中的人，添加到列表中
        this.selectedContacts.forEach(contact => {
            // 去重检查：如果列表里已经有这个人，就不加了
            const exists = this.passengerList.some(p => p.idCard === contact.idCard);
            if (!exists) {
                let name = contact.name;
                if (name.endsWith(" (本人)")) name = name.replace(" (本人)", "");
                
                this.passengerList.push({
                    name: name,
                    idCard: contact.idCard,
                    phone: contact.phone,
                    email: contact.email || ''
                });
            }
        });
        
        this.contactDialogVisible = false;
        // 清空选择
        this.$refs.contactTable.clearSelection();
        this.$message.success(`已添加 ${this.selectedContacts.length} 位乘机人`);
      }
    },

    // 手动添加空行
    addEmptyPassenger() {
      this.passengerList.push({ name: '', idCard: '', phone: '', email: '' });
    },

    // 删除某一行
    removePassenger(index) {
      this.passengerList.splice(index, 1);
    },

    formatDateTime(dateTime) { return moment(dateTime).format('YYYY-MM-DD HH:mm'); },
    calculateDuration(departure, arrival) { const diff = moment(arrival).diff(moment(departure)); const duration = moment.duration(diff); return `${duration.hours()}小时${duration.minutes()}分`; },
    selectSeatType(seatType) { this.selectedSeatClass = seatType; },
    calculateSeatPrice(seatType) { if (!this.selectedFlight) return 0; return seatType === 'BUSINESS' ? this.selectedFlight.businessSeatPrice : this.selectedFlight.economySeatPrice; },
    
    // ★ 改动：循环校验
    validateForm() {
        let isValid = true;
        const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/i;
        const phoneRegex = /^1[3-9]\d{9}$/;

        for (let i = 0; i < this.passengerList.length; i++) {
            const p = this.passengerList[i];
            if (!p.name.trim() || !p.idCard.trim() || !p.phone.trim()) {
                this.$message.error(`第 ${i+1} 位乘客信息不完整`);
                return false;
            }
            if (!idCardRegex.test(p.idCard)) {
                this.$message.error(`第 ${i+1} 位乘客身份证格式错误`);
                return false;
            }
            if (!phoneRegex.test(p.phone)) {
                this.$message.error(`第 ${i+1} 位乘客手机号格式错误`);
                return false;
            }
        }
        return isValid;
    },

    submitBooking() {
      if (this.passengerList.length === 0) { this.$message.error("请至少添加一位乘机人"); return; }
      if (!this.validateForm()) return;
      if (!this.agreedToTerms) { this.$message.error('请先同意用户服务协议'); return; }
      this.confirmBooking();
    },

    // ★ 改动：批量提交
    async confirmBooking() {
      this.submitting = true;
      try {
        if (!this.user || !this.user.passenger?.passengerId) {
            throw new Error("无法获取当前登录用户信息，请重新登录。");
        }

        // 使用 Promise.all 并发提交所有订单
        // 注意：这里我们前端循环调用 createOrder，虽然不是原子操作，但在目前架构下是最简单的实现方式
        const orderPromises = this.passengerList.map(passenger => {
            const orderData = {
                passenger: { passengerId: this.user.passenger.passengerId }, // 下单人
                flight: { flightId: this.selectedFlight.flightId },
                seatClass: this.selectedSeatClass,
                contactPhone: passenger.phone, // 使用乘机人的联系方式作为订单联系方式
                ticketPassenger: {
                    name: passenger.name,
                    idCard: passenger.idCard,
                    phone: passenger.phone,
                    email: passenger.email
                },
            };
            return api.createOrder(orderData);
        });

        await Promise.all(orderPromises);
        
        this.$message.success(`成功创建 ${this.passengerList.length} 个订单！请前往用户中心支付。`);
        this.$router.push({ name: 'UserOverview' });

      } catch (error) {
        console.error('预订失败：', error);
        const message = error.response?.data?.message || '部分或全部订单创建失败，请检查余票或重试';
        this.$message.error(message);
      } finally {
        this.submitting = false;
      }
    },
    
    goBack() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
.booking-form { font-family: 'Arial', sans-serif; max-width: 900px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }
.container { background: white; padding: 25px; border-radius: 8px; }
h2, h3, h4 { color: #333; margin: 0; }
.flight-info-section, .booking-form-section { margin-bottom: 25px; padding: 20px; border: 1px solid #eee; border-radius: 8px; }
.flight-details { padding: 15px; background-color: #f5f7fa; border-radius: 6px; }
.route-info { display: flex; justify-content: space-between; align-items: center; margin: 20px 0; }
.departure, .arrival { text-align: center; flex: 1; }
.route-line { flex: 2; text-align: center; }
.line { height: 2px; background: #ddd; margin: 10px 0; }
.price-info { text-align: right; font-size: 1.1em; margin-top: 15px; display: flex; justify-content: flex-end; gap: 20px; }
.price { font-weight: bold; color: #e74c3c; }

/* 乘客卡片样式 */
.passenger-section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 2px solid #eee; }
.passenger-card-item { background: #fafafa; border: 1px solid #e0e0e0; border-radius: 6px; padding: 15px; margin-bottom: 15px; position: relative; }
.passenger-index-label { display: flex; justify-content: space-between; margin-bottom: 10px; font-weight: bold; color: #409EFF; font-size: 14px; }
.delete-btn { color: #F56C6C; padding: 0; }

.form-row { display: flex; gap: 20px; margin-bottom: 15px; }
.form-group { flex: 1; position: relative; }
label { display: block; margin-bottom: 5px; font-weight: 500; font-size: 13px; }
.required { color: #e74c3c; }
input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; transition: border-color 0.3s; }
input:focus { border-color: #409EFF; outline: none; }

.seat-types { display: flex; gap: 15px; margin-top: 10px; }
.seat-type-option { flex: 1; padding: 15px; border: 1px solid #ddd; border-radius: 6px; cursor: pointer; transition: all 0.3s; }
.seat-type-option.active { border-color: #3498db; background-color: #e1f0fa; }
.seat-type-header { display: flex; justify-content: space-between; margin-bottom: 10px; }
.seat-name { font-weight: bold; }
.seat-price { color: #e74c3c; font-weight: bold; }

.price-summary { margin-top: 20px; padding: 15px; background-color: #f5f7fa; border-radius: 6px; }
.price-total { display: flex; justify-content: flex-end; align-items: center; margin-top: 10px; padding-top: 10px; }
.calc-detail { color: #666; margin-right: 15px; font-size: 14px; }
.total-amount { color: #e74c3c; font-size: 1.5em; font-weight: bold; }

.agreement-section { margin: 20px 0; padding: 10px 0; }
.checkbox-item { display: flex; align-items: center; }
.checkbox-item input { width: auto; margin-right: 8px; }
.form-actions { display: flex; justify-content: flex-end; gap: 15px; margin-top: 20px; }
.back-btn, .submit-btn { padding: 12px 25px; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; transition: background-color 0.3s; }
.back-btn { background-color: #eee; color: #333; }
.submit-btn { background-color: #e6a23c; color: white; font-weight: bold; }
.submit-btn:hover { background-color: #cf9236; }
.submit-btn:disabled { background-color: #95a5a6; cursor: not-allowed; }
.availability-text { font-size: 0.9em; font-weight: normal; color: #595959; margin-left: 8px; }
</style>