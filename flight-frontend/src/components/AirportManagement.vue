<template>
  <div class="airport-management">
    <h2>机场管理</h2>
    
    <div class="airport-form">
      <h3>{{ editMode ? '编辑机场' : '添加机场' }}</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>机场代码</label>
          <input v-model="form.airportCode" type="text" required placeholder="如: PEK" :disabled="editMode" />
        </div>
        <div class="form-group">
          <label>机场名称</label>
          <input v-model="form.airportName" type="text" required placeholder="如: 北京首都国际机场" />
        </div>
        <div class="form-group">
          <label>城市</label>
          <input v-model="form.city" type="text" required placeholder="如: 北京" />
        </div>
        <div class="form-group">
          <label>国家</label>
          <input v-model="form.country" type="text" required placeholder="如: 中国" />
        </div>
        <button type="submit" class="submit-btn">{{ editMode ? '更新' : '添加' }}</button>
        <button v-if="editMode" @click="cancelEdit" type="button" class="cancel-btn">取消</button>
      </form>
    </div>

    <div class="airports-list">
      <h3>机场列表</h3>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="airports.length === 0" class="no-data">暂无机场数据</div>
      <table v-else class="airports-table">
        <thead>
          <tr>
            <th>代码</th>
            <th>名称</th>
            <th>城市</th>
            <th>国家</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="airport in airports" :key="airport.airportCode">
            <td>{{ airport.airportCode }}</td>
            <td>{{ airport.airportName }}</td>
            <td>{{ airport.city }}</td>
            <td>{{ airport.country }}</td>
            <td>
              <button @click="editAirport(airport)" class="edit-btn">编辑</button>
              <button @click="handleDelete(airport.airportCode)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import api from '@/api/api'; // 修正：默认导出

export default {
  name: 'AirportManagement',
  data() {
    return {
      airports: [],
      form: {
        airportCode: '',
        airportName: '',
        city: '',
        country: ''
      },
      editMode: false,
      editCode: null, // 使用code作为编辑标识
      loading: false
    }
  },
  mounted() {
    this.loadAirports()
  },
  methods: {
    async loadAirports() {
      this.loading = true
      try {
        this.airports = await api.getAirports()
      } catch (error) {
        alert('加载机场数据失败，请稍后重试')
        console.error('Error loading airports:', error)
      } finally {
        this.loading = false
      }
    },
    async handleSubmit() {
      if (!this.form.airportCode || !this.form.airportName || !this.form.city || !this.form.country) {
        alert('请填写所有必填字段')
        return
      }

      this.loading = true
      try {
        if (this.editMode) {
          await api.updateAirport(this.editCode, this.form)
          alert('机场更新成功')
        } else {
          await api.addAirport(this.form)
          alert('机场添加成功')
        }
        this.resetForm()
        await this.loadAirports()
      } catch (error) {
        alert(`操作失败: ${error.response?.data?.message || '未知错误'}`)
        console.error('Error submitting form:', error)
      } finally {
        this.loading = false
      }
    },
    editAirport(airport) {
      this.editMode = true
      this.editCode = airport.airportCode; // 使用 airportCode
      this.form = { ...airport }
    },
    async handleDelete(code) { // 参数为 code
      if (!confirm('确定要删除此机场吗？此操作不可撤销')) {
        return
      }

      this.loading = true
      try {
        await api.deleteAirport(code) // 使用 code 调用API
        alert('机场删除成功')
        await this.loadAirports()
      } catch (error) {
        alert('删除失败，请稍后重试')
        console.error('Error deleting airport:', error)
      } finally {
        this.loading = false
      }
    },
    cancelEdit() {
      this.editMode = false
      this.resetForm()
    },
    resetForm() {
      this.form = {
        airportCode: '',
        airportName: '',
        city: '',
        country: ''
      }
      this.editMode = false
      this.editCode = null;
    }
  }
}
</script>

<style scoped>
/* 样式保持不变 */
.airport-management {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
.airport-form {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  max-width: 500px;
}
.form-group {
  margin-bottom: 15px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}
.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}
.form-group input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
}
.submit-btn {
  background: #007bff;
  color: white;
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}
.submit-btn:hover {
  background: #0056b3;
}
.cancel-btn {
  background: #6c757d;
  color: white;
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.cancel-btn:hover {
  background: #5a6268;
}
.edit-btn {
  background: #28a745;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
}
.edit-btn:hover {
  background: #218838;
}
.delete-btn {
  background: #dc3545;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.delete-btn:hover {
  background: #c82333;
}
.airports-list {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
.airports-table {
  width: 100%;
  border-collapse: collapse;
}
.airports-table th,
.airports-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}
.airports-table th {
  background: #f8f9fa;
  font-weight: bold;
  color: #333;
}
.airports-table tbody tr:hover {
  background: #f1f1f1;
}
.loading, .no-data {
  text-align: center;
  padding: 20px;
  color: #666;
}
</style>