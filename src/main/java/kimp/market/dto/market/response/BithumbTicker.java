package kimp.market.dto.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BithumbTicker extends Ticker{

    // 종목 구분 코드
    @JsonProperty("market")
    private String market;

    // 최근거래일 (UTC, 포맷 yyyyMMdd)
    @JsonProperty("trade_date")
    private String tradeDate;

    // 최근 거래 시각 (UTC, 포맷 HHmmss)
    @JsonProperty("trade_time")
    private String tradeTime;

    // 최근 거래 일자 (KST, 포맷 yyyyMMdd)
    @JsonProperty("trade_date_kst")
    private String tradeDateKst;

    // 최근 거래 시각(KST, 포맷 HHmmss)
    @JsonProperty("trade_time_kst")
    private String tradeTimeKst;

    // 최근 거래 일시(UTC, 포맷 UnixTimestamp)
    @JsonProperty("trade_timestamp")
    private Long tradeTimestamp;

    // 시가
    @JsonProperty("opening_price")
    private BigDecimal openingPrice;

    // 고가
    @JsonProperty("high_price")
    private BigDecimal highPrice;

    // 저가
    @JsonProperty("low_price")
    private BigDecimal lowPrice;

    // 종가(현재가)
    @JsonProperty("trade_price")
    private BigDecimal tradePrice;

    // 전일 종가(KST 0시 기준)
    @JsonProperty("prev_closing_price")
    private BigDecimal prevClosingPrice;

    // EVEN : 보합, RISE : 상승, FALL : 하락
    @JsonProperty("change")
    private String change;

    // 변화액의 절대값
    @JsonProperty("change_price")
    private BigDecimal changePrice;

    // 변화율의 절대값
    @JsonProperty("change_rate")
    private BigDecimal changeRate;

    // 부호가 있는 변화액
    @JsonProperty("signed_change_price")
    private BigDecimal signedChangePrice;

    // 부호가 있는 변화율
    @JsonProperty("signed_change_rate")
    private BigDecimal signedChangeRate;

    // 가장 최근 거래량
    @JsonProperty("trade_volume")
    private BigDecimal tradeVolume;

    // 누적 거래대금(KST 0시 기준)
    @JsonProperty("acc_trade_price")
    private BigDecimal accTradePrice;

    // 24시간 누적 거래대금
    @JsonProperty("acc_trade_price_24h")
    private BigDecimal accTradePrice24h;

    // 누적 거래량(KST 0시 기준)
    @JsonProperty("acc_trade_volume")
    private BigDecimal accTradeVolume;

    // 24시간 누적 거래량
    @JsonProperty("acc_trade_volume_24h")
    private BigDecimal accTradeVolume24h;

    // 52주 신고가
    @JsonProperty("highest_52_week_price")
    private BigDecimal highest52WeekPrice;

    // 52주 신고가 달성일 (포맷 yyyy-MM-dd)
    @JsonProperty("highest_52_week_date")
    private String highest52WeekDate;

    // 52주 신저가
    @JsonProperty("lowest_52_week_price")
    private BigDecimal lowest52WeekPrice;

    // 52주 신저가 달성일 (포맷 yyyy-MM-dd)
    @JsonProperty("lowest_52_week_date")
    private String lowest52WeekDate;

    // 타임스탬프
    @JsonProperty("timestamp")
    private Long timestamp;

    public BithumbTicker(String market, String tradeDate, String tradeTime, String tradeDateKst, String tradeTimeKst, Long tradeTimestamp, BigDecimal openingPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal tradePrice, BigDecimal prevClosingPrice, String change, BigDecimal changePrice, BigDecimal changeRate, BigDecimal signedChangePrice, BigDecimal signedChangeRate, BigDecimal tradeVolume, BigDecimal accTradePrice, BigDecimal accTradePrice24h, BigDecimal accTradeVolume, BigDecimal accTradeVolume24h, BigDecimal highest52WeekPrice, String highest52WeekDate, BigDecimal lowest52WeekPrice, String lowest52WeekDate, Long timestamp) {
        this.market = market;
        this.tradeDate = tradeDate;
        this.tradeTime = tradeTime;
        this.tradeDateKst = tradeDateKst;
        this.tradeTimeKst = tradeTimeKst;
        this.tradeTimestamp = tradeTimestamp;
        this.openingPrice = openingPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tradePrice = tradePrice;
        this.prevClosingPrice = prevClosingPrice;
        this.change = change;
        this.changePrice = changePrice;
        this.changeRate = changeRate;
        this.signedChangePrice = signedChangePrice;
        this.signedChangeRate = signedChangeRate;
        this.tradeVolume = tradeVolume;
        this.accTradePrice = accTradePrice;
        this.accTradePrice24h = accTradePrice24h;
        this.accTradeVolume = accTradeVolume;
        this.accTradeVolume24h = accTradeVolume24h;
        this.highest52WeekPrice = highest52WeekPrice;
        this.highest52WeekDate = highest52WeekDate;
        this.lowest52WeekPrice = lowest52WeekPrice;
        this.lowest52WeekDate = lowest52WeekDate;
        this.timestamp = timestamp;
    }
}