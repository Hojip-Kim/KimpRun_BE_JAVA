package kimp.market.service;

import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.coin.response.CoinMarketDto;
import kimp.market.dto.market.common.MarketList;
import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.vo.*;

import java.io.IOException;
import java.util.List;

public interface MarketService {

    public ServiceCoinWrapperDto getCoinListFromExchange(MarketType marketType);

    public CombinedMarketList getMarketList(MarketType firstMarket, MarketType secondMarket);

    // DB 기반 마켓 리스트 조회 (새로운 메서드)
    public CombinedMarketList getMarketListFromDatabase(GetMarketListVo vo);

    public CombinedMarketDataList getCombinedMarketDataList(GetCombinedMarketDataListVo vo);

    public List<String> getCombineMarketList(MarketList firstMarketList, MarketList secondMarketList);

    public List<CoinMarketDto> getCombineMarketListFromDatabase(List<CoinMarketDto> firstMarketList, List<CoinMarketDto> secondMarketList);

    public MarketDataList getMarketDataList(GetMarketDataListVo vo) throws IOException;

    public void getMarkets() throws IOException;
}
