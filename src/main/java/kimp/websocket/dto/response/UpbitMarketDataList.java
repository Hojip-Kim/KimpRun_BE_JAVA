package kimp.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UpbitMarketDataList {

    @JsonProperty("marketDataList")
    private List<SimpleUpbitDto> marketDataList;

    public UpbitMarketDataList(List<SimpleUpbitDto> marketDataList) {
        this.marketDataList = marketDataList;
    }

    public List<SimpleUpbitDto> getMarketDataList() {
        return marketDataList;
    }
}
