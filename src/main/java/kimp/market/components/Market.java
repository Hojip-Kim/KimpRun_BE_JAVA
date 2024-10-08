package kimp.market.components;


import kimp.market.dto.response.MarketList;
import kimp.market.dto.response.MarketDataList;

import java.io.IOException;

public abstract class Market implements MarketInterface {

    public abstract void initFirst() throws IOException;

    public abstract MarketList getMarketList() throws IOException;

    public abstract MarketDataList getMarketDataList() throws IOException;

    public abstract MarketList getMarketPair() throws IOException;
}
