package kimp.market.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.dto.common.UpbitMarketData;
import kimp.market.dto.response.MarketList;
import kimp.market.common.MarketCommonMethod;
import kimp.market.dto.response.MarketDataList;
import kimp.market.dto.response.UpbitMarketList;
import kimp.market.dto.response.UpbitTicker;
import kimp.websocket.dto.response.UpbitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Upbit extends Market{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;

    public Upbit(RestTemplate restTemplate, ObjectMapper objectMapper, MarketCommonMethod marketCommonMethod) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.marketCommonMethod = marketCommonMethod;
    }

    public MarketList upbitMarketList = null;

    public MarketList upbitMarketPair = null;

    public MarketDataList<UpbitDto> upbitMarketDataList;

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

    @PostConstruct
    @Override
    public void initFirst() throws IOException {
        if(this.upbitMarketList == null) {
            setUpbitMarketList();
        }
    }

    public void setUpbitMarketList() throws IOException{
        String url = upbitApiUrl;
        // marketList : 업비트의 마켓이름 데이터
        List<String> marketList = marketCommonMethod.getMarketListByURLAndStartWith(url, "KRW-", "getMarket", UpbitMarketData[].class);

        List<String> marketPair = new ArrayList<>();
        for (int i = 0; i < marketList.size(); i++) {
            String modifiedString = marketList.get(i).replace("KRW-", "");
            marketPair.add(modifiedString);
        }

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
