package kimp.market.dto.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
public class CoinoneOrderBook {

    // 가격 (매도/매수)
    @JsonProperty("price")
    private BigDecimal price;

    // 수량 (매도/매수)
    @JsonProperty("qty")
    private BigDecimal qty;

    public CoinoneOrderBook(BigDecimal price, BigDecimal qty) {
        this.price = price;
        this.qty = qty;
    }
}
