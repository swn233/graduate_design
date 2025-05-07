<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside width="200px" style="background-color: #f5f5f5">
      <el-menu
        :default-active="activeMenu"
        router
        class="el-menu-vertical"
      >
        <el-menu-item index="/manage/users">
          <el-icon><user /></el-icon>
          <span>账号管理</span>
        </el-menu-item>
        
        <el-menu-item index="/manage/datasets">
          <el-icon><folder-opened /></el-icon>
          <span>数据集管理</span>
        </el-menu-item>

        <el-menu-item index="/manage/articles">
          <el-icon><document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>

        <el-menu-item index="/manage/models">
          <el-icon><cpu /></el-icon>
          <span>模型管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import {
  User,
  FolderOpened,
  Document,
  Cpu
} from '@element-plus/icons-vue'

const activeMenu = ref('')
</script>

<style scoped>
.el-menu-vertical {
  border-right: none;
}
.el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 0;
}
.el-menu-item.is-active {
  background-color: #ecf5ff;
}
</style>