package kimp.market.dto.market.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CombinedMarketList{

    @JsonProperty("firstMarketList")
    private List<String> firstMarketList;

    @JsonProperty("secondMarketList")
    private List<String> secondMarketList;

    public CombinedMarketList(List<String> firstMarketList, List<String> secondMarketList) {
        this.firstMarketList = firstMarketList;
        this.secondMarketList = secondMarketList;
    }

    public List<String> getFirstMarketList() {
        return firstMarketList;
    }

    public List<String> getSecondMarketList() {
        return secondMarketList;
    }
}
