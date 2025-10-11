package kimp.market.components.impl.market;

import jakarta.annotation.PostConstruct;
import kimp.market.Enum.MarketType;
import kimp.market.components.MarketListProvider;
import kimp.market.components.impl.Market;
import kimp.market.dto.coin.internal.ChangeCoinDto;
import kimp.market.dto.coin.internal.ServiceCoinDto;
import kimp.market.dto.coin.internal.ServiceCoinWrapperDto;
import kimp.market.dto.coin.internal.crypto.BithumbCryptoDto;
import kimp.market.dto.coin.internal.market.BithumbDto;
import kimp.market.dto.market.internal.MarketList;
import kimp.market.dto.market.response.BithumbTicker;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Qualifier("bithumb")
public class Bithumb extends Market<BithumbCryptoDto> {

    private final MarketListProvider bithumbMarketListProvider;
    private final RestClient restClient;
    private final CoinService coinService;

    public Bithumb(@Qualifier("bithumbName") MarketListProvider bithumbMarketListProvider, RestClient restClient, CoinService coinService) {
        this.bithumbMarketListProvider = bithumbMarketListProvider;
        this.restClient = restClient;
        this.coinService = coinService;
    }

    public MarketList<BithumbCryptoDto> bithumbMarketList = null;

    public MarketDataList<BithumbDto> bithumbMarketDataList;

    @Value("${bithumb.ticker.url}")
    private String bithumbTickerUrl;

    @PostConstruct
    @Override
    public void initFirst() throws IOException {
        if(this.bithumbMarketList == null) {
            setBithumbMarketList();
        }
    }

    // "KRW-, BTC-를 지운상태의 Marrket Ticker만을 추출"
    @Override
    public MarketList getMarketList() {
        return this.bithumbMarketList;
    }

    @Override
    public MarketDataList getMarketDataList() {
        if(this.bithumbMarketDataList == null) {
            setMarketDataList();
        }
        return this.bithumbMarketDataList;
    }

    @Override
    public ServiceCoinWrapperDto getServiceCoins() {
        List<String> stringMarketPair = getMarketList().getPairList();

        List<ServiceCoinDto> serviceCoinDtos = new ArrayList<>();

        for(String pair : stringMarketPair){
            serviceCoinDtos.add(new ServiceCoinDto(pair, pair, null));
        }

        return new ServiceCoinWrapperDto(this.getMarketType(), serviceCoinDtos);
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.BITHUMB;
    }

    public void setMarketDataList() {
        if (this.bithumbMarketList == null) {
            throw new IllegalArgumentException("Bithumb Market List is null");
        }
        String markets = String.join(",", bithumbMarketList.getKrCryptoNameList());



        String tickerUrlwithParams = bithumbTickerUrl + "?markets=" + markets;
        
        BithumbDto bithumbDto = null;
        MarketDataList<BithumbDto> bithumbMarketDataList = null;

        try{
            BithumbTicker[] tickers = restClient.get()
                    .uri(tickerUrlwithParams)
                    .retrieve()
                    .body(BithumbTicker[].class);

            List<BithumbDto> marketDataList = new ArrayList<>();

            for (int i = 0; i < tickers.length; i++) {
                bithumbDto = new BithumbDto(tickers[i].getMarket().replace("KRW-", ""), tickers[i].getAccTradeVolume24h(), tickers[i].getChangeRate(), tickers[i].getHighest52WeekPrice(), tickers[i].getLowest52WeekPrice(), tickers[i].getOpeningPrice(), tickers[i].getTradePrice(), tickers[i].getChange(), tickers[i].getAccTradePrice24h());
                marketDataList.add(bithumbDto);
            }

            bithumbMarketDataList = new MarketDataList<>(marketDataList);
            if(bithumbMarketDataList.getMarketDataList() != null && !bithumbMarketDataList.getMarketDataList().isEmpty()) {
                this.bithumbMarketDataList = bithumbMarketDataList;
            }
            else{
                throw new IllegalArgumentException("Bithumb Market List is null");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setBithumbMarketList() throws IOException{

        List<String> marketList = bithumbMarketListProvider.getMarketListWithTicker();

        MarketList<BithumbCryptoDto> marketDtoList = new MarketList<>(new ArrayList<BithumbCryptoDto>());

        for(String market : marketList){
            String replacedMarket = market.replace("KRW-", "");
            marketDtoList.getCryptoDtoList().add(new BithumbCryptoDto(replacedMarket, "KRW"));
        }

        this.bithumbMarketList = marketDtoList;

    }

    @Scheduled(fixedDelay = 1000*60)
    public void scheduledSetupUpbitMarketData() throws IOException {
        MarketList prevMarketPair = getMarketList();
        setBithumbMarketList();
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
