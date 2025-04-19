import { get } from './index'

// 获取轮播图文章列表
export const fetchCarouselArticles = () => {
    return get('/api/article/carousel', () => {
        console.log('获取轮播图文章列表成功')
    })
}