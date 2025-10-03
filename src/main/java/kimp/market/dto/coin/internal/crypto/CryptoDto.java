package kimp.market.dto.coin.internal.crypto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CryptoDto {

    // coin의 name만 받는 필드
    // 예 : "BTC", "ETH"
    private String name;

    // 통화를 받는 필드
    // 예 : "USDT", "KRW"
    private String currency;

    public CryptoDto(String name, String currency) {
        this.name = name;
        this.currency = currency;
    }

    public String getFullName() {
        return name + currency;
    }

}
