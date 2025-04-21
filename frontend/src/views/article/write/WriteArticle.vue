<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Edit, Picture, Upload, Check } from '@element-plus/icons-vue'

// 文章内容状态
const article = reactive({
  title: '',
  content: '',
  image: '',
  tags: []
})

// 编辑器状态
const isSaving = ref(false)
const isEditorScrolling = ref(false)
const isPreviewScrolling = ref(false)

// 标签输入
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref(null)
const fileInputRef = ref(null)

const triggerFileInput = () => {
  fileInputRef.value?.click()
}



// 保存文章
const saveArticle = () => {
  if (!article.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  
  if (!article.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  
  isSaving.value = true
  
  // 这里应该调用API保存文章
  // 模拟API调用
  setTimeout(() => {
    ElMessage.success('文章保存成功')
    isSaving.value = false
  }, 1000)
}

// 发布文章
const publishArticle = () => {
  if (!article.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  
  if (!article.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }

  if (!article.image) {
    ElMessage.warning('请上传文章封面')
    return
  }
  
  const articleData = {
    title: article.title,
    content: article.content,
    image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=200&fit=crop&q=80',
    author:'test'
  }
  // 这里应该调用API发布文章
  post('/api/article/save',articleData,()=>{
    ElMessage.success('文章发布成功')
  } )
}

// 处理标签相关操作
const showTagInput = () => {
  tagInputVisible.value = true
  // 在DOM更新后聚焦输入框
  setTimeout(() => {
    tagInputRef.value?.input?.focus()
  })
}

const handleTagInputConfirm = () => {
  if (tagInputValue.value) {
    if (!article.tags.includes(tagInputValue.value)) {
      article.tags.push(tagInputValue.value)
    }
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

const handleTagClose = (tag) => {
  article.tags = article.tags.filter(t => t !== tag)
}

// 处理图片上传
const handleImageUpload = (event) => {
  console.log("触发了")
  const file = event.target.files[0]
  if (file) {
    // 在实际应用中，这里应该上传图片到服务器
    // 这里仅做演示，将图片转为base64显示
    const reader = new FileReader()
    reader.onload = (e) => {
      article.image = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// 插入Markdown语法
const insertMarkdown = (syntax) => {
  const textarea = document.getElementById('markdown-editor')
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = textarea.value
  
  let insertion = ''
  let newCursorPos = 0
  
  switch(syntax) {
    case 'bold':
      insertion = `**${text.substring(start, end) || '粗体文本'}**`
      newCursorPos = start + 2
      break
    case 'italic':
      insertion = `*${text.substring(start, end) || '斜体文本'}*`
      newCursorPos = start + 1
      break
    case 'heading':
      insertion = `\n## ${text.substring(start, end) || '标题'}\n`
      newCursorPos = start + 4
      break
    case 'link':
      insertion = `[${text.substring(start, end) || '链接文本'}](url)`
      newCursorPos = start + 1
      break
    case 'image':
      insertion = `![${text.substring(start, end) || '图片描述'}](图片URL)`
      newCursorPos = start + 2
      break
    case 'code':
      insertion = `\`\`\`\n${text.substring(start, end) || '代码块'}\n\`\`\``
      newCursorPos = start + 4
      break
    case 'list':
      insertion = `\n- ${text.substring(start, end) || '列表项'}\n`
      newCursorPos = start + 3
      break
  }
  
  article.content = text.substring(0, start) + insertion + text.substring(end)
  
  // 在DOM更新后设置光标位置
  setTimeout(() => {
    textarea.focus()
    if (text.substring(start, end)) {
      // 有选中文本时，选中插入的全部内容
      textarea.setSelectionRange(start, start + insertion.length);
    } else {
      // 无选中文本时，将光标移动到预设位置
      textarea.setSelectionRange(newCursorPos, newCursorPos);
    }
  });
}

// 处理编辑器滚动
const handleEditorScroll = (e) => {
  if (isPreviewScrolling.value) return
  isEditorScrolling.value = true
  const editor = e.target
  const preview = document.querySelector('.markdown-preview')
  const percentage = editor.scrollTop / (editor.scrollHeight - editor.clientHeight)
  preview.scrollTop = percentage * (preview.scrollHeight - preview.clientHeight)
  setTimeout(() => isEditorScrolling.value = false, 100)
}

// 处理预览区滚动
const handlePreviewScroll = (e) => {
  if (isEditorScrolling.value) return
  isPreviewScrolling.value = true
  const preview = e.target
  const editor = document.getElementById('markdown-editor')
  const percentage = preview.scrollTop / (preview.scrollHeight - preview.clientHeight)
  editor.scrollTop = percentage * (editor.scrollHeight - editor.clientHeight)
  setTimeout(() => isPreviewScrolling.value = false, 100)
}

</script>

<template>
  <div class="write-container">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="left-tools">     
          <el-button-group class="markdown-tools">
            <el-tooltip content="标题" placement="bottom">
              <el-button @click="insertMarkdown('heading')">
                <span class="tool-text">H</span>
              </el-button>
            </el-tooltip>
            <el-tooltip content="粗体" placement="bottom">
              <el-button @click="insertMarkdown('bold')">
                <span class="tool-text"><b>B</b></span>
              </el-button>
            </el-tooltip>
            <el-tooltip content="斜体" placement="bottom">
              <el-button @click="insertMarkdown('italic')">
                <span class="tool-text"><i>I</i></span>
              </el-button>
            </el-tooltip>
            <el-tooltip content="链接" placement="bottom">
              <el-button @click="insertMarkdown('link')">
                <span class="tool-text">🔗</span>
              </el-button>
            </el-tooltip>
            <el-tooltip content="图片" placement="bottom">
              <el-button @click="insertMarkdown('image')">
                <el-icon><Picture /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="代码块" placement="bottom">
              <el-button @click="insertMarkdown('code')">
                <span class="tool-text">&lt;/&gt;</span>
              </el-button>
            </el-tooltip>
            <el-tooltip content="列表" placement="bottom">
              <el-button @click="insertMarkdown('list')">
                <span class="tool-text">• </span>
              </el-button>
            </el-tooltip>
          </el-button-group>
      </div>
      
      <div class="right-tools">
        <el-button @click="saveArticle" :loading="isSaving">
          保存草稿
        </el-button>
        <el-button type="primary" @click="publishArticle">
          <el-icon><Check /></el-icon> 发布文章
        </el-button>
      </div>
    </div>
    
    <!-- 文章标题 -->
    <div class="title-container">
      <el-input
        v-model="article.title"
        placeholder="请输入文章标题"
        class="title-input"
        maxlength="100"
        show-word-limit
      />
    </div>
    
    <!-- 封面图片上传 -->
    <div class="cover-container">
      <div v-if="article.image" class="image-preview">
        <img :src="article.image" alt="文章封面" />
        <div class="image-actions">
          <el-button type="danger" size="small" @click="article.image = ''">
            删除封面
          </el-button>
        </div>
      </div>
      <div v-else class="upload-container">
        <el-button type="primary" plain @click="triggerFileInput">
          <el-icon><Upload /></el-icon>
          <span>上传封面图片</span>
        </el-button>
        <input ref="fileInputRef" type="file" class="file-input" accept="image/*" @change="handleImageUpload" />
        <div class="upload-tip">建议尺寸: 1200 x 400 像素，JPG/PNG 格式</div>
      </div>
    </div>
    
    <!-- 标签 -->
    <div class="tags-container">
      <span class="tags-label">文章标签:</span>
      <el-tag
        v-for="tag in article.tags"
        :key="tag"
        closable
        @close="handleTagClose(tag)"
        class="article-tag"
      >
        {{ tag }}
      </el-tag>
      <el-input
        v-if="tagInputVisible"
        ref="tagInputRef"
        v-model="tagInputValue"
        class="tag-input"
        size="small"
        @keyup.enter="handleTagInputConfirm"
        @blur="handleTagInputConfirm"
      />
      <el-button v-else class="button-new-tag" size="small" @click="showTagInput">
        + 添加标签
      </el-button>
    </div>
    
    <!-- 编辑器和预览区域 -->
    <div class="editor-container">
      <div class="markdown-editor-wrapper">
        <textarea
          id="markdown-editor"
          v-model="article.content"
          class="markdown-editor"
          placeholder="请输入文章内容，支持 Markdown 格式..."
          @scroll="handleEditorScroll"
        ></textarea>
      </div>
      <div class="markdown-preview" @scroll="handlePreviewScroll">
        <div v-if="article.content" v-html="renderMarkdown(article.content)" class="preview-content"></div>
        <div v-else class="empty-preview">预览区域为空，请先编写内容</div>
      </div>
    </div>
  </div>
</template>

<script>
// 引入markdown-it库进行Markdown渲染
import MarkdownIt from 'markdown-it'
import { post } from '@/net'

const md = new MarkdownIt({
  html: true,        // 允许HTML标签
  breaks: true,      // 转换\n为<br>
  linkify: true,     // 自动转换URL为链接
  typographer: true  // 启用一些语言中立的替换和引号美化
})

function renderMarkdown(text) {
  return md.render(text || '')
}

export default {
  methods: {
    renderMarkdown
  }
}
</script>

<style scoped>
.write-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 40px);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.left-tools, .right-tools {
  display: flex;
  align-items: center;
  gap: 10px;
}

.markdown-tools {
  display: flex;
  gap: 5px;
}

.tool-text {
  font-size: 14px;
  font-weight: bold;
}

.title-container {
  margin-bottom: 20px;
}

.title-input {
  font-size: 24px;
  font-weight: bold;
}

.cover-container {
  margin-bottom: 20px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  padding: 20px;
  text-align: center;
}

.image-preview {
  position: relative;
  max-width: 100%;
  overflow: hidden;
}

.image-preview img {
  max-width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 4px;
}

.image-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 4px;
  padding: 5px;
}

.upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0;
  width: 0%;
  height: 0%;
  cursor: pointer;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
}

.tags-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.tags-label {
  font-weight: bold;
  margin-right: 10px;
}

.article-tag {
  margin-right: 5px;
}

.tag-input {
  width: 100px;
}

.editor-container {
  display: flex;
  flex-direction: row;
  height: 100vh;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.markdown-editor-wrapper {
  width: 50%;
  height: 100%;
  border-right: 1px solid #dcdfe6;
}

.markdown-editor {
  width: 100%;
  height: 100%;
  padding: 15px;
  border: none;
  resize: none;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
  font-size: 14px;
  line-height: 1.6;
  outline: none;
}

.markdown-preview {
  width: 50%;
  height: 100%;
  padding: 15px;
  overflow-y: auto;
  background-color: #fafafa;
}

.preview-content {
  line-height: 1.8;
}

.empty-preview {
  color: #909399;
  text-align: center;
  margin-top: 200px;

}

/* 预览区域样式 */
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

/* 响应式调整 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .right-tools {
    width: 100%;
    justify-content: flex-end;
  }
  
  .editor-container {
    height: 400px;
  }
}
</style>
