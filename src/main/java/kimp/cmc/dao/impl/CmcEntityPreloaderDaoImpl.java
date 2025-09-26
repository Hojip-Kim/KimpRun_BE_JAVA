package kimp.cmc.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kimp.cmc.dao.CmcEntityPreloaderDao;
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.exchange.CmcExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kimp.cmc.entity.coin.QCmcCoin.cmcCoin;
import static kimp.cmc.entity.exchange.QCmcExchange.cmcExchange;

/**
 *
 * N+1 쿼리 방지를 위한 CMC 엔티티 프리로딩 데이터 접근 구현체.
 */
@Slf4j
@Repository
public class CmcEntityPreloaderDaoImpl implements CmcEntityPreloaderDao {

    @PersistenceContext
    private EntityManager entityManager;
    
    private JPAQueryFactory queryFactory;
    
    public CmcEntityPreloaderDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * CmcCoin 엔티티와 OneToOne 관계들을 fetch join으로 일괄 로딩
     * @return 모든 CmcCoin과 연관 엔티티 리스트
     */
    @Override
    public List<CmcCoin> findAllCmcCoinsWithAssociations() {
        List<CmcCoin> cmcCoins = queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.coin).fetchJoin()
            .leftJoin(cmcCoin.cmcRank).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo.cmcCoinMeta).fetchJoin()
            .fetch();
                
        log.debug("CmcCoin with associations 조회 완료: {} 개", cmcCoins.size());
        return cmcCoins;
    }

    /**
     * CmcExchange 엔티티와 OneToOne 관계들을 fetch join으로 일괄 로딩
     * @return 모든 CmcExchange와 연관 엔티티 리스트
     */
    @Override
    public List<CmcExchange> findAllCmcExchangesWithAssociations() {
        List<CmcExchange> cmcExchanges = queryFactory
            .selectFrom(cmcExchange)
            .distinct()
            .leftJoin(cmcExchange.exchange).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeInfo).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeMeta).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeUrl).fetchJoin()
            .fetch();
                
        log.debug("CmcExchange with associations 조회 완료: {} 개", cmcExchanges.size());
        return cmcExchanges;
    }

    /**
     * CmcMainnet 컬렉션이 있는 CmcCoin들만 로딩 (성능 최적화)
     * @return CmcMainnet 컬렉션을 가진 CmcCoin 리스트
     */
    @Override
    public List<CmcCoin> findCmcCoinsWithMainnet() {
        List<CmcCoin> cmcCoins = queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.cmcMainnet).fetchJoin()
            .where(cmcCoin.cmcMainnet.isNotEmpty())
            .fetch();
            
        log.debug("CmcCoin with Mainnet 조회 완료: {} 개", cmcCoins.size());
        return cmcCoins;
    }

    /**
     * CmcPlatform 컬렉션이 있는 CmcCoin들만 로딩 (성능 최적화)
     * @return CmcPlatform 컬렉션을 가진 CmcCoin 리스트
     */
    @Override
    public List<CmcCoin> findCmcCoinsWithPlatforms() {
        List<CmcCoin> cmcCoins = queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.cmcPlatforms).fetchJoin()
            .where(cmcCoin.cmcPlatforms.isNotEmpty())
            .fetch();
            
        log.debug("CmcCoin with Platforms 조회 완료: {} 개", cmcCoins.size());
        return cmcCoins;
    }

    /**
     * 특정 coin ID들에 대한 CMC 관계만 선택적으로 사전 로딩
     * @param coinIds 사전 로딩할 coin ID 목록
     * @return 지정된 코인들의 CmcCoin과 연관 엔티티 리스트
     */
    @Override
    public List<CmcCoin> findCmcCoinsWithAssociationsByIds(List<Long> coinIds) {
        if (coinIds == null || coinIds.isEmpty()) {
            log.debug("CoinIds가 비어있음 - 빈 리스트 반환");
            return List.of();
        }
        
        List<CmcCoin> cmcCoins = queryFactory
            .selectFrom(cmcCoin)
            .distinct()
            .leftJoin(cmcCoin.coin).fetchJoin()
            .leftJoin(cmcCoin.cmcRank).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo).fetchJoin()
            .leftJoin(cmcCoin.cmcCoinInfo.cmcCoinMeta).fetchJoin()
            .where(cmcCoin.coin.id.in(coinIds))
            .fetch();
                
        log.debug("특정 코인들의 CmcCoin with associations 조회 완료: {} 개 (요청: {} 개)", 
            cmcCoins.size(), coinIds.size());
        return cmcCoins;
    }

    /**
     * 특정 exchange ID들에 대한 CMC 관계만 선택적으로 사전 로딩
     * @param exchangeIds 사전 로딩할 exchange ID 목록
     * @return 지정된 거래소들의 CmcExchange와 연관 엔티티 리스트
     */
    @Override
    public List<CmcExchange> findCmcExchangesWithAssociationsByIds(List<Long> exchangeIds) {
        if (exchangeIds == null || exchangeIds.isEmpty()) {
            log.debug("ExchangeIds가 비어있음 - 빈 리스트 반환");
            return List.of();
        }
        
        List<CmcExchange> cmcExchanges = queryFactory
            .selectFrom(cmcExchange)
            .distinct()
            .leftJoin(cmcExchange.exchange).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeInfo).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeMeta).fetchJoin()
            .leftJoin(cmcExchange.cmcExchangeUrl).fetchJoin()
            .where(cmcExchange.exchange.id.in(exchangeIds))
            .fetch();
                
        log.debug("특정 거래소들의 CmcExchange with associations 조회 완료: {} 개 (요청: {} 개)", 
            cmcExchanges.size(), exchangeIds.size());
        return cmcExchanges;
    }
}