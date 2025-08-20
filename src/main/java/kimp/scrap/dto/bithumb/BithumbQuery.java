package kimp.scrap.dto.bithumb;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BithumbQuery {
    private String page;

    public BithumbQuery(String page) {
        this.page = page;
    }
}
