<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

// 模拟数据集数据
const datasets = ref([
  {
    id: 1,
    name: '图像分类数据集',
    description: '包含多种物体类别的图像数据集，适用于图像分类任务',
    createTime: '2023-10-05',
    size: '2.5GB',
    type: '图像'
  },
  {
    id: 2,
    name: '中文文本语料库',
    description: '大规模中文文本语料库，适用于NLP任务训练',
    createTime: '2023-09-10',
    size: '1.8GB',
    type: '文本'
  },
  {
    id: 3,
    name: '语音识别数据集',
    description: '多人多场景的语音数据集，适用于语音识别模型训练',
    createTime: '2023-11-15',
    size: '3.2GB',
    type: '音频'
  }
]);

// 新建数据集按钮在新窗口打开label页面
const goToLabel = () => {
  window.open('/label', '_blank');
};
</script>

<template>
  <div class="datasets-container">
    <div class="datasets-header">
      <h1 class="datasets-title">我的数据</h1>
      <el-button type="primary" @click="goToLabel">新建数据集</el-button>
    </div>
    
    <el-table :data="datasets" style="width: 100%;" border>
      <el-table-column prop="id" label="ID" width="60" align="center"/>
      <el-table-column prop="name" label="数据集名称" align="center"/>
      <el-table-column prop="description" label="描述" align="center"/>
      <el-table-column prop="type" label="类型" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.type === '图像' ? 'success' : scope.row.type === '文本' ? 'primary' : scope.row.type === '音频' ? 'warning' : 'info'">
            {{ scope.row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小" width="100" align="center"/>
      <el-table-column prop="createTime" label="创建时间" width="120" align="center"/>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" text>查看</el-button>
          <el-button type="danger" size="small" text>删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.datasets-container {
  padding: 20px;
}

.datasets-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.datasets-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}
</style>