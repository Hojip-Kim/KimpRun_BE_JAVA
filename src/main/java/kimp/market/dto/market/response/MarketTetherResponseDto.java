package kimp.market.dto.market.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MarketTetherResponseDto {
    private double tether;

    public MarketTetherResponseDto(double tether) {
        this.tether = tether;
    }
}
