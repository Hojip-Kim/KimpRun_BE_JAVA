package kimp.market.dao;


import kimp.market.Enum.MarketType;
import kimp.market.entity.Coin;
import kimp.market.entity.CoinExchange;

import java.util.List;

public interface CoinDao {

    public Coin findById(long id);

    public Coin createCoin(String symbol, String name, String englishName);

    public Coin updateContentCoin(long id, String content);

    public void removeExchangeIdsCoin(long coinId, List<Long> exchangeIds);

    public Coin addExchangeIdsCoin(long coinId, List<CoinExchange> exchangeIds);

    public void deleteCoin(Coin coin);

    public Coin findByIdWithExchanges(long id);

    public List<MarketType> findMarketTypesByCoinId(long coinId);
}
