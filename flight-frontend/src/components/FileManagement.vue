<!-- 最终的、完整正确的文件: src/components/FileManagement.vue (已修复 handleEdit bug) -->

<template>
  <div class="announcement-management">
    <!-- 模块一：发布/编辑公告的表单 (仅管理员可见) -->
    <el-card class="box-card" v-if="isAdmin">
      <div slot="header" class="clearfix">
        <span>{{ form.id ? '编辑公告' : '发布新公告' }}</span>
        <el-button v-if="form.id" style="float: right; padding: 3px 0" type="text" @click="resetForm">
          取消编辑
        </el-button>
      </div>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" v-loading="formLoading">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input type="textarea" :rows="5" placeholder="请输入详细的公告内容" v-model="form.content"></el-input>
        </el-form-item>
        <el-form-item label="上传附件">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :on-error="handleUploadError"
            :file-list="fileList"
            :auto-upload="true"
            multiple>
            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
            <div class="el-upload__tip" slot="tip">可以上传多个附件。文件选择后将自动上传。</div>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">
            {{ form.id ? '确认修改' : '立即发布' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 模块二：公告发布历史 (所有人都可见) -->
    <el-card class="box-card">
        <div slot="header" class="clearfix">
            <span>公告发布历史</span>
        </div>
        <el-table :data="announcementList" style="width: 100%" v-loading="tableLoading">
            <el-table-column prop="title" label="标题" min-width="300"></el-table-column>
            <el-table-column prop="createdAt" label="发布时间" width="200"></el-table-column>
            <el-table-column label="操作" width="200" align="center">
                <template slot-scope="scope">
                    <el-button @click="showDetails(scope.row)" type="text" size="small">查看详情</el-button>
                    <el-button v-if="isAdmin" @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
                    <el-button v-if="isAdmin" @click="handleDelete(scope.row.id)" type="text" size="small" style="color: #F56C6C;">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <!-- 详情弹窗 Dialog -->
    <el-dialog :title="currentAnnouncementDetails.title" :visible.sync="dialogVisible" width="60%">
      <div class="detail-content" v-if="currentAnnouncementDetails.id">
        <h4>公告内容：</h4>
        <p class="content-text">{{ currentAnnouncementDetails.content }}</p>
        <el-divider></el-divider>
        <h4>附件列表：</h4>
        <div v-if="currentAnnouncementDetails.attachments && currentAnnouncementDetails.attachments.length > 0">
          <ul class="attachment-list">
            <li v-for="file in currentAnnouncementDetails.attachments" :key="file.id">
              <el-link :href="file.filePath" type="primary" target="_blank" icon="el-icon-document">{{ file.fileName }}</el-link>
            </li>
          </ul>
        </div>
        <div v-else>
          <p>无附件</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';
import { mapGetters } from 'vuex';

export default {
  name: 'AnnouncementManagement',
  data() {
    return {
      tableLoading: false,
      formLoading: false,
      dialogVisible: false,
      form: {
        id: null,
        title: '',
        content: '',
        attachments: []
      },
      currentAnnouncementDetails: {},
      fileList: [],
      rules: {
        title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
      },
      announcementList: [],
      uploadUrl: 'http://localhost:8080/api/files/upload' 
    };
  },
  computed: {
    ...mapGetters(['isAdmin'])
  },
  created() {
    this.fetchAnnouncements();
  },
  methods: {
    async fetchAnnouncements() {
      this.tableLoading = true;
      try {
        const response = await axios.get('/announcements');
        this.announcementList = response.data;
      } catch (error) {
        this.$message.error('获取公告列表失败');
        console.error("获取公告列表失败:", error);
      } finally {
        this.tableLoading = false;
      }
    },
    handleUploadSuccess(response, file) {
      this.$message.success(`文件 [${file.name}] 上传成功!`);
      this.form.attachments.push({
        fileName: response.data.name,
        filePath: response.data.url
      });
      const uploadedFile = this.fileList.find(f => f.uid === file.uid);
      if (uploadedFile) {
        uploadedFile.serverInfo = response.data;
      }
    },
    handleRemove(file) {
      const fileInfo = file.serverInfo || (file.response && file.response.data);
      if (fileInfo) {
        this.form.attachments = this.form.attachments.filter(att => att.filePath !== fileInfo.url);
      }
      this.$message.info(`文件 [${file.name}] 已移除`);
    },
    handleUploadError(err) {
      this.$message.error('文件上传失败，请检查后端服务');
      console.error("文件上传失败:", err);
    },
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.formLoading = true;
          try {
            if (this.form.id) {
              await axios.put(`/announcements/${this.form.id}`, this.form);
              this.$message.success('更新成功！');
            } else {
              await axios.post('/announcements', this.form);
              this.$message.success('发布成功！');
            }
            this.resetForm();
            this.fetchAnnouncements();
          } catch (error) {
            this.$message.error('操作失败，请查看控制台');
             console.error("操作失败:", error);
          } finally {
            this.formLoading = false;
          }
        }
      });
    },
    showDetails(row) {
      this.currentAnnouncementDetails = row;
      this.dialogVisible = true;
    },
    handleEdit(row) {
      // 深拷贝，防止在表单中修改时影响到列表的原始数据
      this.form = JSON.parse(JSON.stringify(row));
      
      // ★★★ 核心修复就在这里 ★★★
      // 这段代码的作用是：检查从后端来的数据里有没有 attachments 数组，
      // 如果没有（比如那条公告确实没附件），就手动给它创建一个空数组。
      // 这样，后面的 .push() 操作就永远不会失败。
      if (!this.form.attachments) {
        this.form.attachments = [];
      }
      
      this.fileList = this.form.attachments.map((att, index) => ({
        uid: `edit-${att.id || index}`,
        name: att.fileName,
        url: att.filePath,
        status: 'success',
        serverInfo: { name: att.fileName, url: att.filePath }
      }));
    },
    resetForm() {
      this.$refs.form.resetFields();
      this.form = { id: null, title: '', content: '', attachments: [] };
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles();
      }
      this.fileList = [];
    },
    handleDelete(id) {
      this.$confirm('此操作将永久删除该公告, 是否继续?', '危险操作', {
        /* ... */
      }).then(async () => {
        try {
            await axios.delete(`/announcements/${id}`);
            this.$message.success('删除成功!');
            this.fetchAnnouncements();
        } catch (error) {
            this.$message.error('删除失败');
        }
      }).catch(() => { /* ... */ });
    }
  }
};
</script>

<style scoped>
/* ...样式不变... */
.detail-content .content-text {
  white-space: pre-wrap;
  line-height: 1.6;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  color: #333;
}
.attachment-list {
  list-style: none;
  padding-left: 0;
}
.attachment-list li {
  margin-bottom: 8px;
}
</style>