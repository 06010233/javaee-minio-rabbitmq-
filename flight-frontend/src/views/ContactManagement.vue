<!-- 文件路径: src/views/ContactManagement.vue -->
<template>
  <div class="contact-management-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix header-content">
        <span class="header-title">常用乘机人</span>
        <el-button type="warning" size="small" icon="el-icon-plus" @click="openAddDialog">添加乘机人</el-button>
      </div>

      <!-- 列表区域 -->
      <el-table :data="contacts" style="width: 100%" v-loading="loading" stripe :header-cell-style="{background:'#f5f7fa'}">
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column label="证件类型" width="150">
          <template>居民身份证</template>
        </el-table-column>
        <el-table-column prop="idCard" label="证件号码" width="220"></el-table-column>
        <el-table-column prop="phone" label="手机号码" width="150"></el-table-column>
        <el-table-column label="旅客类型">
          <template>成人</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-popconfirm title="确定删除该乘机人吗？" @confirm="handleDelete(scope.row.passengerId)">
              <el-button slot="reference" type="text" class="delete-text">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 仿 12306 添加弹窗 -->
    <el-dialog title="添加乘机人" :visible.sync="dialogVisible" width="520px" :close-on-click-modal="false" custom-class="add-dialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" size="medium" class="add-form">
        
        <!-- 证件类型 (固定显示，为了UI一致性) -->
        <el-form-item label="证件类型" required>
          <el-select v-model="fixedIdType" disabled style="width: 100%;">
            <el-option label="居民身份证" value="ID_CARD"></el-option>
          </el-select>
        </el-form-item>

        <!-- 姓名 -->
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
          <div class="form-tip">姓名填写规则 (用于身份核验)</div>
        </el-form-item>

        <!-- 证件号码 -->
        <el-form-item label="证件号码" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入证件号码" maxlength="18"></el-input>
          <div class="form-tip">用于身份核验，请正确填写</div>
        </el-form-item>

        <!-- 手机号码 -->
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="11">
             <template slot="prepend">+86</template>
          </el-input>
          <div class="form-tip">请填写真实有效的联系方式</div>
        </el-form-item>

        <!-- 优惠类型 (固定显示) -->
        <el-form-item label="优惠(待)类型">
           <el-select v-model="fixedPassengerType" style="width: 100%;">
            <el-option label="成人" value="ADULT"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="medium">取 消</el-button>
        <el-button type="warning" @click="handleSubmit" :loading="submitting" size="medium">保 存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api/api';
import { mapState } from 'vuex';

export default {
  name: 'ContactManagement',
  data() {
    return {
      contacts: [],
      loading: false,
      dialogVisible: false,
      submitting: false,
      // 辅助显示字段
      fixedIdType: 'ID_CARD',
      fixedPassengerType: 'ADULT',
      // 表单数据
      form: {
        name: '',
        idCard: '',
        phone: ''
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        idCard: [
          { required: true, message: '请输入证件号码', trigger: 'blur' },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证格式不正确', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    ...mapState(['user'])
  },
  created() {
    this.fetchContacts();
  },
  methods: {
    async fetchContacts() {
      if (!this.user) return;
      this.loading = true;
      try {
        this.contacts = await api.getContacts(this.user.userId);
      } catch (error) {
        this.$message.error('数据加载失败');
      } finally {
        this.loading = false;
      }
    },
    openAddDialog() {
      this.form = { name: '', idCard: '', phone: '' };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.formRef.clearValidate();
      });
    },
    handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            // 调用之前写好的后端逻辑：自动注册或关联
            await api.addContact(this.user.userId, this.form);
            this.$message.success('保存成功');
            this.dialogVisible = false;
            this.fetchContacts();
          } catch (error) {
            this.$message.error(error.response?.data?.message || '保存失败，请检查输入');
          } finally {
            this.submitting = false;
          }
        }
      });
    },
    async handleDelete(passengerId) {
      try {
        await api.removeContact(this.user.userId, passengerId);
        this.$message.success('删除成功');
        this.fetchContacts();
      } catch (error) {
        this.$message.error('删除失败');
      }
    }
  }
};
</script>

<style scoped>
.contact-management-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
.delete-text {
  color: #F56C6C;
}
.delete-text:hover {
  color: #ff4949;
}

/* 弹窗和表单微调，模仿12306的橙色风格 */
.form-tip {
  font-size: 12px;
  color: #ff9900;
  line-height: 1.5;
  margin-top: 2px;
}
</style>