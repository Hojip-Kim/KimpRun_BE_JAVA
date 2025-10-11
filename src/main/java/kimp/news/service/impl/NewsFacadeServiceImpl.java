package kimp.news.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.news.dao.NewsInsightDao;
import kimp.news.dao.NewsKeywordDao;
import kimp.news.dao.NewsSummaryDao;
import kimp.news.dto.response.NewsResponseDto;
import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import kimp.news.service.NewsFacadeService;
import kimp.news.service.NewsService;
import kimp.news.vo.GetNewsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsFacadeServiceImpl implements NewsFacadeService {

    private static final String NEWS_CACHE_PREFIX = "news:list:";
    private static final String NEWS_DETAIL_CACHE_PREFIX = "news:detail:";
    private static final long CACHE_EXPIRE_SECONDS = 300; // 5분

    private final NewsService newsService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final NewsKeywordDao newsKeywordDao;
    private final NewsSummaryDao newsSummaryDao;
    private final NewsInsightDao newsInsightDao;

    public NewsFacadeServiceImpl(NewsService newsService,
                                 RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 kimp.news.dao.NewsKeywordDao newsKeywordDao,
                                 kimp.news.dao.NewsSummaryDao newsSummaryDao,
                                 kimp.news.dao.NewsInsightDao newsInsightDao) {
        this.newsService = newsService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.newsKeywordDao = newsKeywordDao;
        this.newsSummaryDao = newsSummaryDao;
        this.newsInsightDao = newsInsightDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseDto> getAllNews(GetNewsVo vo) {
        String cacheKey = NEWS_CACHE_PREFIX + "all:" + vo.getPage() + ":" + vo.getSize();

        // Redis에서 조회 시도
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("뉴스 목록 캐시 적중: {}", cacheKey);
                List<NewsResponseDto> cachedList = objectMapper.convertValue(
                        cachedData,
                        new TypeReference<List<NewsResponseDto>>() {}
                );
                PageRequest pageRequest = PageRequest.of(vo.getPage() - 1, vo.getSize());
                return new PageImpl<>(cachedList, pageRequest, cachedList.size());
            }
        } catch (Exception e) {
            log.warn("Redis 캐시 오류, 데이터베이스 조회로 전환: {}", e.getMessage());
        }

        // DB에서 조회
        PageRequest pageRequest = PageRequest.of(vo.getPage() - 1, vo.getSize());
        Page<News> newsPage = newsService.getAllNews(pageRequest);
        Page<NewsResponseDto> result = newsPage.map(this::convertToDto);

        // Redis에 캐싱 (비동기로 처리하여 응답 지연 최소화)
        try {
            redisTemplate.opsForValue().set(cacheKey, result.getContent(), CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("뉴스 목록 캐싱 실패: {}", e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseDto> getNewsByType(GetNewsVo vo) {
        String cacheKey = NEWS_CACHE_PREFIX + "type:" + vo.getNewsType() + ":" + vo.getPage() + ":" + vo.getSize();
        return getCachedPage(cacheKey, () -> {
            PageRequest pageRequest = PageRequest.of(vo.getPage() - 1, vo.getSize());
            return newsService.getNewsByType(vo.getNewsType(), pageRequest).map(this::convertToDto);
        }, vo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseDto> getHeadlines(GetNewsVo vo) {
        String cacheKey = NEWS_CACHE_PREFIX + "headlines:" + vo.getPage() + ":" + vo.getSize();
        return getCachedPage(cacheKey, () -> {
            PageRequest pageRequest = PageRequest.of(vo.getPage() - 1, vo.getSize());
            return newsService.getHeadlines(pageRequest).map(this::convertToDto);
        }, vo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NewsResponseDto> getNewsById(Long id) {
        String cacheKey = NEWS_DETAIL_CACHE_PREFIX + "id:" + id;

        // Redis에서 조회 시도
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("뉴스 상세 캐시 적중: {}", cacheKey);
                NewsResponseDto cached = objectMapper.convertValue(cachedData, NewsResponseDto.class);
                return Optional.of(cached);
            }
        } catch (Exception e) {
            log.warn("Redis 캐시 오류, 데이터베이스 조회로 전환: {}", e.getMessage());
        }

        // DB에서 조회
        Optional<NewsResponseDto> result = newsService.getNewsById(id).map(this::convertToDto);

        // Redis에 캐싱
        result.ifPresent(dto -> {
            try {
                redisTemplate.opsForValue().set(cacheKey, dto, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("뉴스 상세 캐싱 실패: {}", e.getMessage());
            }
        });

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseDto> getNewsByNewsSource(GetNewsVo vo) {
        String cacheKey = NEWS_CACHE_PREFIX + "source:" + vo.getNewsSource() + ":" + vo.getPage() + ":" + vo.getSize();
        return getCachedPage(cacheKey, () -> {
            PageRequest pageRequest = PageRequest.of(vo.getPage() - 1, vo.getSize());
            return newsService.getNewsByNewsSource(vo.getNewsSource(), pageRequest).map(this::convertToDto);
        }, vo);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NewsResponseDto> getNewsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long seq) {
        String cacheKey = NEWS_DETAIL_CACHE_PREFIX + "source:" + newsSource + ":seq:" + seq;

        // Redis에서 조회 시도
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("뉴스 소스별 조회 캐시 적중: {}", cacheKey);
                NewsResponseDto cached = objectMapper.convertValue(cachedData, NewsResponseDto.class);
                return Optional.of(cached);
            }
        } catch (Exception e) {
            log.warn("Redis 캐시 오류, 데이터베이스 조회로 전환: {}", e.getMessage());
        }

        // DB에서 조회
        Optional<NewsResponseDto> result = newsService.getNewsByNewsSourceAndSourceSequenceId(newsSource, seq)
                .map(this::convertToDto);

        // Redis에 캐싱
        result.ifPresent(dto -> {
            try {
                redisTemplate.opsForValue().set(cacheKey, dto, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("뉴스 소스별 조회 캐싱 실패: {}", e.getMessage());
            }
        });

        return result;
    }

    /**
     * 캐시에서 페이지 조회, 없으면 DB 조회 후 캐싱
     */
    private Page<NewsResponseDto> getCachedPage(String cacheKey,
                                                  java.util.function.Supplier<Page<NewsResponseDto>> dbSupplier,
                                                  GetNewsVo vo) {
        // Redis에서 조회 시도
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                log.debug("캐시 적중: {}", cacheKey);
                @SuppressWarnings("unchecked")
                List<NewsResponseDto> cachedList = objectMapper.convertValue(
                        cachedData,
                        new TypeReference<List<NewsResponseDto>>() {}
                );
                PageRequest pageRequest = PageRequest.of(vo.getPage() - 1, vo.getSize());
                return new PageImpl<>(cachedList, pageRequest, cachedList.size());
            }
        } catch (Exception e) {
            log.warn("Redis 캐시 오류, 데이터베이스 조회로 전환: {}", e.getMessage());
        }

        // DB에서 조회
        Page<NewsResponseDto> result = dbSupplier.get();

        // Redis에 캐싱
        try {
            redisTemplate.opsForValue().set(cacheKey, result.getContent(), CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("캐싱 실패: {}", e.getMessage());
        }

        return result;
    }

    private NewsResponseDto convertToDto(News news) {
        // 별도로 컬렉션 조회 (DAO 사용)
        List<String> keywords = newsKeywordDao.findByNewsId(news.getId()).stream()
                .map(kimp.news.entity.NewsKeyword::getKeyword)
                .collect(Collectors.toList());

        List<String> summaries = newsSummaryDao.findByNewsIdOrderByDisplayOrder(news.getId()).stream()
                .map(kimp.news.entity.NewsSummary::getSummary)
                .collect(Collectors.toList());

        List<String> insights = newsInsightDao.findByNewsIdOrderByDisplayOrder(news.getId()).stream()
                .map(kimp.news.entity.NewsInsight::getInsight)
                .collect(Collectors.toList());

        // shortContent 생성: plainTextContent 또는 첫번째 summary, 최대 200자
        String shortContent = getShortContent(news, summaries);

        return NewsResponseDto.builder()
                .id(news.getId())
                .newsSource(news.getNewsSource().getCode())
                .title(news.getTitle())
                .thumbnail(news.getThumbnail())
                .shortContent(shortContent)
                .sourceUrl(news.getSourceUrl())
                .createEpochMillis(news.getCreateEpochMillis())
                .createdAt(news.getCreatedAt())
                .newsType(news.getNewsType())
                .region(news.getRegion())
                .sentiment(news.getSentiment())
                .isNew(news.getIsNew())
                .isHeadline(news.getIsHeadline())
                .keywords(keywords)
                .summaries(summaries)
                .insights(insights)
                .build();
    }

    private String getShortContent(News news, List<String> summaries) {
        String content = null;

        if (news.getPlainTextContent() != null && !news.getPlainTextContent().isEmpty()) {
            content = news.getPlainTextContent();
        }
        else if (summaries != null && !summaries.isEmpty()) {
            content = summaries.get(0);
        }

        if (content != null) {
            return content.length() > 200 ? content.substring(0, 200) + "..." : content;
        }

        return "";
    }
}
