package kimp.exchange.service.impl;

import jakarta.annotation.PostConstruct;
import kimp.common.method.DtoConverter;
import kimp.exchange.dao.ExchangeDao;
import kimp.exchange.dto.exchange.request.ExchangeCreateRequestDto;
import kimp.exchange.dto.exchange.response.ExchangeDto;
import kimp.exchange.entity.Exchange;
import kimp.exchange.service.ExchangeService;
import kimp.market.Enum.MarketType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeDao exchangeDao;
    private final DtoConverter dtoConverter;

    public ExchangeServiceImpl(ExchangeDao exchangeDao, DtoConverter dtoConverter) {
        this.exchangeDao = exchangeDao;
        this.dtoConverter = dtoConverter;
    }

    @PostConstruct
    public void setBeginExchangeData(){
        MarketType[] market = MarketType.values();
        for(MarketType type : market){
            ExchangeCreateRequestDto dto = new ExchangeCreateRequestDto(type,type.getMainUrl());
            this.createExchange(dto);
        }
    }

    @Override
    public ExchangeDto getExchange(Long id) {
        Exchange exchange = this.exchangeDao.getExchangeById(id);

        return dtoConverter.convertExchangeToExchangeDto(exchange);
    }

    @Override
    public List<ExchangeDto> getExchanges() {
        List<Exchange> exchanges = exchangeDao.getExchanges();

        return dtoConverter.convertExchangeListToExchangeDtoList(exchanges);
    }

    @Override
    public ExchangeDto getExchangeByMarketType(MarketType exchangeName) {
        Exchange exchange = exchangeDao.getExchangeByMarketType(exchangeName);
        return dtoConverter.convertExchangeToExchangeDto(exchange);
    }

    @Override
    public List<ExchangeDto> getExchangesByMarketTypes(List<MarketType> exchangeName) {
        List<Exchange> exchanges = exchangeDao.getExchangeByMarketTypes(exchangeName);
        return dtoConverter.convertExchangeListToExchangeDtoList(exchanges);
    }

    @Override
    public ExchangeDto createExchange(ExchangeCreateRequestDto requestDto) {
        MarketType exchangeType = requestDto.getExchangeName();
        String exchangeMainLink = requestDto.getLink();

        Exchange exchange = new Exchange(exchangeType, exchangeMainLink);

        Exchange craetedExchange = this.exchangeDao.createExchange(exchange);

        return dtoConverter.convertExchangeToExchangeDto(craetedExchange);
    }

}
