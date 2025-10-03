package kimp.market.dto.coin.internal.crypto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CoinoneCryptoDto extends CryptoDto{

    public CoinoneCryptoDto(String name, String currency) {
        super(name, currency);
    }
}
