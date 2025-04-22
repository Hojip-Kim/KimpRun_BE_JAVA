package kimp.market.dto.coin.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetCoinByIdDto {

    private long id;

    public GetCoinByIdDto(long id) {
        this.id = id;
    }

}
