package kimp.market.components;

import kimp.market.dto.market.response.MarketList;
import kimp.market.dto.market.response.MarketDataList;

import java.io.IOException;

public interface MarketInterface {

    public void initFirst() throws IOException;

    public MarketList getMarketList() throws IOException;

    public MarketDataList getMarketDataList() throws IOException;

}
