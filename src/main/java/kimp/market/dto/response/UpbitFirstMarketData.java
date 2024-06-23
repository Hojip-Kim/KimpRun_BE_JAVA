package kimp.market.dto.response;


/*
*
* @JsonProperty("token")
    private String token;
    @JsonProperty("trade_volume")
    private BigDecimal tradeVolume24;
    @JsonProperty("change_rate")
    private BigDecimal changeRate;
    @JsonProperty("highest_price")
    private BigDecimal highestPricePer52;
    @JsonProperty("lowest_price")
    private BigDecimal lowestPricePer52;
* */

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.websocket.dto.response.SimpleUpbitDto;

public class UpbitFirstMarketData {
    @JsonProperty("upbit_datas")
    private SimpleUpbitDto[] simpleUpbitDtos;

    public UpbitFirstMarketData() {
    }

    public UpbitFirstMarketData(SimpleUpbitDto[] simpleUpbitDtos) {
        this.simpleUpbitDtos = simpleUpbitDtos;
    }



}
