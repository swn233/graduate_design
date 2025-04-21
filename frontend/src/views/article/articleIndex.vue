<script setup>
import { ref } from 'vue'
import { DocumentAdd } from '@element-plus/icons-vue'
import router from "@/router"

const articles = ref([
  { id: 1, title: '初探机器学习', date: '2024-03-15', views: 128 },
  { id: 2, title: 'Vue3最佳实践', date: '2024-03-14', views: 256 },
])
const postArticle = () => {
  // 跳转到文章编辑页面
  window.open(`/write`, "_blank")
  
}
</script>

<template>
  <div class="article-container">
    <div class="toolbar">
      <el-button type="primary" :icon="DocumentAdd" @click="postArticle">新建文章</el-button>
    </div>
    
    <el-table :data="articles" style="width: 100%" stripe>
      <el-table-column prop="title" label="文章标题" width="300" />
      <el-table-column prop="date" label="发布日期" width="180" />
      <el-table-column prop="views" label="浏览次数" width="120" />
      <el-table-column label="操作">
        <template #default>
          <el-button link type="primary" size="small">编辑</el-button>
          <el-button link type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.article-container {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>