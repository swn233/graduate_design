package com.example.backend.entity.vo.request;
import lombok.Data;

@Data
public class ArticlesWriteVO {
    // 文章标题，对应数据库表中的 title 字段
    private String title;
    // 文章配图链接，对应数据库表中的 image 字段
    private String image;
    // 文章内容，对应数据库表中的 content 字段
    private String content;
    // 文章评论，对应数据库表中的 comments 字段
    private String author;
}