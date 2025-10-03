package kimp.market.dto.market.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
public class CoinoneMarketInfo {

    // 마켓 기준 통화
    @JsonProperty("quote_currency")
    private String quoteCurrency;

    // 거래 가능한 종목 명
    @JsonProperty("target_currency")
    private String targetCurrency;

    // 가격 호가 단위
    @JsonProperty("price_unit")
    private BigDecimal priceUnit;

    // 주문 가능 수량 단위
    @JsonProperty("qty_unit")
    private BigDecimal qtyUnit;

    // 최대 주문 총액 (가격 X 주문량)
    @JsonProperty("max_order_amount")
    private BigDecimal maxOrderAmount;

    // 최대 주문 가능한 가격 (KRW 기준)
    @JsonProperty("max_price")
    private BigDecimal maxPrice;

    // 최대 주문 가능한 수량 (종목 수량)
    @JsonProperty("max_qty")
    private BigDecimal maxQty;

    // 최소 주문 총액 (가격 X 주문량)
    @JsonProperty("min_order_amount")
    private BigDecimal minOrderAmount;

    // 최소 주문 가능한 가격 (KRW 기준)
    @JsonProperty("min_price")
    private BigDecimal minPrice;

    // 최소 주문 가능한 수량 (종목 수량)
    @JsonProperty("min_qty")
    private BigDecimal minQty;

    // 오더북 단위 정보
    @JsonProperty("order_book_units")
    private List<BigDecimal> orderBookUnits;

    // 점검 여부 상태 (0: 정상, 1: 점검 중)
    @JsonProperty("maintenance_status")
    private Integer maintenanceStatus;

    // 거래 가능 여부 상태 (0: 매수/매도 불가, 1: 매수/매도 가능, 2: 매수 불가, 3: 매도 불가)
    @JsonProperty("trade_status")
    private Integer tradeStatus;

    // 가능한 주문 방식 (market: 시장가, limit: 지정가, stop_limit: 예약가)
    @JsonProperty("order_types")
    private List<String> orderTypes;
}