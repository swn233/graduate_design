<template>
  <div class="management-container" ref="tableContainerRef">
    <el-table class="table" :data="paginatedArticles" style="width: 100%;" border stripe ref="tableRef">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="title" label="标题" align="center" />
      <el-table-column prop="author" label="作者" width="120" align="center" />
      <el-table-column prop="publish_time" label="发布时间" width="180" align="center" />
      <el-table-column prop="view_count" label="浏览量" width="100" align="center" />
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editArticle(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteArticle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-block">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="dynamicPageSize"
        :background="true"
        layout="prev, pager, next, jumper"
        :total="articleList.length"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { get } from '@/net'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const articleList = ref([])
const tableContainerRef = ref(null)
const tableRef = ref(null)

// 分页相关
const currentPage = ref(1)
const dynamicPageSize = ref(10) // 默认值，会被动态计算替换

// 计算当前页显示的文章
const paginatedArticles = computed(() => {
  const start = (currentPage.value - 1) * dynamicPageSize.value
  const end = start + dynamicPageSize.value
  return articleList.value.slice(start, end)
})

const loadArticles = () => {
  get('/api/article/all', (data) => {
    articleList.value = data.map(item => ({
      id: item.id,
      title: item.title,
      author: item.author,
      publish_time: (item.publish_time || '').split('T')[0],
      view_count: item.view_count,
      content: item.content,
      image: item.image
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
          const maxPage = Math.ceil(articleList.value.length / newSize) || 1
          if (currentPage.value > maxPage) {
            currentPage.value = maxPage
          }
        }
      } else if (availableHeight === 0 && articleList.value.length > 0) {
        // 如果表格高度为0但有数据，设置一个小的默认值
        dynamicPageSize.value = 5
      }
    }
  }
}

let resizeObserver = null

// 在组件挂载时获取数据并设置 ResizeObserver
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

const editArticle = (article) => {
  router.push({
    path: '/write',
    query: {
      id: article.id,
      title: article.title,
      author: article.author,
      content: article.content,
      image: article.image,
      comments: article.comments || '[]',
      recentSevenDaysLikes: article.recentSevenDaysLikes || 0,
      recentLikes: article.recentLikes || 0,
      views: article.view_count || 0
    }
  })
}

const deleteArticle = (id) => {
  ElMessageBox.confirm('确定要删除该文章吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里添加删除接口调用
  })
}

// 处理分页当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}
</script>

<style scoped>
.management-container {
  padding: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 50px);
  box-sizing: border-box;
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