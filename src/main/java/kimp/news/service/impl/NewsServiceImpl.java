package kimp.news.service.impl;

import kimp.news.dao.NewsDao;
import kimp.news.dto.internal.bloomingbit.BloomingBitNewsDto;
import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import kimp.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NewsServiceImpl implements NewsService {

    private final NewsDao newsDao;
    private final BloomingBitNewsSourceServiceImpl bloomingBitNewsSourceService;

    public NewsServiceImpl(NewsDao newsDao,
                           BloomingBitNewsSourceServiceImpl bloomingBitNewsSourceService) {
        this.newsDao = newsDao;
        this.bloomingBitNewsSourceService = bloomingBitNewsSourceService;
    }

    @Override
    @Transactional
    public News createNews(BloomingBitNewsDto bloomingBitNewsDto) {
        News news = bloomingBitNewsSourceService.createNewsFromSource(bloomingBitNewsDto);
        News savedNews = newsDao.save(news);
        bloomingBitNewsSourceService.saveNewsCollections(savedNews, bloomingBitNewsDto);
        return savedNews;
    }

    @Override
    @Transactional
    public News updateNews(News existingNews, BloomingBitNewsDto bloomingBitNewsDto) {
        News updatedNews = bloomingBitNewsSourceService.updateNewsFromSource(existingNews, bloomingBitNewsDto);
        News savedNews = newsDao.save(updatedNews);
        bloomingBitNewsSourceService.updateNewsCollections(savedNews, bloomingBitNewsDto);
        return savedNews;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<News> getNewsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId) {
        return newsDao.findByNewsSourceAndSourceSequenceId(newsSource, sourceSequenceId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<News> getNewsById(Long id) {
        return newsDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getAllNews(Pageable pageable) {
        return newsDao.findAllOrderByCreateEpochDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getNewsByNewsSource(NewsSource newsSource, Pageable pageable) {
        return newsDao.findByNewsSourceOrderByCreateEpochDesc(newsSource, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getNewsByType(String newsType, Pageable pageable) {
        return newsDao.findByNewsTypeOrderByCreateEpochDesc(newsType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<News> getHeadlines(Pageable pageable) {
        return newsDao.findHeadlinesOrderByCreateEpochDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId) {
        return newsDao.existsByNewsSourceAndSourceSequenceId(newsSource, sourceSequenceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getRecentSourceSequenceIdsByNewsSource(NewsSource newsSource, int limit) {
        return newsDao.findSourceSequenceIdsByNewsSource(newsSource, PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public void saveAll(List<News> newsList) {
        newsDao.saveAll(newsList);
    }
}
