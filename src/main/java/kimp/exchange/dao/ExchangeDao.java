package kimp.exchange.dao;

import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;

import java.util.List;

public interface ExchangeDao {

    public Exchange getExchangeById(Long id);

    public List<Exchange> getExchanges();

    public Exchange createExchange(Exchange exchange);

    public Exchange getExchangeByMarketType(MarketType marketType);

    public Exchange getExchangeByMarketTypeWithCmcExchange(MarketType marketType);

    public List<Exchange> getExchangeByMarketTypes(List<MarketType> marketTypes);

    public List<Exchange> getExchangeByMarketTypesWithCmcExchange(List<MarketType> marketTypes);

    public List<Exchange> getExchangesAndCoinExchangesByIds(Long coinId, List<Long> ids);

}
