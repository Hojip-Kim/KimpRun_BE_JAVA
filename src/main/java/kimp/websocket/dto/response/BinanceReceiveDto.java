package kimp.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BinanceReceiveDto {

    @JsonProperty("e")
    private String type; // event type
    @JsonProperty("E")
    private long time; // event time
    @JsonProperty("s")
    private String token; // event token
    @JsonProperty("t")
    private long tradeId; // trade id
    @JsonProperty("p")
    private BigDecimal price; // token price
    @JsonProperty("T")
    private long total; // Trade time
    @JsonProperty("m")
    private boolean isMarketMaker; // Is the buyer the market maker?
    @JsonProperty("M")
    private boolean Ignore; // Ignore
    @JsonProperty("q")
    private BigDecimal qty; // quantity

    public BinanceReceiveDto(String type, long time, String token, long tradeId, BigDecimal price, long total, boolean isMarketMaker, boolean ignore, BigDecimal qty) {
        this.type = type;
        this.time = time;
        this.token = token;
        this.tradeId = tradeId;
        this.price = price;
        this.total = total;
        this.isMarketMaker = isMarketMaker;
        Ignore = ignore;
        this.qty = qty;
    }

}
