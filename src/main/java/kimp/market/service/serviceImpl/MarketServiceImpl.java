package kimp.market.service.serviceImpl;

import kimp.market.components.Binance;
import kimp.market.dto.response.CombinedMarketList;
import kimp.market.Enum.MarketType;
import kimp.market.components.Market;
import kimp.market.components.Upbit;
import kimp.market.dto.response.CombinedMarketDataList;
import kimp.market.dto.response.MarketDataList;
import kimp.market.service.MarketService;
import kimp.websocket.dto.response.MarketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MarketServiceImpl implements MarketService {
    private final Upbit upbit;
    private final Binance binance;

    private final Map<MarketType, Market> marketMap;

    public MarketServiceImpl(Upbit upbit, Binance binance) {
        this.upbit = upbit;
        this.binance = binance;
        this.marketMap = Map.of(MarketType.BINANCE, binance, MarketType.UPBIT, upbit);

    }

    // first 마켓에 있는 second 마켓의 코인 페어 쌍을 추출
    // "KRW-"가 제거된 marketList들의 모음을 반환합니다.
    @Override
    public CombinedMarketList getMarketList(String firstMarket, String secondMarket) throws IOException {
        MarketType firstMarketType = MarketType.valueOf(firstMarket.toUpperCase());
        MarketType secondMarketType = MarketType.valueOf(secondMarket.toUpperCase());
        Market first = marketMap.get(firstMarketType);
        Market second = marketMap.get(secondMarketType);

        CombinedMarketList marketList = new CombinedMarketList(first.getMarketPair().getMarkets(),getMarketPair(first, second));

        if(marketList == null){
            throw new IllegalArgumentException("Not have marketList");
        }
        return marketList;

    }

    @Override
    public CombinedMarketDataList getCombinedMarketDataList(String firstMarket, String secondMarket) throws IOException {
        MarketType firstMarketType = MarketType.valueOf(firstMarket.toUpperCase());
        MarketType secondMarketType = MarketType.valueOf(secondMarket.toUpperCase());

        Market first = marketMap.get(firstMarketType);
        Market second = marketMap.get(secondMarketType);

        // 공통의 marketData만 추출할 수 있도록 pair 추출
        List<String> marketPair = getMarketPair(first, second);

        // 각 market에 맞는 market data list 추출
        MarketDataList<? extends MarketDto> firstMarketDataList = first.getMarketDataList();
        MarketDataList<? extends MarketDto> secondMarketDataList = second.getMarketDataList();

        List<? extends MarketDto> secondMarketDataOfPair = getMarketDataOfPair(marketPair, secondMarketDataList);

        return new CombinedMarketDataList(firstMarketDataList.getMarketDataList() ,secondMarketDataOfPair);
    }

    public List<? extends MarketDto> getMarketDataOfPair(List<String> marketPair, MarketDataList<? extends MarketDto> dataList){
        Map<String, Boolean> isHaveMarket = new HashMap<>();

        List<MarketDto> resultDataList = new ArrayList<>();

        for(String market : marketPair){
            isHaveMarket.put(market, true);
        }

        List<? extends MarketDto> marketDataList = dataList.getMarketDataList();

        for(MarketDto data : marketDataList){
            if(isHaveMarket.get(data.getToken()) == null){
                continue;
            }else {
                if (isHaveMarket.get(data.getToken())) {
                    resultDataList.add(data);
                }
            }
        }
        return resultDataList;
    }


    @Override
    public MarketDataList getMarketDataList(String query) throws IOException {
        MarketType marketType = MarketType.valueOf(query.toUpperCase());

        Market market = marketMap.get(marketType);
        if(market == null){
            throw new IllegalArgumentException("제공하지 않는 마켓 타입입니다." + query);
        }

        return market.getMarketDataList();
    }

    @Override
    public void getMarkets() throws IOException {
        binance.setBinanceMarketList();
    }

    // binance 마켓리스트에서 upbit의 마켓리스트에 있는 market들을 추출합니다.
    // 여기서, 마켓 데이터(예 : BTC , ETH)을 제외한 나머지 String은 제외한 상태로 추출합니다.
    public List<String> getMarketPair(Market firstMarket, Market secondMarket) throws IOException {
        Map<String, Integer> hashMap = new HashMap<>();

        List<String> marketPairList = new ArrayList<>();

        for (String market : secondMarket.getMarketPair().getMarkets()){
            hashMap.put(market, 0);
        }
        for(String market : firstMarket.getMarketPair().getMarkets()){
            if(hashMap.get(market) != null && hashMap.get(market) == 0){
                marketPairList.add(market);
            }
        }

        return marketPairList;
    }

    @Async
    @Scheduled(fixedDelay = 5000)
    public void setMarketsData() throws IOException {
        upbit.setMarketDataList();
        binance.setMarketDataList();
    }

}
