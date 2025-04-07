package kimp.market.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.dto.market.common.BinanceMarketData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("binanceName")
public class BinanceMarketListProvider implements MarketListProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarketCommonMethod marketCommonMethod;

    @Value("${binance.api.url}")
    private String binanceApiUrl;

    public BinanceMarketListProvider(RestTemplate restTemplate, ObjectMapper objectMapper, MarketCommonMethod marketCommonMethod) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.marketCommonMethod = marketCommonMethod;
    }

    // "-USDT"를 지운상태의 Market Ticker만을 추출
    @Override
    public List<String> getMarketList() throws IOException {
        List<String> marketList = getMarketListWithTicker();
        return marketList.stream()
                .map(ticker -> ticker.replace("USDT", ""))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMarketListWithTicker() throws IOException {
        String url = binanceApiUrl;
        List<String> marketList = marketCommonMethod.getMarketListByURLAndEndWith(url, "USDT", "getSymbol", BinanceMarketData[].class);
        return marketList;
    }
}