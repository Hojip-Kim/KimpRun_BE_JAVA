package kimp.market.components.impl;


import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.internal.ServiceCoinWrapperDto;
import kimp.market.dto.coin.internal.crypto.CryptoDto;
import kimp.market.dto.market.internal.MarketList;
import kimp.market.dto.market.response.MarketDataList;

import java.io.IOException;

public abstract class Market<T extends CryptoDto> {

    public abstract void initFirst() throws IOException;

    public abstract MarketList<T> getMarketList();

    public abstract MarketDataList getMarketDataList();

    // String값으로 되어있는 coin들을 dto형태로 변환하여 제공합니다.
    public abstract ServiceCoinWrapperDto getServiceCoins() throws IOException;

    // MarketType을 리턴합니다.
    public abstract MarketType getMarketType();
}
