package kimp.scrap.dto.upbit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpbitDataDto {
    public int total_pages;
    public int total_count;
    public List<UpbitNoticeDataDto> notices;
}
