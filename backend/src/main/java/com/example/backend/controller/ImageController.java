package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    // 图片上传目录
    private static final String UPLOAD_DIR = "uploads/images/";

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @DeleteMapping("/{filename}")
    public RestBean<String> deleteImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return RestBean.success("图片删除成功");
            }
            return RestBean.failure(404, "图片不存在");
        } catch (IOException e) {
            return RestBean.failure(500, "图片删除失败：" + e.getMessage());
        }
    }
} 