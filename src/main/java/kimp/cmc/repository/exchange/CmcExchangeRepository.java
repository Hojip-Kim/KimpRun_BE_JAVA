package kimp.cmc.repository.exchange;

import kimp.cmc.entity.exchange.CmcExchange;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CmcExchangeRepository extends JpaRepository<CmcExchange, Long> {
    
    /**
     * spotVolumeUsd 기준으로 정렬된 Exchange 페이지 조회 (fetch join으로 N+1 방지)
     */
    @Query("SELECT DISTINCT e FROM CmcExchange e " +
           "LEFT JOIN FETCH e.cmcExchangeInfo ei " +
           "LEFT JOIN FETCH e.cmcExchangeMeta em " +
           "LEFT JOIN FETCH e.cmcExchangeUrl eu " +
           "WHERE em.spotVolumeUsd IS NOT NULL " +
           "ORDER BY em.spotVolumeUsd DESC")
    Page<CmcExchange> findAllOrderBySpotVolumeWithFetchJoin(org.springframework.data.domain.Pageable pageable);
}
