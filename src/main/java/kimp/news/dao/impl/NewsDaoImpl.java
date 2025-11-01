package kimp.news.dao.impl;

import kimp.news.dao.NewsDao;
import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import kimp.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * News DAO 구현체
 * Repository 레이어에 위임하여 비즈니스 로직과 데이터 접근 분리
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class NewsDaoImpl implements NewsDao {

    private final NewsRepository newsRepository;

    @Override
    public Page<News> findAllOrderByCreateEpochDesc(Pageable pageable) {
        return newsRepository.findAllOrderByCreateEpochDesc(pageable);
    }

    @Override
    public Page<News> findByNewsSourceOrderByCreateEpochDesc(NewsSource newsSource, Pageable pageable) {
        return newsRepository.findByNewsSourceOrderByCreateEpochDesc(newsSource, pageable);
    }

    @Override
    public Page<News> findByNewsTypeOrderByCreateEpochDesc(String newsType, Pageable pageable) {
        return newsRepository.findByNewsTypeOrderByCreateEpochDesc(newsType, pageable);
    }

    @Override
    public Page<News> findHeadlinesOrderByCreateEpochDesc(Pageable pageable) {
        return newsRepository.findHeadlinesOrderByCreateEpochDesc(pageable);
    }

    @Override
    public Optional<News> findById(Long id) {
        return newsRepository.findById(id);
    }

    @Override
    public Optional<News> findByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId) {
        return newsRepository.findByNewsSourceAndSourceSequenceId(newsSource, sourceSequenceId);
    }

    @Override
    public List<Long> findSourceSequenceIdsByNewsSource(NewsSource newsSource, Pageable pageable) {
        return newsRepository.findSourceSequenceIdsByNewsSource(newsSource, pageable);
    }

    @Override
    public boolean existsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId) {
        return newsRepository.existsByNewsSourceAndSourceSequenceId(newsSource, sourceSequenceId);
    }

    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    public List<News> saveAll(List<News> newsList) {
        return newsRepository.saveAll(newsList);
    }

    @Override
    public List<News> findByNewsSourceAndSourceSequenceIdIn(NewsSource newsSource, List<Long> sourceSequenceIds) {
        return newsRepository.findByNewsSourceAndSourceSequenceIdIn(newsSource, sourceSequenceIds);
    }

    @Override
    public List<Long> findExistingSourceSequenceIds(NewsSource newsSource, List<Long> sourceSequenceIds) {
        return newsRepository.findExistingSourceSequenceIds(newsSource, sourceSequenceIds);
    }
}
