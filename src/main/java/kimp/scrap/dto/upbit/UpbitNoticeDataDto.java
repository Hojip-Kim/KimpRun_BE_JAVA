package kimp.scrap.dto.upbit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpbitNoticeDataDto {

    public OffsetDateTime listed_at;
    public OffsetDateTime first_listed_at;
    public int id;
    public String title;
    public String category;
    public boolean need_new_badge;
    public boolean need_update_badge;

}
