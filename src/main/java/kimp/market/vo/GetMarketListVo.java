package kimp.market.vo;

import kimp.market.Enum.MarketType;

public class GetMarketListVo {

    private final MarketType first;
    private final MarketType second;

    public GetMarketListVo(MarketType first, MarketType second) {
        this.first = first;
        this.second = second;
    }

    public MarketType getFirst() {
        return first;
    }

    public MarketType getSecond() {
        return second;
    }
}
