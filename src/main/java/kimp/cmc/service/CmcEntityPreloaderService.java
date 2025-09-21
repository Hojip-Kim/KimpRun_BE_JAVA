package kimp.cmc.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.exchange.CmcExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kimp.cmc.entity.coin.QCmcCoin.cmcCoin;
import static kimp.cmc.entity.exchange.QCmcExchange.cmcExchange;

/**
 * CMC Entity Preloader Service
 * 
 * 초기 설정 시 발생하는 N+1 쿼리 문제를 해결하기 위해 CMC 엔티티들을 
 * 영속성 컨텍스트에 미리 로딩하는 서비스입니다.
 */
@Slf4j
@Service
public class CmcEntityPreloaderService {

    @PersistenceContext
    private EntityManager entityManager;
    
    private JPAQueryFactory queryFactory;
    
    public CmcEntityPreloaderService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 모든 CMC 엔티티를 영속성 컨텍스트에 미리 로딩
     * 초기 설정(@PostConstruct) 전에 호출하여 N+1 쿼리 방지
     */
    @Transactional(readOnly = true)
    public void preloadAllCmcEntities() {
        log.info("CMC 엔티티 사전 로딩 시작 - N+1 쿼리 방지");
        
        long startTime = System.currentTimeMillis();
        
        // 1. CmcCoin과 모든 OneToOne 관계 엔티티들을 fetch join으로 일괄 로딩
        preloadCmcCoins();
        
        // 2. CmcExchange와 모든 OneToOne 관계 엔티티들을 fetch join으로 일괄 로딩  
        preloadCmcExchanges();
        
        // 3. OneToMany 컬렉션들도 미리 로딩 (필요시)
        preloadCmcCollections();
        
        long endTime = System.currentTimeMillis();
        log.info("CMC 엔티티 사전 로딩 완료 - 소요시간: {}ms", (endTime - startTime));
    }

    /**
     * CmcCoin 엔티티와 OneToOne 관계들을 fetch join으로 일괄 로딩
     */
    private void preloadCmcCoins() {
        List<CmcCoin> cmcCoins = queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.coin).fetchJoin()
            .leftJoin(cmcCoin.cmcRank).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo.cmcCoinMeta).fetchJoin()
            .fetch();
                
        log.debug("CmcCoin 사전 로딩 완료: {} 개", cmcCoins.size());
    }

    /**
     * CmcExchange 엔티티와 OneToOne 관계들을 fetch join으로 일괄 로딩
     */
    private void preloadCmcExchanges() {
        List<CmcExchange> cmcExchanges = queryFactory
            .selectFrom(cmcExchange)
            .distinct()
            .leftJoin(cmcExchange.exchange).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeInfo).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeMeta).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeUrl).fetchJoin()
            .fetch();
                
        log.debug("CmcExchange 사전 로딩 완료: {} 개", cmcExchanges.size());
    }

    /**
     * CMC OneToMany 컬렉션들을 미리 로딩
     */
    private void preloadCmcCollections() {
        log.debug("CMC 컬렉션 사전 로딩 시작 - 성능 최적화 버전");
        
        // CmcMainnet 컬렉션이 있는 코인만 로딩 (성능 최적화)
        queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.cmcMainnet).fetchJoin()
            .where(cmcCoin.cmcMainnet.isNotEmpty())
            .fetch();
        
        // CmcPlatform 컬렉션이 있는 코인만 로딩 (성능 최적화)     
        queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.cmcPlatforms).fetchJoin()
            .where(cmcCoin.cmcPlatforms.isNotEmpty())
            .fetch();
                
        log.debug("CMC 컬렉션 사전 로딩 완료");
    }

    /**
     * 특정 coin ID들에 대한 CMC 관계만 선택적으로 사전 로딩
     * @param coinIds 사전 로딩할 coin ID 목록
     */
    @Transactional(readOnly = true)
    public void preloadCmcEntitiesForCoins(List<Long> coinIds) {
        if (coinIds == null || coinIds.isEmpty()) {
            return;
        }
        
        log.debug("특정 코인들의 CMC 엔티티 사전 로딩 시작: {} 개", coinIds.size());
        
        queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.coin).fetchJoin()
            .leftJoin(cmcCoin.cmcRank).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo.cmcCoinMeta).fetchJoin()
            .where(cmcCoin.coin.id.in(coinIds))
            .fetch();
                
        log.debug("특정 코인들의 CMC 엔티티 사전 로딩 완료");
    }

    /**
     * 특정 exchange ID들에 대한 CMC 관계만 선택적으로 사전 로딩  
     * @param exchangeIds 사전 로딩할 exchange ID 목록
     */
    @Transactional(readOnly = true)
    public void preloadCmcEntitiesForExchanges(List<Long> exchangeIds) {
        if (exchangeIds == null || exchangeIds.isEmpty()) {
            return;
        }
        
        log.debug("특정 거래소들의 CMC 엔티티 사전 로딩 시작: {} 개", exchangeIds.size());
        
        queryFactory
            .selectFrom(cmcExchange)
            .distinct()
            .leftJoin(cmcExchange.exchange).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeInfo).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeMeta).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeUrl).fetchJoin()
            .where(cmcExchange.exchange.id.in(exchangeIds))
            .fetch();
                
        log.debug("특정 거래소들의 CMC 엔티티 사전 로딩 완료");
    }
}