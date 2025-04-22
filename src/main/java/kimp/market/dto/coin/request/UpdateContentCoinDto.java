package kimp.market.dto.coin.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateContentCoinDto {
    private long id;
    private String content;

    public UpdateContentCoinDto(long id, String content) {
        this.id = id;
        this.content = content;
    }
}
