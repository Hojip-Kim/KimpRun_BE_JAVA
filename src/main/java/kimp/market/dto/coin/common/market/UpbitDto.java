package kimp.market.dto.coin.common.market;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
public class UpbitDto extends MarketDto {

    public UpbitDto(String token, BigDecimal tradeVolume24, BigDecimal signedChangeRate, BigDecimal highestPricePer52, BigDecimal lowestPricePer52, BigDecimal opening_price, BigDecimal trade_price, String rate_change, BigDecimal acc_trade_price24) {
        super(token, tradeVolume24, signedChangeRate, highestPricePer52, lowestPricePer52, opening_price, trade_price, rate_change, acc_trade_price24);
    }
}
