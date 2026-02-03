<template>
  <div class="file-management-container">
    <!-- 顶部标签页切换 -->
    <a-tabs default-active-key="profiles" @change="handleTabChange" type="card">
      <a-tab-pane key="profiles" tab="航空公司简介">
        
        <div class="split-layout">
          <!-- 左侧：航空公司列表 -->
          <div class="left-panel">
            <div class="panel-title">航空公司列表</div>
            <a-spin :spinning="loadingAirlines">
              <a-menu mode="inline" :selectedKeys="[selectedAirlineId]" @click="handleAirlineSelect">
                <a-menu-item v-for="airline in airlines" :key="airline.airlineId">
                  <a-icon type="bank" />
                  <span>{{ airline.airlineName }} ({{ airline.airlineCode }})</span>
                </a-menu-item>
              </a-menu>
            </a-spin>
          </div>

          <!-- 右侧：详情编辑区 -->
          <div class="right-panel">
            <div v-if="!selectedAirlineId" class="empty-state">
              <a-icon type="arrow-left" /> 请从左侧选择一家航空公司进行管理
            </div>
            
            <div v-else class="detail-content">
              <div class="detail-header">
                <h2>{{ currentAirline.airlineName }} - 资料管理</h2>
                <a-button type="primary" icon="save" @click="saveCurrentData">保存资料</a-button>
              </div>

              <!-- 简介编辑 -->
              <div class="section">
                <h3><a-icon type="file-text" /> 企业简介</h3>
                <a-textarea 
                  v-model="currentData.introduction" 
                  placeholder="请输入该航空公司的详细介绍..." 
                  :rows="6" 
                  style="resize: none;"
                />
              </div>

              <!-- 附件上传 -->
              <div class="section">
                <h3><a-icon type="cloud-upload" /> 多媒体附件 (PDF, Word, MP3, MP4)</h3>
                <div class="upload-bar">
                  <a-upload
                    name="file"
                    :customRequest="customUpload"
                    :showUploadList="false"
                  >
                    <a-button icon="upload" :loading="uploading">点击上传文件</a-button>
                  </a-upload>
                  <span class="upload-hint">文件将存储至 MinIO，支持预览和下载。</span>
                </div>

                <!-- 文件列表表格 -->
                <a-table 
                  :columns="fileColumns" 
                  :data-source="currentData.files" 
                  rowKey="uid" 
                  size="small" 
                  bordered
                  style="margin-top: 15px;"
                >
                  <template slot="type" slot-scope="text, record">
                    <a-tag color="red" v-if="isType(record, 'pdf')">PDF</a-tag>
                    <a-tag color="blue" v-else-if="isType(record, 'word')">WORD</a-tag>
                    <a-tag color="purple" v-else-if="isType(record, 'video')">视频</a-tag>
                    <a-tag color="green" v-else-if="isType(record, 'audio')">音频</a-tag>
                    <a-tag v-else>文件</a-tag>
                  </template>
                  <template slot="action" slot-scope="text, record">
                    <a-button type="link" size="small" @click="handlePreview(record)">预览/播放</a-button>
                    <a-button type="link" size="small" @click="handleDownload(record)">下载</a-button>
                    <a-popconfirm title="确定删除此附件吗?" @confirm="deleteFile(record.uid)">
                      <a-button type="link" size="small" style="color: #ff4d4f">删除</a-button>
                    </a-popconfirm>
                  </template>
                </a-table>
              </div>
            </div>
          </div>
        </div>

      </a-tab-pane>
      
      <!-- 公告通知 Tab (留空) -->
      <a-tab-pane key="announcements" tab="公告通知">
        <div class="empty-placeholder">
          <a-icon type="notification" style="font-size: 48px; color: #ccc;" />
          <p>公告通知模块正在开发中...</p>
        </div>
      </a-tab-pane>
    </a-tabs>

    <!-- 预览模态框 -->
    <a-modal v-model="previewVisible" :title="previewTitle" :footer="null" width="800px" destroyOnClose>
      <div class="preview-box">
        <video v-if="previewType === 'video'" :src="previewUrl" controls autoplay style="width: 100%; max-height: 500px;"></video>
        <audio v-else-if="previewType === 'audio'" :src="previewUrl" controls autoplay style="width: 100%;"></audio>
        <iframe v-else-if="previewType === 'pdf'" :src="previewUrl" style="width: 100%; height: 600px; border: none;"></iframe>
        <div v-else class="no-preview">
          <a-icon type="file-unknown" />
          <p>该格式暂不支持在线预览，请下载后查看。</p>
          <a-button type="primary" :href="previewUrl" target="_blank">下载文件</a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import api from '@/api/api';

export default {
  name: 'FileManagement',
  data() {
    return {
      activeTab: 'profiles',
      airlines: [],
      loadingAirlines: false,
      selectedAirlineId: null,
      uploading: false,
      
      // 当前编辑的数据
      currentData: {
        introduction: '',
        files: []
      },
      
      // 预览相关
      previewVisible: false,
      previewUrl: '',
      previewType: '',
      previewTitle: '',
      
      fileColumns: [
        { title: '文件名', dataIndex: 'name' },
        { title: '类型', dataIndex: 'type', scopedSlots: { customRender: 'type' }, width: 100 },
        { title: '上传时间', dataIndex: 'uploadTime', width: 180 },
        { title: '操作', key: 'action', scopedSlots: { customRender: 'action' }, width: 200 }
      ]
    };
  },
  computed: {
    currentAirline() {
      return this.airlines.find(a => a.airlineId === this.selectedAirlineId) || {};
    }
  },
  mounted() {
    this.fetchAirlines();
  },
  methods: {
    handleTabChange(key) {
      this.activeTab = key;
    },
    async fetchAirlines() {
      this.loadingAirlines = true;
      try {
        // 调用后端接口获取最新的航空公司列表，实现自动同步
        this.airlines = await api.getAirlines();
      } catch (error) {
        this.$message.error('获取航空公司列表失败');
      } finally {
        this.loadingAirlines = false;
      }
    },
    handleAirlineSelect({ key }) {
      this.selectedAirlineId = key;
      this.loadAirlineData(key);
    },
    // 加载数据 (目前使用 localStorage 模拟数据库存取)
    loadAirlineData(airlineId) {
      const storageKey = `airline_data_${airlineId}`;
      const savedData = localStorage.getItem(storageKey);
      if (savedData) {
        this.currentData = JSON.parse(savedData);
      } else {
        this.currentData = { introduction: '', files: [] };
      }
    },
    // 保存数据
    saveCurrentData() {
      if (!this.selectedAirlineId) return;
      const storageKey = `airline_data_${this.selectedAirlineId}`;
      localStorage.setItem(storageKey, JSON.stringify(this.currentData));
      this.$message.success('资料已保存');
    },
    
    // 上传文件到 MinIO
    async customUpload({ file }) {
      if (!this.selectedAirlineId) {
        this.$message.warning('请先选择一家航空公司');
        return;
      }
      this.uploading = true;
      const formData = new FormData();
      formData.append('file', file);

      try {
        const res = await api.uploadFile(formData);
        const fileName = res.fileName || res.data?.fileName;
        const url = res.url || res.data?.url;

        if (url) {
          const newFile = {
            uid: Date.now(),
            name: file.name,
            type: file.type,
            url: url,
            uploadTime: new Date().toLocaleString()
          };
          this.currentData.files.push(newFile);
          this.saveCurrentData(); // 自动保存
          this.$message.success('上传成功');
        }
      } catch (error) {
        this.$message.error('上传失败');
      } finally {
        this.uploading = false;
      }
    },
    
    // 文件操作
    deleteFile(uid) {
      this.currentData.files = this.currentData.files.filter(f => f.uid !== uid);
      this.saveCurrentData();
    },
    handleDownload(record) {
      window.open(record.url, '_blank');
    },
    handlePreview(record) {
      this.previewUrl = record.url;
      this.previewTitle = record.name;
      this.previewVisible = true;
      
      const type = record.type.toLowerCase();
      if (type.includes('video') || type.includes('mp4')) this.previewType = 'video';
      else if (type.includes('audio') || type.includes('mp3')) this.previewType = 'audio';
      else if (type.includes('pdf')) this.previewType = 'pdf';
      else this.previewType = 'other';
    },
    
    // 辅助判断类型
    isType(record, typeKey) {
        const t = record.type.toLowerCase();
        const n = record.name.toLowerCase();
        if (typeKey === 'pdf') return t.includes('pdf') || n.endsWith('.pdf');
        if (typeKey === 'video') return t.includes('video') || n.endsWith('.mp4');
        if (typeKey === 'audio') return t.includes('audio') || n.endsWith('.mp3');
        if (typeKey === 'word') return t.includes('word') || n.endsWith('.doc') || n.endsWith('.docx');
        return false;
    }
  }
};
</script>

<style scoped>
.file-management-container {
  background: #fff;
  padding: 20px;
  min-height: calc(100vh - 120px);
}

.split-layout {
  display: flex;
  height: 600px;
  border: 1px solid #e8e8e8;
}

.left-panel {
  width: 250px;
  border-right: 1px solid #e8e8e8;
  background: #fafafa;
  overflow-y: auto;
}

.panel-title {
  padding: 15px;
  font-weight: bold;
  border-bottom: 1px solid #e8e8e8;
  background: #f0f2f5;
}

.right-panel {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #fff;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  font-size: 16px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.detail-header h2 { margin: 0; }

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin-bottom: 10px;
  color: #1890ff;
}

.upload-bar {
  display: flex;
  align-items: center;
  gap: 15px;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
}

.upload-hint {
  color: #999;
  font-size: 12px;
}

.empty-placeholder {
  text-align: center;
  padding: 100px;
  color: #ccc;
}

.preview-box {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #000;
  min-height: 400px;
}

.no-preview {
  background: #fff;
  text-align: center;
  padding: 50px;
}
.no-preview i { font-size: 40px; margin-bottom: 20px; }
</style>