<template>
  <div class="management-container">
    <el-table :data="articleList" style="width: 100%" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="publish_time" label="发布时间" width="180" />
      <el-table-column prop="view_count" label="浏览量" width="100" />
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

const articleList = ref([
  {
    id: 1,
    title: '示例文章',
    author: '管理员',
    publish_time: '2023-03-10 09:30',
    view_count: 1500
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