package com.example.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Articles;
import com.example.backend.mapper.ArticlesMapper;
import com.example.backend.service.ArticlesService;

@Service
public class ArticlesServiceImpl extends ServiceImpl<ArticlesMapper, Articles> implements ArticlesService{
    
}
