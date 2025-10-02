package kimp.market.vo;

import kimp.market.Enum.MarketType;

public class GetMarketDataListVo {

    private final MarketType market;

    public GetMarketDataListVo(MarketType market) {
        this.market = market;
    }

    public MarketType getMarket() {
        return market;
    }
}
