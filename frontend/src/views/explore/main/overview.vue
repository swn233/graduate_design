<script setup>
import { ref, onMounted } from 'vue'
import { Search, View, Picture, Star } from '@element-plus/icons-vue'
import { fetchCarouselArticles } from '@/net/article'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const searchQuery = ref('')

// 轮播图数据
const carouselItems = ref([])

// 获取轮播图数据
const loadCarouselData = async () => {
  try {
    const response = await fetchCarouselArticles()
    console.log('完整接口响应:', response)
    carouselItems.value = response
    console.log('处理后的轮播数据:', carouselItems.value)
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  loadCarouselData()
})

// 热门应用数据
const hotApps = ref([
  {
    id: 1,
    title: 'smartpaper | arxiv一键读懂',
    icon: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=50&h=50&fit=crop&q=80',
    author: '复可',
    views: 4300,
    likes: 36
  },
  {
    id: 2,
    title: 'DeepResearch | 实现AI论文解读',
    icon: 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=50&h=50&fit=crop&q=80',
    author: '魔术师',
    views: 4000,
    likes: 15
  },
  {
    id: 3,
    title: 'DeepSeek R1 | Chat集成工具',
    icon: 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=50&h=50&fit=crop&q=80',
    author: 'Maplemx',
    views: 6400,
    likes: 16
  },
  {
    id: 4,
    title: 'AI绘画 | 一键生成精美图像',
    icon: 'https://images.unsplash.com/photo-1681319350180-6c7844a09c86?w=50&h=50&fit=crop&q=80',
    author: '飞速',
    views: 14000,
    likes: 42
  }
])

// 案例数据
const cases = ref([
  {
    title: '智能图像识别系统',
    category: '计算机视觉',
    type: 'success',
    description: '基于深度学习的图像识别系统，可以准确识别和分类各种物体、场景和人脸。',
    views: 1234
  },
  {
    title: '自然语言处理助手',
    category: 'NLP',
    type: 'warning',
    description: '智能文本分析和处理系统，支持多语言翻译、情感分析和文本摘要。',
    views: 856
  },
  {
    title: '智能推荐系统',
    category: '机器学习',
    type: 'danger',
    description: '基于用户行为的个性化推荐系统，提供精准的内容和产品推荐。',
    views: 2045
  }
])
</script>

<template>
  <div class="explore-container">
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="输入AI应用关键词"
        class="search-input"
        clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 轮播图部分 -->
    <div class="carousel-section">
      <el-carousel :interval="4000" type="card" height="200px">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <div class="carousel-content" :style="{backgroundImage: `url(${item.image})`}">
            <h3 class="carousel-title">{{ item.title }}</h3>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 热门应用部分 -->
    <div class="hot-apps-section">
      <div class="section-header">
        <h2 class="section-title"><el-icon><Star /></el-icon> 热门应用</h2>
      </div>

      <div class="hot-apps-wrapper">
        <div class="hot-apps-grid" :class="{ 'collapsed': props.isCollapse, 'expanded': !props.isCollapse }">
          <el-card v-for="app in hotApps" :key="app.id" class="hot-app-card" shadow="hover">
            <div class="hot-app-content">
              <div class="hot-app-icon">
                <img :src="app.icon" :alt="app.title">
              </div>
              <div class="hot-app-info">
                <h3 class="hot-app-title">{{ app.title }}</h3>
                <div class="hot-app-author">{{ app.author }}</div>
              </div>
            </div>
            <div class="hot-app-footer">
              <span class="hot-app-stats">
                <span class="hot-app-views"><el-icon><View /></el-icon> {{ app.views }}</span>
                <span class="hot-app-likes"><el-icon><Star /></el-icon> {{ app.likes }}</span>
              </span>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- AI案例部分 -->
    <div class="cases-section">
      <div class="section-header">
        <h2 class="section-title"><el-icon><Picture /></el-icon> AI案例展示</h2>
      </div>
      <div class="cases-wrapper">
        <div class="cases-grid" :class="{ 'collapsed': props.isCollapse, 'expanded': !props.isCollapse }">
          <el-card v-for="(item, index) in cases" :key="index" class="case-card">
            <template #header>
              <div class="card-header">
                <h3>{{ item.title }}</h3>
                <el-tag size="small" :type="item.type">{{ item.category }}</el-tag>
              </div>
            </template>
            <p class="case-description">{{ item.description }}</p>
            <div class="case-footer">
              <el-button type="primary" size="small">查看详情</el-button>
              <span class="view-count">
                <el-icon><View /></el-icon>
                {{ item.views }}
              </span>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.explore-container {
  padding: 20px;
}

.search-section {
  margin-bottom: 20px;
}

.search-input {
  max-width: 600px;
  border-radius: 4px;
}

/* 轮播图样式 */
.carousel-section {
  margin-bottom: 30px;
}

.carousel-content {
  height: 100%;
  background-size: cover;
  background-position: center;
  border-radius: 8px;
  display: flex;
  align-items: flex-end;
  padding: 20px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.carousel-title {
  margin-bottom: 40px;
  font-size: 20px;
  font-weight: bold;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  color: white;
}

/* 热门应用样式 */
.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.hot-apps-section {
  margin-bottom: 30px;
}

.hot-apps-wrapper {
  width: 100%;
  transition: all 0.3s ease;
  padding-right: 16px;
}

.hot-apps-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  transition: all 0.3s ease;
}

.hot-apps-grid.collapsed {
  grid-template-columns: repeat(4, 1fr);
  margin-left: 0;
}

.hot-apps-grid.expanded {
  grid-template-columns: repeat(4, 1fr);
  margin-left: 0;
}

@media (max-width: 1400px) {
  .hot-apps-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1100px) {
  .hot-apps-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.hot-app-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

.hot-app-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.hot-app-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.hot-app-icon {
  width: 40px;
  height: 40px;
  margin-right: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.hot-app-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hot-app-info {
  flex: 1;
}

.hot-app-title {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.hot-app-author {
  font-size: 12px;
  color: #999;
}

.hot-app-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.hot-app-stats {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 12px;
}

.hot-app-views, .hot-app-likes {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* AI案例部分 */
.cases-section {
  margin-bottom: 30px;
}

.cases-wrapper {
  width: 100%;
  transition: all 0.3s ease;
  padding-right: 16px;
}

.cases-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  transition: all 0.3s ease;
}

.cases-grid.collapsed {
  grid-template-columns: repeat(2, 1fr);
  margin-left: 0;
}

.cases-grid.expanded {
  grid-template-columns: repeat(2, 1fr);
  margin-left: 0;
}

@media (max-width: 1400px) {
  .cases-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 900px) {
  .cases-grid {
    grid-template-columns: repeat(1, 1fr);
  }
}

.case-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

.case-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.case-description {
  color: #666;
  margin: 10px 0;
  line-height: 1.5;
}

.case-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.view-count {
  color: #999;
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>