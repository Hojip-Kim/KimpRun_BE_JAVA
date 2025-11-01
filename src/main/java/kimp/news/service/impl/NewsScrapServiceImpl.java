package kimp.news.service.impl;

import kimp.common.lock.DistributedLockService;
import kimp.common.redis.constant.RedisKeyType;
import kimp.news.component.impl.BloomingBitComponent;
import kimp.news.dto.internal.bloomingbit.BloomingBitNewsDto;
import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import kimp.news.service.NewsScrapService;
import kimp.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NewsScrapServiceImpl implements NewsScrapService {

    private static final String LOCK_KEY = "news-scrape:bloomingbit";
    private static final int LOCK_TIMEOUT = 300; // 5 minutes

    private final BloomingBitComponent bloomingBitComponent;
    private final NewsService newsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DistributedLockService distributedLockService;

    public NewsScrapServiceImpl(BloomingBitComponent bloomingBitComponent,
                                NewsService newsService,
                                RedisTemplate<String, Object> redisTemplate,
                                DistributedLockService distributedLockService) {
        this.bloomingBitComponent = bloomingBitComponent;
        this.newsService = newsService;
        this.redisTemplate = redisTemplate;
        this.distributedLockService = distributedLockService;
    }

    @Override
    public void scrapBloomingBitNews() {
        String lockToken = distributedLockService.tryLock(LOCK_KEY, LOCK_TIMEOUT);

        if (lockToken == null) {
            String lockOwner = distributedLockService.getLockOwner(LOCK_KEY);
            log.info("블루밍비트 뉴스 스크래핑이 이미 진행 중입니다 - 작업자: {}", lockOwner);
            return;
        }

        try {
            log.info("블루밍비트 뉴스 스크래핑 시작");

            // Fetch news from BloomingBit API
            List<BloomingBitNewsDto> newsList = bloomingBitComponent.fetchNews();

            if (newsList == null || newsList.isEmpty()) {
                log.info("블루밍비트 API에서 가져온 뉴스 없음");
                return;
            }

            // Process all news with bulk upsert logic (N+1 문제 해결)
            int insertCount = 0;
            int updateCount = 0;
            List<News> processedNewsList = new ArrayList<>();

            // 1. 모든 ID를 한번에 조회
            List<Long> allIds = newsList.stream()
                    .map(BloomingBitNewsDto::getSeq)
                    .toList();

            Map<Long, News> existingNewsMap = newsService.findByNewsSourceAndSourceSequenceIdIn(
                    NewsSource.BLOOMING_BIT,
                    allIds
            ).stream().collect(java.util.stream.Collectors.toMap(News::getSourceSequenceId, n -> n));

            // 2. 메모리에서 구분하여 처리
            for (BloomingBitNewsDto newsDto : newsList) {
                try {
                    News savedNews;
                    News existingNews = existingNewsMap.get(newsDto.getSeq());

                    if (existingNews != null) {
                        // Update existing news
                        savedNews = newsService.updateNews(existingNews, newsDto);
                        updateCount++;
                        log.debug("뉴스 업데이트 완료: seq={}, title={}", newsDto.getSeq(), newsDto.getTitle());
                    } else {
                        // Insert new news
                        savedNews = newsService.createNews(newsDto);
                        insertCount++;

                        // Update Redis cache for new news
                        redisTemplate.opsForSet().add(RedisKeyType.NEWS_BLOOMINGBIT_SEQS.getKey(), String.valueOf(newsDto.getSeq()));
                        log.debug("뉴스 저장 완료: seq={}, title={}", newsDto.getSeq(), newsDto.getTitle());
                    }

                    processedNewsList.add(savedNews);
                } catch (Exception e) {
                    log.error("뉴스 처리 중 오류 발생: seq={}", newsDto.getSeq(), e);
                }
            }

            log.info("뉴스 처리 완료 - 신규: {}건, 업데이트: {}건, 총: {}건",
                    insertCount, updateCount, processedNewsList.size());

        } catch (Exception e) {
            log.error("블루밍비트 뉴스 스크래핑 중 오류 발생", e);
        } finally {
            distributedLockService.releaseLock(LOCK_KEY, lockToken);
            log.info("블루밍비트 뉴스 스크래핑 완료");
        }
    }
}
