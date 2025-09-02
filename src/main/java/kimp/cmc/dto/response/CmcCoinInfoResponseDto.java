package kimp.cmc.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
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

    // QueryDSL Projection을 위한 생성자
    public CmcCoinInfoResponseDto(String symbol, String name, String slug, String logo, String rank,
                                 String description, Double dominance, String maxSupply, String totalSupply,
                                 String circulatingSupply, String marketCap, String fullyDilutedMarektCap,
                                 String selfReportedCirculatingSupply, String selfReportedMarketCap,
                                 LocalDateTime lastUpdated, LocalDateTime dateAdded) {
        this.symbol = symbol;
        this.name = name;
        this.slug = slug;
        this.logo = logo;
        this.rank = rank;
        this.description = description;
        this.dominance = dominance;
        this.maxSupply = maxSupply;
        this.totalSupply = totalSupply;
        this.circulatingSupply = circulatingSupply;
        this.marketCap = marketCap;
        this.fullyDilutedMarektCap = fullyDilutedMarektCap;
        this.selfReportedCirculatingSupply = selfReportedCirculatingSupply;
        this.selfReportedMarketCap = selfReportedMarketCap;
        this.lastUpdated = lastUpdated;
        this.dateAdded = dateAdded;
        this.platform = new ArrayList<>();
        this.explorerUrl = new ArrayList<>();
    }

    // platform과 explorerUrl 설정을 위한 메서드
    public void setPlatform(List<String> platform) {
        this.platform = platform != null ? platform : new ArrayList<>();
    }

    public void setExplorerUrl(List<String> explorerUrl) {
        this.explorerUrl = explorerUrl != null ? explorerUrl : new ArrayList<>();
    }
}
