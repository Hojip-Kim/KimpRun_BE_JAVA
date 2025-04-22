package kimp.exchange.repository;

import kimp.exchange.entity.Exchange;

import java.util.List;

public interface ExchangeRepositoryCustom {

    public List<Exchange> findExchangesWithCoinExchangesAndCoinByIds(Long coinId,List<Long> exchangesIds);

}
