package kimp.market.dto.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.market.dto.coin.response.CoinMarketDto;

import java.util.List;

public class CombinedMarketList{

    @JsonProperty("firstMarketList")
    private List<CoinMarketDto> firstMarketList;

    @JsonProperty("secondMarketList")
    private List<CoinMarketDto> secondMarketList;

    public CombinedMarketList(List<CoinMarketDto> firstMarketList, List<CoinMarketDto> secondMarketList) {
        this.firstMarketList = firstMarketList;
        this.secondMarketList = secondMarketList;
    }
    
    public CombinedMarketList(List<String> firstMarketStringList, List<String> secondMarketStringList, boolean isStringType) {
        this.firstMarketList = firstMarketStringList.stream()
                .map(symbol -> new CoinMarketDto(null, symbol))
                .collect(java.util.stream.Collectors.toList());
        this.secondMarketList = secondMarketStringList.stream()
                .map(symbol -> new CoinMarketDto(null, symbol))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CoinMarketDto> getFirstMarketList() {
        return firstMarketList;
    }

    public List<CoinMarketDto> getSecondMarketList() {
        return secondMarketList;
    }
}
