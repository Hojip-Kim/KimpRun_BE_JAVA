package kimp.market.components;

import java.io.IOException;
import java.util.List;

public interface CombineMarketListProvider extends MarketListProvider {

    public List<String> getMarketCombineList(List<String> firstMarketList, List<String> secondMarketList) throws IOException;

    public List<String> getUpbitMarketList() throws IOException;

    public List<String> getBinanceMarketList() throws IOException;

}
