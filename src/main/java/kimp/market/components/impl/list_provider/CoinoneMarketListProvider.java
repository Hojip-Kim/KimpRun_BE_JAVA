package kimp.market.components.impl.list_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.common.MarketCommonMethod;
import kimp.market.components.MarketListProvider;
import kimp.market.dto.market.common.CoinoneMarketInfo;
import kimp.market.dto.market.common.CoinoneMarketNameData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("coinoneName")
public class CoinoneMarketListProvider implements MarketListProvider {

    private final RestClient restClient;
    private final MarketCommonMethod marketCommonMethod;

    @Value("${coinone.api.url}")
    private String coinoneApiUrl;

    public CoinoneMarketListProvider(RestClient restClient, MarketCommonMethod marketCommonMethod) {
        this.restClient = restClient;
        this.marketCommonMethod = marketCommonMethod;
    }

    @Override
    public List<String> getMarketList() throws IOException {
        List<String> marketList = getMarketListWithTicker();

        marketList.replaceAll(market -> market.replace("KRW-", ""));

        return marketList;
    }

    @Override
    public List<String> getMarketListWithTicker() {
        String url = coinoneApiUrl;

        CoinoneMarketNameData coinoneMarketNameData = restClient.get()
                .uri(url)
                .retrieve()
                .body(CoinoneMarketNameData.class);

        if(coinoneMarketNameData.getResult().equals("success")) {
            List<CoinoneMarketInfo> markets = coinoneMarketNameData.getMarkets();
            return new ArrayList<>(markets.stream().filter(market -> market.getQuoteCurrency().equals("KRW")).map(data -> (data.getQuoteCurrency() + "-" + data.getTargetCurrency())).collect(Collectors.toList()));
        }else{
            return null;
        }
    }
}
