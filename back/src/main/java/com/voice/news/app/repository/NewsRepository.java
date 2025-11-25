package com.voice.news.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voice.news.app.entity.News;

/**
 * News实体的Repository接口
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    /**
     * 根据发布时间倒序查询新闻列表，使用Pageable控制返回数量
     * @param pageable 分页参数，使用PageRequest.ofSize(limit)创建
     * @return 新闻列表
     */
    List<News> findByOrderByPublishedAtDesc(Pageable pageable);
    
    /**
     * 根据标签查询新闻
     * @param tag 标签名称
     * @return 匹配的新闻列表
     */
    @Query("SELECT n FROM News n WHERE n.tags LIKE %:tag%")
    List<News> findByTag(@Param("tag") String tag);
    
    /**
     * 根据标题模糊查询新闻
     * @param keyword 关键词
     * @return 匹配的新闻列表
     */
    List<News> findByTitleContaining(String keyword);
    
    /**
     * 查询指定时间范围内发布的新闻
     * @param start 开始时间
     * @param end 结束时间
     * @return 时间范围内的新闻列表
     */
    List<News> findByPublishedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据来源查询新闻
     * @param source 新闻来源
     * @return 来自指定来源的新闻列表
     */
    List<News> findBySource(String source);
    
    /**
     * 根据ID查询单个新闻详情
     * @param id 新闻ID
     * @return 新闻对象（可选）
     */
    @Override
    Optional<News> findById(Long id);
}
