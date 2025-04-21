package com.example.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Articles;
import com.example.backend.entity.vo.request.ArticlesWriteVO;
import com.example.backend.mapper.ArticlesMapper;
import com.example.backend.service.ArticlesService;

import java.util.Date;
import java.util.List;
@Service
public class ArticlesServiceImpl extends ServiceImpl<ArticlesMapper, Articles> implements ArticlesService{
    @Override
    public Articles getArticleById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Articles> getAllArticles() {
        return this.list();
    }

    @Override
    public String saveArticleByVO(ArticlesWriteVO vo) {
        String title = vo.getTitle();
        String image = vo.getImage();
        String content = vo.getContent();
        String author = vo.getAuthor();
        if (title == null || image == null || content == null || author == null) {
            return "文章信息不完整";
        }
        
        // 修改comments字段为JSON数组格式
        Articles article = new Articles( null,title,image,content,30,"[]",author,new Date(),100,100);
        if( this.save(article)){
            return "文章保存成功";
        }
        return "文章保存失败";
    }
    
}
