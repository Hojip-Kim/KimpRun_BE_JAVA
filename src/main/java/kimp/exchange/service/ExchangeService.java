package kimp.exchange.service;

import kimp.exchange.dto.exchange.request.ExchangeCreateRequestDto;
import kimp.exchange.dto.exchange.response.ExchangeDto;
import kimp.exchange.vo.*;
import kimp.market.Enum.MarketType;

import java.util.List;

public interface ExchangeService {
    public ExchangeDto getExchange(GetExchangeVo vo);

    public List<ExchangeDto> getExchanges();

    public ExchangeDto createExchange(CreateExchangeVo vo);

}
