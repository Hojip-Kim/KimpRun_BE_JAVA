package kimp.scrap.dto.internal.binance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BinanceNoticeDataDto {
    private List<BinanceCatalogsDto> catalogs;

}
