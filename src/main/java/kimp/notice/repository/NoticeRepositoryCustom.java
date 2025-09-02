package kimp.notice.repository;

import kimp.notice.entity.Notice;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeRepositoryCustom {
    
    /**
     * N+1 문제 해결을 위한 QueryDSL 기반 조회
     * Exchange와 CmcExchange를 JOIN FETCH로 함께 조회
     */
    List<Notice> findAllNoticesByMarketTypeWithFetch(MarketType marketType);
    
    /**
     * 모든 공지사항을 페이징으로 조회 (N+1 문제 해결)
     */
    Page<Notice> findAllByOrderByDateDescWithFetch(Pageable pageable);
}