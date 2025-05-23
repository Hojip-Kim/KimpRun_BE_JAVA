package kimp.market.dto.market.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MarketDollarResponseDto {

    private double dollar;

    public MarketDollarResponseDto(double dollar) {
        this.dollar = dollar;
    }
}
