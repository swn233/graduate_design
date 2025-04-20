<script setup>
import { ref } from 'vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

// 模拟项目数据
const projects = ref([
  {
    id: 1,
    name: '智能问答系统',
    description: '基于大语言模型的智能问答系统，可以回答用户的各种问题',
    createTime: '2023-10-15',
    status: '进行中'
  },
  {
    id: 2,
    name: '图像识别应用',
    description: '使用深度学习技术实现的图像识别应用，可以识别多种物体',
    createTime: '2023-09-20',
    status: '已完成'
  },
  {
    id: 3,
    name: '自然语言处理工具',
    description: '文本分类、情感分析、命名实体识别等NLP工具集合',
    createTime: '2023-11-05',
    status: '规划中'
  }
]);

// 新建项目对话框
const dialogVisible = ref(false);
const newProject = ref({
  name: '',
  description: ''
});

// 打开新建项目对话框
const openDialog = () => {
  dialogVisible.value = true;
};

// 创建新项目
const createProject = () => {
  // 这里应该有API调用来创建项目
  projects.value.push({
    id: projects.value.length + 1,
    name: newProject.value.name,
    description: newProject.value.description,
    createTime: new Date().toISOString().split('T')[0],
    status: '规划中'
  });
  dialogVisible.value = false;
  newProject.value = { name: '', description: '' };
};
</script>

<template>
  <div class="projects-container">
    <div class="projects-header">
      <h1 class="projects-title">我的项目</h1>
      <el-button type="primary" @click="openDialog">新建项目</el-button>
    </div>
    
    <div class="projects-list">
      <el-card v-for="project in projects" :key="project.id" class="project-card">
        <div class="project-header">
          <h2 class="project-name">{{ project.name }}</h2>
          <el-tag :type="project.status === '已完成' ? 'success' : project.status === '进行中' ? 'primary' : 'info'">
            {{ project.status }}
          </el-tag>
        </div>
        <p class="project-description">{{ project.description }}</p>
        <div class="project-footer">
          <span class="project-time">创建时间: {{ project.createTime }}</span>
          <div class="project-actions">
            <el-button type="primary" size="small" text>编辑</el-button>
            <el-button type="danger" size="small" text>删除</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 新建项目对话框 -->
    <el-dialog v-model="dialogVisible" title="新建项目" width="30%">
      <el-form :model="newProject" label-width="80px">
        <el-form-item label="项目名称">
          <el-input v-model="newProject.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="newProject.description" type="textarea" placeholder="请输入项目描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createProject">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.projects-container {
  padding: 20px;
}

.projects-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.projects-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.projects-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.project-card {
  transition: all 0.3s;
}

.project-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.project-name {
  font-size: 18px;
  margin: 0;
  color: #333;
}

.project-description {
  color: #666;
  margin-bottom: 15px;
  min-height: 40px;
}

.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.project-time {
  color: #999;
}

.project-actions {
  display: flex;
  gap: 10px;
}
</style>