package kimp.exchange.dao.impl;

import kimp.exchange.dao.ExchangeDao;
import kimp.exchange.entity.Exchange;
import kimp.exchange.repository.ExchangeRepository;
import kimp.market.Enum.MarketType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExchangeDaoImpl implements ExchangeDao {

    private final ExchangeRepository exchangeRepository;

    public ExchangeDaoImpl(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public Exchange getExchangeById(Long id) {
        Optional<Exchange> exchange = exchangeRepository.findById(id);

        if(!exchange.isPresent()) {
            throw new IllegalArgumentException("Exchange not found : " + id);
        }

        return exchange.get();
    }

    @Override
    public List<Exchange> getExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        if(exchanges.isEmpty()) {
            throw new IllegalArgumentException("No exchanges found");
        }
        return exchanges;
    }

    @Override
    public Exchange createExchange(Exchange exchange) {
        Exchange foundExchange = exchangeRepository.findExchangeByMarket(exchange.getMarket());
        if(foundExchange != null) {
            throw new IllegalArgumentException("Exchange already exists");
        }

        return exchangeRepository.save(exchange);
    }

    @Override
    public Exchange getExchangeByMarketType(MarketType marketType) {
        Exchange exchange = exchangeRepository.findExchangeByMarket(marketType);
        if(exchange == null) {
            throw new IllegalArgumentException("Exchange not found : " + marketType);
        }
        return exchange;
    }

    @Override
    public List<Exchange> getExchangeByMarketTypes(List<MarketType> marketTypes) {
        List<Exchange> exchanges = exchangeRepository.findByMarketIn(marketTypes);

        if(exchanges.isEmpty()) {
            throw new IllegalArgumentException("No exchanges found");
        }

        return exchanges;
    }

    @Override
    public List<Exchange> getExchangesByIds(List<Long> ids) {
        List<Exchange> exchanges = exchangeRepository.findByIdIn(ids);
        if(exchanges.isEmpty()) {
            throw new IllegalArgumentException("No exchanges found");
        }
        return exchanges;
    }

    @Override
    public List<Exchange> getExchangesAndCoinExchangesByIds(Long coinId, List<Long> ids) {

        List<Exchange> exchanges = exchangeRepository.findExchangesWithCoinExchangesAndCoinByIds(coinId, ids);

        return exchanges;
    }
}
