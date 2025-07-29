package kimp.websocket.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BinanceErrorDto {
    private int code;
    private String msg;
}
