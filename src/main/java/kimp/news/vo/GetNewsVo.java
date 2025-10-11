package kimp.news.vo;

import kimp.news.enums.NewsSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetNewsVo {

    private NewsSource newsSource;
    private String newsType;
    private int page;
    private int size;
}
