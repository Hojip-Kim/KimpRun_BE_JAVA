package kimp.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UpbitReceiveDto {
    @JsonProperty("type")
    private String type;
    @JsonProperty("code")
    private String code;
    @JsonProperty("opening_price")
    private BigDecimal openingPrice;
    @JsonProperty("high_price")
    private BigDecimal highPrice;
    @JsonProperty("low_price")
    private BigDecimal lowPrice;
    @JsonProperty("trade_price")
    private BigDecimal tradePrice;
    @JsonProperty("prev_closing_price")
    private BigDecimal prevClosingPrice;
    @JsonProperty("change")
    private String change;
    @JsonProperty("change_price")
    private BigDecimal changePrice;
    @JsonProperty("signed_change_price")
    private BigDecimal signedChangePrice;
    @JsonProperty("change_rate")
    private BigDecimal changeRate;
    @JsonProperty("signed_change_rate")
    private BigDecimal signedChangeRate;
    @JsonProperty("trade_volume")
    private BigDecimal tradeVolume;
    @JsonProperty("acc_trade_volume")
    private BigDecimal accTradeVolume;
    @JsonProperty("acc_trade_volume_24h")
    private BigDecimal accTradeVolume24h;
    @JsonProperty("acc_trade_price")
    private BigDecimal accTradePrice;
    @JsonProperty("acc_trade_price_24h")
    private BigDecimal accTradePrice24h;
    @JsonProperty("trade_date")
    private String tradeDate;
    @JsonProperty("trade_time")
    private String tradeTime;
    @JsonProperty("trade_timestamp")
    private Long tradeTimestamp;
    @JsonProperty("ask_bid")
    private String askBid;
    @JsonProperty("acc_ask_volume")
    private BigDecimal accAskVolume;
    @JsonProperty("acc_bid_volume")
    private BigDecimal accBidVolume;
    @JsonProperty("highest_52_week_price")
    private BigDecimal highest52WeekPrice;
    @JsonProperty("highest_52_week_date")
    private String highest52WeekDate;
    @JsonProperty("lowest_52_week_price")
    private BigDecimal lowest52WeekPrice;
    @JsonProperty("lowest_52_week_date")
    private String lowest52WeekDate;
    @JsonProperty("market_state")
    private String marketState;
    @JsonProperty("is_trading_suspended")
    private Boolean isTradingSuspended;
    @JsonProperty("delisting_date")
    private Object delistingDate;

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getOpeningPrice() {
        return openingPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public BigDecimal getPrevClosingPrice() {
        return prevClosingPrice;
    }

    public String getChange() {
        return change;
    }

    public BigDecimal getChangePrice() {
        return changePrice;
    }

    public BigDecimal getSignedChangePrice() {
        return signedChangePrice;
    }

    public BigDecimal getChangeRate() {
        return changeRate;
    }

    public BigDecimal getSignedChangeRate() {
        return signedChangeRate;
    }

    public BigDecimal getTradeVolume() {
        return tradeVolume;
    }

    public BigDecimal getAccTradeVolume() {
        return accTradeVolume;
    }

    public BigDecimal getAccTradeVolume24h() {
        return accTradeVolume24h;
    }

    public BigDecimal getAccTradePrice() {
        return accTradePrice;
    }

    public BigDecimal getAccTradePrice24h() {
        return accTradePrice24h;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public Long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public String getAskBid() {
        return askBid;
    }

    public BigDecimal getAccAskVolume() {
        return accAskVolume;
    }

    public BigDecimal getAccBidVolume() {
        return accBidVolume;
    }

    public BigDecimal getHighest52WeekPrice() {
        return highest52WeekPrice;
    }

    public String getHighest52WeekDate() {
        return highest52WeekDate;
    }

    public BigDecimal getLowest52WeekPrice() {
        return lowest52WeekPrice;
    }

    public String getLowest52WeekDate() {
        return lowest52WeekDate;
    }

    public String getMarketState() {
        return marketState;
    }

    public Boolean getTradingSuspended() {
        return isTradingSuspended;
    }

    public Object getDelistingDate() {
        return delistingDate;
    }

    public String getMarketWarning() {
        return marketWarning;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getStreamType() {
        return streamType;
    }

    @JsonProperty("market_warning")
    private String marketWarning;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("stream_type")
    private String streamType;

    public UpbitReceiveDto() {
        // 기본 생성자 필요
    }


    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(3, RoundingMode.HALF_UP);
    }
}
