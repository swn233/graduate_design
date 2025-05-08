import {createRouter,createWebHistory}from "vue-router";
import {unauthorized} from "@/net/index.js";
const router=createRouter({
    history:createWebHistory(import.meta.env.BASE_URL),
    routes:[
    {
      path: '/article/:id',
      name: 'authorized-ArticleDetail',
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
            name:'authorized-index',
            component:()=>import('../views/indexView.vue')
        },
            {
                path:'/write',
                name:'authorized-write',
                component:()=>import('@/views/article/write/WriteArticle.vue')
            }
            ,
            {
                path:'/notebook',
                name:'authorized-notebook',
                component:()=>import('@/views/explore/components/NotebookView.vue')
            },
            {
                path:'/label',
                name:'authorized-label',
                component:()=>import('@/views/explore/components/ImageLabelView.vue')
            },{
                path: '/manage',
                name:'authorized-manage',
                component: () => import('../views/manage/ManageView.vue'),
                redirect: '/manage/users',
                children: [
                    { path: 'users',
                        name:'authorized-users',
                        component: () => import('../views/manage/UserManagement.vue') },
                    { path: 'datasets', 
                        name:'authorized-datasets',
                        omponent: () => import('../views/manage/DatasetManagement.vue') },
                    { path: 'articles',
                        name:'authorized-articles', 
                        component: () => import('../views/manage/ArticleManagement.vue') },
                    { path: 'models', 
                        name:'authorized-models',
                        component: () => import('../views/manage/ModelManagement.vue') }
                ]
            }
    ]
})

router.beforeEach((to,from,next)=>{
    const isUnauthorized=unauthorized()
    if(to.name.startsWith('welcome-')&&!isUnauthorized){
        next('/index')
    }else if(to.name.startsWith('authorized')&&isUnauthorized){
        next('/')
    }else {
        next()
    }
})
export default router