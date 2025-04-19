package com.example.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Articles;
import com.example.backend.mapper.ArticlesMapper;
import com.example.backend.service.ArticlesService;

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
}
