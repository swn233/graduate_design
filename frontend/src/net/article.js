import { get } from './index'

// 获取轮播图文章列表
// 根据文章 ID 查询文章
export const getArticleById = (id) => {
    return get(`/api/article/${id}`, () => {
        console.log(`获取文章 ID 为 ${id} 的文章成功`)
    })
}

// 查询所有文章
export const getAllArticles = () => {
    return get('/api/article/all', () => {
        console.log('获取所有文章成功')
    })
}
export const fetchCarouselArticles = () => {
    return get('/api/article/all', (data) => {
        console.log('获取轮播图文章列表成功', data)
        return data
    })
}