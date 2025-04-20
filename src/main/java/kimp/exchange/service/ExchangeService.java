package kimp.exchange.service;

import kimp.exchange.dto.exchange.request.ExchangeCreateRequestDto;
import kimp.exchange.dto.exchange.response.ExchangeDto;
import kimp.market.Enum.MarketType;

import java.util.List;

public interface ExchangeService {
    public ExchangeDto getExchange(Long id);

    public List<ExchangeDto> getExchanges();

    public ExchangeDto getExchangeByMarketType(MarketType exchangeName);

    public List<ExchangeDto> getExchangesByMarketTypes(List<MarketType> exchangeName);

    public ExchangeDto createExchange(ExchangeCreateRequestDto requestDto);

}
