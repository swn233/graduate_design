package com.example.backend.controller;

import com.example.backend.entity.dto.Articles;
import com.example.backend.entity.vo.request.ArticlesWriteVO;
import com.example.backend.service.ArticlesService;
import com.example.backend.entity.RestBean;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @Resource
    ArticlesService articlesService;

    @GetMapping("/{id}")
    public RestBean<Articles> getArticleById(@PathVariable Long id) {
        Articles article = articlesService.getArticleById(id);
        return article != null ? RestBean.success(article) : RestBean.failure(404, "未找到该文章");
    }

    @GetMapping("/all")
    public RestBean<List<Articles>> getAllArticles() {
        List<Articles> articles = articlesService.getAllArticles();
        return RestBean.success(articles);
    }

    @PostMapping("/save")
    public RestBean<String> saveArticle(@RequestBody ArticlesWriteVO vo) {
        String result = articlesService.saveArticleByVO(vo);
        return result != null ? RestBean.success(result) : RestBean.failure(400, "文章保存失败");
    }
}