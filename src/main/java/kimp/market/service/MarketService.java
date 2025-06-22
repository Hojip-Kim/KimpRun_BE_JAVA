package kimp.market.service;

import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.market.common.MarketList;
import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.MarketDataList;

import java.io.IOException;
import java.util.List;

public interface MarketService {

    public ServiceCoinWrapperDto getCoinListFromExchange(MarketType marketType);

    public CombinedMarketList getMarketList(MarketType firstMarket, MarketType secondMarket);

    public CombinedMarketDataList getCombinedMarketDataList(MarketType firstMarket, MarketType secondMarket);

    public List<String> getCombineMarketList(MarketList firstMarketList, MarketList secondMarketList);

    public MarketDataList getMarketDataList(MarketType market) throws IOException;

    public void getMarkets() throws IOException;
}
