package kimp.market.dto.internal.marketInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MarketInfoWebsocketDto<T> {
    public String type;
    public T data;

    public MarketInfoWebsocketDto(String type, T data) {
        this.type = type;
        this.data = data;
    }
}
