package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.Articles;
import java.util.List;

public interface ArticlesService extends IService<Articles>{

    /**
     * 根据文章 ID 查询文章
     * @param id 文章 ID
     * @return 文章实体
     */
    Articles getArticleById(Long id);

    /**
     * 查询所有文章
     * @return 文章列表
     */
    List<Articles> getAllArticles();
}
