package kimp.market.dto.coin.common.crypto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BithumbCryptoDto extends CryptoDto {

    public BithumbCryptoDto(String name, String currency) {
        super(name, currency);
    }
}
