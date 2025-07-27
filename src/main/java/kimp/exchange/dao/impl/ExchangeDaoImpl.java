package kimp.exchange.dao.impl;

import kimp.exchange.dao.ExchangeDao;
import kimp.exchange.entity.Exchange;
import kimp.exchange.repository.ExchangeRepository;
import kimp.market.Enum.MarketType;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import org.springframework.http.HttpStatus;
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
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "Exchange not found with ID: " + id, HttpStatus.NOT_FOUND, "ExchangeDaoImpl.getExchangeById");
        }

        return exchange.get();
    }

    @Override
    public List<Exchange> getExchanges() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        if(exchanges.isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "No exchanges found in database", HttpStatus.NOT_FOUND, "ExchangeDaoImpl.getExchanges");
        }
        return exchanges;
    }

    @Override
    public Exchange createExchange(Exchange exchange) {
        Exchange foundExchange = exchangeRepository.findExchangeByMarket(exchange.getMarket());
        if(foundExchange != null) {
            return foundExchange;
        }

        return exchangeRepository.save(exchange);
    }

    @Override
    public Exchange getExchangeByMarketType(MarketType marketType) {
        Exchange exchange = exchangeRepository.findExchangeByMarket(marketType);
        if(exchange == null) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "Exchange not found for market type: " + marketType, HttpStatus.NOT_FOUND, "ExchangeDaoImpl.getExchangeByMarketType");
        }
        return exchange;
    }

    @Override
    public List<Exchange> getExchangeByMarketTypes(List<MarketType> marketTypes) {
        List<Exchange> exchanges = exchangeRepository.findByMarketIn(marketTypes);

        if(exchanges.isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "No exchanges found for the provided market types", HttpStatus.NOT_FOUND, "ExchangeDaoImpl.getExchangeByMarketTypes");
        }

        return exchanges;
    }

    @Override
    public List<Exchange> getExchangesByIds(List<Long> ids) {
        List<Exchange> exchanges = exchangeRepository.findByIdIn(ids);
        if(exchanges.isEmpty()) {
            throw new KimprunException(KimprunExceptionEnum.RESOURCE_NOT_FOUND_EXCEPTION, "No exchanges found for the provided IDs", HttpStatus.NOT_FOUND, "ExchangeDaoImpl.getExchangesByIds");
        }
        return exchanges;
    }

    @Override
    public List<Exchange> getExchangesAndCoinExchangesByIds(Long coinId, List<Long> ids) {

        List<Exchange> exchanges = exchangeRepository.findExchangesWithCoinExchangesAndCoinByIds(coinId, ids);

        return exchanges;
    }
}
