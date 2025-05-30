package kimp.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PingMessage {
    private String type;

    public PingMessage(String type) {
        this.type = type;
    }
}
