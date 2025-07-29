package kimp.exchange.dto.bithumb;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BithumbCategory {
    private int id;
    private String name;

    public BithumbCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
