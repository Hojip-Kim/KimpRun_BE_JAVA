package kimp.market.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.cmc.entity.coin.QCmcCoin;
import kimp.cmc.entity.exchange.QCmcExchange;
import kimp.exchange.entity.QExchange;
import kimp.market.Enum.MarketType;
import kimp.market.entity.Coin;
import kimp.market.entity.QCoin;
import kimp.market.entity.QCoinExchange;
import kimp.market.repository.CoinRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CoinRepositoryCustomImpl implements CoinRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CoinRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    QCoin coin = QCoin.coin;
    QCoinExchange coinExchange = QCoinExchange.coinExchange;
    QExchange exchange = QExchange.exchange;
    QCmcCoin cmcCoin = QCmcCoin.cmcCoin;
    QCmcExchange cmcExchange = QCmcExchange.cmcExchange;

    @Override
    @Transactional
    public Optional<Coin> findByIdWithExchanges(Long id) {
        Coin result = queryFactory
                .selectFrom(coin)
                .leftJoin(coin.coinExchanges, coinExchange).fetchJoin()
                .leftJoin(coinExchange.exchange, exchange).fetchJoin()
                .leftJoin(exchange.cmcExchange, cmcExchange).fetchJoin()
                .leftJoin(coin.cmcCoin, cmcCoin).fetchJoin()
                .where(coin.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public List<MarketType> findMarketTypesByCoinId(Long coinId) {
        return queryFactory
                .select(exchange.market)
                .from(coin)
                .join(coin.coinExchanges, coinExchange)
                .join(coinExchange.exchange, exchange)
                .where(coin.id.eq(coinId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteByIdWithExchangeIds(Long coinId, List<Long> exchangeIds) {
            queryFactory
                .delete(coinExchange)
                .where(
                       coinExchange.coin.id.eq(coinId),
                       coinExchange.exchange.id.in(exchangeIds)
                )
                .execute();
    }

    @Override
    public List<Coin> findCoinsByExchange(Long exchangeId) {
        return queryFactory
                .selectFrom(coin)
                .distinct()
                .join(coin.coinExchanges, coinExchange).fetchJoin()
                .join(coinExchange.exchange, exchange).fetchJoin()
                .leftJoin(coin.cmcCoin, cmcCoin).fetchJoin()
                .where(exchange.id.eq(exchangeId))
                .fetch();
    }

    @Override
    public List<Coin> findCoinWithExchangesBySymbols(List<String> symbols) {

        return queryFactory
                .selectFrom(coin)
                .distinct()
                .leftJoin(coin.coinExchanges, coinExchange).fetchJoin()
                .leftJoin(coinExchange.exchange, exchange).fetchJoin()
                .leftJoin(exchange.cmcExchange, cmcExchange).fetchJoin()
                .leftJoin(coin.cmcCoin, cmcCoin).fetchJoin()
                .where(coin.symbol.in(symbols))
                .fetch();
    }
}
