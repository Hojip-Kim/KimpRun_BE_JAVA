package kimp.market.service.serviceImpl;

import kimp.market.Enum.MarketType;
import kimp.market.components.Binance;
import kimp.market.components.Market;
import kimp.market.components.Upbit;
import kimp.market.dto.response.CombinedMarketList;
import kimp.market.dto.response.MarketDataList;
import kimp.market.service.MarketService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketServiceImpl implements MarketService {
    private final Upbit upbit;
    private final Binance binance;

    private final Map<MarketType, Market> marketMap;

    public MarketServiceImpl(Upbit upbit, Binance binance) {
        this.upbit = upbit;
        this.binance = binance;
        this.marketMap = Map.of(MarketType.BINANCE, binance, MarketType.UPBIT, upbit);
    }

    // Upbit 마켓에 있는 binance 마켓의 코인 페어 쌍을 추출
    // "KRW-"가 제거된 marketList들의 모음을 반환합니다.
    @Override
    public CombinedMarketList getMarketList() throws IOException {

        CombinedMarketList marketList = new CombinedMarketList(upbit.upbitMarketPair.getMarkets(),getUpbitBinanceMarketPair());

        if(marketList == null){
            throw new IllegalArgumentException("Not have marketList");
        }
        return marketList;

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
    // 여기서, 마켓 데이터(BTC , ETH)을 제외한 나머지 String은 제외한 상태로 추출합니다.
    public List<String> getUpbitBinanceMarketPair() throws IOException {
        Map<String, Integer> hashMap = new HashMap<>();

        List<String> marketPairList = new ArrayList<>();

        for (String binanceMarket : binance.getMarketPair().getMarkets()){
            hashMap.put(binanceMarket, 0);
        }
        for(String upbitMarket : upbit.getMarketPair().getMarkets()){
            if(hashMap.get(upbitMarket) != null && hashMap.get(upbitMarket) == 0){
                marketPairList.add(upbitMarket);
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
