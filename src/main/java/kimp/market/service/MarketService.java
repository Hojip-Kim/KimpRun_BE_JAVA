package kimp.market.service;

import kimp.market.dto.response.CombinedMarketList;
import kimp.market.dto.response.MarketDataList;

import java.io.IOException;

public interface MarketService {

    public CombinedMarketList getMarketList() throws IOException;

    public MarketDataList getMarketDataList(String market) throws IOException;

    public void getMarkets() throws IOException;
}
