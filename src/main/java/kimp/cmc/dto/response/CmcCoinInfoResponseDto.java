package kimp.cmc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmcCoinInfoResponseDto {
    private String symbol;
    private String name;
    private String slug;
    private String logo;
    private String rank;

    private String description;
    private Double dominance;

    private String maxSupply;
    private String totalSupply;
    private String circulatingSupply;
    private String marketCap;
    private String fullyDilutedMarektCap;
    private String selfReportedCirculatingSupply;
    private String selfReportedMarketCap;
    private LocalDateTime lastUpdated;
    private LocalDateTime dateAdded;

    private List<String> platform = new ArrayList<>();

    private List<String> explorerUrl = new ArrayList<>();

    // platform과 explorerUrl 설정을 위한 메서드
    public void setPlatform(List<String> platform) {
        this.platform = platform != null ? platform : new ArrayList<>();
    }

    public void setExplorerUrl(List<String> explorerUrl) {
        this.explorerUrl = explorerUrl != null ? explorerUrl : new ArrayList<>();
    }
}
