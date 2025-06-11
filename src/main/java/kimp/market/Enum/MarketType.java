package kimp.market.Enum;

import lombok.Getter;

@Getter
public enum MarketType {

    ALL("ALL", "", 1, ""),
    BINANCE("BINANCE", "https://www.binance.com/", 2, "https://www.binance.com/en/support/announcement/detail/"),
    UPBIT("UPBIT", "https://upbit.com/home", 3, "https://upbit.com/service_center/notice?id="),
    COINONE("COINONE", "https://www.coinone.com/", 4, "https://coinone.co.kr"),
    BITHUMB("BITHUMB", "https://www.bithumb.com/", 5, "https://feed.bithumb.com"),
    ;

    private final String MarketName;
    private final String mainUrl;
    private final long id;
    private final String noticeUrl;

    MarketType(String MarketName, String url, long id, String noticeUrl) {
        this.MarketName = MarketName;
        this.mainUrl = url;
        this.id = id;
        this.noticeUrl = noticeUrl;
    }

    public static MarketType getMarketTypeByExchangeId(long id){
        for(MarketType marketType : MarketType.values()){
            if(marketType.getId() == id){
                return marketType;
            }
        }
        return null;
    }
}
