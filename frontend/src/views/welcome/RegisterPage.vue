<script setup>
import {reactive,ref,computed} from "vue";
import router from "@/router";
import {get, post} from "@/net";
import {Lock, User, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
const form=reactive(({
  username: '',
  password:'',
  password_repeat:'',
  email:'',
  code:''
}))

const coldTime=ref(0)
const formRef=ref()
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'));
  } else if (!/^[a-zA-Z0-9_]{3,16}$/.test(value)) {
    callback(new Error('用户名不能包含特殊字符'));
  }
  else {
    callback();
  }
};

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'));
  } else if (value!==form.password) {
    callback(new Error('两次输入的密码不一致'));
  }
  else {
    callback();
  }
};

const rule={
  username:[
    { validator: validateUsername,trigger:['blur','change']},
  ],
  password:[
    {required:true,message:'请输入密码',trigger:'blur'},
    {min:6,max:20,message:'密码长度在6-20之间',trigger:['blur','change']},
  ],
  password_repeat:[
    {validator:validatePassword,trigger:['blur','change']},
    {min:6,max:20,message:'密码长度在6-20之间',trigger:['blur','change']},
  ],
  code:[
    {required:true,message:'请输入验证码',trigger:'blur'},
    {min:6,max:6,message:'验证码长度为6',trigger:'blur'},
  ],
  email:[
    {required:true,message:'请输入邮箱地址',trigger:'blur'},
    {type:'email',message:'请输入正确的邮箱地址',trigger:['blur','change']},
  ]
}

function askCode() {
  if (isEmailValid) {
    coldTime.value=60
    get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
      ElMessage.success('验证码发送成功')
      setInterval(()=>coldTime.value--,1000)
    }, () => {
      ElMessage.error('验证码发送失败')
    })
  }
  else
  {
    ElMessage.error('请输入正确的邮箱地址')
  }
}
function  register(){
  formRef.value.validate((valid)=>{
    if(valid){
      post(`/api/auth/register`,{...form},()=>{
        ElMessage.success('注册成功,欢迎加入我们')
        router.push('/')
      })
    }
    else {
      ElMessage.error('注册失败')
    }
  })
}

const isEmailValid = computed(() => {
  return /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/.test(form.email)
})
</script>

<template>
  <div style="text-align: center;margin:0 20px">
    <div style="margin-top: 100px">
      <div style="font-size: 25px;font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color:grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
    </div>
    <div style="margin-top:40px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon> <User /> </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="10" type="password" placeholder="密码">
            <template #prefix>
              <el-icon> <Lock /> </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" maxlength="30" type="password" placeholder="重复密码">
            <template #prefix>
              <el-icon> <Lock /> </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" maxlength="20" type="text" placeholder="电子邮件地址">
            <template #prefix>
              <el-icon> <Message /> </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <div style="align-self: center;width: 100%;margin-right: 20px">
          <el-row :gutter="10" style="width: 100%;align-self: center;margin-right: 20px">
            <el-col :span="16">
              <el-input v-model="form.code" maxlength="10" type="password" placeholder="验证码">
                <template #prefix>
                  <el-icon> <Message /> </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="4" style="">
              <el-button @click="askCode" :disabled="!isEmailValid||coldTime>0" type="success" >
                {{coldTime > 0 ? `请稍后${coldTime}秒`: '获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
          </div>
        </el-form-item>
      </el-form>
        <el-button @click="register" style="width:100%" type="warning" plain>
          立即注册
        </el-button>
      <div style="margin-top: 20px">
        <span style="font-size: 14px;color: grey;line-height: 15px">已有账号?</span>
        <el-link style="translate:0 -1px" @click="router.push('/')">立即登录</el-link>
      </div>
    </div>

  </div>
</template>

<style scoped>

</style>