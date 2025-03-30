package kimp.exchange.dto.coinone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CoinoneNoticeDto {
    private int count;
    private String next;
    private String previous;
    private List<CoinoneNoticeResultDto> results;


}
