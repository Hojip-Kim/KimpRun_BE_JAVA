package kimp.scrap.dto.internal.binance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BinanceArticleDto {
    private int id;
    private String code;
    private String title;
    private int type;
    private Long releaseDate;
}
