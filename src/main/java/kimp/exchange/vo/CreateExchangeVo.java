package kimp.exchange.vo;

import kimp.exchange.dto.exchange.request.ExchangeCreateRequestDto;

public class CreateExchangeVo {

    private final ExchangeCreateRequestDto request;

    public CreateExchangeVo(ExchangeCreateRequestDto request) {
        this.request = request;
    }

    public ExchangeCreateRequestDto getRequest() {
        return request;
    }
}
