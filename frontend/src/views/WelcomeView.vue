<script setup>
import { Transition } from 'vue'
</script>

<template>
  <body style="margin: 0 0 0 0">
  <div style="width: 100vw;height: 100vh;overflow:hidden;display: flex">
    <div style="flex:1; background-color: rgba(255,255,255,0.31) ;">
      <el-image src="https://s2.loli.net/2023/12/19/NymEQgeLSbKqij4.png" style="width: 100%;height: 100%" fit="cover"/>
    </div>
    <div class="welcome-title">
      <p style="font-size: 28px;font-weight: bold">欢迎来到人工智能教学案例分享平台</p>
      <p style="font-size: 14px; margin-top: 10px">在这里你可以分享自己的人工智能学习案例，让更多人了解人工智能</p>
      <p style="font-size: 14px; margin-top: 5px">在这里你可以学习人工智能，快速了解人工智能</p>
    </div>
    <div class="right-card">
      <router-view v-slot="{ Component }">
        <Transition name="el-fade-in-linear"  mode="out-in">
          <component :is="Component"></component>
        </Transition>
      </router-view>
    </div>
  </div>
  </body>
</template>

<style scoped>
.welcome-title{
  position:absolute;
  bottom:30px;
  left:30px;
  color: #01012f;
  text-shadow: 0px 0px 10px;
}
.right-card{
  width: 25%;
  z-index: 1;
  background-color: white;
}
</style>