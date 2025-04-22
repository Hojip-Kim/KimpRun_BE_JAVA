package kimp.market.dto.coin.common;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class ChangeCoinDto {
    private MarketType type;
    private List<String> listedSymbols = new ArrayList<>();
    private List<String> delistedSymbols = new ArrayList<>();

    public ChangeCoinDto(MarketType type, List<String> listedSymbols, List<String> delistedSymbols) {
        this.type = type;
        this.listedSymbols = listedSymbols;
        this.delistedSymbols = delistedSymbols;
    }
}
