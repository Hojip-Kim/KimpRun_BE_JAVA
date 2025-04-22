package kimp.market.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.Enum.MarketType;
import kimp.market.common.MarketCommonMethod;
import kimp.market.dto.coin.common.ChangeCoinDto;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.market.response.BinanceMarketList;
import kimp.market.dto.market.response.BinanceTicker;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.dto.market.response.MarketList;
import kimp.market.service.CoinService;
import kimp.websocket.dto.response.BinanceDto;
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
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("binance")
public class Binance extends Market {

    private final MarketCommonMethod marketCommonMethod;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketListProvider binanceMarketListProvider;
    private final CombineMarketListProvider combineMarketListProvider;
    private final CoinService coinService;

    /**
     * binance의 market List.
     * Market Data에 "USDT"가 붙어있는 상태의 리스트입니다.
     * 예를들어, ["BTCUSDT", "ETHUSDT"] 형태의 데이터입니다.
     */
    public MarketList binanceMarketList = null;

    /**
     * binance의 market List.
     * "USDT"가 붙어있지 않은 상태의 리스트입니다.
     * 예를들어, ["BTC", "ETH"] 형태의 데이터입니다.
     */
    public MarketList binanceMarketPair = null;

    public MarketDataList<BinanceDto> binanceMarketDataList;

    @Value("${binance.api.url}")
    private String binanceApiUrl;

    @Value("${binance.ticker.url}")
    private String binanceTickerUrl;

    public Binance(MarketCommonMethod marketCommonMethod, RestTemplate restTemplate, ObjectMapper objectMapper, @Qualifier("binanceName") MarketListProvider binanceMarketListProvider, @Qualifier("combineName") CombineMarketListProvider combineMarketListProvider, CoinService coinService) {
        this.marketCommonMethod = marketCommonMethod;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.binanceMarketListProvider = binanceMarketListProvider;
        this.combineMarketListProvider = combineMarketListProvider;
        this.coinService = coinService;
    }

    @PostConstruct
    public void initFirst() throws IOException {
        if(this.binanceMarketList == null){
            setBinanceMarketList();
        }
    }

    /**
     * @param
     * @return binance의 market List들을 return합니다.
     *         여기서 return되는 market List들은 "USDT"가 빠진 데이터입니다.
     *         즉, ["BTC", "ETH"] 등과같은 형태로 return이 됩니다.
     * @throws IOException
     */
    @Override
    public MarketList getMarketList(){
        return this.binanceMarketList;
    }

    @Override
    public MarketList getMarketPair(){
        return this.binanceMarketPair;
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
        return MarketType.BINANCE;
    }

    @Override
    public MarketDataList getMarketDataList() {
        if(this.binanceMarketDataList == null) {
            setMarketDataList();
        }
        return this.binanceMarketDataList;
    }

    public void setMarketDataList(){
        log.info("Binance dataList reset");
        if(this.binanceMarketList == null) {
            throw new IllegalArgumentException("Binance Market List is null");
        }

        String requestStringURL = binanceTickerUrl + "[" +
                binanceMarketList.getMarkets().stream()
                        .map(market -> "\"" + market + "\"")
                        .collect(Collectors.joining(","))
                + "]";
        BinanceTicker[] tickerData = restTemplate.getForObject(requestStringURL, BinanceTicker[].class);

        BinanceDto binanceDto = null;
        MarketDataList<BinanceDto> binanceMarketDataList = null;

        List<BinanceDto> marketDataList = new ArrayList<>();
        String rateChange = "";
        for (int i = 0; i < tickerData.length; i++) {
            if(tickerData[i].getPriceChangePercent().compareTo(BigDecimal.ZERO) < 0){
                rateChange = "FALL";
            }else if(tickerData[i].getPriceChangePercent().compareTo(BigDecimal.ZERO) > 0){
                rateChange = "RISE";
            }else{
                rateChange = "EVEN";
            }

            binanceDto = new BinanceDto(tickerData[i].getSymbol().replace("USDT", ""), tickerData[i].getQuoteVolume(), tickerData[i].getPriceChangePercent(), tickerData[i].getHighPrice(), tickerData[i].getLowPrice(), tickerData[i].getOpenPrice(), tickerData[i].getLastPrice(), rateChange, tickerData[i].getVolume());
            marketDataList.add(binanceDto);
        }

        binanceMarketDataList = new MarketDataList<>(marketDataList);
        if(binanceMarketDataList != null){
            this.binanceMarketDataList = binanceMarketDataList;
        }else{
            throw new IllegalArgumentException("Binance Market List is null");
        }

    }

    private List<String> getBinanceMarketListMatchedInUpbit() throws IOException {
        List<String> binanceMarketList = this.combineMarketListProvider.getBinanceMarketList();
        List<String> upbitMarketList = this.combineMarketListProvider.getUpbitMarketList();
        return this.combineMarketListProvider.getMarketCombineList(binanceMarketList, upbitMarketList);
    }

    public List<String> detachUsdtTicker(List<String> marketList){

        return marketList.stream().map(market -> market + "USDT").collect(Collectors.toList());
    }

    /**
     * Market List를 갱신합니다.
     * binance의 Market List를 "USDT"를 제거하고 갱신합니다.
     * 예를들어, ["BTC", "ETH"] 의 형태로 갱신합니다.
     *
     * @throws IOException
     */
    public void setBinanceMarketList() throws IOException {
        // marketList : 바이낸스의 마켓이름 데이터

        // 아래 해당하는 binanceMarketListProvider의 marketList는 binance의 모든 ticker에 대한 데이터값을 받아올 수 있습니다.
        // 단, binacne측 nginx 설정으로 인해 request uri가 너무 길다는 에러를 반환하므로, 현재는 사용되지 않습니다.
//         List<String> marketList = binanceMarketListProvider.getMarketListWithTicker();

        // upbit와 연관이 있는 마켓리스트만 호출합니다.
        List<String> marketList = detachUsdtTicker(getBinanceMarketListMatchedInUpbit());

        List<String> marketPair = new ArrayList();

        for (int i = 0; i < marketList.size(); i++) {
            String modifiedString = marketList.get(i).replace("USDT", "");
            marketPair.add(modifiedString);
        }

        // "USDT"가 붙은 market List
        this.binanceMarketList = new BinanceMarketList(marketList);
        // "USDT"가 빠진 market pair
        this.binanceMarketPair = new BinanceMarketList(marketPair);
    }

    /**
     * 1시간마다 주기적으로 Binance의 마켓리스트를 갱신합니다.
     *
     * @throws IOException
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void scheduledSetupBinanceMarketData() throws IOException {
        MarketList prevMarketPair = getMarketPair();
        setBinanceMarketList();
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
