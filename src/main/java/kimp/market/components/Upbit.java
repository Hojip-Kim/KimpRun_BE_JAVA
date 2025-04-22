package kimp.market.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ChangeCoinDto;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.market.response.MarketList;
import kimp.market.common.MarketCommonMethod;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.dto.market.response.UpbitMarketList;
import kimp.market.dto.market.response.UpbitTicker;
import kimp.market.service.CoinService;
import kimp.websocket.dto.response.UpbitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Qualifier("upbit")
public class Upbit extends Market{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;
    private final MarketListProvider upbitMarketListProvider;
    private final CoinService coinService;


    public Upbit(RestTemplate restTemplate, ObjectMapper objectMapper, MarketCommonMethod marketCommonMethod, @Qualifier("upbitName") MarketListProvider upbitMarketListProvider, CoinService coinService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.marketCommonMethod = marketCommonMethod;
        this.upbitMarketListProvider = upbitMarketListProvider;
        this.coinService = coinService;
    }

    public MarketList upbitMarketList = null;

    public MarketList upbitMarketPair = null;

    public MarketDataList<UpbitDto> upbitMarketDataList;

    @Value("${tether.url}")
    private String tetherApiUrl;

    @Value("${upbit.api.url}")
    private String upbitApiUrl;
    @Value("${upbit.ticker.url}")
    private String upbitTickerUrl;


    @PostConstruct
    @Override
    public void initFirst() throws IOException {
        if(this.upbitMarketList == null) {
            setUpbitMarketList();
        }
    }

    @Override
    public MarketList getMarketList() {
        return this.upbitMarketList;
    }

    @Override
    public MarketList getMarketPair() {

        return this.upbitMarketPair;
    }

    @Override
    public ServiceCoinWrapperDto getServiceCoins(){
        MarketList marketList = getMarketPair();
        List<String> stringMarketList = marketList.getMarkets();

        List<ServiceCoinDto> serviceCoinDtos = new ArrayList<>();

        for(String market : stringMarketList){
            serviceCoinDtos.add(new ServiceCoinDto(market, null, market));
        }

        return new ServiceCoinWrapperDto(this.getMarketType(), serviceCoinDtos);
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.UPBIT;
    }


    @Override
    public MarketDataList getMarketDataList() {
        if(this.upbitMarketDataList == null) {
            setMarketDataList();
        }
        return this.upbitMarketDataList;
    }


    public void setMarketDataList() {
        log.info("Upbit dataList reset");
        if (this.upbitMarketList == null) {
            throw new IllegalArgumentException("Upbit Market List is null");
        }
        String markets = String.join(",", upbitMarketList.getMarkets());

        String tickerUrlwithParams = upbitTickerUrl + "?markets=" + markets;
        String tickerData = restTemplate.getForObject(tickerUrlwithParams, String.class);

        UpbitDto UpbitDto = null;
        MarketDataList<UpbitDto> upbitMarketDataList = null;

        try{
            UpbitTicker[] tickers = objectMapper.readValue(tickerData, UpbitTicker[].class);

            List<UpbitDto> marketDataList = new ArrayList<>();

            for (int i = 0; i < tickers.length; i++) {
                UpbitDto = new UpbitDto(tickers[i].getMarket().replace("KRW-", ""), tickers[i].getTrade_volume(), tickers[i].getSigned_change_rate(), tickers[i].getHighest_52_week_price(), tickers[i].getLowest_52_week_price(), tickers[i].getOpening_price(), tickers[i].getTrade_price(), tickers[i].getChange(), tickers[i].getAcc_trade_price_24h());
                marketDataList.add(UpbitDto);
            }

            upbitMarketDataList = new MarketDataList<UpbitDto>(marketDataList);
            if(upbitMarketDataList != null) {
                this.upbitMarketDataList = upbitMarketDataList;
            }
            else{
                throw new IllegalArgumentException("Upbit Market List is null");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public BigDecimal getUpbitTether(){
        UpbitTicker[] tickers = restTemplate.getForObject(tetherApiUrl, UpbitTicker[].class);
        if (tickers != null && tickers.length > 0) {
            return tickers[0].getTrade_price();
        } else {
            return BigDecimal.ZERO;
        }
    }

    public void setUpbitMarketList() throws IOException{

        List<String> marketList = upbitMarketListProvider.getMarketListWithTicker();

        List<String> marketPair = upbitMarketListProvider.getMarketList();

        // "KRW-"가 붙은 market List
        this.upbitMarketList = new UpbitMarketList(marketList);
        // "KRW-"를 뺀 market pair
        this.upbitMarketPair = new UpbitMarketList(marketPair);

    }

    @Scheduled(fixedDelay = 1000*60)
    public void scheduledSetupUpbitMarketData() throws IOException {
        MarketList prevMarketPair = getMarketPair();
        setUpbitMarketList();
        MarketList nextMarketPair = getMarketPair();

        // 만약 이전과, 이후의 객체가 다르면 바뀐것
        if(!prevMarketPair.equals(nextMarketPair)){
            List<String> prevMarketList = prevMarketPair.getMarkets();
            Set<String> prevMarketSet = new HashSet<>(prevMarketList);
            List<String> nextMarketList = nextMarketPair.getMarkets();
            Set<String> nextMarketSet = new HashSet<>(nextMarketList);

            List<String> listCoinSymbols = new ArrayList<>();
            List<String> delistCoinSymbols = new ArrayList<>();

            for(String nextMarket : nextMarketList){
                if(!prevMarketSet.contains(nextMarket)){
                    listCoinSymbols.add(nextMarket);
                }
            }

            for(String prevMarket : prevMarketList){
                if(!nextMarketSet.contains(prevMarket)){
                    delistCoinSymbols.add(prevMarket);
                }
            }

            ChangeCoinDto changeCoinDto = new ChangeCoinDto(getMarketType() ,listCoinSymbols, delistCoinSymbols);
            coinService.createWithDeleteCoin(changeCoinDto);

        }
    }
}
