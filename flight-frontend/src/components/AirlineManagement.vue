<template>
  <div class="airline-management" style="padding: 20px;">
    
    <el-card style="margin-bottom: 20px;">
      <el-button type="primary" @click="handleOpenAdd">添加航空公司</el-button>
    </el-card>

    <el-card>
      <el-table :data="airlineList" border style="width: 100%">
        <el-table-column prop="airlineCode" label="航空公司代码" width="120" />
        <el-table-column prop="airlineName" label="航空公司名称" min-width="150" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="website" label="官网地址" min-width="180" />

        <el-table-column label="航空公司简介" min-width="250">
          <template slot-scope="scope">
            <div v-if="scope.row.files && scope.row.files.length > 0">
              <div v-for="file in scope.row.files" :key="file.id" style="margin-bottom: 5px; display: flex; align-items: center;">
                <el-tag type="info" style="margin-right: 8px;">
                  <a :href="file.fileUrl" target="_blank" style="text-decoration: none; color: inherit;">
                    📄 {{ file.fileName }}
                  </a>
                </el-tag>
                <el-button type="danger" size="mini" circle icon="el-icon-close" @click="handleDeleteFile(scope.row.airlineId, file.id)"></el-button>
              </div>
            </div>
            <div v-else style="color: #999; font-size: 12px;"></div>

            <div style="margin-top: 8px;">
              <!-- ★★★ 关键修改 1：使用 :http-request 替代 :action ★★★ -->
              <el-upload
                action="" 
                :http-request="customUploadRequest"
                :data="{ airlineId: scope.row.airlineId }"
                :show-file-list="false"
              >
                <el-button size="small" type="success" plain>上传资料</el-button>
              </el-upload>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(scope.row.airlineId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="isEditMode ? '编辑航空公司' : '添加航空公司'" :visible.sync="dialogVisible" width="500px">
      <el-form :model="formAirline" label-width="120px">
        <el-form-item label="航空公司代码">
          <el-input v-model="formAirline.airlineCode" placeholder="例如: MU" />
        </el-form-item>
        <el-form-item label="航空公司名称">
          <el-input v-model="formAirline.airlineName" placeholder="例如: 中国东方航空" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formAirline.contactPhone" />
        </el-form-item>
        <el-form-item label="官网地址">
          <el-input v-model="formAirline.website" placeholder="例如: https://www.ceair.com" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AirlineManagement',
  data() {
    return {
      airlineList: [],
      dialogVisible: false,
      isEditMode: false,
      formAirline: {
        airlineId: null,
        airlineCode: '',
        airlineName: '',
        contactPhone: '',
        website: '',
      },
    };
  },
  // computed 部分不再需要 uploadHeaders，因为我们会在 axios 请求中手动设置
  created() {
    this.fetchAirlines();
  },
  methods: {
    // ★★★ 关键修改 2：添加自定义上传方法 ★★★
    async customUploadRequest(options) {
      const { file, data } = options;
      const formData = new FormData();
      formData.append('file', file);
      formData.append('airlineId', data.airlineId);

      // 从 localStorage 获取 Token
      const token = localStorage.getItem('token'); 

      try {
        await axios.post('http://localhost:8080/api/airlines/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}` // 手动添加认证头
          }
        });
        // 上传成功后，手动调用成功逻辑
        this.handleUploadSuccess();
      } catch (error) {
        // 上传失败后，手动调用失败逻辑
        console.error('Upload failed:', error);
        this.handleUploadError();
      }
    },
    
    // ... (fetchAirlines, handleOpenAdd, handleEdit, handleSubmit, handleDelete 方法保持不变) ...

    async fetchAirlines() {
      try {
        const response = await axios.get('http://localhost:8080/api/airlines');
        this.airlineList = response.data;
      } catch (error) {
        this.$message.error('加载航空公司列表失败');
        console.error(error);
      }
    },
    handleOpenAdd() {
      this.isEditMode = false;
      this.formAirline = {
        airlineId: null,
        airlineCode: '',
        airlineName: '',
        contactPhone: '',
        website: '',
      };
      this.dialogVisible = true;
    },
    handleEdit(row) {
      this.isEditMode = true;
      this.formAirline = { ...row };
      this.dialogVisible = true;
    },
    async handleSubmit() {
      try {
        if (this.isEditMode) {
          await axios.put(`http://localhost:8080/api/airlines/${this.formAirline.airlineId}`, this.formAirline);
          this.$message.success('更新成功');
        } else {
          await axios.post('http://localhost:8080/api/airlines', this.formAirline);
          this.$message.success('添加成功');
        }
        this.dialogVisible = false;
        this.fetchAirlines();
      } catch (error) {
        this.$message.error('操作失败');
        console.error(error);
      }
    },
    handleDelete(id) {
      this.$confirm('确定要删除该航空公司吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.delete(`http://localhost:8080/api/airlines/${id}`);
          this.$message.success('删除成功');
          this.fetchAirlines();
        } catch (error) {
          this.$message.error('删除失败');
          console.error(error);
        }
      }).catch(() => {});
    },

    // 成功和失败的回调现在由 customUploadRequest 手动调用
    handleUploadSuccess() {
      this.$message.success('文件上传成功');
      this.fetchAirlines(); // 重新加载列表以显示新文件
    },
    handleUploadError() {
      this.$message.error('文件上传失败，请检查后端服务或网络连接');
    },

    handleDeleteFile(airlineId, fileId) {
       this.$confirm('确定要删除该文件吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
       }).then(async () => {
         try {
           await axios.delete(`http://localhost:8080/api/airlines/files/${fileId}`);
           this.$message.success('文件已删除');
           this.fetchAirlines();
         } catch(e) {
            this.$message.error('删除失败');
         }
       }).catch(()=>{});
    }
  }
};
</script>

<style scoped>
.airline-management {
  background-color: #f7f8fa;
}
</style>