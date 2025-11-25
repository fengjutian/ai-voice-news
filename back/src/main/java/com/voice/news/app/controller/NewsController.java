package com.voice.news.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voice.news.app.entity.News;
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
    public ResponseEntity<List<News>> getLatestNews(@RequestParam(defaultValue = "10") int limit) {
        List<News> newsList = newsService.getLatestNews(limit);
        return new ResponseEntity<>(newsList, HttpStatus.OK);
    }
    
    /**
     * 根据标签查询新闻
     * @param tag 标签名称
     * @return 匹配的新闻列表
     */
    @GetMapping("/by-tag")
    public ResponseEntity<List<News>> getNewsByTag(@RequestParam String tag) {
        List<News> newsList = newsService.getNewsByTag(tag);
        return new ResponseEntity<>(newsList, HttpStatus.OK);
    }
    
    /**
     * 根据标题关键词搜索新闻
     * @param keyword 关键词
     * @return 匹配的新闻列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<News>> searchNewsByTitle(@RequestParam String keyword) {
        List<News> newsList = newsService.searchNewsByTitle(keyword);
        return new ResponseEntity<>(newsList, HttpStatus.OK);
    }
    
    /**
     * 根据时间范围查询新闻
     * @param startDate 开始日期（格式：yyyy-MM-dd HH:mm:ss）
     * @param endDate 结束日期（格式：yyyy-MM-dd HH:mm:ss）
     * @return 时间范围内的新闻列表
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<News>> getNewsByDateRange(
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        
        List<News> newsList = newsService.getNewsByPublishTimeRange(start, end);
        return new ResponseEntity<>(newsList, HttpStatus.OK);
    }
    
    /**
     * 根据来源查询新闻
     * @param source 新闻来源
     * @return 来自指定来源的新闻列表
     */
    @GetMapping("/by-source")
    public ResponseEntity<List<News>> getNewsBySource(@RequestParam String source) {
        List<News> newsList = newsService.getNewsBySource(source);
        return new ResponseEntity<>(newsList, HttpStatus.OK);
    }
    
    /**
     * 根据ID获取单个新闻详情
     * @param id 新闻ID
     * @return 新闻对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id)
                .map(news -> new ResponseEntity<>(news, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * 创建新闻
     * @param news 新闻对象
     * @return 创建的新闻对象
     */
    @PostMapping
    public ResponseEntity<News> createNews(@RequestBody News news) {
        News savedNews = newsService.saveNews(news);
        return new ResponseEntity<>(savedNews, HttpStatus.CREATED);
    }
    
    /**
     * 更新新闻
     * @param id 新闻ID
     * @param news 新闻对象
     * @return 更新后的新闻对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNews(@PathVariable Long id, @RequestBody News news) {
        if (!id.equals(news.getId())) {
            return new ResponseEntity<>("News ID in path and body must match", HttpStatus.BAD_REQUEST);
        }
        
        try {
            News updatedNews = newsService.updateNews(news);
            return new ResponseEntity<>(updatedNews, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 删除单个新闻
     * @param id 新闻ID
     * @return 响应状态
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 批量删除新闻
     * @param ids 新闻ID列表
     * @return 响应状态
     */
    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteNewsBatch(@RequestBody List<Long> ids) {
        try {
            newsService.deleteNewsBatch(ids);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
