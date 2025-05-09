package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Annotation;
import com.example.backend.mapper.AnnotationMapper;
import com.example.backend.service.AnnotationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnotationServiceImpl extends ServiceImpl<AnnotationMapper, Annotation> implements AnnotationService {
    
    @Resource
    AnnotationMapper annotationMapper;
    
    @Override
    public void saveAnnotation(Annotation annotation) {
        save(annotation);
    }
    
    @Override
    public List<Annotation> getAnnotationsByImageId(String imageId) {
        LambdaQueryWrapper<Annotation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Annotation::getImageId, imageId);
        return list(wrapper);
    }
    
    @Override
    public void deleteAnnotation(Long id) {
        removeById(id);
    }
} 