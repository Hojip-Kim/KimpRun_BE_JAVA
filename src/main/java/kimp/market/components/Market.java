package kimp.market.components;


import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinWrapperDto;
import kimp.market.dto.market.response.MarketList;
import kimp.market.dto.market.response.MarketDataList;

import java.io.IOException;

public abstract class Market implements MarketInterface {

    public abstract void initFirst() throws IOException;

    public abstract MarketList getMarketList() throws IOException;

    public abstract MarketDataList getMarketDataList() throws IOException;

    public abstract MarketList getMarketPair();

    // String값으로 되어있는 coin들을 dto형태로 변환하여 제공합니다.
    public abstract ServiceCoinWrapperDto getServiceCoins() throws IOException;

    public abstract MarketType getMarketType();
}
