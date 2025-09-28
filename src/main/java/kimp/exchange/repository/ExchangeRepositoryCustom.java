package kimp.exchange.repository;

import kimp.exchange.entity.Exchange;

import java.util.List;

public interface ExchangeRepositoryCustom {

    public List<Exchange> findExchangesWithCoinExchangesAndCoinByIds(Long coinId,List<Long> exchangesIds);

    public Exchange findExchangeByMarketTypeWithCmcExchange(kimp.market.Enum.MarketType marketType);

    public List<Exchange> findByMarketTypesWithCmcExchange(List<kimp.market.Enum.MarketType> marketTypes);

}
