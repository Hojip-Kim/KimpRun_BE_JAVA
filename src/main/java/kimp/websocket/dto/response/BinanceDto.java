package kimp.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.market.common.MarketCommonMethod;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
public class BinanceDto extends MarketDto{
    @JsonProperty("token")
    private String token;
    // Trade Volume (거래량)
    @JsonProperty("trade_volume")
    private BigDecimal tradeVolume24;
    // Change Rate (전일 대비 가격변화율)
    @JsonProperty("change_rate")
    private BigDecimal changeRate;
    // Highest Price (최고가 - 52주 기준)
    @JsonProperty("highest_price")
    private BigDecimal highestPricePer52;
    // Lowest Price (최저가 - 52주 기준)
    @JsonProperty("lowest_price")
    private BigDecimal lowestPricePer52;
    // Opening Price (시가)
    @JsonProperty("opening_price")
    private BigDecimal opening_price;
    // Trade Price (실시간 가격)
    @JsonProperty("trade_price")
    private BigDecimal trade_price;
    // RISE, EVEN, FALL
    @JsonProperty("rate_change")
    private String rate_change;
    @JsonProperty("acc_trade_price24")
    private BigDecimal acc_trade_price24;

    public BinanceDto(){}


    public BinanceDto(String token, BigDecimal tradeVolume24, BigDecimal signedChangeRate, BigDecimal highestPricePer52, BigDecimal lowestPricePer52, BigDecimal opening_price, BigDecimal trade_price, String rate_change, BigDecimal acc_trade_price24) {
        this.token = token;
        this.tradeVolume24 = MarketCommonMethod.setScale(tradeVolume24);
        this.changeRate = MarketCommonMethod.setScale(signedChangeRate.multiply(new BigDecimal(10)));
        this.highestPricePer52 = MarketCommonMethod.setScale(highestPricePer52);
        this.lowestPricePer52 = MarketCommonMethod.setScale(lowestPricePer52);
        this.opening_price = MarketCommonMethod.setScale(opening_price);
        this.trade_price = MarketCommonMethod.setScale(trade_price);
        this.rate_change = rate_change;

        // WARNING :: bigdecimal 9버전에서 ROUND_HALF_UP deprecate될것임. 유의
        this.acc_trade_price24 = acc_trade_price24.setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
