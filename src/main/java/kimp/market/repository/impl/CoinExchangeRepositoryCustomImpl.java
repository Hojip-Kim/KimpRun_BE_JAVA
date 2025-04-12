package kimp.market.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.exchange.entity.QExchange;
import kimp.market.entity.CoinExchange;
import kimp.market.entity.QCoinExchange;
import kimp.market.repository.CoinExchangeRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CoinExchangeRepositoryCustomImpl implements CoinExchangeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CoinExchangeRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    QCoinExchange coinExchange = QCoinExchange.coinExchange;
    QExchange exchange = QExchange.exchange;

    @Override
    @Transactional
    public List<CoinExchange> findCoinExchangeWithExchangeByCoinAndExchangeIn(Long coinId, List<Long> exchangeIds) {

        return queryFactory
                .select(coinExchange)
                .join(coinExchange.exchange, exchange).fetchJoin()
                .where(
                        coinExchange.coin.id.eq(coinId)
                                .and(coinExchange.exchange.id.in(exchangeIds))
                )
                .fetch();
    }


    @Override
    @Transactional
    public List<CoinExchange> findCoinExchangeWithExchangeByCoinId(Long coinId){

        return queryFactory
                .select(coinExchange)
                .join(coinExchange.exchange, exchange).fetchJoin()
                .where(
                        coinExchange.coin.id.eq(coinId)
                )
                .fetch();
    }
}
