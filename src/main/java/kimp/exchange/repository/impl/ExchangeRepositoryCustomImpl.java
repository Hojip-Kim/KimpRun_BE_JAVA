package kimp.exchange.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.exchange.entity.Exchange;
import kimp.exchange.entity.QExchange;
import kimp.exchange.repository.ExchangeRepositoryCustom;
import kimp.market.entity.QCoin;
import kimp.market.entity.QCoinExchange;
import kimp.market.Enum.MarketType;
import kimp.cmc.entity.exchange.QCmcExchange;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ExchangeRepositoryCustomImpl implements ExchangeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ExchangeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exchange> findExchangesWithCoinExchangesAndCoinByIds(Long coinId, List<Long> exchangesIds) {

        QExchange exchange = QExchange.exchange;
        QCoinExchange coinExchange = QCoinExchange.coinExchange;
        QCoin coin = QCoin.coin;

        return queryFactory
                .selectFrom(exchange)
                .join(exchange.coinExchanges, coinExchange).fetchJoin()
                .where(exchange.id.in(exchangesIds)
                        .and(coin.id.eq(coinId)))
                .fetch();
    }

    @Override
    @Transactional(readOnly = true)
    public Exchange findExchangeByMarketTypeWithCmcExchange(MarketType marketType) {
        QExchange exchange = QExchange.exchange;
        QCmcExchange cmcExchange = QCmcExchange.cmcExchange;

        return queryFactory
                .selectFrom(exchange)
                .leftJoin(exchange.cmcExchange, cmcExchange).fetchJoin()
                .where(exchange.market.eq(marketType))
                .fetchOne();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exchange> findByMarketTypesWithCmcExchange(List<MarketType> marketTypes) {
        QExchange exchange = QExchange.exchange;
        QCmcExchange cmcExchange = QCmcExchange.cmcExchange;

        return queryFactory
                .selectFrom(exchange)
                .leftJoin(exchange.cmcExchange, cmcExchange).fetchJoin()
                .where(exchange.market.in(marketTypes))
                .fetch();
    }
}
