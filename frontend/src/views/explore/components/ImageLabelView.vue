<template>
  <div class="label-container">
    <iframe
      :src="labelStudioUrl"
      class="label-studio-frame"
      frameborder="0"
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowfullscreen
      @load="onIframeLoad"
    ></iframe>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const labelStudioUrl = ref('http://localhost:8081')

// 注入自定义样式
const injectCustomStyles = (iframe) => {
  try {
    const style = iframe.contentDocument.createElement('style')
    style.textContent = `
      /* 修改主题色 */
      :root {
        --primary-color: #409EFF;
        --secondary-color: #67C23A;
      }

      /* 修改工具栏样式 */
      .lsf-toolbar {
        background-color: #f5f7fa;
        border-bottom: 1px solid #e4e7ed;
      }

      /* 修改标注区域样式 */
      .lsf-annotation-area {
        background-color: #fff;
      }

      /* 修改标签样式 */
      .lsf-tag {
        border-radius: 4px;
        padding: 4px 8px;
      }

      /* 修改按钮样式 */
      .lsf-button {
        border-radius: 4px;
        transition: all 0.3s;
      }

      /* 修改输入框样式 */
      .lsf-input {
        border-radius: 4px;
        border: 1px solid #dcdfe6;
      }

      /* 修改下拉菜单样式 */
      .lsf-dropdown {
        border-radius: 4px;
        box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
      }

      /* 修改滚动条样式 */
      ::-webkit-scrollbar {
        width: 6px;
        height: 6px;
      }

      ::-webkit-scrollbar-thumb {
        background: #c0c4cc;
        border-radius: 3px;
      }

      ::-webkit-scrollbar-track {
        background: #f5f7fa;
      }

      /* 修改 Logo 样式 */
      .lsf-header__logo {
        background-image: url('您的logo图片URL') !important;
        background-size: contain !important;
        background-repeat: no-repeat !important;
        background-position: center !important;
        width: 120px !important; /* 调整宽度 */
        height: 40px !important; /* 调整高度 */
      }

      /* 隐藏原有的 Logo 文字 */
      .lsf-header__logo-text {
        display: none !important;
      }
    `
    iframe.contentDocument.head.appendChild(style)
  } catch (e) {
    console.error('注入样式失败:', e)
  }
}

// iframe 加载完成后注入样式
const onIframeLoad = (event) => {
  const iframe = event.target
  injectCustomStyles(iframe)
}

// 如果需要传递项目ID或其他参数
onMounted(() => {
  // 从路由参数中获取项目ID
  const projectId = route.query.projectId
  if (projectId) {
    labelStudioUrl.value = `http://localhost:8081/projects/${projectId}`
  }
})
</script>

<style scoped>
.label-container {
  height: 100vh;
  width: 100%;
  background: #fff;
}

.label-studio-frame {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
