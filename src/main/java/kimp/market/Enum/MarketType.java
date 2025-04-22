package kimp.market.Enum;

import lombok.Getter;

@Getter
public enum MarketType {

    BINANCE("BINANCE", "https://www.binance.com/"),
    UPBIT("UPBIT", "https://upbit.com/home"),
    COINONE("COINONE", "https://www.coinone.com/"),
    BITHUMB("BITHUMB", "https://www.bithumb.com/"),
    ;

    private final String MarketName;
    private final String mainUrl;

    MarketType(String MarketName, String url) {
        this.MarketName = MarketName;
        this.mainUrl = url;
    }


}
