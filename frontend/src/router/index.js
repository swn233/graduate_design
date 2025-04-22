import {createRouter,createWebHistory}from "vue-router";
import {unauthorized} from "@/net/index.js";
const router=createRouter({
    history:createWebHistory(import.meta.env.BASE_URL),
    routes:[
    {
      path: '/article/:id',
      name: 'ArticleDetail',
      component: () => import('../views/article/ArticleDetail.vue'),
      props: true
    },
        {
            path:'/',
            name:'welcome',
            component:()=>import('../views/WelcomeView.vue'),
            children:[
                {
                    path:'',
                    name:'welcome-login',
                    component:()=>import('../views/welcome/LoginPage.vue')
                }, {
                    path:'/register',
                    name:'welcome-register',
                    component:()=>import('../views/welcome/RegisterPage.vue')
                }, {
                    path:'/reset',
                    name:'welcome-reset',
                    component:()=>import('../views/welcome/ResetPage.vue')
                }
                ]
        },
        {
            path:'/index',
            name:'index',
            component:()=>import('../views/indexView.vue')
        },
            {
                path:'/write',
                name:'write',
                component:()=>import('@/views/article/write/WriteArticle.vue')
            }
            ,
            {
                path:'/notebook',
                name:'notebook',
                component:()=>import('@/views/explore/components/NotebookView.vue')
            }
        
    ]
})
router.beforeEach((to,from,next)=>{
    const isUnauthorized=unauthorized()
    if(to.name.startsWith('welcome-')&&!isUnauthorized){
        next('/index')
    }else if(to.fullPath.startsWith('/index')&&isUnauthorized){
        next('/')
    }else {
        next()
    }
})
export default router