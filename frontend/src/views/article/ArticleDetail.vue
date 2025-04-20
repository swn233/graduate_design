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
    
    <div class="article-content" v-html="article.content" />
    <article-comments v-if="article.comments" :comments="article.comments" />
  </div>
</template>

<style scoped>
.article-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0px;
  margin-top: 0px;
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
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
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
</style>