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
const sortBy = ref('综合排序')

const taskTypes = ref([
  { label: '文心大模型', value: 'wenxin' },
  { label: '大语言模型', value: 'llm' },
  { label: '支持训练', value: 'training' }
])

const applicationScenes = ref([
  { label: '基础模型', value: 'base' },
  { label: '产业方案', value: 'industry' },
  { label: '创意工坊', value: 'creative' }
])

const models = ref([
  {
    id: 1,
    name: 'ERNIE X1',
    description: '文心大模型X1是最完善的模型，提供、识别、反馈、进化能力，作为能力基座面向各类场景，文心X1...',
    tags: ['文心大模型', '大语言模型'],
    author: '飞桨AI Studio',
    usage: '75k',
    likes: 4,
    views: 47
  },
  {
    id: 2,
    name: 'ERNIE 4.5',
    description: '文心大模型4.5是百度自主研发的新一代生态类模型，通过多个不同场景训练而成...',
    tags: ['文心大模型', '大语言模型'],
    author: '飞桨AI Studio',
    usage: '71k',
    likes: 217,
    views: '8.1k'
  }
])
</script>

<template>
  <div class="explore-container">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <el-button type="primary" class="create-btn">
        <el-icon><Plus /></el-icon>创建模型
      </el-button>
      <div class="search-sort">
        <el-input
          v-model="searchQuery"
          placeholder="请输入关键字搜索"
          class="search-input"
          :prefix-icon="Search"
        />
        <el-select v-model="sortBy" class="sort-select">
          <el-option label="综合排序" value="综合排序" />
          <el-option label="最新发布" value="最新发布" />
          <el-option label="最多使用" value="最多使用" />
        </el-select>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-group">
        <span class="filter-label">任务方向：</span>
        <el-radio-group v-model="taskTypes[0].value">
          <el-radio-button v-for="type in taskTypes" :key="type.value" :label="type.value">
            {{ type.label }}
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-group">
        <span class="filter-label">应用场景：</span>
        <el-radio-group v-model="applicationScenes[0].value">
          <el-radio-button v-for="scene in applicationScenes" :key="scene.value" :label="scene.value">
            {{ scene.label }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 模型卡片网格 -->
    <div class="models-grid">
      <el-card v-for="model in models" :key="model.id" class="model-card">
        <div class="model-header">
          <h3 class="model-name">{{ model.name }}</h3>
        </div>
        <p class="model-description">{{ model.description }}</p>
        <div class="model-tags">
          <el-tag v-for="tag in model.tags" :key="tag" size="small" class="tag">
            {{ tag }}
          </el-tag>
        </div>
        <div class="model-footer">
          <div class="author">
            <el-avatar :size="24" class="author-avatar">{{ model.author[0] }}</el-avatar>
            <span>{{ model.author }}</span>
          </div>
          <div class="stats">
            <span><el-icon><View /></el-icon> {{ model.views }}</span>
            <span><el-icon><Star /></el-icon> {{ model.likes }}</span>
            <span><el-icon><Download /></el-icon> {{ model.usage }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.explore-container {
  padding: 20px;
  background-color: #f5f7fa;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.create-btn {
  font-weight: 500;
}

.search-sort {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 300px;
}

.filter-section {
  background-color: white;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-group {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.filter-group:last-child {
  margin-bottom: 0;
}

.filter-label {
  color: #606266;
  margin-right: 12px;
  min-width: 70px;
}

.models-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.model-card {
  transition: transform 0.2s;
}

.model-card:hover {
  transform: translateY(-4px);
}

.model-header {
  margin-bottom: 12px;
}

.model-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.model-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.model-tags {
  margin-bottom: 16px;
}

.tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.model-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.stats {
  display: flex;
  gap: 16px;
  color: #909399;
  font-size: 13px;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>