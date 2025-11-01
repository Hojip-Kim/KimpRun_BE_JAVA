package kimp.news.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static kimp.news.entity.QNews.news;

/**
 * NewsRepositoryCustom 구현체
 * QueryDSL을 사용한 복잡한 동적 쿼리 구현
 */
@RequiredArgsConstructor
public class NewsRepositoryCustomImpl implements NewsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<News> findAllOrderByCreateEpochDesc(Pageable pageable) {
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
    public List<News> findByNewsSourceAndSourceSequenceIdIn(NewsSource newsSource, List<Long> sourceSequenceIds) {
        if (sourceSequenceIds == null || sourceSequenceIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .selectFrom(news)
                .where(
                        news.newsSource.eq(newsSource),
                        news.sourceSequenceId.in(sourceSequenceIds)
                )
                .fetch();
    }

    @Override
    public List<Long> findExistingSourceSequenceIds(NewsSource newsSource, List<Long> sourceSequenceIds) {
        if (sourceSequenceIds == null || sourceSequenceIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(news.sourceSequenceId)
                .from(news)
                .where(
                        news.newsSource.eq(newsSource),
                        news.sourceSequenceId.in(sourceSequenceIds)
                )
                .fetch();
    }
}
