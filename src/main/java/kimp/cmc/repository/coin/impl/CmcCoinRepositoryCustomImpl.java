package kimp.cmc.repository.coin.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.cmc.entity.coin.CmcCoin;
import kimp.cmc.entity.coin.CmcMainnet;
import kimp.cmc.entity.coin.CmcPlatform;
import kimp.cmc.entity.coin.QCmcCoin;
import kimp.cmc.entity.coin.QCmcCoinInfo;
import kimp.cmc.entity.coin.QCmcCoinMeta;
import kimp.cmc.entity.coin.QCmcMainnet;
import kimp.cmc.entity.coin.QCmcPlatform;
import kimp.cmc.entity.coin.QCmcRank;
import kimp.market.entity.QCoin;
import kimp.cmc.repository.coin.CmcCoinRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CmcCoinRepositoryCustomImpl implements CmcCoinRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CmcCoinRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // QueryDSL Q클래스 인스턴스
    QCmcCoin cmcCoin = QCmcCoin.cmcCoin;
    QCmcRank cmcRank = QCmcRank.cmcRank;
    QCmcCoinInfo cmcCoinInfo = QCmcCoinInfo.cmcCoinInfo;
    QCmcCoinMeta cmcCoinMeta = QCmcCoinMeta.cmcCoinMeta;
    QCmcMainnet cmcMainnet = QCmcMainnet.cmcMainnet;
    QCmcPlatform cmcPlatform = QCmcPlatform.cmcPlatform;
    QCoin coin = QCoin.coin;

    @Override
    @Transactional(readOnly = true)
    public Optional<CmcCoin> findByIdWithOneToOneRelations(Long coinId) {
        // CmcCoin과 OneToOne 관계의 모든 엔티티를 fetch join으로 가져옴 (1번째 쿼리)
        // coinId는 Coin 테이블의 ID이므로 cmcCoin.coin.id로 조회
        CmcCoin result = queryFactory
                .selectFrom(cmcCoin)
                .leftJoin(cmcCoin.coin, coin).fetchJoin()
                .leftJoin(cmcCoin.cmcRank, cmcRank).fetchJoin()
                .leftJoin(cmcCoin.cmcCoinInfo, cmcCoinInfo).fetchJoin()
                .leftJoin(cmcCoinInfo.cmcCoinMeta, cmcCoinMeta).fetchJoin()
                .where(cmcCoin.coin.id.eq(coinId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CmcMainnet> findMainnetsByCmcCoinId(Long cmcCoinId) {
        // cmcCoinId 기준으로 CmcMainnet 목록 조회
        return queryFactory
                .selectFrom(cmcMainnet)
                .where(cmcMainnet.cmcCoin.cmcCoinId.eq(cmcCoinId))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CmcPlatform> findPlatformsByCmcCoinId(Long cmcCoinId) {
        // cmcCoinId 기준으로 CmcPlatform 목록 조회
        return queryFactory
                .selectFrom(cmcPlatform)
                .where(cmcPlatform.cmcCoin.cmcCoinId.eq(cmcCoinId))
                .fetch();
    }
}