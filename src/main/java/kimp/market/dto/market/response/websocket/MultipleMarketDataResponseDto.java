package kimp.market.dto.market.response.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.market.dto.coin.internal.market.BinanceDto;
import kimp.market.dto.coin.internal.market.BithumbDto;
import kimp.market.dto.coin.internal.market.CoinoneDto;
import kimp.market.dto.coin.internal.market.UpbitDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class MultipleMarketDataResponseDto {
    @JsonProperty("upbitData")
    private List<UpbitDto> upbitData = new ArrayList<>();

    @JsonProperty("binanceData")
    private List<BinanceDto> binanceData = new ArrayList<>();

    @JsonProperty("coinoneData")
    private List<CoinoneDto> coinoneData = new  ArrayList<>();

    @JsonProperty("bithumbData")
    private List<BithumbDto> bithumbData = new ArrayList<>();

    public MultipleMarketDataResponseDto(List<UpbitDto> upbitData, List<BinanceDto> binanceData,
                                         List<CoinoneDto> coinoneData, List<BithumbDto> bithumbData) {
        this.upbitData = upbitData;
        this.binanceData = binanceData;
        this.coinoneData = coinoneData;
        this.bithumbData = bithumbData;
    }
}