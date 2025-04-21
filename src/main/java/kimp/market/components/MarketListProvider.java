package kimp.market.components;

import java.io.IOException;
import java.util.List;

public interface MarketListProvider {

    /**
     * Binance의 마켓 목록(티커 목록)을 반환합니다.
     * @return 예: ["BTCUSDT", "ETHUSDT", ...]
     */

    List<String> getMarketList() throws IOException;

    public List<String> getMarketListWithTicker() throws IOException;

}
