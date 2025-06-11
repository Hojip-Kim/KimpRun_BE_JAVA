package kimp.common.method;

import kimp.exchange.component.impl.exchange.BinanceScrap;
import kimp.exchange.component.impl.exchange.BithumbScrap;
import kimp.exchange.component.impl.exchange.CoinoneScrap;
import kimp.exchange.component.impl.exchange.UpbitScrap;
import kimp.market.Enum.MarketType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MarketMethod {

    @Value("${upbit.notice.detail.url}")
    private String upbitAbsoluteUrl;
    @Value("${bithumb.notice.detail.url}")
    private String bithumbAbsoluteUrl;
    @Value("${coinone.notice.detail.url}")
    private String coinoneAbsoluteUrl;
    @Value("${binance.notice.detail.url}")
    private String binanceAbsoluteUrl;

    public static Class getMarketClass(MarketType marketType){
        return switch (marketType) {
            case UPBIT -> UpbitScrap.class;
            case BINANCE -> BinanceScrap.class;
            case COINONE -> CoinoneScrap.class;
            case BITHUMB -> BithumbScrap.class;
            default -> throw new IllegalArgumentException("Unsupported market type: " + marketType);
        };
    }

    public String getMarketAbsoluteUrlByMarketType(MarketType marketType){
        return switch (marketType){
            case ALL -> "";
            case UPBIT -> this.upbitAbsoluteUrl;
            case BITHUMB -> this.bithumbAbsoluteUrl;
            case COINONE -> this.coinoneAbsoluteUrl;
            case BINANCE -> this.binanceAbsoluteUrl;
            default -> throw new IllegalArgumentException("Unsupported market type: " + marketType);
        };
    }


}
