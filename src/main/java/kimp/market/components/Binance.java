package kimp.market.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.common.MarketCommonMethod;
import kimp.market.dto.common.BinanceMarketData;
import kimp.market.dto.response.BinanceMarketList;
import kimp.market.dto.response.BinanceTicker;
import kimp.market.dto.response.MarketDataList;
import kimp.market.dto.response.MarketList;
import kimp.websocket.dto.response.BinanceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Binance extends Market {

    private final MarketCommonMethod marketCommonMethod;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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

    public MarketDataList binanceMarketDataList;

    @Value("${binance.api.url}")
    private String binanceApiUrl;

    @Value("${binance.ticker.url}")
    private String binanceTickerUrl;

    public Binance(MarketCommonMethod marketCommonMethod, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.marketCommonMethod = marketCommonMethod;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    /**
     * @param
     * @return binance의 market List들을 return합니다.
     *         여기서 return되는 market List들은 "USDT"가 빠진 데이터입니다.
     *         즉, ["BTC", "ETH"] 등과같은 형태로 return이 됩니다.
     * @throws IOException
     */
    @Override
    public MarketList getMarketList() throws IOException {
        if(this.binanceMarketList == null) {
            setBinanceMarketList();
        }
        return this.binanceMarketList;
    }

    @Override
    public MarketList getMarketPair() throws IOException {
        if(this.binanceMarketPair == null) {
            setBinanceMarketList();
        }

        return this.binanceMarketPair;
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
        String tickerData = restTemplate.getForObject(requestStringURL, String.class);

        BinanceDto binanceDto = null;
        MarketDataList<BinanceDto> binanceMarketDataList = null;

        try{
            BinanceTicker[] tickers = objectMapper.readValue(tickerData, BinanceTicker[].class);

            List<BinanceDto> marketDataList = new ArrayList<>();
            String rateChange = "";
            for (int i = 0; i < tickers.length; i++) {
                if(tickers[i].getPriceChangePercent().compareTo(BigDecimal.ZERO) < 0){
                    rateChange = "FALL";
                }else if(tickers[i].getPriceChangePercent().compareTo(BigDecimal.ZERO) > 0){
                    rateChange = "RISE";
                }else{
                    rateChange = "EVEN";
                }

                binanceDto = new BinanceDto(tickers[i].getSymbol().replace("USDT", ""), tickers[i].getQuoteVolume(), tickers[i].getPriceChangePercent(), tickers[i].getHighPrice(), tickers[i].getLowPrice(), tickers[i].getOpenPrice(), tickers[i].getLastPrice(), rateChange, tickers[i].getVolume());
                marketDataList.add(binanceDto);
            }

            binanceMarketDataList = new MarketDataList<>(marketDataList);
            if(binanceMarketDataList != null){
                this.binanceMarketDataList = binanceMarketDataList;
            }else{
                throw new IllegalArgumentException("Binance Market List is null");
            }

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @PostConstruct
    public void initFirst() throws IOException {
        if(this.binanceMarketList == null){
            setBinanceMarketList();
        }
    }

    /**
     * Market List를 갱신합니다.
     * binance의 Market List를 "USDT"를 제거하고 갱신합니다.
     * 예를들어, ["BTC", "ETH"] 의 형태로 갱신합니다.
     *
     * @throws IOException
     */
    public void setBinanceMarketList() throws IOException {
        String url = binanceApiUrl;
        // marketList : 바이낸스의 마켓이름 데이터
        List<String> marketList = marketCommonMethod.getMarketListByURLAndEndWith(url, "USDT", "getSymbol", BinanceMarketData[].class);

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
     * 24시간마다 주기적으로 Binance의 마켓리스트를 갱신합니다.
     *
     * @throws IOException
     */
    @Scheduled(fixedRate = 60 * 1000 * 24L)
    public void scheduledSetupBinanceMarketData() throws IOException {
        setBinanceMarketList();
    }

}
