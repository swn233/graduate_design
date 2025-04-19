<script setup>
import {logout, takeRole,takeUsername} from "@/net/index.js";
import router from "@/router/index.js";
import { ref, onMounted } from 'vue';
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
  Expand as IconExpand,
  ArrowDown as IconArrowDown,
  Search as IconSearch,
  SwitchButton as IconSwitchButton
} from '@element-plus/icons-vue';

const userInfo = ref({
  name: '',
  role: '',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
});

function handleCommand(command) {
  if (command === 'logout') {
    logout(() => router.push('/'))
  }
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

onMounted(() => {
  const username = JSON.parse(takeUsername());
  const role = JSON.parse(takeRole());
  userInfo.value.name = username || '';
  userInfo.value.role = role === 'admin' ? '管理员' : '用户';
});
</script>

<template>
  <el-container class="layout-container">
    <!-- header -->
    <el-header class="header">
      <div class="header-left">
      <div class="toggle-btn" @click="toggleSidebar">
          <el-icon v-if="isCollapse"><icon-expand /></el-icon>
          <el-icon v-else><icon-fold /></el-icon>
    </div>
      <div class="logo-container">
        <h2 class="logo-text" ><span class="paic-text">PAIC</span>人工智能案例平台</h2>
      </div>
    </div>
        <div class="header-right">
          <div class="search-container">
            <el-input
              placeholder="请输入搜索关键词"
              prefix-icon="icon-search"
              size="small"
              class="search-input">
              <template #prefix>
                <el-icon><icon-search /></el-icon>
              </template>
            </el-input>
          </div>
          <el-dropdown trigger="hover" @command="handleCommand">
            <div class="user-avatar-container">
              <el-avatar :size="32" :src="userInfo.avatar" />
              <span class="user-name">{{ userInfo.name }}</span>
              <el-icon class="el-icon--right"><icon-arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <div class="user-info">
                    <span class="user-role">{{ userInfo.role }}</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><icon-switch-button /></el-icon>
                  <span style="margin-left: 5px;">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
    <!-- 左侧边栏 -->
  <el-container>
    <el-aside :width="isCollapse ? '64px' : '100px'" class="sidebar">
      <el-menu
        :default-active="activeIndex"
        class="el-menu-vertical"
        :collapse="isCollapse"
        @select="handleSelect"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409EFF"
        collapse-transition="true">
        
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
      <el-main style="margin: 0px;padding: 0px;height: 100vh;">
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
  overflow: hidden;
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
  color: black;
}

.paic-text {
  color: #1E90FF;
  font-style: italic;
  font-family: 'Georgia', serif;
  font-weight: bold;
  font-size: 24px;
  margin-right: 8px;
}

.el-menu-vertical {
  border-right: none;
  transition: width 0.3s ease;
}

.el-menu-item {
  transition: all 0.3s ease;
}

.el-menu-item span {
  display: inline-block;
  opacity: 1;
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.el-menu--collapse .el-menu-item span {
  opacity: 0;
  transform: translateX(10px);
  width: 0;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0px;
}

.toggle-btn {
  margin-top: 10px;
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
  margin-right: 20px;
}

.search-container {
  margin-right: 30px;
}

.search-input {
  width: 300px;
}

.user-avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 8px;
}

.user-name {
  margin: 0 8px;
  font-size: 14px;
  color: #606266;
}

.user-info {
  padding: 4px 0;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.el-menu-vertical {
  border-right: none;
}

.header-left {
  display: flex;
  align-items: left;
}
</style>