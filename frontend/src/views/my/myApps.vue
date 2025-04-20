<script setup>
import { ref } from 'vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

// 模拟应用数据
const apps = ref([
  {
    id: 1,
    name: '智能客服助手',
    description: '基于大语言模型的智能客服系统，可以自动回答客户问题',
    createTime: '2023-10-10',
    status: '已上线'
  },
  {
    id: 2,
    name: '图像识别API',
    description: '提供图像识别服务的API接口，支持多种识别场景',
    createTime: '2023-09-15',
    status: '测试中'
  },
  {
    id: 3,
    name: '文本分析工具',
    description: '支持文本分类、情感分析、关键词提取等功能的在线工具',
    createTime: '2023-11-01',
    status: '开发中'
  }
]);

// 新建应用对话框
const dialogVisible = ref(false);
const newApp = ref({
  name: '',
  description: ''
});

// 打开新建应用对话框
const openDialog = () => {
  dialogVisible.value = true;
};

// 创建新应用
const createApp = () => {
  // 这里应该有API调用来创建应用
  apps.value.push({
    id: apps.value.length + 1,
    name: newApp.value.name,
    description: newApp.value.description,
    createTime: new Date().toISOString().split('T')[0],
    status: '开发中'
  });
  dialogVisible.value = false;
  newApp.value = { name: '', description: '' };
};
</script>

<template>
  <div class="apps-container">
    <div class="apps-header">
      <h1 class="apps-title">我的应用</h1>
      <el-button type="primary" @click="openDialog">新建应用</el-button>
    </div>
    
    <div class="apps-list">
      <el-card v-for="app in apps" :key="app.id" class="app-card">
        <div class="app-header">
          <h2 class="app-name">{{ app.name }}</h2>
          <el-tag :type="app.status === '已上线' ? 'success' : app.status === '测试中' ? 'warning' : 'info'">
            {{ app.status }}
          </el-tag>
        </div>
        <p class="app-description">{{ app.description }}</p>
        <div class="app-footer">
          <span class="app-time">创建时间: {{ app.createTime }}</span>
          <div class="app-actions">
            <el-button type="primary" size="small" text>编辑</el-button>
            <el-button type="danger" size="small" text>删除</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 新建应用对话框 -->
    <el-dialog v-model="dialogVisible" title="新建应用" width="30%">
      <el-form :model="newApp" label-width="80px">
        <el-form-item label="应用名称">
          <el-input v-model="newApp.name" placeholder="请输入应用名称"></el-input>
        </el-form-item>
        <el-form-item label="应用描述">
          <el-input v-model="newApp.description" type="textarea" placeholder="请输入应用描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createApp">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.apps-container {
  padding: 20px;
}

.apps-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.apps-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.apps-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.app-card {
  transition: all 0.3s;
}

.app-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.app-name {
  font-size: 18px;
  margin: 0;
  color: #333;
}

.app-description {
  color: #666;
  margin-bottom: 15px;
  min-height: 40px;
}

.app-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.app-time {
  color: #999;
}

.app-actions {
  display: flex;
  gap: 10px;
}
</style>