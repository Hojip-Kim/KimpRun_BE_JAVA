package kimp.market.dto.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class CoinoneTickerInfo {

    // 마켓 기준 통화
    @JsonProperty("quote_currency")
    private String quoteCurrency;

    // 티커 종목 명
    @JsonProperty("target_currency")
    private String targetCurrency;

    // 티커 생성 시점 (Unix time) (ms)
    @JsonProperty("timestamp")
    private Long timestamp;

    // 고가 (24시간 기준)
    @JsonProperty("high")
    private BigDecimal high;

    // 저가 (24시간 기준)
    @JsonProperty("low")
    private BigDecimal low;

    // 시가 (24시간 기준)
    @JsonProperty("first")
    private BigDecimal first;

    // 종가 (24시간 기준)
    @JsonProperty("last")
    private BigDecimal last;

    // 24시간 기준 종목 체결 금액 (원화)
    @JsonProperty("quote_volume")
    private BigDecimal quoteVolume;

    // 24시간 기준 종목 체결량 (종목)
    @JsonProperty("target_volume")
    private BigDecimal targetVolume;

    // 매도 최저가의 오더북 정보
    @JsonProperty("best_asks")
    private List<CoinoneOrderBook> bestAsks;

    // 매수 최고가의 오더북 정보
    @JsonProperty("best_bids")
    private List<CoinoneOrderBook> bestBids;

    // 티커 별 ID 값으로 클수록 최신 티커 정보
    @JsonProperty("id")
    private String id;

    // 어제 고가
    @JsonProperty("yesterday_high")
    private BigDecimal yesterdayHigh;

    // 어제 저가
    @JsonProperty("yesterday_low")
    private BigDecimal yesterdayLow;

    // 어제 시가
    @JsonProperty("yesterday_first")
    private BigDecimal yesterdayFirst;

    // 어제 종가
    @JsonProperty("yesterday_last")
    private BigDecimal yesterdayLast;

    // 어제 24시간 기준 종목 체결 금액 (원화)
    @JsonProperty("yesterday_quote_volume")
    private BigDecimal yesterdayQuoteVolume;

    // 어제 24시간 기준 종목 체결량 (종목)
    @JsonProperty("yesterday_target_volume")
    private BigDecimal yesterdayTargetVolume;

    public CoinoneTickerInfo(String quoteCurrency, String targetCurrency, Long timestamp, BigDecimal high, BigDecimal low, BigDecimal first, BigDecimal last, BigDecimal quoteVolume, BigDecimal targetVolume, List<CoinoneOrderBook> bestAsks, List<CoinoneOrderBook> bestBids, String id, BigDecimal yesterdayHigh, BigDecimal yesterdayLow, BigDecimal yesterdayFirst, BigDecimal yesterdayLast, BigDecimal yesterdayQuoteVolume, BigDecimal yesterdayTargetVolume) {
        this.quoteCurrency = quoteCurrency;
        this.targetCurrency = targetCurrency;
        this.timestamp = timestamp;
        this.high = high;
        this.low = low;
        this.first = first;
        this.last = last;
        this.quoteVolume = quoteVolume;
        this.targetVolume = targetVolume;
        this.bestAsks = bestAsks;
        this.bestBids = bestBids;
        this.id = id;
        this.yesterdayHigh = yesterdayHigh;
        this.yesterdayLow = yesterdayLow;
        this.yesterdayFirst = yesterdayFirst;
        this.yesterdayLast = yesterdayLast;
        this.yesterdayQuoteVolume = yesterdayQuoteVolume;
        this.yesterdayTargetVolume = yesterdayTargetVolume;
    }
}