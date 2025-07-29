package kimp.notice.repository;

import kimp.notice.entity.Notice;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    public Page<Notice> findByExchangeIdOrderByDateDesc(long exchangeId, Pageable pageable);

    public Page<Notice> findByOrderByDateDesc(Pageable pageable);

    @Query("SELECT n FROM Notice n JOIN FETCH n.exchange WHERE n.link = :link")
    public Notice findNoticeByLink(String link);

    /**
     * 특정 거래소의 최신 공지사항 링크들을 가져옴 (등록일 기준 내림차순)
     */
    @Query("SELECT n.link FROM Notice n WHERE n.exchange.market = :marketType ORDER BY n.registedAt DESC")
    public List<String> findRecentNoticeLinksByMarketType(@Param("marketType") MarketType marketType, Pageable pageable);
}
