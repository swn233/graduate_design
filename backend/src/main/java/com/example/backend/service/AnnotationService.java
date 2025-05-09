package com.example.backend.service;

import com.example.backend.entity.dto.Annotation;
import java.util.List;

public interface AnnotationService {
    void saveAnnotation(Annotation annotation);
    List<Annotation> getAnnotationsByImageId(String imageId);
    void deleteAnnotation(Long id);
} 