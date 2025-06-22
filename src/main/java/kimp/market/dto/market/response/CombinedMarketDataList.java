package kimp.market.dto.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.market.dto.coin.common.market.MarketDto;
import lombok.Getter;

import java.util.List;

@Getter
public class CombinedMarketDataList {

    @JsonProperty("firstMarketDataList")
    private List<? extends MarketDto> firstMarketList;

    @JsonProperty("secondMarketDataList")
    private List<? extends MarketDto> secondMarketList;

    public CombinedMarketDataList(List<? extends MarketDto> firstMarketList, List<? extends MarketDto> secondMarketList) {
        this.firstMarketList = firstMarketList;
        this.secondMarketList = secondMarketList;
    }


}
