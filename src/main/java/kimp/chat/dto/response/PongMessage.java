package kimp.chat.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PongMessage {
    private String type;

    public PongMessage(String type) {
        this.type = type;
    }
}
