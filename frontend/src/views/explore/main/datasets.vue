<script setup>
import { ref } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const searchQuery = ref('')
const selectedTag = ref('全部')
const sortBy = ref('综合排序')

const tags = [
  '全部',
  '智力推理',
  '计算机视觉',
  '自然语言处理',
  '推荐系统',
  '机器学习'
]

const datasets = ref([
  {
    id: 1,
    title: 'BAAI/IndustryInstruction_Law-Justice',
    description: '行业指令_法律-正义',
    author: 'BAAI_DATA',
    createTime: '2025-03-26',
    views: 119,
    likes: 0,
    tags: ['自然语言处理']
  },
  {
    id: 2,
    title: 'BAAI/Infinity-Preference',
    description: '此数据包含59438个来自Infinity-Instruct语言模型的马尔科夫链，适用于有监督的偏好学习。',
    author: 'BAAI_DATA',
    createTime: '2025-03-26',
    views: 91,
    likes: 0,
    tags: ['智力推理']
  },
  {
    id: 3,
    title: '中文基于通用DeepSeek-R1数据集-110K-SFT',
    description: '本数据集为中文开源通用语料的数据集，数据集中不仅包含math数据，还包括大量的通用类型数据。',
    author: '柠檬茶不加冰',
    createTime: '2025-03-13',
    views: 171,
    likes: 6,
    tags: ['自然语言处理']
  }
])
</script>

<template>
  <div class="explore-container">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <el-button type="primary" class="create-btn">
        <el-icon><Plus /></el-icon>创建数据集
      </el-button>
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索数据集关键字、标签"
          :prefix-icon="Search"
          clearable
        />
      </div>
    </div>

    <!-- 标签筛选栏 -->
    <div class="filter-bar">
      <div class="tags-list">
        <el-radio-group v-model="selectedTag" class="tag-group">
          <el-radio-button 
            v-for="tag in tags" 
            :key="tag" 
            :label="tag"
          />
        </el-radio-group>
      </div>
      <el-select v-model="sortBy" class="sort-select">
        <el-option label="综合排序" value="综合排序" />
        <el-option label="最新发布" value="最新发布" />
        <el-option label="最多浏览" value="最多浏览" />
      </el-select>
    </div>

    <!-- 数据集列表 -->
    <div class="datasets-grid">
      <el-card v-for="dataset in datasets" :key="dataset.id" class="dataset-card">
        <div class="dataset-header">
          <router-link :to="`/dataset/${dataset.id}`" class="dataset-title">{{ dataset.title }}</router-link>
          <el-tag size="small" type="info">{{ dataset.tags[0] }}</el-tag>
        </div>
        <p class="dataset-description">{{ dataset.description }}</p>
        <div class="dataset-info">
          <span class="author">{{ dataset.author }}</span>
          <span class="create-time">{{ dataset.createTime }}</span>
        </div>
        <div class="dataset-stats">
          <span class="views">
            <el-icon><View /></el-icon>
            {{ dataset.views }}
          </span>
          <span class="likes">
            <el-icon><Star /></el-icon>
            {{ dataset.likes }}
          </span>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.explore-container {
  padding: 20px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 5px;
}

.search-box {
  width: 300px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tag-group {
  display: flex;
  gap: 10px;
}

.sort-select {
  width: 120px;
}

.datasets-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

@media screen and (max-width: 768px) {
  .datasets-grid {
    grid-template-columns: 1fr;
  }
}
.dataset-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.dataset-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.dataset-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.dataset-title {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  flex: 1;
  margin-right: 10px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.dataset-title:hover {
  color: #409EFF;
}
.dataset-description {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.dataset-info {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
  margin-top: 10px;
}

.dataset-stats {
  display: flex;
  gap: 15px;
  color: #999;
  font-size: 12px;
  margin-top: 10px;
}

.views, .likes {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>