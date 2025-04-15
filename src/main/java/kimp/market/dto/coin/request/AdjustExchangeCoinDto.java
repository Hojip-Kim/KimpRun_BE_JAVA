package kimp.market.dto.coin.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdjustExchangeCoinDto {
    private long coinId;
    private List<Long> exchangeIds;
}
