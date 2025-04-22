package kimp.market.dto.coin.request;

import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateCoinDto {

    @NonNull
    private String symlbol;
    private String name;
    private String englishName;
    private List<MarketType> marketType;

    public CreateCoinDto(String symlbol, String name, String englishName, List<MarketType> marketType) {
        this.symlbol = symlbol;
        this.name = name;
        this.englishName = englishName;
        this.marketType = marketType;
    }

    public CreateCoinDto(@NonNull String symlbol, List<MarketType> marketType) {
        this.symlbol = symlbol;
        this.marketType = marketType;
    }
}
