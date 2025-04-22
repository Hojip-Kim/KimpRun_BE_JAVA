package kimp.exchange.dto.exchange.response;

import kimp.market.Enum.MarketType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExchangeDto {

    private Long exchangeId;
    private MarketType exchangeName;
    private String link;

}
