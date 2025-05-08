<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { DocumentAdd } from '@element-plus/icons-vue'
import router from "@/router"
import { get, post } from '@/net'
import { ElMessage, ElMessageBox } from 'element-plus'

const articles = ref([])
const tableContainerRef = ref(null)
const tableRef = ref(null)

// 分页相关
const currentPage = ref(1)
const dynamicPageSize = ref(10) // 默认值，会被动态计算替换

// 计算当前页显示的文章
const paginatedArticles = computed(() => {
  const start = (currentPage.value - 1) * dynamicPageSize.value
  const end = start + dynamicPageSize.value
  return articles.value.slice(start, end)
})

const loadArticles = () => {
  get('/api/article/all', (data) => {
    articles.value = data.map(item => ({
      id: item.id,
      title: item.title,
      image: item.image,
      content: item.content,
      recentSevenDaysLikes: item.recentSevenDaysLikes,
      comments: item.comments,
      author: item.author,
      date: (item.publish_time || item.date).split('T')[0],
      views: item.view_count || item.views,
      recentLikes: item.recent_likes
    }))
    updatePageSize() // 数据加载后计算页面大小
  })
}

const updatePageSize = async () => {
  await nextTick() // 确保 DOM 更新
  if (tableRef.value && tableRef.value.$el) {
    const tableBodyWrapper = tableRef.value.$el.querySelector('.el-table__body-wrapper')
    if (tableBodyWrapper) {
      const availableHeight = tableBodyWrapper.clientHeight
      const firstRow = tableBodyWrapper.querySelector('.el-table__row')
      let rowHeight = 48 // 默认行高，Element Plus 的典型行
      if (firstRow) {
        rowHeight = firstRow.offsetHeight
      }

      if (availableHeight > 0 && rowHeight > 0) {
        // 计算可以显示的行数，向上取整以确保填满空间
        const newSize = Math.ceil(availableHeight / rowHeight)
        if (dynamicPageSize.value !== newSize) {
          dynamicPageSize.value = newSize
          // 如果当前页在新页面大小下超出范围，则调整
          const maxPage = Math.ceil(articles.value.length / newSize) || 1
          if (currentPage.value > maxPage) {
            currentPage.value = maxPage
          }
        }
      } else if (availableHeight === 0 && articles.value.length > 0) {
        // 如果表格高度为0但有数据，设置一个小的默认值
        dynamicPageSize.value = 5
      }
    }
  }
}

let resizeObserver = null

onMounted(() => {
  loadArticles()
  if (tableContainerRef.value) {
    resizeObserver = new ResizeObserver(() => {
      updatePageSize()
    })
    resizeObserver.observe(tableContainerRef.value)
  }
  updatePageSize() // 初始计算
})

onUnmounted(() => {
  if (resizeObserver && tableContainerRef.value) {
    resizeObserver.unobserve(tableContainerRef.value)
  }
  if (resizeObserver) {
    resizeObserver.disconnect()
  }
})

const postArticle = () => {
  window.open(`/write`, "_blank")
}

const editArticle = (row) => {
  router.push({
    path: '/write',
    query: {
      id: row.id,
      title: row.title,
      author: row.author,
      content: row.content,
      image: row.image
    }
  })
}

const deleteArticle = (id) => {
  ElMessageBox.confirm('确定要删除该文章吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    post(`/api/article/delete/${id}`, {}, (res) => {
      ElMessage.success('删除成功')
      loadArticles() // 重新加载数据
    }, (err) => {
      ElMessage.error(err)
    })
  })
}

// 处理分页当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}
</script>

<template>
  <div class="article-container" ref="tableContainerRef">
    <div class="toolbar">
      <el-button type="primary" :icon="DocumentAdd" @click="postArticle">新建文章</el-button>
    </div>
    
    <el-table class="table" :data="paginatedArticles" style="width: 100%;" border stripe ref="tableRef">
      <el-table-column prop="id" label="ID" width="60" align="center"/>
      <el-table-column prop="title" label="文章标题" align="center"/>
      <el-table-column prop="date" label="发布日期" width="180" align="center"/>
      <el-table-column prop="views" label="浏览次数" width="120" align="center"/>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" text @click="editArticle(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" text @click="deleteArticle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-block">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="dynamicPageSize"
        :background="true"
        layout="prev, pager, next, jumper"
        :total="articles.length"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.article-container {
  padding: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 50px);
  box-sizing: border-box;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.table {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.table :deep(.el-table__body-wrapper) {
  flex: 1;
  overflow-y: auto;
}

.pagination-block {
  margin-top: 10px;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
}
</style>