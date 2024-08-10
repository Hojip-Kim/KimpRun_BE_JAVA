package kimp.market.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.common.MarketCommonMethod;
import kimp.market.service.UpbitService;
import kimp.market.dto.response.Ticker;
import kimp.market.dto.response.UpbitMarketData;
import kimp.market.dto.response.UpbitMarketList;
import kimp.websocket.dto.response.SimpleUpbitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpbitServiceImpl implements UpbitService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;

    public static UpbitMarketList upbitMarketList;
    @Value("${upbit.api.url}")
    private String upbitApiUrl;
    @Value("${upbit.ticker.url}")
    private String upbitTickerUrl;


    // 가장 먼저 setupUpbitMarketData가 세팅되어야 합니다.
    @PostConstruct
    public void initFirst() throws IOException {
        setupUpbitMarketData();
    }

    public Map<String, SimpleUpbitDto> formattingData(SimpleUpbitDto[] dto) {
        Map<String, SimpleUpbitDto> map = new HashMap<>();
        String token;
        for (SimpleUpbitDto data : dto) {
            token = data.getToken();
            map.put(token, data);
        }
        return map;
    }


    // TODO : Map = > dto refactoring 필수
    public Map<String, SimpleUpbitDto> getUpbitFirstMarketData() {
        if (upbitMarketList == null) {
            throw new IllegalArgumentException("Upbit Market List is null");
        }

        List<String> marketData = upbitMarketList.getMarketList();

        String markets = String.join(",", marketData);

        String tickerUrlwithParams = upbitTickerUrl + "?markets=" + markets;
        String tickerData = restTemplate.getForObject(tickerUrlwithParams, String.class);
        SimpleUpbitDto[] simpleUpbitDtos = null;

        try{
            Ticker[] tickers = objectMapper.readValue(tickerData, Ticker[].class);

            simpleUpbitDtos = new SimpleUpbitDto[tickers.length];

            for (int i = 0; i < tickers.length; i++) {
                simpleUpbitDtos[i] = new SimpleUpbitDto(tickers[i].getMarket(), tickers[i].getTrade_volume(), tickers[i].getChange_rate(), tickers[i].getHighest_52_week_price(), tickers[i].getLowest_52_week_price(), tickers[i].getOpening_price(), tickers[i].getTrade_price(), tickers[i].getChange());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if(simpleUpbitDtos != null) {
            return formattingData(simpleUpbitDtos);
        }
        else{
            throw new IllegalArgumentException("Upbit Market List is null");
        }
    }

    @Override
    public UpbitMarketList getUpbitMarketData() {
        return UpbitServiceImpl.upbitMarketList;
    }

    // 1H Scheduling
    @Scheduled(fixedDelay = 1000*60*24L)
    public void setupUpbitMarketData() throws IOException {

        String url = upbitApiUrl;
        // marketList : 업비트의 마켓이름 데이터
        List<String> marketList = marketCommonMethod.getMarketListByURLAndStartWith(url, "KRW-", "getMarket", UpbitMarketData[].class);

        this.upbitMarketList = new UpbitMarketList(marketList);

    }





}
