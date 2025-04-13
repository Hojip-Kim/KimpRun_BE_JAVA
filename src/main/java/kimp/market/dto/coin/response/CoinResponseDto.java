package kimp.market.dto.coin.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CoinResponseDto {

    private long id;

    private String symbol;

    private String name;

    private String englishName;

    public CoinResponseDto(long id, String symbol, String name, String englishName) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.englishName = englishName;
    }
}
