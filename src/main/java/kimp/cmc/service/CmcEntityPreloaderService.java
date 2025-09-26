package kimp.cmc.service;

import java.util.List;

/**
 * CMC Entity Preloader Service Interface
 * 
 * 초기 설정 시 발생하는 N+1 쿼리 문제를 해결하기 위해 CMC 엔티티들을 
 * 영속성 컨텍스트에 미리 로딩하는 서비스 인터페이스입니다.
 */
public interface CmcEntityPreloaderService {

    /**
     * 모든 CMC 엔티티를 영속성 컨텍스트에 미리 로딩
     * 초기 설정(@PostConstruct) 전에 호출하여 N+1 쿼리 방지
     */
    void preloadAllCmcEntities();

    /**
     * 특정 coin ID들에 대한 CMC 관계만 선택적으로 사전 로딩
     * @param coinIds 사전 로딩할 coin ID 목록
     */
    void preloadCmcEntitiesForCoins(List<Long> coinIds);

    /**
     * 특정 exchange ID들에 대한 CMC 관계만 선택적으로 사전 로딩  
     * @param exchangeIds 사전 로딩할 exchange ID 목록
     */
    void preloadCmcEntitiesForExchanges(List<Long> exchangeIds);
}