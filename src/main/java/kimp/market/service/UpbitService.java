package kimp.market.service;

import kimp.market.dto.response.UpbitMarketList;
import kimp.websocket.dto.response.UpbitMarketDataList;


public interface UpbitService {

    public UpbitMarketList getUpbitMarketData();

    public UpbitMarketDataList getUpbitFirstMarketData();


}
