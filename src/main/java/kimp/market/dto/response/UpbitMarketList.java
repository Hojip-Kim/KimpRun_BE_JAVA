package kimp.market.dto.response;


import java.util.List;

public class UpbitMarketList extends MarketList{

    private List<String> marketList;

    public UpbitMarketList(List<String> marketList) {
        super(marketList);
        this.marketList = marketList;
    }

    @Override
    public List<String> getMarkets() {
        return this.marketList;
    }
}
