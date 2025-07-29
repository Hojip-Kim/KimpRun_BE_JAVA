package kimp.market.components.impl.list_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.components.MarketListProvider;
import kimp.market.dto.market.common.UpbitMarketNameData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Component
@Qualifier("upbitName")
public class UpbitMarketListProvider implements MarketListProvider {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;

    @Value("${upbit.api.url}")
    private String upbitApiUrl;
    @Value("${upbit.ticker.url}")
    private String upbitTickerUrl;

    public UpbitMarketListProvider(RestClient restClient, ObjectMapper objectMapper, MarketCommonMethod marketCommonMethod) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.marketCommonMethod = marketCommonMethod;
    }

    // "KRW-"를 지운상태의 Market Ticker만을 추출
    @Override
    public List<String> getMarketList() throws IOException {
        List<String> marketList = getMarketListWithTicker();

        marketList.replaceAll(market -> market.replace("KRW-", ""));
        return marketList;
    }

    @Override
    public List<String> getMarketListWithTicker() throws IOException {
        String url = upbitApiUrl;
        return marketCommonMethod.getMarketListByURLAndStartWith(url, "KRW-", "getMarket", UpbitMarketNameData[].class);

    }
}
