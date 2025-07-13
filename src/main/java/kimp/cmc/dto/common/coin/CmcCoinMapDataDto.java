package kimp.cmc.dto.common.coin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CmcCoinMapDataDto {
    private Long id;
    private Long rank;
    private String name;
    private String symbol;
    private String slug;
    @JsonProperty("is_active")
    private Boolean isActive;
    private Boolean status;
    private String first_historical_data;
    private String last_historical_data;
    private CmcDataPlatformDto platform;

    public CmcCoinMapDataDto(Long id, Long rank, String name, String symbol, String slug, Boolean isActive, Boolean status, String first_historical_data, String last_historical_data, CmcDataPlatformDto platform) {
        this.id = id;
        this.rank = rank;
        this.name = name;
        this.symbol = symbol;
        this.slug = slug;
        this.isActive = isActive;
        this.status = status;
        this.first_historical_data = first_historical_data;
        this.last_historical_data = last_historical_data;
        this.platform = platform;
    }
}
