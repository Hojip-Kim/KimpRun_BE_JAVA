package kimp.cmc.service.impl;

import kimp.cmc.dao.CmcEntityPreloaderDao;
import kimp.cmc.service.CmcEntityPreloaderService;
import kimp.common.lock.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CMC Entity Preloader Service Implementation
 * 
 * 초기 설정 시 발생하는 N+1 쿼리 문제를 해결하기 위해 CMC 엔티티들을 
 * 영속성 컨텍스트에 미리 로딩하는 서비스 구현체.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CmcEntityPreloaderServiceImpl implements CmcEntityPreloaderService {

    private final CmcEntityPreloaderDao cmcEntityPreloaderDao;

    @Autowired(required = false)
    private DistributedLockService distributedLockService;
    
    // 분산락 키
    private static final String CMC_PRELOAD_LOCK_KEY = "cmc-entity-preload";
    private static final int PRELOAD_LOCK_TTL_SECONDS = 300; // 5분

    /**
     * 모든 CMC 엔티티를 영속성 컨텍스트에 미리 로딩
     * 초기 설정(@PostConstruct) 전에 호출하여 N+1 쿼리 방지
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void preloadAllCmcEntities() {
        // 분산락이 없는 경우 일반 실행
        if (distributedLockService == null) {
            executePreloadingInternal();
            return;
        }
        
        // 분산 환경에서는 하나의 서버만 프리로딩 실행
        preloadAllCmcEntitiesWithLock();
    }
    
    /**
     * 분산락을 사용한 CMC 엔티티 프리로딩
     * 분산 환경에서 여러 서버 중 하나만 프리로딩을 수행하도록 보장
     */
    private void preloadAllCmcEntitiesWithLock() {
        String lockToken = distributedLockService.tryLock(CMC_PRELOAD_LOCK_KEY, PRELOAD_LOCK_TTL_SECONDS);
        
        if (lockToken == null) {
            log.info("CMC 엔티티 프리로딩 건너뜀 - 다른 서버에서 처리 중");
            return;
        }
        
        try {
            executePreloadingInternal();
        } catch (Exception e) {
            log.error("CMC 엔티티 프리로딩 중 오류 발생", e);
        } finally {
            distributedLockService.releaseLock(CMC_PRELOAD_LOCK_KEY, lockToken);
        }
    }
    
    /**
     * 실제 프리로딩 실행 로직 - 트랜잭션 내에서 실행
     * N+1 쿼리 방지를 위한 벌크 로딩
     */
    private void executePreloadingInternal() {
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("1. CmcExchange 엔티티 사전 로딩 시작");
            // Exchange-CmcExchange 관계의 N+1 쿼리 방지를 위해 우선 로딩
            cmcEntityPreloaderDao.findAllCmcExchangesWithAssociations();
            log.debug("CmcExchange 사전 로딩 완료");
            
            log.debug("2. CmcCoin 엔티티 사전 로딩 시작");
            // CmcCoin과 모든 OneToOne 관계 엔티티들을 fetch join으로 일괄 로딩
            cmcEntityPreloaderDao.findAllCmcCoinsWithAssociations();
            log.debug("CmcCoin 사전 로딩 완료");
            
            log.debug("3. CmcCollection 사전 로딩 시작");
            // OneToMany 컬렉션들도 미리 로딩 (필요시)
            preloadCmcCollectionsInternal();
            log.debug("CmcCollection 사전 로딩 완료");
            
            long endTime = System.currentTimeMillis();
            log.info("CMC 엔티티 사전 로딩 완료 - 소요시간: {}ms", (endTime - startTime));
            
        } catch (Exception e) {
            log.error("CMC 엔티티 사전 로딩 중 오류 발생", e);
            throw e; // 오류 재발생으로 초기화 중단
        }
    }

    /**
     * CMC OneToMany 컬렉션들을 미리 로딩 - 내부 메서드
     */
    private void preloadCmcCollectionsInternal() {
        // CmcMainnet 컬렉션이 있는 코인만 로딩 (성능 최적화)
        cmcEntityPreloaderDao.findCmcCoinsWithMainnet();
        
        // CmcPlatform 컬렉션이 있는 코인만 로딩 (성능 최적화)     
        cmcEntityPreloaderDao.findCmcCoinsWithPlatforms();
    }

    /**
     * 특정 coin ID들에 대한 CMC 관계만 선택적으로 사전 로딩
     * @param coinIds 사전 로딩할 coin ID 목록
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void preloadCmcEntitiesForCoins(List<Long> coinIds) {
        if (coinIds == null || coinIds.isEmpty()) {
            log.debug("사전 로딩할 코인 ID 목록이 비어있음");
            return;
        }
        
        log.debug("특정 코인들의 CMC 엔티티 사전 로딩 시작: {} 개", coinIds.size());
        
        cmcEntityPreloaderDao.findCmcCoinsWithAssociationsByIds(coinIds);
                
        log.debug("특정 코인들의 CMC 엔티티 사전 로딩 완료");
    }

    /**
     * 특정 exchange ID들에 대한 CMC 관계만 선택적으로 사전 로딩  
     * @param exchangeIds 사전 로딩할 exchange ID 목록
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void preloadCmcEntitiesForExchanges(List<Long> exchangeIds) {
        if (exchangeIds == null || exchangeIds.isEmpty()) {
            log.debug("사전 로딩할 거래소 ID 목록이 비어있음");
            return;
        }
        
        log.debug("특정 거래소들의 CMC 엔티티 사전 로딩 시작: {} 개", exchangeIds.size());
        
        cmcEntityPreloaderDao.findCmcExchangesWithAssociationsByIds(exchangeIds);
                
        log.debug("특정 거래소들의 CMC 엔티티 사전 로딩 완료");
    }
}