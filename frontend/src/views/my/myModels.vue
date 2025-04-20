<script setup>
import { ref } from 'vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

// 模拟模型数据
const models = ref([
  {
    id: 1,
    name: '图像分类模型',
    description: '基于ResNet50架构的图像分类模型，支持多种物体识别',
    createTime: '2023-10-20',
    framework: 'PyTorch',
    accuracy: '92.5%'
  },
  {
    id: 2,
    name: '中文文本生成模型',
    description: '基于Transformer架构的中文文本生成模型，可用于内容创作',
    createTime: '2023-09-25',
    framework: 'TensorFlow',
    accuracy: '88.3%'
  },
  {
    id: 3,
    name: '语音识别模型',
    description: '基于深度学习的语音识别模型，支持中英文识别',
    createTime: '2023-11-10',
    framework: 'PyTorch',
    accuracy: '90.1%'
  }
]);

// 新建模型对话框
const dialogVisible = ref(false);
const newModel = ref({
  name: '',
  description: '',
  framework: ''
});

// 打开新建模型对话框
const openDialog = () => {
  dialogVisible.value = true;
};

// 创建新模型
const createModel = () => {
  // 这里应该有API调用来创建模型
  models.value.push({
    id: models.value.length + 1,
    name: newModel.value.name,
    description: newModel.value.description,
    createTime: new Date().toISOString().split('T')[0],
    framework: newModel.value.framework,
    accuracy: '未评估'
  });
  dialogVisible.value = false;
  newModel.value = { name: '', description: '', framework: '' };
};
</script>

<template>
  <div class="models-container">
    <div class="models-header">
      <h1 class="models-title">我的模型</h1>
      <el-button type="primary" @click="openDialog">新建模型</el-button>
    </div>
    
    <div class="models-list">
      <el-card v-for="model in models" :key="model.id" class="model-card">
        <div class="model-header">
          <h2 class="model-name">{{ model.name }}</h2>
          <el-tag :type="model.framework === 'PyTorch' ? 'danger' : model.framework === 'TensorFlow' ? 'primary' : 'info'">
            {{ model.framework }}
          </el-tag>
        </div>
        <p class="model-description">{{ model.description }}</p>
        <div class="model-footer">
          <div class="model-info">
            <span class="model-time">创建时间: {{ model.createTime }}</span>
            <span class="model-accuracy">准确率: {{ model.accuracy }}</span>
          </div>
          <div class="model-actions">
            <el-button type="primary" size="small" text>部署</el-button>
            <el-button type="warning" size="small" text>训练</el-button>
            <el-button type="danger" size="small" text>删除</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 新建模型对话框 -->
    <el-dialog v-model="dialogVisible" title="新建模型" width="30%">
      <el-form :model="newModel" label-width="80px">
        <el-form-item label="模型名称">
          <el-input v-model="newModel.name" placeholder="请输入模型名称"></el-input>
        </el-form-item>
        <el-form-item label="模型描述">
          <el-input v-model="newModel.description" type="textarea" placeholder="请输入模型描述"></el-input>
        </el-form-item>
        <el-form-item label="框架">
          <el-select v-model="newModel.framework" placeholder="请选择框架">
            <el-option label="PyTorch" value="PyTorch"></el-option>
            <el-option label="TensorFlow" value="TensorFlow"></el-option>
            <el-option label="Keras" value="Keras"></el-option>
            <el-option label="MXNet" value="MXNet"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createModel">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.models-container {
  padding: 20px;
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

.models-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.model-card {
  transition: all 0.3s;
}

.model-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.model-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.model-name {
  font-size: 18px;
  margin: 0;
  color: #333;
}

.model-description {
  color: #666;
  margin-bottom: 15px;
  min-height: 40px;
}

.model-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.model-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.model-time, .model-accuracy {
  color: #999;
}

.model-actions {
  display: flex;
  gap: 10px;
}
</style>