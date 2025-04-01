package kimp.market.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.dto.market.response.MarketList;
import kimp.market.common.MarketCommonMethod;
import kimp.market.dto.market.response.MarketDataList;
import kimp.market.dto.market.response.UpbitMarketList;
import kimp.market.dto.market.response.UpbitTicker;
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
import java.util.List;

@Component
@Slf4j
public class Upbit extends Market{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;
    private final MarketListProvider upbitMarketListProvider;


    public Upbit(RestTemplate restTemplate, ObjectMapper objectMapper, MarketCommonMethod marketCommonMethod, @Qualifier("upbitName") MarketListProvider upbitMarketListProvider) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.marketCommonMethod = marketCommonMethod;
        this.upbitMarketListProvider = upbitMarketListProvider;
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


    @Override
    public MarketList getMarketList() throws IOException {
        if(this.upbitMarketList == null) {
            setUpbitMarketList();
        }
        return this.upbitMarketList;
    }

    @Override
    public MarketList getMarketPair() throws IOException {
        if(this.upbitMarketPair == null) {
            setUpbitMarketList();
        }
        return this.upbitMarketPair;
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

    @PostConstruct
    @Override
    public void initFirst() throws IOException {
        if(this.upbitMarketList == null) {
            setUpbitMarketList();
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

    @Scheduled(fixedDelay = 1000*60*24L)
    public void scheduledSetupUpbitMarketData() throws IOException {
        setUpbitMarketList();
    }
}
