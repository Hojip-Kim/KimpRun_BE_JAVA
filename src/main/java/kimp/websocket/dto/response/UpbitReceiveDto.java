package kimp.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@Getter
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
    @JsonProperty("market_warning")
    private String marketWarning;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("stream_type")
    private String streamType;

    public UpbitReceiveDto(String type, String code, BigDecimal openingPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal tradePrice, BigDecimal prevClosingPrice, String change, BigDecimal changePrice, BigDecimal signedChangePrice, BigDecimal changeRate, BigDecimal signedChangeRate, BigDecimal tradeVolume, BigDecimal accTradeVolume, BigDecimal accTradeVolume24h, BigDecimal accTradePrice, BigDecimal accTradePrice24h, String tradeDate, String tradeTime, Long tradeTimestamp, String askBid, BigDecimal accAskVolume, BigDecimal accBidVolume, BigDecimal highest52WeekPrice, String highest52WeekDate, BigDecimal lowest52WeekPrice, String lowest52WeekDate, String marketState, Boolean isTradingSuspended, Object delistingDate, String marketWarning, Long timestamp, String streamType) {
        this.type = type;
        this.code = code;
        this.openingPrice = openingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tradePrice = tradePrice;
        this.prevClosingPrice = prevClosingPrice;
        this.change = change;
        this.changePrice = changePrice;
        this.signedChangePrice = signedChangePrice;
        this.changeRate = changeRate;
        this.signedChangeRate = signedChangeRate;
        this.tradeVolume = tradeVolume;
        this.accTradeVolume = accTradeVolume;
        this.accTradeVolume24h = accTradeVolume24h;
        this.accTradePrice = accTradePrice;
        this.accTradePrice24h = accTradePrice24h;
        this.tradeDate = tradeDate;
        this.tradeTime = tradeTime;
        this.tradeTimestamp = tradeTimestamp;
        this.askBid = askBid;
        this.accAskVolume = accAskVolume;
        this.accBidVolume = accBidVolume;
        this.highest52WeekPrice = highest52WeekPrice;
        this.highest52WeekDate = highest52WeekDate;
        this.lowest52WeekPrice = lowest52WeekPrice;
        this.lowest52WeekDate = lowest52WeekDate;
        this.marketState = marketState;
        this.isTradingSuspended = isTradingSuspended;
        this.delistingDate = delistingDate;
        this.marketWarning = marketWarning;
        this.timestamp = timestamp;
        this.streamType = streamType;
    }

    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(3, RoundingMode.HALF_UP);
    }
}
