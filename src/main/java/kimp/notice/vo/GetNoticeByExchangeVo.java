package kimp.notice.vo;

import kimp.common.dto.PageRequestDto;
import kimp.market.Enum.MarketType;

public class GetNoticeByExchangeVo {

    private final MarketType exchangeType;
    private final PageRequestDto pageRequestDto;

    public GetNoticeByExchangeVo(MarketType exchangeType, PageRequestDto pageRequestDto) {
        this.exchangeType = exchangeType;
        this.pageRequestDto = pageRequestDto;
    }

    public MarketType getExchangeType() {
        return exchangeType;
    }

    public PageRequestDto getPageRequestDto() {
        return pageRequestDto;
    }
}
