package com.voice.news.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.voice.news.app.entity.News;

/**
 * News服务接口，提供新闻相关的业务逻辑操作
 */
public interface NewsService {
    
    /**
     * 根据发布时间倒序查询最新新闻列表
     * @param limit 查询数量限制
     * @return 新闻列表
     */
    List<News> getLatestNews(int limit);
    
    /**
     * 根据标签查询新闻
     * @param tag 标签名称
     * @return 匹配的新闻列表
     */
    List<News> getNewsByTag(String tag);
    
    /**
     * 根据标题模糊查询新闻
     * @param keyword 关键词
     * @return 匹配的新闻列表
     */
    List<News> searchNewsByTitle(String keyword);
    
    /**
     * 查询指定时间范围内发布的新闻
     * @param start 开始时间
     * @param end 结束时间
     * @return 时间范围内的新闻列表
     */
    List<News> getNewsByPublishTimeRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据来源查询新闻
     * @param source 新闻来源
     * @return 来自指定来源的新闻列表
     */
    List<News> getNewsBySource(String source);
    
    /**
     * 根据ID查询单个新闻详情
     * @param id 新闻ID
     * @return 新闻对象（可选）
     */
    Optional<News> getNewsById(Long id);
    
    /**
     * 保存新闻
     * @param news 新闻对象
     * @return 保存后的新闻对象
     */
    News saveNews(News news);
    
    /**
     * 更新新闻
     * @param news 新闻对象
     * @return 更新后的新闻对象
     */
    News updateNews(News news);
    
    /**
     * 删除新闻
     * @param id 新闻ID
     */
    void deleteNews(Long id);
    
    /**
     * 批量删除新闻
     * @param ids 新闻ID列表
     */
    void deleteNewsBatch(List<Long> ids);
}
