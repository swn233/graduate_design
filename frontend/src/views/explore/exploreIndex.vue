<script setup>
import { ref } from 'vue'
import { Coin, PieChart, Pointer } from '@element-plus/icons-vue'
import Overview from './main/overview.vue'
import Datasets from './main/datasets.vue'
import Models from './main/models.vue'
import Experience from './main/experience.vue'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const activeMenu = ref('overview')
</script>

<template>
  <div class="explore-layout">
    <!-- 侧边栏 -->
    <div class="explore-sidebar" :style="{ width: props.isCollapse ? '64px' : '130px' }">
      <el-menu
        :default-active="activeMenu"
        class="explore-menu"
        :collapse="props.isCollapse"
        @select="activeMenu = $event">
        <el-menu-item index="overview">
          <el-icon><Files /></el-icon>
          <span class="menu-text">概览</span>
        </el-menu-item>
        <el-menu-item index="datasets">
          <el-icon><Coin /></el-icon>
          <span class="menu-text">数据集</span>
        </el-menu-item>
        <el-menu-item index="models">
          <el-icon><PieChart /></el-icon>
          <span class="menu-text">模型库</span>
        </el-menu-item>
        <el-menu-item index="experience">
          <el-icon><Pointer /></el-icon>
          <span class="menu-text">体验中心</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="explore-content">
      <!-- 概览内容 -->
      <Overview v-if="activeMenu === 'overview'" :isCollapse="props.isCollapse" />

      <!-- 数据集内容 -->
      <Datasets v-else-if="activeMenu === 'datasets'" :isCollapse="props.isCollapse" />

      <!-- 模型库内容 -->
      <Models v-else-if="activeMenu === 'models'" :isCollapse="props.isCollapse" />

      <!-- 体验中心内容 -->
      <Experience v-else-if="activeMenu === 'experience'" :isCollapse="props.isCollapse" />
    </div>
  </div>
</template>

<style scoped>
.explore-layout {
  display: flex;
  height: 100%;
  background-color: #f5f7fa;
}

.explore-sidebar {
  border-right: 1px solid #dcdfe6;
  height: 100%;
  background-color: #fff;
  transition: width 0.3s ease;
}

.explore-menu {
  height: 100%;
  border-right: none;
}

.explore-content {
  flex: 1;
  overflow-y: auto;
}

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
.menu-text {
  opacity: 1;
  transform: translateX(0);
  transition: opacity 0.3s ease, transform 0.3s ease;
  white-space: nowrap;
  overflow: hidden;
}

.el-menu--collapse .menu-text {
  opacity: 0;
  transform: translateX(10px);
}
</style>