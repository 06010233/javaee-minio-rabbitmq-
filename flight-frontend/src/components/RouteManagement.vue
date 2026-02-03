<template>
  <div class="route-management">
    <el-card>
      <div slot="header" class="clearfix">
        <span>航线模板管理</span>
        <el-button style="float: right;" type="primary" icon="el-icon-plus" @click="handleOpenCreateDialog">
          新增航线
        </el-button>
      </div>

      <el-table :data="routes" style="width: 100%" v-loading="loading" row-key="routeId"
        @expand-change="handleExpandChange">
        <!-- 展开行 -->
        <el-table-column type="expand">
          <template slot-scope="props">
            <div class="future-flights-panel">
              <h4><i class="el-icon-date"></i> 未来7天航班计划 ({{ props.row.airline.airlineCode }}{{ props.row.flightNumber }})</h4>
              <div v-if="props.row.futureFlights && props.row.futureFlights.length > 0">
                <el-table :data="props.row.futureFlights" size="mini" border>
                  <el-table-column label="起飞时间" prop="departureTime" :formatter="formatDateTimeCell"></el-table-column>
                  <el-table-column label="到达时间" prop="arrivalTime" :formatter="formatDateTimeCell"></el-table-column>
                  <el-table-column label="状态" prop="status" :formatter="formatStatusCell"></el-table-column>
                  <el-table-column label="操作">
                    <template slot-scope="scope">
                      <el-button type="text" size="small" @click="handleEditFlight(scope.row)">单独编辑</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <el-empty v-else description="未来7天内没有该航线的航班安排"></el-empty>
            </div>
          </template>
        </el-table-column>

        <!-- 主表格列 -->
        <el-table-column prop="flightNumber" label="基础航班号"></el-table-column>
        <el-table-column label="航空公司" prop="airline.airlineName"></el-table-column>
        <el-table-column label="航线">
          <template slot-scope="scope">
            {{ scope.row.departureAirport.city }} -> {{ scope.row.arrivalAirport.city }}
          </template>
        </el-table-column>
        <el-table-column prop="departureTime" label="起飞时刻"></el-table-column>
        <el-table-column prop="arrivalTime" label="到达时刻"></el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.active ? 'success' : 'danger'">{{ scope.row.active ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        
        <!-- ★★★ 修改 1: 调整宽度并添加新按钮 ★★★ -->
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEditRoute(scope.row)">编辑航线</el-button>
            <el-button type="text" size="small" @click="handleScheduleFlights(scope.row)">发布航班</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑航线模板 Dialog -->
    <el-dialog :title="isEditMode ? '编辑航线模板' : '新增航线模板'" :visible.sync="routeDialogVisible" width="600px"
      :close-on-click-modal="false" @close="routeDialogVisible = false">
      <el-form :model="currentRoute" label-width="120px" ref="routeForm">
        <el-form-item label="航空公司">
          <el-select v-model="currentRoute.airline.airlineId" placeholder="请选择航空公司" :disabled="isEditMode" style="width: 100%;">
            <el-option v-for="item in airlines" :key="item.airlineId" :label="item.airlineName" :value="item.airlineId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="基础航班号">
          <el-input v-model="currentRoute.flightNumber" placeholder="如 5101, 不含航司代码" :disabled="isEditMode"></el-input>
        </el-form-item>
        <el-form-item label="出发机场">
          <el-select v-model="currentRoute.departureAirport.airportCode" placeholder="请选择出发机场" filterable
            :disabled="isEditMode" style="width: 100%;">
            <el-option v-for="item in airports" :key="item.airportCode"
              :label="`${item.city} - ${item.airportName} (${item.airportCode})`" :value="item.airportCode"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="到达机场">
          <el-select v-model="currentRoute.arrivalAirport.airportCode" placeholder="请选择到达机场" filterable
            :disabled="isEditMode" style="width: 100%;">
            <el-option v-for="item in airports" :key="item.airportCode"
              :label="`${item.city} - ${item.airportName} (${item.airportCode})`" :value="item.airportCode"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="起飞时刻">
          <el-time-picker v-model="currentRoute.departureTime" value-format="HH:mm:ss" placeholder="选择每日起飞时间">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="到达时刻">
          <el-time-picker v-model="currentRoute.arrivalTime" value-format="HH:mm:ss" placeholder="选择每日到达时间">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="机型">
          <el-input v-model="currentRoute.aircraftModel"></el-input>
        </el-form-item>
        
        <el-form-item label="经济舱座位数">
          <el-input-number v-model="currentRoute.economySeats" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="经济舱价格">
          <el-input-number v-model="currentRoute.economySeatPrice" :precision="2" :step="100" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="商务舱座位数">
          <el-input-number v-model="currentRoute.businessSeats" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="商务舱价格">
          <el-input-number v-model="currentRoute.businessSeatPrice" :precision="2" :step="100" :min="0"></el-input-number>
        </el-form-item>
        
        <el-form-item label="航线状态">
          <el-switch v-model="currentRoute.active" active-text="启用" inactive-text="停用"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="routeDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSaveRoute" :loading="saving">保 存</el-button>
      </span>
    </el-dialog>

    <!-- 编辑单个航班 Dialog -->
    <el-dialog title="单独编辑航班" :visible.sync="flightDialogVisible" width="600px" 
      :close-on-click-modal="false" @close="flightDialogVisible = false">
      <el-form :model="currentFlight" label-width="120px" ref="flightForm" v-if="currentFlight.flightId">
        <el-form-item label="航班号">
          <el-input :value="currentFlight.flightNumber" disabled></el-input>
        </el-form-item>
        <el-form-item label="起飞时间">
          <el-date-picker v-model="currentFlight.departureTime" type="datetime" placeholder="选择起飞日期时间"
            value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="到达时间">
          <el-date-picker v-model="currentFlight.arrivalTime" type="datetime" placeholder="选择到达日期时间"
            value-format="yyyy-MM-dd HH:mm:ss" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="航班状态">
          <el-select v-model="currentFlight.status" placeholder="请选择航班状态" style="width: 100%;">
            <el-option label="计划中" value="PLANNED"></el-option>
            <el-option label="登机中" value="BOARDING"></el-option>
            <el-option label="飞行中" value="IN_FLIGHT"></el-option>
            <el-option label="已到达" value="ARRIVED"></el-option>
            <el-option label="已取消" value="CANCELLED"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="flightDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSaveFlight" :loading="saving">保 存</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import api from '@/api/api';
import moment from 'moment';

export default {
  name: 'RouteManagement',
  data() {
    return {
      routes: [],
      airlines: [],
      airports: [],
      loading: false,
      saving: false,
      isEditMode: false,
      routeDialogVisible: false,
      flightDialogVisible: false,
      currentRoute: this.getEmptyRoute(),
      currentFlight: {},
    };
  },
  created() {
    this.fetchInitialData();
  },
  methods: {
    getEmptyRoute() {
      return {
        flightNumber: '',
        airline: { airlineId: null },
        departureAirport: { airportCode: null },
        arrivalAirport: { airportCode: null },
        departureTime: '08:00:00',
        arrivalTime: '10:00:00',
        aircraftModel: 'A320',
        economySeats: 150,
        economySeatPrice: 1000.00,
        businessSeats: 12,
        businessSeatPrice: 2000.00,
        active: true,
      };
    },
    async fetchInitialData() {
      this.loading = true;
      try {
        const [routes, airlines, airports] = await Promise.all([
          api.getFlightRoutes(),
          api.getAirlines(),
          api.getAirports()
        ]);
        this.routes = routes.map(r => ({ ...r, futureFlights: null }));
        this.airlines = airlines;
        this.airports = airports;
      } catch (error) {
        this.$message.error('加载基础数据失败');
      } finally {
        this.loading = false;
      }
    },
    async handleExpandChange(row, expandedRows) {
      const isExpanded = expandedRows.some(r => r.routeId === row.routeId);
      if (isExpanded && row.futureFlights === null) {
        try {
          const flights = await api.getFutureFlightsForRoute(row.routeId);
          this.$set(row, 'futureFlights', flights);
        } catch (error) {
          this.$message.error('加载未来航班计划失败');
          this.$set(row, 'futureFlights', []);
        }
      }
    },
    handleOpenCreateDialog() {
      this.isEditMode = false;
      this.currentRoute = this.getEmptyRoute();
      this.routeDialogVisible = true;
    },
    handleEditRoute(route) {
      this.isEditMode = true;
      this.currentRoute = JSON.parse(JSON.stringify(route));
      this.routeDialogVisible = true;
    },
    async handleSaveRoute() {
      this.saving = true;
      try {
        if (this.isEditMode) {
          await api.updateFlightRoute(this.currentRoute.routeId, this.currentRoute);
          this.$message.success('航线模板更新成功！未来航班已同步更新。');
        } else {
          await api.createFlightRoute(this.currentRoute);
          this.$message.success('新航线创建成功！');
        }
        this.routeDialogVisible = false;
        await this.fetchInitialData();
      } catch (error) {
        this.$message.error('保存失败: ' + (error.response?.data?.message || '未知错误'));
      } finally {
        this.saving = false;
      }
    },
    handleEditFlight(flight) {
      this.currentFlight = JSON.parse(JSON.stringify(flight));
      this.currentFlight.departureTime = moment(this.currentFlight.departureTime).format('YYYY-MM-DD HH:mm:ss');
      this.currentFlight.arrivalTime = moment(this.currentFlight.arrivalTime).format('YYYY-MM-DD HH:mm:ss');
      this.flightDialogVisible = true;
    },
    async handleSaveFlight() {
      this.saving = true;
      try {
        await api.updateFlight(this.currentFlight.flightId, this.currentFlight);
        this.$message.success('航班信息更新成功！');
        this.flightDialogVisible = false;
        
        const route = this.routes.find(r => r.futureFlights && r.futureFlights.some(f => f.flightId === this.currentFlight.flightId));
        if (route) {
          const flights = await api.getFutureFlightsForRoute(route.routeId);
          this.$set(route, 'futureFlights', flights);
        }
      } catch (error) {
        this.$message.error('保存失败: ' + (error.response?.data?.message || '未知错误'));
      } finally {
        this.saving = false;
      }
    },
    
    // ★★★ 修改 2: 添加新的方法用于处理航班发布 ★★★
    async handleScheduleFlights(route) {
      this.$prompt('请输入要为该航线生成的未来天数：', '发布航班计划', {
        confirmButtonText: '确定发布',
        cancelButtonText: '取消',
        inputPattern: /^[1-9]\d*$/,
        inputErrorMessage: '请输入一个大于0的正整数',
        inputValue: '7' // 默认值为7天
      }).then(async ({ value }) => {
        this.loading = true; // 使用全局 loading
        try {
          const days = parseInt(value, 10);
          // 调用我们新添加的 API
          const response = await api.scheduleFlightsForRoute(route.routeId, days);
          
          this.$message.success(response.message || `成功操作！`);
          
          // 智能刷新：如果该行已展开，则重新加载其航班数据
          // 检查 route.futureFlights 是否为数组（表示已展开过）
          if (Array.isArray(route.futureFlights)) {
            const flights = await api.getFutureFlightsForRoute(route.routeId);
            this.$set(route, 'futureFlights', flights);
          } else {
             // 如果未展开，将其重置为null，以便下次展开时能重新加载
             route.futureFlights = null;
          }
        } catch (error) {
          this.$message.error('发布失败: ' + (error.response?.data?.message || '未知错误'));
        } finally {
          this.loading = false;
        }
      }).catch(() => {
        this.$message.info('已取消发布操作');
      });
    },

    formatDateTimeCell(row, column, cellValue) {
      return moment(cellValue).format('YYYY-MM-DD HH:mm');
    },
    formatStatusCell(row, column, cellValue) {
      const statusMap = {
        'PLANNED': '计划中', 'BOARDING': '登机中', 'IN_FLIGHT': '飞行中',
        'ARRIVED': '已到达', 'CANCELLED': '已取消'
      };
      return statusMap[cellValue] || cellValue;
    }
  }
};
</script>

<style scoped>
.route-management {
  padding: 20px;
}
.future-flights-panel {
  padding: 15px 25px;
  background-color: #f9f9f9;
}
.future-flights-panel h4 {
  margin-bottom: 10px;
  color: #333;
}
</style>