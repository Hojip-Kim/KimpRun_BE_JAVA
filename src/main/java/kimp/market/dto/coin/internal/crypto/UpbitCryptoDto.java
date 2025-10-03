package kimp.market.dto.coin.internal.crypto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpbitCryptoDto extends CryptoDto {

    public UpbitCryptoDto(String name, String currency) {
        super(name, currency);
    }

    @Override
    public String getFullName() {
        return super.getCurrency() + "-" + super.getName();
    }
}
