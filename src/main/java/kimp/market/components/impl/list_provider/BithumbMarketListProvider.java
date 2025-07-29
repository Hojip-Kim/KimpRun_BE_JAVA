package kimp.market.components.impl.list_provider;

import kimp.market.common.MarketCommonMethod;
import kimp.market.components.MarketListProvider;
import kimp.market.dto.market.common.BithumbMarketNameData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Qualifier("bithumbName")
public class BithumbMarketListProvider implements MarketListProvider {
    private final MarketCommonMethod marketCommonMethod;

    @Value("${bithumb.api.url}")
    private String bithumbApiUrl;

    public BithumbMarketListProvider(MarketCommonMethod marketCommonMethod) {
        this.marketCommonMethod = marketCommonMethod;
    }

    @Override
    public List<String> getMarketList() throws IOException {
        List<String> marketList = getMarketListWithTicker();

        marketList.replaceAll(market -> market.replace("KRW-", ""));

        return marketList;
    }

    @Override
    public List<String> getMarketListWithTicker() throws IOException {
        String url = bithumbApiUrl;
        return marketCommonMethod.getMarketListByURLAndStartWith(url, "KRW-", "getMarket", BithumbMarketNameData[].class);
    }
}
