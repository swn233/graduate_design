<script setup>
import { ref, onMounted } from 'vue'
import { Search, View, Star, Reading, Collection, Histogram, Connection } from '@element-plus/icons-vue'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const searchQuery = ref('')

// 当前选中的轮播项
const activeCarouselIndex = ref(0)

// 轮播图数据
const carouselItems = ref([
  {
    id: 1,
    title: '目标检测之YOLO系列',
    subtitle: '从0到1学习目标检测 完整版',
    description: '匠心之作 系统教学',
    image: 'https://images.unsplash.com/photo-1591696331111-ef9586a5b17a?q=80&w=1470&auto=format&fit=crop',
    buttonText: '立即学习'
  },
  {
    id: 2,
    title: '大模型微调技术',
    subtitle: '从理论到实践的全面指南',
    description: '掌握PEFT、LoRA等前沿技术',
    image: 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=1470&h=380&fit=crop&q=80',
    buttonText: '开始学习'
  },
  {
    id: 3,
    title: 'AI应用开发实战',
    subtitle: '从零构建企业级AI应用',
    description: '实用案例 代码详解',
    image: 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=1470&h=380&fit=crop&q=80',
    buttonText: '查看课程'
  }
])

// AI课程分类
const categories = ref([
  {
    title: '大模型',
    description: 'LLM/RAG/多模态',
    icon: 'Reading'
  },
  {
    title: '提示词工程',
    description: 'Prompt Engineering',
    icon: 'Collection'
  },
  {
    title: 'AI应用开发',
    description: 'AI原生应用开发实战',
    icon: 'Connection'
  },
  {
    title: 'AI行业案例',
    description: '产业级AI应用案例分享',
    icon: 'Histogram'
  },
  {
    title: 'AI模型部署',
    description: '推理/部署/硬件',
    icon: 'Setting'
  },
  {
    title: '深度学习',
    description: 'CV/NLP/AI4S/RL',
    icon: 'View'
  },
  {
    title: '机器学习',
    description: 'Python/人工智能入门课',
    icon: 'Star'
  }
])

// 进站必学课程
const essentialCourses = ref([
  {
    id: 1,
    title: 'Prompt工程: 大模型沟通指南',
    image: 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=300&h=180&fit=crop&q=80',
    description: '快速掌握人工智能大模型的基本沟通技巧，提高AI输出质量'
  },
  {
    id: 2,
    title: '千帆AppBuilder实操课程',
    image: 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=300&h=180&fit=crop&q=80',
    description: '本课程将带领大家系统了解千帆AppBuilder平台的使用方法'
  },
  {
    id: 3,
    title: '零门槛开发产业级 AI 模型',
    image: 'https://images.unsplash.com/photo-1677442136019-21780ecad995?w=300&h=180&fit=crop&q=80',
    description: '百度深研发工程师手把手带你入门AI模型开发与部署'
  }
])

const handleCarouselClick = (id) => {
  console.log('点击了轮播图', id)
}

const handleCarouselHover = (index) => {
  activeCarouselIndex.value = index
}

const handleCategoryClick = (category) => {
  console.log('选择了分类', category)
}

const handleCourseClick = (course) => {
  console.log('点击了课程', course)
}
</script>

<template>
  <div class="learn-container">
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="搜索课程、教程或学习资料"
        class="search-input"
        clearable>
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 轮播图部分 -->
    <div class="carousel-section">
      <div class="carousel-container">
        <div class="carousel-main">
          <el-carousel :interval="4000" :autoplay="false" indicator-position="none" height="240px">
            <el-carousel-item v-for="(item, index) in carouselItems" :key="item.id" @click="handleCarouselClick(item.id)">
              <div class="carousel-content" :style="{backgroundImage: `url(${item.image})`}">
                <div class="carousel-text">
                  <h2 class="carousel-title">{{ item.title }}</h2>
                  <p class="carousel-subtitle">{{ item.subtitle }}</p>
                  <p class="carousel-description">{{ item.description }}</p>
                  <el-button type="primary" class="carousel-button" round>{{ item.buttonText }}</el-button>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
        <div class="carousel-list">
          <div 
            v-for="(item, index) in carouselItems" 
            :key="item.id" 
            class="carousel-list-item" 
            :class="{ 'active': activeCarouselIndex === index }"
            @mouseenter="handleCarouselHover(index)"
          >
            <div class="list-item-content">
              <h3 class="list-item-title">{{ item.title }}</h3>
              <p class="list-item-description">{{ item.subtitle }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程分类部分 -->
    <div class="categories-section">
      <div class="categories-grid" :class="{ 'collapsed': props.isCollapse, 'expanded': !props.isCollapse }">
        <el-card 
          v-for="(category, index) in categories" 
          :key="index" 
          class="category-card" 
          shadow="hover"
          @click="handleCategoryClick(category)">
          <div class="category-content">
            <div class="category-icon">
              <el-icon v-if="category.icon === 'Reading'"><Reading /></el-icon>
              <el-icon v-else-if="category.icon === 'Collection'"><Collection /></el-icon>
              <el-icon v-else-if="category.icon === 'Connection'"><Connection /></el-icon>
              <el-icon v-else-if="category.icon === 'Histogram'"><Histogram /></el-icon>
              <el-icon v-else-if="category.icon === 'Setting'"><Setting /></el-icon>
              <el-icon v-else-if="category.icon === 'View'"><View /></el-icon>
              <el-icon v-else-if="category.icon === 'Star'"><Star /></el-icon>
            </div>
            <div class="category-info">
              <h3 class="category-title">{{ category.title }}</h3>
              <div class="category-description">{{ category.description }}</div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 进站必学部分 -->
    <div class="essential-courses-section">
      <div class="section-header">
        <h2 class="section-title">进站必学</h2>
        <div class="section-subtitle">官方出品，边看边练步步为营</div>
      </div>
      <div class="courses-wrapper">
        <div class="courses-grid" :class="{ 'collapsed': props.isCollapse, 'expanded': !props.isCollapse }">
          <el-card 
            v-for="course in essentialCourses" 
            :key="course.id" 
            class="course-card" 
            shadow="hover"
            @click="handleCourseClick(course)">
            <div class="course-image">
              <img :src="course.image" :alt="course.title">
            </div>
            <div class="course-content">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-description">{{ course.description }}</p>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.learn-container {
  padding: 20px;
  background-color: #f5f7fa;
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

.carousel-container {
  display: flex;
  gap: 20px;
  height: 240px;
}

.carousel-main {
  flex: 1;
  overflow: hidden;
  border-radius: 8px;
}

.carousel-list {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: auto;
}

.carousel-list-item {
  padding: 12px 15px;
  background-color: #f5f7fa;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.carousel-list-item:hover, .carousel-list-item.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409EFF;
  transform: translateX(5px);
}

.list-item-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.list-item-description {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.carousel-content {
  height: 100%;
  background-size: cover;
  background-position: center;
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 20px 40px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
}

.carousel-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.4) 50%, rgba(0,0,0,0.1) 100%);
  border-radius: 8px;
}

.carousel-text {
  position: relative;
  z-index: 2;
  max-width: 60%;
}

.carousel-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.carousel-subtitle {
  font-size: 16px;
  margin-bottom: 8px;
}

.carousel-description {
  font-size: 14px;
  margin-bottom: 15px;
  opacity: 0.9;
}

.carousel-button {
  margin-top: 10px;
}

/* 课程分类样式 */
.categories-section {
  margin-bottom: 30px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  transition: all 0.3s ease;
}

.categories-grid.collapsed {
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
}

.category-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.category-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.category-icon {
  font-size: 24px;
  margin-right: 15px;
  color: #409EFF;
}

.category-info {
  flex: 1;
}

.category-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.category-description {
  font-size: 12px;
  color: #666;
}

/* 进站必学样式 */
.essential-courses-section {
  margin-bottom: 30px;
}

.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.section-subtitle {
  font-size: 14px;
  color: #666;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  transition: all 0.3s ease;
}

.courses-grid.collapsed {
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
}

.course-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  overflow: hidden;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.course-image img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 4px 4px 0 0;
}

.course-content {
  padding: 15px;
}

.course-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.course-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .courses-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .courses-grid {
    grid-template-columns: 1fr;
  }
  
  .carousel-text {
    max-width: 80%;
  }
  
  .carousel-container {
    flex-direction: column;
    height: auto;
  }
  
  .carousel-main {
    width: 100%;
  }
  
  .carousel-list {
    width: 100%;
    flex-direction: row;
    overflow-x: auto;
    overflow-y: hidden;
    height: 80px;
  }
  
  .carousel-list-item {
    min-width: 200px;
    height: 60px;
    border-left: none;
    border-bottom: 3px solid transparent;
  }
  
  .carousel-list-item:hover, .carousel-list-item.active {
    border-left: none;
    border-bottom: 3px solid #409EFF;
    transform: translateY(-5px);
  }
}
</style>