package kimp.cmc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
