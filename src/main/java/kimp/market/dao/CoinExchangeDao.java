package kimp.market.dao;

import kimp.market.entity.CoinExchange;

import java.util.List;

public interface CoinExchangeDao {

    public CoinExchange createCoinExchange(CoinExchange coinExchange);

    public List<CoinExchange> findCoinExchangeWithExchangeByCoinIdAndExchangeIds(long coinId, List<Long> exchangeIds);

    public void deleteAllByCoinExchanges(List<CoinExchange> coinExchanges);

    public List<CoinExchange> findCoinExchangeWithExchangeByCoinId(Long coinId);
}
