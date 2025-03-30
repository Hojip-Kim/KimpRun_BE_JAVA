package kimp.exchange.dto.upbit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpbitNoticeDto {
    public boolean success;
    public UpbitDataDto data;
}
