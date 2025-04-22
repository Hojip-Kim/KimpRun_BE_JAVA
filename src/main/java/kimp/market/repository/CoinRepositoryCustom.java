package kimp.market.repository;

import kimp.market.Enum.MarketType;
import kimp.market.entity.Coin;

import java.util.List;
import java.util.Optional;

public interface CoinRepositoryCustom {
    Optional<Coin> findByIdWithExchanges(Long id);
    List<MarketType> findMarketTypesByCoinId(Long coinId);
    void deleteByIdWithExchangeIds(Long coinId, List<Long> exchangeIds);
    List<Coin> findCoinsByExchange(Long exchangeId);
    List<Coin> findCoinWithExchangesBySymbols(List<String> symbols);
}
