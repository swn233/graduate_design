package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.dto.Annotation;
import org.apache.ibatis.annotations.Mapper;
 
@Mapper
public interface AnnotationMapper extends BaseMapper<Annotation> {
} 