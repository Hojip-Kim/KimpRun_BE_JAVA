package kimp.market.dto.coin.response;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CoinResponseWithMarketTypeDto extends CoinResponseDto {

    private List<MarketType> marketTypes;

    public CoinResponseWithMarketTypeDto(long id, String symbol, String name, String englishName, List<MarketType> marketTypes) {
        super(id, symbol, name, englishName);
        this.marketTypes = marketTypes;
    }
}
