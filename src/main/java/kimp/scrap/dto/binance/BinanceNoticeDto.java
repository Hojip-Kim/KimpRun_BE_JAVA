package kimp.scrap.dto.binance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BinanceNoticeDto {
    private String code;
    private String message;
    private String messageDetail;
    private BinanceNoticeDataDto data;
    private boolean success;
}
