<template>
  <div class="revenue-dashboard">
    <h2>收益统计</h2>
    <a-row :gutter="24">
      <a-col :xs="24" :lg="12">
        <div class="chart-container">
          <h3>月度总收益</h3>
          <v-chart v-if="!loading" class="chart" :option="monthlyChartOptions" autoresize />
          <a-skeleton v-if="loading" active />
        </div>
      </a-col>
      <a-col :xs="24" :lg="12">
        <div class="chart-container">
          <h3>航空公司收益分布</h3>
          <v-chart v-if="!loading" class="chart" :option="airlineChartOptions" autoresize />
          <a-skeleton v-if="loading" active />
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import api from '@/api/api';

export default {
  name: 'RevenueDashboard',
  data() {
    return {
      loading: true,
      monthlyChartOptions: {},
      airlineChartOptions: {},
    };
  },
  async mounted() {
    await this.loadChartData();
  },
  methods: {
    async loadChartData() {
      this.loading = true;
      try {
        const [monthlyData, airlineData] = await Promise.all([
          api.getMonthlyRevenue(),
          api.getAirlineRevenue(),
        ]);
        this.setupMonthlyChart(monthlyData);
        this.setupAirlineChart(airlineData);
      } catch (error) {
        this.$message.error('图表数据加载失败');
      } finally {
        this.loading = false;
      }
    },
    setupMonthlyChart(data) {
      this.monthlyChartOptions = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        xAxis: {
          type: 'category',
          data: data.map(item => item.month),
        },
        yAxis: { type: 'value', name: '收益 (元)' },
        series: [{
          data: data.map(item => item.revenue),
          type: 'bar',
          showBackground: true,
          backgroundStyle: { color: 'rgba(180, 180, 180, 0.2)' }
        }],
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      };
    },
    setupAirlineChart(data) {
      this.airlineChartOptions = {
        tooltip: { trigger: 'item', formatter: '{a} <br/>{b} : {c}元 ({d}%)' },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: data.map(item => item.airlineName),
        },
        series: [{
          name: '收益来源',
          type: 'pie',
          radius: '70%',
          center: ['60%', '50%'],
          data: data.map(item => ({ value: item.revenue, name: item.airlineName })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        }],
      };
    },
  },
};
</script>

<style scoped>
.revenue-dashboard { padding: 24px; }
.chart-container {
  background: #fff;
  padding: 16px;
  border-radius: 4px;
  margin-bottom: 24px;
}
.chart-container h3 {
  margin-bottom: 16px;
}
.chart {
  height: 400px;
}
</style>