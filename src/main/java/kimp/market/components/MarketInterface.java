package kimp.market.components;

import kimp.market.dto.response.MarketDataList;
import kimp.market.dto.response.MarketList;

import java.io.IOException;

public interface MarketInterface {

    public void initFirst() throws IOException;

    public MarketList getMarketList() throws IOException;

    public MarketDataList getMarketDataList() throws IOException;

}
