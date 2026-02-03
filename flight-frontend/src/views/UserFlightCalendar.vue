<!-- 文件路径: src/views/UserFlightCalendar.vue (新文件) -->
<template>
  <div class="calendar-page-container">
    <el-card class="calendar-card">
      <div slot="header" class="clearfix">
        <span class="header-title"><i class="el-icon-date"></i> 我的航班日历</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">返回用户中心</el-button>
      </div>
      <FullCalendar ref="fullCalendar" :options="calendarOptions" />
    </el-card>

    <!-- 事件详情弹窗 -->
    <el-dialog :title="selectedEvent.title" :visible.sync="eventModalVisible" width="400px">
        <div v-if="selectedEvent.extendedProps" class="event-details">
            <p><strong>订单号:</strong> {{ selectedEvent.extendedProps.orderId }}</p>
            <p><strong>航程:</strong> {{ selectedEvent.extendedProps.from }} → {{ selectedEvent.extendedProps.to }}</p>
            <p><strong>起飞时间:</strong> {{ formatEventTime(selectedEvent.start) }}</p>
            <p><strong>降落时间:</strong> {{ formatEventTime(selectedEvent.end) }}</p>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="eventModalVisible = false">关闭</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import api from '@/api/api';
import FullCalendar from '@fullcalendar/vue';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import moment from 'moment';

export default {
  name: 'UserFlightCalendar',
  components: {
    FullCalendar,
  },
  data() {
    return {
      eventModalVisible: false,
      selectedEvent: {},
      calendarOptions: {
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth', // 默认视图为月
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay', // 提供月、周、日视图切换
        },
        locale: 'zh-cn', // 设置为中文
        buttonText: {
            today: '今天',
            month: '月',
            week: '周',
            day: '日'
        },
        events: this.fetchEvents, // 动态加载事件的函数
        eventClick: this.handleEventClick, // 事件点击处理
        eventColor: '#409EFF', // 事件的默认颜色
        height: 'auto', // 高度自适应
      },
    };
  },
  computed: {
    ...mapState(['user']),
  },
  methods: {
    async fetchEvents(fetchInfo, successCallback, failureCallback) {
      if (!this.user || !this.user.passenger?.passengerId) {
        this.$message.error('无法获取用户信息，请重新登录。');
        failureCallback(new Error('User not found'));
        return;
      }
      try {
        const passengerId = this.user.passenger.passengerId;
        const events = await api.getCalendarEventsForPassenger(passengerId); // 调用新API
        successCallback(events);
      } catch (error) {
        console.error('加载日历事件失败:', error);
        this.$message.error('加载行程日历失败！');
        failureCallback(error);
      }
    },
    handleEventClick(clickInfo) {
      this.selectedEvent = clickInfo.event;
      this.eventModalVisible = true;
    },
    goBack() {
      this.$router.push({ name: 'UserDashboard' });
    },
    formatEventTime(date) {
        return moment(date).format('YYYY-MM-DD HH:mm');
    }
  },
};
</script>

<style scoped>
.calendar-page-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}
.header-title {
    font-size: 18px;
    font-weight: bold;
}
.event-details p {
    font-size: 16px;
    line-height: 1.8;
}
/* 覆盖 FullCalendar 的一些默认样式以更好地融入 Element UI */
.calendar-card >>> .fc-button {
    background: #409EFF;
    border-color: #409EFF;
    opacity: 0.9;
}
.calendar-card >>> .fc-button:hover {
    opacity: 1;
}
.calendar-card >>> .fc-button-primary:not(:disabled).fc-button-active {
    background-color: #3a8ee6;
    border-color: #3a8ee6;
}
</style>