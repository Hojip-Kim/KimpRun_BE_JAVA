package kimp.exchange.dto.exchange.request;

import jakarta.validation.constraints.NotBlank;
import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExchangeCreateRequestDto {

    @NotBlank
    public MarketType exchangeName;
    @NotBlank
    public String link;

    public ExchangeCreateRequestDto(MarketType exchangeName, String link) {
        this.exchangeName = exchangeName;
        this.link = link;
    }
}
