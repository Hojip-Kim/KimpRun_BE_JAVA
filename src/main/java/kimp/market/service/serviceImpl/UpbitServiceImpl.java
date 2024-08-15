package kimp.market.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.common.MarketCommonMethod;
import kimp.market.service.UpbitService;
import kimp.market.dto.response.Ticker;
import kimp.market.dto.response.UpbitMarketData;
import kimp.market.dto.response.UpbitMarketList;
import kimp.websocket.dto.response.SimpleUpbitDto;
import kimp.websocket.dto.response.UpbitMarketDataList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;

    public static UpbitMarketList upbitMarketList = null;
    @Value("${upbit.api.url}")
    private String upbitApiUrl;
    @Value("${upbit.ticker.url}")
    private String upbitTickerUrl;


    // 가장 먼저 setupUpbitMarketData가 세팅되어야 합니다.
    @PostConstruct
    public void initFirst() throws IOException {
        if(UpbitServiceImpl.upbitMarketList == null) {
            setUpbitMarketData();
        }
    }

    public UpbitMarketDataList getUpbitFirstMarketData() {
        if (UpbitServiceImpl.upbitMarketList == null) {
            throw new IllegalArgumentException("Upbit Market List is null");
        }

        List<String> marketData = upbitMarketList.getMarketList();


        String markets = String.join(",", marketData);

        String tickerUrlwithParams = upbitTickerUrl + "?markets=" + markets;
        String tickerData = restTemplate.getForObject(tickerUrlwithParams, String.class);

        SimpleUpbitDto simpleUpbitDto = null;
        UpbitMarketDataList upbitMarketDataList = null;

        try{
            Ticker[] tickers = objectMapper.readValue(tickerData, Ticker[].class);
            List<SimpleUpbitDto> marketDataList = new ArrayList<>();
            for (int i = 0; i < tickers.length; i++) {
                simpleUpbitDto = new SimpleUpbitDto(tickers[i].getMarket(), tickers[i].getTrade_volume(), tickers[i].getSigned_change_rate(), tickers[i].getHighest_52_week_price(), tickers[i].getLowest_52_week_price(), tickers[i].getOpening_price(), tickers[i].getTrade_price(), tickers[i].getChange(), tickers[i].getAcc_trade_price_24h());
                marketDataList.add(simpleUpbitDto);
            }

            upbitMarketDataList = new UpbitMarketDataList(marketDataList);

        }catch(Exception e){
            e.printStackTrace();
        }
        if(upbitMarketDataList != null) {
            return upbitMarketDataList;
        }
        else{
            throw new IllegalArgumentException("Upbit Market List is null");
        }
    }



    @Override
    public UpbitMarketList getUpbitMarketData() {
        return UpbitServiceImpl.upbitMarketList;
    }

    public void setUpbitMarketData() throws IOException{
        String url = upbitApiUrl;
        // marketList : 업비트의 마켓이름 데이터
        List<String> marketList = marketCommonMethod.getMarketListByURLAndStartWith(url, "KRW-", "getMarket", UpbitMarketData[].class);
        UpbitServiceImpl.upbitMarketList = new UpbitMarketList(marketList);
    }

    // 1H Scheduling
    @Scheduled(fixedDelay = 1000*60*24L)
    public void scheduledSetupUpbitMarketData() throws IOException {
        setUpbitMarketData();
    }

}
