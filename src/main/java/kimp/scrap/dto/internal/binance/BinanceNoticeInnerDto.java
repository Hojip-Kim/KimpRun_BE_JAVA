package kimp.scrap.dto.internal.binance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BinanceNoticeInnerDto {
    private String code;
    private String message;
    private String messageDetail;
    private BinanceNoticeDataDto data;
    private boolean success;
}
