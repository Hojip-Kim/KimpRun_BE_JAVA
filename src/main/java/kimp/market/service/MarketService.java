package kimp.market.service;

import kimp.market.dto.response.CombinedMarketList;
import kimp.market.dto.response.CombinedMarketDataList;
import kimp.market.dto.response.MarketDataList;

import java.io.IOException;

public interface MarketService {

    public CombinedMarketList getMarketList(String firstMarket, String secondMarket) throws IOException;

    public CombinedMarketDataList getCombinedMarketDataList(String firstMarket, String secondMarket) throws IOException;

    public MarketDataList getMarketDataList(String market) throws IOException;

    public void getMarkets() throws IOException;
}
