package kimp.market.service;

import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.MarketDataList;

import java.io.IOException;

public interface MarketService {

    public ServiceCoinWrapperDto getCoinListFromExchange(MarketType marketType);

    public CombinedMarketList getMarketList(String firstMarket, String secondMarket) throws IOException;

    public CombinedMarketDataList getCombinedMarketDataList(String firstMarket, String secondMarket) throws IOException;

    public MarketDataList getMarketDataList(String market) throws IOException;

    public void getMarkets() throws IOException;
}
