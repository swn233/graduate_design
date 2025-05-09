package com.example.backend.controller;

import com.example.backend.entity.dto.Articles;
import com.example.backend.entity.vo.request.ArticlesWriteVO;
import com.example.backend.service.ArticlesService;
import com.example.backend.entity.RestBean;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @Resource
    ArticlesService articlesService;

    // 图片上传目录
    private static final String UPLOAD_DIR = "uploads/images/";

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

    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestBean<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = Paths.get(UPLOAD_DIR + newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 返回可访问的URL
            String imageUrl = "http://localhost:8080/images/" + newFilename;
            return RestBean.success(imageUrl);
        } catch (IOException e) {
            return RestBean.failure(500, "图片上传失败：" + e.getMessage());
        }
    }
}