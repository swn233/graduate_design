<template>
  <div class="management-container">
    <el-table :data="datasetList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="数据集名称" />
      <el-table-column prop="type" label="类型" width="120" />
      <el-table-column prop="size" label="大小" width="120" />
      <el-table-column prop="create_time" label="创建时间" width="180" />
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

const datasetList = ref([
  {
    id: 1,
    name: '示例数据集',
    type: '文本',
    size: '1.2GB',
    create_time: '2023-02-15 14:30'
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