package com.example.backend.controller;

import com.example.backend.entity.dto.Articles;
import com.example.backend.entity.vo.request.ArticlesWriteVO;
import com.example.backend.service.ArticlesService;
import com.example.backend.entity.RestBean;
import jakarta.annotation.Resource;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/edit")
    public RestBean<String> editArticle(@RequestBody Articles article) {
        try {
            String result = articlesService.editArticle(article);
            return result != null ? RestBean.success(result) : RestBean.failure(400, "文章编辑失败");
        } catch (HttpMessageNotReadableException e) {
            return RestBean.failure(400, "日期格式错误,请使用 yyyy-MM-dd'T'HH:mm:ss.SSS 格式");
        }
    }

    @PostMapping("/delete/{id}")
    public RestBean<String> deleteArticle(@PathVariable("id") Long id) {
        boolean result = articlesService.deleteArticleById(id);
        return result ? RestBean.success("删除成功") : RestBean.failure(400, "删除失败");
    }
}