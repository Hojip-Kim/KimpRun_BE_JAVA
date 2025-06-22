package kimp.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BithumbReceiveDto {

    @JsonProperty("status")
    private String status;

    // 타입 (ticker: 현재가)
    @JsonProperty("type")
    private String type;

    // 마켓 코드 (ex. KRW-BTC)
    @JsonProperty("code")
    private String code;

    // 시가
    @JsonProperty("opening_price")
    private BigDecimal openingPrice;

    // 고가
    @JsonProperty("high_price")
    private BigDecimal highPrice;

    // 저가
    @JsonProperty("low_price")
    private BigDecimal lowPrice;

    // 현재가
    @JsonProperty("trade_price")
    private BigDecimal tradePrice;

    // 전일 종가
    @JsonProperty("prev_closing_price")
    private BigDecimal prevClosingPrice;

    // 전일 대비 (RISE: 상승, EVEN: 보합, FALL: 하락)
    @JsonProperty("change")
    private String change;

    // 부호 없는 전일 대비 값
    @JsonProperty("change_price")
    private BigDecimal changePrice;

    // 전일 대비 값
    @JsonProperty("signed_change_price")
    private BigDecimal signedChangePrice;

    // 부호 없는 전일 대비 등락율
    @JsonProperty("change_rate")
    private BigDecimal changeRate;

    // 전일 대비 등락율
    @JsonProperty("signed_change_rate")
    private BigDecimal signedChangeRate;

    // 가장 최근 거래량
    @JsonProperty("trade_volume")
    private BigDecimal tradeVolume;

    // 누적 거래량(KST 0시 기준)
    @JsonProperty("acc_trade_volume")
    private BigDecimal accTradeVolume;

    // 24시간 누적 거래량
    @JsonProperty("acc_trade_volume_24h")
    private BigDecimal accTradeVolume24h;

    // 누적 거래대금(KST 0시 기준)
    @JsonProperty("acc_trade_price")
    private BigDecimal accTradePrice;

    // 24시간 누적 거래대금
    @JsonProperty("acc_trade_price_24h")
    private BigDecimal accTradePrice24h;

    // 최근 거래 일자(KST, 포맷: yyyyMMdd)
    @JsonProperty("trade_date")
    private String tradeDate;

    // 최근 거래 시각(KST, 포맷: HHmmss)
    @JsonProperty("trade_time")
    private String tradeTime;

    // 체결 타임스탬프 (milliseconds)
    @JsonProperty("trade_timestamp")
    private Long tradeTimestamp;

    // 매수/매도 구분 (ASK: 매도, BID: 매수)
    @JsonProperty("ask_bid")
    private String askBid;

    // 누적 매도량
    @JsonProperty("acc_ask_volume")
    private BigDecimal accAskVolume;

    // 누적 매수량
    @JsonProperty("acc_bid_volume")
    private BigDecimal accBidVolume;

    // 52주 최고가
    @JsonProperty("highest_52_week_price")
    private BigDecimal highest52WeekPrice;

    // 52주 최고가 달성일 (포맷: yyyy-MM-dd)
    @JsonProperty("highest_52_week_date")
    private String highest52WeekDate;

    // 52주 최저가
    @JsonProperty("lowest_52_week_price")
    private BigDecimal lowest52WeekPrice;

    // 52주 최저가 달성일 (포맷: yyyy-MM-dd)
    @JsonProperty("lowest_52_week_date")
    private String lowest52WeekDate;

    // 거래상태
    @JsonProperty("market_state")
    private String marketState;

    // 거래 정지 여부
    @JsonProperty("is_trading_suspended")
    private Boolean isTradingSuspended;

    // 거래지원 종료일
    @JsonProperty("delisting_date")
    private String delistingDate;

    // 유의 종목 여부 (NONE: 해당없음, CAUTION: 거래유의)
    @JsonProperty("market_warning")
    private String marketWarning;

    // 타임스탬프 (millisecond)
    @JsonProperty("timestamp")
    private Long timestamp;

    // 스트림 타입 (SNAPSHOT: 스냅샷, REALTIME: 실시간)
    @JsonProperty("stream_type")
    private String streamType;

    public BithumbReceiveDto(String type, String code, BigDecimal openingPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal tradePrice, BigDecimal prevClosingPrice, String change, BigDecimal changePrice, BigDecimal signedChangePrice, BigDecimal changeRate, BigDecimal signedChangeRate, BigDecimal tradeVolume, BigDecimal accTradeVolume, BigDecimal accTradeVolume24h, BigDecimal accTradePrice, BigDecimal accTradePrice24h, String tradeDate, String tradeTime, Long tradeTimestamp, String askBid, BigDecimal accAskVolume, BigDecimal accBidVolume, BigDecimal highest52WeekPrice, String highest52WeekDate, BigDecimal lowest52WeekPrice, String lowest52WeekDate, String marketState, Boolean isTradingSuspended, String delistingDate, String marketWarning, Long timestamp, String streamType) {
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
}