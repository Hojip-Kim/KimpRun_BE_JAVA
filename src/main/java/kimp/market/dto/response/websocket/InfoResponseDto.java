package kimp.market.dto.response.websocket;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InfoResponseDto {

    UserWebsocketResponseDto userData;
    MarketWebsocketResponseDto marketData;
}
