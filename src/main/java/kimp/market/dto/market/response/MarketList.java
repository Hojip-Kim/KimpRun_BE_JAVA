package kimp.market.dto.market.response;

import java.util.List;

public class MarketList {

    private List<String> marketList;

    public MarketList(List<String> marketList) {
    }


    public List<String> getMarkets() {
        return this.marketList;
    }
}
