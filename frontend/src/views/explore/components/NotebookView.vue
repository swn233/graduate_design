<template>
  <el-container class="notebook-container">
    <el-aside :width="sidebarWidth + 'px'" class="notebook-sidebar" ref="sidebar">
      <div class="sidebar-header">
        <h3>文件管理</h3>
        <div class="file-actions">
          <el-tooltip content="刷新" placement="top">
            <el-button type="text" @click="fetchFileList" :icon="Refresh" size="small"></el-button>
          </el-tooltip>
          <el-tooltip content="新建文件" placement="top">
            <el-button type="text" @click="createNewFile" :icon="DocumentAdd" size="small"></el-button>
          </el-tooltip>
          <el-tooltip content="新建文件夹" placement="top">
            <el-button type="text" @click="createNewFolder" :icon="FolderAdd" size="small"></el-button>
          </el-tooltip>
          <el-tooltip content="上传文件" placement="top">
            <el-button type="text" @click="triggerUploadFile" :icon="Upload" size="small"></el-button>
          </el-tooltip>
          <el-tooltip content="删除选中" placement="top" v-if="selectedNodes.length > 0">
            <el-button type="text" @click="deleteSelectedFiles" :icon="Delete" size="small"></el-button>
          </el-tooltip>
        </div>
      </div>
      <div class="file-tree"
           @dragover.prevent="onDragOver"
           @dragleave.prevent="onDragLeave"
           @drop.prevent="onDrop"
           :class="{ 'drag-over': isDragging }">
        <input 
          ref="uploadFileRef" 
          type="file" 
          style="display: none;"
          @change="handleFileUpload" 
        />
        <el-tree
          ref="fileTreeRef"
          :data="fileTree"
          :props="defaultProps"
          @node-click="handleNodeClick"
          node-key="path"
          class="custom-tree"
          default-expand-all
          show-checkbox
          @check="handleCheck"
          @check-change="handleCheckChange"
          @node-contextmenu="handleRightClick"
          :expand-on-click-node="false"
          :check-strictly="false"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span class="node-content">
                <el-icon v-if="data.isDirectory"><Folder /></el-icon>
                <el-icon v-else><Document /></el-icon>
                <span class="node-label">{{ data.name }}</span>
              </span>
              <span class="node-actions">
                <el-dropdown v-if="data.path !== '/'" trigger="click" @command="(command) => handleCommand(command, data)">
                  <span class="el-dropdown-link">
                    <el-icon><More /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="rename">重命名</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                      <el-dropdown-item v-if="!data.isDirectory" command="download">下载</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </span>
            </span>
          </template>
        </el-tree>
      </div>

      <!-- 资源统计面板 -->
      <div class="resource-stats-panel">
        <div class="panel-header">
          <h4>资源使用统计</h4>
          <el-tooltip content="刷新资源统计" placement="top">
            <el-button type="text" @click="refreshResourceStats" :icon="RefreshRight" size="small"></el-button>
          </el-tooltip>
        </div>

        <div class="stats-content">
          <!-- 训练时长 -->
          <div class="stat-item">
            <div class="stat-label">
              <el-icon><Timer /></el-icon>
              <span>训练时长</span>
            </div>
            <div class="stat-value">{{ resourceStats.trainingTime }}</div>
          </div>

          <!-- 内存使用 -->
          <div class="stat-item">
            <div class="stat-label">
              <el-icon><Cpu /></el-icon>
              <span>内存占用</span>
            </div>
            <div class="stat-value">
              <el-progress 
                :percentage="resourceStats.memoryUsage" 
                :color="getProgressColor(resourceStats.memoryUsage)"
                :show-text="false"
                style="width: 80px;"
              />
              <span>{{ resourceStats.memoryUsage }}%</span>
            </div>
          </div>

          <!-- CPU使用 -->
          <div class="stat-item">
            <div class="stat-label">
              <el-icon><Connection /></el-icon>
              <span>CPU占用</span>
            </div>
            <div class="stat-value">
              <el-progress 
                :percentage="resourceStats.cpuUsage" 
                :color="getProgressColor(resourceStats.cpuUsage)"
                :show-text="false"
                style="width: 80px;"
              />
              <span>{{ resourceStats.cpuUsage }}%</span>
            </div>
          </div>

          <!-- GPU使用 -->
          <div class="stat-item">
            <div class="stat-label">
              <el-icon><Monitor /></el-icon>
              <span>GPU占用</span>
            </div>
            <div class="stat-value">
              <el-progress 
                :percentage="resourceStats.gpuUsage" 
                :color="getProgressColor(resourceStats.gpuUsage)"
                :show-text="false"
                style="width: 80px;"
              />
              <span>{{ resourceStats.gpuUsage }}%</span>
            </div>
          </div>

          <!-- 硬盘使用 -->
          <div class="stat-item">
            <div class="stat-label">
              <el-icon><CopyDocument /></el-icon>
              <span>硬盘空间</span>
            </div>
            <div class="stat-value">
              <el-progress 
                :percentage="resourceStats.diskUsage" 
                :color="getProgressColor(resourceStats.diskUsage)"
                :show-text="false"
                style="width: 80px;"
              />
              <span>{{ resourceStats.diskUsage }}%</span>
            </div>
          </div>

          <!-- 当前费用 -->
          <div class="stat-item">
            <div class="stat-label">
              <el-icon><Money /></el-icon>
              <span>估算费用</span>
            </div>
            <div class="stat-value">¥{{ resourceStats.cost.toFixed(2) }}</div>
          </div>
        </div>
      </div>
    </el-aside>
    
    <div 
      class="resizer" 
      @mousedown="startResize" 
      :style="{ left: sidebarWidth + 'px' }"
    ></div>
    
    <el-container>
      <el-header class="notebook-header">
        <div class="header-left">
          <el-button 
            type="primary" 
            @click="restartKernel"
            :icon="Refresh"
          >
            重启内核
          </el-button>
        </div>
        <div class="header-right">
          <input 
            ref="fileInputRef" 
            type="file" 
            accept=".ipynb" 
            style="display: none;"
            @change="handleIpynbUpload" 
          />
          <el-button 
            type="success" 
            @click="triggerFileInput"
            :icon="Upload"
            style="margin-right: 10px;"
          >
            导入ipynb
          </el-button>
          <el-button 
            type="warning" 
            @click="saveAsIpynb"
            :icon="DocumentAdd"
            style="margin-right: 10px;"
          >
            导出ipynb
          </el-button>
          <el-button 
            type="primary" 
            @click="saveNotebook"
            :icon="Download"
          >
            保存
          </el-button>
        </div>
      </el-header>

      <el-main class="editor-container">
        <div class="cells-container">
          <div v-for="(cell, index) in cells" :key="index" class="cell-container" :class="{'markdown-cell': cell.type === CELL_TYPES.MARKDOWN}">
            <div class="cell-header">
              <div class="cell-type-indicator">
                <el-tag size="small" :type="cell.type === CELL_TYPES.CODE ? 'primary' : 'success'">
                  {{ cell.type === CELL_TYPES.CODE ? 'Python' : 'Markdown' }}
                </el-tag>
              </div>
              <div v-if="cell.type === CELL_TYPES.CODE" class="cell-actions">
                <el-button-group>
                  <el-button 
                    type="primary" 
                    size="small"
                    @click="executeCell(index)"
                    :icon="VideoPlay"
                    :loading="cell.isRunning"
                  >
                    运行
                  </el-button>
                  <el-button 
                    type="danger" 
                    size="small"
                    @click="stopCell(index)"
                    :icon="VideoPause"
                    :disabled="!cell.isRunning"
                  >
                    停止
                  </el-button>
                </el-button-group>
              </div>
              <div v-else class="cell-actions">
                <el-button-group v-if="cell.isEditing" class="markdown-tools">
                  <el-tooltip content="标题" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'heading')">
                      <span class="tool-text">H</span>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="粗体" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'bold')">
                      <span class="tool-text"><b>B</b></span>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="斜体" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'italic')">
                      <span class="tool-text"><i>I</i></span>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="链接" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'link')">
                      <span class="tool-text">🔗</span>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="图片" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'image')">
                      <el-icon><Picture /></el-icon>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="代码块" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'code')">
                      <span class="tool-text">&lt;/&gt;</span>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="列表" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'list')">
                      <span class="tool-text">• </span>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="表格" placement="bottom">
                    <el-button size="small" @click="insertMarkdown(index, 'table')">
                      <span class="tool-text">▦</span>
                    </el-button>
                  </el-tooltip>
                </el-button-group>
                <el-button
                  type="primary"
                  size="small"
                  @click="toggleMarkdownMode(index)"
                  :icon="cell.isEditing ? View : EditPen"
                >
                  {{ cell.isEditing ? '预览' : '编辑' }}
                </el-button>
              </div>
              <el-button 
                type="text" 
                size="small"
                @click="removeCell(index)"
                :icon="Delete"
              >
                删除
              </el-button>
            </div>
            <div class="cell-content" v-if="cell.type === CELL_TYPES.CODE">
              <div :ref="el => cellRefs[index] = el" class="code-editor"/>
            </div>
            <div v-else-if="cell.type === CELL_TYPES.MARKDOWN && cell.isEditing" class="markdown-editor-wrapper">
              <textarea
                v-model="cell.content"
                class="markdown-editor"
                placeholder="请输入Markdown内容，支持Markdown格式..."
                @scroll="handleMarkdownEditorScroll($event, index)"
              ></textarea>
            </div>
            <div 
              v-else 
              class="markdown-preview" 
              @scroll="handleMarkdownPreviewScroll($event, index)"
            >
              <div v-html="renderMarkdown(cell.content)" class="preview-content"></div>
            </div>
            <div class="cell-output" v-if="cell.type === CELL_TYPES.CODE && (cell.output || cell.images.length > 0)">
              <el-text type="info" size="small">输出：</el-text>
              <pre class="result" v-if="cell.output">{{ cell.output }}</pre>
              <div class="image-output" v-if="cell.images && cell.images.length > 0">
                <div v-for="(img, imgIndex) in cell.images" :key="imgIndex" class="image-container">
                  <img :src="img" :alt="`Cell output image ${imgIndex+1}`" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="add-cell-actions">
          <el-button-group>
            <el-button 
              type="primary" 
              plain
              @click="addCell()"
              :icon="Plus"
            >
              添加代码单元
            </el-button>
            <el-button 
              type="success" 
              plain
              @click="addMarkdownCell()"
              :icon="Reading"
            >
              添加Markdown单元
            </el-button>
          </el-button-group>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { ElText, ElMessage, ElMessageBox } from 'element-plus';
import { basicSetup } from 'codemirror';
import { EditorView } from '@codemirror/view';
import { python } from '@codemirror/lang-python';
import { accessHeader, get } from '@/net';
import MarkdownIt from 'markdown-it';
import { 
  VideoPlay, 
  VideoPause, 
  Delete, 
  Plus, 
  Refresh,
  Download,
  Folder,
  FolderAdd,
  Document,
  Edit,
  View,
  Document as IconDocument,
  EditPen,
  Reading,
  Picture,
  Upload,
  DocumentAdd,
  More,
  Timer,
  Cpu,
  Connection,
  Monitor,
  CopyDocument,
  Money,
  RefreshRight
} from '@element-plus/icons-vue';

// 资源使用统计数据
const resourceStats = ref({
  trainingTime: '00:00:00',
  memoryUsage: 0,
  cpuUsage: 0,
  gpuUsage: 0,
  diskUsage: 0,
  cost: 0
});

// 资源监控定时器
let resourceMonitorTimer = null;

// 随机生成资源使用数据
const generateRandomResourceStats = () => {
  // 训练时间累加
  const currentTime = resourceStats.value.trainingTime;
  let [hours, minutes, seconds] = currentTime.split(':').map(Number);
  
  seconds += 1; // 每次更新增加1秒
  if (seconds >= 60) {
    seconds = 0;
    minutes += 1;
  }
  if (minutes >= 60) {
    minutes = 0;
    hours += 1;
  }
  
  const formattedHours = hours.toString().padStart(2, '0');
  const formattedMinutes = minutes.toString().padStart(2, '0');
  const formattedSeconds = seconds.toString().padStart(2, '0');
  
  // 随机波动资源使用率
  const fluctuate = (value, range = 2) => {
    const change = Math.random() * range * 2 - range; // -range到+range的随机变化
    return Math.min(98, Math.max(5, Math.round(value + change))); // 限制在5-98%范围内
  };
  
  // 更新统计数据
  resourceStats.value = {
    trainingTime: `${formattedHours}:${formattedMinutes}:${formattedSeconds}`,
    memoryUsage: fluctuate(resourceStats.value.memoryUsage || 40),
    cpuUsage: fluctuate(resourceStats.value.cpuUsage || 30),
    gpuUsage: fluctuate(resourceStats.value.gpuUsage || 70),
    diskUsage: fluctuate(resourceStats.value.diskUsage || 25, 1), // 硬盘使用率波动较小
    cost: resourceStats.value.cost + 0.002 // 每次更新增加0.002元，保持大致相同的增长速率
  };
};

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage < 40) return '#67C23A'; // 绿色
  if (percentage < 70) return '#E6A23C'; // 黄色
  return '#F56C6C'; // 红色
};

// 刷新资源统计数据
const refreshResourceStats = () => {
  generateRandomResourceStats();
  ElMessage.success('已刷新资源统计数据');
};

// 初始化资源监控
const initResourceMonitor = () => {
  // 初始化随机起点
  resourceStats.value = {
    trainingTime: '00:00:00',
    memoryUsage: Math.floor(Math.random() * 30) + 20, // 20-50%
    cpuUsage: Math.floor(Math.random() * 20) + 15,    // 15-35%
    gpuUsage: Math.floor(Math.random() * 30) + 50,    // 50-80%
    diskUsage: Math.floor(Math.random() * 15) + 15,   // 15-30%
    cost: Math.random() * 5                           // 0-5元
  };
  
  // 定时更新资源统计
  resourceMonitorTimer = setInterval(() => {
    generateRandomResourceStats();
  }, 1000); // 每1秒更新一次
};

// 停止资源监控
const stopResourceMonitor = () => {
  if (resourceMonitorTimer) {
    clearInterval(resourceMonitorTimer);
    resourceMonitorTimer = null;
  }
};

// 初始化markdown-it
const md = new MarkdownIt({
  html: true,        // 允许HTML标签
  breaks: true,      // 转换\n为<br>
  linkify: true,     // 自动转换URL为链接
  typographer: true  // 启用一些语言中立的替换和引号美化
});

// Markdown 渲染函数
const renderMarkdown = (text) => {
  return md.render(text || '');
};

// 获取路由参数
const route = useRoute();
const caseId = route.params.id;
const caseName = route.query.name || '未命名案例';
const caseType = route.query.type || '项目';

/* 侧边栏宽度管理 */
const sidebarWidth = ref(300); // 初始宽度
const sidebar = ref(null);
const isResizing = ref(false);

// 文件树引用和选中状态
const fileTreeRef = ref(null);
const selectedNodes = ref([]);
const lastSelectedNode = ref(null);
const expandedKeys = ref([]); // 保存展开的节点

// 处理节点选中
const handleCheck = (node, checkedInfo) => {
  // 更新选中节点列表
  selectedNodes.value = checkedInfo.checkedNodes;
  console.log('选中的节点:', selectedNodes.value);
};

// 处理节点选中状态变化
const handleCheckChange = (data, checked, indeterminate) => {
  // 检查是否按下Shift键进行多选
  if (window.event && window.event.shiftKey && lastSelectedNode.value) {
    handleShiftSelect(data, checked);
  }
  
  // 更新最后选中的节点
  if (checked) {
    lastSelectedNode.value = data;
  } else if (lastSelectedNode.value && lastSelectedNode.value.path === data.path) {
    lastSelectedNode.value = null;
  }
};

// 处理Shift键多选
const handleShiftSelect = (currentNode, checked) => {
  try {
    // 如果没有上一个选中节点，或当前已经是选中状态，则不处理
    if (!lastSelectedNode.value || !checked) return;
    
    const treeInstance = fileTreeRef.value;
    if (!treeInstance) return;
    
    // 获取树中所有节点
    const allNodes = getAllTreeNodes();
    
    // 找到当前节点和上一个选中节点的索引
    const currentIndex = allNodes.findIndex(node => node.path === currentNode.path);
    const lastIndex = allNodes.findIndex(node => node.path === lastSelectedNode.value.path);
    
    if (currentIndex === -1 || lastIndex === -1) return;
    
    // 确定选择范围（始终从小到大）
    const startIndex = Math.min(currentIndex, lastIndex);
    const endIndex = Math.max(currentIndex, lastIndex);
    
    // 选中范围内的所有节点
    for (let i = startIndex; i <= endIndex; i++) {
      const nodePath = allNodes[i].path;
      // 排除目录，仅选择文件
      if (!allNodes[i].isDirectory) {
        treeInstance.setChecked(nodePath, true, false);
      }
    }
    
    // 更新选中节点列表
    selectedNodes.value = treeInstance.getCheckedNodes();
  } catch (error) {
    console.error('Shift多选失败:', error);
  }
};

// 获取树中所有可见节点（展平树结构）
const getAllTreeNodes = () => {
  const nodes = [];
  
  const traverse = (data) => {
    if (!data) return;
    
    if (Array.isArray(data)) {
      data.forEach(item => {
        nodes.push(item);
        if (item.children && item.children.length > 0) {
          traverse(item.children);
        }
      });
    } else {
      nodes.push(data);
      if (data.children && data.children.length > 0) {
        traverse(data.children);
      }
    }
  };
  
  traverse(fileTree.value);
  return nodes;
};

// 处理右键点击
const handleRightClick = (event, data, node) => {
  // 右键点击时自动选中该节点
  fileTreeRef.value?.setChecked(data.path, true, false);
  
  // 阻止默认右键菜单
  event.preventDefault();
};

// 删除选中的文件
const deleteSelectedFiles = () => {
  if (selectedNodes.value.length === 0) {
    ElMessage.warning('请先选择要删除的文件');
    return;
  }
  
  // 过滤掉根目录和只保留文件（如果需要，也可以包含目录）
  const filesToDelete = selectedNodes.value.filter(node => node.path !== '/');
  
  if (filesToDelete.length === 0) {
    ElMessage.warning('没有可删除的文件');
    return;
  }
  
  const fileNames = filesToDelete.map(file => file.name).join('、');
  const fileCount = filesToDelete.length;
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${fileCount} 个文件/文件夹吗？\n${fileNames}`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    try {
      // 保存当前展开状态
      saveExpandedState();
      
      // 依次删除文件
      const deletePromises = filesToDelete.map(file => {
        return new Promise((resolve) => {
          sendFileWsMessage({
            action: 'deleteFile',
            path: file.path
          });
          // 给后端一些处理时间
          setTimeout(resolve, 100);
        });
      });
      
      Promise.all(deletePromises).then(() => {
        ElMessage.success(`已删除 ${fileCount} 个文件/文件夹`);
        // 清除选中状态
        selectedNodes.value = [];
      });
    } catch (error) {
      console.error('删除文件过程出错:', error);
      ElMessage.error('删除文件失败');
    }
  }).catch(() => {
    // 取消操作
  });
};

// 保存节点展开状态
const saveExpandedState = () => {
  const treeInstance = fileTreeRef.value;
  if (!treeInstance) return;
  
  try {
    // 直接获取所有已展开的节点key
    expandedKeys.value = treeInstance.getExpandedKeys() || [];
    console.log('保存展开状态:', expandedKeys.value);
  } catch (error) {
    console.error('保存展开状态失败:', error);
  }
};

// 恢复节点展开状态
const restoreExpandedState = () => {
  nextTick(() => {
    const treeInstance = fileTreeRef.value;
    if (!treeInstance || !expandedKeys.value.length) return;
    
    try {
      console.log('恢复展开状态:', expandedKeys.value);
      treeInstance.setExpandedKeys(expandedKeys.value);
    } catch (error) {
      console.error('恢复展开状态失败:', error);
    }
  });
};

// 启动侧边栏调整
const startResize = (e) => {
  isResizing.value = true;
  
  // 添加全局事件监听器
  document.addEventListener('mousemove', handleResize);
  document.addEventListener('mouseup', stopResize);
  
  // 防止文本选择
  e.preventDefault();
};

// 处理侧边栏调整
const handleResize = (e) => {
  if (!isResizing.value) return;
  
  const newWidth = e.clientX;
  // 限制最小宽度和最大宽度
  sidebarWidth.value = Math.max(150, Math.min(500, newWidth));
};

// 停止侧边栏调整
const stopResize = () => {
  isResizing.value = false;
  
  // 移除全局事件监听器
  document.removeEventListener('mousemove', handleResize);
  document.removeEventListener('mouseup', stopResize);
};

const CELL_TYPES = {
  CODE: 'code',
  MARKDOWN: 'markdown'
};

const cells = ref([
  {
    editor: null,
    output: '',
    images: [],
    isRunning: false,
    type: CELL_TYPES.CODE,
    isEditing: true,
    content: ''
  }
]);
const cellRefs = ref([]);
const ws = ref(null);
const isConnected = ref(false);

// 同步滚动状态
const isMarkdownEditorScrolling = ref(false);
const isMarkdownPreviewScrolling = ref(false);

// 文件树数据
const fileTree = ref([
  {
    label: '工作目录',
    type: 'directory',
    path: '/',
    children: []
  }
]);

// 文件树相关属性配置
const defaultProps = {
  children: 'children',
  label: 'name'  // 修改为使用name属性作为显示标签
};

// 文件输入引用
const fileInputRef = ref(null);

// WebSocket相关
const fileWs = ref(null);
const isFileWsConnected = ref(false);
const uploadFileRef = ref(null);
const selectedNodePath = ref('');

// 文件拖放状态
const isDragging = ref(false);

// 触发文件选择
const triggerFileInput = () => {
  console.log('触发文件选择');
  fileInputRef.value?.click();
};

/**
 * 读取并处理ipynb文件
 * @param {Event} event - 文件选择事件
 */
const handleIpynbUpload = (event) => {
  console.log('文件选择事件触发');
  const file = event.target.files[0];
  if (!file) {
    console.log('没有选择文件');
    return;
  }
  
  console.log('选择的文件:', file.name, file.size, file.type);
  
  const reader = new FileReader();
  
  // 设置文件读取完成后的回调
  reader.onload = (e) => {
    console.log('文件读取完成');
    try {
      // 尝试解析JSON内容
      const content = JSON.parse(e.target.result);
      console.log('JSON解析成功，开始导入内容', content);
      
      // 检查是否是有效的ipynb格式
      if (!content.cells || !Array.isArray(content.cells)) {
        console.error('无效的ipynb格式:', content);
        ElMessage.error('无效的ipynb格式，缺少cells数组');
        return;
      }
      
      importIpynb(content);
      ElMessage.success('Jupyter Notebook导入成功');
    } catch (error) {
      console.error('解析ipynb文件失败:', error);
      console.error('文件内容:', e.target.result.substring(0, 500) + '...');
      ElMessage.error('解析ipynb文件失败，请确保文件格式正确: ' + error.message);
    }
  };
  
  // 设置错误处理
  reader.onerror = (error) => {
    console.error('读取文件时出错:', error);
    ElMessage.error('读取文件失败');
  };
  
  // 开始读取文件
  reader.readAsText(file);
  
  // 重置文件输入，以便可以选择同一个文件
  event.target.value = '';
};

/**
 * 导入ipynb内容到当前notebook
 * @param {Object} notebook - 解析后的ipynb内容
 */
const importIpynb = (notebook) => {
  console.log('开始导入ipynb内容');
  // 清空当前单元格
  cells.value = [];
  
  // 添加ipynb中的单元格
  if (notebook.cells && Array.isArray(notebook.cells)) {
    console.log(`找到${notebook.cells.length}个单元格`);
    
    notebook.cells.forEach((cell, index) => {
      console.log(`处理第${index+1}个单元格:`, cell.cell_type);
      
      if (cell.cell_type === 'code') {
        // 处理代码单元格
        const source = Array.isArray(cell.source) ? cell.source.join('') : cell.source || '';
        console.log(`代码单元格内容长度: ${source.length}`);
        
        // 处理输出，包括文本和图片
        let outputResult = { text: '', images: [] };
        if (cell.outputs && cell.outputs.length > 0) {
          outputResult = formatIpynbOutput(cell.outputs);
        }
        
        cells.value.push({
          editor: null,
          output: outputResult.text,
          images: outputResult.images,
          isRunning: false,
          type: CELL_TYPES.CODE,
          isEditing: true,
          content: source
        });
      } else if (cell.cell_type === 'markdown') {
        // 处理Markdown单元格
        const source = Array.isArray(cell.source) ? cell.source.join('') : cell.source || '';
        console.log(`Markdown单元格内容长度: ${source.length}`);
        
        cells.value.push({
          editor: undefined,
          output: '',
          images: [],
          isRunning: false,
          type: CELL_TYPES.MARKDOWN,
          isEditing: false,
          content: source
        });
      } else {
        console.log(`跳过未知类型单元格: ${cell.cell_type}`);
      }
    });
  }
  
  // 如果没有单元格，添加一个默认的代码单元格
  if (cells.value.length === 0) {
    console.log('未找到有效单元格，添加默认单元格');
    addCell(CELL_TYPES.CODE);
  }
  
  // 初始化代码编辑器
  console.log('开始初始化代码编辑器');
  setTimeout(() => {
    cells.value.forEach((cell, index) => {
      if (cell.type === CELL_TYPES.CODE) {
        console.log(`初始化第${index+1}个代码单元格`);
        initializeEditor(index);
      }
    });
    
    // 强制更新视图
    console.log('导入完成，cells数量:', cells.value.length);
  }, 0);
};

/**
 * 格式化ipynb输出内容
 * @param {Array} outputs - ipynb格式的输出数组
 * @returns {Object} 包含文本输出和图片输出数组的对象
 */
const formatIpynbOutput = (outputs) => {
  let result = {
    text: '',
    images: []
  };
  
  if (Array.isArray(outputs)) {
    console.log(`处理${outputs.length}个输出项`);
    
    outputs.forEach((output, index) => {
      console.log(`处理第${index+1}个输出:`, output.output_type);
      
      if (output.output_type === 'stream') {
        // 处理流输出（如print语句）
        if (Array.isArray(output.text)) {
          result.text += output.text.join('');
        } else if (typeof output.text === 'string') {
          result.text += output.text;
        }
      } else if (output.output_type === 'execute_result' || output.output_type === 'display_data') {
        // 处理执行结果
        if (output.data) {
          if (output.data['text/plain']) {
            // 过滤掉matplotlib的描述性输出（如<Figure ...>等）
            const textData = Array.isArray(output.data['text/plain']) 
              ? output.data['text/plain'].join('') 
              : output.data['text/plain'];
            
            // 不添加含有<Figure字样的输出
            if (!textData.includes('<Figure') && !textData.includes('matplotlib.') && 
                !textData.includes('Text(') && !textData.includes('Axes(')) {
              result.text += textData;
            } else {
              console.log('过滤掉matplotlib描述性输出:', textData);
            }
          }
          
          // 处理图片数据
          if (output.data['image/png']) {
            console.log('找到图片数据');
            const imageData = output.data['image/png'];
            result.images.push(`data:image/png;base64,${imageData}`);
          } else if (output.data['image/jpeg']) {
            console.log('找到JPEG图片数据');
            const imageData = output.data['image/jpeg'];
            result.images.push(`data:image/jpeg;base64,${imageData}`);
          } else if (output.data['image/svg+xml']) {
            console.log('找到SVG图片数据');
            const imageData = output.data['image/svg+xml'];
            result.images.push(`data:image/svg+xml;base64,${imageData}`);
          }
        }
      } else if (output.output_type === 'error') {
        // 处理错误信息
        result.text += `${output.ename || 'Error'}: ${output.evalue || ''}\n`;
        if (output.traceback && Array.isArray(output.traceback)) {
          result.text += output.traceback.join('\n');
        }
      }
    });
  }
  
  console.log(`输出格式化结果: 文本长度: ${result.text.length}, 图片数量: ${result.images.length}`);
  return result;
};

/**
 * 将当前notebook保存为ipynb格式并下载
 */
const saveAsIpynb = () => {
  console.log('开始生成ipynb文件');
  
  // 获取所有单元格的当前内容
  const cellsData = cells.value.map((cell, index) => {
    console.log(`处理第${index+1}个单元格:`, cell.type);
    
    if (cell.type === CELL_TYPES.CODE) {
      // 代码单元格：获取编辑器中的最新内容
      let content = '';
      if (cell.editor) {
        content = cell.editor.state.doc.toString();
        console.log(`从编辑器获取代码内容，长度: ${content.length}`);
      } else {
        content = cell.content || '';
        console.log(`从content属性获取代码内容，长度: ${content.length}`);
      }
      
      // 准备输出数据
      const outputs = [];
      
      // 添加文本输出(如果有)
      if (cell.output) {
        outputs.push({
          output_type: 'stream',
          name: 'stdout',
          text: cell.output.split('\n').map(line => line + '\n')
        });
      }
      
      // 添加图片输出(如果有)
      if (cell.images && cell.images.length > 0) {
        cell.images.forEach((imageUrl, index) => {
          try {
            // 从data URL提取base64部分
            const parts = imageUrl.split(',');
            if (parts.length >= 2) {
              const base64Data = parts[1];
              // 确定图片类型
              let imageType = 'image/png';
              if (imageUrl.includes('data:image/jpeg')) {
                imageType = 'image/jpeg';
              } else if (imageUrl.includes('data:image/svg+xml')) {
                imageType = 'image/svg+xml';
              }
              // 输出键名
              const outputKey = imageType.replace('image/', '');
              
              outputs.push({
                output_type: 'display_data',
                data: {
                  [outputKey]: base64Data,
                  // 不再输出Figure描述文本
                  // 'text/plain': [`<Figure ${index+1}>`]
                },
                metadata: {
                  [outputKey]: {
                    'width': 600,
                    'height': 400
                  }
                }
              });
            }
          } catch (error) {
            console.error(`处理第${index+1}张图片数据时出错:`, error);
          }
        });
      }
      
      return {
        cell_type: 'code',
        execution_count: null,
        metadata: {},
        outputs: outputs,
        source: content.split('\n').map(line => line + '\n')
      };
    } else if (cell.type === CELL_TYPES.MARKDOWN) {
      // Markdown单元格：从content属性获取内容
      const content = cell.content || '';
      console.log(`处理Markdown内容，长度: ${content.length}`);
      
      if (!content) {
        console.warn('警告: 发现空的Markdown内容');
      }
      
      return {
        cell_type: 'markdown',
        metadata: {},
        source: content.split('\n').map(line => line + '\n')
      };
    }
  }).filter(Boolean); // 过滤掉undefined值
  
  console.log(`处理完成，共${cellsData.length}个单元格`);
  
  // 创建完整的notebook对象
  const notebook = {
    cells: cellsData,
    metadata: {
      kernelspec: {
        display_name: 'Python 3',
        language: 'python',
        name: 'python3'
      },
      language_info: {
        codemirror_mode: {
          name: 'ipython',
          version: 3
        },
        file_extension: '.py',
        mimetype: 'text/x-python',
        name: 'python',
        nbconvert_exporter: 'python',
        pygments_lexer: 'ipython3',
        version: '3.8.5'
      }
    },
    nbformat: 4,
    nbformat_minor: 5
  };
  
  // 转换为JSON字符串
  const dataStr = JSON.stringify(notebook, null, 2);
  console.log(`生成的JSON大小: ${dataStr.length} 字节`);
  
  // 创建下载链接
  const dataUri = 'data:application/json;charset=utf-8,' + encodeURIComponent(dataStr);
  const exportFileName = `notebook_${new Date().getTime()}.ipynb`;
  
  console.log(`准备下载文件: ${exportFileName}`);
  
  try {
    // 创建并触发下载
    const linkElement = document.createElement('a');
    linkElement.setAttribute('href', dataUri);
    linkElement.setAttribute('download', exportFileName);
    document.body.appendChild(linkElement); // 某些浏览器需要先添加到DOM
    linkElement.click();
    document.body.removeChild(linkElement); // 下载后移除
    
    console.log('下载链接已触发');
    ElMessage.success('Jupyter Notebook导出成功');
  } catch (error) {
    console.error('导出文件失败:', error);
    ElMessage.error('导出文件失败: ' + error.message);
  }
};

// 重启内核
const restartKernel = async () => {
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.close();
  }
  ws.value = null;
  isConnected.value = false;
  
  // 清空所有cell的输出
  cells.value.forEach(cell => {
    cell.output = '';
    cell.images = [];
    cell.isRunning = false;
  });
  
  // 重新连接
  connectWebSocket();
};

// 保存notebook
const saveNotebook = async () => {
  const notebook = {
    cells: cells.value.map(cell => ({
      code: cell.editor.state.doc.toString(),
      output: cell.output
    }))
  };
  
  try {
    const response = await fetch('/api/notebook/save', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...accessHeader()
      },
      body: JSON.stringify(notebook)
    });
    
    const result = await response.json();
    if (result.code === 200) {
      ElMessage.success(result.data);
    } else {
      ElMessage.error(result.message || '保存失败');
    }
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  }
};

const addCell = (type = CELL_TYPES.CODE) => {
  cells.value.push({
    editor: type === CELL_TYPES.CODE ? null : undefined,
    output: '',
    images: [],
    isRunning: false,
    type: type,
    isEditing: true,
    content: type === CELL_TYPES.MARKDOWN ? markdownTemplate : ''
  });
  if (type === CELL_TYPES.CODE) {
    setTimeout(() => {
      initializeEditor(cells.value.length - 1);
    }, 0);
  }
};

const addMarkdownCell = () => {
  addCell(CELL_TYPES.MARKDOWN);
};

const initializeEditor = (index) => {
  const editorEl = cellRefs.value[index];
  if (!editorEl) return;

  const cell = cells.value[index];
  if (cell.type !== CELL_TYPES.CODE) return;
  
  // 初始代码模板
  const cellContents = [
    ``,
    ``,
    ``
  ];

  const initialContent = cell.content || cellContents[Math.min(index, cellContents.length - 1)];
  cell.content = initialContent;

  cell.editor = new EditorView({
    doc: initialContent,
    extensions: [basicSetup, python()],
    parent: editorEl
  });
};

const connectWebSocket = () => {
  // 如果已有连接
  if (ws.value && ws.value.readyState !== WebSocket.CLOSED) {
    console.log('已有WebSocket连接');
    ws.value.close();
  }
  
  const token = accessHeader();
  const wsUrl = `ws://localhost:8080/ws/python?token=${token.Authorization}`;
  console.log('尝试连接WebSocket:', wsUrl);
  ws.value = new WebSocket(wsUrl);
  
  ws.value.onopen = () => {
    console.log('WebSocket连接已建立');
    isConnected.value = true;
  };

  ws.value.onerror = (error) => {
    console.error('WebSocket错误:', error);
    isConnected.value = false;
  };

  ws.value.onclose = (event) => {
    console.log('WebSocket连接已关闭, 代码:', event.code, '原因:', event.reason);
    isConnected.value = false;
  };
  
  // 添加所有消息处理器
  ws.value.onmessage = (event) => {
    if (event.data) {
      console.log('收到处理过的WebSocket消息:', 
                 event.data.length > 100 ? 
                 event.data.substring(0, 100) + '...' : 
                 event.data);
    }
  };
};

const executeCell = async (index) => {
  const cell = cells.value[index];
  cell.output = '';
  cell.images = [];
  cell.isRunning = true;

  if (!ws.value || ws.value.readyState !== WebSocket.OPEN) {
    connectWebSocket();
  }

  // 等待连接建立
  if (!isConnected.value) {
    await new Promise((resolve) => {
      const checkConnection = setInterval(() => {
        if (isConnected.value) {
          clearInterval(checkConnection);
          resolve();
        }
      }, 100);
    });
  }

  // 设置消息处理器
  const messageHandler = (event) => {
    if (event.data) {
      console.log('收到WebSocket消息:', event.data.substring(0, 100));
      // 检查是否是图片消息
      if (event.data.startsWith('IMAGE:')) {
        try {
          // 使用正则表达式提取类型和base64
          const firstSep = event.data.indexOf(':', 6); // 从'IMAGE:'后面开始查找
          if (firstSep !== -1) {
            const type = event.data.substring(6, firstSep);
            const base64 = event.data.substring(firstSep + 1);
            console.log(`已收到图片数据: 类型=${type}, 长度=${base64.length}`);
            cell.images.push(`data:image/${type};base64,${base64}`);
          }
        } catch (e) {
          console.error('处理图片数据错误:', e);
        }
      } else {
        cell.output += event.data;
      }
    }
  };
  ws.value.addEventListener('message', messageHandler);

  // 发送代码
  const code = cell.editor.state.doc.toString();
  if (code.trim()) {
    console.log('发送代码到服务器:', code.substring(0, 100));
    ws.value.send(code);
  }

  // 等待输出稳定
  let lastOutputLength = 0;
  let lastImageCount = 0;
  let stableCount = 0;
  
  const checkComplete = setInterval(() => {
    const currentOutputLength = cell.output.length;
    const currentImageCount = cell.images.length;
    
    // 检查是否有变化
    if (currentOutputLength === lastOutputLength && currentImageCount === lastImageCount) {
      stableCount++;
      // 如果10次都没有变化，认为执行完成
      if (stableCount >= 10) {
        clearInterval(checkComplete);
        ws.value.removeEventListener('message', messageHandler);
        cell.isRunning = false;
        console.log(`执行完成: 输出长度=${currentOutputLength}, 图片数量=${currentImageCount}`);
      }
    } else {
      // 重置计数器
      stableCount = 0;
      lastOutputLength = currentOutputLength;
      lastImageCount = currentImageCount;
    }
  }, 100);
  
  // 最长等待时间为15秒
  setTimeout(() => {
    clearInterval(checkComplete);
    if (cell.isRunning) {
      ws.value.removeEventListener('message', messageHandler);
      cell.isRunning = false;
      console.log('执行超时，强制结束');
    }
  }, 15000);
};

const stopCell = (index) => {
  const cell = cells.value[index];
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.send('exit()');
  }
  cell.isRunning = false;
};

const removeCell = (index) => {
  if (cells.value.length > 0) {
    const cell = cells.value[index];
    if (cell.type === CELL_TYPES.CODE && ws.value && ws.value.readyState === WebSocket.OPEN) {
      ws.value.close();
    }
    cells.value.splice(index, 1);
  }
};

// 切换Markdown单元格的编辑/预览模式
const toggleMarkdownMode = (index) => {
  const cell = cells.value[index];
  if (cell.type !== CELL_TYPES.MARKDOWN) return;
  
  cell.isEditing = !cell.isEditing;
};

// 插入Markdown语法
const insertMarkdown = (index, syntax) => {
  const cell = cells.value[index];
  if (cell.type !== CELL_TYPES.MARKDOWN || !cell.isEditing) return;
  
  // 获取对应的textarea元素
  const textareas = document.getElementsByClassName('markdown-editor');
  if (!textareas.length) return;
  
  // 找到对应index的textarea
  let textarea = null;
  for (let i = 0, count = 0; i < textareas.length; i++) {
    if (cells.value[count].type === CELL_TYPES.MARKDOWN) {
      if (count === index) {
        textarea = textareas[i];
        break;
      }
      count++;
    }
  }
  
  if (!textarea) return;
  
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const text = textarea.value;
  
  let insertion = '';
  let newCursorPos = 0;
  
  switch(syntax) {
    case 'bold':
      insertion = `**${text.substring(start, end) || '粗体文本'}**`;
      newCursorPos = start + 2;
      break;
    case 'italic':
      insertion = `*${text.substring(start, end) || '斜体文本'}*`;
      newCursorPos = start + 1;
      break;
    case 'heading':
      insertion = `\n## ${text.substring(start, end) || '标题'}\n`;
      newCursorPos = start + 4;
      break;
    case 'link':
      insertion = `[${text.substring(start, end) || '链接文本'}](url)`;
      newCursorPos = start + 1;
      break;
    case 'image':
      insertion = `![${text.substring(start, end) || '图片描述'}](图片URL)`;
      newCursorPos = start + 2;
      break;
    case 'code':
      insertion = `\`\`\`\n${text.substring(start, end) || '代码块'}\n\`\`\``;
      newCursorPos = start + 4;
      break;
    case 'list':
      insertion = `\n- ${text.substring(start, end) || '列表项'}\n`;
      newCursorPos = start + 3;
      break;
    case 'table':
      insertion = `\n| 列1 | 列2 | 列3 |\n| --- | --- | --- |\n| 内容1 | 内容2 | 内容3 |\n| 内容4 | 内容5 | 内容6 |\n`;
      newCursorPos = start + 1;
      break;
  }
  
  cell.content = text.substring(0, start) + insertion + text.substring(end);
  
  // 在DOM更新后设置光标位置
  setTimeout(() => {
    textarea.focus();
    if (text.substring(start, end)) {
      // 有选中文本时，选中插入的全部内容
      textarea.setSelectionRange(start, start + insertion.length);
    } else {
      // 无选中文本时，将光标移动到预设位置
      textarea.setSelectionRange(newCursorPos, newCursorPos);
    }
  });
};

// Markdown模板
const markdownTemplate = ``;

// 处理Markdown编辑器滚动
const handleMarkdownEditorScroll = (e, index) => {
  if (isMarkdownPreviewScrolling.value) return;
  isMarkdownEditorScrolling.value = true;
  
  const previewDivs = document.getElementsByClassName('markdown-preview');
  if (!previewDivs.length) return;
  
  // 找到匹配当前index的预览区
  let previewDiv = null;
  for (let i = 0, count = 0; i < cells.value.length; i++) {
    if (cells.value[i].type === CELL_TYPES.MARKDOWN) {
      if (count === index) {
        previewDiv = previewDivs[count];
        break;
      }
      count++;
    }
  }
  
  if (!previewDiv) return;
  
  const editor = e.target;
  const percentage = editor.scrollTop / (editor.scrollHeight - editor.clientHeight);
  previewDiv.scrollTop = percentage * (previewDiv.scrollHeight - previewDiv.clientHeight);
  
  setTimeout(() => {
    isMarkdownEditorScrolling.value = false;
  }, 100);
};

// 处理Markdown预览区滚动
const handleMarkdownPreviewScroll = (e, index) => {
  if (isMarkdownEditorScrolling.value) return;
  isMarkdownPreviewScrolling.value = true;
  
  const textareas = document.getElementsByClassName('markdown-editor');
  if (!textareas.length) return;
  
  // 找到匹配当前index的textarea
  let textarea = null;
  for (let i = 0, count = 0; i < cells.value.length; i++) {
    if (cells.value[i].type === CELL_TYPES.MARKDOWN) {
      if (count === index) {
        textarea = textareas[count];
        break;
      }
      count++;
    }
  }
  
  if (!textarea) return;
  
  const preview = e.target;
  const percentage = preview.scrollTop / (preview.scrollHeight - preview.clientHeight);
  textarea.scrollTop = percentage * (textarea.scrollHeight - textarea.clientHeight);
  
  setTimeout(() => {
    isMarkdownPreviewScrolling.value = false;
  }, 100);
};

// 连接到文件系统WebSocket
const connectFileWebSocket = () => {
  // 如果已有连接
  if (fileWs.value && fileWs.value.readyState !== WebSocket.CLOSED) {
    console.log('已有文件WebSocket连接');
    fileWs.value.close();
  }
  
  const token = accessHeader();
  const wsUrl = `ws://localhost:8080/ws/files?token=${token.Authorization}`;
  console.log('尝试连接文件WebSocket:', wsUrl);
  fileWs.value = new WebSocket(wsUrl);
  
  fileWs.value.onopen = () => {
    console.log('文件WebSocket连接已建立');
    isFileWsConnected.value = true;
    // 获取文件树
    sendFileWsMessage({
      action: 'getFileTree'
    });
  };

  fileWs.value.onerror = (error) => {
    console.error('文件WebSocket错误:', error);
    isFileWsConnected.value = false;
  };

  fileWs.value.onclose = (event) => {
    console.log('文件WebSocket连接已关闭, 代码:', event.code, '原因:', event.reason);
    isFileWsConnected.value = false;
  };
  
  fileWs.value.onmessage = (event) => {
    if (event.data) {
      console.log('收到文件WebSocket消息:', event.data.substring(0, 100) + '...');
      try {
        const message = JSON.parse(event.data);
        handleFileWsMessage(message);
      } catch (error) {
        console.error('解析WebSocket消息失败:', error);
        ElMessage.error('解析消息失败: ' + error.message);
      }
    }
  };
};

// 发送WebSocket消息
const sendFileWsMessage = (message) => {
  if (!isFileWsConnected.value) {
    console.log('文件WebSocket未连接，尝试重连');
    connectFileWebSocket();
    setTimeout(() => {
      if (isFileWsConnected.value) {
        fileWs.value.send(JSON.stringify(message));
      } else {
        ElMessage.error('无法连接到文件服务器');
      }
    }, 1000);
    return;
  }
  
  fileWs.value.send(JSON.stringify(message));
};

// 处理WebSocket消息
const handleFileWsMessage = (message) => {
  try {
    console.log('处理文件WebSocket消息:', message.type);
    
    switch (message.type) {
      case 'fileTree':
        // 处理文件树数据
        if (message.data) {
          processFileTreeData(message.data);
        }
        break;
      case 'fileSystemChange':
        // 文件系统变化，刷新文件树
        sendFileWsMessage({
          action: 'getFileTree'
        });
        break;
      case 'fileContent':
        // 处理加载的文件内容
        handleLoadedFileContent(message.path, message.content);
        break;
      case 'error':
        ElMessage.error(message.message);
        break;
      default:
        console.log('未处理的消息类型:', message.type);
    }
  } catch (error) {
    console.error('处理WebSocket消息失败:', error);
    ElMessage.error('处理消息失败: ' + error.message);
  }
};

// 处理文件树数据，转换为前端可用的格式
const processFileTreeData = (data) => {
  try {
    // 确保数据存在
    if (!data) return;
    
    // 首先保存当前展开状态
    if (fileTreeRef.value) {
      saveExpandedState();
    }
    
    console.log('处理文件树数据:', data);
    
    // 过滤掉隐藏文件和文件夹的函数
    const filterHiddenFiles = (item) => {
      // 获取文件/文件夹名称
      const name = item.name || (item.path ? item.path.split('/').pop() : '');
      // 过滤掉以.开头的文件和文件夹
      return !name.startsWith('.');
    };
    
    // 递归过滤子目录中的隐藏文件
    const processItem = (item) => {
      if (item.children && Array.isArray(item.children)) {
        // 过滤子项中的隐藏文件
        item.children = item.children.filter(filterHiddenFiles)
          .map(child => processItem({ ...child }));
      }
      return item;
    };
    
    // 如果是单个根目录对象
    if (!Array.isArray(data)) {
      const rootDir = {
        ...data,
        // 保留原始路径用于操作
        path: data.path,
        // 添加标签属性，如果已有name则使用，否则使用路径最后部分
        label: data.name || data.path.split('/').pop()
      };
      
      // 处理根目录子项
      processItem(rootDir);
      
      fileTree.value = [rootDir];
    } else {
      // 如果是数组，则先过滤隐藏文件，再处理每一项
      const filteredData = data.filter(filterHiddenFiles)
        .map(item => processItem({
          ...item,
          path: item.path,
          label: item.name || item.path.split('/').pop()
        }));
      
      fileTree.value = filteredData;
    }
    
    // 恢复之前的展开状态
    if (fileTreeRef.value) {
      nextTick(() => {
        restoreExpandedState();
      });
    }
  } catch (error) {
    console.error('处理文件树数据失败:', error);
    ElMessage.error('加载文件目录失败');
  }
};

// 加载文件内容到编辑器
const handleLoadedFileContent = (path, content) => {
  // 创建新的cell并加载文件内容
  addCell();
  const newCell = cells.value[cells.value.length - 1];
  newCell.editor.dispatch({
    changes: {
      from: 0,
      to: newCell.editor.state.doc.length,
      insert: content
    }
  });
};

// 处理文件树节点点击
const handleNodeClick = (data) => {
  console.log('点击节点:', data);
  selectedNodePath.value = data.path; // 使用完整路径
  
  if (!data.isDirectory) {
    // 加载文件内容
    sendFileWsMessage({
      action: 'getFileContent',
      path: data.path
    });
  }
};

// 处理文件操作命令
const handleCommand = (command, data) => {
  console.log('执行命令:', command, '节点:', data);
  
  switch (command) {
    case 'rename':
      renameFile(data);
      break;
    case 'delete':
      deleteFile(data);
      break;
    case 'download':
      downloadFile(data.path);
      break;
  }
};

// 重命名文件
const renameFile = (data) => {
  ElMessageBox.prompt('输入新名称', '重命名', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputValue: data.name
  }).then(({ value }) => {
    if (!value) {
      ElMessage.warning('名称不能为空');
      return;
    }
    
    try {
      // 保存展开状态
      if (fileTreeRef.value) {
        saveExpandedState();
      }
      
      // 构建新路径
      const pathParts = data.path.split('/');
      pathParts.pop();
      const newPath = [...pathParts, value].join('/');
      
      sendFileWsMessage({
        action: 'renameFile',
        oldPath: data.path,
        newPath: newPath
      });
      
      ElMessage.success('重命名命令已发送');
    } catch (error) {
      console.error('重命名文件失败:', error);
      ElMessage.error('重命名失败: ' + error.message);
    }
  }).catch(() => {
    // 取消操作
  });
};

// 删除文件
const deleteFile = (data) => {
  ElMessageBox.confirm(
    `确定要删除${data.isDirectory ? '文件夹' : '文件'} "${data.name}"?`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    try {
      // 保存展开状态
      if (fileTreeRef.value) {
        saveExpandedState();
      }
      
      // 发送删除命令
      sendFileWsMessage({
        action: 'deleteFile',
        path: data.path
      });
      
      // 通知用户
      ElMessage.success('删除命令已发送');
    } catch (error) {
      console.error('删除文件失败:', error);
      ElMessage.error('删除文件失败');
    }
  }).catch(() => {
    // 取消操作
  });
};

// 下载文件
const downloadFile = (path) => {
  const filename = path.split('/').pop();
  const xhr = new XMLHttpRequest();
  xhr.open('GET', `/api/notebook/file?path=${encodeURIComponent(path)}`, true);
  
  Object.entries(accessHeader()).forEach(([key, value]) => {
    xhr.setRequestHeader(key, value);
  });
  
  xhr.responseType = 'blob';
  
  xhr.onload = function() {
    if (this.status === 200) {
      const blob = new Blob([this.response]);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      ElMessage.success('文件下载成功');
    } else {
      ElMessage.error('文件下载失败');
    }
  };
  
  xhr.send();
};

// 触发文件上传
const triggerUploadFile = () => {
  uploadFileRef.value?.click();
};

// 处理文件上传
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  uploadFile(file);
  event.target.value = '';
};

// 创建新文件
const createNewFile = () => {
  ElMessageBox.prompt('输入文件名', '新建文件', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputValue: 'untitled.py'
  }).then(({ value }) => {
    if (!value) {
      ElMessage.warning('文件名不能为空');
      return;
    }
    
    try {
      // 保存展开状态
      if (fileTreeRef.value) {
        saveExpandedState();
      }
      
      // 获取目标目录路径
      let parentPath = '/';
      
      if (selectedNodePath.value) {
        // 检查选中的是文件还是目录
        const isSelectedDir = fileTree.value.some(item => {
          if (item.path === selectedNodePath.value) {
            return item.isDirectory;
          }
          return checkIsDirectoryRecursive(item.children, selectedNodePath.value);
        });
        
        if (isSelectedDir) {
          // 如果选中的是目录，在其中创建文件
          parentPath = selectedNodePath.value;
        } else {
          // 如果选中的是文件，在其所在目录创建文件
          parentPath = selectedNodePath.value.substring(0, selectedNodePath.value.lastIndexOf('/'));
        }
      }
      
      // 构建完整路径
      const targetPath = `${parentPath}${parentPath.endsWith('/') ? '' : '/'}${value}`;
      
      sendFileWsMessage({
        action: 'createFile',
        path: targetPath,
        content: ''
      });
      
      ElMessage.success('创建文件命令已发送');
    } catch (error) {
      console.error('创建文件失败:', error);
      ElMessage.error('创建文件失败: ' + error.message);
    }
  }).catch(() => {
    // 取消操作
  });
};

// 创建新文件夹
const createNewFolder = () => {
  ElMessageBox.prompt('输入文件夹名', '新建文件夹', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputValue: 'new_folder'
  }).then(({ value }) => {
    if (!value) {
      ElMessage.warning('文件夹名不能为空');
      return;
    }
    
    try {
      // 保存展开状态
      if (fileTreeRef.value) {
        saveExpandedState();
      }
      
      // 获取目标目录路径
      let parentPath = '/';
      
      if (selectedNodePath.value) {
        // 检查选中的是文件还是目录
        const isSelectedDir = fileTree.value.some(item => {
          if (item.path === selectedNodePath.value) {
            return item.isDirectory;
          }
          return checkIsDirectoryRecursive(item.children, selectedNodePath.value);
        });
        
        if (isSelectedDir) {
          // 如果选中的是目录，在其中创建文件夹
          parentPath = selectedNodePath.value;
        } else {
          // 如果选中的是文件，在其所在目录创建文件夹
          parentPath = selectedNodePath.value.substring(0, selectedNodePath.value.lastIndexOf('/'));
        }
      }
      
      // 构建完整路径
      const targetPath = `${parentPath}${parentPath.endsWith('/') ? '' : '/'}${value}`;
      
      sendFileWsMessage({
        action: 'createDirectory',
        path: targetPath
      });
      
      ElMessage.success('创建文件夹命令已发送');
    } catch (error) {
      console.error('创建文件夹失败:', error);
      ElMessage.error('创建文件夹失败: ' + error.message);
    }
  }).catch(() => {
    // 取消操作
  });
};

// 获取文件列表
const fetchFileList = async () => {
  if (isFileWsConnected.value) {
    sendFileWsMessage({
      action: 'getFileTree'
    });
  } else {
    connectFileWebSocket();
  }
};

// 处理拖拽进入
const onDragOver = (event) => {
  isDragging.value = true;
  event.dataTransfer.dropEffect = 'copy';
};

// 处理拖拽离开
const onDragLeave = () => {
  isDragging.value = false;
};

// 处理文件拖放
const onDrop = (event) => {
  isDragging.value = false;
  const files = event.dataTransfer.files;
  if (files.length === 0) return;
  
  // 处理多个文件上传
  Array.from(files).forEach(file => {
    uploadFile(file);
  });
};

// 统一的文件上传处理
const uploadFile = (file) => {
  const reader = new FileReader();
  
  reader.onload = (e) => {
    const content = e.target.result;
    
    // 构建上传路径：根据当前选中的路径确定上传位置
    let targetPath;
    if (selectedNodePath.value) {
      // 检查选中的是文件还是目录
      const isSelectedDir = fileTree.value.some(item => {
        if (item.path === selectedNodePath.value) {
          return item.isDirectory;
        }
        // 递归检查子目录
        return checkIsDirectoryRecursive(item.children, selectedNodePath.value);
      });
      
      if (isSelectedDir) {
        // 如果选中的是目录，直接在其中创建文件
        targetPath = `${selectedNodePath.value}/${file.name}`;
      } else {
        // 如果选中的是文件，则在其所在目录创建文件
        const dirPath = selectedNodePath.value.substring(0, selectedNodePath.value.lastIndexOf('/'));
        targetPath = `${dirPath}/${file.name}`;
      }
    } else {
      // 默认上传到根目录
      targetPath = `/${file.name}`;
    }
    
    sendFileWsMessage({
      action: 'createFile',
      path: targetPath,
      content: content
    });
    
    ElMessage.success(`文件 ${file.name} 上传成功`);
  };
  
  reader.onerror = () => {
    ElMessage.error(`文件 ${file.name} 读取失败`);
  };
  
  // 根据文件类型决定读取方式
  if (file.type.startsWith('text/') || 
      file.name.endsWith('.py') || 
      file.name.endsWith('.ipynb') || 
      file.name.endsWith('.md') || 
      file.name.endsWith('.json')) {
    reader.readAsText(file);
  } else {
    reader.readAsArrayBuffer(file);
  }
};

// 递归检查路径是否为目录
const checkIsDirectoryRecursive = (items, path) => {
  if (!items || !Array.isArray(items)) return false;
  
  for (const item of items) {
    if (item.path === path) {
      return item.isDirectory;
    }
    
    if (item.children && item.isDirectory) {
      const result = checkIsDirectoryRecursive(item.children, path);
      if (result) return true;
    }
  }
  
  return false;
};

onMounted(() => {
  setTimeout(() => {
    initializeEditor(0);
  }, 0);
  connectWebSocket();
  connectFileWebSocket();
  
  // 初始化资源监控
  initResourceMonitor();
  
  // 添加键盘事件监听器，用于Shift多选
  document.addEventListener('keydown', (e) => {
    if (e.shiftKey) {
      // 可以在这里添加视觉反馈，表示进入多选模式
    }
  });
  
  document.addEventListener('keyup', (e) => {
    if (e.key === 'Shift') {
      // 退出多选模式的视觉反馈
    }
  });
});

onUnmounted(() => {
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.close();
  }
  if (fileWs.value && fileWs.value.readyState === WebSocket.OPEN) {
    fileWs.value.close();
  }
  
  // 停止资源监控计时器
  stopResourceMonitor();
});
</script>

<style scoped>
.notebook-container {
  height: 100vh;
  position: relative; /* 添加相对定位 */
}

.notebook-sidebar {
  border-right: 1px solid var(--el-border-color-light);
  background-color: var(--el-bg-color-page);
  transition: width 0.3s;
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
}

/* 添加分割线拖动句柄样式 */
.resizer {
  position: absolute;
  height: 100%;
  width: 5px;
  background-color: transparent;
  cursor: col-resize;
  z-index: 100;
  transition: background-color 0.2s;
}

.resizer:hover,
.resizer:active {
  background-color: var(--el-color-primary-light-7);
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-actions {
  display: flex;
  gap: 4px;
}

.file-tree {
  padding: 16px;
  flex: 1;
  overflow: auto; /* 添加滚动条 */
  min-height: 200px;
  max-height: calc(100vh - 330px); /* 预留资源面板的空间 */
  transition: all 0.3s;
}

/* 自定义树组件样式 */
.custom-tree {
  width: 100%;
  overflow: auto; /* 添加滚动条 */
}

.drag-over {
  background-color: var(--el-color-primary-light-9);
  border: 2px dashed var(--el-color-primary);
  border-radius: 4px;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  width: 100%;
  justify-content: space-between;
  padding-right: 8px;
  box-sizing: border-box;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  min-width: 0; /* 确保flex子项可以缩小到其内容以下 */
}

.node-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  min-width: 0; /* 确保flex子项可以缩小到其内容以下 */
}

.node-actions {
  display: flex;
  align-items: center;
  opacity: 0.7;
  margin-left: 8px;
  flex-shrink: 0; /* 防止操作图标被压缩 */
}

.node-actions:hover {
  opacity: 1;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.resource-stats-panel {
  padding: 16px;
  border-top: 1px solid var(--el-border-color-light);
  background-color: var(--el-bg-color-page);
  flex-shrink: 0;
  margin-top: auto; /* 使面板固定在底部 */
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.panel-header h4 {
  margin: 0;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.stat-label {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--el-text-color-regular);
}

.stat-value {
  font-weight: bold;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.notebook-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid var(--el-border-color-light);
  background-color: var(--el-bg-color-page);
}

.editor-container {
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.cells-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cell-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  overflow: hidden;
}

.cell-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  background: var(--el-bg-color-page);
  border-bottom: 1px solid var(--el-border-color-light);
}

.cell-type-indicator {
  margin-right: 10px;
}

.cell-actions {
  display: flex;
  gap: 8px;
}

.cell-content {
  padding: 10px;
}

.code-editor {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  overflow: hidden;
}

.cell-output {
  padding: 10px;
  background: var(--el-bg-color-page);
  border-top: 1px solid var(--el-border-color-light);
}

.result {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: monospace;
  font-size: 14px;
  line-height: 1.5;
  color: var(--el-text-color-primary);
}

.add-cell-actions {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.image-output {
  margin-top: 10px;
  padding: 10px;
  background: var(--el-bg-color-page);
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.image-container {
  display: flex;
  justify-content: center;
  border-bottom: 1px dashed #e0e0e0;
  padding-bottom: 20px;
  max-width: 100%;
  overflow: hidden;
}

.image-container:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.image-output img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

.markdown-cell {
  background-color: rgba(230, 244, 230, 0.2);
}

.markdown-editor-wrapper {
  width: 100%;
  height: 100%;
  border-right: 1px solid var(--el-border-color-light);
  padding: 10px;
}

.markdown-editor {
  width: 100%;
  height: 200px; /* 固定高度，可以根据需要调整 */
  padding: 15px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  resize: vertical;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
  font-size: 14px;
  line-height: 1.6;
  outline: none;
}

.markdown-preview {
  width: 100%;
  padding: 15px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  background-color: #fafafa;
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
}

.preview-content {
  line-height: 1.8;
}

.empty-preview {
  color: #909399;
  text-align: center;
  margin-top: 50px;
}

/* Markdown 预览区域样式 */
.preview-content :deep(h1) {
  font-size: 28px;
  margin: 20px 0 10px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.preview-content :deep(h2) {
  font-size: 24px;
  margin: 20px 0 10px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.preview-content :deep(h3) {
  font-size: 20px;
  margin: 16px 0 8px;
}

.preview-content :deep(p) {
  margin: 10px 0;
}

.preview-content :deep(ul) {
  padding-left: 20px;
  margin: 10px 0;
}

.preview-content :deep(li) {
  margin: 5px 0;
}

.preview-content :deep(code) {
  background-color: #f5f5f5;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
}

.preview-content :deep(pre) {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 10px 0;
}

.preview-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
}

.preview-content :deep(img) {
  max-width: 100%;
  border-radius: 4px;
  margin: 10px 0;
}

.preview-content :deep(a) {
  color: #409eff;
  text-decoration: none;
}

.preview-content :deep(a:hover) {
  text-decoration: underline;
}

.preview-content :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 15px 0;
}

.preview-content :deep(th),
.preview-content :deep(td) {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.preview-content :deep(th) {
  background-color: #f2f2f2;
  font-weight: bold;
}

.preview-content :deep(tr:nth-child(even)) {
  background-color: #f9f9f9;
}

.markdown-tools {
  display: flex;
  gap: 5px;
}

.tool-text {
  font-size: 14px;
  font-weight: bold;
}
</style>
