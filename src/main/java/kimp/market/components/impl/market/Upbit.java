package kimp.market.components.impl.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.Enum.MarketType;
import kimp.market.components.MarketListProvider;
import kimp.market.components.impl.Market;
import kimp.market.dto.coin.common.ChangeCoinDto;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.coin.common.crypto.UpbitCryptoDto;
import kimp.market.dto.market.common.MarketList;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.dto.market.response.UpbitTicker;
import kimp.market.service.CoinService;
import kimp.market.dto.coin.common.market.UpbitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Qualifier("upbit")
public class Upbit extends Market<UpbitCryptoDto> {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final MarketListProvider upbitMarketListProvider;
    private final CoinService coinService;


    public Upbit(RestClient restClient, ObjectMapper objectMapper, @Qualifier("upbitName") MarketListProvider upbitMarketListProvider, CoinService coinService) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.upbitMarketListProvider = upbitMarketListProvider;
        this.coinService = coinService;
    }

    public MarketList<UpbitCryptoDto> upbitMarketList = null;

    public MarketDataList<UpbitDto> upbitMarketDataList;

    @Value("${tether.url}")
    private String tetherApiUrl;

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
    public MarketList<UpbitCryptoDto> getMarketList() {
        return this.upbitMarketList;
    }

    @Override
    public ServiceCoinWrapperDto getServiceCoins(){
        List<String> stringMarketPair = getMarketList().getPairList();

        List<ServiceCoinDto> serviceCoinDtos = new ArrayList<>();

        for(String pair : stringMarketPair){
            serviceCoinDtos.add(new ServiceCoinDto(pair, pair, null));
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
        String markets = String.join(",", upbitMarketList.getCryptoList());

        String tickerUrlwithParams = upbitTickerUrl + "?markets=" + markets;
        String tickerData = restClient.get()
                .uri(tickerUrlwithParams)
                .retrieve()
                .body(String.class);

        UpbitDto upbitDto = null;
        MarketDataList<UpbitDto> upbitMarketDataList = null;

        try{
            UpbitTicker[] tickers = objectMapper.readValue(tickerData, UpbitTicker[].class);

            List<UpbitDto> marketDataList = new ArrayList<>();

            for (int i = 0; i < tickers.length; i++) {
                upbitDto = new UpbitDto(tickers[i].getMarket().replace("KRW-", ""), tickers[i].getTrade_volume(), tickers[i].getSigned_change_rate(), tickers[i].getHighest_52_week_price(), tickers[i].getLowest_52_week_price(), tickers[i].getOpening_price(), tickers[i].getTrade_price(), tickers[i].getChange(), tickers[i].getAcc_trade_price_24h());
                marketDataList.add(upbitDto);
            }

            upbitMarketDataList = new MarketDataList<>(marketDataList);
            if(upbitMarketDataList.getMarketDataList() != null && !upbitMarketDataList.getMarketDataList().isEmpty()) {
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
        UpbitTicker[] tickers = restClient.get()
                .uri(tetherApiUrl)
                .retrieve()
                .body(UpbitTicker[].class);
        if (tickers != null && tickers.length > 0) {
            return tickers[0].getTrade_price();
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Market List를 갱신합니다.
     * @throws IOException
     */
    public void setUpbitMarketList() throws IOException{

        List<String> marketList = upbitMarketListProvider.getMarketListWithTicker();

        MarketList<UpbitCryptoDto> marketDtoList = new MarketList<>(new ArrayList<UpbitCryptoDto>());

        for(String market : marketList){
            String replacedMarket = market.replace("KRW-", "");
            marketDtoList.getCryptoDtoList().add(new UpbitCryptoDto(replacedMarket, "KRW"));
        }

        this.upbitMarketList = marketDtoList;

    }

    @Scheduled(fixedDelay = 1000*60)
    public void scheduledSetupUpbitMarketData() throws IOException {
        MarketList prevMarketPair = getMarketList();
        setUpbitMarketList();
        MarketList nextMarketPair = getMarketList();

        // 만약 이전과, 이후의 객체가 다르면 바뀐것
        if(!prevMarketPair.equals(nextMarketPair)){
            List<String> prevMarketList = prevMarketPair.getPairList();
            Set<String> prevMarketSet = new HashSet<>(prevMarketList);
            List<String> nextMarketList = nextMarketPair.getPairList();
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
