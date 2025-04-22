package kimp.market.dto.coin.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCoinDto {

    private long id;
    private String symbol;
    private String name;
    private String englishName;
    private String content;

    public UpdateCoinDto(long id, String symbol, String name, String englishName, String content) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.englishName = englishName;
        this.content = content;
    }
}
