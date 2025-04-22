package kimp.market.dto.coin.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

// 서비스 내에서 활용하는 coin dto입니다.
@Getter
@NoArgsConstructor
public class ServiceCoinDto{

    @NonNull
    private String symbol;
    private String krName;
    private String englishName;

    public ServiceCoinDto(@NonNull String symbol) {
        this.symbol = symbol;
    }

    public ServiceCoinDto(@NonNull String symbol, String krName, String englishName) {
        this.symbol = symbol;
        this.krName = krName;
        this.englishName = englishName;
    }
}
