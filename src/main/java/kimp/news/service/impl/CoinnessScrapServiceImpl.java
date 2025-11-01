package kimp.news.service.impl;

import kimp.common.lock.DistributedLockService;
import kimp.common.redis.constant.RedisKeyType;
import kimp.news.component.impl.CoinnessComponent;
import kimp.news.dto.internal.coinness.CoinnessArticleDto;
import kimp.news.dto.internal.coinness.CoinnessBreakingNewsDto;
import kimp.news.entity.News;
import kimp.news.dao.NewsDao;
import kimp.news.enums.NewsSource;
import kimp.news.service.CoinnessScrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CoinnessScrapServiceImpl implements CoinnessScrapService {

    private static final String BREAKING_NEWS_LOCK_KEY = "news-scrape:coinness:breaking";
    private static final String ARTICLES_LOCK_KEY = "news-scrape:coinness:articles";
    private static final int LOCK_TIMEOUT = 300;

    private final CoinnessComponent coinnessComponent;
    private final CoinnessBreakingNewsSourceServiceImpl breakingNewsSourceService;
    private final CoinnessArticleNewsSourceServiceImpl articleNewsSourceService;
    private final NewsDao newsDao;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DistributedLockService distributedLockService;

    public CoinnessScrapServiceImpl(CoinnessComponent coinnessComponent,
                                    @Qualifier("coinnessBreakingNewsSourceService") CoinnessBreakingNewsSourceServiceImpl breakingNewsSourceService,
                                    @Qualifier("coinnessArticleNewsSourceService") CoinnessArticleNewsSourceServiceImpl articleNewsSourceService,
                                    NewsDao newsDao,
                                    RedisTemplate<String, Object> redisTemplate,
                                    DistributedLockService distributedLockService) {
        this.coinnessComponent = coinnessComponent;
        this.breakingNewsSourceService = breakingNewsSourceService;
        this.articleNewsSourceService = articleNewsSourceService;
        this.newsDao = newsDao;
        this.redisTemplate = redisTemplate;
        this.distributedLockService = distributedLockService;
    }

    @Override
    public void scrapCoinnessBreakingNews() {
        String lockToken = distributedLockService.tryLock(BREAKING_NEWS_LOCK_KEY, LOCK_TIMEOUT);

        if (lockToken == null) {
            String lockOwner = distributedLockService.getLockOwner(BREAKING_NEWS_LOCK_KEY);
            log.info("코인니스 속보 스크래핑이 이미 진행 중입니다 - 작업자: {}", lockOwner);
            return;
        }

        try {
            log.info("코인니스 속보 스크래핑 시작");

            List<CoinnessBreakingNewsDto> newsList = coinnessComponent.fetchBreakingNews();

            if (newsList == null || newsList.isEmpty()) {
                log.info("코인니스 API에서 가져온 속보 없음");
                return;
            }

            int insertCount = 0;
            int updateCount = 0;
            List<News> processedNewsList = new ArrayList<>();

            // 1. 모든 ID를 한번에 조회
            List<Long> allIds = newsList.stream()
                    .map(CoinnessBreakingNewsDto::getId)
                    .toList();

            Map<Long, News> existingNewsMap = newsDao.findByNewsSourceAndSourceSequenceIdIn(
                    NewsSource.COINNESS,
                    allIds
            ).stream().collect(java.util.stream.Collectors.toMap(News::getSourceSequenceId, n -> n));

            // 2. 메모리에서 구분하여 처리
            for (CoinnessBreakingNewsDto newsDto : newsList) {
                try {
                    News savedNews;
                    News existingNews = existingNewsMap.get(newsDto.getId());

                    if (existingNews != null) {
                        breakingNewsSourceService.updateNewsFromSource(existingNews, newsDto);
                        savedNews = existingNews;
                        updateCount++;
                        log.debug("속보 업데이트 완료: id={}, title={}", newsDto.getId(), newsDto.getTitle());
                    } else {
                        News news = breakingNewsSourceService.createNewsFromSource(newsDto);
                        savedNews = newsDao.save(news);
                        breakingNewsSourceService.saveNewsCollections(savedNews, newsDto);
                        insertCount++;

                        redisTemplate.opsForSet().add(RedisKeyType.NEWS_COINNESS_BREAKING_IDS.getKey(), String.valueOf(newsDto.getId()));
                        log.debug("속보 저장 완료: id={}, title={}", newsDto.getId(), newsDto.getTitle());
                    }

                    processedNewsList.add(savedNews);
                } catch (Exception e) {
                    log.error("속보 처리 중 오류 발생: id={}", newsDto.getId(), e);
                }
            }

            log.info("속보 처리 완료 - 신규: {}건, 업데이트: {}건, 총: {}건",
                    insertCount, updateCount, processedNewsList.size());

        } catch (Exception e) {
            log.error("코인니스 속보 스크래핑 중 오류 발생", e);
        } finally {
            distributedLockService.releaseLock(BREAKING_NEWS_LOCK_KEY, lockToken);
            log.info("코인니스 속보 스크래핑 완료");
        }
    }

    @Override
    public void scrapCoinnessArticles() {
        String lockToken = distributedLockService.tryLock(ARTICLES_LOCK_KEY, LOCK_TIMEOUT);

        if (lockToken == null) {
            String lockOwner = distributedLockService.getLockOwner(ARTICLES_LOCK_KEY);
            log.info("코인니스 기사 스크래핑이 이미 진행 중입니다 - 작업자: {}", lockOwner);
            return;
        }

        try {
            log.info("코인니스 기사 스크래핑 시작");

            List<CoinnessArticleDto> articles = coinnessComponent.fetchArticles();

            if (articles == null || articles.isEmpty()) {
                log.info("코인니스 API에서 가져온 기사 없음");
                return;
            }

            int insertCount = 0;
            int updateCount = 0;
            List<News> processedArticlesList = new ArrayList<>();

            // 1. 모든 ID를 한번에 조회
            List<Long> allIds = articles.stream()
                    .map(CoinnessArticleDto::getId)
                    .toList();

            Map<Long, News> existingNewsMap = newsDao.findByNewsSourceAndSourceSequenceIdIn(
                    NewsSource.COINNESS,
                    allIds
            ).stream().collect(java.util.stream.Collectors.toMap(News::getSourceSequenceId, n -> n));

            // 2. 메모리에서 구분하여 처리
            for (CoinnessArticleDto articleDto : articles) {
                try {
                    News savedNews;
                    News existingNews = existingNewsMap.get(articleDto.getId());

                    if (existingNews != null) {
                        articleNewsSourceService.updateNewsFromSource(existingNews, articleDto);
                        savedNews = existingNews;  // 영속 엔티티 그대로 사용
                        updateCount++;
                        log.debug("기사 업데이트 완료: id={}, title={}", articleDto.getId(), articleDto.getTitle());
                    } else {
                        News news = articleNewsSourceService.createNewsFromSource(articleDto);
                        savedNews = newsDao.save(news);
                        articleNewsSourceService.saveNewsCollections(savedNews, articleDto);
                        insertCount++;

                        redisTemplate.opsForSet().add(RedisKeyType.NEWS_COINNESS_ARTICLES_IDS.getKey(), String.valueOf(articleDto.getId()));
                        log.debug("기사 저장 완료: id={}, title={}", articleDto.getId(), articleDto.getTitle());
                    }

                    processedArticlesList.add(savedNews);
                } catch (Exception e) {
                    log.error("기사 처리 중 오류 발생: id={}", articleDto.getId(), e);
                }
            }

            log.info("기사 처리 완료 - 신규: {}건, 업데이트: {}건, 총: {}건",
                    insertCount, updateCount, processedArticlesList.size());

        } catch (Exception e) {
            log.error("코인니스 기사 스크래핑 중 오류 발생", e);
        } finally {
            distributedLockService.releaseLock(ARTICLES_LOCK_KEY, lockToken);
            log.info("코인니스 기사 스크래핑 완료");
        }
    }
}
