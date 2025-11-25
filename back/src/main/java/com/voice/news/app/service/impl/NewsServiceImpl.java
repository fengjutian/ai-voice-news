package com.voice.news.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.voice.news.app.entity.News;
import com.voice.news.app.repository.NewsRepository;
import com.voice.news.app.service.NewsService;

/**
 * NewsService接口的实现类，提供新闻相关的业务逻辑操作
 */
@Service
public class NewsServiceImpl implements NewsService {
    
    @Autowired
    private NewsRepository newsRepository;
    
    @Override
    public List<News> getLatestNews(int limit) {
        // 创建Pageable对象，使用PageRequest.ofSize指定返回数量
        Pageable pageable = PageRequest.ofSize(limit);
        return newsRepository.findByOrderByPublishedAtDesc(pageable);
    }
    
    @Override
    public List<News> getNewsByTag(String tag) {
        return newsRepository.findByTag(tag);
    }
    
    @Override
    public List<News> searchNewsByTitle(String keyword) {
        return newsRepository.findByTitleContaining(keyword);
    }
    
    @Override
    public List<News> getNewsByPublishTimeRange(LocalDateTime start, LocalDateTime end) {
        return newsRepository.findByPublishedAtBetween(start, end);
    }
    
    @Override
    public List<News> getNewsBySource(String source) {
        return newsRepository.findBySource(source);
    }
    
    @Override
    public Optional<News> getNewsById(Long id) {
        return newsRepository.findById(id);
    }
    
    @Override
    public News saveNews(News news) {
        // 设置创建时间
        if (news.getCreatedAt() == null) {
            news.setCreatedAt(LocalDateTime.now());
        }
        return newsRepository.save(news);
    }
    
    @Override
    public News updateNews(News news) {
        // 检查新闻是否存在
        Optional<News> existingNews = newsRepository.findById(news.getId());
        if (existingNews.isPresent()) {
            // 设置更新时间
            news.setCreatedAt(existingNews.get().getCreatedAt()); // 保留原创建时间
            return newsRepository.save(news);
        }
        throw new RuntimeException("News not found with id: " + news.getId());
    }
    
    @Override
    public void deleteNews(Long id) {
        // 检查新闻是否存在
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("News not found with id: " + id);
        }
        newsRepository.deleteById(id);
    }
    
    @Override
    public void deleteNewsBatch(List<Long> ids) {
        // 检查所有ID是否存在
        for (Long id : ids) {
            if (!newsRepository.existsById(id)) {
                throw new RuntimeException("News not found with id: " + id);
            }
        }
        newsRepository.deleteAllById(ids);
    }
}
