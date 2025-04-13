<script setup>
import {ref}from 'vue'

import router from "@/router"
import {login} from "@/net/index.js";

const form=ref({
  username:'',
  password:'',
  remember:false,
})

const formRef=ref()

const rule={
  username:[
    {required:true,message:'请输入用户名'}
  ],
  password:[
    {required:true,message:'请输入密码'}
  ]
}

function userLogin(){
  formRef.value.validate((valid)=>{
    if (valid)
      login(form.value.username,form.value.password,form.value.remember,()=>{router.push('/index')})
  })
}
</script>

<template>
  <div>
    <div style="text-align: center;margin:0 20px">
      <!--  -->
      <div style="margin-top: 120px">
        <div style="font-size: 28px;font-weight: bold">登录</div>
        <div style="margin-top: 15px">
          <div style="font-size: 14px;color: grey">请输入用户名和密码</div>
        </div>
      </div>
      <!--  -->
      <div>
        <el-form :rules="rule" :model="form"  style="margin-top: 20px" ref="formRef">
          <el-form-item prop="username">
            <el-input style="margin:0 20px;" v-model="form.username" placeholder="用户名/邮箱" type="text">
              <template #prefix>
                <el-icon> <User /> </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input style="margin:0 20px;" v-model="form.password" placeholder="密码" type="password">
              <template #prefix>
                <el-icon> <Lock /> </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-row>
            <el-col :span="12" style="text-align: left">
              <el-form-item >
                <el-checkbox style="margin:0 20px" v-model="form.remember" label="记住我"></el-checkbox>
              </el-form-item>
            </el-col>
            <el-col :span="12" style="text-align: right">
              <el-link  @click="router.push('/welcome/reset')" style="margin: 0 20px" type="primary">忘记密码？</el-link>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <!--  -->
      <div style="text-align: center">
        <el-button @click="userLogin" style="width: 270px;" type="success" plain>立即登录</el-button>
      </div>
      <el-divider>
        <span style="font-size: 14px;color: grey">没有账号</span>
      </el-divider>
      <div style="text-align: center">
        <el-button @click="router.push('/welcome/register')" style="width: 270px" type="warning" plain>立即注册</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
body{
  display:block;
  margin:0px;
}
</style>