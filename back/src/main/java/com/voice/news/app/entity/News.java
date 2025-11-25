package com.voice.news.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 新闻实体类
 */
@Data
@Entity
@Table(name = "news")
public class News {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID'")
    private Long id;
    
    /**
     * 新闻标题
     */
    @Column(name = "title", nullable = false, length = 500, columnDefinition = "VARCHAR(500) NOT NULL COMMENT '新闻标题'")
    private String title;
    
    /**
     * 新闻摘要
     */
    @Column(name = "summary", columnDefinition = "TEXT COMMENT '新闻摘要'")
    private String summary;
    
    /**
     * 新闻正文
     */
    @Column(name = "content", columnDefinition = "LONGTEXT COMMENT '新闻正文'")
    private String content;
    
    /**
     * 新闻标签，逗号分隔，例如 "科技,AI"
     */
    @Column(name = "tags", length = 200, columnDefinition = "VARCHAR(200) COMMENT '新闻标签，逗号分隔，例如 \"科技,AI\"'")
    private String tags;
    
    /**
     * 新闻来源
     */
    @Column(name = "source", length = 200, columnDefinition = "VARCHAR(200) COMMENT '新闻来源'")
    private String source;
    
    /**
     * 新闻原文链接
     */
    @Column(name = "url", length = 500, columnDefinition = "VARCHAR(500) COMMENT '新闻原文链接'")
    private String url;
    
    /**
     * 新闻实际发布时间
     */
    @Column(name = "published_at", columnDefinition = "DATETIME COMMENT '新闻实际发布时间'")
    private LocalDateTime publishedAt;
    
    /**
     * 抓取时间
     */
    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '抓取时间'")
    private LocalDateTime createdAt;
}
