<script setup>
const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})
</script>

<template>
  <div class="explore-container">
    <h1 class="explore-title">体验中心</h1>
    <p>这里是体验中心内容</p>
  </div>
</template>

<style scoped>
.explore-container {
  padding: 20px;
}

.explore-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}
</style>