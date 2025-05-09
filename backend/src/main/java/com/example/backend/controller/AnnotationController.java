package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.Annotation;
import com.example.backend.service.AnnotationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annotation")
public class AnnotationController {
    
    @Resource
    AnnotationService annotationService;
    
    @PostMapping("/save")
    public RestBean<String> saveAnnotation(@RequestBody Annotation annotation) {
        try {
            annotationService.saveAnnotation(annotation);
            return RestBean.success("标注保存成功");
        } catch (Exception e) {
            return RestBean.failure(500, "标注保存失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{imageId}")
    public RestBean<List<Annotation>> getAnnotations(@PathVariable String imageId) {
        try {
            List<Annotation> annotations = annotationService.getAnnotationsByImageId(imageId);
            return RestBean.success(annotations);
        } catch (Exception e) {
            return RestBean.failure(500, "获取标注失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public RestBean<String> deleteAnnotation(@PathVariable Long id) {
        try {
            annotationService.deleteAnnotation(id);
            return RestBean.success("标注删除成功");
        } catch (Exception e) {
            return RestBean.failure(500, "标注删除失败：" + e.getMessage());
        }
    }
} 