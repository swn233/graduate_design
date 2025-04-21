<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  }
})
</script>

<template>
  <div class="comments-section">
    <h3 class="comment-title">用户评论（{{ comments.length }}）</h3>
    <el-divider />
    <div class="comment-list">
      <el-card 
        v-for="(comment, index) in comments" 
        :key="index"
        class="comment-item"
      >
        <div class="comment-header">
          <span class="comment-author">{{ comment.author }}</span>
          <span class="comment-time">{{ comment.timestamp | dateFormat }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.comments-section {
  margin-top: 0px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.comment-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

.comment-item {
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
}

.comment-item:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.comment-author {
  color: #409eff;
  font-weight: 500;
}

.comment-time {
  color: #999;
}

.comment-content {
  line-height: 1.6;
  color: #666;
}
</style>