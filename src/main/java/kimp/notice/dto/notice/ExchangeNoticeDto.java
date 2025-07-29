package kimp.notice.dto.notice;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExchangeNoticeDto<T> {
    private T data;
    private String absoluteUrl;
    private MarketType marketType;

    public ExchangeNoticeDto(String absoluteUrl, MarketType marketType) {
        this.absoluteUrl = absoluteUrl;
        this.marketType = marketType;
    }

    public ExchangeNoticeDto<T> setData(T data) {
        this.data = data;
        return this;
    }
}
