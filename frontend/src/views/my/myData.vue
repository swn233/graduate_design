<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { get } from '@/net';
const router = useRouter();
const loading = ref(false);

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

// 数据集列表
const datasets = ref([]);

// LabelStudio API Key
const LABEL_STUDIO_TOKEN = 'a605f194caa3fb615d0ff27da4efa571f31aab7f';

// 获取数据集列表
const fetchDatasets = async () => {
  loading.value = true;
  try {
    const response = await axios.get('http://localhost:8081/api/projects/', {
      headers: {
        'Authorization': `Token ${LABEL_STUDIO_TOKEN}`
      }
    });
    
    // 转换数据格式
    datasets.value = response.data.results.map(project => ({
      id: project.id,
      name: project.title,
      description: project.description || '暂无描述',
      createTime: new Date(project.created_at).toLocaleDateString(),
      size: formatSize(project.total_annotations_number),
      type: getProjectType(project.parsed_label_config),
      status: project.is_published ? '已发布' : '草稿',
      taskCount: project.task_number,
      annotationCount: project.total_annotations_number,
      isDraft: project.is_draft
    }));
  } catch (error) {
    console.error('获取数据集失败:', error);
    if (error.response && error.response.status === 401) {
      ElMessage.error('认证失败，请检查 API token');
    } else {
      ElMessage.error('获取数据集失败，请稍后重试');
    }
  } finally {
    loading.value = false;
  }
};

// 格式化数据大小
const formatSize = (count) => {
  if (!count) return '0条';
  if (count < 1000) return `${count}条`;
  if (count < 10000) return `${(count/1000).toFixed(1)}K条`;
  return `${(count/10000).toFixed(1)}W条`;
};

// 获取项目类型
const getProjectType = (parsedConfig) => {
  if (!parsedConfig) return '未知';
  try {
    if (parsedConfig.image) return '图像';
    if (parsedConfig.audio) return '音频';
    if (parsedConfig.text) return '文本';
    return '其他';
  } catch {
    return '未知';
  }
};

// 删除数据集
const deleteDataset = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/projects/${id}/`, {
      headers: {
        'Authorization': `Token ${LABEL_STUDIO_TOKEN}`
      }
    });
    ElMessage.success('删除成功');
    fetchDatasets(); // 重新加载数据
  } catch (error) {
    console.error('删除失败:', error);
    if (error.response && error.response.status === 401) {
      ElMessage.error('认证失败，请检查 API token');
    } else {
      ElMessage.error('删除失败，请稍后重试');
    }
  }
};

// 查看数据集
const viewDataset = (id) => {
  window.open(`/label?projectId=${id}`, '_blank');
};

// 新建数据集按钮在新窗口打开label页面
const goToLabel = () => {
  window.open('/label', '_blank');
};

// 页面加载时获取数据
onMounted(() => {
  fetchDatasets();
});
</script>

<template>
  <div class="datasets-container">
    <div class="datasets-header">
      <h1 class="datasets-title">我的数据</h1>
      <el-button type="primary" @click="goToLabel">新建数据集</el-button>
    </div>
    
    <el-table 
      v-loading="loading"
      :data="datasets" 
      style="width: 100%;" 
      border
    >
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
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isDraft ? 'info' : 'success'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="taskCount" label="任务数" width="80" align="center"/>
      <el-table-column prop="annotationCount" label="标注数" width="80" align="center"/>
      <el-table-column prop="createTime" label="创建时间" width="120" align="center"/>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" text @click="viewDataset(scope.row.id)">查看</el-button>
          <el-button type="danger" size="small" text @click="deleteDataset(scope.row.id)">删除</el-button>
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