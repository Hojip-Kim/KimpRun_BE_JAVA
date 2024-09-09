package kimp.market.dto.response;

import java.util.List;

public class BinanceMarketList extends MarketList {

    private List<String> marketList;

    public BinanceMarketList(List<String> marketList) {
        super(marketList);
        this.marketList = marketList;
    }

    @Override
    public List<String> getMarkets() {
        return this.marketList;
    }
}
