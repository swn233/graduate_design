<script setup>
import { ref, reactive } from 'vue'
import { Search, ArrowDown, View, Download, Star } from '@element-plus/icons-vue'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

// 模型数据
const modelList = ref([
  {
    id: 1,
    name: 'ERNIE X1',
    description: '文心大模型X1具备强大的理解、规划、反思、迭代能力，作为能力支撑的基座大模型，文心X1...',
    tags: ['文心大模型', '大语言模型'],
    otherTags: ['other'],
    studio: '飞桨AI Studio',
    views: '75k',
    likes: 4,
    downloads: 47
  },
  {
    id: 2,
    name: 'ERNIE 4.5',
    description: '文心大模型4.5是百度自主研发的新一代通用大模型，通过多个维度提升模型能力...',
    tags: ['文心大模型', '大语言模型'],
    otherTags: ['other'],
    studio: '飞桨AI Studio',
    views: '71k',
    likes: 217,
    downloads: '8.1k'
  },
  {
    id: 3,
    name: 'ERNIE 4.0 Turbo',
    description: 'ERNIE 4.0 Turbo是百度自研的编码超大规模语言模型，综合效果表现出色，广泛适用于各种...',
    tags: ['文心大模型', '大语言模型'],
    otherTags: ['other'],
    studio: '飞桨AI Studio',
    views: '63.5k',
    likes: 84,
    downloads: '2.2k',
    features: ['支持训练', '支持快速部署']
  },
  {
    id: 4,
    name: 'PP-OCRv3',
    description: 'PP-OCRv3文字检测识别系统',
    tags: ['基础模型', 'OCR', '文字检测'],
    otherTags: ['...'],
    studio: '飞桨',
    views: '46.4k',
    likes: 320,
    downloads: '9.6k',
    features: ['支持训练']
  }
])

// 任务方向选项
const taskDirections = [
  { label: '热门任务', active: true },
  { label: '多模态理解', active: false },
  { label: '基础模型', active: false },
  { label: '产业方案', active: false },
  { label: '创意工坊', active: false }
]

// 应用场景选项
const applicationScenarios = [
  { label: '应用场景', active: true },
  { label: '数据标注测试(11)', active: false },
  { label: '光学字符识别(5)', active: false },
  { label: '视觉分类(6)', active: false },
  { label: '视觉分割(10)', active: false },
  { label: '三维视觉(2)', active: false },
  { label: '生成式(2)', active: false }
]

// 自然语言处理选项
const nlpOptions = [
  { label: '自然语言处理', active: true }
]

// 搜索关键词
const searchKeyword = ref('')

// 排序方式
const sortOptions = [
  { label: '按综合排序', value: 'comprehensive' },
  { label: '按热度排序', value: 'popularity' },
  { label: '按时间排序', value: 'time' }
]
const currentSort = ref('comprehensive')

// 切换任务方向
const toggleTaskDirection = (index) => {
  taskDirections.forEach((item, i) => {
    item.active = i === index
  })
}

// 切换应用场景
const toggleApplicationScenario = (index) => {
  applicationScenarios.forEach((item, i) => {
    item.active = i === index
  })
}

// 搜索模型
const searchModels = () => {
  // 实际项目中这里会调用API进行搜索
  console.log('搜索关键词:', searchKeyword.value)
}

// 更改排序方式
const changeSort = (option) => {
  currentSort.value = option.value
  // 实际项目中这里会根据排序方式重新获取数据
  console.log('排序方式:', currentSort.value)
}

// 创建模型
const createModel = () => {
  console.log('创建模型')
  // 实际项目中这里会跳转到创建模型页面或打开创建模型对话框
}
</script>

<template>
  <div class="models-container">
    <!-- 标题和创建按钮 -->
    <div class="models-header">
      <h1 class="models-title">模型库</h1>
      <el-button type="primary" class="create-button" @click="createModel">创建模型</el-button>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <!-- 任务方向 -->
      <div class="filter-row">
        <div class="filter-tabs">
          <div 
            v-for="(item, index) in taskDirections" 
            :key="'task-' + index"
            class="filter-tab" 
            :class="{ 'active': item.active }"
            @click="toggleTaskDirection(index)"
          >
            <el-icon v-if="index === 0"><Star /></el-icon>
            {{ item.label }}
          </div>
        </div>
      </div>

      <!-- 应用场景 -->
      <div class="filter-row">
        <div class="filter-tabs">
          <div 
            v-for="(item, index) in applicationScenarios" 
            :key="'app-' + index"
            class="filter-tab" 
            :class="{ 'active': item.active }"
            @click="toggleApplicationScenario(index)"
          >
            {{ item.label }}
          </div>
        </div>
      </div>

      <!-- 自然语言处理 -->
      <div class="filter-row">
        <div class="filter-tabs">
          <div 
            v-for="(item, index) in nlpOptions" 
            :key="'nlp-' + index"
            class="filter-tab" 
            :class="{ 'active': item.active }"
          >
            {{ item.label }}
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和排序 -->
    <div class="search-sort-section">
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入关键字搜索"
          class="search-input"
          :prefix-icon="Search"
          @keyup.enter="searchModels"
        />
      </div>
      <div class="sort-box">
        <el-dropdown @command="changeSort">
          <span class="sort-dropdown">
            {{ sortOptions.find(option => option.value === currentSort).label }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item 
                v-for="option in sortOptions" 
                :key="option.value"
                :command="option"
              >
                {{ option.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 模型列表 -->
    <div class="models-grid">
      <el-card 
        v-for="model in modelList" 
        :key="model.id"
        class="model-card"
        shadow="hover"
      >
        <div class="model-header">
          <h2 class="model-title">{{ model.name }}</h2>
        </div>
        <div class="model-description">{{ model.description }}</div>
        <div class="model-tags">
          <el-tag 
            v-for="(tag, index) in model.tags" 
            :key="index"
            size="small"
            class="model-tag"
          >
            {{ tag }}
          </el-tag>
          <el-tag 
            v-if="model.otherTags"
            size="small"
            class="model-tag other-tag"
          >
            {{ model.otherTags[0] }}
          </el-tag>
        </div>
        <div class="model-features" v-if="model.features">
          <span 
            v-for="(feature, index) in model.features" 
            :key="index"
            class="feature-item"
          >
            <span class="feature-dot"></span>
            {{ feature }}
          </span>
        </div>
        <div class="model-footer">
          <div class="model-author">
            <el-avatar size="small" class="author-avatar">{{ model.studio.charAt(0) }}</el-avatar>
            <span class="author-name">{{ model.studio }}</span>
          </div>
          <div class="model-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ model.views }}
            </span>
            <span class="stat-item">
              <el-icon><Star /></el-icon>
              {{ model.likes }}
            </span>
            <span class="stat-item">
              <el-icon><Download /></el-icon>
              {{ model.downloads }}
            </span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.models-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.models-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.models-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.create-button {
  background-color: #4e6ef2;
  border-color: #4e6ef2;
}

.filter-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.filter-row {
  margin-bottom: 15px;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-tab {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #f5f7fa;
  color: #606266;
}

.filter-tab.active {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

.filter-tab .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

.search-sort-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.sort-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}

.models-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.model-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s;
}

.model-card:hover {
  transform: translateY(-5px);
}

.model-header {
  margin-bottom: 10px;
}

.model-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.model-description {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
  flex-grow: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.model-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.model-tag {
  background-color: #f0f2f5;
  color: #606266;
  border: none;
}

.other-tag {
  background-color: #f0f2f5;
  color: #909399;
}

.model-features {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 13px;
  color: #67c23a;
}

.feature-item {
  display: flex;
  align-items: center;
}

.feature-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #67c23a;
  margin-right: 6px;
}

.model-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.model-author {
  display: flex;
  align-items: center;
}

.author-avatar {
  margin-right: 8px;
  background-color: #409eff;
}

.author-name {
  font-size: 13px;
  color: #606266;
}

.model-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.stat-item .el-icon {
  margin-right: 4px;
  font-size: 14px;
}
</style>