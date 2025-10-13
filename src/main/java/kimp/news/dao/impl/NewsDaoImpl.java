package kimp.news.dao.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kimp.news.dao.NewsDao;
import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kimp.news.entity.QNews.news;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NewsDaoImpl implements NewsDao {

    private final JPAQueryFactory queryFactory;
    private final kimp.news.repository.NewsRepository newsRepository;

    @Override
    public Page<News> findAllOrderByCreateEpochDesc(Pageable pageable) {
        // News만 조회 (컬렉션은 @BatchSize로 처리)
        List<News> content = queryFactory
                .selectFrom(news)
                .distinct()
                .orderBy(news.createEpochMillis.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(news.count())
                .from(news)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<News> findByNewsSourceOrderByCreateEpochDesc(NewsSource newsSource, Pageable pageable) {
        List<News> content = queryFactory
                .selectFrom(news)
                .distinct()
                .where(news.newsSource.eq(newsSource))
                .orderBy(news.createEpochMillis.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(news.count())
                .from(news)
                .where(news.newsSource.eq(newsSource))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<News> findByNewsTypeOrderByCreateEpochDesc(String newsType, Pageable pageable) {
        List<News> content = queryFactory
                .selectFrom(news)
                .distinct()
                .where(news.newsType.eq(newsType))
                .orderBy(news.createEpochMillis.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(news.count())
                .from(news)
                .where(news.newsType.eq(newsType))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<News> findHeadlinesOrderByCreateEpochDesc(Pageable pageable) {
        List<News> content = queryFactory
                .selectFrom(news)
                .distinct()
                .where(news.isHeadline.eq(true))
                .orderBy(news.createEpochMillis.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(news.count())
                .from(news)
                .where(news.isHeadline.eq(true))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Optional<News> findById(Long id) {
        News result = queryFactory
                .selectFrom(news)
                .where(news.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<News> findByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId) {
        News result = queryFactory
                .selectFrom(news)
                .where(news.newsSource.eq(newsSource)
                        .and(news.sourceSequenceId.eq(sourceSequenceId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Long> findSourceSequenceIdsByNewsSource(NewsSource newsSource, Pageable pageable) {
        return queryFactory
                .select(news.sourceSequenceId)
                .from(news)
                .where(news.newsSource.eq(newsSource))
                .orderBy(news.createEpochMillis.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public boolean existsByNewsSourceAndSourceSequenceId(NewsSource newsSource, Long sourceSequenceId) {
        Integer count = queryFactory
                .selectOne()
                .from(news)
                .where(news.newsSource.eq(newsSource)
                        .and(news.sourceSequenceId.eq(sourceSequenceId)))
                .fetchFirst();

        return count != null;
    }

    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    public List<News> saveAll(List<News> newsList) {
        return newsRepository.saveAll(newsList);
    }
}
