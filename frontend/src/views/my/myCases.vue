<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

// 案例类型枚举
const CASE_TYPES = {
  PROJECT: '项目',
  APP: '应用'
};

// 模拟案例数据
const cases = ref([
  {
    id: 1,
    name: '智能问答系统',
    description: '基于大语言模型的智能问答系统，可以回答用户的各种问题',
    createTime: '2023-10-15',
    status: '进行中',
    type: CASE_TYPES.PROJECT
  },
  {
    id: 2,
    name: '图像识别应用',
    description: '使用深度学习技术实现的图像识别应用，可以识别多种物体',
    createTime: '2023-09-20',
    status: '已完成',
    type: CASE_TYPES.PROJECT
  },
  {
    id: 3,
    name: '智能客服助手',
    description: '基于大语言模型的智能客服系统，可以自动回答客户问题',
    createTime: '2023-10-10',
    status: '已上线',
    type: CASE_TYPES.APP
  },
  {
    id: 4,
    name: '图像识别API',
    description: '提供图像识别服务的API接口，支持多种识别场景',
    createTime: '2023-09-15',
    status: '测试中',
    type: CASE_TYPES.APP
  },
  {
    id: 5,
    name: '自然语言处理工具',
    description: '文本分类、情感分析、命名实体识别等NLP工具集合',
    createTime: '2023-11-05',
    status: '规划中',
    type: CASE_TYPES.PROJECT
  },
  {
    id: 6,
    name: '文本分析工具',
    description: '支持文本分类、情感分析、关键词提取等功能的在线工具',
    createTime: '2023-11-01',
    status: '开发中',
    type: CASE_TYPES.APP
  }
]);

// 过滤条件
const activeFilter = ref('all');
const filteredCases = computed(() => {
  if (activeFilter.value === 'all') {
    return cases.value;
  }
  return cases.value.filter(item => item.type === activeFilter.value);
});

// 新建案例对话框
const dialogVisible = ref(false);
const newCase = ref({
  name: '',
  description: '',
  type: CASE_TYPES.PROJECT
});

// 打开新建案例对话框
const openDialog = () => {
  dialogVisible.value = true;
};

// 创建新案例
const createCase = () => {
  // 这里应该有API调用来创建案例
  const newId = cases.value.length + 1;
  
  cases.value.push({
    id: newId,
    name: newCase.value.name,
    description: newCase.value.description,
    createTime: new Date().toISOString().split('T')[0],
    status: newCase.value.type === CASE_TYPES.PROJECT ? '规划中' : '开发中',
    type: newCase.value.type
  });
  
  dialogVisible.value = false;
  
  // 直接跳转到Notebook页面
  router.push({
    name: 'notebook',
    params: { id: newId },
    query: { 
      name: newCase.value.name,
      type: newCase.value.type 
    }
  });
  
  // 重置表单
  newCase.value = { 
    name: '', 
    description: '', 
    type: CASE_TYPES.PROJECT
  };
};

// 打开Notebook
const openNotebook = (caseItem) => {
  router.push({
    name: 'notebook',
    params: { id: caseItem.id },
    query: { 
      name: caseItem.name,
      type: caseItem.type 
    }
  });
};

// 获取状态标签类型
const getStatusType = (status) => {
  if (['已完成', '已上线'].includes(status)) return 'success';
  if (['进行中', '测试中'].includes(status)) return 'warning';
  return 'info';
};
</script>

<template>
  <div class="cases-container">
    <div class="cases-header">
      <h1 class="cases-title">我的案例</h1>
      <div class="cases-actions">
        <el-radio-group v-model="activeFilter" size="small" style="margin-right: 15px;">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button :label="CASE_TYPES.PROJECT">项目</el-radio-button>
          <el-radio-button :label="CASE_TYPES.APP">应用</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="openDialog">新建案例</el-button>
      </div>
    </div>
    
    <el-table :data="filteredCases" style="width: 100%;" border>
      <el-table-column prop="id" label="ID" width="60" align="center"/>
      <el-table-column prop="name" label="名称" align="center"/>
      <el-table-column prop="type" label="类型" width="80" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.type === CASE_TYPES.PROJECT ? 'primary' : 'success'" effect="plain">
            {{ scope.row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" align="center"/>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="120" align="center"/>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
            <el-button type="primary" size="small" text @click="openNotebook(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" text>删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 新建案例对话框 -->
    <el-dialog v-model="dialogVisible" title="新建案例" width="30%">
      <el-form :model="newCase" label-width="80px">
        <el-form-item label="案例类型">
          <el-radio-group v-model="newCase.type">
            <el-radio :label="CASE_TYPES.PROJECT">项目</el-radio>
            <el-radio :label="CASE_TYPES.APP">应用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="案例名称">
          <el-input v-model="newCase.name" placeholder="请输入案例名称"></el-input>
        </el-form-item>
        <el-form-item label="案例描述">
          <el-input v-model="newCase.description" type="textarea" placeholder="请输入案例描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createCase">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.cases-container {
  padding: 20px;
}

.cases-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.cases-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.cases-actions {
  display: flex;
  align-items: center;
}
</style> 