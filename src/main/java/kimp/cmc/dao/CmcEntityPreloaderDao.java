package kimp.cmc.dao;

import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.exchange.CmcExchange;

import java.util.List;

/**
 *
 * N+1 쿼리 방지를 위한 CMC 엔티티 프리로딩 데이터 접근 인터페이스.
 */
public interface CmcEntityPreloaderDao {

    /**
     * CmcCoin 엔티티와 OneToOne 관계들을 fetch join으로 일괄 로딩
     * @return 모든 CmcCoin과 연관 엔티티 리스트
     */
    List<CmcCoin> findAllCmcCoinsWithAssociations();

    /**
     * CmcExchange 엔티티와 OneToOne 관계들을 fetch join으로 일괄 로딩
     * @return 모든 CmcExchange와 연관 엔티티 리스트
     */
    List<CmcExchange> findAllCmcExchangesWithAssociations();

    /**
     * CmcMainnet 컬렉션이 있는 CmcCoin들만 로딩 (성능 최적화)
     * @return CmcMainnet 컬렉션을 가진 CmcCoin 리스트
     */
    List<CmcCoin> findCmcCoinsWithMainnet();

    /**
     * CmcPlatform 컬렉션이 있는 CmcCoin들만 로딩 (성능 최적화)
     * @return CmcPlatform 컬렉션을 가진 CmcCoin 리스트
     */
    List<CmcCoin> findCmcCoinsWithPlatforms();

    /**
     * 특정 coin ID들에 대한 CMC 관계만 선택적으로 사전 로딩
     * @param coinIds 사전 로딩할 coin ID 목록
     * @return 지정된 코인들의 CmcCoin과 연관 엔티티 리스트
     */
    List<CmcCoin> findCmcCoinsWithAssociationsByIds(List<Long> coinIds);

    /**
     * 특정 exchange ID들에 대한 CMC 관계만 선택적으로 사전 로딩
     * @param exchangeIds 사전 로딩할 exchange ID 목록
     * @return 지정된 거래소들의 CmcExchange와 연관 엔티티 리스트
     */
    List<CmcExchange> findCmcExchangesWithAssociationsByIds(List<Long> exchangeIds);
}