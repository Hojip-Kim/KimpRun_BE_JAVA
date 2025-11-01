package kimp.news.service;

import kimp.news.dto.internal.bloomingbit.BloomingBitNewsDto;
import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NewsService {

    News createNews(BloomingBitNewsDto bloomingBitNewsDto);

    News updateNews(News existingNews, BloomingBitNewsDto bloomingBitNewsDto);

    Optional<News> getNewsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId);

    Optional<News> getNewsById(Long id);

    Page<News> getAllNews(Pageable pageable);

    Page<News> getNewsByNewsSource(NewsSource newsSource, Pageable pageable);

    Page<News> getNewsByType(String newsType, Pageable pageable);

    Page<News> getHeadlines(Pageable pageable);

    boolean existsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId);

    List<Long> getRecentSourceSequenceIdsByNewsSource(NewsSource newsSource, int limit);

    void saveAll(List<News> newsList);

    /**
     * 여러 sourceSequenceId에 대한 기존 뉴스를 한번에 조회
     */
    List<News> findByNewsSourceAndSourceSequenceIdIn(NewsSource newsSource, List<Long> sourceSequenceIds);
}
