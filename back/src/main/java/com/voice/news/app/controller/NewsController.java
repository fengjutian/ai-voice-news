package com.voice.news.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voice.news.app.common.R;
import com.voice.news.app.entity.News;
import com.voice.news.app.exception.ErrorCode;
import com.voice.news.app.service.NewsService;

/**
 * 新闻相关的RESTful API控制器
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {
    
    @Autowired
    private NewsService newsService;
    
    /**
     * 获取最新新闻列表
     * @param limit 查询数量限制，默认10条
     * @return 新闻列表
     */
    @GetMapping("/latest")
    public R<List<News>> getLatestNews(@RequestParam(defaultValue = "10") int limit) {
        List<News> newsList = newsService.getLatestNews(limit);
        return R.ok(newsList);
    }
    
    /**
     * 根据标签查询新闻
     * @param tag 标签名称
     * @return 匹配的新闻列表
     */
    @GetMapping("/by-tag")
    public R<List<News>> getNewsByTag(@RequestParam String tag) {
        List<News> newsList = newsService.getNewsByTag(tag);
        return R.ok(newsList);
    }
    
    /**
     * 根据标题关键词搜索新闻
     * @param keyword 关键词
     * @return 匹配的新闻列表
     */
    @GetMapping("/search")
    public R<List<News>> searchNewsByTitle(@RequestParam String keyword) {
        List<News> newsList = newsService.searchNewsByTitle(keyword);
        return R.ok(newsList);
    }
    
    /**
     * 根据时间范围查询新闻
     * @param startDate 开始日期（格式：yyyy-MM-dd HH:mm:ss）
     * @param endDate 结束日期（格式：yyyy-MM-dd HH:mm:ss）
     * @return 时间范围内的新闻列表
     */
    @GetMapping("/by-date-range")
    public R<List<News>> getNewsByDateRange(
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        
        List<News> newsList = newsService.getNewsByPublishTimeRange(start, end);
        return R.ok(newsList);
    }
    
    /**
     * 根据来源查询新闻
     * @param source 新闻来源
     * @return 来自指定来源的新闻列表
     */
    @GetMapping("/by-source")
    public R<List<News>> getNewsBySource(@RequestParam String source) {
        List<News> newsList = newsService.getNewsBySource(source);
        return R.ok(newsList);
    }
    
    /**
     * 根据ID获取单个新闻详情
     * @param id 新闻ID
     * @return 新闻对象
     */
    @GetMapping("/{id}")
    public R<News> getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id)
                .map(R::ok)
                .orElseGet(() -> R.build(ErrorCode.NOT_FOUND));
    }
    
    /**
     * 创建新闻
     * @param news 新闻对象
     * @return 创建的新闻对象
     */
    @PostMapping
    public R<News> createNews(@RequestBody News news) {
        News savedNews = newsService.saveNews(news);
        return R.ok(savedNews);
    }
    
    /**
     * 更新新闻
     * @param id 新闻ID
     * @param news 新闻对象
     * @return 更新后的新闻对象
     */
    @PutMapping("/{id}")
    public R<?> updateNews(@PathVariable Long id, @RequestBody News news) {
        if (!id.equals(news.getId())) {
            return R.build(ErrorCode.PARAM_ERROR);
        }
        
        try {
            News updatedNews = newsService.updateNews(news);
            return R.ok(updatedNews);
        } catch (RuntimeException e) {
            return R.error(ErrorCode.NOT_FOUND.code, e.getMessage());
        }
    }
    
    /**
     * 删除单个新闻
     * @param id 新闻ID
     * @return 响应状态
     */
    @DeleteMapping("/{id}")
    public R<?> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return R.ok();
        } catch (RuntimeException e) {
            return R.error(ErrorCode.NOT_FOUND.code, e.getMessage());
        }
    }
    
    /**
     * 批量删除新闻
     * @param ids 新闻ID列表
     * @return 响应状态
     */
    @DeleteMapping("/batch")
    public R<?> deleteNewsBatch(@RequestBody List<Long> ids) {
        try {
            newsService.deleteNewsBatch(ids);
            return R.ok();
        } catch (RuntimeException e) {
            return R.error(ErrorCode.NOT_FOUND.code, e.getMessage());
        }
    }
}
