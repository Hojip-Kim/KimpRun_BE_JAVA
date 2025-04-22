package kimp.market.dto.market.response.websocket;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class InfoResponseDto {

    private String type = "market";
    private UserWebsocketResponseDto userData;
    private MarketWebsocketResponseDto marketData;

    public InfoResponseDto(UserWebsocketResponseDto userData, MarketWebsocketResponseDto marketData) {
        this.userData = userData;
        this.marketData = marketData;
    }
}
