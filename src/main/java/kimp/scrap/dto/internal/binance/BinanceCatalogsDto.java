package kimp.scrap.dto.internal.binance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BinanceCatalogsDto {
    private int catalogId;
    private Integer parentCatalogId;
    private String icon;
    private String catalogName;
    private String description;
    private int catalogType;
    private int total;
    private List<BinanceArticleDto> articles;
    private List<String> catalogs;

}
