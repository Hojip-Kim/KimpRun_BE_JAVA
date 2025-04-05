package kimp.market.dto.market.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BinanceTicker extends Ticker{
    private String symbol;
    private BigDecimal priceChange;           // 가격 변화량
    private BigDecimal priceChangePercent;    // 가격 변화 퍼센트
    private BigDecimal weightedAvgPrice;      // 가중 평균가
    private BigDecimal prevClosePrice;        // 이전 종가
    private BigDecimal lastPrice;             // 최종 가격
    private BigDecimal lastQty;               // 최종 거래량
    private BigDecimal bidPrice;              // 입찰 가격
    private BigDecimal bidQty;                // 입찰 수량
    private BigDecimal askPrice;              // 매도 가격
    private BigDecimal askQty;                // 매도 수량
    private BigDecimal openPrice;             // 시가
    private BigDecimal highPrice;             // 최고가
    private BigDecimal lowPrice;              // 최저가
    private BigDecimal volume;                // 거래량
    private BigDecimal quoteVolume;           // 견적 거래량
    private Long openTime;                    // 개장 시간 (Unix 타임스탬프)
    private Long closeTime;                   // 폐장 시간 (Unix 타임스탬프)
    private Long firstId;                     // 첫 번째 거래 ID
    private Long lastId;                      // 마지막 거래 ID
    private Integer count;                    // 거래 횟수

    public BinanceTicker() {};
    public BinanceTicker(String symbol, BigDecimal priceChange, BigDecimal priceChangePercent, BigDecimal weightedAvgPrice, BigDecimal prevClosePrice, BigDecimal lastPrice, BigDecimal lastQty, BigDecimal bidPrice, BigDecimal bidQty, BigDecimal askPrice, BigDecimal askQty, BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal volume, BigDecimal quoteVolume, Long openTime, Long closeTime, Long firstId, Long lastId, Integer count) {
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
