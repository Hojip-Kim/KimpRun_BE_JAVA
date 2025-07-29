package kimp.market.dto.coin.common.crypto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BinanceCryptoDto extends CryptoDto {

    public BinanceCryptoDto(String name, String currency) {
        super(name, currency);
    }

    @Override
    public String getFullName() {
        return super.getFullName();
    }
}
