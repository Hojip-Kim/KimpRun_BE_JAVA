package kimp.exchange.repository;

import kimp.exchange.entity.Exchange;
import kimp.market.Enum.MarketType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long>, ExchangeRepositoryCustom {

    public Exchange findExchangeByMarket(MarketType marketType);

    public List<Exchange> findByMarketIn(List<MarketType> marketTypeList);

    public List<Exchange> findByIdIn(List<Long> idList);

}
