package kimp.market.components.impl.list_provider;

import kimp.market.components.CombineMarketListProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Qualifier("combineName")
public class MarketCombineListProvider implements CombineMarketListProvider {

    public final UpbitMarketListProvider upbitMarketListProvider;
    public final BinanceMarketListProvider binanceMarketListProvider;

    public MarketCombineListProvider(UpbitMarketListProvider upbitMarketListProvider, BinanceMarketListProvider binanceMarketListProvider) {
        this.upbitMarketListProvider = upbitMarketListProvider;
        this.binanceMarketListProvider = binanceMarketListProvider;
    }

    @Override
    public List<String> getMarketList() throws IOException {
        return List.of();
    }

    // ~USDT, ~
    @Override
    public List<String> getMarketListWithTicker() throws IOException {
        return List.of();
    }

    // first market에서 second marketList와 매칭되는 마켓리스트만 반환합니다.
    // 통화 화폐를 제거한 값의 list만이 파라미터로 들어올 수 있습니다.
    @Override
    public List<String> getMarketCombineList(List<String> firstMarketList, List<String> secondMarketList) throws IOException {
        Set<String> firstMarketSet = new HashSet<>(firstMarketList);
        List<String> matchedMarketList = new ArrayList<>();

        for(String marketName : secondMarketList) {
            if(firstMarketSet.contains(marketName)) {
                matchedMarketList.add(marketName);
            }
        }

        return matchedMarketList;
    }

    @Override
    public List<String> getUpbitMarketList() throws IOException {
        return this.upbitMarketListProvider.getMarketList();
    }

    @Override
    public List<String> getBinanceMarketList() throws IOException {
        return this.binanceMarketListProvider.getMarketList();
    }


}
