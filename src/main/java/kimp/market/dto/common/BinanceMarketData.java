package kimp.market.dto.common;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class BinanceMarketData {

    private String symbol;
    private String priceChange;
    private String priceChangePercent;
    private String weightedAvgPrice;
    private String prevClosePrice;
    private String lastPrice;
    private String lastQty;
    private String bidPrice;
    private String bidQty;
    private String askPrice;
    private String askQty;
    private String openPrice;
    private String highPrice;
    private String lowPrice;
    private String volume;
    private String quoteVolume;
    private BigDecimal openTime;
    private BigDecimal closeTime;
    private BigDecimal firstId;
    private BigDecimal lastId;
    private BigDecimal count;

    public BinanceMarketData(){}

    public BinanceMarketData(String symbol, String priceChange, String priceChangePercent, String weightedAvgPrice, String prevClosePrice, String lastPrice, String lastQty, String bidPrice, String bidQty, String askPrice, String askQty, String openPrice, String highPrice, String lowPrice, String volume, String quoteVolume, BigDecimal openTime, BigDecimal closeTime, BigDecimal firstId, BigDecimal lastId, BigDecimal count) {
        this.symbol = symbol;
        this.priceChange = priceChange;
        this.priceChangePercent = priceChangePercent;
        this.weightedAvgPrice = weightedAvgPrice;
        this.prevClosePrice = prevClosePrice;
        this.lastPrice = lastPrice;
        this.lastQty = lastQty;
        this.bidPrice = bidPrice;
        this.bidQty = bidQty;
        this.askPrice = askPrice;
        this.askQty = askQty;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.volume = volume;
        this.quoteVolume = quoteVolume;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.firstId = firstId;
        this.lastId = lastId;
        this.count = count;
    }

}
