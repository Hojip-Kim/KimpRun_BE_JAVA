package kimp.notice.repository;

import kimp.notice.entity.Notice;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    public Page<Notice> findByExchangeIdOrderByDateDesc(long exchangeId, Pageable pageable);

    @Query("SELECT n FROM Notice n JOIN FETCH n.exchange ORDER BY n.date DESC")
    public Page<Notice> findByOrderByDateDesc(Pageable pageable);

    @Query("SELECT n FROM Notice n JOIN FETCH n.exchange WHERE n.link = :link")
    public Notice findNoticeByLink(String link);

    /**
     * 특정 거래소의 최신 공지사항 링크들을 가져옴 (등록일 기준 내림차순)
     */
    @Query("SELECT n.link FROM Notice n WHERE n.exchange.market = :marketType ORDER BY n.date DESC")
    public List<String> findRecentNoticeLinksByMarketType(@Param("marketType") MarketType marketType, Pageable pageable);

    /**
     * 특정 거래소의 가장 최근 공지사항 날짜를 가져옴
     */
    @Query("SELECT MAX(n.date) FROM Notice n WHERE n.exchange.market = :marketType")
    public LocalDateTime findLatestNoticeDateByMarketType(@Param("marketType") MarketType marketType);

    /**
     * 특정 거래소의 지정된 날짜 이후 공지사항들의 링크를 가져옴 (날짜 기준 오름차순)
     */
    @Query("SELECT n.link FROM Notice n WHERE n.exchange.market = :marketType AND n.date > :afterDate ORDER BY n.date ASC")
    public List<String> findNoticeLinksAfterDate(@Param("marketType") MarketType marketType, @Param("afterDate") LocalDateTime afterDate);

    /**
     * 특정 거래소의 지정된 날짜 이후 공지사항들을 가져옴 (날짜 기준 오름차순)
     * Redis 캐시 초기화 시 URL과 날짜를 함께 가져오기 위해 사용
     */
    @Query("SELECT n FROM Notice n JOIN FETCH n.exchange WHERE n.exchange.market = :marketType AND n.date > :afterDate ORDER BY n.date ASC")
    public List<Notice> findNoticesAfterDate(@Param("marketType") MarketType marketType, @Param("afterDate") LocalDateTime afterDate);

    /**
     * 특정 거래소의 모든 공지사항을 가져옴 (새로운 로직용)
     */
    @Query("SELECT n FROM Notice n WHERE n.exchange.market = :marketType ORDER BY n.date DESC")
    public List<Notice> findAllNoticesByMarketType(@Param("marketType") MarketType marketType);

    /**
     * N+1 문제 해결: 여러 링크에 대한 기존 공지사항 존재 여부를 한번에 확인
     */
    @Query("SELECT n.link FROM Notice n WHERE n.link IN :links")
    public List<String> findExistingNoticeLinks(@Param("links") List<String> links);
}
