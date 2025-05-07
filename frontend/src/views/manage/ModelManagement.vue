<template>
  <div class="management-container">
    <el-table :data="modelList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="模型名称" />
      <el-table-column prop="type" label="类型" width="120" />
      <el-table-column prop="version" label="版本" width="100" />
      <el-table-column prop="update_time" label="更新时间" width="180" />
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

const modelList = ref([
  {
    id: 1,
    name: '文本分类模型',
    type: 'NLP',
    version: 'v1.2.0',
    update_time: '2023-04-20 16:45'
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