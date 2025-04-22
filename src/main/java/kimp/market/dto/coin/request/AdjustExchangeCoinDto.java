package kimp.market.dto.coin.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdjustExchangeCoinDto {
    private long coinId;
    private List<Long> exchangeIds;

    public AdjustExchangeCoinDto(long coinId, List<Long> exchangeIds) {
        this.coinId = coinId;
        this.exchangeIds = exchangeIds;
    }
}
