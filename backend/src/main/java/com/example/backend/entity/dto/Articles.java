package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.entity.BaseData;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("articles")
@AllArgsConstructor
public class Articles implements BaseData {
    @TableId(type = IdType.AUTO)
    // 文章ID，对应数据库表中的 id 字段
    private Integer id;
    // 文章标题，对应数据库表中的 title 字段
    private String title;
    // 文章配图链接，对应数据库表中的 image 字段
    private String image;
    // 文章链接，对应数据库表中的 link 字段
    private String link;
    // 文章内容，对应数据库表中的 content 字段
    private String content;
    // 最近七日点赞数，对应数据库表中的 recent_seven_days_likes 字段
    private Integer recentSevenDaysLikes;
    // 文章评论，对应数据库表中的 comments 字段
    private String comments;
}
