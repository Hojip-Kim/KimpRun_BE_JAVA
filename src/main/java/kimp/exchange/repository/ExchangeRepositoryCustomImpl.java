package kimp.exchange.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kimp.exchange.entity.Exchange;
import kimp.exchange.entity.QExchange;
import kimp.market.entity.QCoin;
import kimp.market.entity.QCoinExchange;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ExchangeRepositoryCustomImpl implements ExchangeRepositoryCustom{

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
}
