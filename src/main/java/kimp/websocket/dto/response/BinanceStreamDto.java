package kimp.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class BinanceStreamDto {
    @JsonProperty("token")
    private String token;
    @JsonProperty("trade_price")
    private BigDecimal trade_price;

    public BinanceStreamDto(String token, BigDecimal trade_price) {
        this.token = token;
        this.trade_price = setScale(trade_price);
    }

    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(7, RoundingMode.HALF_UP);
    }


}
