package kimp.market.components.impl.list_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.components.MarketListProvider;
import kimp.market.dto.market.common.BinanceMarketData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("binanceName")
public class BinanceMarketListProvider implements MarketListProvider {

    private final MarketCommonMethod marketCommonMethod;

    @Value("${binance.api.url}")
    private String binanceApiUrl;

    public BinanceMarketListProvider(MarketCommonMethod marketCommonMethod) {
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