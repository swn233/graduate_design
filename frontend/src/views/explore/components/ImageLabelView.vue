<template>
  <el-container class="container">
    <el-main class="main-content">
      <!-- 图片上传区域 -->
      <el-upload
        class="upload-area"
        action=""
        :auto-upload="false"
        :on-change="handleUpload"
        :show-file-list="false"
      >
        <el-button type="primary" size="large">选择图片</el-button>
      </el-upload>

      <!-- 图片展示与标注区域 -->
      <div class="image-wrapper">
        <canvas ref="canvas" 
                @mousedown="startDrawing"
                @mousemove="draw"
                @mouseup="stopDrawing"
                @mouseleave="stopDrawing"></canvas>
        
        <div class="image-list">
          <div v-for="(img, index) in images" 
               :key="index"
               class="thumbnail"
               @click="selectImage(index)">
            <img :src="img.url" />
          </div>
        </div>
      </div>

      <!-- 标签管理区域 -->
      <div class="tag-manager">
        <el-tag
          v-for="(tag, index) in tags"
          :key="index"
          :type="selectedTag === tag ? 'success' : ''"
          class="tag"
          @click="selectTag(tag)"
          closable
          @close="removeTag(index)"
        >
          {{ tag }}
        </el-tag>
        
        <el-input
          v-if="inputVisible"
          ref="tagInput"
          v-model="newTag"
          size="small"
          @keyup.enter="addTag"
          @blur="addTag"
        />
        <el-button v-else class="new-tag-btn" size="small" @click="showInput">
          + 新建标签
        </el-button>
      </div>
    </el-main>

    <el-footer class="toolbar">
      <el-button type="primary" size="large" @click="saveAnnotation">保存标注</el-button>
    </el-footer>
  </el-container>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const images = ref([])
const tags = ref(['动物', '植物', '建筑'])
const selectedTag = ref('')
const newTag = ref('')
const inputVisible = ref(false)

// Canvas相关状态
const canvas = ref(null)
const ctx = ref(null)
const isDrawing = ref(false)
const startX = ref(0)
const startY = ref(0)
const currentImage = ref(null)

onMounted(() => {
  ctx.value = canvas.value.getContext('2d')
})

const handleUpload = (file) => {
  const url = URL.createObjectURL(file.raw)
  images.value.push({ 
    id: Date.now(),
    url,
    annotations: []
  })
  selectImage(images.value.length - 1)
}

const selectImage = (index) => {
  currentImage.value = images.value[index]
  const img = new Image()
  img.src = currentImage.value.url
  img.onload = () => {
    canvas.value.width = img.width
    canvas.value.height = img.height
    ctx.value.drawImage(img, 0, 0)
  }
}

// 标签管理逻辑
const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    tagInput.value.focus()
  })
}

const addTag = () => {
  if (newTag.value.trim()) {
    tags.value.push(newTag.value.trim())
    newTag.value = ''
  }
  inputVisible.value = false
}

const removeTag = (index) => {
  tags.value.splice(index, 1)
}

const selectTag = (tag) => {
  selectedTag.value = tag === selectedTag.value ? '' : tag
}

// 标注绘制逻辑
const startDrawing = (e) => {
  if (!selectedTag.value) {
    ElMessage.warning('请先选择标签')
    return
  }
  isDrawing.value = true
  const rect = canvas.value.getBoundingClientRect()
  startX.value = e.clientX - rect.left
  startY.value = e.clientY - rect.top
}

const draw = (e) => {
  if (!isDrawing.value) return
  
  const rect = canvas.value.getBoundingClientRect()
  const currentX = e.clientX - rect.left
  const currentY = e.clientY - rect.top
  
  ctx.value.clearRect(0, 0, canvas.value.width, canvas.value.height)
  ctx.value.beginPath()
  ctx.value.rect(startX.value, startY.value, currentX - startX.value, currentY - startY.value)
  ctx.value.strokeStyle = '#409EFF'
  ctx.value.lineWidth = 2
  ctx.value.stroke()
}

const stopDrawing = (e) => {
  if (!isDrawing.value) return
  
  isDrawing.value = false
  const rect = canvas.value.getBoundingClientRect()
  const endX = e.clientX - rect.left
  const endY = e.clientY - rect.top
  
  currentImage.value.annotations.push({
    tag: selectedTag.value,
    x: startX.value,
    y: startY.value,
    width: endX - startX.value,
    height: endY - startY.value
  })
}

const saveAnnotation = () => {
  if (!currentImage.value) {
    ElMessage.warning('请先选择图片')
    return
  }
  
  console.log('保存标注结果:', currentImage.value.annotations)
  ElMessage.success('标注保存成功')
}
</script>


<style scoped>
.container {
  height: 100vh;
  background: var(--el-bg-color-page);
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

.upload-area {
  margin-bottom: 20px;
}

.image-wrapper {
  display: flex;
  gap: 20px;
  height: 60vh;
}

canvas {
  flex: 3;
  border: 2px solid var(--el-border-color-light);
  background: white;
  cursor: crosshair;
}

.image-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid var(--el-border-color-light);
  background: white;
}

.thumbnail {
  margin-bottom: 10px;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumbnail:hover {
  border-color: var(--el-color-primary);
}

.thumbnail img {
  width: 100%;
  height: auto;
}

.tag-manager {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 15px;
  border: 1px solid var(--el-border-color-light);
  background: white;
}

.tag {
  cursor: pointer;
  transition: all 0.3s;
}

.new-tag-btn {
  height: 24px;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 10px 20px;
  background: white;
  border-top: 1px solid var(--el-border-color-light);
}
</style>