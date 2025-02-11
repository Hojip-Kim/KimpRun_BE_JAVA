package kimp.market.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.dto.common.UpbitMarketData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("upbitName")
public class UpbitMarketListProvider implements MarketListProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;

    @Value("${upbit.api.url}")
    private String upbitApiUrl;
    @Value("${upbit.ticker.url}")
    private String upbitTickerUrl;

    public UpbitMarketListProvider(RestTemplate restTemplate, ObjectMapper objectMapper, MarketCommonMethod marketCommonMethod) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.marketCommonMethod = marketCommonMethod;
    }

    // "KRW-"를 지운상태의 Market Ticker만을 추출
    @Override
    public List<String> getMarketList() throws IOException {
        List<String> marketList = getMarketListWithTicker();
        List<String> marketPair = new ArrayList<>();

        for (int i = 0; i < marketList.size(); i++) {
            String modifiedString = marketList.get(i).replace("KRW-", "");
            marketPair.add(modifiedString);
        }

        return marketPair;
    }

    @Override
    public List<String> getMarketListWithTicker() throws IOException {

        String url = upbitApiUrl;
        // marketList : 업비트의 마켓이름 데이터
        List<String> marketList = marketCommonMethod.getMarketListByURLAndStartWith(url, "KRW-", "getMarket", UpbitMarketData[].class);

        return marketList;
    }
}
