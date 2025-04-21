<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { get } from '@/net'
import ArticleComments from '@/components/article/ArticleComments.vue'
const route = useRoute()
const article = ref({})

const loadArticle = () => {
  get(`/api/article/${route.params.id}`, data => {
    article.value = data
    console.log("publishtime",data.publish_time)
    document.title = data.title + ' - AI应用平台'
  })
}

// 引入markdown-it库进行Markdown渲染
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt({
  html: true,        // 允许HTML标签
  breaks: true,      // 转换\n为<br>
  linkify: true,     // 自动转换URL为链接
  typographer: true  // 启用一些语言中立的替换和引号美化
})

function renderMarkdown(text) {
  return md.render(text || '')
}


onMounted(() => {
  loadArticle()
})
</script>

<template>
  <div class="article-container" >
    <header>
      <el-image 
        v-if="article.image"
        :src="article.image"
        style=" margin:0px; display: block"
        fit="cover"
      />
      <h1 class="article-title">{{ article.title }}</h1>
      <div class="article-meta">
        <span>作者：{{ article.author }}</span>
        <span>发布时间：{{ article.publish_time }}</span>
        <span>浏览：{{ article.view_count }}</span>
        <span>最近点赞：{{ article.recent_likes }}</span>
      </div>
    </header>
    <el-divider />
    
    <div class="markdown-preview" >
        <div v-if="article.content" v-html="renderMarkdown(article.content)" class="article-content"></div>
        <div v-else class="empty-preview">预览区域为空，请先编写内容</div>
      </div>
    <article-comments v-if="article.comments" :comments="article.comments" />
  </div>
</template>

<style scoped>
.article-container {
  
  margin: 20px auto;
  padding: 0px;
  margin-top: 0px;
  align-items: center;
}

.article-title {
  font-size: 24px;
  margin: 0 0 10px;
  margin-left: 20px;
}

.article-meta {
  color: #666;
  font-size: 14px;
  margin-left: 20px;
}

.article-meta span {
  margin-right: 15px;
}

.article-content {
  line-height: 1.8;
  font-size: 16px;
  margin: 8px;
  padding: 8px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);  /* 调整阴影参数 */
  width: 100%;
}


.article-content :deep(p) {
  margin: 1em 0;
}

.article-content :deep(pre) {
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  margin: 10px 0;
  border-radius: 4px;
}

.markdown-preview {
  
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  overflow-x: hidden;  
  overflow-y: auto;
  background-color: #fafafa;
}

.markdown-preview :deep(div) {
  max-width: 100%;    
  width: 100%;
}

.markdown-preview :deep(*) {
  max-width: 100%;
  text-align: left;   
}

.markdown-preview :deep(pre),
.markdown-preview :deep(code) {
  text-align: left;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.markdown-preview :deep(img),
.markdown-preview :deep(pre) {
  margin: 20px auto;
  display: block;
}
</style>