package kimp.news.repository;

import kimp.news.entity.News;
import kimp.news.enums.NewsSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * News Repository의 커스텀 쿼리 인터페이스
 * QueryDSL을 사용한 복잡한 쿼리를 위한 인터페이스
 */
public interface NewsRepositoryCustom {

    /**
     * 모든 뉴스 조회 (페이징, 생성시간 내림차순)
     */
    Page<News> findAllOrderByCreateEpochDesc(Pageable pageable);

    /**
     * 뉴스 소스별 조회 (페이징, 생성시간 내림차순)
     */
    Page<News> findByNewsSourceOrderByCreateEpochDesc(NewsSource newsSource, Pageable pageable);

    /**
     * 뉴스 타입별 조회 (페이징, 생성시간 내림차순)
     */
    Page<News> findByNewsTypeOrderByCreateEpochDesc(String newsType, Pageable pageable);

    /**
     * 헤드라인 뉴스 조회 (페이징, 생성시간 내림차순)
     */
    Page<News> findHeadlinesOrderByCreateEpochDesc(Pageable pageable);

    /**
     * 뉴스 소스별 최근 시퀀스 ID 목록 조회
     */
    List<Long> findSourceSequenceIdsByNewsSource(NewsSource newsSource, Pageable pageable);

    /**
     * 여러 sourceSequenceId에 대한 기존 뉴스를 한번에 조회
     * @param newsSource 뉴스 소스
     * @param sourceSequenceIds 조회할 시퀀스 ID 목록
     * @return 기존에 존재하는 뉴스 목록
     */
    List<News> findByNewsSourceAndSourceSequenceIdIn(NewsSource newsSource, List<Long> sourceSequenceIds);

    /**
     * 여러 sourceSequenceId 중 이미 존재하는 ID만 반환
     * @param newsSource 뉴스 소스
     * @param sourceSequenceIds 확인할 시퀀스 ID 목록
     * @return 이미 존재하는 시퀀스 ID 목록
     */
    List<Long> findExistingSourceSequenceIds(NewsSource newsSource, List<Long> sourceSequenceIds);
}
