package kimp.cmc.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class CmcCoinResponseDto {
    private String symbol;
    private String name;
    private String logo;
    private String maxSupply;
    private String totalSupply;
    private String circulatingSupply;
    private String marketCap;
    private List<String> explorerUrl = new ArrayList<>();
    private List<String> platforms = new ArrayList<>();
    private Integer rank;
    private LocalDateTime lastUpdated;

    public CmcCoinResponseDto(String symbol, String name, String logo, String maxSupply, String totalSupply, String circulatingSupply, String marketCap, List<String> explorerUrl, List<String> platforms, Integer rank, LocalDateTime lastUpdated) {
        this.symbol = symbol;
        this.name = name;
        this.logo = logo;
        this.maxSupply = maxSupply;
        this.totalSupply = totalSupply;
        this.circulatingSupply = circulatingSupply;
        this.marketCap = marketCap;
        this.explorerUrl = explorerUrl;
        this.platforms = platforms;
        this.rank = rank;
        this.lastUpdated = lastUpdated;
    }
}
