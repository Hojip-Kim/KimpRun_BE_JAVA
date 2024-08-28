package kimp.market.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MarketDataList<T> {
    @JsonProperty("marketDataList")
    private List<T> marketDataList;

    public MarketDataList() {
    }

    public MarketDataList(List<T> marketDtos) {
        this.marketDataList = marketDtos;
    }

    public List<T> getMarketDataList() {
        return marketDataList;
    }
}
