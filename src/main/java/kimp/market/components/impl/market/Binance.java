package kimp.market.components.impl.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.Enum.MarketType;
import kimp.market.common.MarketCommonMethod;
import kimp.market.components.CombineMarketListProvider;
import kimp.market.components.MarketListProvider;
import kimp.market.components.impl.Market;
import kimp.market.dto.coin.common.ChangeCoinDto;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.coin.common.crypto.BinanceCryptoDto;
import kimp.market.dto.market.response.BinanceTicker;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.dto.market.common.MarketList;
import kimp.market.service.CoinService;
import kimp.market.dto.coin.common.market.BinanceDto;
import kimp.market.service.MarketInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("binance")
public class Binance extends Market<BinanceCryptoDto> {

    private final MarketCommonMethod marketCommonMethod;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final MarketListProvider binanceMarketListProvider;
    private final CombineMarketListProvider combineMarketListProvider;
    private final CoinService coinService;
    private final MarketInfoService marketInfoService;

    /**
     * binance의 market List.
     * binanceMarketList에 market name들을 저장해둡니다
     */
    public MarketList<BinanceCryptoDto> binanceMarketList = null;

    public MarketDataList<BinanceDto> binanceMarketDataList;

    public Map<String, BinanceDto> binanceDtosMap = new HashMap<>();

    @Value("${binance.api.url}")
    private String binanceApiUrl;

    @Value("${binance.ticker.url}")
    private String binanceTickerUrl;

    public Binance(MarketCommonMethod marketCommonMethod, RestClient restClient, ObjectMapper objectMapper, @Qualifier("binanceName") MarketListProvider binanceMarketListProvider, @Qualifier("combineName") CombineMarketListProvider combineMarketListProvider, CoinService coinService, MarketInfoService marketInfoService) {
        this.marketCommonMethod = marketCommonMethod;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.binanceMarketListProvider = binanceMarketListProvider;
        this.combineMarketListProvider = combineMarketListProvider;
        this.coinService = coinService;
        this.marketInfoService = marketInfoService;
    }

    @PostConstruct
    public void initFirst() throws IOException {
        if(this.binanceMarketList == null){
            setBinanceMarketList();
            setMarketDataList();
        }
    }

    /**
     * @param
     * @return binance의 market List들을 return합니다.
     *         여기서 return되는 market List들은 "USDT"가 빠진 데이터입니다.
     *         즉, ["BTC", "ETH"] 등과같은 형태로 return이 됩니다.
     */
    @Override
    public MarketList<BinanceCryptoDto> getMarketList(){
        return this.binanceMarketList;
    }

    @Override
    public ServiceCoinWrapperDto getServiceCoins(){
        List<String> stringMarketPair = getMarketList().getPairList();

        List<ServiceCoinDto> serviceCoinDtos = new ArrayList<>();

        // 여기서 영어이름 넣어줘야함
        for(String pair : stringMarketPair){
            serviceCoinDtos.add(new ServiceCoinDto(pair, null, pair));
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

        List<String> binanceCryptoList = binanceMarketList.getCryptoList();

        int maxIndex = binanceCryptoList.size() / 100 + 1;

        String[][] binanceCryptoListSplit = new String[maxIndex][];
        for(int i = 0; i < maxIndex; i++){
            if(i == maxIndex - 1){
                binanceCryptoListSplit[i] = binanceCryptoList.subList(i * 100, binanceCryptoList.size()).toArray(new String[0]);
            }else{
                binanceCryptoListSplit[i] = binanceCryptoList.subList(i * 100, (i + 1) * 100).toArray(new String[0]);
            }
        }

        List<BinanceDto> marketDataList = new ArrayList<>();

        for(String[] binanceCrypto : binanceCryptoListSplit){
            if(binanceCrypto.length == 0){
                continue;
            }
            String requestUrl = binanceTickerUrl + "[\"" + String.join("\",\"", binanceCrypto) + "\"]";
            BinanceTicker[] tickerData = restClient.get()
                    .uri(requestUrl)
                    .retrieve()
                    .body(BinanceTicker[].class);

            BinanceDto binanceDto = null;

            String rateChange = "";
            for (int i = 0; i < tickerData.length; i++) {
                if(tickerData[i].getPriceChangePercent().compareTo(BigDecimal.ZERO) < 0){
                    rateChange = "FALL";
                }else if(tickerData[i].getPriceChangePercent().compareTo(BigDecimal.ZERO) > 0){
                    rateChange = "RISE";
                }else{
                    rateChange = "EVEN";
                }
                binanceDto = new BinanceDto(tickerData[i].getSymbol().replace("USDT", ""), tickerData[i].getVolume().multiply(BigDecimal.valueOf(marketInfoService.getDollarKRW())), tickerData[i].getPriceChangePercent().divide(new BigDecimal(100)), tickerData[i].getHighPrice().multiply(BigDecimal.valueOf(marketInfoService.getDollarKRW())), tickerData[i].getLowPrice().multiply(BigDecimal.valueOf(marketInfoService.getDollarKRW())), tickerData[i].getOpenPrice().multiply(BigDecimal.valueOf(marketInfoService.getDollarKRW())), tickerData[i].getLastPrice().multiply(BigDecimal.valueOf(marketInfoService.getDollarKRW())), rateChange, tickerData[i].getQuoteVolume().multiply(BigDecimal.valueOf(marketInfoService.getDollarKRW())));
                marketDataList.add(binanceDto);
            }
        }

        marketDataList = marketDataList.stream()
                .filter(data -> data.getTrade_price().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList());

        binanceMarketDataList = new MarketDataList<>(marketDataList);

        for(BinanceDto binanceDto : binanceMarketDataList.getMarketDataList()){
            binanceDtosMap.put(binanceDto.getToken(), binanceDto);
        }
    }

    private List<String> getBinanceMarketListMatchedInUpbit() throws IOException {
        List<String> binanceMarketList = this.combineMarketListProvider.getBinanceMarketList();
        List<String> upbitMarketList = this.combineMarketListProvider.getUpbitMarketList();
        return this.combineMarketListProvider.getMarketCombineList(binanceMarketList, upbitMarketList);
    }

    /**
     * Market List를 갱신합니다.
     * @throws IOException
     */
    public void setBinanceMarketList() throws IOException {
        List<String> marketList = binanceMarketListProvider.getMarketListWithTicker();

        MarketList<BinanceCryptoDto> marketDtoList = new MarketList<>(new ArrayList<BinanceCryptoDto>());

        for(String market : marketList){
            String replacedMarket = market.replace("USDT", "");
            marketDtoList.getCryptoDtoList().add(new BinanceCryptoDto(replacedMarket, "USDT"));
        }

        this.binanceMarketList = marketDtoList;
    }

    /**
     * 1시간마다 주기적으로 Binance의 마켓리스트를 갱신합니다.
     *
     * @throws IOException
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void scheduledSetupBinanceMarketData() throws IOException {
        MarketList<BinanceCryptoDto> prevMarketPair = getMarketList();
        setBinanceMarketList();
        MarketList<BinanceCryptoDto> nextMarketPair = getMarketList();

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
