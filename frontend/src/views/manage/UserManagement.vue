<template>
  <div class="management-container" ref="tableContainerRef">
    <el-table class="table" :data="paginatedUsers" style="width: 100%;" border stripe ref="tableRef">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="用户名" align="center" />
      <el-table-column prop="email" label="邮箱" width="220" align="center" />
      <el-table-column prop="role" label="权限" width="120" align="center" />
      <el-table-column prop="register_time" label="注册时间" width="180" :formatter="formatDate" align="center" />
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-block">
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="dynamicPageSize"
      :background="true"
      layout="prev, pager, next, jumper"
      :total="allUsers.length"
      @current-change="handleCurrentChange"
    />
  </div>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editDialogVisible" title="修改用户信息" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="editForm.role" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="editForm.password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="submitEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { get, post, deleteUser } from '@/net'
import { ElMessage, ElMessageBox } from 'element-plus'

// 用户数据
const allUsers = ref([])
const tableContainerRef = ref(null)
const tableRef = ref(null)

// 分页相关
const currentPage = ref(1)
const dynamicPageSize = ref(10) // 默认值，会被动态计算替换

// 计算当前页显示的用户
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * dynamicPageSize.value
  const end = start + dynamicPageSize.value
  return allUsers.value.slice(start, end)
})

// 编辑对话框相关
const editDialogVisible = ref(false)
const editForm = ref({
  id: null,
  username: '',
  email: '',
  role: '',
  password: '',
})

// 获取用户数据
const loadUserListData = () => {
  get('/api/account/all', (data) => {
    console.log('获取用户数据成功', data)
    allUsers.value = data
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
          const maxPage = Math.ceil(allUsers.value.length / newSize) || 1
          if (currentPage.value > maxPage) {
            currentPage.value = maxPage
          }
        }
      } else if (availableHeight === 0 && allUsers.value.length > 0) {
        // 如果表格高度为0但有数据，设置一个小的默认值
        dynamicPageSize.value = 5
      }
    }
  }
}

let resizeObserver = null

// 在组件挂载时获取数据并设置 ResizeObserver
onMounted(() => {
  loadUserListData()
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

// 格式化日期
const formatDate = (row, column, cellValue) => {
  if (cellValue) {
    return cellValue.split('T')[0]
  }
  return ''
}

const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除该用户吗?',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      deleteUser(id, () => {
        ElMessage.success('用户删除成功')
        loadUserListData() // 重新加载数据
      }, (message) => {
        ElMessage.error(message)
      })
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 处理编辑按钮点击
const handleEdit = (row) => {
  editForm.value = { ...row } // 使用展开运算符复制对象，避免直接修改原始数据
  editDialogVisible.value = true
}

// 提交编辑
const submitEdit = () => {
  post('/api/account/edit', editForm.value, () => {
    ElMessage.success('用户信息更新成功')
    editDialogVisible.value = false
    loadUserListData() // 重新加载数据
  }, (message) => {
    ElMessage.error(message)
  })
}

// 取消编辑
const cancelEdit = () => {
  editDialogVisible.value = false
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

/* .nstration class was unused, removed from template */
</style>