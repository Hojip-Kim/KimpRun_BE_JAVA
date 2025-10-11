package kimp.scrap.dto.internal.bithumb;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BithumbPageProps {
    // ex) ok
    private String status;
    private List<BithumbNotice> noticeList;
    private int totalCount;
    private List<BithumbCategory> categories;
}
