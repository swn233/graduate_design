<script setup>
import { ref, reactive } from 'vue'
import { Search, Plus, Star, View, Download } from '@element-plus/icons-vue'

// 模拟数据集数据
const datasets = reactive([
  {
    id: 1,
    title: 'BAAI/IndustryInstruction_Law-Justice',
    description: '行业指导_法律-正义',
    author: 'BAAI_DATA',
    verified: true,
    date: '2025-03-26',
    views: 119,
    downloads: 0,
    likes: 0,
    tags: ['整理', '生成']
  },
  {
    id: 2,
    title: 'BAAI/Infinity-Preference',
    description: '此数据包含5438个来自Infinity-Instruct语言模型的对齐特性数据，适用于各种任务类型，备选项全都附从Gemma-2-9B-IT...',
    author: 'BAAI_DATA',
    verified: true,
    date: '2025-03-26',
    views: 91,
    downloads: 0,
    likes: 0,
    tags: ['整理', '生成']
  },
  {
    id: 3,
    title: '中文基于满血DeepSeek-R1预训练数据集-110K-SFT',
    description: '本数据集为中文开源预训练的1K数据集，数据集中不仅包含math数据，还包括大量的通用类型数据，总数量为110K，注意...',
    author: '柠檬茶不加冰',
    verified: true,
    date: '2025-03-13',
    views: 171,
    downloads: 0,
    likes: 6,
    tags: ['对话系统', '自然语言处理']
  },
  {
    id: 4,
    title: 'summarize_from_feedback',
    description: 'openai为支持建模发布的人类反馈数据集',
    author: 'openai',
    verified: false,
    date: '2025-03-31',
    views: 45,
    downloads: 0,
    likes: 1,
    tags: []
  }
])

// 筛选和排序
const activeTab = ref('全部')
const tabs = ['全部', '官方推荐', '计算机视觉', '自然语言处理', '推荐系统', '机器学习']
const sortOptions = ref('综合排序')
const searchQuery = ref('')
</script>

<template>
  <div class="datasets-container">
    <!-- 标题和创建按钮 -->
    <div class="header">
      <h1 class="title">数据集</h1>
      <el-button type="primary" class="create-btn">
        <el-icon><Plus /></el-icon>创建数据集
      </el-button>
    </div>

    <!-- 标签筛选栏 -->
    <div class="filter-tabs">
      <el-tabs v-model="activeTab" class="demo-tabs">
        <el-tab-pane 
          v-for="tab in tabs" 
          :key="tab" 
          :label="tab" 
          :name="tab">
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 排序和搜索 -->
    <div class="sort-search">
      <el-select v-model="sortOptions" class="sort-select">
        <el-option label="综合排序" value="综合排序" />
        <el-option label="最新发布" value="最新发布" />
        <el-option label="最多浏览" value="最多浏览" />
        <el-option label="最多下载" value="最多下载" />
      </el-select>
      
      <el-input
        v-model="searchQuery"
        placeholder="搜索数据集关键字、标签..."
        class="search-input"
        clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 数据集列表 -->
    <div class="dataset-list">
      <el-card 
        v-for="dataset in datasets" 
        :key="dataset.id"
        class="dataset-card"
        shadow="hover">
        <div class="dataset-header">
          <div class="dataset-icon">{{ dataset.title.charAt(0) }}</div>
          <div class="dataset-title-container">
            <h3 class="dataset-title">{{ dataset.title }}</h3>
            <p class="dataset-date">{{ dataset.date }}</p>
          </div>
        </div>
        
        <p class="dataset-description">{{ dataset.description }}</p>
        
        <div class="dataset-author">
          <span class="author-name">{{ dataset.author }}</span>
          <el-tag v-if="dataset.verified" size="small" type="warning" class="verified-tag">认证</el-tag>
        </div>
        
        <div class="dataset-tags">
          <el-tag 
            v-for="(tag, index) in dataset.tags" 
            :key="index"
            size="small"
            class="dataset-tag">
            {{ tag }}
          </el-tag>
        </div>
        
        <div class="dataset-stats">
          <div class="stat-item">
            <el-icon><View /></el-icon>
            <span>{{ dataset.views }}</span>
          </div>
          <div class="stat-item">
            <el-icon><Download /></el-icon>
            <span>{{ dataset.downloads }}</span>
          </div>
          <div class="like-button" :class="{ 'liked': dataset.likes > 0 }">
            <el-button text>
              <el-icon><Star /></el-icon>
              <span>{{ dataset.likes }}</span>
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.datasets-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.create-btn {
  font-weight: 500;
}

.filter-tabs {
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 4px;
  padding: 0 15px;
}

.sort-search {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.sort-select {
  width: 150px;
}

.search-input {
  width: 300px;
}

.dataset-list {
  display: grid;
  grid-template-columns: repeat(2,1fr);
  gap: 20px;
}

.dataset-card {
  transition: transform 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.dataset-card:hover {
  transform: translateY(-5px);
}

.dataset-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.dataset-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  margin-right: 12px;
}

.dataset-title-container {
  flex: 1;
}

.dataset-title {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dataset-date {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.dataset-description {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.dataset-author {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.author-name {
  font-size: 14px;
  color: #409eff;
  font-weight: 500;
  margin-right: 8px;
}

.verified-tag {
  background-color: transparent;
  border-color: #e6a23c;
  color: #e6a23c;
}

.dataset-tags {
  margin-bottom: 15px;
}

.dataset-tag {
  margin-right: 8px;
  margin-bottom: 8px;
  background-color: #f0f2f5;
  color: #606266;
  border: none;
}

.dataset-stats {
  display: flex;
  align-items: center;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-right: 15px;
  font-size: 13px;
  color: #909399;
}

.stat-item .el-icon {
  margin-right: 4px;
  font-size: 16px;
}

.like-button {
  margin-left: auto;
}

.like-button .el-button {
  color: #909399;
  padding: 0;
  display: flex;
  align-items: center;
}

.like-button.liked .el-button {
  color: #e6a23c;
}
</style>