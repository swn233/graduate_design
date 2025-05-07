<template>
  <div class="management-container">
    <el-table :data="userList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" width="220" />
      <el-table-column prop="role" label="角色" width="120" />
      <el-table-column prop="register_time" label="注册时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default>
          <el-button type="primary" size="small">编辑</el-button>
          <el-button type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      layout="prev, pager, next"
      :total="100"
      class="pagination-wrapper"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const userList = ref([
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    role: '管理员',
    register_time: '2023-01-01 10:00'
  }
])
</script>

<style scoped>
.management-container {
  padding: 20px;
}
.pagination-wrapper {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>