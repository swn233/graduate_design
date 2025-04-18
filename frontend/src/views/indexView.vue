<script setup>
import {logout} from "@/net/index.js";
import router from "@/router/index.js";
import { ref } from 'vue';
import ExploreIndex from './explore/exploreIndex.vue';
import {
  Menu as IconMenu,
  Location as IconLocation,
  Document as IconDocument,
  Setting as IconSetting,
  User as IconUser,
  Compass as IconCompass,
  Reading as IconReading,
  Fold as IconFold,
  Expand as IconExpand
} from '@element-plus/icons-vue';

function userLogout(){
  logout(()=>router.push('/'))
}

const activeIndex = ref('explore');
const isCollapse = ref(false);

function handleSelect(key) {
  activeIndex.value = key;
  // 这里可以根据选择的菜单项执行相应的操作
  console.log(`选择了${key}菜单项`);
}

function toggleSidebar() {
  isCollapse.value = !isCollapse.value;
}
</script>

<template>
  <el-container class="layout-container">
    <!-- 左侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo-container">
        <h2 class="logo-text" v-if="!isCollapse"><span class="paic-text">PAIC</span>人工智能案例平台</h2>
        <h2 class="logo-text" v-else><span class="paic-text">PAIC</span></h2>
      </div>
      
      <el-menu
        :default-active="activeIndex"
        class="el-menu-vertical"
        :collapse="isCollapse"
        @select="handleSelect"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409EFF">
        
        <el-menu-item index="explore">
          <el-icon><icon-compass /></el-icon>
          <span>探索</span>
        </el-menu-item>
        
        <el-menu-item index="learn">
          <el-icon><icon-reading /></el-icon>
          <span>学习</span>
        </el-menu-item>
        
        <el-divider style="margin: 8px 0; border-color: rgba(255, 255, 255, 0.1);" />
        
        <el-menu-item index="my">
          <el-icon><icon-user /></el-icon>
          <span>我的</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
  <el-container>
      <el-header class="header">
        <div class="toggle-btn" @click="toggleSidebar">
          <el-icon v-if="isCollapse"><icon-expand /></el-icon>
          <el-icon v-else><icon-fold /></el-icon>
        </div>
        <div class="header-right">
          <el-button @click="userLogout" type="primary" size="small">退出登录</el-button>
        </div>
      </el-header>
      
      <el-main style="margin: 0px;padding: 0px;">
        <!-- 根据选择的菜单项显示不同的内容 -->
         <!-- 探索页面 -->
        <explore-index v-if="activeIndex === 'explore'" :is-collapse="isCollapse" />
        
        <div v-else-if="activeIndex === 'learn'">
          <h2>学习页面</h2>
          <p>这里是PAIC人工智能案例平台的学习页面，您可以在这里学习AI相关知识。</p>
        </div>
        
        <div v-else-if="activeIndex === 'my'">
          <h2>我的页面</h2>
          <p>这里是PAIC人工智能案例平台的个人页面，您可以在这里管理您的个人信息和案例。</p>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100%;
}

.sidebar {
  background-color: #001529;
  color: white;
  height: 100%;
  overflow-x: hidden;
  transition: width 0.3s ease;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  padding: 0 5px;
  transition: all 0.3s;
}

.logo-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 16px;
  transition: all 0.3s;
}

.paic-text {
  color: #1E90FF;
  font-style: italic;
  font-family: 'Georgia', serif;
  font-weight: bold;
  font-size: 16px;
  margin-right: 8px;
}

.el-menu-vertical {
  border-right: none;
  transition: width 0.3s ease;
}

.el-menu-item {
  transition: all 0.3s ease;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.toggle-btn {
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.toggle-btn:hover {
  background-color: #f5f5f5;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-menu-vertical {
  border-right: none;
}
</style>