package kimp.market.service.serviceImpl;

import kimp.market.components.impl.market.Binance;
import kimp.market.components.MarketListProvider;
import kimp.market.components.impl.market.Bithumb;
import kimp.market.components.impl.market.Coinone;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.market.common.MarketList;
import kimp.market.dto.market.response.CombinedMarketList;
import kimp.market.Enum.MarketType;
import kimp.market.components.impl.Market;
import kimp.market.components.impl.market.Upbit;
import kimp.market.dto.market.response.CombinedMarketDataList;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.MarketService;
import kimp.market.dto.coin.common.market.MarketDto;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final Bithumb bithumb;
    private final Coinone coinone;

    private final MarketListProvider upbitMarketListProvider;

    private final MarketListProvider binanceMarketListProvider;

    private final Map<MarketType, Market> marketMap;

    public MarketServiceImpl(Upbit upbit, Binance binance, Bithumb bithumb, Coinone coinone, MarketListProvider upbitMarketListProvider, MarketListProvider binanceMarketListProvider) {
        this.upbit = upbit;
        this.binance = binance;
        this.bithumb = bithumb;
        this.coinone = coinone;
        this.marketMap = Map.of(
            MarketType.BINANCE, binance, 
            MarketType.UPBIT, upbit,
            MarketType.BITHUMB, bithumb,
            MarketType.COINONE, coinone
        );
        this.binanceMarketListProvider = binanceMarketListProvider;
        this.upbitMarketListProvider = upbitMarketListProvider;

    }

    @Override
    public ServiceCoinWrapperDto getCoinListFromExchange(MarketType marketType){
        switch(marketType){
            case UPBIT: return upbit.getServiceCoins();
            case BINANCE: return binance.getServiceCoins();
            case BITHUMB: return bithumb.getServiceCoins();
            case COINONE: return coinone.getServiceCoins();
            default: return null;

        }
    }

    // first 마켓에 있는 second 마켓의 코인 페어 쌍을 추출
    // "KRW-"나, "USDT"가 제거된 marketList들의 모음을 반환합니다.
    @Override
    public CombinedMarketList getMarketList(MarketType firstMarket, MarketType secondMarket){
        Market first = marketMap.get(firstMarket);
        Market second = marketMap.get(secondMarket);

        CombinedMarketList marketList = new CombinedMarketList(first.getMarketList().getPairList(),getCombineMarketList(first.getMarketList(), second.getMarketList()));

        if(marketList.getFirstMarketList().isEmpty() || marketList.getSecondMarketList().isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.DATA_PROCESSING_EXCEPTION, "Market lists are empty for markets: " + firstMarket + ", " + secondMarket, HttpStatus.INTERNAL_SERVER_ERROR, "MarketServiceImpl.getMarketList");
        }
        return marketList;
    }

    @Override
    public CombinedMarketDataList getCombinedMarketDataList(MarketType firstMarket, MarketType secondMarket) {
        Market first = marketMap.get(firstMarket);
        Market second = marketMap.get(secondMarket);

        // 공통의 marketData만 추출할 수 있도록 pair 추출
        List<String> marketPair = getCombineMarketList(first.getMarketList(), second.getMarketList());

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
    public MarketDataList getMarketDataList(MarketType marketType) throws IOException {
        Market market = marketMap.get(marketType);
        if(market == null){
            throw new KimprunException(KimprunExceptionEnum.INVALID_PARAMETER_EXCEPTION, "Unsupported market type: " + marketType.toString(), HttpStatus.BAD_REQUEST, "MarketServiceImpl.getMarketDataList");
        }
        return market.getMarketDataList();
    }

    @Override
    public void getMarkets() throws IOException {
        binance.setBinanceMarketList();
    }

    // 뒤의 currency가 제거된 형태로, 첫번째 마켓리스트와 두번째 마켓리스트 간 중복되는 데이터만 추출합니다.
    // 여기서, 마켓 데이터(예 : BTC , ETH)을 제외한 나머지 String은 제외한 상태로 추출합니다.
    public List<String> getCombineMarketList(MarketList firstMarketList, MarketList secondMarketList) {
        List<String> firstMarketPairList = firstMarketList.getPairList();
        List<String> secondMarketPairList = secondMarketList.getPairList();

        firstMarketPairList.retainAll(secondMarketPairList);


        return firstMarketPairList;
    }

    @Scheduled(fixedDelay = 5000)
    public void setMarketsData(){
        upbit.setMarketDataList();
        binance.setMarketDataList();
        bithumb.setMarketDataList();
        coinone.setMarketDataList();
    }

}
