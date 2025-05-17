<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id

// 模拟课程数据
const courseData = ref({
  id: courseId,
  title: '目标检测之YOLO系列',
  subtitle: '从0到1学习目标检测 完整版',
  description: '本课程将带领大家系统学习YOLO系列目标检测算法，从YOLOv1到最新的YOLOv8，深入理解算法原理，掌握实战应用技巧。课程包含大量实践案例，帮助学员快速掌握目标检测技术。',
  coverImage: 'https://images.unsplash.com/photo-1591696331111-ef9586a5b17a?q=80&w=1470&auto=format&fit=crop',
  instructor: '张教授',
  level: '中级',
  duration: '20小时',
  students: 1234,
  price: 299,
  rating: 4.8,
  reviews: 256,
  isPurchased: false,
  isFavorite: false,
  progress: 0,
  chapters: [
    {
      id: 1,
      title: '第一章：目标检测基础',
      lessons: [
        {
          id: 101,
          title: '1.1 目标检测概述',
          duration: '30分钟',
          videoUrl: 'https://example.com/video1.mp4',
          isFree: true
        },
        {
          id: 102,
          title: '1.2 目标检测评价指标',
          duration: '45分钟',
          videoUrl: 'https://example.com/video2.mp4',
          isFree: true
        }
      ]
    },
    {
      id: 2,
      title: '第二章：YOLOv1详解',
      lessons: [
        {
          id: 201,
          title: '2.1 YOLOv1网络结构',
          duration: '60分钟',
          videoUrl: 'https://example.com/video3.mp4',
          isFree: false
        },
        {
          id: 202,
          title: '2.2 YOLOv1损失函数',
          duration: '50分钟',
          videoUrl: 'https://example.com/video4.mp4',
          isFree: false
        }
      ]
    },
    {
      id: 3,
      title: '第三章：YOLOv5实战',
      lessons: [
        {
          id: 301,
          title: '3.1 YOLOv5环境配置',
          duration: '40分钟',
          videoUrl: 'https://example.com/video5.mp4',
          isFree: false
        },
        {
          id: 302,
          title: '3.2 YOLOv5训练流程',
          duration: '55分钟',
          videoUrl: 'https://example.com/video6.mp4',
          isFree: false
        }
      ]
    }
  ]
})

// 新增：后端章节结构视频列表
const backendChapters = ref([])

onMounted(async () => {
  // 模拟B站视频数据，不从后端获取
  backendChapters.value = [
    {
      chapter: "chapter1",
      chapterTitle: "第一章：计算机视觉基础",
      lessons: [
        {
          id: 1,
          title: "1.1 计算机视觉概述",
          bilibiliId: "BV1GJ411x7h7",
          isFree: true
        },
        {
          id: 2,
          title: "1.2 图像处理基础",
          bilibiliId: "BV1r7411k7Yg",
          isFree: true
        }
      ]
    },
    {
      chapter: "chapter2",
      chapterTitle: "第二章：目标检测基础",
      lessons: [
        {
          id: 3,
          title: "2.1 目标检测概述",
          bilibiliId: "BV1AF411b7SL",
          isFree: true
        },
        {
          id: 4,
          title: "2.2 目标检测评价指标",
          bilibiliId: "BV1JK4y1N7HC",
          isFree: false
        }
      ]
    },
    {
      chapter: "chapter3",
      chapterTitle: "第三章：YOLO系列详解",
      lessons: [
        {
          id: 5,
          title: "3.1 YOLOv1详解",
          bilibiliId: "BV1yi4y1t7rk",
          isFree: false
        },
        {
          id: 6,
          title: "3.2 YOLOv3详解",
          bilibiliId: "BV1Vg411V7MD",
          isFree: false
        },
        {
          id: 7,
          title: "3.3 YOLOv5实战",
          bilibiliId: "BV1T44y1x7GY",
          isFree: false
        }
      ]
    },
    {
      chapter: "chapter4",
      chapterTitle: "第四章：项目实战",
      lessons: [
        {
          id: 8,
          title: "4.1 口罩检测项目实战",
          bilibiliId: "BV1QU4y1c7ZR",
          isFree: false
        },
        {
          id: 9,
          title: "4.2 行人检测项目实战",
          bilibiliId: "BV1Aa411F76J",
          isFree: false
        }
      ]
    }
  ]
})

// 当前播放的视频
const currentVideo = ref(null)
const isPlaying = ref(false)

// 添加B站视频ID属性
const getBilibiliVideoId = (video) => {
  // 从后端数据获取B站视频ID，如果没有则使用默认ID
  return video.bilibiliId || 'BV1GJ411x7h7'
}

// 评价相关
const showReviewDialog = ref(false)
const reviewForm = ref({
  rating: 5,
  content: ''
})

// 购买课程
const purchaseCourse = () => {
  ElMessageBox.confirm(
    `确认购买课程《${courseData.value.title}》？\n价格：¥${courseData.value.price}`,
    '购买确认',
    {
      confirmButtonText: '确认购买',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 模拟购买成功
    courseData.value.isPurchased = true
    ElMessage.success('购买成功！')
  }).catch(() => {})
}

// 收藏/取消收藏
const toggleFavorite = () => {
  courseData.value.isFavorite = !courseData.value.isFavorite
  ElMessage.success(courseData.value.isFavorite ? '收藏成功' : '已取消收藏')
}

// 提交评价
const submitReview = () => {
  if (!reviewForm.value.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  // 模拟提交评价
  courseData.value.reviews++
  courseData.value.rating = ((courseData.value.rating * (courseData.value.reviews - 1) + reviewForm.value.rating) / courseData.value.reviews).toFixed(1)
  
  showReviewDialog.value = false
  ElMessage.success('评价提交成功')
  reviewForm.value = { rating: 5, content: '' }
}

// 播放视频
const playVideo = (video) => {
  currentVideo.value = video
  isPlaying.value = true
}

// 更新学习进度
const updateProgress = () => {
  const totalLessons = courseData.value.chapters.reduce((sum, chapter) => sum + chapter.lessons.length, 0)
  const completedLessons = courseData.value.chapters.reduce((sum, chapter) => 
    sum + chapter.lessons.filter(lesson => lesson.isCompleted).length, 0)
  courseData.value.progress = Math.round((completedLessons / totalLessons) * 100)
}

// 关闭视频播放
const closeVideo = () => {
  isPlaying.value = false
  currentVideo.value = null
}
</script>

<template>
  <div class="course-container">
    <!-- 课程头部信息 -->
    <div class="course-header">
      <div class="course-info">
        <h1 class="course-title">{{ courseData.title }}</h1>
        <p class="course-subtitle">{{ courseData.subtitle }}</p>
        <div class="course-meta">
          <span class="instructor">讲师：{{ courseData.instructor }}</span>
          <span class="level">难度：{{ courseData.level }}</span>
          <span class="duration">时长：{{ courseData.duration }}</span>
          <span class="students">学习人数：{{ courseData.students }}</span>
          <span class="rating">
            <el-rate
              v-model="courseData.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
            ({{ courseData.reviews }}条评价)
          </span>
        </div>
        <p class="course-description">{{ courseData.description }}</p>
        
        <!-- 课程操作按钮 -->
        <div class="course-actions">
          <el-button 
            type="primary" 
            size="large"
            @click="purchaseCourse"
            v-if="!courseData.isPurchased"
          >
            立即购买 ¥{{ courseData.price }}
          </el-button>
          <el-button 
            type="success" 
            size="large"
            v-else
          >
            继续学习
          </el-button>
          <el-button 
            :type="courseData.isFavorite ? 'warning' : 'default'"
            size="large"
            @click="toggleFavorite"
          >
            <el-icon>
              <component :is="courseData.isFavorite ? CollectionFilled : Collection" />
            </el-icon>
            {{ courseData.isFavorite ? '已收藏' : '收藏' }}
          </el-button>
          <el-button 
            type="primary" 
            size="large"
            @click="showReviewDialog = true"
            v-if="courseData.isPurchased"
          >
            评价课程
          </el-button>
        </div>
      </div>
      <div class="course-cover">
        <img :src="courseData.coverImage" :alt="courseData.title">
        <div class="progress-bar" v-if="courseData.isPurchased">
          <el-progress 
            :percentage="courseData.progress"
            :format="format => `学习进度：${format}%`"
          />
        </div>
      </div>
    </div>

    <!-- 课程目录 -->
    <div class="course-content">
      <h2 class="content-title">后端视频课程目录</h2>
      <div class="chapters-list">
        <div v-for="chapter in backendChapters" :key="chapter.chapter" class="chapter-item">
          <div class="chapter-header">
            <h3 class="chapter-title">{{ chapter.chapterTitle || chapter.chapter }}</h3>
          </div>
          <div class="lessons-list">
            <div v-for="lesson in chapter.lessons" :key="lesson.id" class="lesson-item" @click="playVideo(lesson)">
              <div class="lesson-info">
                <span class="lesson-title">{{ lesson.title }}</span>
                <el-tag :type="lesson.isFree ? 'success' : 'warning'" size="small">
                  {{ lesson.isFree ? '免费' : '付费' }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 视频播放弹窗 -->
    <el-dialog
      v-model="isPlaying"
      :title="currentVideo?.title"
      width="80%"
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      @close="closeVideo"
    >
      <div class="video-player">
        <iframe 
          v-if="currentVideo"
          :src="`https://player.bilibili.com/player.html?bvid=${getBilibiliVideoId(currentVideo)}&page=1&high_quality=1&danmaku=0`"
          scrolling="no"
          border="0"
          frameborder="no"
          framespacing="0"
          allowfullscreen="true"
          class="video-element"
        ></iframe>
      </div>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog
      v-model="showReviewDialog"
      title="课程评价"
      width="500px"
    >
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showReviewDialog = false">取消</el-button>
          <el-button type="primary" @click="submitReview">提交评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.course-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.course-header {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.course-info {
  flex: 1;
}

.course-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.course-subtitle {
  font-size: 18px;
  color: #666;
  margin-bottom: 20px;
}

.course-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.course-description {
  color: #666;
  line-height: 1.6;
}

.course-cover {
  width: 400px;
  height: 225px;
  border-radius: 8px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-content {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.content-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.chapter-item {
  margin-bottom: 20px;
}

.chapter-header {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 10px;
}

.chapter-title {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.lessons-list {
  padding-left: 20px;
}

.lesson-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.lesson-item:hover {
  background: #f5f7fa;
}

.lesson-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.lesson-title {
  color: #333;
}

.lesson-duration {
  color: #999;
  font-size: 14px;
}

.video-player {
  width: 100%;
  height: 0;
  padding-bottom: 56.25%; /* 16:9 比例 */
  position: relative;
  background: #000;
}

.video-element {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: none;
}

.course-actions {
  margin-top: 20px;
  display: flex;
  gap: 15px;
}

.progress-bar {
  margin-top: 15px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
}

.lesson-item .el-icon {
  margin-left: 10px;
}

@media (max-width: 768px) {
  .course-header {
    flex-direction: column;
  }

  .course-cover {
    width: 100%;
  }

  .course-meta {
    flex-wrap: wrap;
  }
}
</style>
