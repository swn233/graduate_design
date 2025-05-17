<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 模拟当前用户数据
const currentUser = {
  username: '测试用户',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
}

// 示例评论数据
const comments = ref([
  {
    id: 1,
    author: '张三',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    content: '这篇文章写得很好，对我帮助很大！',
    timestamp: '2024-03-15 14:30:00'
  },
  {
    id: 2,
    author: '李四',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    content: '学习了，感谢分享！',
    timestamp: '2024-03-15 15:45:00'
  },
  {
    id: 3,
    author: '王五',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    content: '内容很详细，期待更多相关文章。',
    timestamp: '2024-03-15 16:20:00'
  }
])

const newComment = ref('')
const isSubmitting = ref(false)

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString()
}

// 发送评论
const submitComment = () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  isSubmitting.value = true
  
  // 模拟发送评论
  setTimeout(() => {
    const comment = {
      id: comments.value.length + 1,
      author: currentUser.username,
      avatar: currentUser.avatar,
      content: newComment.value.trim(),
      timestamp: new Date().toLocaleString()
    }
    
    comments.value.unshift(comment)
    newComment.value = ''
    isSubmitting.value = false
    ElMessage.success('评论发表成功')
  }, 500)
}
</script>

<template>
  <div class="comments-section">
    <h3 class="comment-title">用户评论（{{ comments.length }}）</h3>
    <el-divider />
    
    <!-- 评论输入框 -->
    <div class="comment-input">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        :maxlength="500"
        show-word-limit
      />
      <div class="input-footer">
        <el-button 
          type="primary" 
          @click="submitComment"
          :loading="isSubmitting"
          :disabled="!newComment.trim()"
        >
          发表评论
        </el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <el-card 
        v-for="comment in comments" 
        :key="comment.id"
        class="comment-item"
      >
        <div class="comment-header">
          <div class="user-info">
            <el-avatar :size="40" :src="comment.avatar">
              {{ comment.author?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <div class="user-details">
              <span class="comment-author">{{ comment.author }}</span>
              <span class="comment-time">{{ formatTime(comment.timestamp) }}</span>
            </div>
          </div>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.comments-section {
  margin-top: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.comment-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

.comment-input {
  margin-bottom: 30px;
}

.input-footer {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.comment-list {
  margin-top: 20px;
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
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.comment-author {
  color: #409eff;
  font-weight: 500;
  font-size: 14px;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  margin-left: 50px;
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
}
</style>