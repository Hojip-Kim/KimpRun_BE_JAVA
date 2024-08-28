package kimp.market.dto.common;

import java.math.BigDecimal;

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

    public String getSymbol() {
        return symbol;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public String getPriceChangePercent() {
        return priceChangePercent;
    }

    public String getWeightedAvgPrice() {
        return weightedAvgPrice;
    }

    public String getPrevClosePrice() {
        return prevClosePrice;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getLastQty() {
        return lastQty;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public String getBidQty() {
        return bidQty;
    }

    public String getAskPrice() {
        return askPrice;
    }

    public String getAskQty() {
        return askQty;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public String getVolume() {
        return volume;
    }

    public String getQuoteVolume() {
        return quoteVolume;
    }

    public BigDecimal getOpenTime() {
        return openTime;
    }

    public BigDecimal getCloseTime() {
        return closeTime;
    }

    public BigDecimal getFirstId() {
        return firstId;
    }

    public BigDecimal getLastId() {
        return lastId;
    }

    public BigDecimal getCount() {
        return count;
    }
}
