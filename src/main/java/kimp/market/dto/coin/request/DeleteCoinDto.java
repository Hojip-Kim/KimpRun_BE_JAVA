package kimp.market.dto.coin.request;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class DeleteCoinDto {

    private long id;

    public DeleteCoinDto(long id) {
        this.id = id;
    }
}
